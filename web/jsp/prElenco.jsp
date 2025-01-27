<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="URL.jspf" %>
    <h3 class="mt-1 m-0 font-weight-bold float-left">Processi Organizzativi</h3>    
    <a href="${prCSV}" class="float-right badge badge-pill lightTable bgAct20" title="Scarica il database completo dei processi organizzativi censiti a fini anticorruttivi">
      <i class="fas fa-download"></i>Scarica tutti i dati
    </a>
    <span class="float-right">
      <a href="${ppr}" type="button" class="badge bg-success btn-small lightTable text-white marginLeft align-middle refresh" id="insert" title="Aggiungi un macro/processo">
        <i class="fa-solid fa-square-plus"></i>
        Aggiungi
      </a>&nbsp;&nbsp;
    </span>
    <hr class="riga"/>
    <div class="chart-container col-12" style="padding-top:10px;"> </div>
    <div class="ajax-loader" id="imgload">
      <img src="${initParam.urlDirectoryImmagini}ajax-loader.gif" class="img-responsive" />
    </div>
    <script src="<c:out value="${initParam.urlDirectoryScript}" />orgchart/1.0.5/d3.v5.min.js"></script>
    <script src="<c:out value="${initParam.urlDirectoryScript}" />orgchart/1.0.5/index.js"></script>
      <div class="row col-12" id="fas-pro"></div>
    <script>
    /* Ajax call per lista fasi di processo */
    function asyncCallee(d) {
      var url = "data?q=pr" ; // -> Command token
      var idP = d.substring(d.indexOf('.')+1, d.indexOf('-'));  // -> id processo
      var lev = d.substring(d.lastIndexOf('-')+1, d.length);  // -> livello
      var params = "p=pro&pliv=" + idP + "&liv=" + lev + "&r=${param['r']}";
      updateCount = function(data, textStatus, jqXHR) {
        $('#imgload').css('visibility', 'hidden');  
        $("div#fas-pro").html(data);
      }
      ajaxCall(url, "GET", params, '#fas-pro', updateCount);
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
