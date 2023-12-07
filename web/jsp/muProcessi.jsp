<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="URL.jspf" %>
<c:set var="mats" value="${requestScope.macroProcessi}" scope="page" />
<c:set var="names" value="P1,P2,P3,P4,P5,P6,P7,I1,I2,I3,I4" scope="page" />
    <style>
    .grid {
      display: grid;
      grid-template-columns: repeat(16,100px);
      gap: 8px;
      color: #fff;
      height: 600px;
      resize: both;
      overflow: auto;
    }
    .entry {
      background: #999;
      padding: 1em 2em;
    }
    .entry.root {
      background: #000; z-index: 10!important;
    }
    .entry.root, .entry.head {
      position: sticky;
      top: 0; left: 0;
      background: #444;
      z-index: 1;
    }
    .value {
      height: 5em;
      line-height: 5em;
      color: #000;
    }
    .wide {
      width:210px;
    }
    </style>
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
      <div class="entry head bgAct4 wide" title="${mat.nome}">
        <span class="textcolormaroon"><c:out value="${fn:substring(mat.nome, 0, 14)}" />...</span>
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
      <div class="value bgcolor-${fn:toLowerCase(pat.indicatori.get('PxI').informativa)} border-${fn:toLowerCase(pat.indicatori.get('PxI').informativa)}">
        <p class="text-center" title="${pat.indicatori.get('PxI').informativa}">
          <c:out value="${pat.indicatori.get('PxI').informativa}" />
        </p>
      </div>
      <div class="value bgcolor-${fn:toLowerCase(pat.indicatori.get('P').informativa)} border-${fn:toLowerCase(pat.indicatori.get('P').informativa)}">
        <p class="text-center" title="${pat.indicatori.get('P').informativa}">
          <c:out value="${pat.indicatori.get('P').informativa}" />
        </p>
      </div>
      <div class="value bgcolor-${fn:toLowerCase(pat.indicatori.get('I').informativa)} border-${fn:toLowerCase(pat.indicatori.get('I').informativa)}">
        <p class="text-center" title="${pat.indicatori.get('I').informativa}">
          <c:out value="${pat.indicatori.get('I').informativa}" />
        </p>
      </div>
      <c:forTokens var="iname" items="${names}" delims=",">
      <c:set var="idesc" value="${pat.indicatori.get(iname).informativa}" scope="page" />
      <c:if test="${fn:startsWith(idesc, 'Non')}">
        <c:set var="idesc" value="N/D" scope="page" />
      </c:if>
      <div class="value bgcolor-${fn:toLowerCase(pat.indicatori.get(iname).informativa)} border-${fn:toLowerCase(pat.indicatori.get(iname).informativa)}">
        <p class="text-center" title="${pat.indicatori.get(iname).informativa}">
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

    