<%-- 
    Copyright (c) 2014 "(IA)2 Research Group. Universidad de M�laga"
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
--%>

<%--
    Author: Antonio Jesus Gallardo Albarran - antonio.jesus.gallardo@gmail.com
--%>

<!DOCTYPE HTML>
<%@page import="eu.sisob.uma.restserver.client.ApiErrorException"%>
<%@page import="eu.sisob.uma.restserver.client.RESTClient"%>
<%@page import="eu.sisob.uma.restserver.restservices.security.AuthenticationUtils"%>
<%@page import="eu.sisob.uma.restserver.services.communications.OutputAuthorizationResult"%>
<%@page import="eu.sisob.uma.restserver.TheConfig"%>
<%@page import="java.security.MessageDigest"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="javax.ws.rs.core.Response;"%>

<%@page session="true"%>
<%
    String user = request.getParameter("user");  
    String pass = request.getParameter("pass");  
  
    // Convert password to SHA-256
    MessageDigest md = MessageDigest.getInstance("SHA-256");
    md.update(pass.getBytes());

    byte byteData[] = md.digest();
    //convert the byte to hex format method 1
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < byteData.length; i++) {
      sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
    }
    pass = sb.toString();
    // END - Convert password to SHA-256

    // API REST - Authorization
    Map params = new HashMap();
    params.put("user", user);
    params.put("pass", pass);
    RESTClient restClient = new RESTClient(null);
    
    String urlRedirect = "";
    
    try{
        Response authResponse = restClient.getResponse("/authorization", OutputAuthorizationResult.class, params);
        
        OutputAuthorizationResult authResult = authResponse.readEntity(OutputAuthorizationResult.class);
        
        if(OutputAuthorizationResult.ACCOUNT_TYPE_USER.equals(authResult.account_type)){
            
            List authorization = authResponse
                                    .getHeaders()
                                    .get(AuthenticationUtils.AUTHORIZATION_PROPERTY);
            
            if(authorization==null || authorization.isEmpty()){
                throw new Exception();
            }
            String token = (String)authorization.get(0);
            
            session.setAttribute("user", user);
            session.setAttribute("token", token);

            urlRedirect += "/task/list.jsp";
        }
        else if(OutputAuthorizationResult.ACCOUNT_TYPE_APP.equals(authResult.account_type)){
            urlRedirect += "/index.jsp?message=unauth_type";
        }
        else{
            urlRedirect += "/index.jsp?message=error";
        };  
    }
    catch(ApiErrorException ex){
        if(ex.getStatus() == Response.Status.UNAUTHORIZED.getStatusCode()){
            urlRedirect += "/index.jsp?message=unauth_data";
        }
        else{
            urlRedirect += "/index.jsp?message=error";
        }
    }
    catch(Exception ex){
        urlRedirect += "/index.jsp?message=error";
    }
    
    response.sendRedirect(request.getContextPath() + urlRedirect);
%>