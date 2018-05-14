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

import eu.sisob.uma.restserver.beans.NewTask;
import eu.sisob.uma.restserver.beans.TaskOperationResult;
import eu.sisob.uma.restserver.managers.AuthorizationManager;
import eu.sisob.uma.restserver.managers.TaskManager;
import static eu.sisob.uma.restserver.restservices.RESTSERVICEBase.LOG;
import eu.sisob.uma.restserver.restservices.exceptions.InternalServerErrorException;
import eu.sisob.uma.restserver.restservices.security.AuthenticationUtils;
import eu.sisob.uma.restserver.services.communications.LaunchTask;
import eu.sisob.uma.restserver.services.communications.Task;
import java.util.List;
import java.util.logging.Level;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

@Path("/tasks")
public class RESTSERVICETasks extends RESTSERVICEBase{

    /**
     * Retrieves all tasks of the current user
     * @return  If the authorization is correct and a new code task
     */
    @GET
    @RolesAllowed("user")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Task> getTasks() 
    {   
        String user = AuthenticationUtils.getUser(token);
        
        try {
            synchronized(user){
                
                List<Task> rListTask = TaskManager.getTasks(user);
                
                return rListTask;
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error getTasksList", e);
            throw new InternalServerErrorException();
        }
    }
    
    
    /**
     * Retrieves the data of the task
     * @param taskCode 
     * @return an instance of CrawlerTaskStatus with code provided
     */
    @GET
    @RolesAllowed("user")
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{task_code}")
    public Task getTask(@PathParam("task_code") String taskCode) 
    {
        String user = AuthenticationUtils.getUser(token);
        
        try {
            synchronized (AuthorizationManager.getLocker(user)) {

                Task taskStatus = TaskManager.getTask(user, taskCode, true); 
                return taskStatus;
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error getTaskStatus", e);
            throw new InternalServerErrorException();
        }
    }
    
    @POST
    @RolesAllowed("user")
    @Produces(MediaType.APPLICATION_JSON)
    public Task addTask() 
    {
        String user = AuthenticationUtils.getUser(token);
        
        try {
            synchronized(AuthorizationManager.getLocker(user)){
            
                NewTask newTask = TaskManager.prepareNewTask(user);
                
                Task taskStatus = new Task();
                taskStatus.setTask_code(newTask.getCode());
                taskStatus.setStatus(newTask.getStatus());
                taskStatus.setMessage(newTask.getMessage());

                return taskStatus;
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error addNewTask", e);
            throw new InternalServerErrorException();
        }
    }   
    
    @DELETE
    @RolesAllowed("user")
    @Produces(MediaType.TEXT_PLAIN)    
    @Path("/{task_code}")
    public String deleteTask(@PathParam("task_code") String taskCode) 
    {
        String user = AuthenticationUtils.getUser(token);
        
        try {
            synchronized (AuthorizationManager.getLocker(user)){

                TaskOperationResult result = TaskManager.deleteTask(user, taskCode);
                
                if (!result.success) {
                    throw new InternalServerErrorException(result.message);
                }
                
                return result.message;
            }
        } catch (WebApplicationException ex) {
            throw ex;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error deleteTask", e);
            throw new InternalServerErrorException();
        }
    }    
    
    /**
     * Method for re-launch a task.
     * @param taskCode 
     * @return 
     */
    @POST
    @RolesAllowed("user")
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/{task_code}/relaunch")
    public String relaunchTask(@PathParam("task_code") String taskCode) 
    {    
        String user = AuthenticationUtils.getUser(token);
        
        try {
            synchronized (AuthorizationManager.getLocker(user)){
                
                TaskOperationResult result = TaskManager.relaunchTask(user, taskCode);
                
                if (!result.success) {
                    throw new InternalServerErrorException(result.message);
                }
                
                return result.message;
            }
        } catch (WebApplicationException ex) {
            throw ex;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error relaunchTask", e);
            throw new InternalServerErrorException();
        }
    }

    /**
     * Method for launch a task.
     * @param taskCode
     * @param launchTask 
     * @return
     */
    @POST
    @RolesAllowed("user")
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/{task_code}/launch")
    public String launchTask(@PathParam("task_code") String taskCode, 
                            LaunchTask launchTask) 
    {
        String user = AuthenticationUtils.getUser(token);
        
        try {
            validateRequired(launchTask.task_kind, "Task Kind");
            
            synchronized (AuthorizationManager.getLocker(user)){

                TaskOperationResult result;
                result = TaskManager.launchTask(user, taskCode, launchTask.task_kind, 
                                                launchTask.parameters);
                
                if (!result.success) {
                    throw new InternalServerErrorException(result.message);
                }
                
                return result.message;
            }
        } catch (WebApplicationException ex) {
            throw ex;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error launchTask", e);
            throw new InternalServerErrorException();
        }
    }
}
