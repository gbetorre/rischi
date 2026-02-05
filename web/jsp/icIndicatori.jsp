<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"  %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="URL.jspf" %>
<c:set var="meas" value="${requestScope.misura}" scope="page" />
<c:set var="totI" value="${zero}" scope="page" />
<c:catch var="exception">
    <h5 class="p-2 bgAct17 rounded popupMenu heading">
      <i class="fa-solid fa-umbrella ico-home" title="misura di prevenzione"></i>&nbsp; 
      <a href="${initParam.appName}/?q=ic&p=mes&mliv=${meas.codice}&r=${param['r']}" title="Dettagli della misura ${meas.codice}">
        <c:out value="${meas.nome}" />
      </a>
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
          <th scope="col" width="5%">Indicatore Master</th>
          <th scope="col" width="5%"><div class="text-center">Misurato</div></th>
        </tr>
      </thead>
      <tbody>
      <c:set var="status" value="" scope="page" />
      <c:forEach var="fase" items="${meas.fasi}" varStatus="loop">
        <c:set var="status" value="${loop.index}" scope="page" />
        <fmt:formatDate var="lastModified" value="${fase.indicatore.dataUltimaModifica}" pattern="dd/MM/yyyy" />
        <tr>
          <td scope="row">
            <a href="">
              <c:out value="${fase.nome}"/>
            </a>
          </td>
          <td scope="row" id="nameColumn" class="success bgAct${fase.indicatore.tipo.id} bgFade ico-func">
          <c:choose>
          <c:when test="${not empty fase.indicatore}">
            <c:set var="totI" value="${totI + 1}" scope="page" />
            <a href="${initParam.appName}/?q=ic&p=ind&idI=${fase.indicatore.id}&idF=${fase.id}&mliv=${meas.codice}&r=${param['r']}" title="Modificato:${lastModified} ${fn:substring(fase.indicatore.oraUltimaModifica,0,5)}">
              <c:out value="${fase.indicatore.nome}"/>
            </a>
            <c:if test="${fase.indicatore.master}">
              <img src="${initParam.urlDirectoryImmagini}ind-master.png" class="imgTop" alt="icona master" title="Indicatore di riferimento ai fini del monitoraggio" /> &nbsp;
            </c:if>
          </c:when>
          <c:otherwise>
            <div class="btn-group align-items-center">
              <a href="${initParam.appName}/?q=ic&p=ini&idF=${fase.id}&mliv=${meas.codice}&r=${param['r']}" type="button" class="badge bg-success btn-small lightTable text-white  align-middle refresh" title="Aggiungi un indicatore alla misura &quot;${fn:substring(meas.nome, 0, 22)}...&quot; nel contesto della fase &quot;${fase.nome}&quot;">
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
            <span class="badge border-basso textcolormaroon">
              <c:out value="${fase.indicatore.tipo.nome}"/>
            </span>
          </td>
          <c:set var="master" value="NO" scope="page" />
          <c:set var="bgcolor" value="bgcolor-non" scope="page" />
          <c:if test="${fase.indicatore.master}">
            <c:set var="master" value="SI" scope="page" />
            <c:set var="bgcolor" value="bgAct5" scope="page" />
          </c:if>
          <td scope="row" class="${bgcolor}">
            <div class="form-check text-center">
              <strong><c:out value="${master}" /></strong>
            </div>
          </td>
      <c:if test="${not empty fase.indicatore}">
        <c:choose>
          <c:when test="${fase.indicatore.totMisurazioni gt zero}">
            <td scope="row" class="bgcolorgreen">
              <div class="form-check text-center">
                <strong>
                  <a href="${initParam.appName}/?q=ic&p=mon&mliv=${meas.codice}&r=${param['r']}" title="Clicca per visualizzare le misurazioni">
                    SI&nbsp;
                    <span class="badge badge-warning align-items-center border-basso">
                      <c:out value="${fase.indicatore.totMisurazioni}" />
                    </span>
                  </a>
                </strong>
              </div>
            </td>
          </c:when>
          <c:otherwise>
            <td scope="row" class="bgcolorred">
              <div class="form-check text-center">
                <strong>NO</strong>
                <div class="btn-group align-items-center border-basso">
                  <a href="${initParam.appName}/?q=ic&p=imm&idI=${fase.indicatore.id}&idF=${fase.id}&mliv=${meas.codice}&r=${param['r']}" type="button" class="badge bgAct11 btn-small lightTable text-black align-middle refresh" title="Clicca per misurare questo indicatore">
                    <i class="fa-solid fa-pen-to-square"></i> MISURA
                  </a>
                </div>
              </div>
            </td>
          </c:otherwise>
        </c:choose>
      </c:if>
        </tr>
      </c:forEach>
      </tbody>
    </table>
    <div class="avvisiTot text-right">Tot indicatori: <c:out value="${totI}" /></div>
    </c:when>
    <c:otherwise>
    <div class="alert alert-danger">
      <p>Non sono stati trovati indicatori associati alla misura monitorata.</p>
    </div>
    </c:otherwise>
  </c:choose>
</c:catch>
<c:out value="${exception}" />
