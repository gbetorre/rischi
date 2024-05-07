<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="URL.jspf" %>
<c:set var="mats" value="${requestScope.macroProcessi}" scope="page" />
<c:set var="names" value="P1,P2,P3,P4,P5,P6,P7,I1,I2,I3,I4" scope="page" />
    <h3 class="mt-1 m-0 font-weight-bold float-left">Report probabilit&agrave; x impatto</h3>    
    <hr class="riga"/>
    <div class="grid scrollX">
      <!-- columns header -->
      <div class="entry root text-center wide"><span style="width:190px;">PROCESSO</span></div>
      <div class="entry head text-center"></div>
      <div class="entry head text-center textcolorred panel"><span>PxI</span></div>
      <div class="entry head text-center panel"><span>P</span></div>
      <div class="entry head text-center panel"><span>I</span></div>
      <c:forTokens var="iname" items="${names}" delims=",">
      <div class="entry head text-center"><c:out value="${iname}" /></div>
      </c:forTokens>
      <!-- normal cells -->
    <c:forEach var="mat" items="${mats}">
      <div class="entry head bgAct4 textcolormaroon wide" title="${mat.nome}">
        <c:out value="${mat.nome}" />
        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-arrow-down-short float-right" viewBox="0 0 16 16">
          <path fill-rule="evenodd" d="M8 4a.5.5 0 0 1 .5.5v5.793l2.146-2.147a.5.5 0 0 1 .708.708l-3 3a.5.5 0 0 1-.708 0l-3-3a.5.5 0 1 1 .708-.708L7.5 10.293V4.5A.5.5 0 0 1 8 4"/>
        </svg>       
      </div>
      <div class="entry bgAct4"></div>
      <div class="entry bgAct4"></div>
      <div class="entry bgAct4"></div>
      <div class="entry bgAct4"></div>
      <c:forTokens var="iname" items="${names}" delims=",">
      <div class="entry bgAct4"></div>
      </c:forTokens>
      <c:forEach var="pat" items="${mat.processi}" varStatus="status">
      <div class="entry head bgAct4 wide">           
        <a href="${initParam.appName}/?q=pr&p=pro&pliv=${pat.id}&liv=${pat.livello}&r=${param['r']}">
          <c:out value="${status.count}) ${pat.nome}" />
        </a>
      </div>
      <div class="entry"></div>
      <c:set var="classSuffix" value="${fn:toLowerCase(pat.indicatori.get('PxI').informativa)}" scope="page" />
      <c:if test="${fn:indexOf(classSuffix, ' ') gt -1}">
        <c:set var="classSuffix" value="${fn:substring(classSuffix, zero, fn:indexOf(classSuffix, ' '))}" scope="page" />
      </c:if>
      <div class="value bgcolor-${classSuffix} border-${classSuffix}">
        <p class="text-center" title="${pat.indicatori.get('PxI').descrizione}">
          <c:out value="${pat.indicatori.get('PxI').informativa}" />
        </p>
      </div>
      <div class="value bgcolor-${fn:toLowerCase(pat.indicatori.get('P').informativa)} border-${fn:toLowerCase(pat.indicatori.get('P').informativa)}">
        <p class="text-center" title="${pat.indicatori.get('P').descrizione}">
          <c:out value="${pat.indicatori.get('P').informativa}" />
        </p>
      </div>
      <div class="value bgcolor-${fn:toLowerCase(pat.indicatori.get('I').informativa)} border-${fn:toLowerCase(pat.indicatori.get('I').informativa)}">
        <p class="text-center" title="${pat.indicatori.get('I').descrizione}">
          <c:out value="${pat.indicatori.get('I').informativa}" />
        </p>
      </div>
      <c:forTokens var="iname" items="${names}" delims=",">
      <c:set var="idesc" value="${pat.indicatori.get(iname).informativa}" scope="page" />
      <c:if test="${fn:startsWith(idesc, 'Non')}">
        <c:set var="idesc" value="N/D" scope="page" />
      </c:if>
      <div class="value bgcolor-${fn:toLowerCase(pat.indicatori.get(iname).informativa)} border-${fn:toLowerCase(pat.indicatori.get(iname).informativa)}">
        <p class="text-center" title="${pat.indicatori.get(iname).descrizione}">
          <c:out value="${idesc}" />
        </p>
      </div>
      </c:forTokens>
      </c:forEach> 
    </c:forEach>
    </div>
    &nbsp;
    <a href="${mro}&msg=refresh_ce" type="button" class="btn btn-primary float-right" value="Update">
      <i class="fa-solid fa-arrow-rotate-right"></i>
      Ricalcola
    </a>

    