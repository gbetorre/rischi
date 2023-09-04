<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="URL.jspf" %>
<c:set var="risk" value="${requestScope.rischio}" scope="page" />
<c:catch var="exception">
    <h3 class="mt-1 m-0 font-weight-bold">Rischio corruttivo</h3>
    <hr class="riga"/>
    <div class="form-custom">
      <div class="panel-heading bgAct24">
        <div class="noHeader">
          <i class="fa-solid fa-triangle-exclamation" title="rischio corruttivo"></i>&nbsp; 
          <c:out value="${risk.nome}" />
        </div>
      </div>
      <hr class="separatore" />
      <div class="p-3 p-md-4 border rounded-3 icon-demo-examples successPwd">
        <div class="fs-2 mb-3">
          <i class="fas fa-cogs" title="processi"></i>&nbsp; 
          Processi esposti a questo rischio:
        </div>
        <ul class="list-group">
        <c:forEach var="pat" items="${risk.processi}" varStatus="status">
          <c:set var="bgAct" value="bgAct4" scope="page" />
          <c:if test="${status.index mod 2 eq 0}">
            <c:set var="bgAct" value="bgAct20" scope="page" />
          </c:if>
          <li class="list-group-item ${bgAct}">
            <a href="${initParam.appName}/?q=pr&p=pro&pliv=${pat.id}&liv=${pat.livello}&r=${param['r']}">
              <c:out value="${pat.nome}" />
            </a> 
            <span class="float-right">
              (Macroprocesso: <c:out value="${pat.padre.nome}" />)
            </span>
          </li>
        </c:forEach>
        </ul>
        <hr class="separatore" />
        <div class="centerlayout">
          <a href="${initParam.appName}/?q=ri&p=adp&idR=${risk.id}&r=${param['r']}" class="btn btn-success" id="btn-tar" title="Clicca per aggiungere un processo a questo rischio corruttivo">
            <i class="fa-solid fa-gear"></i> &nbsp;Associa Processo
          </a>
        </div>
      </div>
      <hr class="separatore" />
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
