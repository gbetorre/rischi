<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="URL.jspf" %>
<c:set var="facts" value="${requestScope.fattori}" scope="page" />
    <h3 class="mt-1 m-0 font-weight-bold">Registro dei fattori abilitanti</h3>
    <%-- a href="${riCSV}" class="float-right" title="Scarica il database completo del registro dei rischi corruttivi">
      <i class="fas fa-download"></i>Scarica tutti i dati
    </a> --%>
    <hr class="riga"/>
    <div class="p-3 p-md-4 border rounded-3 icon-demo-examples btn-warning">
    <%--
      <div class="fs-2 mb-3">
        Rischi &nbsp;
        <a href="${adr}" class="btn btn-success btn-lg" title="Aggiungi un nuovo rischio corruttivo al registro dei rischi">
          <i class="fa-solid fa-file-circle-plus"></i> &nbsp;Aggiungi Rischio
        </a>
        <span class="float-right panel-body monospace">
          <cite>in parentesi: (n. di processi esposti a questo rischio)</cite>
        </span> 
      </div>
       --%>
      <ul class="list-group">
      <c:forEach var="fat" items="${facts}" varStatus="status">
        <c:set var="bgAct" value="bgAct4" scope="page" />
        <c:if test="${status.index mod 2 eq 0}">
          <c:set var="bgAct" value="bgAct20" scope="page" />
        </c:if>
        <li class="list-group-item ${bgAct}">
          <c:out value="${fat.nome}" />
        </li>
      </c:forEach>
      </ul>
    </div>
    <%--
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
    --%>
    
