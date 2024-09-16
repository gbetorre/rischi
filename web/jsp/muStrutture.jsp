<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="URL.jspf" %>
<c:set var="mats" value="${requestScope.macroProcessi}" scope="page" />
<c:set var="structs" value="${requestScope.strutture}" scope="page" />
<c:set var="subjs" value="${requestScope.soggetti}" scope="page" />
<c:set var="risks" value="${requestScope.rischi}" scope="page" />
<link rel="stylesheet" href="https://cdn.datatables.net/plug-ins/2.1.5/features/searchHighlight/dataTables.searchHighlight.css">
    <script>
    $(document).ready(function() {
        $('.refresh').on('click', function(event) {
          if (confirm("Ricorda di scaricare e consultare il log delle variazioni (\"Log ricalcolo\") PRIMA di effettuare un ricalcolo per verificare se le motivazioni del giudizio sintetico devono essere aggiornate. \n Vuoi ricalcolare ora gli indicatori?")) {
              window.location.replace("${mtr}&msg=refresh_ce");
          } 
          event.preventDefault(); // This will prevent the default behavior of the link
      });
    });
    </script>
    <h3 class="mt-1 m-0 font-weight-bold float-left">Report strutture e rischi</h3>
    <a href="${mtrRTF}" class="float-right badge badge-pill lightTable" title="Scarica il report in formato RTF">
      <i class="fas fa-download"></i>Scarica report
    </a>
    <a href="${mtrHTM}" class="float-right badge badge-pill lightTable bgAct19" title="Scarica il log delle differenze sopravvenute nei valori degli indicatori rispetto all'ultimo ricalcolo">
      <i class="fa-solid fa-book"></i> Log ricalcolo
    </a>
    <span class="float-right">
      <a href="${mtr}&msg=refresh_ce" type="button" class="btn btn-primary float-right refresh" style="padding-top:5px;height:30px" id="refresh" title="Effettua un ricalcolo dei valori di rischio di tutti i processi e li memorizza come nuovi valori cache">
        <i class="fa-solid fa-arrow-rotate-right"></i>
        Ricalcola
      </a>&nbsp;&nbsp;
    </span>
    <hr class="riga"/>
    <div class="col-md-offset-1">
      <div class="table-responsive">
        <table class="table table-striped table-bordered table-hover" id="listStr">
          <thead class="thead-light">
            <tr class="thin">
              <th width="15%">Macroprocesso</th>
              <th width="15%">Processo</th>
              <th width="25%">Rischi Potenziali</th>
              <th width="15%">Strutture</th>
              <th width="15%">Soggetti</th>
              <th width="15%">Giudizio sintetico</th>
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
                 <c:set var="strs" value="${structs.get(pat.id)}" scope="page" />
                  <c:forEach var="str" items="${strs}">
                  <li class="list-group-item">
                    <img src="${initParam.urlDirectoryImmagini}str-l${str.livello}.png" class="ico-small" alt="icona" title="Struttura di livello ${str.livello}" /> 
                    <c:out value="${str.prefisso} ${str.nome}" />
                  </li>
                  </c:forEach>
                </ul>
              </td>
              <td width="15%">
                <ul class="list-group list-group-flush">
                  <c:set var="subs" value="${subjs.get(pat.id)}" scope="page" />
                  <c:forEach var="sub" items="${subs}">
                  <li class="list-group-item">
                    <img src="${initParam.urlDirectoryImmagini}person-fill.png" class="ico-small" alt="icona" title="Soggetto contingente" /> 
                <c:choose>
                  <c:when test="${not empty sub.informativa}">
                    <c:out value="${sub.informativa}" />
                  </c:when>
                  <c:otherwise>
                    <c:out value="${sub.nome}" />
                  </c:otherwise>
                </c:choose>
                  </li>
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
            </tr>
            </c:forEach>
          </c:forEach>
          </tbody>
        </table>
      </div>
    </div>
<!--     <a href="javascript:DoPost()">test</a> -->
    &nbsp;
    <a href="${mtr}&msg=refresh_ce" type="button" class="btn btn-primary float-right refresh" id="refresh2" title="Effettua un ricalcolo dei valori di rischio di tutti i processi e li memorizza come nuovi valori cache">
      <i class="fa-solid fa-arrow-rotate-right"></i>
      Ricalcola
    </a>
    <script type="text/javascript">
      $(document).ready(function() {
        $('#listStr').DataTable({
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
    </script>
    <script type="text/javascript">
      function doGet(){
          if (confirm("Ricorda di consultare il log delle variazioni per verificare se le motivazioni del giudizio sintetico devono essere aggiornate. \n Vuoi ricalcolare gli indicatori?")) {
              //window.open("${mtr}");  //&msg=refresh_ce
              window.open("http://localhost:8080/rischi/data?q=mu&p=str&r=AT2022&out=html");  // Values must be in JSON format
              window.location.replace("${mtr}&msg=refresh_ce");
          } 
          return false;
        }
    </script>
    <script src="https://cdn.datatables.net/plug-ins/2.1.5/features/searchHighlight/dataTables.searchHighlight.min.js"></script>
    <script src="https://bartaz.github.io/sandbox.js/jquery.highlight.js"></script>
    