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
<%@page import="eu.sisob.uma.restserver.client.Constant"%>
<%@page import="eu.sisob.uma.restserver.client.RESTUri"%>
<%@page import="eu.sisob.uma.restserver.client.UtilJsp"%>

<%@page session="true"%>
<%
    if (!UtilJsp.validateSession(session)){
        response.sendRedirect(request.getContextPath()+"/index.jsp?message=notAllowed");
        return;
    }
    
    //Validate task and file
    String taskCode = request.getParameter("task-code");
    String fileName = request.getParameter("file-name");
    String fileType = request.getParameter("file-type");
    
    if(taskCode==null || fileName==null){
        response.sendRedirect(request.getContextPath()+"/error.jsp");
        return;
    }
    
    if(fileType==null){
        fileType = Constant.FILE_TYPE_RESULT;
    }
    
    String urlJson = RESTUri.getUriFile(taskCode, fileName, fileType);
    request.setAttribute("urlJson", urlJson);
    request.setAttribute("taskCode", taskCode);
    request.setAttribute("fileName", fileName);
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
        
        <div class="well">
            <h4 style="text-align: center">
                File: ${fileName}
                | 
                <a href='download-file.jsp?task-code=${taskCode}&file-name=${fileName}'
                   target='_blank'>
                    Download
                </a>  
            </h4>
            <div id="contentFile" style="word-wrap: break-word;"></div>
        </div>
        
        <script>
            var urlJson = '${urlJson}';
            
            $.ajax({ 
                type: "GET",
                url: urlJson,
                headers: security.getHeader(),                         
                success: function(result){
                    document.getElementById("contentFile").innerHTML = result;
                },
                error: function(response){
                    console.log(response);
                    var text = "Error - File not found";
                    document.getElementById("contentFile").innerHTML = text;
                }
            });

        </script>
        
    </jsp:body> 
</t:generic-template>

