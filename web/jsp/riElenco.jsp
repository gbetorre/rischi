<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="URL.jspf" %>
<c:set var="risks" value="${requestScope.rischi}" scope="page" />
    <h3 class="mt-1 m-0 font-weight-bold float-left">Registro dei rischi corruttivi</h3>
    <a href="${riCSV}" class="float-right badge badge-pill lightTable bgAct20" title="Scarica il database completo del registro dei rischi corruttivi">
      <i class="fas fa-download"></i>Scarica tutti i dati
    </a>
    <hr class="riga"/>
    <c:if test="${param['msg'] eq 'newRel'}">
    <script>
      $(function () { 
          var duration = 4000; // 4 seconds
          setTimeout(function () { $('#mainAlertMessage').hide(); }, duration);
      });
    </script>
    <div id="mainAlertMessage" class="alert alert-success alert-dismissible" role="alert">
      Nuova associazione tra rischio e processo creata con successo
    </div>
    </c:if>
    <div class="p-3 p-md-4 border rounded-3 icon-demo-examples errorPwd">
      <div class="fs-2 mb-3">
        Rischi &nbsp;
        <a href="${adr}" class="btn btn-success btn-lg" title="Aggiungi un nuovo rischio corruttivo al registro dei rischi">
          <i class="fa-solid fa-file-circle-plus"></i> &nbsp;Aggiungi Rischio
        </a>
        <span class="float-right panel-body monospace">
          <cite>in parentesi: (n. di processi esposti a questo rischio)</cite>
        </span> 
      </div>
      <ul class="list-group">
      <c:forEach var="risk" items="${risks}" varStatus="status">
        <c:set var="bgAct" value="bgAct4" scope="page" />
        <c:if test="${status.index mod 2 eq 0}">
          <c:set var="bgAct" value="bgAct20" scope="page" />
        </c:if>
        <li class="list-group-item ${bgAct}">
        <c:set var="alarm" value="" scope="page" />
        <c:set var="explain" value="Clicca per visualizzare i dettagli del rischio" scope="page" />
        <c:if test="${risk.impatto eq zero}">
          <c:set var="explain" value="Questo rischio &egrave; contrassegnato perch&eacute; non &egrave; ancora associato ad alcun processo" scope="page" />
          <c:set var="alarm" value="pHeader heading bgAct13 alert-danger" scope="page" />
          <i class="fa-solid fa-triangle-exclamation"></i>
        </c:if>
          <a href="${initParam.appName}/?q=ri&idR=${risk.id}&r=${param['r']}" class="${alarm}" title="${explain}">
            <c:out value="${risk.nome}" />
          </a>
          <span class="float-right ${alarm}">
            <a href="${initParam.appName}/?q=ri&p=adp&idR=${risk.id}&r=${param['r']}" id="btn-tar" title="Clicca per associare un processo a questo rischio">
              (<c:out value="${risk.impatto}" />)&nbsp;&nbsp;
              <i class="fa-regular fa-square-plus"></i>&nbsp;
            </a>
          </span>
        </li>
      </c:forEach>
      </ul>
    </div>    
    <h4 class="reportStateAct">&nbsp; N. rischi registro: 
      <button type="button" class="btn btn-danger">
        <span class="badge">${risks.size()}</span>
      </button>
      <a href="${riCSV}" class="float-right badge badge-pill lightTable bgAct20" title="Scarica il database completo del registro dei rischi corruttivi">
        <i class="fas fa-download"></i> <span class="sezioneElenco">Scarica tutti i dati&nbsp;</span>
      </a>
    </h4>
    <a href="${adr}" class="btn btn-success btn-lg btn-block" title="Aggiungi un nuovo rischio corruttivo al registro dei rischi">
      <i class="fa-solid fa-file-circle-plus"></i> &nbsp;Aggiungi Rischio
    </a>
