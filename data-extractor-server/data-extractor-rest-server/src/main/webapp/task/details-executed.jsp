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
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@page import="eu.sisob.uma.restserver.client.Constant"%>
<%@page import="eu.sisob.uma.restserver.client.UtilJsp"%>


<%@page session="true"%>
<%
    // Validate data
    if (!UtilJsp.validateSession(session)){
        response.sendRedirect(request.getContextPath()+"/index.jsp?message=notAllowed");
        return;
    }
    if(request.getAttribute("task") == null){
        response.sendRedirect(request.getContextPath()+"/error.jsp");
        return;
    }
     
    request.setAttribute("GateTaskCH", Constant.TASK_NAME_CH);
    request.setAttribute("FileType_Source", Constant.FILE_TYPE_SOURCE);
    request.setAttribute("FileType_Verbose", Constant.FILE_TYPE_VERBOSE);
%>

<fmt:setBundle basename="Bundle" var="msg"/>
    
<div class="well" id="instructions">
    <h4>${task.message}</h4>
</div>

<div class="well" id="instructions">
    
    <c:if test="${GateTaskCH == task.kind}">
        <div style="text-align: center">
            <a class="btn btn-primary" 
               href="task/euParliament/visualizations.jsp?task_code=${task.task_code}">
                <fmt:message key="Jsp_euParliament_show_visualizations" bundle="${msg}"/>
            </a>
        </div>
    </c:if>
    
    <h4>The results files of the task:</h4>    
    <blockquote>
        <h5>Files to download:</h5>
        <c:if test="${task.results != null}">
            <c:forEach items="${task.results}" var="iResult">
                <p style='font-size:14px; margin-bottom: 10px;' >
                    ${iResult}
                    <b>  
                        | 
                        <a href='download-file.jsp?task-code=${task.task_code}&file-name=${iResult}'
                           target='_blank'>
                            Download
                        </a>   
                        | 
                        <a href='view-file.jsp?task-code=${task.task_code}&file-name=${iResult}'
                           target='_blank'>
                            Show
                        </a>
                    </b>
                </p>
            </c:forEach>
        </c:if>
    </blockquote>
    
    <c:if test="${task.params != null && not empty task.params }">
        <h5>Parameters of the task</h5>
        <blockquote>
            <c:forEach items="${task.params}" var="iParam">
                <strong>${iParam.key}</strong> => ${iParam.value}<br>
            </c:forEach>
        </blockquote>
    </c:if>
    
    <h5>Notes of the files to download:</h5>        
    <jsp:include page="get-results-desc.jsp" >
        <jsp:param name="taskKind" value="${task.kind}" />    
    </jsp:include>  
    
    <c:if test="${GateTaskCH != task.kind}">
        
        <h4>Feedback:</h4>
        <c:choose>
            <c:when test="${task.feedback != ''}">
                <blockquote>                                  
                    Here you can find a Google Docs document built to give a 
                    feedback of the process. In the document the user can add 
                    the information not found in the extraction task filling the 
                    cells according to the template given. This results will to 
                    help to the system to improve the accurate.
                    <br>
                    You can fill the feedback using this link: 
                    <a href="${task.feedback}">Feedback document</a>            
                </blockquote>
                <h4>Or you can edit and view here:</h4>
                <blockquote>
                    <iframe src="${task.feedback}" height="500" width="90%"></iframe>
                </blockquote>
            </c:when>
            <c:otherwise>
                <blockquote>
                    This task has not document feedback document (ask to administrator for this).
                </blockquote>
            </c:otherwise>
        </c:choose>
    </c:if>

    <c:if test="${GateTaskCH != task.kind}">
        <c:if test="${task.errors != null && task.errors!=''}">
            <h4 class="text-error">Errors obtained in the task (please, report to the administrator):</h4>
            <blockquote>
                ${task.errors}
            </blockquote>
        </c:if>
    </c:if>
        
    <h4>Relaunch the task (Relaunch the task with same source data)</h4>
    <blockquote>    
        <button type="submit" class="btn btn-primary" id="task-launcher">
            <i class="icon-upload icon-white input-append"></i>
            <span>Relaunch the task</span>            
        </button>        
    </blockquote>
    
    <c:if test="${task.verbose != null && not empty task.verbose }">
        <h4>The verbose files generated in the task:</h4>
        <blockquote>
            <c:forEach items="${task.verbose}" var="iVerbose">
                <a href='download-file.jsp?task-code=${task.task_code}&file-name=${iVerbose}&file-type=${FileType_Verbose}'
                    target='_blank'>
                    ${iVerbose}
                </a><br><br>
            </c:forEach>
        </blockquote>
    </c:if>
        
    <h4>Delete the task</h4>
    <blockquote>    
        <button type="submit" class="btn btn-danger" id="task-deleter">
            <i class="icon-upload icon-white input-append"></i>
            <span>Delete the task</span>            
        </button>        
    </blockquote>   
    
    <c:if test="${task.source != null && not empty task.source }">
        <h4>The sources used in the task:</h4>
        <blockquote>
            <c:forEach items="${task.source}" var="iSource">
                <a href='download-file.jsp?task-code=${task.task_code}&file-name=${iSource}&file-type=${FileType_Source}'
                    target='_blank' >
                    ${iSource}
                </a><br><br>
            </c:forEach>
        </blockquote>
    </c:if>
</div>
    
<div class="modal fade" id="test_modal">
    <div class="modal-header">
        <a class="close" data-dismiss="modal">&times;</a>
        <h3><fmt:message key="Jsp Popup Msg" bundle="${msg}"/></h3>
    </div>
    <div class="modal-body">
        <div id="task-result"> 
        </div>
    </div>
    <div class="modal-footer">
        <a href="#" class="btn" data-dismiss="modal">Close</a>
    </div>
</div> 
    
<script type="text/javascript">    
$(document).ready(function(){
    
    var urlBase = "${pageContext.request.contextPath}/resources/";
    
    var task_code = "${task.task_code}";
    var task_kind = "${task.kind}";
    
    $("button#task-launcher").click(function()
    {   
        var securityHeader = {};
        securityHeader[security.AUTHORIZATION_PROPERTY] = '${sessionScope.token}';
        
        $.ajax({ 
            type: "POST",
            url: urlBase + "tasks/" + task_code + "/relaunch",
            headers: securityHeader,
            contentType: 'application/json',
            success: function(result) { 
                showModal("success", "("+task_kind+")  "+result);
                setTimeout(function() {
                    window.location = 'task/list.jsp';
                }, 2000);
            },
            error: function(xml){
                showModal("error", xml.responseText);
            }
        });  
    }); 
        
    $("button#task-deleter").click(function(){
        
        var securityHeader = {};
        securityHeader[security.AUTHORIZATION_PROPERTY] = '${sessionScope.token}';

        $.ajax({ 
            type: "DELETE",
            url: urlBase + "tasks/"+task_code,
            headers: securityHeader,
            contentType: 'application/json',
            success: function(result){
                showModal("success", "("+task_kind+")  "+result);
                setTimeout(function() {
                    window.location = 'task/list.jsp';
                }, 2000);
            },
            error: function(response){
                console.log(response);
                showModal("error", response.responseText);
            }
        });
    });
    
    function showModal(pType, pMessage){
        var htmlMessage = "<h4 class='text-"+pType+"'>" + pMessage + "</h4>";
        $("div#task-result").html(htmlMessage);
        $('#test_modal').modal('show');
    }
});
</script>     