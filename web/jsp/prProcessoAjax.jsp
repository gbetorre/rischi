<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="input" value="${requestScope.listaInput}" scope="page" />
<c:set var="fasi" value="${requestScope.listaFasi}" scope="page" />
<c:set var="output" value="${requestScope.listaOutput}" scope="page" />
<c:catch var="exception">
  <c:set var="processo" value="informazione non disponibile" scope="page" />
  <c:if test="${not empty input}">
    <c:set var="processo" value="${input.get(0).labelWeb}" scope="page" />
  </c:if>
  <c:set var="arearischio" value="informazione non disponibile" scope="page" />
  <c:if test="${not empty input}">
    <c:set var="arearischio" value="${input.get(0).nomeReale}" scope="page" />
  </c:if>
    <h3>Processo: <c:out value="${processo}" /></h3>
    <div class="subfields ">Area di rischio: <span class="file-data"><c:out value="${arearischio}" /></span>
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
            <c:out value="${soggetto.nome}" /><br />
          </c:forEach>
          </td>
        </tr>
        </c:forEach>
      </table>
      <hr class="separatore" />
      <div class="p-3 p-md-4 border rounded-3 icon-demo-examples successPwd">
        <div class="fs-2 mb-3">Output:</div>
        <ul class="list-group">
        <c:forEach var="output" items="${output}" varStatus="status">
          <c:set var="bgAct" value="bgAct4" scope="page" />
          <c:if test="${status.index mod 2 eq 0}">
            <c:set var="bgAct" value="bgAct20" scope="page" />
          </c:if>
          <li class="list-group-item ${bgAct}"><c:out value="${output.nome}" /></li>
        </c:forEach>
        </ul>
      </div>
    </div>
</c:catch>
<c:out value="${exception}" />
