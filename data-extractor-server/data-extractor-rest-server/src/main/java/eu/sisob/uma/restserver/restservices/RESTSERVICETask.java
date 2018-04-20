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

import eu.sisob.uma.restserver.beans.Task;
import eu.sisob.uma.restserver.beans.TaskOperationResult;
import eu.sisob.uma.restserver.managers.AuthorizationManager;
import eu.sisob.uma.restserver.managers.TaskManager;
import eu.sisob.uma.restserver.restservices.exceptions.InternalServerErrorException;
import eu.sisob.uma.restserver.restservices.exceptions.UnAuthorizedException;
import eu.sisob.uma.restserver.services.communications.InputAddTask;
import eu.sisob.uma.restserver.services.communications.InputLaunchTask;
import eu.sisob.uma.restserver.services.communications.OutputTaskStatus;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/task")
public class RESTSERVICETask {
    
    public RESTSERVICETask(){
    }

    /**
     * Retrieves the state of the task
     * @param user 
     * @param pass 
     * @param task_code 
     * @return an instance of CrawlerTaskStatus with code provided
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public OutputTaskStatus getTaskStatus(@QueryParam("user") String user, 
                                    @QueryParam("pass") String pass, 
                                    @QueryParam("task_code") String task_code) 
    {
        try {
            synchronized (AuthorizationManager.getLocker(user)) {

                RESTSERVICEUtils.validateAccess(user, pass);

                OutputTaskStatus taskStatus = TaskManager.getTask(user, pass, task_code, true); 
                return taskStatus;
            }
        } catch (UnAuthorizedException | InternalServerErrorException ex) {
            throw ex;
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)    
    @Path("/add")
    public OutputTaskStatus addNewTask(InputAddTask input) 
    {
        try {
            synchronized(AuthorizationManager.getLocker(input.user)){
            
                RESTSERVICEUtils.validateAccess(input.user, input.pass);
                
                Task newTask = TaskManager.prepareNewTask(input.user, input.pass);
                
                OutputTaskStatus taskStatus = new OutputTaskStatus();
                taskStatus.setTask_code(newTask.getCode());
                taskStatus.setStatus(newTask.getStatus());
                taskStatus.setMessage(newTask.getMessage());

                return taskStatus;
            }
        } catch (UnAuthorizedException | InternalServerErrorException ex) {
            throw ex;
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }   
    
    @POST
    @Produces(MediaType.TEXT_PLAIN)    
    @Path("/delete")
    public String deleteTask(InputLaunchTask input) 
    {   
        try {
            synchronized (AuthorizationManager.getLocker(input.user)){

                RESTSERVICEUtils.validateAccess(input.user, input.pass);

                TaskOperationResult result = TaskManager.deleteTask(input.user, input.pass, input.task_code);
                
                if (!result.success) {
                    throw new InternalServerErrorException(result.message);
                }
                
                return result.message;
            }
        } catch (UnAuthorizedException | InternalServerErrorException ex) {
            throw ex;
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }    
    
    /**
     * POST method for updating or creating an instance of HelloWorld
     * @param input 
     * @return an instance of RESTResult
     */
    @POST    
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/relaunch")
    public String relaunchTask(InputLaunchTask input) 
    {       
        try {
            synchronized (AuthorizationManager.getLocker(input.user)){

                RESTSERVICEUtils.validateAccess(input.user, input.pass);

                TaskOperationResult result = TaskManager.launchTask(input, true);
                
                if (!result.success) {
                    throw new InternalServerErrorException(result.message);
                }
                
                return result.message;
            }
        } catch (UnAuthorizedException | InternalServerErrorException ex) {
            throw ex;
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    /**
     * POST method for updating or creating an instance of HelloWorld
     * @param input 
     * @return an instance of RESTResult
     */
    @POST    
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/launch")
    public String launchTask(InputLaunchTask input) 
    {   
        try {
            synchronized (AuthorizationManager.getLocker(input.user)){

                RESTSERVICEUtils.validateAccess(input.user, input.pass);

                TaskOperationResult result = TaskManager.launchTask(input, false);
                
                if (!result.success) {
                    throw new InternalServerErrorException(result.message);
                }
                
                return result.message;
            }
        } catch (UnAuthorizedException | InternalServerErrorException ex) {
            throw ex;
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }
}
