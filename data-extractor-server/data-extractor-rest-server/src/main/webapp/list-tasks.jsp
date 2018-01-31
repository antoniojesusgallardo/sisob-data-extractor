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
<%@page import="com.sun.jersey.core.util.MultivaluedMapImpl"%>
<%@page import="com.sun.jersey.api.client.Client"%>
<%@page import="com.sun.jersey.api.client.WebResource"%>
<%@page import="eu.sisob.uma.restserver.TheConfig"%>
<%@page import="eu.sisob.uma.restserver.services.communications.OutputTaskStatusList"%>
<%@page import="eu.sisob.uma.restserver.services.communications.OutputTaskStatus"%>
<%@page import="eu.sisob.uma.restserver.TheResourceBundle"%>
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
  
    Client client = Client.create();
    
    MultivaluedMap queryParams = new MultivaluedMapImpl();
    queryParams.add("user", user);
    queryParams.add("pass", pass);

    WebResource webResourceTasks = client.resource(TheConfig.getInstance().getString(TheConfig.SERVER_URL) + "/resources/tasks");
    OutputTaskStatusList task_status_list = webResourceTasks.queryParams(queryParams)
                                  .accept(MediaType.APPLICATION_JSON)
                                  .get(OutputTaskStatusList.class);
%>

<jsp:include page="header.jsp" >    
    <jsp:param name="showUserLogged" value="true" />
</jsp:include> 

<div class="container">   
            
    <div class="well" id="tasks-list">

        <h4><%=TheResourceBundle.getString("Jsp Welcome User Msg")%></h4>
        <h5><%=TheResourceBundle.getString("Jsp Tasks List Msg")%></h4>
        <table class="table table-striped">
            <tr>
                <th>Task name</th>
                <th>Task kind</th>
                <th>Task status</th>                    
                <th>Started</th>
                <th>Finished</th>
                <th>View</th>
            </tr>
        <%
        if(task_status_list.task_status_list != null)
        for(OutputTaskStatus task_status : task_status_list.task_status_list){
            
            String css_style_by_status = "";
            if(task_status.status.equals(OutputTaskStatus.TASK_STATUS_EXECUTED))
                css_style_by_status = "success";
            else if(task_status.status.equals(OutputTaskStatus.TASK_STATUS_TO_EXECUTE))
                css_style_by_status = "info";
            else if(task_status.status.equals(OutputTaskStatus.TASK_STATUS_EXECUTING))
                css_style_by_status = "warning";                
        %>
            <tr class="<%=css_style_by_status%>">
                <td><%=task_status.name%></td>
                <td><%=task_status.kind%></td>
                <td><%=task_status.status%></td>
                <td><%=task_status.date_started%></td>
                <td><%=task_status.date_finished%></td>
                <td>
                    <a class="btn btn-primary" 
                       href="upload-and-launch.jsp?task_code=<%=task_status.name%>">
                        View
                    </a>
                </td>
            </tr>                
        <%
        }
        %>
        </table>
    </div>        
    <div class="well" id="second-step">
        <p>Create a new task with the button.</p>     
        <div>
            <div>
                <button type="submit" class="btn btn-primary" id="task-creator">
                    <i class="icon-upload icon-white input-append"></i>
                    <span>Create new task</span>
                </button>       
            </div>                
        </div>
    </div>
    <div class="modal fade" id="test_modal">
        <div class="modal-header">
            <a class="close" data-dismiss="modal">&times;</a>
            <h3><%=TheResourceBundle.getString("Jsp Popup Msg")%></h3>
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
            
<jsp:include page="footer.jsp" />

<script type="text/javascript">    
    $(document).ready(function()
    {
        function showModal(pType, pMessage){
            var htmlMessage = "<h4 class='text-"+pType+"'>" + pMessage + "</h4>";
            $("div#task-result").html(htmlMessage);
            $('#test_modal').modal('show');
        }
        
        $("button#task-creator").click(function(){
            var data = {
                user: "<%=user%>", 
                pass: "<%=pass%>"
            }

            $.ajax({ 
                type: "POST",
                url: "resources/task/add",
                data: JSON.stringify(data),
                dataType: "json",         
                contentType: 'application/json',                                                      
                success: function(result){                        
                    if(result.status == "<%=OutputTaskStatus.TASK_STATUS_TO_EXECUTE%>"){
                        showModal("success", result.message);
                        setTimeout(function() {
                            window.location = 'upload-and-launch.jsp?task_code=' + result.task_code;
                        }, 2000);
                    }
                    else{
                        showModal("warning", result.message);
                    }                        
                },
                error: function(xml,result){
                    var messageError =  '<%=TheResourceBundle.getString("Jsp Was Error")%> '+
                                    '<%=TheResourceBundle.getString("Jsp Contact To Admin")%>';
                    showModal("error", messageError);
                }
            });
        });
    });
    </script>  
