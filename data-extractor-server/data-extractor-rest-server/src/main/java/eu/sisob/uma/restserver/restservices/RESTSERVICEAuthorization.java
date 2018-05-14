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
import eu.sisob.uma.restserver.managers.AuthorizationManager;
import static eu.sisob.uma.restserver.restservices.RESTSERVICEBase.LOG;
import eu.sisob.uma.restserver.restservices.security.AuthenticationUtils;
import eu.sisob.uma.restserver.restservices.exceptions.InternalServerErrorException;
import eu.sisob.uma.restserver.services.communications.User;
import eu.sisob.uma.restserver.services.communications.Authorization;
import java.security.MessageDigest;
import java.util.logging.Level;
import javax.annotation.security.PermitAll;
import javax.ws.rs.Path;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/authorization")
public class RESTSERVICEAuthorization extends RESTSERVICEBase{

    /**
     * Checking access
     * @param authorization
     * @return If the authorization is correct
     */
    @PermitAll
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response authorization(Authorization authorization) {    
        try {
            String user = authorization.getUser();
            String pass = authorization.getPass();
            
            validateRequired(user, "User");
            validateRequired(pass, "Password");
            
            synchronized(AuthorizationManager.getLocker(user)){
                
                // Convert password to SHA-256
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                md.update(pass.getBytes());

                byte byteData[] = md.digest();
                //convert the byte to hex format method 1
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < byteData.length; i++) {
                    sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
                }
                pass = sb.toString();
                // END - Convert password to SHA-256

                AuthorizationResult autResult = AuthorizationManager.validateAccess(user, pass);
                
                Response response;
                if(!autResult.getSuccess()){
                    throw new WebApplicationException(autResult.getMessage(), Response.Status.UNAUTHORIZED);
                }
                
                User rUser = new User();
                rUser.setUsername(user);
                rUser.setAccount_type(autResult.getAccountType());
                
                response = AuthenticationUtils.createResponseWithToken(user, pass, rUser);
                
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
