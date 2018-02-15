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
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@page import="eu.sisob.uma.restserver.AuthorizationManager"%>
<%@page import="eu.sisob.uma.restserver.SystemManager"%>

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
    
    String version = SystemManager.getInstance().getVersion();
    
    String user = (String)session.getAttribute("user");
    String pass = (String)session.getAttribute("pass");
    
    String task_code    = request.getParameter("task_code");
    String speech_id    = request.getParameter("speech_id");

    String urlJson = AuthorizationManager.getGetFileUrlToShow(user, 
                                                              pass, 
                                                              task_code, 
                                                              speech_id+".json", 
                                                              AuthorizationManager.detailed_results_dirname);
    
    request.setAttribute("urlJson", urlJson);
    
%>

<%-- Library: NDD3.js - D3.js --%>
<link href="nvd3/nv.d3.css" rel="stylesheet" type="text/css">
<!--<script src="https://cdnjs.cloudflare.com/ajax/libs/d3/3.5.2/d3.min.js" charset="utf-8"></script>-->
<script src="nvd3/d3.min.js" charset="utf-8"></script>
<script src="nvd3/nv.d3.js"></script>
<script src="nvd3/d3.js"></script>

<%-- JavaScript Development --%>
<script src="js/euParliament/util.js?v.<%=version%>" ></script>
<script src="js/euParliament/data/loadData.js?v.<%=version%>" ></script>

<fmt:setBundle basename="Bundle" var="msg"/>

<jsp:include page="header.jsp" >   
    <jsp:param name="showUserLogged" value="true" />
</jsp:include>  
      
<div class="container">   
    <div class="well">        
        
        <table style="width:100%; font-size:14px; line-height:20px;">
            <tr>
                <td>                    
                    <h4 style="text-align: center">
                        <fmt:message key="Jsp_euParliament_data_title_details" bundle="${msg}"/>
                    </h4>                    
                    <div id="contentData"></div>
                </td>
                <td>
                    <h4 style="text-align: center">
                        <fmt:message key="Jsp_euParliament_data_title_legend" bundle="${msg}"/>
                    </h4>
                    <div id="contentLegend" ></div>
                </td>
            </tr>
        </table>
    </div>
</div>          
            
<div class="container">   
    <div class="well">
        <h4 style="text-align: center">
            <fmt:message key="Jsp_euParliament_data_title_text" bundle="${msg}"/>
        </h4>
        <div id="contentText"></div>
    </div>
</div>      

<jsp:include page="footer.jsp" />

<script>
    var urlJson = '${urlJson}';
    
    loadData();  
</script>
    