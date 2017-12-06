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

<%@page import="eu.sisob.uma.restserver.AuthorizationManager"%>
<%@page import="eu.sisob.uma.restserver.services.communications.InputParameter"%>
<%@page import="eu.sisob.uma.restserver.TheResourceBundle"%>
<%@page import="eu.sisob.uma.restserver.SystemManager"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%  
    
    String user         = request.getParameter("user");
    String pass         = request.getParameter("pass");
    String task_code    = request.getParameter("task_code");
    String reason       = request.getParameter("reason");
    String reason_type  = request.getParameter("reason_type");
    
    String version      = SystemManager.getInstance().getVersion();
    String urlBaseJson  = AuthorizationManager.getGetFileUrlToShow(user, 
                                                                  pass, 
                                                                  task_code, 
                                                                  "", 
                                                                  AuthorizationManager.results_dirname);
    
    String paramsUserTaskPass = "?user=" + user + 
                                "&task_code=" + task_code +
                                "&pass="+ pass ;
    
    String urlBaseData = "task-euParliament-data.jsp"+
                                paramsUserTaskPass +
                                "&reason=" + reason +
                                "&reason_type=" + reason_type +
                                "&type=" + AuthorizationManager.detailed_results_dirname +
                                "&file=";
    
    String urlBack = "upload-and-launch.jsp"+ paramsUserTaskPass;
    
    List<String> visualizationsTypes = new ArrayList<String>();
    visualizationsTypes.add("temporalEvolution");
    visualizationsTypes.add("wordCloud");
    visualizationsTypes.add("generalIndicators");
    visualizationsTypes.add("speechesByCountries");
%>

<%-- Library: NDD3.js - D3.js --%>
<link href="nvd3/nv.d3.css" rel="stylesheet" type="text/css">
<script src="nvd3/d3.min.js" charset="utf-8"></script>
<script src="nvd3/nv.d3.js"></script>

<%-- Module - word cloud --%>
<script src="nvd3/d3.layout.cloud.js" ></script>

<%-- JavaScript Development - Custom visualizations --%>
<script src="js/euParliament/util.js?v.<%=version%>" ></script>
<script src="js/euParliament/visualizations/visualizations.js?v.<%=version%>" ></script>
<script src="js/euParliament/visualizations/barChart_generalIndicators.js?v.<%=version%>" ></script>
<script src="js/euParliament/visualizations/barChart_speechesByCountry.js?v.<%=version%>" ></script>
<script src="js/euParliament/visualizations/lineChart_keywordsEvolution.js?v.<%=version%>" ></script>
<script src="js/euParliament/visualizations/wordCloud_keywords.js?v.<%=version%>" ></script>


<!DOCTYPE HTML>
<jsp:include page="header.jsp" >
    <jsp:param name="user" value="<%=user%>" />                        
    <jsp:param name="reason" value="<%=reason%>" />
    <jsp:param name="reason_type" value="<%=reason_type%>" />         
    <jsp:param name="back_to_list" value="false" />
    <jsp:param name="logout" value="true" />
</jsp:include>  

<div class="container"> 
    <h5>
        <a href="<%=urlBack%>">
            <%=TheResourceBundle.getString("Jsp_euParliament_visualizations_back")%>
        </a>
    </h5>
</div>

<div class="container">   
    <div class="well" id="instructions">
        
        <h4 style="text-align: center">
            <%=TheResourceBundle.getString("Jsp_euParliament_visualizations_title")%>
        </h4>
        
        <%=TheResourceBundle.getString("Jsp_euParliament_visualizations_selectVisualizations")%>:
        <select class="chzn-select" id="visualization-selector" onchange="Visualizations.changeSelector();">   
            <% for(String iVisualization : visualizationsTypes) { %>                
            <option value="<%=iVisualization%>">
                <%=TheResourceBundle.getString("Jsp_euParliament_visualizations_"+iVisualization) %>
            </option>                
            <% } %>
        </select>
        
        <div id="chart1"></div>
    </div>
</div>

<jsp:include page="footer.jsp" />

<script>
    var jsonData_ontologies;
    var jsonData_speeches;
    
    var urlBaseJson = '<%=urlBaseJson%>';
    var urlBaseData = '<%=urlBaseData%>'
    
    Visualizations.init();    
</script>
    