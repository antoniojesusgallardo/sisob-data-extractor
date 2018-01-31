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
%>

<jsp:include page="header.jsp" >  
    <jsp:param name="showUserLogged" value="false" />
</jsp:include>

<div class="container">
        
    <table>
        <tr>
            <td>  
                <blockquote>
                    <%=TheResourceBundle.getString("Jsp Welcome Message")%>
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
                                value="<%=TheResourceBundle.getString("Jsp Auth Button")%>" 
                                class="btn btn-primary"/>
                    </form>
                </div>                
            </td>
        </tr>
    </table>
                                
    <div class="modal fade" id="test_modal">
        <div class="modal-header">
            <a class="close" data-dismiss="modal">&times;</a>
            <h3><%=TheResourceBundle.getString("Jsp Popup Msg")%></h3>
        </div>
        <div class="modal-body" id="operation-result">
        </div>
        <div class="modal-footer">
            <a href="#" class="btn" data-dismiss="modal">Close</a>            
        </div>
    </div> 
</div>
        
<jsp:include page="footer.jsp" />
        
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

        <%
        if(modalMessage!=null && modalMessage!="")
        { 
        %>
            showModal('<%=modalMessage%>', '<%=typeMessage%>');
        <%    
        } 
        %>
    });
</script>