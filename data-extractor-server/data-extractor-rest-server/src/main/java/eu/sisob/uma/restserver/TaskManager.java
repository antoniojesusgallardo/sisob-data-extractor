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
package eu.sisob.uma.restserver;

import static eu.sisob.uma.restserver.AuthorizationManager.TASKS_USERS_PATH;
import static eu.sisob.uma.restserver.AuthorizationManager.begin_flag_file;
import static eu.sisob.uma.restserver.AuthorizationManager.end_flag_file;
import static eu.sisob.uma.restserver.AuthorizationManager.error_flag_file;
import static eu.sisob.uma.restserver.AuthorizationManager.feedback_flag_file;
import static eu.sisob.uma.restserver.AuthorizationManager.kind_flag_file;
import static eu.sisob.uma.restserver.AuthorizationManager.params_flag_file;

import eu.sisob.uma.restserver.services.communications.OutputTaskStatus;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
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
     * @param retrieveTiming 
     * @param retrieveDataUrls 
     * @param retrieveFeedback 
     * @return
     */
    public static OutputTaskStatus getTaskStatus(String user, String pass, String task_code, boolean retrieveTiming, boolean retrieveDataUrls, boolean retrieveFeedback)
    {
        OutputTaskStatus task_status = new OutputTaskStatus();
        task_status.setName(task_code);
        task_status.setTask_code(task_code);
        
        // Start validations
        if(user==null || pass==null || task_code==null){
            task_status.setStatus(OutputTaskStatus.TASK_STATUS_NO_AUTH);            
            task_status.setMessage(TheResourceBundle.getString("Jsp Params Invalid Msg"));
            return task_status;
        }
        
        StringWriter message = new StringWriter();
        if(!AuthorizationManager.validateAccess(user, pass, message)){
            task_status.setStatus(OutputTaskStatus.TASK_STATUS_NO_AUTH);
            task_status.setMessage(message.toString());
            return task_status;
        }
        
        String code_task_folder = TASKS_USERS_PATH + File.separator + user + File.separator + task_code;            
        File file = new File(code_task_folder);
        if (!file.exists()) {
            task_status.setStatus(OutputTaskStatus.TASK_STATUS_NO_ACCESS);   
            task_status.setMessage(TheResourceBundle.getString("Jsp Task Unknowed Msg"));
            return task_status;
        }
        // End validations
             
        /* Retrieve data created*/                                        
        if(retrieveTiming){
            task_status.setDate_created((new SimpleDateFormat("yyyy.MM.dd - HH:mm:ss")).format(new Date(file.lastModified())));
        }
        
        File kind_file = new File(code_task_folder + File.separator + kind_flag_file);
        if(kind_file.exists()){  
            try{                            
                task_status.setKind(FileUtils.readFileToString(kind_file));                                
            } 
            catch (FileNotFoundException ex){
                LOG.log(Level.SEVERE, "Error, cant read " + kind_file.getAbsolutePath());
                task_status.setKind("none");
            }
            catch (IOException ex){
                LOG.log(Level.SEVERE, "Error, cant read " + kind_file.getAbsolutePath());
                task_status.setKind("none");
            }
        }
        else{
            task_status.setKind("none");
        }
        

        File begin_file = new File(code_task_folder + File.separator + begin_flag_file);
        
        // If not exist beginFile then the task status is: To Execute. EXIT
        if (!begin_file.exists()) {
            task_status.setStatus(OutputTaskStatus.TASK_STATUS_TO_EXECUTE);            
            task_status.setMessage(TheResourceBundle.getString("Jsp Task To Execute Msg"));
            return task_status;
        }
            
        if(retrieveTiming){
            task_status.setDate_started((new SimpleDateFormat("yyyy.MM.dd - HH:mm:ss")).format(new Date(begin_file.lastModified())));
        }

        File end_file = new File(code_task_folder + File.separator + end_flag_file);
        
        // If not exist endFile then the task status is: Executing. EXIT
        if (!end_file.exists()) {
            task_status.setStatus(OutputTaskStatus.TASK_STATUS_EXECUTING);            
            task_status.setMessage(TheResourceBundle.getString("Jsp Task Executing Msg"));
            return task_status;
        }

        if(retrieveTiming){
            task_status.setDate_finished((new SimpleDateFormat("yyyy.MM.dd - HH:mm:ss")).format(new Date(end_file.lastModified())));
        }

        task_status.setStatus(OutputTaskStatus.TASK_STATUS_EXECUTED);            
        task_status.setMessage(TheResourceBundle.getString("Jsp Task Executed Msg"));                            
        
        if(retrieveDataUrls){

            task_status.setResults(new ArrayList<String[]>());
            List<String> fresults = AuthorizationManager.getResultFiles(user, task_code);

            for(String fileName : fresults){
                String urlDownload = AuthorizationManager.getGetFileUrl(user, pass, task_code, fileName, "results");
                String urlShow = urlDownload.replace("file/download?","file/show?");
                String[] iResult = {fileName, urlDownload, urlShow};

                task_status.getResults().add(iResult);
            }

            task_status.setSource(new ArrayList<String[]>());
            List<String> fsources = AuthorizationManager.getSourceFiles(user, task_code);
            for(String fsource : fsources){
                String file_url = AuthorizationManager.getGetFileUrl(user, pass, task_code, fsource, "");
                String[] iSource = {fsource, file_url};
                task_status.getSource().add(iSource);
            }

            //Also do method to build path
            task_status.setVerbose(new ArrayList<String[]>());
            List<String> fverboses = AuthorizationManager.getVerboseFiles(user, task_code);

            for(String fverbose : fverboses){
                String file_url = AuthorizationManager.getGetFileUrlToShow(user, pass, task_code, fverbose, "verbose");
                String[] iVerbose = {fverbose, file_url};
                task_status.getVerbose().add(iVerbose);
            }

            File errors_file = new File(code_task_folder + File.separator + AuthorizationManager.results_dirname + File.separator + error_flag_file);
            if(errors_file.exists()){
                try {
                    task_status.setErrors(FileUtils.readFileToString(errors_file));
                } catch (IOException ex) {
                    LOG.log(Level.SEVERE, "Error, cant read " + errors_file.getAbsolutePath() + " " + ex.getMessage());
                    task_status.setErrors("Error, cant read " + errors_file.getName());
                }
            } 

            File params_file = new File(code_task_folder + File.separator + params_flag_file);

            task_status.setParams(new ArrayList<String[]>());
            if(params_file.exists()){
                try {
                    String params = FileUtils.readFileToString(params_file);
                    if(!params.equals("")){
                        String[] lines = params.split("\r\n");

                        for(String line : lines){
                            String[] values = line.split("\\$");
                            if(values.length > 1){
                                String[] iParam = {values[0], values[1]};
                                task_status.getParams().add(iParam);                                                      
                            }
                        }
                    }
                } catch (IOException ex) {
                    LOG.log(Level.SEVERE, "Error, cant read " + params_file.getAbsolutePath() + " " + ex.getMessage());
                    task_status.setErrors("Error, cant read " + params_file.getName());
                }
            }
        }          

        if(retrieveFeedback){
            String pathFeedback = code_task_folder + File.separator + 
                                AuthorizationManager.results_dirname + File.separator + 
                                feedback_flag_file;
            File feedback_file = new File(pathFeedback);

            String feedback = "";
            if(feedback_file.exists()){                                        
                try {
                    feedback = FileUtils.readFileToString(feedback_file);
                } catch (IOException ex) {
                    LOG.log(Level.SEVERE, "Error, cant read " + feedback_file.getAbsolutePath() + " " + ex.getMessage());
                }
            }
            task_status.setFeedback(feedback);
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
     * @param status
     * @param message 
     * @return
     */
    public static String prepareNewTask(String user, String pass, StringWriter status, StringWriter message)
    {
        String new_folder_name = "";        
        if(message == null){ 
            return "";
        }        
        message.getBuffer().setLength(0);
        
        if (user==null || pass==null) {
            new_folder_name = "";
            status.write(OutputTaskStatus.TASK_STATUS_NO_AUTH);
            if(message != null) message.write(TheResourceBundle.getString("Jsp Params Invalid Msg"));
            return new_folder_name;
        }
        
        if(!AuthorizationManager.validateAccess(user, pass, message)){
            new_folder_name = "";
            status.append(OutputTaskStatus.TASK_STATUS_NO_AUTH);
            return new_folder_name;
        }
            
        message.getBuffer().setLength(0);
        List<String> task_code_list = TaskManager.listTasks(user, pass);                

        int num_tasks_alive = 0;
        int max = -1;
        if(task_code_list != null){
            for(String task_code : task_code_list){
                
                OutputTaskStatus task_status = TaskManager.getTaskStatus(user, pass, task_code, false, false, false);
                if(!task_status.getStatus().equals(OutputTaskStatus.TASK_STATUS_EXECUTED)){
                    //Think about it
                    num_tasks_alive++;
                }

                try{
                    int act = Integer.parseInt(task_code);
                    if(max < act){
                        max = act;
                    }
                }
                catch(Exception ex){
                }
            }
        }                    

        if(num_tasks_alive < AuthorizationManager.MAX_TASKS_PER_USER){
            
            new_folder_name = String.valueOf(max + 1);                    

            String code_task_folder = TASKS_USERS_PATH + File.separator + user + File.separator + new_folder_name;
            File task_dir = new File(code_task_folder);
            if(!task_dir.exists()){
                task_dir.mkdir();
                status.append(OutputTaskStatus.TASK_STATUS_TO_EXECUTE);
                if(message != null) message.append("A new task has been created successfully."); //FIXME
            }
            else{
                new_folder_name = "";
                status.append(OutputTaskStatus.TASK_STATUS_NO_ACCESS);
                if(message != null) message.append("Error creating place for the new task."); //FIXME
            }
        }
        else{                        
            new_folder_name = "";
            status.append(OutputTaskStatus.TASK_STATUS_EXECUTING);
            if(message != null) message.append("There are still tasks running or there are a task created ready to be launched."); //FIXME
        }                    

        return new_folder_name;
    }
   
            
    /**
     * Return the task codes of a user
     * 
     *  Is possible that use thee locker of AuthorizationManager
     * 
     * @param user
     * @param pass
     * @return
     */
    public static List<String> listTasks(String user, String pass)
    {
        List<String> results = new ArrayList<String>();
        
        String result_code_task_folder = TASKS_USERS_PATH + File.separator + user;
            
        File result_file = new File(result_code_task_folder);
        if(result_file.exists()){   
            
            List<String> tasks_folders = Arrays.asList(result_file.list());
            for(String folder : tasks_folders){
                results.add(folder);
            }                    
        }
        
        return results;   
    }
            
}
