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
<%@page import="eu.sisob.uma.restserver.SystemManager"%>
<%@page import="eu.sisob.uma.restserver.TheResourceBundle"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>

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
    
    String task_code    = request.getParameter("task_code");
    
    String version      = SystemManager.getInstance().getVersion();
    String urlBaseJson  = AuthorizationManager.getGetFileUrlToShow(user, 
                                                                  pass, 
                                                                  task_code, 
                                                                  "", 
                                                                  AuthorizationManager.results_dirname);
    
    String urlBaseData = "task-euParliament-data.jsp"+"?task_code="+task_code+"&speech_id=";
    
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
    <jsp:param name="showUserLogged" value="true" />
</jsp:include>  

<div class="container"> 
    <h5>
        <a href="upload-and-launch.jsp?task_code=<%=task_code%>">
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
    