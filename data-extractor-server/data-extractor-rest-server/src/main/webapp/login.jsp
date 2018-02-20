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
<%@page import="eu.sisob.uma.restserver.RESTClient"%>
<%@page import="eu.sisob.uma.restserver.services.communications.OutputAuthorizationResult"%>
<%@page import="eu.sisob.uma.restserver.TheConfig"%>
<%@page import="java.security.MessageDigest"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>

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
    RESTClient restClient = new RESTClient("/authorization", OutputAuthorizationResult.class, params);
    OutputAuthorizationResult auth_result = (OutputAuthorizationResult)restClient.get();
    
    // Redirect
    String urlRedirect = request.getContextPath();
    if(!auth_result.success){
        urlRedirect += "/index.jsp?message=unauth_data"; 
    }
    else if(!auth_result.account_type.equals(OutputAuthorizationResult.ACCOUNT_TYPE_USER)){
        urlRedirect += "/index.jsp?message=unauth_type"; 
    }
    else {
        session.setAttribute("user",user);
        session.setAttribute("pass",pass);

        urlRedirect += "/task/list.jsp"; 
    }
    response.sendRedirect(urlRedirect);
%>