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
<!DOCTYPE HTML>
<%@page import="eu.sisob.uma.restserver.TheResourceBundle"%>

<%
    String paramMessage = request.getParameter("message");
    
    String modalMessage = "";
    String typeMessage = "error";
    
    if("notAllowed".equals(paramMessage)){
        modalMessage = TheResourceBundle.getString("Jsp No Access Msg");
    }
    else if("unauth_data".equals(paramMessage)){
        modalMessage = TheResourceBundle.getString("Jsp Unauth Msg");
    }
    else if("unauth_type".equals(paramMessage)){
        modalMessage = TheResourceBundle.getString("Jsp Unauth Type Msg");
    }
    else if("logout".equals(paramMessage)){
        modalMessage = TheResourceBundle.getString("Jsp Logout");
        typeMessage = "success";
    }
    
    request.setAttribute("modalMessage", modalMessage);
    request.setAttribute("typeMessage", typeMessage);
%>

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
      
        <table>
            <tr>
                <td>  
                    <blockquote>
                        <fmt:message key="Jsp Welcome Message" bundle="${msg}"/>
                    </blockquote>
                </td>
                <td>
                    <div class="well">
                        <form method="post" action="login.jsp">
                            <label>Username:</label> 
                            <input type="text" name="user" id="user">

                            <label>Password:</label> 
                            <input type="password" name="pass" id="pass">

                            <br>
                            <input  id="btnLogin"
                                    type="submit" 
                                    value="<fmt:message key='Jsp Auth Button' bundle='${msg}'/>" 
                                    class="btn btn-primary"/>
                        </form>
                    </div>                
                </td>
            </tr>
        </table>

        <div class="modal fade" id="test_modal">
            <div class="modal-header">
                <a class="close" data-dismiss="modal">&times;</a>
                <h3><fmt:message key="Jsp Popup Msg" bundle="${msg}"/></h3>
            </div>
            <div class="modal-body" id="operation-result">
            </div>
            <div class="modal-footer">
                <a href="#" class="btn" data-dismiss="modal">Close</a>            
            </div>
        </div> 
    </jsp:body>
</t:generic-template>
        
<script type="text/javascript">    
    $(document).ready(function()
    {
        $('#pass').keypress(function (e) {
          if (e.which == 13) {
            $("#btnLogin").click();
            return false;
          }
        });

        $('#user').keypress(function (e) {
          if (e.which == 13) {
            $("#btnLogin").click();
            return false;
          }
        });

        function showModal(pMessage, pTypeMessage){

            var htmlMessage =   '<h4 class="text-'+pTypeMessage+'">' + 
                                    pMessage + 
                                '</h4>';

            $("div#operation-result").append(htmlMessage);                                                                           
            $('#test_modal').modal('show');
        }

        
    <c:if test="${modalMessage!=null && modalMessage!=''}">
        showModal('${modalMessage}', '${typeMessage}');  
    </c:if>
        
    });
</script>