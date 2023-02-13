<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="risks" value="${requestScope.rischi}" scope="page" />
    <h3 class="mt-1 m-0 font-weight-bold">Registro rischi corruttivi</h3>
    <hr class="riga"/>
    <div class="p-3 p-md-4 border rounded-3 icon-demo-examples errorPwd">
      <div class="fs-2 mb-3">
        Rischi
        <span class="float-right panel-body monospace">
          <cite>in parentesi: (n. di processi esposti a questo rischio)</cite>
        </span> 
      </div>
      <ul class="list-group">
      <c:forEach var="risk" items="${risks}" varStatus="status">
        <c:set var="bgAct" value="bgAct4" scope="page" />
        <c:if test="${status.index mod 2 eq 0}">
          <c:set var="bgAct" value="bgAct20" scope="page" />
        </c:if>
        <li class="list-group-item ${bgAct}">
        <c:set var="alarm" value="" scope="page" />
        <c:if test="${risk.impatto gt 2}">
          <c:set var="alarm" value="pHeader" scope="page" />
        </c:if>
          <a href="${initParam.appName}/?q=ri&idR=${risk.id}&r=${param['r']}">
          <c:out value="${risk.nome}" />
          <span class="float-right ${alarm}">
            (<c:out value="${risk.impatto}" />)
          </span>
          </a>
        </li>
      </c:forEach>
      </ul>
    </div>
    <h4 class="reportStateAct">&nbsp; N. rischi registro: 
      <button type="button" class="btn btn-danger">
        <span class="badge badge-pill badge-light">${risks.size()}</span>
      </button>
      <a href="${sqsCSV}" class="float-right lastMenuContent" title="Scarica il database completo del registro dei rischi corruttivi">
        <i class="fas fa-download"></i> <span class="sezioneElenco">Scarica tutti i dati&nbsp;</span>
      </a>
    </h4>

