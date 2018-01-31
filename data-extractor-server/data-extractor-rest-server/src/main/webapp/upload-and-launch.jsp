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
<%@page import="eu.sisob.uma.restserver.services.communications.OutputTaskStatus"%>
<%@page import="eu.sisob.uma.restserver.TheConfig"%>
<%@page import="eu.sisob.uma.restserver.TheResourceBundle"%>
<%@page import="com.sun.jersey.api.client.Client"%>
<%@page import="com.sun.jersey.api.client.WebResource"%>
<%@page import="com.sun.jersey.core.util.MultivaluedMapImpl"%>
<%@page import="javax.ws.rs.core.MediaType"%>
<%@page import="javax.ws.rs.core.MultivaluedMap"%>

<%@page session="true"%>
<%  
    if( session == null || 
        session.getAttribute("user")==null ||
        session.getAttribute("pass")==null ){
        if(session != null){
            session.invalidate();
        }
        response.sendRedirect("index.jsp?message=notAllowed");
        return;
    }

    String user = (String)session.getAttribute("user");
    String pass = (String)session.getAttribute("pass");
    
    String task_code = request.getParameter("task_code");
  
    String status;
    String message = "";
    String reason = "";
    String feedback = "";
    String result = "";
    String source = "";
    String verbose = "";  
    String errors = "";
    String reason_type = "";
    String task_kind = "";
    String params = "";

    Client client = Client.create();
    
    WebResource webResource = client.resource(TheConfig.getInstance().getString(TheConfig.SERVER_URL) + "/resources/task");  

    MultivaluedMap queryParams = new MultivaluedMapImpl();
    queryParams.add("user", user);
    queryParams.add("pass", pass);
    queryParams.add("task_code", task_code);

    OutputTaskStatus r = webResource.queryParams(queryParams)
                                    .accept(MediaType.APPLICATION_JSON)
                                    .get(OutputTaskStatus.class);
    status = r.status;
    message = r.message;      

    if(status.equals(OutputTaskStatus.TASK_STATUS_EXECUTED)){
        reason = TheResourceBundle.getString("Jsp Auth Msg");
        reason_type = "success";   
        result = r.result;
        source = r.source;
        verbose = r.verbose;
        task_kind = r.kind;
        feedback = r.feedback;
        errors = r.errors;
        params = r.params; 
    }
    else if(status.equals(OutputTaskStatus.TASK_STATUS_NO_AUTH) || 
            status.equals(OutputTaskStatus.TASK_STATUS_NO_ACCESS)){
        reason_type = "error";
        reason = TheResourceBundle.getString("Jsp No Access Msg");
    }
    else if(status.equals(OutputTaskStatus.TASK_STATUS_EXECUTING)){
        reason_type = "success";
        reason = TheResourceBundle.getString("Jsp Auth Msg");
    }
    else if(status.equals(OutputTaskStatus.TASK_STATUS_TO_EXECUTE)){
        reason_type = "success";
        reason = TheResourceBundle.getString("Jsp Auth Msg");
    }
%>
<!DOCTYPE HTML>
<jsp:include page="header.jsp" >
    <jsp:param name="showUserLogged" value="true" />
</jsp:include>

<div class="container"> 
    <h5>
        <a href="list-tasks.jsp">Back to listing</a>
    </h5>
</div>  

<div class="container">    
    <%    
    if( status.equals(OutputTaskStatus.TASK_STATUS_NO_AUTH) || 
        status.equals(OutputTaskStatus.TASK_STATUS_NO_ACCESS) || 
        status.equals(OutputTaskStatus.TASK_STATUS_EXECUTING)){
    %>
        <jsp:include page="upload-and-launch-executing.jsp" >
            <jsp:param name="message" value="<%=message%>" />
        </jsp:include>        
    <%    
    }            
    else if(status.equals(OutputTaskStatus.TASK_STATUS_EXECUTED)){
    %>
        <jsp:include page="upload-and-launch-executed.jsp" >
            <jsp:param name="task_code" value="<%=task_code%>" />
            <jsp:param name="task_kind" value="<%=task_kind%>" />
            <jsp:param name="reason_type" value="<%=reason_type%>" />
            <jsp:param name="reason" value="<%=reason%>" />
            <jsp:param name="result" value="<%=result%>" />            
            <jsp:param name="source" value="<%=source%>" />
            <jsp:param name="verbose" value="<%=verbose%>" />
            <jsp:param name="feedback" value="<%=feedback%>" />
            <jsp:param name="errors" value="<%=errors%>" />
            <jsp:param name="message" value="<%=message%>" />
            <jsp:param name="params" value="<%=params%>" />
        </jsp:include>        
    <%
    }
    else if(status.equals(OutputTaskStatus.TASK_STATUS_TO_EXECUTE)){
    %>
        <jsp:include page="upload-and-launch-to-execute.jsp" >
            <jsp:param name="task_code" value="<%=task_code%>" />
            <jsp:param name="reason_type" value="<%=reason_type%>" />
            <jsp:param name="reason" value="<%=reason%>" />                        
            <jsp:param name="message" value="<%=message%>" />
        </jsp:include>
    <%
    }
    %>
</div>
    
<jsp:include page="footer.jsp" />
