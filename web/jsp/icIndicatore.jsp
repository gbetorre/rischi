<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="URL.jspf" %>
<c:set var="meas" value="${requestScope.misura}" scope="page" />
<c:forEach var="fase" items="${meas.fasi}">
  <c:if test="${fase.id eq param['idF']}">
    <c:set var="ind" value="${fase.indicatore}" scope="page" />
  </c:if>
</c:forEach>
  <c:catch var="exception">
    <h3 class="mt-1 m-0 font-weight-bold">Indicatore di monitoraggio</h3>
    <hr class="riga"/>
    <h4 class="p-2 bgAct15 rounded popupMenu heading"> 
      <i class="fa-solid fa-pen-ruler"></i>&nbsp;
      <c:out value="${ind.nome}" />
      <c:if test="${ind.master}">
      <span class="float-right">
        <img src="${initParam.urlDirectoryImmagini}ind-master.png" class="ico-func" alt="icona master" title="Indicatore di riferimento ai fini del monitoraggio" /> &nbsp;
      </span>
      </c:if>
    </h4>
    <div class="form-custom bgAct28">
      <dl class="sezioneElenco custom-dl">
        <dt class="text-primary">Tipo Indicatore</dt>
        <dd>
          <span class="badge border-basso textcolormaroon">
            <c:out value="${ind.tipo.nome}" />
          </span>
        </dd>
        <dt class="text-primary">Baseline</dt>
        <dd><c:out value="${fn:toUpperCase(ind.getLabel(ind.baseline))}" /></dd>
        <dt class="text-primary">Data Baseline</dt>
        <dd><fmt:formatDate value="${ind.dataBaseline}" pattern="dd/MM/yyyy" /></dd>
        <dt class="text-primary">Target</dt>
        <dd><c:out value="${fn:toUpperCase(ind.getLabel(ind.target))}" /></dd>
        <dt class="text-primary">Data Target</dt>
        <dd><fmt:formatDate value="${ind.dataTarget}" pattern="dd/MM/yyyy" /></dd>
        <dt class="text-primary">Indicatore Master</dt>
        <dd><c:out value="${ind.master}" /></dd>
      </dl>
      <h5 class="fw-bold text-dark border-bottom border-2 border-secondary">&nbsp;
        <i class="fa-solid fa-scroll"></i>&nbsp;&nbsp; 
        Descrizione
      </h5>
      <div class="alert alert-warning">
        <c:out value="${ind.descrizione}" />
      </div>
      <hr class="separatore" /><hr class="separapoco" />
      <h5 class="fw-bold text-dark border-bottom border-2 border-secondary">&nbsp;
        <i class="fa-regular fa-circle-right"></i>&nbsp;&nbsp;
        Fase dell'indicatore
      </h5>
      <ul class="line bgcolor-minimo">
        <li class="line bordo bordo-bgcolor1" title="fase di attuazione">&nbsp;&nbsp;&nbsp; 
          <c:out value="${ind.fase.nome}" />
        </li>
      </ul>
      <hr class="separatore" />
      <h5 class="fw-bold text-dark border-bottom border-2 border-secondary">&nbsp;
        <i class="fa-solid fa-umbrella" title="misura di prevenzione"></i>&nbsp;&nbsp;
        Misura cui questo indicatore &egrave; applicato
      </h5>    
      <ul class="line bgAct27">
        <li class="line bordo bordo-bgcolor2">&nbsp;&nbsp;
          <a href="${initParam.appName}/?q=ic&p=mes&mliv=${meas.codice}&r=${param['r']}" title="${meas.codice}">
            <c:out value="${meas.nome}" />
          </a>
        </li>
      </ul>
    </div>
  </c:catch>
  <c:out value="${exception}" />
