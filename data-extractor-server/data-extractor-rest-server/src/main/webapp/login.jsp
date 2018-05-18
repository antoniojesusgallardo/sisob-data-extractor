<%-- 
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
--%>

<%--
    Author: Antonio Jesus Gallardo Albarran - antonio.jesus.gallardo@gmail.com
--%>

<!DOCTYPE HTML>
<%@page import="eu.sisob.uma.restserver.client.ApiErrorException"%>
<%@page import="eu.sisob.uma.restserver.client.RESTClient"%>
<%@page import="eu.sisob.uma.restserver.restservices.security.AuthenticationUtils"%>
<%@page import="eu.sisob.uma.restserver.services.communications.Authorization"%>
<%@page import="eu.sisob.uma.restserver.services.communications.User"%>
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
  
    RESTClient restClient = new RESTClient(null);
    
    String urlRedirect = "";
    
    try{
        Authorization auth = new Authorization();
        auth.setUser(user);
        auth.setPass(pass);
        Response authResponse = restClient.postResponse("/authorization", auth);
        
        User userAuth = authResponse.readEntity(User.class);
        
        if(User.ACCOUNT_TYPE_USER.equals(userAuth.getAccount_type())){
            
            List authorization = authResponse
                                    .getHeaders()
                                    .get(AuthenticationUtils.AUTHORIZATION_PROPERTY);
            
            if(authorization==null || authorization.isEmpty()){
                throw new Exception();
            }
            String token = (String)authorization.get(0);
            
            session.setAttribute("user", userAuth.getUsername());
            session.setAttribute("token", token);
        }
        else if(User.ACCOUNT_TYPE_APP.equals(userAuth.getAccount_type())){
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
    
    if(!urlRedirect.isEmpty()){
        response.sendRedirect(request.getContextPath() + urlRedirect);
    }
    
%>

<script type="text/javascript">
    
    localStorage.wsToken = '${sessionScope.token}';
    
    window.location = 'task/list.jsp';

</script>