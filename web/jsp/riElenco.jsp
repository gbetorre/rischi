<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="URL.jspf" %>
<c:set var="risks" value="${requestScope.rischi}" scope="page" />
    <h3 class="mt-1 m-0 font-weight-bold">Registro rischi corruttivi</h3>
    <a href="${riCSV}" class="float-right" title="Scarica il database completo del registro dei rischi corruttivi">
      <i class="fas fa-download"></i>Scarica tutti i dati
    </a>
    <hr class="riga"/>
    <div class="p-3 p-md-4 border rounded-3 icon-demo-examples errorPwd">
      <div class="fs-2 mb-3">
        Rischi
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
        <c:set var="explain" value="" scope="page" />
        <c:if test="${risk.impatto eq zero}">
          <c:set var="explain" value="Questo rischio &egrave; contrassegnato perch&eacute; non &egrave; ancora associato ad alcun processo" scope="page" />
          <c:set var="alarm" value="pHeader heading bgAct13 alert-danger" scope="page" />
          <i class="fa-solid fa-triangle-exclamation"></i>
        </c:if>
          <a href="${initParam.appName}/?q=ri&idR=${risk.id}&r=${param['r']}" class="${alarm}" title="${explain}">
          <c:out value="${risk.nome}" />
          <span class="float-right ${alarm}">
            (<c:out value="${risk.impatto}" />)
          </span>
          </a>
        </li>
      </c:forEach>
      </ul>
    </div>
    <h4 class="reportStateAct">&nbsp; N. rischi registro: 
      <button type="button" class="btn btn-danger">
        <span class="badge badge-pill badge-light">${risks.size()}</span>
      </button>
      <a href="${riCSV}" class="float-right lastMenuContent" title="Scarica il database completo del registro dei rischi corruttivi">
        <i class="fas fa-download"></i> <span class="sezioneElenco">Scarica tutti i dati&nbsp;</span>
      </a>
    </h4>
    <a href="${adr}" class="btn btn-success btn-lg btn-block" title="Aggiungi un nuovo rischio corruttivo al registro dei rischi">
      <i class="fa-solid fa-file-circle-plus"></i> &nbsp;Aggiungi Rischio
    </a>
