<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="URL.jspf" %>
<c:set var="risk" value="${requestScope.rischio}" scope="page" />
<c:catch var="exception">
    <h3 class="mt-1 m-0 font-weight-bold">Rischio corruttivo</h3>
    <hr class="riga"/>
    <%-- 
    <div class="row">
      <div class="col-xl-7 col-md-6 mx-auto">
        <h3 class="mt-1 m-0">Rischio corruttivo:</h3>
      </div>
      <div class="col-xl-5 col-md-6 mx-auto">
      <c:choose>
      <c:when test="${empty param['out']}">
        <a href="javascript:openWin('data?q=pr&p=pro&pliv=${input.get(0).value2AsInt}&liv=2&r=AT2022&out=pop')" title="Apri in una finestra separata per la stampa">
          <i class="fa-solid fa-arrow-up-right-from-square"></i> Apri in una nuova finestra
        </a>
        <a href="data?q=pr&p=pro&pliv=${input.get(0).value2AsInt}&liv=${param['liv']}&r=${param['r']}&out=csv" class="float-right" title="Scarica i dati del processo '${processo}'">
          <i class="fas fa-download"></i>Scarica i dati di questo rischio
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
    --%>
    <div class="form-custom">
      <h3 class="mt-1 m-0 bgAct24"><c:out value="${risk.nome}" /></h3> 
      <hr class="separatore" />
      <div class="p-3 p-md-4 border rounded-3 icon-demo-examples successPwd">
        <div class="fs-2 mb-3">Processi esposti a questo rischio:</div>
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
