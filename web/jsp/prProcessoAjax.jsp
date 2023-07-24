<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
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
<c:catch var="exception">
  <c:set var="processo" value="informazione non disponibile" scope="page" />
  <c:if test="${not empty input}">
    <c:set var="processo" value="${input.get(0).labelWeb}" scope="page" />
  </c:if>
  <c:set var="arearischio" value="informazione non disponibile" scope="page" />
  <c:if test="${not empty input}">
    <c:set var="arearischio" value="${input.get(0).nomeReale}" scope="page" />
  </c:if>
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
        <h3 class="mt-1 m-0">Processo: <c:out value="${processo}" /></h3>
      </div>
      <div class="col-xl-5 col-md-6 mx-auto">
      <c:choose>
      <c:when test="${empty param['out']}">
        <a href="javascript:openWin('data?q=pr&p=pro&pliv=${input.get(0).value2AsInt}&liv=2&r=AT2022&out=pop')" title="Apri in una finestra separata per la stampa">
          <i class="fa-solid fa-arrow-up-right-from-square"></i> Apri in una nuova finestra
        </a>
        <a href="data?q=pr&p=pro&pliv=${input.get(0).value2AsInt}&liv=${param['liv']}&r=${param['r']}&out=csv" class="float-right" title="Scarica i dati del processo '${processo}'">
          <i class="fas fa-download"></i>Scarica i dati di questo processo
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
      <span class="breadcrumb-item bgcolor3"><c:out value="${arearischio}" /></span>
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
      <h3 class="bordo">
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
            <c:out value="${fase.nome}" />
          </td>
          <td width="40%">
          <c:forEach var="struttura" items="${fase.strutture}">
            <a href="#">
              <c:out value="${struttura.prefisso}" /> <c:out value="${struttura.nome}" />
            </a><br />
          </c:forEach>
          <c:forEach var="soggetto" items="${fase.soggetti}">
          <c:choose>
          <c:when test="${empty soggetto.informativa}">
            <c:out value="${soggetto.nome}" />
          </c:when>
          <c:otherwise>
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
              <c:out value="${outp.nome}" />
            </a>
          </li>
        </c:forEach>
        </ul>
      </div>
      <hr class="separatore" />
      <div class="p-3 p-md-4 border rounded-3 icon-demo-examples errorPwd">
        <h3 class="bordo">
          Rischi e fattori abilitanti 
          <button type="button" class="btn btn-danger float-right">
            <span class="badge badge-pill badge-light"><c:out value="${risks.size()}" /></span>
          </button>
        </h3>
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
                <c:out value="${risk.nome}" />
              </a>
            </td>
            <td width="40%">
              <ul class="list-group">
            <c:forEach var="fat" items="${risk.fattori}">
              <li class="list-group-item popupMenu textcolormaroon"><c:out value="${fat.nome}" /></li>
            </c:forEach>
              </ul>
            </td>
            <td width="15%" class="text-center"><%--
              <a href="#" class="btn btn-primary" title="Aggiungi un fattore abilitante al rischio &quot;${fn:substring(risk.nome, 0, 22)}...&quot; nel contesto del processo &quot;${processo}&quot;">
                <small><i class="fas fa-plus"></i> Fattore</small>
              </a> --%>
            </td>
          </tr>
          </c:forEach>
        </table>
      </div>
    </div>
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
