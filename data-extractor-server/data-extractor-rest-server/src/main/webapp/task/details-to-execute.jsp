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

<%@page import="eu.sisob.uma.restserver.client.Constant"%>
<%@page import="eu.sisob.uma.restserver.client.RESTUri"%>
<%@page import="eu.sisob.uma.restserver.client.UtilJsp"%>
<%@page import="eu.sisob.uma.restserver.services.communications.Task"%>
<%@page import="eu.sisob.uma.restserver.TheConfig"%>
<%@page import="eu.sisob.uma.restserver.TheResourceBundle"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>

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
    
    List<String[]> taskTypes = new ArrayList();
    String[] none = {"none", TheResourceBundle.getString("Jsp Select Task Msg")};  
    taskTypes.add(none);

    if(TheConfig.getInstance().getString(TheConfig.SERVICES_CRAWLER).equals("enabled")) {   
        String[] crawler = {"crawler", TheResourceBundle.getString("Task Crawler Title")};  
        taskTypes.add(crawler);              
    }
    if(TheConfig.getInstance().getString(TheConfig.SERVICES_GATE).equals("enabled")) {   
        String[] crawler = {"gate", TheResourceBundle.getString("Task Gate Title")};  
        taskTypes.add(crawler);              
        
        String[] gateTaskCH = {Constant.TASK_NAME_CH, Constant.TASK_NAME_CH};  
        taskTypes.add(gateTaskCH);
    }
    if(TheConfig.getInstance().getString(TheConfig.SERVICES_INTERNAL_CV_FILES).equals("enabled")) {   
        String[] internalcvfiles = {"internalcvfiles", TheResourceBundle.getString("Task Internal CV Files Title")};  
        taskTypes.add(internalcvfiles);              
    }
    if(TheConfig.getInstance().getString(TheConfig.SERVICES_WEBSEARCHER).equals("enabled")) {   
        String[] websearcher = {"websearcher", TheResourceBundle.getString("Task WebSearcher Title")};  
        taskTypes.add(websearcher);              
        String[] websearcher_cv = {"websearcher_cv", TheResourceBundle.getString("Task WebSearcher CV Title")};  
        taskTypes.add(websearcher_cv);
    }
    if(TheConfig.getInstance().getString(TheConfig.SERVICES_EMAIL).equals("enabled")) {   
        String[] email = {"email", TheResourceBundle.getString("Task Email Title")};  
        taskTypes.add(email);              
    }
    
    Task task = (Task)request.getAttribute("task");
    String urlBaseToDelete = RESTUri.getUriFile(task.getTask_code(), "", "");
    
    request.setAttribute("taskTypes", taskTypes);
    request.setAttribute("urlBaseToDelete", urlBaseToDelete);
    request.setAttribute("task_code", task.getTask_code());
%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:setBundle basename="Bundle" var="msg"/>

<div class="well" id="task-selection">
    <h4>${task.message}</h4>
    <h5><fmt:message key="Jsp Select Task Msg" bundle="${msg}"/></h5>
    <select class="chzn-select" id="task-selector">                
        <c:forEach items="${taskTypes}" var="taskType">
            <option value="${taskType[0]}">${taskType[1]}</option>
        </c:forEach>
    </select>
</div>
    
<div class="well" id="instructions">            
</div>
    
<div class="well" id="first-step">
    <blockquote>   
        <h4><fmt:message key="Jsp First Step" bundle="${msg}"/></h4>
            <fmt:message key="Jsp File Uploads Inst" bundle="${msg}"/>
    </blockquote>    
    <!-- The file upload form used as target for the file upload widget -->
    <form id="fileupload" action="resources/file" method="POST" enctype="multipart/form-data">
        <!-- The fileupload-buttonbar contains buttons to add/delete files and start/cancel the upload -->            
                <input type="hidden" value="${task.task_code}" name="task_code" />
        <div class="row fileupload-buttonbar">
            <div class="span7">
                    (need to update)
                <!-- The fileinput-button span is used to style the file input field as button -->
                <span class="btn btn-success fileinput-button">
                    <i class="icon-plus icon-white"></i>
                    <span>Add files...</span>
                    <input type="file" name="files[]" multiple>
                </span>
            </div>
            <!-- The global progress information -->
            <div class="span5 fileupload-progress fade">
                <!-- The global progress bar -->
                <div class="progress progress-success progress-striped active" role="progressbar" aria-valuemin="0" aria-valuemax="100">
                    <div class="bar" style="width:0%;"></div>
                </div>
                <!-- The extended global progress information -->
                <div class="progress-extended">&nbsp;</div>
            </div>
        </div>
        <!-- The loading indicator is shown during file processing -->
        <div class="fileupload-loading"></div>
        <br>
        <!-- The table listing the files available for upload/download -->
        <table role="presentation" class="table table-striped"><tbody class="files" data-toggle="modal-gallery" data-target="#modal-gallery"></tbody></table>
    </form>   
</div>  
        
<div class="well" id="second-step">
    <blockquote>   
        <h4><fmt:message key="Jsp Second Step" bundle="${msg}"/></h4>
    </blockquote> 
    <div id="task-launch-result">        
    </div>            
    <p>Launch the task with the button.</p>     
    <div>
        <button type="submit" class="btn btn-primary" id="task-launcher">
            <i class="icon-upload icon-white input-append"></i>
            <span>Launch the task</span>
        </button>            
    </div>            
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
  
<!-- modal-gallery is the modal dialog used for the image gallery -->
<div id="modal-gallery" class="modal modal-gallery hide fade" data-filter=":odd">
    <div class="modal-header">
        <a class="close" data-dismiss="modal">&times;</a>
        <h3 class="modal-title"></h3>
    </div>
    <div class="modal-body"><div class="modal-image"></div></div>
    <div class="modal-footer">
        <a class="btn modal-download" target="_blank">
            <i class="icon-download"></i>
            <span>Download</span>
        </a>
        <a class="btn btn-success modal-play modal-slideshow" data-slideshow="5000">
            <i class="icon-play icon-white"></i>
            <span>Slideshow</span>
        </a>
        <a class="btn btn-info modal-prev">
            <i class="icon-arrow-left icon-white"></i>
            <span>Previous</span>
        </a>
        <a class="btn btn-primary modal-next">
            <span>Next</span>
            <i class="icon-arrow-right icon-white"></i>
        </a>
    </div>
</div>
<!-- The template to display files available for upload -->
<script id="template-upload" type="text/x-tmpl">
{% for (var i=0, file; file=o.files[i]; i++) { %}
    <tr class="template-upload fade">
        <td class="name">
            <span>{%=file.name%}</span>
        </td>
        <td class="size">
            <span>{%=o.formatFileSize(file.size)%}</span>
        </td>
        {% if (file.error) { %}
            <td class="error" colspan="2">
                <span class="label label-important">
                    {%=locale.fileupload.error%}
                </span>
                {%=locale.fileupload.errors[file.error] || file.error%}
            </td>
        {% } else if (o.files.valid && !i) { %}
            <td>
                <div class="progress progress-success progress-striped active" 
                        role="progressbar" aria-valuemin="0" aria-valuemax="100" 
                        aria-valuenow="0">
                    <div class="bar" style="width:0%;">
                    </div>
                </div>
            </td>
            <td class="start">{% if (!o.options.autoUpload) { %}
                <button class="btn btn-primary">
                    <i class="icon-upload icon-white"></i>
                    <span>{%=locale.fileupload.start%}</span>
                </button>
            {% } %}</td>
        {% } else { %}
            <td colspan="2"></td>
        {% } %}
        <td class="cancel">{% if (!i) { %}
            <button class="btn btn-warning">
                <i class="icon-ban-circle icon-white"></i>
                <span>{%=locale.fileupload.cancel%}</span>
            </button>
        {% } %}</td>
    </tr>
{% } %}
</script>
<!-- The template to display files available for download -->
<script id="template-download" type="text/x-tmpl">   
{% for (var i=0, file; file=o.files[i]; i++) { %}
    <tr class="template-download fade">
        {% if (file.error) { %}
            <td></td>
            <td class="name">
                <span>{%=file.name%}</span>
            </td>
            <td class="size">
                <span>{%=o.formatFileSize(file.size)%}</span>
            </td>
            <td class="error" colspan="2">
                <span class="label label-important">{%=locale.fileupload.error%}</span>
                {%=locale.fileupload.errors[file.error] || file.error%}
            </td>
        {% } else { %}
            <td class="name">
                <a href="download-file.jsp?task-code=${task_code}&file-name={%=file.name%}&file-type=source" 
                    title="{%=file.name%}" 
                    rel="" 
                    download="{%=file.name%}">
                    {%=file.name%}
                </a>
            </td>
            <td class="size">
                <span>{%=o.formatFileSize(file.size)%}</span>
            </td>
            <td colspan="2"></td>
        {% } %}
        <td class="delete">
            <button class="btn btn-danger" data-type="DELETE" 
                data-url="${urlBaseToDelete}{%=file.name%}">
                <i class="icon-trash icon-white"></i>
                <span>{%=locale.fileupload.destroy%}</span>
            </button>
        </td>
    </tr>
{% } %}
</script>

<script type="text/javascript"> 
    
$(document).ready(function()
{   
    $("select#task-selector").change(function(){

        var taskKind = $("select#task-selector").val();
        $.ajax({
            type: "GET",
            url: "task/get-desc.jsp",
            dataType: 'text',
            data: "taskKind=" + taskKind,
            success: function(result){                        
                $("div#instructions").html(result);
            },
            error: function(xml,result){
                var messageError =  '<fmt:message key="Jsp Was Error" bundle="${msg}"/> '+
                                    '<fmt:message key="Jsp Contact To Admin" bundle="${msg}"/>';
                $("div#instructions").html("<h5 class='text-error'>"+messageError+"</h5>");
            }
        });                
    }); 
            
    $("button#task-launcher").click(function(){

        var taskKind = $("select#task-selector").val();
        
        var data = {
            task_kind: taskKind,
            parameters: []
        };
        
        $('#params-block :checked').each(function(){   
            var id = $(this).attr("id");
            var value = "true";
            data.parameters.push({ key: id, value: value});
        });

        $('#params-block input[type=text]').each(function(){  
            var id = $(this).attr("id");
            var value= $(this).val();
            data.parameters.push({ key: id, value: value});    
        });

        if(taskKind !== "none"){
            
            var urlBase = "${pageContext.request.contextPath}/resources/";
            
            $.ajax({ 
                type: "POST",
                url: urlBase + "tasks/${task.task_code}/launch",
                headers: security.getHeader(),
                data: JSON.stringify(data),
                contentType: 'application/json',
                success: function(result){
                    showModal("success", "("+taskKind+")  "+result);
                    setTimeout(function() {
                        window.location.reload();
                    }, 2000);
                },
                error: function(response){
                    $("button#task-launcher").removeAttr("disabled");
                    showModal("error", response.responseText);
                }
            });                                                           
        }
    });
    
    function showModal(pType, pMessage){
        var htmlMessage = "<h4 class='text-"+pType+"'>" + pMessage + "</h4>";
        $("div#task-result").html(htmlMessage);
        $('#test_modal').modal('show');
    }
});
</script>