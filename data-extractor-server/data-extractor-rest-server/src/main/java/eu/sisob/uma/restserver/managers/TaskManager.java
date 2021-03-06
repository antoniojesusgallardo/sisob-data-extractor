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
import eu.sisob.uma.restserver.beans.TaskOperationResult;
import eu.sisob.uma.restserver.TheResourceBundle;
import static eu.sisob.uma.restserver.managers.AuthorizationManager.error_flag_file;
import static eu.sisob.uma.restserver.managers.AuthorizationManager.feedback_flag_file;
import static eu.sisob.uma.restserver.managers.AuthorizationManager.params_flag_file;
import eu.sisob.uma.restserver.services.communications.TaskParameter;
import eu.sisob.uma.restserver.services.communications.Task;
import eu.sisob.uma.restserver.services.communications.TaskParameters;
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
     * @param task_code
     * @param retrieveDetails
     * @return
     * @throws java.lang.Exception
     */
    public static Task getTask(String user, String task_code, 
                                            boolean retrieveDetails)
                                            throws Exception
    {
        Task task_status = new Task();
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
                            TaskParameter newParameter = new TaskParameter();
                            newParameter.setKey(values[0]);
                            newParameter.setValue(values[1]);
                            task_status.getParams().add(newParameter);
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
     * @return
     * @throws java.lang.Exception
     */
    public static Task prepareNewTask(String user) throws Exception
    {
        List<Integer> listTaskCode = TaskManager.listTaskCode(user);                

        int num_tasks_alive = 0;
        for(Integer taskCode : listTaskCode){
            Task task = TaskManager.getTask(user, taskCode.toString(), false);
            if(!task.getStatus().equals(Task.STATUS_EXECUTED)){
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
        Task rTask = new Task();
        if(!task_dir.exists()){
            task_dir.mkdir();
            
            TaskFileManager.createFileTaskData(pathTaskFolder);
            
            rTask.setTask_code(newTaskFolder);
            rTask.setStatus(Task.STATUS_TO_EXECUTE);
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
     * @return
     * @throws java.lang.Exception
     */
    public static List<Task> getTasks(String user) throws Exception{
        
        List<Task> rListTask = new ArrayList();
        
        List<Integer> listTaskCode = TaskManager.listTaskCode(user);
        
        for(Integer taskCode : listTaskCode){
            Task task_status = TaskManager.getTask(user, taskCode.toString(), false);
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
    
    public static TaskOperationResult launchTask(String user, String taskCode, 
                                                String taskKind, TaskParameter[] parameters) 
                                                                throws Exception{
        
        TaskOperationResult result = new TaskOperationResult(); 
        
        Task task = TaskManager.getTask(user, taskCode, true);
                
        if(!Task.STATUS_TO_EXECUTE.equals(task.getStatus())){
            result.success = false;
            result.message = "Task with wrong status, the status must be: " + Task.STATUS_TO_EXECUTE;

            return result;
        }
        
        result = executeTask(user, taskCode, taskKind, parameters);
        
        // Save the params
        if (result.success) {
            String taskFolder = TaskFileManager.getTaskFolder(user, taskCode);           

            String pathFileParams = taskFolder + File.separator + AuthorizationManager.params_flag_file;
            try {
                File params_file = new File(pathFileParams);
                params_file.createNewFile();

                FileUtils.write(params_file, "", "UTF-8", false);
                if(parameters != null){
                    for(TaskParameter ip : parameters){
                        String content = ip.getKey() + "$" + ip.getValue() + "\r\n";
                        FileUtils.write(params_file, content, "UTF-8", true);
                    }                            
                }                          
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Error creating params filename " + pathFileParams, ex);
            }
        }
        
        return result;
    }
    
    public static TaskOperationResult relaunchTask(String user, String taskCode) 
                                                                throws Exception{
        
        TaskOperationResult result = new TaskOperationResult(); 
        
        Task task = TaskManager.getTask(user, taskCode, true);
                
        if(!Task.STATUS_EXECUTED.equals(task.getStatus())){
            result.success = false;
            result.message = "Task with wrong status, the status must be: " + Task.STATUS_EXECUTED;

            return result;
        }
        
        TaskParameter[] parameters = task.getParams().toArray(new TaskParameter[0]);
                
        result = executeTask(user, taskCode, task.getKind(), parameters);
        return result;
    }
    
    private static TaskOperationResult executeTask(String user, String taskCode, 
                                                String taskKind, TaskParameter[] parameters) 
                                                                throws Exception{
        
        TaskOperationResult result = new TaskOperationResult(); 
        
        String taskFolder = TaskFileManager.getTaskFolder(user, taskCode);           

        if("crawler".equals(taskKind)){

            StringWriter message = new StringWriter();

            result.success = CrawlerTask.launch(user, taskCode, taskFolder, user, message);                    
            result.message = message.toString();
        } 
        else if("websearcher".equals(taskKind)) {                       

            WebSearchersExtractor.SearchPatterns pattern = WebSearchersExtractor.SearchPatterns.P2;

            try{
                String value_mode = TaskParameter.get(TaskParameters.PARAM_CRAWLER_P1, parameters);
                if(TaskParameters.PARAM_TRUE.equals(value_mode))
                    pattern = WebSearchersExtractor.SearchPatterns.P1;

                value_mode = TaskParameter.get(TaskParameters.PARAM_CRAWLER_P2, parameters);
                if(TaskParameters.PARAM_TRUE.equals(value_mode))
                    pattern = WebSearchersExtractor.SearchPatterns.P2;

                value_mode = TaskParameter.get(TaskParameters.PARAM_CRAWLER_P3, parameters);
                if(TaskParameters.PARAM_TRUE.equals(value_mode))
                    pattern = WebSearchersExtractor.SearchPatterns.P3;

                value_mode = TaskParameter.get(TaskParameters.PARAM_CRAWLER_P4, parameters);
                if(TaskParameters.PARAM_TRUE.equals(value_mode))
                    pattern = WebSearchersExtractor.SearchPatterns.P4;

                value_mode = TaskParameter.get(TaskParameters.PARAM_CRAWLER_P5, parameters);
                if(TaskParameters.PARAM_TRUE.equals(value_mode))
                    pattern = WebSearchersExtractor.SearchPatterns.P5;

            }catch(Exception ex){
                pattern = WebSearchersExtractor.SearchPatterns.P2;
            }

            StringWriter message = new StringWriter();
            result.success = WebSearcherTask.launch(user, taskCode, taskFolder, user, pattern, message);  
            result.message = message.toString(); 
        }
        else if("websearcher_cv".equals(taskKind)) {                                   

            StringWriter message = new StringWriter();

            result.success = WebSearcherCVTask.launch(user, taskCode, taskFolder, user, message);                    
            result.message = message.toString();
        } 
        else if("internalcvfiles".equals(taskKind)) {                       

            StringWriter message = new StringWriter();

            result.success = InternalCVFilesTask.launch(user, taskCode, taskFolder, user, message);                    
            result.message = message.toString();
        }
        else if("email".equals(taskKind)) {                    

            String value_filters = TaskParameter.get(TaskParameters.PARAM_EMAIL_FILTERS, parameters);
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
            result.success = EmailTask.launch(user, taskCode, taskFolder, user, filters, message);                    
            result.message = message.toString();    
        }
        else if("gate".equals(taskKind)) {   

            boolean verbose = false;
            try{
                String value_verbose = TaskParameter.get(TaskParameters.PARAM_GATE_VERBOSE, parameters);
                if(TaskParameters.PARAM_TRUE.equals(value_verbose))
                    verbose = true;
            }catch(Exception ex){
                verbose = true;
            }

            boolean split = false;
            try{
                String value_split = TaskParameter.get(TaskParameters.PARAM_GATE_SPLIT, parameters);
                if(TaskParameters.PARAM_TRUE.equals(value_split))
                    split = true;
            }catch(Exception ex){
                split = false;
            }

            StringWriter message = new StringWriter();
            result.success = GateTask.launch(user, taskCode, taskFolder, user, message, verbose, split);
            result.message = message.toString();      
        }  
        else if(GateTaskCH.NAME.equals(taskKind)) {                       

            TaskOperationResult resultCH = GateTaskCH.launch(user, taskCode);
            result.success = resultCH.success;
            result.message = resultCH.message;
        }                
        else{

            result.success = false;
            result.message = TheResourceBundle.getString("Jsp Task Unknowed Msg");
        }

        // Notify in the folder that the task has been launched
        if(result.success){   
            TaskFileManager.registerTaskLaunched(taskFolder, taskKind);
        }   
        
        return result;
    }
    
    public static TaskOperationResult deleteTask(String user, String task_code) throws Exception {
        
        TaskOperationResult result = new TaskOperationResult();
        
        Task task = TaskManager.getTask(user, task_code, false);
        
        try{
            if(!Task.STATUS_EXECUTED.equals(task.getStatus())){   
                result.success = false;                 
                result.message = "The task couldn't be deleted. The task status must be: " + Task.STATUS_EXECUTED;
                return result;
            }           
             
            String taskFolder = TaskFileManager.getTaskFolder(user, task_code);
            File dir_to_delete = new File(taskFolder);

            FileUtils.deleteDirectory(dir_to_delete);   
            if(dir_to_delete.exists()){
                result.success = false;              
                result.message = "The task couldn't be deleted";
            }
            else{
                result.success = true;              
                result.message = "The task " + task_code + " has been deleted";
            }
        }catch(Exception ex){
            result.success = false;              
            result.message = "";
            LOG.log(Level.SEVERE, "Error deleting task " + task_code, ex);
        }
        
        return result;
    }
    
    private  static String fileToString(String pPathFile, Task pTask){
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
