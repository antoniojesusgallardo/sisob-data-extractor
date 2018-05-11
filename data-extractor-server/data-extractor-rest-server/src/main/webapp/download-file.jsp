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
<%@page import="eu.sisob.uma.restserver.client.UtilJsp"%>
<%@page import="java.io.File"%>
<%@page import="java.io.FileInputStream"%>
<%@page import="java.nio.file.Files"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="org.apache.commons.io.FileUtils"%>

<%@page session="true"%>
<%
    if (!UtilJsp.validateSession(session)){
        response.sendRedirect(request.getContextPath()+"/index.jsp?message=notAllowed");
        return;
    }
    
    String user = (String)session.getAttribute("user");
    String token = (String)session.getAttribute("token");

    //Validate task and file
    String taskCode = request.getParameter("task-code");
    String fileName = request.getParameter("file-name");
    String fileType = request.getParameter("file-type");
    
    if(taskCode==null || fileName==null){
        response.sendRedirect(request.getContextPath()+"/error.jsp");
        return;
    }
    
    if(fileType==null){
        fileType = "results";
    }
    
    if("source".equals(fileType)){
        fileType = "";
    }
    
    try{
        Map params = new HashMap();
        params.put("task_code", taskCode);
        params.put("file", fileName);
        params.put("type", fileType);
        RESTClient restClient = new RESTClient(token);
        File file = (File)restClient.get("/file/download", File.class, params);
        
        response.setContentType("text/csv");
        response.setHeader("Content-disposition", "attachment; filename=\""+fileName+"\"");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Expires", "-1");
        
        byte[] bytes = Files.readAllBytes(file.toPath());
        
        response.getOutputStream().write(bytes);
        response.getOutputStream().flush();
        response.getOutputStream().close();
    }
    catch(ApiErrorException ex){
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
