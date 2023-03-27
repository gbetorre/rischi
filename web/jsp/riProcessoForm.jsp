<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="URL.jspf" %>
<c:set var="p" value="${requestScope.persona}" scope="page" />
    <div class="row">
      <h3 class="mt-1 m-0 font-weight-bold"><c:out value="${p.nome} ${p.cognome}" /></h3>
      <hr class="riga"/>
      <dl class="row">
        <dt class="col-sm-3">Data di nascita</dt>
        <dd class="col-sm-9"><fmt:formatDate value="${p.dataNascita}" pattern="dd/MM/yyyy" /> (${p.eta} anni)</dd>
        <dt class="col-sm-3">Afferenza</dt>
        <dd class="col-sm-9"><c:out value="${p.dipartimento}" /></dd>
        <dt class="col-sm-3">Ruolo</dt>
        <dd class="col-sm-9"><c:out value="${p.codRuoloGiuridico}" /></dd>
        <dt class="col-sm-3">Impiego</dt>
        <dd class="col-sm-9">?</dd>
        <dt class="col-sm-3">Area funzionale</dt>
        <dd class="col-sm-9"><c:out value="${p.codAreaFunzionale}" /></dd>
        <dt class="col-sm-3">Tempo pieno</dt>
        <dd class="col-sm-9"><c:out value="${p.tempoPieno}" /></dd>
        <dt class="col-sm-3">Responsabile di struttura</dt>
        <dd class="col-sm-9"><c:out value="${p.responsabile}" /></dd>
        <dt class="col-sm-3">Resp. organizzativa</dt>
        <dd class="col-sm-9"><c:out value="${p.livResponsabilitaOrganizzativa}" /></dd>
        <dt class="col-sm-3">Funz. specialistica</dt>
        <dd class="col-sm-9"><c:out value="${p.livFunzioneSpecialistica}" /></dd>
      </dl>
    </div>
    <hr class="riga" />
  <c:if test="${not empty requestScope.macroprocessi}">
    <!-- Persone trovate -->
    <h3 class="bordo">Macroprocessi <button type="button" class="btn btn-success float-right"><span class="badge badge-pill  badge-light">${requestScope.macroprocessi.size()}</span></button></h3>
    <table class="table table-striped risultati" id="foundPerson">
      <thead>
        <tr>
          <th class="Content_Medio" width="5%">N. </th>
          <th class="Content_Medio" width="80%">Macroprocesso </th>
          <th class="Content_Medio" width="10%">Quotaparte </th>
          <th class="Content_Medio" width="5%">FTE </th>
        </tr>
      </thead>
      <c:forEach var="macro" items="${requestScope.macroprocessi}" varStatus="status">
      <tr>
        <td width="5%">
          <c:out value="${status.count}" />
        </td>
        <td width="80%">
          <c:out value="${macro.codice}" /> &ndash;
          <c:out value="${macro.nome}" escapeXml="false" />
          <c:if test="${not empty macro.processi}">
            <c:forEach var="pr" items="${macro.processi}">
            <div class="card">
              <div class="card-body">
                <h5 class="card-title">Sottoprocessi</h5>
                <h6 class="card-subtitle mb-2 text-muted">
                  <c:out value="${pr.codice}"/> &ndash; <c:out value="${pr.nome}"/> :
                   <i>Personale FTE: <fmt:formatNumber type="number" value="${pr.fte}" maxFractionDigits="2"/> |
                   Quotaparte: <fmt:formatNumber type="number" value="${pr.quotaParte}" maxFractionDigits="2"/>%</i>
                </h6>
              </div>
            </div>
           </c:forEach>
          </c:if>
        </td>
        <td width="10%" align="center">
          <fmt:formatNumber type="number" value="${macro.quotaParte}" maxFractionDigits="2"/>%
        </td>
        <td width="5%" align="center">
          <fmt:formatNumber type="number" value="${macro.fte}" maxFractionDigits="2"/>
        </td>
      </tr>
      </c:forEach>
    </table>
  </c:if>
