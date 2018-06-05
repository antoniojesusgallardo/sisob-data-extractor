<%-- 
    Copyright (c) 2014 "(IA)2 Research Group. Universidad de M�laga"                        http://iaia.lcc.uma.es | http://www.uma.es

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

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="req" value="${pageContext.request}" />
<c:set var="url">${req.requestURL}</c:set>
<c:set var="uri" value="${req.requestURI}" />

<%@page import="eu.sisob.uma.restserver.managers.SystemManager"%>
 
<%
    String version = SystemManager.getInstance().getVersion();
    request.setAttribute("version", version);
%>

<fmt:setBundle basename="Bundle" var="msg"/>

<!-- Force latest IE rendering engine or ChromeFrame if installed -->
<!--[if IE]><meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"><![endif]-->
<meta charset="utf-8">
<title><fmt:message key="Jsp System Title" bundle="${msg}"/></title>
<meta name="description" content="">
<meta name="viewport" content="width=device-width">

<base href="${fn:substring(url, 0, fn:length(url) - fn:length(uri))}${req.contextPath}/">

<link rel="stylesheet" href="static/css/bootstrap.min.2.3.2.css">
<!-- Generic page styles -->
<link rel="stylesheet" href="static/css/style.css">
<!-- Bootstrap styles for responsive website layout, supporting different screen sizes -->
<link rel="stylesheet" href="static/css/bootstrap-responsive.min.2.3.2.css">
<!-- Bootstrap CSS fixes for IE6 -->
<!--[if lt IE 7]><link rel="stylesheet" href="static/css/bootstrap-ie6.min.css"><![endif]-->
<!-- Bootstrap Image Gallery styles -->
<link rel="stylesheet" href="static/css/bootstrap-image-gallery.min.2.3.2.css">
<!-- CSS to style the file input field as button and adjust the Bootstrap progress bars -->
<!-- Shim to make HTML5 elements usable in older Internet Explorer versions -->
<!--[if lt IE 9]><script src="static/js/html5.js"></script><![endif]-->
<!-- blueimp Gallery styles -->
<link rel="stylesheet" href="static/css/blueimp-gallery.min.css">
<!-- CSS to style the file input field as button and adjust the Bootstrap progress bars -->
<link rel="stylesheet" href="static/css/jquery.fileupload-ui.css">
<!-- CSS adjustments for browsers with JavaScript disabled -->

<script src="static/js/security.js?v.${version}"></script>

<script src="static/js/jquery-1.8.2.min.js"></script>
<!-- The jQuery UI widget factory, can be omitted if jQuery UI is already included -->
<script src="static/js/vendor/jquery.ui.widget.js"></script>
<script src="static/js/bootstrap.min.js"></script>
<script src="static/js/bootstrap-image-gallery.min.js"></script>

<%-- Library: NDD3.js - D3.js --%>
<link href="static/nvd3/nv.d3.css" rel="stylesheet" type="text/css">
<script src="static/nvd3/d3.min.js" charset="utf-8"></script>
<script src="static/nvd3/nv.d3.js"></script>

<%-- Module - word cloud --%>
<script src="static/nvd3/d3.layout.cloud.js" ></script>

<%-- Plugin - uploadFile --%>
<!-- The Templates plugin is included to render the upload/download listings http://blueimp.github.com/JavaScript-Templates/tmpl.min.js -->
<script src="static/js/tmpl.min.js"></script>
<!-- The Load Image plugin is included for the preview images and image resizing functionality http://blueimp.github.com/JavaScript-Load-Image/load-image.min.js -->
<script src="static/js/load-image.min.js"></script>
<!-- The Canvas to Blob plugin is included for image resizing functionality http://blueimp.github.com/JavaScript-Canvas-to-Blob/canvas-to-blob.min.js -->
<script src="static/js/canvas-to-blob.min.js"></script>
<!-- The Iframe Transport is required for browsers without support for XHR file uploads -->
<script src="static/js/jquery.iframe-transport.js"></script>
<!-- The basic File Upload plugin -->
<script src="static/js/jquery.fileupload.js"></script>
<!-- The File Upload file processing plugin -->
<script src="static/js/jquery.fileupload-fp.js"></script>
<!-- The File Upload user interface plugin -->
<script src="static/js/jquery.fileupload-ui.js?v.${version}"></script>
<!-- The localization script -->
<script src="static/js/locale.js"></script>
<!-- The main application script -->
<script src="static/js/main.js?v.${version}"></script>
<!-- The XDomainRequest Transport is included for cross-domain file deletion for IE8+ -->
<!--[if gte IE 8]><script src="static/js/cors/jquery.xdr-transport.js"></script><![endif]-->

<%-- JavaScript Development --%>
<script src="static/js/util.js?v.${version}" ></script>

<script src="static/js/euParliament/data/loadData.js?v.${version}" ></script>

<script src="static/js/euParliament/visualizations/visualizations.js?v.${version}" ></script>
<script src="static/js/euParliament/visualizations/barChart_generalIndicators.js?v.${version}" ></script>
<script src="static/js/euParliament/visualizations/barChart_speechesByCountry.js?v.${version}" ></script>
<script src="static/js/euParliament/visualizations/lineChart_keywordsEvolution.js?v.${version}" ></script>
<script src="static/js/euParliament/visualizations/wordCloud_keywords.js?v.${version}" ></script>
<%-- END - JavaScript Development --%>
<link rel="icon" type="image/png" href="static/img/favicon.png">