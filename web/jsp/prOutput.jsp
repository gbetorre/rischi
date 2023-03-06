<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="URL.jspf" %>
<c:set var="output" value="${requestScope.output}" scope="page" />
<c:catch var="exception">
    <h3 class="mt-1 m-0 font-weight-bold">Output di processo</h3>
    <hr class="riga"/>
    <div class="form-custom">
      <div class="panel-heading bgAct11">
        <div class="noHeader">
          <i class="fa-solid fa-right-from-bracket" title="output"></i>&nbsp; 
          <c:out value="${output.nome}" />
        </div>
      </div>
      <hr class="separatore" />
      <div class="p-3 p-md-4 border rounded-3 icon-demo-examples successPwd">
        <div class="fs-2 mb-3">
          <i class="fas fa-cogs" title="processi"></i>&nbsp; 
          Processi generati da questo output:
          <span class="float-right" title="I processi generati da questo output sono i processi che prendono questo output come proprio input">
            <i class="fa-solid fa-circle-question"></i>
          </span>
        </div>
      <c:choose>
      <c:when test="${not empty output.processi}">
        <ul class="list-group">
        <c:forEach var="pat" items="${output.processi}" varStatus="status">
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
      </c:when>
      <c:otherwise>
        <div class="alert alert-danger">Nessuno</div>
      </c:otherwise>
      </c:choose>
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
