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

import eu.sisob.uma.restserver.managers.TaskManager;
import eu.sisob.uma.restserver.restservices.exceptions.InternalServerErrorException;
import eu.sisob.uma.restserver.restservices.security.AuthenticationUtils;
import eu.sisob.uma.restserver.services.communications.OutputTaskStatus;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

@Path("/tasks")
public class RESTSERVICETasks {

    @Context 
    HttpHeaders headers;
    
    public RESTSERVICETasks() {
    }       
    
    /**
     * Retrieves the state of the task
     * @return  If the authorization is correct and a new code task
     */
    @GET
    @RolesAllowed("user")
    @Produces(MediaType.APPLICATION_JSON)
    public List<OutputTaskStatus> getTasksList() 
    {   
        String user = AuthenticationUtils.getCurrentUser(headers);
        
        try {
            synchronized(user){
                
                List<OutputTaskStatus> rListTask = TaskManager.getTasks(user);
                
                return rListTask;
            }
        } catch (WebApplicationException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }
}
