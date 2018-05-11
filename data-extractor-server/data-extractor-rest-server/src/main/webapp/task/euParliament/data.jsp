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
<%@page import="eu.sisob.uma.restserver.client.RESTUri"%>
<%@page import="eu.sisob.uma.restserver.client.UtilJsp"%>
<%@page import="eu.sisob.uma.restserver.managers.AuthorizationManager"%>
<%@page import="eu.sisob.uma.restserver.managers.SystemManager"%>

<%@page session="true"%>
<%  
    if (!UtilJsp.validateSession(session)){
        response.sendRedirect(request.getContextPath()+"/index.jsp?message=notAllowed");
        return;
    }
    
    String version = SystemManager.getInstance().getVersion();
    
    String task_code    = request.getParameter("task_code");
    String speech_id    = request.getParameter("speech_id");

    String urlJson = RESTUri.getFileToShow(task_code, speech_id+".json", 
                                            AuthorizationManager.detailed_results_dirname);
    
    request.setAttribute("urlJson", urlJson);
    request.setAttribute("version", version);
%>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<fmt:setBundle basename="Bundle" var="msg"/>

<t:generic-template>
    <jsp:attribute name="resources">
        <jsp:include page="../../layout/resources.jsp" />
        
        <%-- JavaScript Development --%>
        <script src="static/js/euParliament/util.js?v.${version}" ></script>
        <script src="static/js/euParliament/data/loadData.js?v.${version}" ></script>
    </jsp:attribute>
    <jsp:attribute name="header">
        <jsp:include page="../../layout/header.jsp" >  
            <jsp:param name="showUserLogged" value="false" />
        </jsp:include>
    </jsp:attribute>
    <jsp:attribute name="footer">
        <jsp:include page="../../layout/footer.jsp" />
    </jsp:attribute>
    <jsp:body> 

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

        <div class="well">
            <h4 style="text-align: center">
                <fmt:message key="Jsp_euParliament_data_title_text" bundle="${msg}"/>
            </h4>
            <div id="contentText"></div>
        </div>
    </jsp:body>
</t:generic-template>

<script>
    var urlJson = '${urlJson}';
    
    loadData("${sessionScope.token}");  
</script>
    