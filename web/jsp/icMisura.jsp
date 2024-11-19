<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="URL.jspf" %>
<%@ include file="msMisura.jsp"%>
  <c:if test="${meas.dettagli}">
    <style>
        ul {
            background-color: #f2f2f2;
        }
        ul li:hover {
            background-color: #e1e1ff;
        }
    </style>
    <div class="form-custom bg-note">
      <div class="panel-heading bgAct19" id="details">
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
              <c:forEach var="fase" items="${meas.fasi}" varStatus="status">
                <c:if test="${status.index gt zero}">
                  <c:set var="color" value="bordo-bgcolor${status.index}" scope="page" />
                </c:if>
                <li class="list-group-item bordo ${color}"><c:out value="${fase.nome}" />
                <c:choose>
                <c:when test="${not empty fase.indicatore}">
                  <div class="ico-func float-right">
                    <strong>Indicatore: </strong>
                    <a href="${initParam.appName}/?q=ic&p=ind&idI=${fase.indicatore.id}&idF=${fase.id}&mliv=${meas.codice}&r=${param['r']}" class="" title="Visualizza dettagli indicatore">
                      <c:out value="${fase.indicatore.nome}" />
                    </a>&nbsp;
                  </div>
                </c:when>
                <c:otherwise>
                  <div class="btn-group ico-func float-right">
                    <a href="${initParam.appName}/?q=ic&p=ini&idF=${fase.id}&mliv=${meas.codice}&r=${param['r']}" class="btn btn-primary" title="Aggiungi un indicatore alla misura &quot;${fn:substring(meas.nome, 0, 22)}...&quot; nel contesto della fase &quot;${fase.nome}&quot;">
                      <small><i class="fa-solid fa-square-plus"></i> Indicatore</small>
                    </a>&nbsp;
                  </div>
                </c:otherwise>
                </c:choose>      
                </li>
              </c:forEach>
              </ul>
            </dd>
          </dl>
        </div>
      </div>
    </div>
  </c:if>

