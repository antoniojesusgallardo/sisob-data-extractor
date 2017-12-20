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
<%@page import="eu.sisob.uma.restserver.TheResourceBundle"%>
<%@page import="eu.sisob.uma.restserver.SystemManager"%>
<%    
    String version = SystemManager.getInstance().getVersion();
    
    String user         = request.getParameter("user");
    String pass         = request.getParameter("pass");
    String task_code    = request.getParameter("task_code");
    String reason       = request.getParameter("reason");
    String reason_type  = request.getParameter("reason_type");
    String file         = request.getParameter("file");

    String urlJson = AuthorizationManager.getGetFileUrlToShow(user, 
                                                              pass, 
                                                              task_code, 
                                                              file, 
                                                              AuthorizationManager.detailed_results_dirname);
%>

<%-- Library: NDD3.js - D3.js --%>
<link href="nvd3/nv.d3.css" rel="stylesheet" type="text/css">
<!--<script src="https://cdnjs.cloudflare.com/ajax/libs/d3/3.5.2/d3.min.js" charset="utf-8"></script>-->
<script src="nvd3/d3.min.js" charset="utf-8"></script>
<script src="nvd3/nv.d3.js"></script>
<script src="nvd3/d3.js"></script>

<%-- JavaScript Development --%>
<script src="js/euParliament/util.js?v.<%=version%>" ></script>
<script src="js/euParliament/data/loadData.js?v.<%=version%>" ></script>

<!DOCTYPE HTML>
<jsp:include page="header.jsp" >
    <jsp:param name="user" value="<%=user%>" />                        
    <jsp:param name="reason" value="<%=reason%>" />
    <jsp:param name="reason_type" value="<%=reason_type%>" />         
    <jsp:param name="back_to_list" value="false" />
    <jsp:param name="logout" value="false" />
</jsp:include>  
      
<div class="container">   
    <div class="well">        
        
        <table style="width:100%; font-size:14px; line-height:20px;">
            <tr>
                <td>                    
                    <h4 style="text-align: center">
                        <%=TheResourceBundle.getString("Jsp_euParliament_data_title_details")%>
                    </h4>                    
                    <div id="contentData"></div>
                </td>
                <td>
                    <h4 style="text-align: center">
                        <%=TheResourceBundle.getString("Jsp_euParliament_data_title_legend")%>
                    </h4>
                    <div id="contentLegend" ></div>
                </td>
            </tr>
        </table>
    </div>
</div>          
            
<div class="container">   
    <div class="well">
        <h4 style="text-align: center">
            <%=TheResourceBundle.getString("Jsp_euParliament_data_title_text")%>
        </h4>
        <div id="contentText"></div>
    </div>
</div>      

<jsp:include page="footer.jsp" />

<script>
    var urlJson = '<%=urlJson%>';
    
    loadData();  
</script>
    