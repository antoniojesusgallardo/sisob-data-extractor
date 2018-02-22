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
<!DOCTYPE HTML>
<%@page import="eu.sisob.uma.restserver.services.communications.OutputTaskStatus"%>
<%@page import="eu.sisob.uma.restserver.RESTClient"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>

<%@page session="true"%>
<%  
    // Validate data
    if( session == null || 
        session.getAttribute("user")==null ||
        session.getAttribute("pass")==null ){
        if(session != null){
            session.invalidate();
        }
        response.sendRedirect("index.jsp?message=notAllowed");
        return;
    }

    // Input Data
    String user = (String)session.getAttribute("user");
    String pass = (String)session.getAttribute("pass");
    String task_code = request.getParameter("task_code");
    
    // Get Task data from the API REST
    Map params = new HashMap();
    params.put("user", user);
    params.put("pass", pass);
    params.put("task_code", task_code);
    RESTClient restClient = new RESTClient("/task", OutputTaskStatus.class, params);
    OutputTaskStatus task = (OutputTaskStatus)restClient.get();
    
    // Save the result in the request
    request.setAttribute("task", task);
    
    // Save constant in the request
    request.setAttribute("TASK_STATUS_EXECUTED", OutputTaskStatus.TASK_STATUS_EXECUTED);
    request.setAttribute("TASK_STATUS_EXECUTING", OutputTaskStatus.TASK_STATUS_EXECUTING);
    request.setAttribute("TASK_STATUS_TO_EXECUTE", OutputTaskStatus.TASK_STATUS_TO_EXECUTE);
    request.setAttribute("TASK_STATUS_NO_AUTH", OutputTaskStatus.TASK_STATUS_NO_AUTH);
    request.setAttribute("TASK_STATUS_NO_ACCESS", OutputTaskStatus.TASK_STATUS_NO_ACCESS);
%>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<fmt:setBundle basename="Bundle" var="msg"/>

<t:generic-template>
    <jsp:attribute name="resources">
        <jsp:include page="../layout/resources.jsp" />
    </jsp:attribute>
    <jsp:attribute name="header">
        <jsp:include page="../layout/header.jsp" >  
            <jsp:param name="showUserLogged" value="true" />
        </jsp:include>
    </jsp:attribute>
    <jsp:attribute name="footer">
        <jsp:include page="../layout/footer.jsp" />
    </jsp:attribute>
    <jsp:body>

        <h5>
            <a href="task/list.jsp">Back to listing</a>
        </h5>
        
        <c:choose>
            <c:when test="${TASK_STATUS_NO_AUTH==task.status || TASK_STATUS_NO_ACCESS==task.status || TASK_STATUS_EXECUTING==task.status}">
                <jsp:include page="details-executing.jsp" />
            </c:when>
            <c:when test="${TASK_STATUS_EXECUTED == task.status}">
                <jsp:include page="details-executed.jsp" />
            </c:when>
            <c:when test="${TASK_STATUS_TO_EXECUTE == task.status}">
                <jsp:include page="details-to-execute.jsp" />
            </c:when>
        </c:choose>
 
    </jsp:body>
</t:generic-template>
