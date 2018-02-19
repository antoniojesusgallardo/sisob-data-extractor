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
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@page session="true"%>

<fmt:setBundle basename="Bundle" var="msg"/>

<div class="page-header">
    <table>
        <tr>
            <td>
                <img src="img/sisob_logo.png" />
            </td>
            <td>
                <h1><fmt:message key="Jsp System Title" bundle="${msg}"/></h1>          
            </td>
        </tr>   
    </table>  

    <c:if test="${param.showUserLogged == true}">
        <blockquote>
            <h4 class="text-success" style="float:left;">
                (${sessionScope.user} : 
                <fmt:message key="Jsp Auth Msg" bundle="${msg}"/>)
            </h4>
            <h4 style="float:right;">
                <span>
                    <a href="logout.jsp">Logout</a>
                </span>   
            </h4>
        </blockquote>
    </c:if>
</div>