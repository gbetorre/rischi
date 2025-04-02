<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="URL.jspf" %>
<c:set var="inputs" value="${requestScope.listaInput}" scope="page" />
    <h3 class="mt-1 m-0 font-weight-bold">Input dei processi</h3>
    <hr class="riga"/>
    <div class="p-3 p-md-4 border rounded-3 icon-demo-examples bgAct5">
      <div class="fs-2 mb-3">
        Input
        <span class="float-right panel-body monospace">
          <cite>in parentesi: (n. di processi che usano questo input)</cite>
        </span> 
      </div>
      <ul class="list-group">
      <c:forEach var="input" items="${inputs}" varStatus="status">
        <c:set var="bgAct" value="bgAct4" scope="page" />
        <c:if test="${status.index mod 2 eq 0}">
          <c:set var="bgAct" value="bgAct20" scope="page" />
        </c:if>
        <li class="list-group-item ${bgAct}">
          <a href="#">
          <c:out value="${input.nome}" />
          <span class="float-right">
            (<fmt:formatNumber value="${input.value2}" minFractionDigits="0" />)
          </span>
          </a>
        </li>
      </c:forEach>
      </ul>
    </div>
    <h4 class="reportStateAct">&nbsp; N. input: 
      <button type="button" class="btn btn-danger">
        <span class="badge badge-pill badge-light">${inputs.size()}</span>
      </button>
    </h4>
    
    