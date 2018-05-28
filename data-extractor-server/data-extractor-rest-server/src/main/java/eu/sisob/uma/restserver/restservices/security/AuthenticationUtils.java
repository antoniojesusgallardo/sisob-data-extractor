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

import static eu.sisob.uma.restserver.restservices.security.AuthenticationFilter.KEY;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Antonio Jesus Gallardo Albarran - antonio.jesus.gallardo@gmail.com
 */
public class AuthenticationUtils {
    
    
    public static String createTokenHeader(String user){
        
        String token = issueToken(user);
        
        String tokenHeader = AuthenticationConstant.SCHEME + " " + token;
        
        return tokenHeader;
    }
    
    public static void validateToken(String tokenHeader){
        
        String token = getToken(tokenHeader);
        
        Jwts.parser().setSigningKey(KEY).parseClaimsJws(token);
    }
    
    public static String getUser(String pToken){
        
        String token = getToken(pToken);
        
        Jws jws = Jwts.parser().setSigningKey(KEY).parseClaimsJws(token);
        Claims claims = (Claims)jws.getBody();
        String user = claims.getSubject();
        
        return user;
    }
    
    public static List<String> getRoles(String pToken){
        
        String token = getToken(pToken);
        
        Jws jws = Jwts.parser().setSigningKey(KEY).parseClaimsJws(token);
        Claims claims = (Claims)jws.getBody();
        String strRoles = claims.get("roles", String.class);
        
        List<String> roles = Arrays.asList(strRoles.split(","));
        
        return roles;
    }
    
    private static String getToken(String tokenHeader){
        return tokenHeader.substring(AuthenticationConstant.SCHEME.length()).trim();
    }
    
    private static String issueToken(String login) {
    	
    	Date issueDate = new Date();
        
        // Get expireDate - 100 minutes
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTime(issueDate);
    	calendar.add(Calendar.MINUTE, 100);
        Date expireDate = calendar.getTime();
        
        //Get jwtToken
        String jwtToken = Jwts.builder()
        		.claim("roles", AuthenticationConstant.ROLE_USER)
                        .setSubject(login)
                        .setIssuer("http://sisob.toe.iaia.lcc.uma.es")
                        .setIssuedAt(issueDate)
                        .setExpiration(expireDate)
                        .signWith(SignatureAlgorithm.HS512, AuthenticationFilter.KEY)
                        .compact();
        return jwtToken;
    }
}
