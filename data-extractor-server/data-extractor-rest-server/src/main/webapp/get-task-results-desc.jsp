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
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@page import="eu.sisob.uma.restserver.services.communications.TasksParams"%>
<%@page import="eu.sisob.uma.restserver.TheResourceBundle"%>
<%@page import="eu.sisob.uma.restserver.services.gateCH.GateTaskCH"%>

<%
    request.setAttribute("GateTaskCH", GateTaskCH.NAME);
%>

<c:choose>
    <c:when test="${'gate' == param.taskKind}">
        Results files:<br>
        - <b>'AgentIdentification.csv'</b>: csv file with <b>personal data</b> extracted from cvs and webpages of the researchers.<br>
        - <b>'AccreditedUniversityStudies.csv'</b>: csv file with  <b>university studies</b> data extracted from cvs and webpages of the researchers.<br>
        - <b>'ProfessionalActivity.csv'</b>: csv file with <b>professional activities</b> data extracted from cv and webpages of the researchers.<br>
        - <b>'_DataExtracted.ods'</b>: Excel file with all the data extracted in three sheets.<br>
        Note: All the rows of each file must contains a researcher id in the frist field.<br>
    </c:when>
    <c:when test="${'crawler' == param.taskKind}">
        - <b>'data-researchers-documents-urls*.csv'</b>: csv file with the researcher's webpages. It has the same format of 'data-researcher-urls.csv' but with columns for the documents found.<br>
        - <b>'notfound.data-researchers-urls*.csv'</b>: csv file with the researchers data. It has the same format of 'data-researcher-urls.csv'.<br>
        - <b>'results.data-researchers-documents-urls.csv'</b>: File that contains a summary of the webpages found per university and subject.<br>
        Note: All the rows of each file must contains a researcher id in the frist field.<br>
    </c:when>
    <c:when test="${'websearcher' == param.taskKind}">
        - <b>'data-researchers-documents-urls*.csv'</b>: csv file with the researcher webpages of the researchers uploaded. It has the same format of 'data-researcher-urls.csv' but with columns for the documents found.<br>
        - <b>'notfound.data-researchers-urls*.csv'</b>: csv file with the researchers uploaded without results. It has the same format of 'data-researcher-urls.csv'.<br>
        <h5>Search patterns notes:.</h5>
        <label class="checkbox">
            <%=TasksParams.PARAM_CRAWLER_P1%>: "John J Smith"<br>
            <%=TasksParams.PARAM_CRAWLER_P2%>: "John J Smith AND Chemistry"<br>
            <%=TasksParams.PARAM_CRAWLER_P3%>: "John J Smith AND site:url"<br>
            <%=TasksParams.PARAM_CRAWLER_P4%>: "John J Smith AND Stanford"<br>
            <%=TasksParams.PARAM_CRAWLER_P5%>: "John J Smith AND Chemistry AND Stanford"<br>
    </label>
    </c:when>
    <c:when test="${'websearcher_cv' == param.taskKind}">
        - <b>'data-researchers-documents-urls*.csv'</b>: csv file with the researcher webpages of the researchers uploaded. It has the same format of 'data-researcher-urls.csv' but with columns for the documents found.<br>
        - <b>'data-researchers-documents-files*.csv'</b>: The same of previous file but the researcher's webpage column has got in this case a reference to the downloaded files.<br>
        - <b>'downloads.zip'</b>: Downloaded files that are referenced in the previous file.<br>
        - <b>'notfound.data-researchers-urls*.csv'</b>: csv file with the researchers uploaded without results. It has the same format of 'data-researcher-urls.csv'.<br>      
    </c:when>
    <c:when test="${'internalcvfiles' == param.taskKind}">
        - <b>'data-researchers-documents-urls-suburls-*.csv'</b>: csv file with the researcher webpages of the researchers uploaded. It has the same format of 'data-researchers-documents-urls*.csv' but in the researcher's webpages columns there are the new subpages located (cv, pub, etc).<br>
        - <b>'data-researchers-documents-urls-subfiles-*.csv'</b>: The same of previous file but the researcher's webpage column has got in this case a reference to the downloaded files.<br>
        - <b>'downloads.zip'</b>: Downloaded files that are referenced in the previous file.<br>
    </c:when>
    <c:when test="${'email' == param.taskKind}">
        - <b>'data-researchers-documents-urls-email*.csv'</b>: csv file with the researcher webpages of the researchers uploaded. It has the same format of 'data-researchers-documents-urls*.csv' but with one column for the email.<br>
        - <b>'notfound.data-researchers-documents-urls*.csv'</b>: csv file with the researchers uploaded without emails. It has the same format of 'data-researchers-documents-urls*.csv'.<br>
        - <b>'norepeat.data-researchers-documents-urls-email*.csv'</b>: csv file with the researcher webpages of the researchers uploaded. It has the same format of 'data-researchers-documents-urls*.csv' but with one column for the email but with a filter applied that removes duplicated emails.<br>
    </c:when>
    <c:when test="${GateTaskCH == param.taskKind}">
        - <b>'IndicatorsByCategory.json'</b>: json file with indicators grouped by categories.<br>
        - <b>'IndicatorsBySpeech.json'</b>: json file with indicators grouped by speeches.<br>
        - <b>'SpeechesDataExtracted.csv'</b>: csv file with speeches relatives to Cultural Heritage. Each row contains the speech data and the keywords found.<br>
    </c:when>
</c:choose>
