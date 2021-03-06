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

<%--
    Author: Antonio Jesus Gallardo Albarran - antonio.jesus.gallardo@gmail.com
--%>

<!DOCTYPE HTML>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<fmt:setBundle basename="Bundle" var="msg"/>

<t:generic-template>
    <jsp:attribute name="resources">
        <jsp:include page="layout/resources.jsp" />
    </jsp:attribute>
    <jsp:attribute name="header">
        <jsp:include page="layout/header.jsp" >  
            <jsp:param name="showUserLogged" value="false" />
        </jsp:include>
    </jsp:attribute>
    <jsp:attribute name="footer">
        <jsp:include page="layout/footer.jsp" />
    </jsp:attribute>
    <jsp:body>
        <div class="well" style="text-align: center">
            <h4>
                <fmt:message key="Jsp Was Error" bundle="${msg}"/>
                <fmt:message key="Jsp Contact To Admin" bundle="${msg}"/>
            </h4>
            <br/>
            <h4>
                <a href="index.jsp">
                    <fmt:message key="Jsp Error Go Index" bundle="${msg}"/>
                </a>
            </h4>
        </div>  
    </jsp:body>
</t:generic-template>


