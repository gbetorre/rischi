<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
    <%-- ul>
    <c:forEach var="l1" items="${requestScope.listaStrutture}" varStatus="status">
    <li>
      ${l1.nome}
      <ul>
      <c:forEach var="l2" items="${l1.figlie}" varStatus="status">
        <li>
          <c:out value="${l2.nome}" />
            <ul>
            <c:forEach var="l3" items="${l2.figlie}" varStatus="status">
              <li>
                <c:out value="${l3.nome}" />
                <ul>
                <c:forEach var="l4" items="${l3.figlie}" varStatus="status">
                  <li><c:out value="${l4.nome}" /></li>
                </c:forEach>
                </ul>
              </li>
            </c:forEach>
            </ul>
        </li>
      </c:forEach>
      </ul>
    </li>
    </c:forEach>
    </ul>
    <div class="row">
      <div id="tab-str" class="col-6">
        Strutture
        <hr class="riga"/>
        <div class="accordion" id="accordionStructures">
        <c:set var="expanded" value="true" scope="page" />
        <c:set var="collapsed" value="" scope="page" />
        <c:set var="show" value="show" scope="page" />
        <c:forEach var="struttura" items="${requestScope.listaStrutture}" varStatus="status">
          <div class="card">
            <div class="card-header" id="heading${status.count}">
              <h5 class="mb-0">
                <button class="btn btn-link ${collapsed}" type="button" data-toggle="collapse" data-target="#collapse${status.count}" aria-expanded="${expanded}" aria-controls="collapse${status.count}">
                  ${struttura.nome}
                </button>
              </h5>
            </div>
            <div id="collapse${status.count}" class="collapse ${show}" aria-labelledby="heading${status.count}" data-parent="#accordionStructures">
              <div class="card-body">
              <c:forEach var="uo_l2" items="${struttura.figlie}" varStatus="status">
                <c:out value="${uo_l2.nome}" /><br>
                <ul>
                  <c:forEach var="uo_l3" items="${uo_l2.figlie}" varStatus="status">
                    <li>
                      <c:out value="${uo_l3.nome}" />
                      <ul>
                        <c:forEach var="uo_l4" items="${uo_l3.figlie}" varStatus="status">
                          <li><c:out value="${uo_l4.nome}" /></li>
                        </c:forEach>
                      </ul>
                    </li>
                  </c:forEach>
                </ul>
              </c:forEach>
              </div>
            </div>
          </div>
          <c:set var="expanded" value="false" scope="page" />
          <c:set var="collapsed" value="collapsed" scope="page" />
          <c:set var="show" value="" scope="page" />
        </c:forEach>
        </div>
      </div>
    </div>
    --%>
    <div class="row" id="org-div">
      <h4 class="mt-1 m-0 font-weight-bold">Strutture coinvolte nel <span id="macro_or_processo"></span> <span id="cod_macro_or_processo"></span> <span class="textcolorred">(personale FTE)</span></h4>
      <hr class="riga"/>
      <div id="tab-dir" class="col-12">
        <script type="text/javascript">
          google.charts.load('current', {packages:["orgchart"]});
          google.charts.setOnLoadCallback(drawChart);
          function drawChart() {
            var data = new google.visualization.DataTable();
            data.addColumn('string', 'Name');
            data.addColumn('string', 'Manager');
            data.addColumn('string', 'ToolTip');
            // For each orgchart box, provide the name, manager, and tooltip to show.
            data.addRows([
              <c:forEach var="l1" items="${requestScope.listaStrutture}" begin="0" end="0">
              [{v:`${fn:trim(l1.nome)}_${l1.id}`, f:`${fn:trim(l1.nome)}<div class="textcolorred"><fmt:formatNumber value="${l1.fte}" minFractionDigits="2" /></div>`}, `Ateneo`, `<c:forEach var="p1" items="${l1.persone}">${p1.cognome} (${p1.note})\r\n</c:forEach>`],
                <c:forEach var="l2" items="${l1.figlie}">
              [{v:`${fn:trim(l2.nome)}_${l2.id}`, f:`${fn:trim(l2.nome)}<div class="textcolorred"><fmt:formatNumber value="${l2.fte}" minFractionDigits="2" /></div>`}, `${fn:trim(l1.nome)}_${l1.id}`, `<c:forEach var="p2" items="${l2.persone}">${p2.cognome} (${p2.note})\r\n</c:forEach>`],
                  <c:forEach var="l3" items="${l2.figlie}">
              [{v:`${fn:trim(l3.nome)}_${l3.id}`, f:`${fn:trim(l3.nome)}<div class="textcolorred"><fmt:formatNumber value="${l3.fte}" minFractionDigits="2" /></div>`}, `${fn:trim(l2.nome)}_${l2.id}`, `<c:forEach var="p3" items="${l3.persone}">${p3.cognome} (${p3.note})\r\n</c:forEach>`],
                    <c:forEach var="l4" items="${l3.figlie}">
              [{v:`${fn:trim(l4.nome)}_${l4.id}`, f:`${fn:trim(l4.nome)}<div class="textcolorred"><fmt:formatNumber value="${l4.fte}" minFractionDigits="2" /></div>`}, `${fn:trim(l3.nome)}_${l3.id}`, `<c:forEach var="p4" items="${l4.persone}">${p4.cognome} (${p4.note})\r\n</c:forEach>`],
                    </c:forEach>
                  </c:forEach>
                </c:forEach>
              </c:forEach>
              ]);
            // Create the chart.
            var chart = new google.visualization.OrgChart(document.getElementById('tab-dir'));
            // Draw the chart, setting the allowHtml option to true for the tooltips.
            chart.draw(data, {'allowHtml':true});
          }
        </script>
      </div>
      <hr class="riga"/>
      <div id="tab-scu" class="col-12">
        <script type="text/javascript">
          google.charts.load('current', {packages:["orgchart"]});
          google.charts.setOnLoadCallback(drawChart);
          function drawChart() {
            var data = new google.visualization.DataTable();
            data.addColumn('string', 'Name');
            data.addColumn('string', 'Manager');
            data.addColumn('string', 'ToolTip');
            // For each orgchart box, provide the name, manager, and tooltip to show.
            data.addRows([
              <c:forEach var="l1" items="${requestScope.listaStrutture}" begin="1" end="1">
              [{v:`${fn:trim(l1.nome)}_${l1.id}`, f:`${fn:trim(l1.nome)}<div class="textcolorred"><fmt:formatNumber value="${l1.fte}" minFractionDigits="2" /></div>`}, `Ateneo`, `<c:forEach var="p1" items="${l1.persone}">${p1.cognome} (${p1.note})\r\n</c:forEach>`],
                <c:forEach var="l2" items="${l1.figlie}">
              [{v:`${fn:trim(l2.nome)}_${l2.id}`, f:`${fn:trim(l2.nome)}<div class="textcolorred"><fmt:formatNumber value="${l2.fte}" minFractionDigits="2" /></div>`}, `${fn:trim(l1.nome)}_${l1.id}`, `<c:forEach var="p2" items="${l2.persone}">${p2.cognome} (${p2.note})\r\n</c:forEach>`],
                  <c:forEach var="l3" items="${l2.figlie}">
              [{v:`${fn:trim(l3.nome)}_${l3.id}`, f:`${fn:trim(l3.nome)}<div class="textcolorred"><fmt:formatNumber value="${l3.fte}" minFractionDigits="2" /></div>`}, `${fn:trim(l2.nome)}_${l2.id}`, `<c:forEach var="p3" items="${l3.persone}">${p3.cognome} (${p3.note})\r\n</c:forEach>`],
                    <c:forEach var="l4" items="${l3.figlie}">
              [{v:`${fn:trim(l4.nome)}_${l4.id}`, f:`${fn:trim(l4.nome)}<div class="textcolorred"><fmt:formatNumber value="${l4.fte}" minFractionDigits="2" /></div>`}, `${fn:trim(l3.nome)}_${l3.id}`, `<c:forEach var="p4" items="${l4.persone}">${p4.cognome} (${p4.note}) </c:forEach>`],
                    </c:forEach>
                  </c:forEach>
                </c:forEach>
              </c:forEach>
              ]);
            // Create the chart.
            var chart = new google.visualization.OrgChart(document.getElementById('tab-scu'));
            // Draw the chart, setting the allowHtml option to true for the tooltips.
            chart.draw(data, {'allowHtml':true});
          }
        </script>
      </div>
      <hr class="riga"/>
      <div id="tab-dip" class="col-12">
        <script type="text/javascript">
          google.charts.load('current', {packages:["orgchart"]});
          google.charts.setOnLoadCallback(drawChart);

          function drawChart() {
            var data = new google.visualization.DataTable();
            data.addColumn('string', 'Name');
            data.addColumn('string', 'Manager');
            data.addColumn('string', 'ToolTip');
            // For each orgchart box, provide the name, manager, and tooltip to show.
            data.addRows([
              <c:forEach var="l1" items="${requestScope.listaStrutture}" begin="2" end="2">
              [{v:`${fn:trim(l1.nome)}_${l1.id}`, f:`${fn:trim(l1.nome)}<div class="textcolorred"><fmt:formatNumber value="${l1.fte}" minFractionDigits="2" /></div>`}, `Ateneo`, `<c:forEach var="p1" items="${l1.persone}">${p1.cognome} (${p1.note})\r\n</c:forEach>`],
                <c:forEach var="l2" items="${l1.figlie}">
              [{v:`${fn:trim(l2.nome)}_${l2.id}`, f:`${fn:trim(l2.nome)}<div class="textcolorred"><fmt:formatNumber value="${l2.fte}" minFractionDigits="2" /></div>`}, `${fn:trim(l1.nome)}_${l1.id}`, `<c:forEach var="p2" items="${l2.persone}">${p2.cognome} (${p2.note})\r\n</c:forEach>`],
                  <c:forEach var="l3" items="${l2.figlie}">
              [{v:`${fn:trim(l3.nome)}_${l3.id}`, f:`${fn:trim(l3.nome)}<div class="textcolorred"><fmt:formatNumber value="${l3.fte}" minFractionDigits="2" /></div>`}, `${fn:trim(l2.nome)}_${l2.id}`, `<c:forEach var="p3" items="${l3.persone}">${p3.cognome} (${p3.note})\r\n</c:forEach>`],
                    <c:forEach var="l4" items="${l3.figlie}">
              [{v:`${fn:trim(l4.nome)}_${l4.id}`, f:`${fn:trim(l4.nome)}<div class="textcolorred"><fmt:formatNumber value="${l4.fte}" minFractionDigits="2" /></div>`}, `${fn:trim(l3.nome)}_${l3.id}`, `<c:forEach var="p4" items="${l4.persone}">${p4.cognome} (${p4.note})\r\n</c:forEach>`],
                    </c:forEach>
                  </c:forEach>
                </c:forEach>
              </c:forEach>
              ]);

            // Create the chart.
            var chart = new google.visualization.OrgChart(document.getElementById('tab-dip'));
            // Draw the chart, setting the allowHtml option to true for the tooltips.
            chart.draw(data, {'allowHtml':true});
          }
        </script>
      </div>
      <%--
      <hr class="separatore" />
      <div id="tab-per" class="col-12">
        Riepilogo Persone
        <hr class="riga"/>
        <table class="table table-striped thin" data-count="${requestScope.listaPersone.size()}" id="foundProducts">
          <thead>
            <tr>
              <th class="Content_Medio" width="10%">N&deg;</th>
              <th class="Content_Medio" width="35%">Nome</th>
              <th class="Content_Medio" width="35%">Cognome</th>
              <th class="Content_Medio" width="15%">Sesso</th>
              <th class="Content_Medio" width="5%">&nbsp;</th>
            </tr>
          </thead>
          <c:forEach var="persona" items="${requestScope.listaPersone}" varStatus="status">
          <tr>
            <td width="10%">
              <c:out value="${status.count}" />
            </td>
            <td width="35%">
              ${persona.nome}
            </td>
            <td width="35%">
              ${persona.cognome}
            </td>
            <td width="15%">
              ${persona.sesso}
            </td>
            <td align="center" width="5%">
              &nbsp;
            </td>
          </tr>
          </c:forEach>
        </table>
      </div>  --%>
    </div>
