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
<%@page import="eu.sisob.uma.restserver.managers.TaskFileManager"%>
<%@page import="eu.sisob.uma.restserver.managers.RestUriManager"%>
<%@page import="java.io.File"%>

<%@page session="true"%>
<%
    // Validate the session
    if( session == null || 
        session.getAttribute("user")==null ||
        session.getAttribute("pass")==null ){
        if(session != null){
            session.invalidate();
        }
        response.sendRedirect(request.getContextPath()+"/index.jsp?message=notAllowed");
        return;
    }
    
    String user = (String)session.getAttribute("user");
    String pass = (String)session.getAttribute("pass");

    //Validate task and file
    String taskCode = request.getParameter("task-code");
    String fileName = request.getParameter("file-name");
    
    if(taskCode==null || fileName==null){
        response.sendRedirect(request.getContextPath()+"/error.jsp");
        return;
    }
    
    File file = TaskFileManager.getFile(user, taskCode, fileName, "results");
    if(file != null){
        String strUrl= RestUriManager.getUriFileToDownload(user, pass, taskCode, fileName, "results");
        response.sendRedirect(strUrl);
        return;
    }
    else{
        // File not found
        request.setAttribute("fileFound", false);
    }
%>


<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<fmt:setBundle basename="Bundle" var="msg"/>

<t:generic-template>
    <jsp:attribute name="resources">
        <jsp:include page="layout/resources.jsp" />
    </jsp:attribute>
    <jsp:attribute name="header">
        <jsp:include page="layout/header.jsp" >  
            <jsp:param name="showUserLogged" value="true" />
        </jsp:include>
    </jsp:attribute>
    <jsp:attribute name="footer">
        <jsp:include page="layout/footer.jsp" />
    </jsp:attribute>
    <jsp:body>
        
        <c:if test="${fileFound != null && fileFound==false }">
            <div class="well" style="text-align: center">
                <h4>
                    <fmt:message key="dowload_file_not_found" bundle="${msg}"/>
                </h4>
                <br/>
                <h4>
                    <a href="index.jsp">
                        <fmt:message key="Jsp Error Go Index" bundle="${msg}"/>
                    </a>
                </h4>
            </div>
        </c:if>
        
    </jsp:body>
</t:generic-template>