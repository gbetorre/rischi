<%@ page contentType="text/html;" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page isErrorPage="true" %>
<%@ page session="false" %>
<c:set var="isErrorPage" value="true" scope="request" />
<c:set var="msg" value="${requestScope.message}" scope="page" />
<!DOCTYPE html>
<html>
  <head>
    <title>Pagina di errore dell'applicazione pr</title>
    <meta charset="utf-8" />
    <meta name="language" content="Italian" />
    <meta name="description" content="${requestScope.advice}" />
    <meta name="creator" content="Giovanroberto Torre, giovanroberto.torre@univr.it" />
    <meta name="author" content="Giovanroberto Torre, giovanroberto.torre@univr.it" />
    <meta http-equiv='cache-control' content='no-cache'>
    <!-- Include jQuery from CDN or from local hosted copy --> 
    <script src="https://code.jquery.com/jquery-3.3.1.min.js"></script>
    <script>
      window.jQuery || document.write('<script src="${initParam.urlDirectoryScrypt}/jquery-3.3.1.js"><\/script>');
    </script>
    <!-- Latest compiled and minified CSS -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.css">
    <!-- Latest compiled CSS, just in case -->
    <link rel="stylesheet" href="<c:out value="${initParam.urlDirectoryStili}" />bootstrap.css" type="text/css" />
    <link rel="stylesheet" href="<c:out value="${initParam.urlDirectoryStili}" />style.css" type="text/css" />
  </head>
  <body>
    <!-- Header -->
    <div id="idHeader">
      <%@ include file="header.jspf"%>
    </div>
    <h1>Pagina di errore dell'applicazione</h1>
    <h2>La richiesta inoltrata (indirizzo o url) non &egrave; corretta.</h2>
    <input type="hidden" value="${msg}">
    <div class="alert alert-danger">
      <c:out value="${fn:substring(pageScope.msg, 35, 350)}" />
    </div>
    <p>
      <a href="${initParam.appName}">Torna al Login</a>
      (vi verrai diretto automaticamente tra: <span id="countdown">10</span>)
    </p>
    <script type="text/javascript">
      var ss = 10;
      function countdown() {
        ss = ss-1;
        if (ss < 0) {
          window.location="${initParam.appName}".replace("&amp;", "&");
        }
        else {
          document.getElementById("countdown").innerHTML = ss;
          window.setTimeout("countdown()", 1000);
        }
      }
      // Avvia il countdown
      countdown();
    </script> 
  </body>
</html>
