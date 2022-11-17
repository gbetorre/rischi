<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:catch var="exception">

      <div class="row" id="org-div">
        <h4 class="mt-1 m-0 font-weight-bold">Strutture coinvolte nel <span id="macro_or_processo"></span> <span id="cod_macro_or_processo"></span> <span class="textcolorred">(personale FTE)</span></h4>
        <hr class="riga"/>
        <div class="chart-container col-12" style="padding-top:10px;"> </div>
        <script src='https://d3js.org/d3.v5.min.js'></script>
        <script src='https://unpkg.com/d3-org-chart@1.0.5/index.js'></script>
        <div class="row" id="str-pro"></div>
        <script id="rendered-js">
        d3.json('<c:out value="${initParam.urlDirectoryDocumenti}" />/json/prElencoAjax.json').
        then(data => {
          new Chart().
          container('.chart-container').
          data(data).
          initialZoom(0.6).
          onNodeClick(d => alert('ok')).
          render();
        });
        </script>
      </div>

</c:catch>
<c:out value="${exception}" />
