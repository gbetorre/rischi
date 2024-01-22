<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="URL.jspf" %>
<c:set var="mats" value="${requestScope.macroProcessi}" scope="page" />
<c:set var="structs" value="${requestScope.strutture}" scope="page" />
<c:set var="subjs" value="${requestScope.soggetti}" scope="page" />
<c:set var="risks" value="${requestScope.rischi}" scope="page" />
    <h3 class="mt-1 m-0 font-weight-bold float-left">Report strutture e rischi</h3>
    <a href="${mtrRTF}" class="float-right badge badge-pill lightTable" title="Scarica il report in formato RTF">
      <i class="fas fa-download"></i>Scarica in documento
    </a>
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
                  <c:set var="rsks" value="${risks.get(pat.id)}" scope="page" />
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
                <c:choose>
                  <c:when test="${str.livello eq 2}">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-diagram-3" viewBox="0 0 16 16">
                      <path fill-rule="evenodd" d="M6 3.5A1.5 1.5 0 0 1 7.5 2h1A1.5 1.5 0 0 1 10 3.5v1A1.5 1.5 0 0 1 8.5 6v1H14a.5.5 0 0 1 .5.5v1a.5.5 0 0 1-1 0V8h-5v.5a.5.5 0 0 1-1 0V8h-5v.5a.5.5 0 0 1-1 0v-1A.5.5 0 0 1 2 7h5.5V6A1.5 1.5 0 0 1 6 4.5v-1zM8.5 5a.5.5 0 0 0 .5-.5v-1a.5.5 0 0 0-.5-.5h-1a.5.5 0 0 0-.5.5v1a.5.5 0 0 0 .5.5h1zM0 11.5A1.5 1.5 0 0 1 1.5 10h1A1.5 1.5 0 0 1 4 11.5v1A1.5 1.5 0 0 1 2.5 14h-1A1.5 1.5 0 0 1 0 12.5v-1zm1.5-.5a.5.5 0 0 0-.5.5v1a.5.5 0 0 0 .5.5h1a.5.5 0 0 0 .5-.5v-1a.5.5 0 0 0-.5-.5h-1zm4.5.5A1.5 1.5 0 0 1 7.5 10h1a1.5 1.5 0 0 1 1.5 1.5v1A1.5 1.5 0 0 1 8.5 14h-1A1.5 1.5 0 0 1 6 12.5v-1zm1.5-.5a.5.5 0 0 0-.5.5v1a.5.5 0 0 0 .5.5h1a.5.5 0 0 0 .5-.5v-1a.5.5 0 0 0-.5-.5h-1zm4.5.5a1.5 1.5 0 0 1 1.5-1.5h1a1.5 1.5 0 0 1 1.5 1.5v1a1.5 1.5 0 0 1-1.5 1.5h-1a1.5 1.5 0 0 1-1.5-1.5v-1zm1.5-.5a.5.5 0 0 0-.5.5v1a.5.5 0 0 0 .5.5h1a.5.5 0 0 0 .5-.5v-1a.5.5 0 0 0-.5-.5h-1z"/>
                    </svg>
                  </c:when>
                  <c:when test="${str.livello eq 3}">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-diagram-2" viewBox="0 0 16 16">
                      <path fill-rule="evenodd" d="M6 3.5A1.5 1.5 0 0 1 7.5 2h1A1.5 1.5 0 0 1 10 3.5v1A1.5 1.5 0 0 1 8.5 6v1H11a.5.5 0 0 1 .5.5v1a.5.5 0 0 1-1 0V8h-5v.5a.5.5 0 0 1-1 0v-1A.5.5 0 0 1 5 7h2.5V6A1.5 1.5 0 0 1 6 4.5v-1zM8.5 5a.5.5 0 0 0 .5-.5v-1a.5.5 0 0 0-.5-.5h-1a.5.5 0 0 0-.5.5v1a.5.5 0 0 0 .5.5h1zM3 11.5A1.5 1.5 0 0 1 4.5 10h1A1.5 1.5 0 0 1 7 11.5v1A1.5 1.5 0 0 1 5.5 14h-1A1.5 1.5 0 0 1 3 12.5v-1zm1.5-.5a.5.5 0 0 0-.5.5v1a.5.5 0 0 0 .5.5h1a.5.5 0 0 0 .5-.5v-1a.5.5 0 0 0-.5-.5h-1zm4.5.5a1.5 1.5 0 0 1 1.5-1.5h1a1.5 1.5 0 0 1 1.5 1.5v1a1.5 1.5 0 0 1-1.5 1.5h-1A1.5 1.5 0 0 1 9 12.5v-1zm1.5-.5a.5.5 0 0 0-.5.5v1a.5.5 0 0 0 .5.5h1a.5.5 0 0 0 .5-.5v-1a.5.5 0 0 0-.5-.5h-1z"/>
                    </svg>
                  </c:when>
                  <c:when test="${str.livello eq 4}">
                   <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-diagram-2-fill" viewBox="0 0 16 16">
                    <path fill-rule="evenodd" d="M6 3.5A1.5 1.5 0 0 1 7.5 2h1A1.5 1.5 0 0 1 10 3.5v1A1.5 1.5 0 0 1 8.5 6v1H11a.5.5 0 0 1 .5.5v1a.5.5 0 0 1-1 0V8h-5v.5a.5.5 0 0 1-1 0v-1A.5.5 0 0 1 5 7h2.5V6A1.5 1.5 0 0 1 6 4.5v-1zm-3 8A1.5 1.5 0 0 1 4.5 10h1A1.5 1.5 0 0 1 7 11.5v1A1.5 1.5 0 0 1 5.5 14h-1A1.5 1.5 0 0 1 3 12.5v-1zm6 0a1.5 1.5 0 0 1 1.5-1.5h1a1.5 1.5 0 0 1 1.5 1.5v1a1.5 1.5 0 0 1-1.5 1.5h-1A1.5 1.5 0 0 1 9 12.5v-1z"/>
                   </svg>
                  </c:when>
                </c:choose>
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
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-person-fill" viewBox="0 0 16 16" title="Soggetto non rappresentato in organigramma">
                      <path d="M3 14s-1 0-1-1 1-4 6-4 6 3 6 4-1 1-1 1H3Zm5-6a3 3 0 1 0 0-6 3 3 0 0 0 0 6Z"/>
                    </svg>
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
                  Nota:<br />
                  <span class="file-data">
                    <c:out value="${pat.indicatori.get('PxI').note}" />
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
    &nbsp;
    <a href="${mtr}&msg=refresh_ce" type="button" class="btn btn-primary float-right" value="Update">
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
          "aaSorting": [[ 1, "asc" ]]
        });
      });
    </script>
    