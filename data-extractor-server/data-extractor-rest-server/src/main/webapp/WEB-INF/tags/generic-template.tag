<%-- 
    Document   : genericTemplate
    Created on : Feb 15, 2018, 11:47:45 PM
    Author     : ajgallardo
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