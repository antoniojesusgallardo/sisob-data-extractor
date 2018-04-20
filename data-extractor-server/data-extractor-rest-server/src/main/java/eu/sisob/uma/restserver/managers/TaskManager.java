/*
    Copyright (c) 2014 "(IA)2 Research Group. Universidad de Málaga"
                        http://iaia.lcc.uma.es | http://www.uma.es

    This file is part of SISOB Data Extractor.

    SISOB Data Extractor is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    SISOB Data Extractor is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with SISOB Data Extractor. If not, see <http://www.gnu.org/licenses/>.
*/
package eu.sisob.uma.restserver.managers;

import eu.sisob.uma.extractors.adhoc.websearchers.WebSearchersExtractor;
import eu.sisob.uma.restserver.beans.Task;
import eu.sisob.uma.restserver.beans.TaskOperationResult;
import eu.sisob.uma.restserver.TheResourceBundle;
import static eu.sisob.uma.restserver.managers.AuthorizationManager.error_flag_file;
import static eu.sisob.uma.restserver.managers.AuthorizationManager.feedback_flag_file;
import static eu.sisob.uma.restserver.managers.AuthorizationManager.params_flag_file;
import eu.sisob.uma.restserver.services.communications.InputLaunchTask;
import eu.sisob.uma.restserver.services.communications.InputParameter;
import eu.sisob.uma.restserver.services.communications.OutputTaskStatus;
import eu.sisob.uma.restserver.services.communications.TasksParams;
import eu.sisob.uma.restserver.services.crawler.CrawlerTask;
import eu.sisob.uma.restserver.services.email.EmailTask;
import eu.sisob.uma.restserver.services.gate.GateTask;
import eu.sisob.uma.restserver.services.gateCH.GateTaskCH;
import eu.sisob.uma.restserver.services.internalcvfiles.InternalCVFilesTask;
import eu.sisob.uma.restserver.services.websearchers.WebSearcherCVTask;
import eu.sisob.uma.restserver.services.websearchers.WebSearcherTask;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

/**
 *
 * This class cover the methods about tasks. This is dependent from AuthorizationManager.
 * 
 * Is important to know, that some method of this class working in a mutex code area of the AuthorizationManager mutex.
 * 
 */
public class TaskManager {
    
    private static final Logger LOG = Logger.getLogger(TaskManager.class.getName());    
    
    /**
     * 
     *  Is possible that use thee locker of AuthorizationManager
     * @param user
     * @param pass
     * @param task_code
     * @param retrieveDetails
     * @return
     * @throws java.lang.Exception
     */
    public static OutputTaskStatus getTask(String user, String pass, String task_code, 
                                            boolean retrieveDetails)
                                            throws Exception
    {
        OutputTaskStatus task_status = new OutputTaskStatus();
        task_status.setName(task_code);
        task_status.setTask_code(task_code);
        
        // Validations
        String taskFolder = TaskFileManager.getTaskFolder(user, task_code);            
        File fileTaskFolder = new File(taskFolder);
        if (!fileTaskFolder.exists()) {
            throw new Exception(TheResourceBundle.getString("Jsp Task Unknowed Msg"));
        }
        
        // Get TaskData
        Properties properties = TaskFileManager.getFileTaskData(taskFolder);
        if (properties != null) {
            task_status.setKind((String)properties.get(TaskFileManager.PROPERTY_TASK_KIND));
            task_status.setStatus((String)properties.get(TaskFileManager.PROPERTY_TASK_STATUS));
            task_status.setDate_started((String)properties.get(TaskFileManager.PROPERTY_TASK_DATE_STARTED));
            task_status.setDate_finished((String)properties.get(TaskFileManager.PROPERTY_TASK_DATE_FINISHED));
            task_status.setDate_created((String)properties.getProperty(TaskFileManager.PROPERTY_TASK_DATE_CREATED));
        }
        
        if (task_status.getStatus() != null){
            switch (task_status.getStatus()) {
                case OutputTaskStatus.TASK_STATUS_TO_EXECUTE:
                    task_status.setMessage(TheResourceBundle.getString("Jsp Task To Execute Msg"));
                    break;
                case OutputTaskStatus.TASK_STATUS_EXECUTING:
                    task_status.setMessage(TheResourceBundle.getString("Jsp Task Executing Msg"));
                    break;
                case OutputTaskStatus.TASK_STATUS_EXECUTED:
                    task_status.setMessage(TheResourceBundle.getString("Jsp Task Executed Msg"));
                    break;
            }
        }                           
        
        if(retrieveDetails){

            List<String> fresults = TaskFileManager.getResultFiles(user, task_code);
            task_status.setResults(fresults);
            
            List<String> fsources = TaskFileManager.getSourceFiles(user, task_code);
            task_status.setSource(fsources);
            
            List<String> fverboses = TaskFileManager.getVerboseFiles(user, task_code);
            task_status.setVerbose(fverboses);
            
            String pathErrorsFile = Paths.get(taskFolder, 
                                                AuthorizationManager.results_dirname, 
                                                error_flag_file).toString();
            String contentError = fileToString(pathErrorsFile, task_status);
            if (contentError != null) {
                task_status.setErrors(contentError);
            }
                    
            String pathParamFile = taskFolder + File.separator + params_flag_file;
            String contentParams = fileToString(pathParamFile, task_status);
            if (contentParams != null) {
                if(!contentParams.equals("")){
                    String[] lines = contentParams.split("\r\n");

                    for(String line : lines){
                        String[] values = line.split("\\$");
                        if(values.length > 1){
                            String[] iParam = {values[0], values[1]};
                            task_status.getParams().add(iParam);                                                      
                        }
                    }
                }
            }
            
            String pathFeedback = taskFolder + File.separator + 
                                AuthorizationManager.results_dirname + File.separator + 
                                feedback_flag_file;
            String contentFeedback = fileToString(pathFeedback, task_status);
            if (contentFeedback != null) {
                task_status.setFeedback(contentFeedback);
            }
        }
        
        return task_status;
    }
    
   /**
     * Create a new folder for a new task if is possible to launch new task
     * Note:
     *  use MAX_TASKS_PER_USER and validateAccess()
     *  Is possible that use thee locker of AuthorizationManager
     * @param user
     * @param pass
     * @return
     * @throws java.lang.Exception
     */
    public static Task prepareNewTask(String user, String pass) throws Exception
    {
        Task rTask = new Task();
        
        List<Integer> listTaskCode = TaskManager.listTaskCode(user);                

        int num_tasks_alive = 0;
        for(Integer taskCode : listTaskCode){
            OutputTaskStatus task = TaskManager.getTask(user, pass, taskCode.toString(), false);
            if(!task.getStatus().equals(OutputTaskStatus.TASK_STATUS_EXECUTED)){
                //Think about it
                num_tasks_alive++;
            }
        }
        
        if(num_tasks_alive >= AuthorizationManager.MAX_TASKS_PER_USER){
            String msg = "There are still tasks running or there are a task created ready to be launched.";
            throw new Exception(msg);
        }
            
        int max = 1; 
        if (!listTaskCode.isEmpty()) {
            max = Collections.max(listTaskCode);
            max++;
        }
        
        String newTaskFolder = String.valueOf(max);                    

        String pathTaskFolder = TaskFileManager.getTaskFolder(user, newTaskFolder);
        File task_dir = new File(pathTaskFolder);
        if(!task_dir.exists()){
            task_dir.mkdir();
            
            TaskFileManager.createFileTaskData(pathTaskFolder);
            
            rTask.setCode(newTaskFolder);
            rTask.setStatus(OutputTaskStatus.TASK_STATUS_TO_EXECUTE);
            rTask.setMessage("A new task has been created successfully."); 
        }
        else{
            throw new Exception("Error creating place for the new task.");
        }
        
        return rTask;
    }
    
    
    /**
     * Return the task of a user
     * 
     *  Is possible that use thee locker of AuthorizationManager
     * 
     * @param user
     * @param pass
     * @return
     * @throws java.lang.Exception
     */
    public static List<OutputTaskStatus> getTasks(String user, String pass) throws Exception{
        
        List<OutputTaskStatus> rListTask = new ArrayList();
        
        List<Integer> listTaskCode = TaskManager.listTaskCode(user);
        
        for(Integer taskCode : listTaskCode){
            OutputTaskStatus task_status = TaskManager.getTask(user, pass, taskCode.toString(), false);
            rListTask.add(task_status);
        }
        
        return rListTask;
    }
    
    
    /**
     * Return the task codes of a user
     * 
     *  Is possible that use thee locker of AuthorizationManager
     * 
     * @param user
     * @return
     */
    private static List<Integer> listTaskCode(String user){
        
        List<Integer> listTaskCode = new ArrayList();
        
        String pathTasksFolder = TaskFileManager.getUserFolder(user);
        File fileTasksFolder = new File(pathTasksFolder);
        if(fileTasksFolder.exists()){   
            
            List<String> tasks_folders = Arrays.asList(fileTasksFolder.list());
            for(String folder : tasks_folders){
                listTaskCode.add(Integer.parseInt(folder));
            }                    
        }
        
        Collections.sort(listTaskCode);
        
        return listTaskCode;
    }
    
    
    
    public static TaskOperationResult launchTask(InputLaunchTask input, boolean isRelaunch) throws Exception{
        
        TaskOperationResult result = new TaskOperationResult(); 
        
        OutputTaskStatus task = TaskManager.getTask(input.user, input.pass, input.task_code, false);
         
        boolean isValidToExecute = false;
        if(isRelaunch && OutputTaskStatus.TASK_STATUS_EXECUTED.equals(task.getStatus())){
            isValidToExecute = true;
        }
        else if (!isRelaunch && OutputTaskStatus.TASK_STATUS_TO_EXECUTE.equals(task.getStatus())) {
            isValidToExecute = true;
        }
        
        if (!isValidToExecute) {
            result.success = false;                 
            result.message = task.getMessage();
            return result;
        }
        
        String taskFolder = TaskFileManager.getTaskFolder(input.user, input.task_code);           

        if (isRelaunch) {
            try {
                File params_file = new File(taskFolder, AuthorizationManager.params_flag_file);

                List<String> params = FileUtils.readLines(params_file);

                input.parameters = new InputParameter[params.size()];

                int i = 0;
                for(String l : params){
                    String[] values = l.split("\\$");
                    if(values.length == 2){
                        InputParameter ip = new InputParameter();
                        ip.key = values[0];
                        ip.value = values[1];
                        input.parameters[i] = ip;
                        i++;                            
                    }                           
                }
            } 
            catch (Exception ex) {
                LOG.log(Level.SEVERE, "Error reading params filename " + AuthorizationManager.params_flag_file + "(" + taskFolder + ")", ex); //FIXME
            }
        }

        if("crawler".equals(input.task_kind)){

            StringWriter message = new StringWriter();

            result.success = CrawlerTask.launch(input.user, input.pass, input.task_code, taskFolder, input.user, message);                    
            result.message = message.toString();
        } 
        else if("websearcher".equals(input.task_kind)) {                       

            WebSearchersExtractor.SearchPatterns pattern = WebSearchersExtractor.SearchPatterns.P2;

            try{
                String value_mode = InputParameter.get(TasksParams.PARAM_CRAWLER_P1, input.parameters);
                if(value_mode != null && value_mode.equals(TasksParams.PARAM_TRUE))
                    pattern = WebSearchersExtractor.SearchPatterns.P1;

                value_mode = InputParameter.get(TasksParams.PARAM_CRAWLER_P2, input.parameters);
                if(value_mode != null && value_mode.equals(TasksParams.PARAM_TRUE))
                    pattern = WebSearchersExtractor.SearchPatterns.P2;

                value_mode = InputParameter.get(TasksParams.PARAM_CRAWLER_P3, input.parameters);
                if(value_mode != null && value_mode.equals(TasksParams.PARAM_TRUE))
                    pattern = WebSearchersExtractor.SearchPatterns.P3;

                value_mode = InputParameter.get(TasksParams.PARAM_CRAWLER_P4, input.parameters);
                if(value_mode != null && value_mode.equals(TasksParams.PARAM_TRUE))
                    pattern = WebSearchersExtractor.SearchPatterns.P4;

                value_mode = InputParameter.get(TasksParams.PARAM_CRAWLER_P5, input.parameters);
                if(value_mode != null && value_mode.equals(TasksParams.PARAM_TRUE))
                    pattern = WebSearchersExtractor.SearchPatterns.P5;

            }catch(Exception ex){
                pattern = WebSearchersExtractor.SearchPatterns.P2;
            }

            StringWriter message = new StringWriter();
            result.success = WebSearcherTask.launch(input.user, input.pass, input.task_code, taskFolder, input.user, pattern, message);  
            result.message = message.toString(); 
        }
        else if("websearcher_cv".equals(input.task_kind)) {                                   

            StringWriter message = new StringWriter();

            result.success = WebSearcherCVTask.launch(input.user, input.pass, input.task_code, taskFolder, input.user, message);                    
            result.message = message.toString();
        } 
        else if("internalcvfiles".equals(input.task_kind)) {                       

            StringWriter message = new StringWriter();

            result.success = InternalCVFilesTask.launch(input.user, input.pass, input.task_code, taskFolder, input.user, message);                    
            result.message = message.toString();
        }
        else if("email".equals(input.task_kind)) {                    

            String value_filters = InputParameter.get(TasksParams.PARAM_EMAIL_FILTERS, input.parameters);
            List<String> filters = new ArrayList();
            if(value_filters != null && !value_filters.equals(""))
            {
                String[] filters_string = value_filters.split(",");
                for(String filter : filters_string){
                    filters.add(filter.trim());
                }
                //filters = Arrays.asList(filters_string);                        
            }

            StringWriter message = new StringWriter();
            result.success = EmailTask.launch(input.user, input.pass, input.task_code, taskFolder, input.user, filters, message);                    
            result.message = message.toString();    
        }
        else if("gate".equals(input.task_kind)) {   

            boolean verbose = false;
            try{
                String value_verbose = InputParameter.get(TasksParams.PARAM_GATE_VERBOSE, input.parameters);
                if(value_verbose != null && value_verbose.equals(TasksParams.PARAM_TRUE))
                    verbose = true;
            }catch(Exception ex){
                verbose = true;
            }

            boolean split = false;
            try{
                String value_split = InputParameter.get(TasksParams.PARAM_GATE_SPLIT, input.parameters);
                if(value_split != null && value_split.equals(TasksParams.PARAM_TRUE))
                    split = true;
            }catch(Exception ex){
                split = false;
            }

            StringWriter message = new StringWriter();
            result.success = GateTask.launch(input.user, input.pass, input.task_code, taskFolder, input.user, message, verbose, split);
            result.message = message.toString();      
        }  
        else if(GateTaskCH.NAME.equals(input.task_kind)) {                       

            TaskOperationResult resultCH = GateTaskCH.launch(input.user, 
                                                            input.pass, 
                                                            input.task_code);
            result.success = resultCH.success;
            result.message = resultCH.message;
        }                
        else{

            result.success = false;
            result.message = TheResourceBundle.getString("Jsp Task Unknowed Msg");
        }

        // Notify in the folder that the task has been launched
        if(result.success)
        {   
            TaskFileManager.registerTaskLaunched(taskFolder, input.task_kind);
            
            if (!isRelaunch) {
                
                String pathFileParams = taskFolder + File.separator + AuthorizationManager.params_flag_file;
                try {
                    File params_file = new File(pathFileParams);
                    params_file.createNewFile();

                    FileUtils.write(params_file, "", "UTF-8", false);
                    if(input.parameters != null){
                        for(InputParameter ip : input.parameters){
                            FileUtils.write(params_file, ip.key + "$" + ip.value + "\r\n", "UTF-8", true);
                        }                            
                    }                          
                } catch (Exception ex) {
                    LOG.log(Level.SEVERE, "Error creating params filename " + pathFileParams, ex);
                }
            }
        }   
        
        return result;
    }
    
    public static TaskOperationResult deleteTask(String user, String pass, String task_code) throws Exception {
        
        TaskOperationResult result = new TaskOperationResult();
        
        OutputTaskStatus task = TaskManager.getTask(user, pass, task_code, false);            

        if( OutputTaskStatus.TASK_STATUS_EXECUTING.equals(task.getStatus()) ||
            OutputTaskStatus.TASK_STATUS_TO_EXECUTE.equals(task.getStatus()) )
        {   
            result.success = false;                 
            result.message = "The task couldn't be deleted. " + task.getMessage();
        }           
        else if(OutputTaskStatus.TASK_STATUS_EXECUTED.equals(task.getStatus()))
        { 
            String taskFolder = TaskFileManager.getTaskFolder(user, task_code);
            File dir_to_delete = new File(taskFolder);
            try{
                FileUtils.deleteDirectory(dir_to_delete);   
                if(dir_to_delete.exists()){
                    result.success = false;              
                    result.message = "The task couldn't be deleted";
                }else{
                    result.success = true;              
                    result.message = "The task " + task_code + " has been deleted";
                }
            }catch(Exception ex){
                result.success = false;              
                result.message = "";
                LOG.log(Level.SEVERE, "Error deleting task " + task_code, ex);
            }
        }
        
        return result;
    }
    
    private  static String fileToString(String pPathFile, OutputTaskStatus pTask){
        String rString = null;
        File file = new File(pPathFile);
        if(file.exists()){
            try {
                rString = FileUtils.readFileToString(file);
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, "Error, cant read {0} {1}", 
                        new Object[]{pPathFile, ex.getMessage()});
                pTask.setErrors("Error, cant read " + file.getName());
            }
        } 
        return rString;
    }
}
