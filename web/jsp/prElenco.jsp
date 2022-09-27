<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="URL.jspf" %>
    <h3 class="mt-1 m-0 font-weight-bold">Processi Anticorruttivi</h3>
    <hr class="riga"/>
    <div class="chart-container col-12" style="padding-top:10px;"> </div>
    <script src="<c:out value="${initParam.urlDirectoryScript}" />orgchart/1.0.5/d3.v5.min.js"></script>
    <script src="<c:out value="${initParam.urlDirectoryScript}" />orgchart/1.0.5/index.js"></script>
    <a href="${stCSV}">
      <i class="fas fa-download"></i> <span class="lead">Scarica tutti i dati</span>
    </a>
    <div class="row" id="str-pro"></div>
    <script>
    // Ajax call per lista processi per struttura
    function asyncCallee(d) {
      // Command token
      var url = "data?q=st" ;
      // Variabili per l'url
      var idS = d.substring(d.indexOf('.')+1, d.indexOf('-'));
      var lev = d.substring(d.lastIndexOf('-')+1, d.length);
      // Parametri
      var params = "idS=" + idS + "&lev=" + lev + "&r=${param['r']}";
      updateCount = function(data, textStatus, jqXHR) {
        $("div#str-pro").html(data);
      }
      ajaxCall(url, "GET", params, '#str-pro', updateCount);
    }
    </script>
    <script id="rendered-js">
    d3.json('<c:out value="${initParam.urlDirectoryDocumenti}" />/json/prElenco.json').
    then(data => {
      new Chart().
      container('.chart-container').
      data(data).
      initialZoom(0.6).
      onNodeClick(d => asyncCallee(d)).
      render();
    });
    </script>
