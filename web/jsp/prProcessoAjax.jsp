<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="URL.jspf" %>
<c:if test="${not empty param['out']}">
  <c:if test="${param['out'] eq 'pop'}">
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css" />
    <link rel="stylesheet" href="<c:out value="${initParam.urlDirectoryStili}" />style.css" type="text/css" />
    <link rel="stylesheet" href="https://use.fontawesome.com/releases/v6.1.1/css/all.css" integrity="sha384-/frq1SRXYH/bSyou/HUp/hib7RVN1TawQYja658FEOodR/FQBKVqT9Ol+Oz3Olq5" crossorigin="anonymous">
  </c:if>
</c:if> 
<c:set var="input" value="${requestScope.listaInput}" scope="page" />
<c:set var="fasi" value="${requestScope.listaFasi}" scope="page" />
<c:set var="output" value="${requestScope.listaOutput}" scope="page" />
<c:set var="risks" value="${requestScope.listaRischi}" scope="page" />
<c:set var="interviews" value="${requestScope.listaInterviste}" scope="page" />
<c:set var="indics" value="${requestScope.listaIndicatori}" scope="page" />
<c:catch var="exception">
  <c:set var="processo" value="informazione non disponibile" scope="page" />
  <c:set var="arearischio" value="informazione non disponibile" scope="page" />
<c:choose>
  <c:when test="${not empty input}">
    <c:set var="processo" value="${input.get(0).labelWeb}" scope="page" />
    <c:set var="arearischio" value="${input.get(0).nomeReale}" scope="page" />
    <script type="text/javascript">
      function openPrint(){
        myWindow = window.open('','','width=200,height=100');
        myWindow.document.write("<p>This is 'myWindow'</p>");
        myWindow.focus();
        print(myWindow);
      }

      function openWin() {
        var token = "data?q=pr" ; // -> Command token
        var idP = "<c:out value='${input.get(0).value2AsInt}' />";  // -> id processo (lo recupera dal primo input)
        var lev =  <c:out value="${param['liv']}" />;  // -> livello: (2 = processo_at | 3 = sottoprocesso_at)
        var url = token + "&p=pro&pliv=" + idP + "&liv=" + lev + "&r=${param['r']}" + "&out=pop";
        // e.g.: /data?q=pr&p=pro&pliv=#&liv=#&r=$
        myWindow = window.open(url, "_blank", "toolbar=no,scrollbars=yes,resizable=no,top=400,left=500,width=1024,height=768");
      }
      
      function openWin(url) {
        myWindow = window.open(url, "_blank", "toolbar=no,scrollbars=yes,resizable=no,top=400,left=500,width=1024,height=768");
      }

      function closeWin() {
        myWindow.close();
      }
      
      let myWindow;
    </script>
    <div class="row">
      <div class="col-xl-7 col-md-6 mx-auto">
        <h3 class="mt-1 m-0">Processo: <a href="${initParam.appName}/?q=pr&p=pro&pliv=${input.get(0).value2AsInt}&liv=2&r=${param['r']}"><c:out value="${processo}" /></a></h3>
      </div>
      <div class="col-xl-5 col-md-6 mx-auto">
      <c:choose>
      <c:when test="${empty param['out']}">
        <a href="data?q=pr&p=pro&pliv=${input.get(0).value2AsInt}&liv=${param['liv']}&r=${param['r']}&out=csv" class="float-right badge badge-pill lightTable bgAct20" title="Scarica i dati del processo '${processo}'">
          <i class="fas fa-download"></i>Scarica i dati 
        </a>
        <a href="javascript:openWin('data?q=pr&p=pro&pliv=${input.get(0).value2AsInt}&liv=2&r=AT2022&out=pop')" class="float-right badge badge-pill lightTable bgAct23" title="Apri in una finestra separata per la stampa">
          <i class="fa-solid fa-arrow-up-right-from-square"></i> Apri in finestra
        </a>
      </c:when>
      <c:when test="${not empty param['out'] and (param['out'] eq 'pop')}">
        <a href="javascript:print()" title="Anteprima di stampa">
          <i class="fas fa-print"></i> Stampa
        </a>
      </c:when>
      </c:choose>
      </div>
    </div>
    <div class="subfields errorPwd">
      Area di rischio: 
      <span class="badge lightTable bgAct25"><c:out value="${arearischio}" /></span>
      <hr class="separatore" />
      <div class="p-3 p-md-4 border rounded-3 icon-demo-examples info">
        <div class="fs-2 mb-3">Input:</div>
        <ul class="list-group">
        <c:forEach var="input" items="${input}" varStatus="status">
          <c:set var="bgAct" value="bgAct4" scope="page" />
          <c:if test="${status.index mod 2 eq 0}">
            <c:set var="bgAct" value="bgAct20" scope="page" />
          </c:if>
          <li class="list-group-item ${bgAct}"><c:out value="${input.nome}" /></li>
        </c:forEach>
        </ul>
      </div>
      <hr class="separatore" />
      <h3 class="bordo" id="fasi">
        Fasi del processo 
        <button type="button" class="btn btn-success float-right">
          <span class="badge badge-pill badge-light"><c:out value="${fasi.size()}" /></span>
        </button>
      </h3>
      <table class="table table-striped risultati" id="foundPerson">
        <thead>
          <tr>
            <th width="5%" align="center">N. </th>
            <th width="55%" align="center">Fase </th>
            <th width="40%" align="center">Struttura <small>e/o</small> Soggetto interessato </th>
          </tr>
        </thead>
        <c:forEach var="fase" items="${fasi}" varStatus="status">
        <tr>
          <td width="5%">
            <c:out value="${status.count}" />
          </td>
          <td width="55%">
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-arrow-right-circle" viewBox="0 0 16 16"  title="${fase.id}">
              <path fill-rule="evenodd" d="M1 8a7 7 0 1 0 14 0A7 7 0 0 0 1 8zm15 0A8 8 0 1 1 0 8a8 8 0 0 1 16 0zM4.5 7.5a.5.5 0 0 0 0 1h5.793l-2.147 2.146a.5.5 0 0 0 .708.708l3-3a.5.5 0 0 0 0-.708l-3-3a.5.5 0 1 0-.708.708L10.293 7.5H4.5z"/>
            </svg>
            <c:out value="${fase.nome}" />
          </td>
          <td width="40%">
          <c:forEach var="struttura" items="${fase.strutture}">
            <a href="#" title="Struttura di organigramma">
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-diagram-2-fill" viewBox="0 0 16 16">
                <path fill-rule="evenodd" d="M6 3.5A1.5 1.5 0 0 1 7.5 2h1A1.5 1.5 0 0 1 10 3.5v1A1.5 1.5 0 0 1 8.5 6v1H11a.5.5 0 0 1 .5.5v1a.5.5 0 0 1-1 0V8h-5v.5a.5.5 0 0 1-1 0v-1A.5.5 0 0 1 5 7h2.5V6A1.5 1.5 0 0 1 6 4.5v-1zm-3 8A1.5 1.5 0 0 1 4.5 10h1A1.5 1.5 0 0 1 7 11.5v1A1.5 1.5 0 0 1 5.5 14h-1A1.5 1.5 0 0 1 3 12.5v-1zm6 0a1.5 1.5 0 0 1 1.5-1.5h1a1.5 1.5 0 0 1 1.5 1.5v1a1.5 1.5 0 0 1-1.5 1.5h-1A1.5 1.5 0 0 1 9 12.5v-1z"/>
              </svg>
              <c:out value="${struttura.prefisso}" /> <c:out value="${struttura.nome}" />
            </a><br />
          </c:forEach>
          <c:forEach var="soggetto" items="${fase.soggetti}">
          <c:choose>
          <c:when test="${empty soggetto.informativa}">
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-person-fill" viewBox="0 0 16 16" title="Soggetto non rappresentato in organigramma">
              <path d="M3 14s-1 0-1-1 1-4 6-4 6 3 6 4-1 1-1 1H3Zm5-6a3 3 0 1 0 0-6 3 3 0 0 0 0 6Z"/>
            </svg>
            <c:out value="${soggetto.nome}" />
          </c:when>
          <c:otherwise>
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-person-fill" viewBox="0 0 16 16" title="Soggetto non rappresentato in organigramma">
              <path d="M3 14s-1 0-1-1 1-4 6-4 6 3 6 4-1 1-1 1H3Zm5-6a3 3 0 1 0 0-6 3 3 0 0 0 0 6Z"/>
            </svg>
            <c:out value="${soggetto.informativa}" />
          </c:otherwise>
          </c:choose>
            <br />
          </c:forEach>
          </td>
        </tr>
        </c:forEach>
      </table>
      <hr class="separatore" />
      <div class="p-3 p-md-4 border rounded-3 icon-demo-examples bgAct">
        <div class="fs-2 mb-3">Output:</div>
        <ul class="list-group">
        <c:forEach var="outp" items="${output}" varStatus="status">
          <c:set var="bgAct" value="bgAct4" scope="page" />
          <c:if test="${status.index mod 2 eq 0}">
            <c:set var="bgAct" value="bgAct20" scope="page" />
          </c:if>
          <li class="list-group-item ${bgAct}">
            <a href="${initParam.appName}/?q=pr&p=out&idO=${outp.id}&r=${param['r']}">
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-box-arrow-right" viewBox="0 0 16 16">
                <path fill-rule="evenodd" d="M10 12.5a.5.5 0 0 1-.5.5h-8a.5.5 0 0 1-.5-.5v-9a.5.5 0 0 1 .5-.5h8a.5.5 0 0 1 .5.5v2a.5.5 0 0 0 1 0v-2A1.5 1.5 0 0 0 9.5 2h-8A1.5 1.5 0 0 0 0 3.5v9A1.5 1.5 0 0 0 1.5 14h8a1.5 1.5 0 0 0 1.5-1.5v-2a.5.5 0 0 0-1 0v2z"/>
                <path fill-rule="evenodd" d="M15.854 8.354a.5.5 0 0 0 0-.708l-3-3a.5.5 0 0 0-.708.708L14.293 7.5H5.5a.5.5 0 0 0 0 1h8.793l-2.147 2.146a.5.5 0 0 0 .708.708l3-3z"/>
              </svg>
              <c:out value="${outp.nome}" />
            </a>
          </li>
        </c:forEach>
        </ul>
      </div>
      <hr class="separatore" />
      <section id="rischi">
      <div class="p-3 p-md-4 border rounded-3 icon-demo-examples errorPwd">
        <h3 class="bordo">
          Rischi e fattori abilitanti 
          <button type="button" class="btn btn-danger float-right">
            <span class="badge badge-pill badge-light"><c:out value="${risks.size()}" /></span>
          </button>
        </h3>
        <c:if test="${param['msg'] eq 'newRel'}">
        <script>
          $(function () { 
              var duration = 4000; // 4 seconds
              setTimeout(function () { $('#mainAlertMessage').hide(); }, duration);
          });
        </script>
        <div id="mainAlertMessage" class="alert alert-success alert-dismissible" role="alert">
          Un nuovo fattore abilitante &egrave; stato aggiunto ai rischi!
        </div>
        </c:if>
        <table class="table table-striped risultati" id="foundRisk">
          <thead>
            <tr>
              <th width="45%">Rischio </th>
              <th width="40%">Fattori abilitanti</th>
              <th width="15%" class="text-center">Funzioni</th>
            </tr>
          </thead>
          <c:forEach var="risk" items="${risks}" varStatus="status">
          <tr class="bgAct20">
            <td width="45%">
              <a href="${initParam.appName}/?q=ri&idR=${risk.id}&r=${param['r']}">
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="maroon" class="bi bi-exclamation-circle" viewBox="0 0 16 16">
                  <path d="M8 15A7 7 0 1 1 8 1a7 7 0 0 1 0 14zm0 1A8 8 0 1 0 8 0a8 8 0 0 0 0 16z"/>
                  <path d="M7.002 11a1 1 0 1 1 2 0 1 1 0 0 1-2 0zM7.1 4.995a.905.905 0 1 1 1.8 0l-.35 3.507a.552.552 0 0 1-1.1 0L7.1 4.995z"/>
                </svg>&nbsp;
                <c:out value="${risk.nome}" />
              </a>
            </td>
            <td width="40%">
              <ul class="list-group">
            <c:forEach var="fat" items="${risk.fattori}">
              <li class="list-group-item textcolormaroon">
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-check2-square" viewBox="0 0 16 16">
                  <path d="M3 14.5A1.5 1.5 0 0 1 1.5 13V3A1.5 1.5 0 0 1 3 1.5h8a.5.5 0 0 1 0 1H3a.5.5 0 0 0-.5.5v10a.5.5 0 0 0 .5.5h10a.5.5 0 0 0 .5-.5V8a.5.5 0 0 1 1 0v5a1.5 1.5 0 0 1-1.5 1.5H3z"/>
                  <path d="m8.354 10.354 7-7a.5.5 0 0 0-.708-.708L8 9.293 5.354 6.646a.5.5 0 1 0-.708.708l3 3a.5.5 0 0 0 .708 0z"/>
                </svg>&nbsp;
                <c:out value="${fat.nome}" />
              </li>
            </c:forEach>
              </ul>
            </td>
            <td width="15%" class="text-center">
              <a href="${initParam.appName}/?q=pr&p=adf&idR=${risk.id}&pliv=${param['pliv']}&liv=2&r=${param['r']}" class="btn btn-primary" title="Aggiungi un fattore abilitante al rischio &quot;${fn:substring(risk.nome, 0, 22)}...&quot; nel contesto del processo &quot;${processo}&quot;">
                <small><i class="fa-solid fa-circle-plus"></i> Fattore</small>
              </a>
            </td>
          </tr>
          </c:forEach>
        </table>
      </div>
      </section>
      <hr class="separatore" />
    <c:choose>
      <c:when test="${empty interviews}">
        <c:set var="surveyLabel" value="nessuna intervista" scope="page" />
      </c:when>
      <c:when test="${interviews.size() eq 1}">
        <c:set var="surveyLabel" value="una intervista" scope="page" />
      </c:when>
      <c:otherwise>
        <c:set var="surveyLabel" value="${interviews.size()} interviste" scope="page" />
      </c:otherwise>
    </c:choose>
      <div class="p-3 p-md-4 border rounded-3 icon-demo-examples bgAct13">
        <div class="fs-2 mb-3">Valutazione del rischio:</div>
        <ol class="breadcrumb mb-4">
          <li class="breadcrumb-item active">
            Il processo &egrave; stato esaminato in&nbsp;
            <span class="badge heading bgAct5 textcolormaroon"><c:out value="${surveyLabel}" /></span>
          <c:if test="${not empty interviews}">
            &nbsp;totalizzando i seguenti risultati negli indicatori di rischio
          </c:if>
          </li>
        </ol>
        <section>
          <div class="errorPwd">
          <c:forEach var="interview" items="${interviews}" varStatus="status">
            <fmt:formatDate var="iviewsqldate" value="${interview.dataUltimaModifica}" pattern="yyyy-MM-dd" scope="page" />
            <fmt:formatDate var="iviewsqltime" value="${interview.oraUltimaModifica}" pattern="HH_mm_ss" scope="page" />
            <c:url var="rqsInstance" context="${initParam.appName}" value="/" scope="page">
              <c:param name="q" value="in" />
              <c:param name="p" value="rqs" />
              <c:param name="sliv1" value="${interview.struttura.informativa}" />
              <c:param name="sliv2" value="${interview.struttura.figlie.get(zero).informativa}" />
              <c:param name="sliv3" value="${interview.struttura.figlie.get(zero).figlie.get(zero).informativa}" />
              <c:param name="sliv4" value="${interview.struttura.figlie.get(zero).figlie.get(zero).figlie.get(zero).informativa}" />
              <c:param name="pliv1" value="${interview.processo.informativa}" />
              <c:param name="pliv2" value="${interview.processo.processi.get(zero).informativa}" />
              <c:param name="pliv3" value="${interview.processo.processi.get(zero).processi.get(zero).informativa}" />
              <c:param name="d" value="${iviewsqldate}" />
              <c:param name="t" value="${iviewsqltime}" />
              <c:param name="r" value="${ril}" />
            </c:url>
            <fmt:formatDate var="iviewitadate" value="${interview.dataUltimaModifica}" pattern="dd/MM/yyyy" /> 
            <fmt:formatDate var="iviewsqltime" value="${interview.oraUltimaModifica}" pattern="HH:mm" scope="page" />
            <c:if test="${status.index gt zero}">
            <hr class="riga" />
            </c:if>
            <ol class="breadcrumb mb-3">
              <li><a href="${rqsInstance}">Intervista del <c:out value="${iviewitadate}" /> <c:out value="${iviewsqltime}" /></a></li>
            </ol>
            <dl class="row">
            <c:set var="keys" value="${interview.indicatori.keySet()}" />
            <c:forEach var="key" items="${keys}">
              <c:set var="indicatore" value="${interview.indicatori.get(key)}" />
              <dt class="col-sm-5">
                <c:out value="${indicatore.nome}" /> &ndash; <em><c:out value="${indicatore.descrizione}" />:</em>
              </dt> 
              <dd class="col-sm-7">Rischio <c:out value="${indicatore.informativa}" /></dd>
            </c:forEach>
            </dl>
          </c:forEach>
          <c:choose>
            <c:when test="${not empty interviews}">
            <hr class="separatore" />
            <div id="rischi">
              <h4 class="btn-lightgray">&nbsp;Valori <c:if test="${interviews.size() gt 1}">complessivi </c:if>di rischio</h4>
              <table class="table table-bordered table-hover">
                <thead class="thead-light">
                  <tr>
                    <th>Indicatore</th>
                    <th>Elementi connessi</th>
                    <th>Tipologia</th>
                    <th>Livello</th>
                  </tr>
                </thead>
                <tbody>
                <c:set var="keys" value="${indics.keySet()}" />
                <c:forEach var="key" items="${keys}">
                  <c:set var="ind" value="${indics.get(key)}" />
                  <c:if test="${(ind.nome eq 'I1') or (ind.nome eq 'P') or (ind.nome eq 'PxI')}">
                  <tr>
                    <td colspan="4"><hr class="riga" /></td>
                  </tr>
                  </c:if>
                  <tr class="selected">
                    <td><c:out value="${ind.nome}" />: <c:out value="${ind.descrizione}" /></td>
                    <td>
                    <c:set var="comma" value="," scope="page" />
                    <c:forEach var="quesito" items="${ind.risposte}" varStatus="status">
                      <c:if test="${status.count eq ind.risposte.size()}">
                        <c:set var="comma" value="" scope="page" />
                      </c:if>
                      <span title="ID${quesito.id} = ${quesito.formulazione}"><c:out value="${quesito.codice}" /></span><c:out value="${comma}" />
                    </c:forEach>
                      <a href="#fasi" title="Vedi dettagli delle fasi e relative strutture/soggetti">
                        <c:out value="${ind.autoreUltimaModifica}" />
                      </a>
                    </td>
                    <td><c:out value="${ind.processo.tipo}" /></td>
                    <c:set var="classSuffix" value="${fn:toLowerCase(ind.informativa)}" scope="page" />
                    <c:if test="${fn:indexOf(classSuffix, ' ') gt -1}">
                      <c:set var="classSuffix" value="${fn:substring(classSuffix, zero, fn:indexOf(classSuffix, ' '))}" scope="page" />
                    </c:if>
                    <td class="text-center bgcolor-${classSuffix}" title="${ind.processo.descrizioneStatoCorrente}">
                      <c:out value="${ind.informativa}" />
                    </td>
                  </tr>
                </c:forEach>
                  <tr class="selected">
                    <td colspan="4" class="center text-center verticalCenter reportRow" align="center" title="Aggiungi/modifica motivazione">
                      <div class="lightTable subfields">
                        Motivazione:<br /><span class="textcolormaroon"><c:out value="${ind.note}" escapeXml="false" />
                          <a href="${initParam.appName}/?q=pr&p=pin&pliv=${param['pliv']}&liv=2&pxi=${indics.get('PxI').informativa}&r=${param['r']}&ref=pro" class="" title="Modifica motivazione PxI">
                            <i class="fa-regular fa-pen-to-square"></i>
                          </a>
                        </span>
                      </div>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
            </c:when>
            <c:otherwise>
            Processo non oggetto di intervista: indicatori non calcolabili!
            </c:otherwise>
          </c:choose>
          </div><br />
        </section>
      </div>
    </div>
  </c:when>
  <c:otherwise>
  <div class="alert alert-danger">
    <strong>Il processo non &egrave; ancora stato dettagliato.</strong>
    <hr class="separapoco" />
    <p>
      Dati carenti: non &egrave; ancora possibile mostrare la pagina.<br/>
      Inserire almeno gli input di processo.
    </p>
  </div>
  </c:otherwise>
</c:choose>
</c:catch>
<c:if test="${not empty exception}">
  <div class="alert alert-danger">
    <strong>Spiacente!</strong>
    <p>
      Si &egrave; verificato un problema<br/>
      <c:out value="${exception}" />
    </p>
  </div>
</c:if>
