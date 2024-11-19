<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"  %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="URL.jspf" %>
<c:set var="meas" value="${requestScope.misura}" scope="page" />
<c:catch var="exception">
    <h5 class="p-2 bgAct17 rounded popupMenu heading">
      <i class="fa-solid fa-umbrella ico-home" title="misura di prevenzione"></i>&nbsp; 
      <c:out value="${meas.nome}" />
    </h5>
    <hr class="separatore" />
    <ul class="nav nav-tabs responsive" role="tablist" id="tabs-0">
      <li class="nav-item"><a class="nav-link active tabactive" data-toggle="tab" href="#">Indicatori</a></li>
      <li class="nav-item"><a class="nav-link"  href="${initParam.appName}/?q=ic&p=mon&mliv=${meas.codice}&r=${param['r']}">Misurazioni</a></li>
      <li class="nav-item"><a class="nav-link" data-toggle="tab" href="">Report</a></li>
    </ul>
    <hr class="separatore" />
  <c:choose>
    <c:when test="${not empty meas.fasi}">
    <table class="table table-bordered table-hover" id="listInd">
      <thead class="thead-light">
        <tr>
          <th scope="col" width="20%">Fase</th>
          <th scope="col" width="*">Nome Indicatore</th>
          <th scope="col" width="6%">Baseline</th>
          <th scope="col" width="9%">Data Baseline</th>
          <th scope="col" width="6%">Target</th>
          <th scope="col" width="9%">Data Target</th>
          <th scope="col" width="5%">Tipo Indicatore</th>
          <th scope="col" width="5%"><div class="text-center">Misurato</div></th>
        </tr>
      </thead>
      <tbody>
      <c:set var="status" value="" scope="page" />
      <c:forEach var="fase" items="${meas.fasi}" varStatus="loop">
        <c:set var="status" value="${loop.index}" scope="page" />
        <fmt:formatDate var="lastModified" value="${fase.indicatore.dataUltimaModifica}" pattern="dd/MM/yyyy" />
        <input type="hidden" id="ind-id${status}" name="ind-id${status}" value="<c:out value="${fase.indicatore.id}"/>">
        <tr>
          <td scope="row">
            <a href="">
              <c:out value="${fase.nome}"/>
            </a>
          </td>
          <td scope="row" id="nameColumn" class="success bgAct${fase.indicatore.tipo.id} bgFade ico-func">
          <c:choose>
          <c:when test="${not empty fase.indicatore}">
            <a href="${initParam.appName}/?q=ic&p=ind&idI=${fase.indicatore.id}&idF=${fase.id}&mliv=${meas.codice}&r=${param['r']}" title="Modificato:${lastModified} ${fn:substring(fase.indicatore.oraUltimaModifica,0,5)}">
              <c:out value="${fase.indicatore.nome}"/>
            </a>
          </c:when>
          <c:otherwise>
            <div class="btn-group align-items-center">
              <a href="${initParam.appName}/?q=ic&p=ini&idF=${fase.id}&mliv=${meas.codice}&r=${param['r']}" type="button" class="badge bg-primary btn-small lightTable text-white  align-middle refresh" title="Aggiungi un indicatore alla misura &quot;${fn:substring(meas.nome, 0, 22)}...&quot; nel contesto della fase &quot;${fase.nome}&quot;">
                <i class="fa-solid fa-square-plus"></i> INDICATORE
              </a>&nbsp;
            </div>
          </c:otherwise>
          </c:choose>          
          </td>
          <td scope="row">
            <c:out value="${fase.indicatore.baseline}" />
          </td>
          <td scope="row">
            <fmt:formatDate value="${fase.indicatore.dataBaseline}" pattern="dd/MM/yyyy" />
          </td>
          <td scope="row">
            <c:out value="${fase.indicatore.target}" />
          </td>
          <td scope="row">
            <fmt:formatDate value="${fase.indicatore.dataTarget}" pattern="dd/MM/yyyy" />
          </td>
          <td scope="row">
            <c:out value="${fase.indicatore.tipo.nome}"/>
          </td>
        <c:choose>
          <c:when test="${false}">
            <td scope="row" class="bgcolorgreen">
              <div class="form-check text-center">
                <span>
                  <a href="" title="Clicca per visualizzare le misurazioni">SI</a>
                  <span class="badge badge-dark">
                    <c:out value="20" />
                  </span>
                </span>
              </div>
            </td>
          </c:when>
          <c:otherwise>
            <td scope="row" class="bgcolorred">
              <div class="form-check text-center">
                <span>NO</span>
              </div>
            </td>
          </c:otherwise>
        </c:choose>
        </tr>
      </c:forEach>
      </tbody>
    </table>
    </c:when>
    <c:otherwise>
    <div class="alert alert-danger">
      <p>Non sono stati trovati indicatori associati alla misura monitorata.</p>
    </div>
    </c:otherwise>
  </c:choose>
    <div id="container-fluid">
      <div class="row">
        <div class="col-2">
          <span class="float-left">
            <a class="btn btnNav" href="${initParam.appName}/?q=ic&p=mes&r=${param['r']}">
              <i class="fas fa-home"></i>
              Monitoraggio
            </a>
          </span>
        </div>
        <div class="col-8 text-center">

        </div>
      </div>
    </div>
</c:catch>
<c:out value="${exception}" />
