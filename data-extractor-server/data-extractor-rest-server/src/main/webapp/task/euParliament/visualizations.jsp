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

<%@page import="eu.sisob.uma.restserver.client.UtilJsp"%>
<%@page import="eu.sisob.uma.restserver.TheResourceBundle"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>

<%@page session="true"%>
<%  
    if (!UtilJsp.validateSession(session)){
        response.sendRedirect(request.getContextPath()+"/index.jsp?message=notAllowed");
        return;
    }
    
    List<String> visualizationNames = new ArrayList();
    visualizationNames.add("temporalEvolution");
    visualizationNames.add("wordCloud");
    visualizationNames.add("generalIndicators");
    visualizationNames.add("speechesByCountries");
    
    List<String[]> visualizationTypes = new ArrayList();
    for (String strType : visualizationNames) {
        String[] iArray = {strType, 
                            TheResourceBundle.getString("Jsp_euParliament_visualizations_"+strType)};
        
        visualizationTypes.add(iArray);
    }
    
    request.setAttribute("visualizationTypes", visualizationTypes);
%>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<fmt:setBundle basename="Bundle" var="msg"/>

<t:generic-template>
    <jsp:attribute name="resources">
        <jsp:include page="../../layout/resources.jsp" />
    </jsp:attribute>
    <jsp:attribute name="header">
        <jsp:include page="../../layout/header.jsp" >  
            <jsp:param name="showUserLogged" value="true" />
        </jsp:include>
    </jsp:attribute>
    <jsp:attribute name="footer">
        <jsp:include page="../../layout/footer.jsp" />
    </jsp:attribute>
    <jsp:body> 

        <h5>
            <a href="task/details.jsp?task_code=${param.task_code}">
                <fmt:message key="Jsp_euParliament_visualizations_back" bundle="${msg}"/>
            </a>
        </h5>
            
        <div class="well" id="instructions">

            <h4 style="text-align: center">
                <fmt:message key="Jsp_euParliament_visualizations_title" bundle="${msg}"/>
            </h4>

            <fmt:message key="Jsp_euParliament_visualizations_selectVisualizations" bundle="${msg}"/>:
            <select class="chzn-select" id="visualization-selector" onchange="sisob.euParliament.visualizations.changeSelector();">   
                <c:forEach items="${visualizationTypes}" var="iType">
                    <option value="${iType[0]}">${iType[1]}</option>
                </c:forEach>
            </select>

            <div id="chart1"></div>
            
            <script>
                sisob.euParliament.visualizations.init(${param.task_code});    
            </script>
        </div>
    </jsp:body>
</t:generic-template>