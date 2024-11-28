<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="URL.jspf" %>
<c:set var="mats" value="${requestScope.macroProcessi}" scope="page" />
<c:set var="risks" value="${requestScope.rischi}" scope="page" />
    <link rel="stylesheet" href="https://cdn.datatables.net/plug-ins/2.1.5/features/searchHighlight/dataTables.searchHighlight.css">
    <h3 class="mt-1 m-0 font-weight-bold float-left">Report misure e rischi</h3>
    <a href="${mesCSV}" class="float-right badge badge-pill lightTable bgAct20" title="Scarica il dataset dei processi organizzativi con PxI iniziale e PxI mitigato (stima)">
      <i class="fas fa-download"></i>Scarica i dati
    </a>
    <hr class="riga"/>
    <div class="col-md-offset-1">
      <div class="table-responsive">
        <table class="table table-striped table-bordered table-hover" id="listMis">
          <thead class="thead-light">
            <tr class="thin">
              <th width="15%">Macroprocesso</th>
              <th width="15%">Processo</th>
              <th width="25%">Rischi Potenziali</th>
              <th width="15%">Misure</th>
              <th width="15%">Giudizio sintetico</th>
              <th width="15%">PxI mitigato (stima)</th>
            </tr>
          </thead>
          <tbody>
          <c:forEach var="mat" items="${mats}">
            <c:forEach var="pat" items="${mat.processi}" varStatus="status">
            <tr class="active thin">
              <td width="15%" class="verticalCenter reportRow">
                <c:out value="${mat.nome}" />
              </td>
              <td width="15%" class="verticalCenter reportRow">
                <a href="${initParam.appName}/?q=pr&p=pro&pliv=${pat.id}&liv=${pat.livello}&r=${param['r']}" title="Vedi dettagli Processo">
                  <c:out value="${pat.nome}" />
                </a>
                <hr class="riga" />
                <div class="lightTable subfields">
                  Area di rischio:<br />
                  <span class="textcolormaroon">
                    <c:out value="${pat.areaRischio}" />
                  </span>
                </div>
              </td>
              <td width="25%">
                <ul class="list-group list-group-flush">
                  <c:set var="rsks" value="${risks.get(pat)}" scope="page" />
                  <c:forEach var="rsk" items="${rsks}">
                  <li class="list-group-item list-group-item-danger">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-exclamation-triangle" viewBox="0 0 16 16">
                      <path d="M7.938 2.016A.13.13 0 0 1 8.002 2a.13.13 0 0 1 .063.016.146.146 0 0 1 .054.057l6.857 11.667c.036.06.035.124.002.183a.163.163 0 0 1-.054.06.116.116 0 0 1-.066.017H1.146a.115.115 0 0 1-.066-.017.163.163 0 0 1-.054-.06.176.176 0 0 1 .002-.183L7.884 2.073a.147.147 0 0 1 .054-.057zm1.044-.45a1.13 1.13 0 0 0-1.96 0L.165 13.233c-.457.778.091 1.767.98 1.767h13.713c.889 0 1.438-.99.98-1.767L8.982 1.566z"/>
                      <path d="M7.002 12a1 1 0 1 1 2 0 1 1 0 0 1-2 0zM7.1 5.995a.905.905 0 1 1 1.8 0l-.35 3.507a.552.552 0 0 1-1.1 0L7.1 5.995z"/>
                    </svg>&nbsp; 
                    <a href="${initParam.appName}/?q=ri&idR=${rsk.id}&r=${param['r']}" title="Vai alla pagina di questo rischio">
                      <c:out value="${rsk.nome}" />
                    </a>
                  </li>
                  </c:forEach>
                </ul>
              </td>
              <td width="15%">
                <ul class="list-group list-group-flush">
                 <c:set var="rsks" value="${risks.get(pat)}" scope="page" />
                 <c:forEach var="rsk" items="${rsks}">
                   <c:forEach var="mes" items="${rsk.misure}">
                  <li class="list-group-item">
                    <img src="${initParam.urlDirectoryImmagini}mis-${mes.carattere.informativa}.png" class="ico-small" alt="icona" title="Misura ${mes.carattere.nome}" /> 
                    <c:out value="${mes.nome}" />
                  </li>
                   </c:forEach>
                 </c:forEach>
                </ul>
              </td>
              <td width="15%" class="text-center verticalCenter reportRow bgcolor-${fn:toLowerCase(pat.indicatori.get('PxI').informativa)}">
                <c:out value="${pat.indicatori.get('PxI').informativa}" />
                <hr class="riga" />
                <div class="lightTable subfields">
                  Motivazione:<br />
                  <span class="file-data">
                    <c:out value="${pat.indicatori.get('PxI').note}" escapeXml="false" />
                    <a href="${initParam.appName}/?q=pr&p=pin&pliv=${pat.id}&liv=2&pxi=${pat.indicatori.get('PxI').informativa}&r=${param['r']}&ref=str" class="" title="Modifica nota PxI">
                      <i class="fa-regular fa-pen-to-square"></i>
                    </a>
                  </span>
                </div>
              </td>
              <td width="15%" class="text-center verticalCenter reportRow bgcolor-${fn:toLowerCase(pat.indicatori.get('PxI (stima)').informativa)}">
                <c:out value="${pat.indicatori.get('PxI (stima)').informativa}" />
              </td>
            </tr>
            </c:forEach>
          </c:forEach>
          </tbody>
        </table>
      </div>
    </div>
    <script type="text/javascript">
      $(document).ready(function() {
          var table = $('#listMis').DataTable({
          "ordering": true,  
          "paging": false,
          "bInfo": false,
          "oLanguage": {
              "sSearch": "Filtra:"
              },
          "searchPanes": {
              "viewTotal": false
              },
          "aaSorting": [[ 1, "asc" ]],
          "searchHighlight": true
        });/*
        table.on('draw', function () {
            var body = $( table.table().body() );
            body.unhighlight();
            body.highlight( table.search() );  
        });*/
      });
    </script>
    <script src="https://cdn.datatables.net/plug-ins/2.1.5/features/searchHighlight/dataTables.searchHighlight.min.js"></script>
    <script src="https://bartaz.github.io/sandbox.js/jquery.highlight.js"></script>
