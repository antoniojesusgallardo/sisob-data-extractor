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
package eu.sisob.uma.restserver.restservices.security;

import io.jsonwebtoken.impl.crypto.MacProvider;
import java.lang.reflect.Method;
import java.security.Key;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
 
import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import org.apache.commons.collections.CollectionUtils;
 
/**
 *
 * @author Antonio Jesus Gallardo Albarran - antonio.jesus.gallardo@gmail.com
 */
@Provider
public class AuthenticationFilter implements javax.ws.rs.container.ContainerRequestFilter
{
    public static final Key KEY = MacProvider.generateKey();
     
    @Context
    private ResourceInfo resourceInfo;
      
    @Override
    public void filter(ContainerRequestContext requestContext){
        
        try {
            
            Method method = resourceInfo.getResourceMethod();
            
            //Access allowed for all
            if (method.isAnnotationPresent(PermitAll.class)) {
                return;
            }
            
            //Access denied for all
            if(method.isAnnotationPresent(DenyAll.class)){
                
                Response response = Response.status(Response.Status.FORBIDDEN)
                                            .entity("Access blocked for all users !!").build();
                
                requestContext.abortWith(response);
                return;
            }

            //Fetch authorization header
            String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

            //If no authorization information present; block access
            if(authorizationHeader == null || authorizationHeader.isEmpty()){
                throw new Exception();
            }

            // Valida el token utilizando la cadena secreta
            AuthenticationUtils.validateToken(authorizationHeader);
            
            //Verify access
            if(method.isAnnotationPresent(RolesAllowed.class)){
                RolesAllowed rolesAnnotation = method.getAnnotation(RolesAllowed.class);
                List<String> rolesAnn = new ArrayList(Arrays.asList(rolesAnnotation.value()));

                List<String> rolesUser = AuthenticationUtils.getRoles(authorizationHeader);
                
                if(!isUserAllowed(rolesUser, rolesAnn)){
                    throw new Exception();
                }
            }
            
        } catch (Exception e) {
            Response response = Response.status(Response.Status.UNAUTHORIZED)
                                        .entity("You cannot access this resource").build();
            requestContext.abortWith(response);
            return;
        }
    }
    
    private boolean isUserAllowed(final List<String> rolesUser, final List<String> rolesAnn)
    {
        boolean isAllowed = true;
        
        Collection<String> intersection = CollectionUtils.intersection(rolesUser, rolesAnn);
        
        if (intersection == null || intersection.isEmpty()) {
            isAllowed = false;
        }
        
        return isAllowed;
    }
}
