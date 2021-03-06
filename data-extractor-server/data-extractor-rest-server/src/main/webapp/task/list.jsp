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
<!DOCTYPE HTML>
<%@page import="eu.sisob.uma.restserver.client.RESTClient"%>
<%@page import="eu.sisob.uma.restserver.client.UtilJsp"%>
<%@page import="eu.sisob.uma.restserver.services.communications.Task"%>
<%@page import="java.util.List"%>
<%@page import="javax.ws.rs.core.GenericType"%>

<%@page session="true"%>
<%
    if (!UtilJsp.validateSession(session)){
        response.sendRedirect(request.getContextPath()+"/index.jsp?message=notAllowed");
        return;
    }
    
    // API REST - get tasks
    String token = UtilJsp.getToken(session);
    RESTClient restClient = new RESTClient(token);
    List<Task> listTasks = (List<Task>)restClient.get("/tasks", new GenericType<List<Task>>(){}, null);
    
    // Save the result in the request
    request.setAttribute("listTasks", listTasks);
    
    // Save constant in the request
    request.setAttribute("TASK_STATUS_EXECUTED", Task.STATUS_EXECUTED);
    request.setAttribute("TASK_STATUS_EXECUTING", Task.STATUS_EXECUTING);
    request.setAttribute("TASK_STATUS_TO_EXECUTE", Task.STATUS_TO_EXECUTE);
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

        <div class="container">   

            <div class="well" id="tasks-list">

                <h4><fmt:message key="Jsp Welcome User Msg" bundle="${msg}"/></h4>
                <h5><fmt:message key="Jsp Tasks List Msg" bundle="${msg}"/></h4>
                <table class="table table-striped">
                    <tr>
                        <th>Task name</th>
                        <th>Task kind</th>
                        <th>Task status</th>                    
                        <th>Started</th>
                        <th>Finished</th>
                        <th>View</th>
                    </tr>

                    <c:if test="${listTasks != null}">
                        <c:forEach items="${listTasks}" var="iTask">

                            <c:choose>
                                <c:when test="${TASK_STATUS_EXECUTED == iTask.status}">
                                    <c:set var="css_status" scope="request" value="success"/>
                                </c:when>
                                <c:when test="${TASK_STATUS_TO_EXECUTE == iTask.status}">
                                    <c:set var="css_status" scope="request" value="info"/>
                                </c:when>
                                <c:when test="${TASK_STATUS_EXECUTING == iTask.status}">
                                    <c:set var="css_status" scope="request" value="warning"/>
                                </c:when>
                                <c:otherwise>
                                    <c:set var="css_status" scope="request" value=""/>
                                </c:otherwise>
                            </c:choose>

                            <tr class="${css_status}">
                                <td>${iTask.name}</td>
                                <td>${iTask.kind}</td>
                                <td>${iTask.status}</td>
                                <td>${iTask.date_started}</td>
                                <td>${iTask.date_finished}</td>
                                <td>
                                    <a class="btn btn-primary" 
                                       href="task/details.jsp?task_code=${iTask.name}">
                                        View
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:if>       
                </table>
            </div>        
            <div class="well" id="second-step">
                <p>Create a new task with the button.</p>
                <div>
                    <button type="submit" class="btn btn-primary" id="task-creator">
                        <i class="icon-upload icon-white input-append"></i>
                        <span>Create new task</span>
                    </button>       
                </div>
            </div>
            <div class="modal fade" id="test_modal">
                <div class="modal-header">
                    <a class="close" data-dismiss="modal">&times;</a>
                    <h3><fmt:message key="Jsp Popup Msg" bundle="${msg}"/></h3>
                </div>
                <div class="modal-body">
                    <div id="operation-result">                    
                    </div>
                </div>
                <div class="modal-footer">
                    <a href="#" class="btn" data-dismiss="modal">Close</a>            
                </div>
            </div>        
        </div>
                                
        <script type="text/javascript">    
            $(document).ready(function()
            {
                $("button#task-creator").click(function(){

                    $.ajax({ 
                        type: "POST",
                        url: "${pageContext.request.contextPath}/resources/tasks",
                        headers: sisob.security.getHeader(),   
                        contentType: 'application/json',                                                      
                        success: function(result){
                            var message = "A new task has been created successfully."
                            showModal("success", message);
                            setTimeout(function() {
                                window.location = 'task/details.jsp?task_code='+result.task_code;
                            }, 2000);
                        },
                        error: function(response){
                            showModal("error", response.responseText);
                        }
                    });
                });

                function showModal(pType, pMessage){
                    var htmlMessage = "<h4 class='text-"+pType+"'>" + pMessage + "</h4>";
                    $("div#operation-result").html(htmlMessage);
                    $('#test_modal').modal('show');
                }
            });
        </script>  
        
    </jsp:body>
</t:generic-template>
