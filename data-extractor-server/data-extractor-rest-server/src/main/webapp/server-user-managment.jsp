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

<jsp:include page="header.jsp" >      
    <jsp:param name="showUserLogged" value="true" />
</jsp:include>

<div class="container">
        
    <blockquote>
        <%=TheResourceBundle.getString("Jsp Welcome Message")%>
    </blockquote>
    
    <br>        
    <div class="well">        
        <div id="result">
        </div>
        <label>User or email:</label> <input type="text" id="user"><br>
        <label>Password</label> <input type="password" id="pass"><br>
        <div>
            <div>
                <button type="submit" class="btn btn-primary" id="launch" href="#test_modal" data-toggle="modal">
                    <i class="icon-upload icon-white input-append"></i>
                    <span><%=TheResourceBundle.getString("Jsp Auth Button")%></span>
                </button>       
            </div>                
        </div>
        <br>
        <br>        
    </div> 
                
    <div class="modal fade" id="test_modal">
        <div class="modal-header">
            <a class="close" data-dismiss="modal">&times;</a>
            <h3><%=TheResourceBundle.getString("Jsp Popup Msg")%></h3>
        </div>
        <div class="modal-body">
            <div id="operation-result">                    
            </div>
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
    function showModal(pType, pMessage){
        var htmlMessage = "<h4 class='text-"+pType+"'>" + pMessage + "</h4>";
        $("div#operation-result").html(htmlMessage);
        $('#test_modal').modal('show');
    }

    var hey = "";
    $('#pass').keypress(function (e) {
      if (e.which == 13) {
        $("#launch").click();
        return false;    //<---- Add this line
      }
    });

    $('#user').keypress(function (e) {
      if (e.which == 13) {
        $("#launch").click();
        return false;    //<---- Add this line
      }
    });

    $("#launch").click(function(){
        $("label#result").text("");
        $("div#operation-result").text("");
        var user = $("#user").val();
        var pass = $("#pass").val();   
        pass = CryptoJS.SHA256(pass);
        $.ajax({ //Comunicación jQuery hacia JSP
            type: "GET",
            data: "user=" + user + "&pass=" + pass,
            url: "resources/authorization",
            dataType: "json",
            success: function(result){                             
                $("div#result").text(""); 

                if(result.success == "true"){
                    if(result.account_type = "user"){
                        showModal("success", result.message + '<br><br><%=TheResourceBundle.getString("Wait Msg")%>');
                        setTimeout(function() {
                            window.location = 'list-tasks.jsp';
                        }, 2000);
                    }
                    else{
                        showModal("error", result.message + ', but your account is not allow to access to webpage</h4>');
                    }                            
                }                                                
                else{ 
                    showModal("error", result.message);
                }
            },
            error: function(xml,result){
                showModal("error", result);
            }                    
        });  
    });
});
</script>
