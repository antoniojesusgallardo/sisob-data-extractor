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
package eu.sisob.uma.restserver.restservices;

import eu.sisob.uma.restserver.beans.AuthorizationResult;
import eu.sisob.uma.restserver.managers.TaskManager;
import eu.sisob.uma.restserver.managers.AuthorizationManager;
import eu.sisob.uma.restserver.services.communications.OutputTaskOperationResult;
import eu.sisob.uma.restserver.services.communications.OutputTaskStatus;

import eu.sisob.uma.restserver.beans.Task;
import eu.sisob.uma.restserver.managers.TaskFileManager;
import eu.sisob.uma.restserver.services.communications.InputAddTask;
import eu.sisob.uma.restserver.services.communications.InputLaunchTask;
import java.io.File;
import java.util.logging.Level;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import org.apache.commons.io.FileUtils;

@Path("/task")
public class RESTSERVICETask {
    private static final java.util.logging.Logger LOG = java.util.logging.Logger.getLogger(RESTSERVICETask.class.getName());        

    @Context
    private UriInfo context;

    /** Creates a new instance of HelloWorld */
    public RESTSERVICETask() 
    {
        
    }

    /**
     * Retrieves the state of the task
     * @param user 
     * @param pass 
     * @param task_code 
     * @return an instance of CrawlerTaskStatus with code provided
     */
    @GET
    @Produces("application/json")
    public OutputTaskStatus getTaskStatus(@QueryParam("user") String user, 
                                        @QueryParam("pass") String pass, 
                                        @QueryParam("task_code") String task_code) 
    {
        OutputTaskStatus taskStatus = new OutputTaskStatus();                             
        
        synchronized (AuthorizationManager.getLocker(user)) {
            
            AuthorizationResult autResult = AuthorizationManager.validateAccess(user, pass);
            if(!autResult.getSuccess()){
                taskStatus.setTask_code("");
                taskStatus.setStatus(OutputTaskStatus.TASK_STATUS_NO_AUTH);
                taskStatus.setMessage(autResult.getMessage());
                return taskStatus;
            }
            
            taskStatus = TaskManager.getTask(user, pass, task_code, true);  
        }
                       
        return taskStatus;        
    }
    
    @POST
    @Produces("application/json")    
    @Path("/add")
    public OutputTaskStatus addNewTask(InputAddTask input) 
    {
        OutputTaskStatus taskStatus = new OutputTaskStatus();
        
        synchronized(AuthorizationManager.getLocker(input.user)){
            
            AuthorizationResult autResult = AuthorizationManager.validateAccess(input.user, input.pass);
            if(!autResult.getSuccess()){
                taskStatus.setTask_code("");
                taskStatus.setStatus(OutputTaskStatus.TASK_STATUS_NO_AUTH);
                taskStatus.setMessage(autResult.getMessage());
                return taskStatus;
            }
            
            Task newTask = TaskManager.prepareNewTask(input.user, input.pass);
                
            taskStatus.setTask_code(newTask.getCode());
            taskStatus.setStatus(newTask.getStatus());
            taskStatus.setMessage(newTask.getMessage());
        }
        
        return taskStatus;
    }   
    
    @POST
    @Produces("application/json")    
    @Path("/delete")
    public OutputTaskOperationResult deleteTask(InputLaunchTask input) 
    {        
        OutputTaskOperationResult result = new OutputTaskOperationResult();            
        
        synchronized (AuthorizationManager.getLocker(input.user)) 
        {
            AuthorizationResult autResult = AuthorizationManager.validateAccess(input.user, input.pass);
            if(!autResult.getSuccess()){
                result.success = autResult.getSuccess();
                result.message = autResult.getMessage();
                return result;
            }
            
            OutputTaskStatus task = TaskManager.getTask(input.user, input.pass, input.task_code, false);            
            
            if( OutputTaskStatus.TASK_STATUS_NO_AUTH.equals(task.getStatus()) ||
                OutputTaskStatus.TASK_STATUS_NO_ACCESS.equals(task.getStatus()) ||
                OutputTaskStatus.TASK_STATUS_EXECUTING.equals(task.getStatus()) ||
                OutputTaskStatus.TASK_STATUS_TO_EXECUTE.equals(task.getStatus()) )
            {   
                result.success = false;                 
                result.message = "The task couldn't be deleted. " + task.getMessage();
            }           
            else if(OutputTaskStatus.TASK_STATUS_EXECUTED.equals(task.getStatus()))
            { 
                String taskFolder = TaskFileManager.getTaskFolder(input.user, input.task_code);
                File dir_to_delete = new File(taskFolder);
                try{
                    FileUtils.deleteDirectory(dir_to_delete);   
                    if(dir_to_delete.exists()){
                        result.success = false;              
                        result.message = "The task couldn't be deleted";
                    }else{
                        result.success = true;              
                        result.message = "The task " + input.task_code + " has been deleted";
                    }
                }catch(Exception ex){
                    result.success = false;              
                    result.message = "";
                    LOG.log(Level.SEVERE, "Error deleting task " + input.task_code, ex);
                }
            }
        }
        
        return result;
    }    
    
    /**
     * POST method for updating or creating an instance of HelloWorld
     * @param input 
     * @return an instance of RESTResult
     */
    @POST    
    @Produces("application/json")
    @Path("/relaunch")
    public OutputTaskOperationResult relaunchTask(InputLaunchTask input) 
    {        
        OutputTaskOperationResult result = new OutputTaskOperationResult();            
        
        synchronized (AuthorizationManager.getLocker(input.user)){
            
            AuthorizationResult autResult = AuthorizationManager.validateAccess(input.user, input.pass);
            if(!autResult.getSuccess()){
                result.success = autResult.getSuccess();
                result.message = autResult.getMessage();
                return result;
            }
            
            result = TaskManager.launchTask(input, true);
        }
        
        return result;
    }

    /**
     * POST method for updating or creating an instance of HelloWorld
     * @param input 
     * @return an instance of RESTResult
     */
    @POST    
    @Produces("application/json")
    @Path("/launch")
    public OutputTaskOperationResult launchTask(InputLaunchTask input) 
    {        
        OutputTaskOperationResult result= new OutputTaskOperationResult();
        
        synchronized (AuthorizationManager.getLocker(input.user)){
            
            AuthorizationResult autResult = AuthorizationManager.validateAccess(input.user, input.pass);
            if(!autResult.getSuccess()){
                result.success = autResult.getSuccess();
                result.message = autResult.getMessage();
                return result;
            }
            
            result = TaskManager.launchTask(input, false);
        }

        return result;
    }
}
