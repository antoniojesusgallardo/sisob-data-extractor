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
    Document   : generic-template
    Created on : Feb 15, 2018, 11:47:45 PM
    Author     : Antonio Jesus Gallardo Albarran - antonio.jesus.gallardo@gmail.com
--%>
<%@tag description="Overall Page template" pageEncoding="UTF-8"%>
<%@attribute name="resources" fragment="true" %>
<%@attribute name="header" fragment="true" %>
<%@attribute name="footer" fragment="true" %>
<html>
    <head>
        <jsp:invoke fragment="resources"/>
    </head>
    <body>
        <div class="container">
            <div id="header">
                <jsp:invoke fragment="header"/>
            </div>
            <div id="body">
                <jsp:doBody/>
            </div>
            <div id="footer">
                <jsp:invoke fragment="footer"/>
            </div>
        </div>
    </body>
</html>