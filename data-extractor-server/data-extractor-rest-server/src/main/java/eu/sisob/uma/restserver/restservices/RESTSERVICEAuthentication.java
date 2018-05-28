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

import eu.sisob.uma.restserver.beans.AuthenticationResult;
import eu.sisob.uma.restserver.managers.AuthorizationManager;
import static eu.sisob.uma.restserver.restservices.RESTSERVICEBase.LOG;
import eu.sisob.uma.restserver.restservices.security.AuthenticationUtils;
import eu.sisob.uma.restserver.restservices.exceptions.InternalServerErrorException;
import eu.sisob.uma.restserver.services.communications.User;
import eu.sisob.uma.restserver.services.communications.Authentication;
import java.util.logging.Level;
import javax.annotation.security.PermitAll;
import javax.ws.rs.Path;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/authentication")
public class RESTSERVICEAuthentication extends RESTSERVICEBase{

    /**
     * Checking access
     * @param authentication
     * @return
     */
    @PermitAll
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response authenticate(Authentication authentication) {    
        try {
            String user = authentication.getUser();
            String pass = authentication.getPass();
            
            validateRequired(user, "User");
            validateRequired(pass, "Password");
            
            synchronized(AuthorizationManager.getLocker(user)){
                
                AuthenticationResult autResult = AuthorizationManager.validateAccess(user, pass);
                
                if(!autResult.getSuccess()){
                    throw new WebApplicationException(autResult.getMessage(), Response.Status.UNAUTHORIZED);
                }
                
                User rUser = new User();
                rUser.setUsername(user);
                rUser.setAccount_type(autResult.getAccountType());
                
                String tokenHeader = AuthenticationUtils.createTokenHeader(user);
                
                Response response;
                response = Response.ok(rUser)
                                    .header(HttpHeaders.AUTHORIZATION, tokenHeader)
                                    .build();
                
                return response;
            }
        } catch (WebApplicationException ex) {
            throw ex;
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error authorization", ex);
            throw new InternalServerErrorException();
        }
    }
}
