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
import eu.sisob.uma.restserver.managers.AuthorizationManager;
import eu.sisob.uma.restserver.beans.AuthorizationResult;
import eu.sisob.uma.restserver.services.communications.OutputTaskStatus;
import java.util.Date;
import java.util.List;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

@Path("/tasks")
public class RESTSERVICETasks
{
    @Context
    private UriInfo context;

    /** Creates a new instance of HelloWorld */
    public RESTSERVICETasks() {

    }       
    
    /**
     * FIXME (OutputTaskStatus cannot report about the authentication, think about this).
     * Retrieves the state of the task
     * @param user 
     * @param pass      
     * @return  If the authorization is correct and a new code task
     */
    @GET
    @Produces("application/json")
    public List<OutputTaskStatus> getTasksList( @QueryParam("user") String user, 
                                                @QueryParam("pass") String pass) 
    {           
        List<OutputTaskStatus> rListTask = null;
        
        if(user == null) user = "";
        if(pass == null) pass = "";
        
        synchronized(user){
            AuthorizationResult autResult = AuthorizationManager.validateAccess(user, pass);
            if(autResult.getSuccess()){
                rListTask = TaskManager.getTasks(user, pass);
            }    
        }
        
        return rListTask;
    }
}
