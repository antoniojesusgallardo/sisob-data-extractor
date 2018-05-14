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

import eu.sisob.uma.restserver.services.communications.User;
import java.util.StringTokenizer;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.internal.util.Base64;

/**
 *
 * @author Antonio Jesus Gallardo Albarran - antonio.jesus.gallardo@gmail.com
 */
public class AuthenticationUtils {
    
    public static final String AUTHORIZATION_PROPERTY = "Authorization";
    public static final String AUTHENTICATION_SCHEME = "Basic";
    
    
    public static Response createResponseWithToken(String user, String pass, User data){
        
        String code = user+":"+pass;
        String token = Base64.encodeAsString(code);
        
        String tokenHeader = AUTHENTICATION_SCHEME + " " + token;
        
        Response response = Response.ok(data)
                                    .header(AUTHORIZATION_PROPERTY, tokenHeader)
                                    .build();
        
        return response;
    }
    
    public static String getUser(String token){
        
        //Get encoded username and password
        final String encodedUserPassword = token.replaceFirst(AUTHENTICATION_SCHEME + " ", "");

        //Decode username and password
        String usernameAndPassword = new String(Base64.decode(encodedUserPassword.getBytes()));;

        //Split username and password tokens
        final StringTokenizer tokenizer = new StringTokenizer(usernameAndPassword, ":");
        final String username = tokenizer.nextToken();
        final String password = tokenizer.nextToken();
        
        return username;
    }
    
    public static String getPassword(String token){
        final String encodedUserPassword = token.replaceFirst(AUTHENTICATION_SCHEME + " ", "");

        //Decode username and password
        String usernameAndPassword = new String(Base64.decode(encodedUserPassword.getBytes()));;

        //Split username and password tokens
        final StringTokenizer tokenizer = new StringTokenizer(usernameAndPassword, ":");
        final String username = tokenizer.nextToken();
        final String password = tokenizer.nextToken();
        
        return password;
    }
}
