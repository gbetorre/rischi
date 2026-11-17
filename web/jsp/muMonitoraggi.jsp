<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="URL.jspf" %>
<c:set var="mats" value="${requestScope.macroProcessi}" scope="page" />
<c:set var="risks" value="${requestScope.rischi}" scope="page" />
<c:set var="selectedYear" value="${param['y']}" />
<c:if test="${empty selectedYear}">
  <c:set var="selectedYear" value="${requestScope.theCurrentYear}" />
</c:if>
<c:choose>
  <c:when test="${selectedYear eq '2027'}">
    <c:set var="sel2027" value="selected" scope="page" />
  </c:when>
  <c:when test="${selectedYear eq '2026'}">
    <c:set var="sel2026" value="selected" scope="page" />
  </c:when>
  <c:when test="${selectedYear eq '2025'}">
    <c:set var="sel2025" value="selected" scope="page" />
  </c:when>
  <c:when test="${selectedYear eq '2024'}">
    <c:set var="sel2024" value="selected" scope="page" />
  </c:when>
  <c:when test="${selectedYear eq '2023'}">
    <c:set var="sel2023" value="selected" scope="page" />
  </c:when>
</c:choose>
    <link rel="stylesheet" href="https://cdn.datatables.net/plug-ins/2.1.5/features/searchHighlight/dataTables.searchHighlight.css">
    <h3 class="mt-1 m-0 font-weight-bold float-left">Report misure monitorate</h3>
    <!-- TODO: make dynamic -->
    <span class="form-custom bgAct3 float-right">
      <select id="myPlans" class="wide" onchange="viewPlan()">
        <option value="2027" ${sel2027}>2027</option>
        <option value="2026" ${sel2026}>2026</option>
        <option value="2025" ${sel2025}>2025</option>
        <option value="2024" ${sel2024}>2024</option>
        <option value="2023" ${sel2023}>2023</option>
      </select>
    </span>
    <hr class="riga"/>
    <div class="col-md-offset-1">
      <div class="table-responsive">
        <table class="table table-striped table-bordered table-hover" id="listMis">
          <thead class="thead-light">
            <tr class="thin">
              <th width="5%">Macroprocesso</th>
              <th width="14%">Processo</th>
              <th width="15%">Rischi Potenziali</th>
              <th width="15%">Misure Previste</th>
              <th width="15%">Misure Applicate</th>
              <th width="13%">Giudizio Sintetico</th>
              <th width="10%">PxI mitigato (stima)</th>
              <th width="13%">PxI mitigato (monitoraggio)</th>
            </tr>
          </thead>
          <tbody>
          <c:forEach var="mat" items="${mats}">
            <c:forEach var="pat" items="${mat.processi}" varStatus="status">
              <c:set var="misStima" value="" scope="page" />
              <c:set var="misMonit" value="" scope="page" />
            <tr class="active thin">
              <td class="verticalCenter reportRow">
                <c:out value="${mat.nome}" />
              </td>
              <td class="verticalCenter reportRow">
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
              <td>
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
              <td>
                <ul class="list-group list-group-flush">
                 <c:set var="rsks" value="${risks.get(pat)}" scope="page" />
                 <c:forEach var="rsk" items="${rsks}" varStatus="outerStatus">
                   <c:forEach var="mes" items="${rsk.misure}" varStatus="innerStatus">
                  <li class="list-group-item">
                    <img src="${initParam.urlDirectoryImmagini}mis-${mes.carattere.informativa}.png" class="ico-small" alt="icona" title="Misura ${mes.carattere.nome}" />
                    <a href="${initParam.appName}/?q=ic&p=mon&mliv=${mes.codice}&r=${param['r']}" title="Monitoraggio della misura ${mes.codice}">
                      <c:out value="${mes.nome}" />
                    </a>
                  </li>
                   </c:forEach>
                   <c:set var="misStima" value="${misStima + rsk.misure.size()}" scope="page" />
                 </c:forEach>
                </ul>
              </td>
              <td>
                <ul class="list-group list-group-flush">
                 <c:set var="rsks" value="${risks.get(pat)}" scope="page" />
                 <c:forEach var="rsk" items="${rsks}">
                   <c:forEach var="mes" items="${rsk.misureMonitorate}">
                  <li class="list-group-item">
                    <img src="${initParam.urlDirectoryImmagini}mis-${mes.carattere.informativa}.png" class="ico-small" alt="icona" title="Misura ${mes.carattere.nome}" /> 
                    <a href="${initParam.appName}/?q=ic&p=mon&mliv=${mes.codice}&r=${param['r']}" title="Monitoraggio della misura ${mes.codice}">
                      <c:out value="${mes.nome}" />
                    </a>
                  </li>
                   </c:forEach>
                   <c:set var="misMonit" value="${misMonit + rsk.misureMonitorate.size()}" scope="page" />
                 </c:forEach>
                </ul>
              </td>
              <td class="text-center reportRow bgcolor-${fn:toLowerCase(pat.indicatori.get('PxI').informativa)}">
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
              <td class="text-center verticalCenter reportRow bgcolor-${fn:toLowerCase(pat.indicatori.get('PxI (stima)').informativa)}">
                <c:out value="${pat.indicatori.get('PxI (stima)').informativa}" />
              </td>
              <td class="text-center reportRow bgcolor-${fn:toLowerCase(pat.indicatori.get('PxI (reale)').informativa)}">
                <c:out value="${pat.indicatori.get('PxI (reale)').informativa}" />
                <c:if test="${misStima gt zero}">
                <hr class="riga" />
                <div class="lightTable subfields file-data">
                  Misure previste &nbsp;<c:out value="${misStima}" /><br>
                  Misure applicate <c:out value="${misMonit}" />
                  <hr class="separapoco" />
                <c:choose>  
                  <c:when test="${misMonit lt misStima}">
                  <img src="${initParam.urlDirectoryImmagini}thumb_down.png" class="ico-small" alt="icona" title="Misure non applicate: ${misStima - misMonit}" /> 
                  </c:when>
                  <c:otherwise>
                  <img src="${initParam.urlDirectoryImmagini}thumb_up.png" class="ico-small" alt="good job" title="Tutte le misure previste sono state applicate" /> 
                  </c:otherwise>
                </c:choose>
                </div>
                </c:if>
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
        });
      });

      function viewPlan() {
        var y = document.getElementById("myPlans");
        showLoader();
        window.self.location.href = '${initParam.appName}/?q=ic&p=mes&r=${param['r']}&y=' + y.value;
      }
        
      function showLoader(){
          $('body').append('<div style="" id="loadingDiv"><div class="loader">Loading...</div></div>');
      }
  
      $(window).on('load', function(){
          setTimeout(removeLoader, 200); //wait for page load PLUS two seconds.
      });
  
      function removeLoader(){
          $( "#loadingDiv" ).fadeOut(500, function() {
            // fadeOut complete. Remove the loading div
            $( "#loadingDiv" ).remove(); //makes page more lightweight 
          });  
        }
    </script>
    <script src="https://cdn.datatables.net/plug-ins/2.1.5/features/searchHighlight/dataTables.searchHighlight.min.js"></script>
    <script src="https://bartaz.github.io/sandbox.js/jquery.highlight.js"></script>
