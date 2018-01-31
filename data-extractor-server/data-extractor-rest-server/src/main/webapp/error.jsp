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

<!DOCTYPE HTML>
<%@page import="eu.sisob.uma.restserver.TheResourceBundle"%>

<jsp:include page="header.jsp" >   
    <jsp:param name="showUserLogged" value="false" />
</jsp:include>  
      
<div class="container">   
    <div class="well" style="text-align: center">
        <h4>
            <%=TheResourceBundle.getString("Jsp Was Error")%>
            <%=TheResourceBundle.getString("Jsp Contact To Admin")%>
        </h4>
        <br/>
        <h4>
            <a href="index.jsp">
                <%=TheResourceBundle.getString("Jsp Error Go Index")%>
            </a>
        </h4>
    </div>
</div>      

<jsp:include page="footer.jsp" />


