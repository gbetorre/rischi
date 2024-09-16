<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="URL.jspf" %>
<c:set var="risks" value="${requestScope.rischi}" scope="page" />
<c:catch var="exception">
    <style>
    .grid {
      display: grid;
      grid-template-columns: repeat(9,160px);
      gap: 8px;
      color: #fff;
      height: 600px;
      resize: both;
      overflow: auto;
    }
    .extra-wide {
      width:320px;
    }
    </style>
    <h3 class="mt-1 m-0 font-weight-bold float-left">Report misure e rischi</h3>    
    <hr class="riga"/>
    <div class="grid scrollX">
      <!-- columns header -->
      <div class="entry root text-center extra-wide">RISCHIO</div>
      <div class="entry head text-center"></div>
      <div class="entry head text-center verticalCenter textcolorred panel"><span>PxI</span></div>
      <div class="entry head text-center"><span>Misure previste</span></div>
      <div class="entry head text-center"><span>Mitigazione (stima)</span></div>
      <div class="entry head text-center"><span>PxI (stima)</span></div>
      <div class="entry head text-center"><span>Misure applicate</span></div>
      <div class="entry head text-center"><span>Mitigazione effettiva</span></div>
      <div class="entry head text-center panel"><span>PxI effettivo</span></div>
      <!-- normal cells -->
    <c:forEach var="entry" items="${risks.entrySet()}">
    <c:set var="pat" value="${entry.getKey()}" scope="page" />
      <c:set var="classSuffix" value="${fn:toLowerCase(pat.indicatori.get('PxI').informativa)}" scope="page" />
      <c:if test="${fn:indexOf(classSuffix, ' ') gt -1}">
        <c:set var="classSuffix" value="${fn:substring(classSuffix, zero, fn:indexOf(classSuffix, ' '))}" scope="page" />
      </c:if>
      <div class="entry head bgAct4 textcolormaroon extra-wide" title="${pat.nome}">
        <c:out value="PROCESSO: ${pat.nome}" />
        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-arrow-down-short float-right" viewBox="0 0 16 16">
          <path fill-rule="evenodd" d="M8 4a.5.5 0 0 1 .5.5v5.793l2.146-2.147a.5.5 0 0 1 .708.708l-3 3a.5.5 0 0 1-.708 0l-3-3a.5.5 0 1 1 .708-.708L7.5 10.293V4.5A.5.5 0 0 1 8 4"/>
        </svg>       
      </div>
      <div class="entry bgAct4"></div>
      <div class="value bgcolor-${classSuffix} border-${classSuffix}">
        <p class="text-center" title="${pat.indicatori.get('PxI').descrizione}">
          <c:out value="${pat.indicatori.get('PxI').informativa}" />
        </p>
      </div>
      <div class="entry bgAct4"></div>
      <div class="entry bgAct4"></div>
      <div class="value bgcolor-${fn:toLowerCase(pat.indicatori.get('PxI (stima)').informativa)} border-${fn:toLowerCase(pat.indicatori.get('PxI (stima)').informativa)}">
        <p class="text-center" title="PxI Ricalcolato">
          <c:out value="${pat.indicatori.get('PxI (stima)').informativa}" />
        </p>
      </div>
      <div class="entry bgAct4"></div>
      <div class="entry bgAct4"></div>
      <div class="entry bgAct4"></div>
      <c:forEach var="risk" items= "${risks.get(pat)}" varStatus="status">
      <div class="entry head bgAct4 extra-wide">           
        <a href="${initParam.appName}/?q=pr&p=pro&pliv=${pat.id}&liv=${pat.livello}&r=${param['r']}#rischi-fattori-misure">
          <c:out value="${status.count}) ${risk.nome}" />
        </a>
      </div>
      <div class="entry"></div>
      <div class="value bgcolor-${classSuffix}">
        <p class="text-center" title="${pat.indicatori.get('PxI').descrizione}">
          <c:out value="${pat.indicatori.get('PxI').informativa}" />
        </p>
      </div>
      <div class="value text-center border">
      <c:set var="generali" value="${zero}" scope="page" />
      <c:set var="specifiche" value="${zero}" scope="page" />
      <c:forEach var="mis" items="${risk.misure}">
        <c:choose>
        <c:when test="${mis.carattere.informativa eq 'G'}">
          <c:set var="generali" value="${generali + 1}" scope="page" />
        </c:when>
        <c:when test="${mis.carattere.informativa eq 'S'}">
          <c:set var="specifiche" value="${specifiche + 1}" scope="page" />
        </c:when>
        </c:choose>
      </c:forEach>
        <p class="profileInfo" title="Generali: ${generali}; Specifiche: ${specifiche}">
      <c:if test="${generali gt zero}">
        <c:out value="${generali}G" />
        <c:if test="${specifiche gt zero}"> + </c:if>
      </c:if>
      <c:if test="${specifiche gt zero}">
        <c:out value="${specifiche}S" />
      </c:if>
        </p>
      </div>
      <div class="value bgcolor-${fn:toLowerCase(risk.livello)}">
        <p class="text-center" title="${pat.indicatori.get('PxI').informativa} &ndash; (${generali*0.5} + ${specifiche*1.0}) = ${risk.livello}">
          <c:out value="${risk.livello}" />
        </p>
      </div>

      <div class="value bgcolor-${fn:toLowerCase(pat.indicatori.get('PxI (stima)').informativa)}">
        <p class="text-center" title="PxI Ricalcolato">
          <c:out value="${pat.indicatori.get('PxI (stima)').informativa}" />
        </p>
      </div>
      <div class="entry bgAct4"></div>
      <div class="entry bgAct4"></div>
      <div class="entry bgAct4"></div>
      </c:forEach> 
    </c:forEach>
    </div>
    &nbsp;
</c:catch>
<c:out value="${exception}" />

    