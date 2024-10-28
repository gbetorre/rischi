<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="URL.jspf" %>
<%@ include file="msMisura.jsp"%>
    <div class="form-custom bg-note">
      <div class="panel-heading bgAct19">
        <h5 class="fw-bold text-dark">
          <i class="fa-solid fa-file-circle-plus"></i>
          <c:out value="Dettagli monitoraggio" />
        </h5>
      </div>
      <div class="panel-body">
        <div class="row">
          <dl class=" custom-dl">
            <dt class="text-primary">Data inserimento</dt>
            <dd><fmt:formatDate value="${meas.dataMonitoraggio}" pattern="dd/MM/yyyy" /></dd>
            <dt class="text-primary">Obiettivo PIAO</dt>
            <dd><c:out value="${meas.obiettivo}" escapeXml="false" /></dd>
            <dt class="text-primary">Fasi di attuazione</dt>
            <dd>
              <ul class="list-group">
              <c:forEach var="fase" items="${meas.fasi}">
                <li class="list-group-item"><c:out value="${fase.nome}" />
                <div class="btn-group">
                <a href="${initParam.appName}/?q=pr&p=adf&idR=${risk.id}&pliv=${param['pliv']}&liv=2&r=${param['r']}" class="btn btn-primary " title="Aggiungi un indicatore alla misura &quot;${fn:substring(meas.nome, 0, 22)}...&quot; nel contesto della fase &quot;${fase.nome}&quot;">
                  <small><i class="fa-solid fa-square-plus"></i> Indicatore</small>
                </a>&nbsp;</div>
                </li>
              </c:forEach>
              </ul>
            </dd>
          </dl>
        </div>
      </div>
    </div>

