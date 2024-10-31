<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="URL.jspf" %>
<c:set var="meas" value="${requestScope.misura}" scope="page" />
<c:set var="risks" value="${requestScope.rischi}" scope="page" />
<c:set var="dets" value="NO" scope="page" />
<c:if test="${meas.dettagli}">
  <c:set var="dets" value="SI" scope="page" />
</c:if>
    <style>
    section {
      margin-top: 20px;
      padding: 20px;
      background-color: #f1f1f1;
      border-radius: 10px;
    }
    </style>
<c:catch var="exception">
    <h3 class="mt-1 m-0 font-weight-bold">Misura di prevenzione / mitigazione del rischio corruttivo</h3>
    <hr class="riga"/>
    <h4 class="p-2 bgAct17 rounded popupMenu heading">
      <i class="fa-solid fa-umbrella ico-home" title="misura di prevenzione"></i>&nbsp; 
      <c:out value="${meas.nome}" />
    </h4>
    <div class="form-custom bgAct28">
      <dl class="sezioneElenco custom-dl">
        <dt class="text-primary">Carattere</dt>
        <dd><c:out value="${meas.carattere.nome}" /></dd>
        <dt class="text-primary">Comporta Spese?</dt>
        <dd><c:out value="${fn:toUpperCase(meas.getOnerosa(meas.onerosa))}" /></dd>
        <dt class="text-primary">Codice</dt>
        <dd><c:out value="${meas.codice}" /></dd>
        <dt class="text-primary">Monitorata</dt>
        <dd>
          <c:out value="${dets}" />&nbsp; 
          <c:if test="${meas.dettagli}">&nbsp; 
          <a href="${initParam.appName}/?q=ic&p=mes&mliv=MP.S.22&r=AT2022#details" class="badge badge-pill border-basso">dettagli monitoraggio</a>
          </c:if>
        </dd>
      </dl>
      <h5 class="fw-bold text-dark border-bottom border-2 border-secondary">
        <svg xmlns="http://www.w3.org/2000/svg" width="22" height="22" fill="currentColor" class="bi bi-umbrella ico-home" viewBox="0 0 16 16">
          <path d="M8 0a.5.5 0 0 1 .5.5v.514C12.625 1.238 16 4.22 16 8c0 0 0 .5-.5.5-.149 0-.352-.145-.352-.145l-.004-.004-.025-.023a3.5 3.5 0 0 0-.555-.394A3.17 3.17 0 0 0 13 7.5c-.638 0-1.178.213-1.564.434a3.5 3.5 0 0 0-.555.394l-.025.023-.003.003s-.204.146-.353.146-.352-.145-.352-.145l-.004-.004-.025-.023a3.5 3.5 0 0 0-.555-.394 3.3 3.3 0 0 0-1.064-.39V13.5H8h.5v.039l-.005.083a3 3 0 0 1-.298 1.102 2.26 2.26 0 0 1-.763.88C7.06 15.851 6.587 16 6 16s-1.061-.148-1.434-.396a2.26 2.26 0 0 1-.763-.88 3 3 0 0 1-.302-1.185v-.025l-.001-.009v-.003s0-.002.5-.002h-.5V13a.5.5 0 0 1 1 0v.506l.003.044a2 2 0 0 0 .195.726c.095.191.23.367.423.495.19.127.466.229.879.229s.689-.102.879-.229c.193-.128.328-.304.424-.495a2 2 0 0 0 .197-.77V7.544a3.3 3.3 0 0 0-1.064.39 3.5 3.5 0 0 0-.58.417l-.004.004S5.65 8.5 5.5 8.5s-.352-.145-.352-.145l-.004-.004a3.5 3.5 0 0 0-.58-.417A3.17 3.17 0 0 0 3 7.5c-.638 0-1.177.213-1.564.434a3.5 3.5 0 0 0-.58.417l-.004.004S.65 8.5.5 8.5C0 8.5 0 8 0 8c0-3.78 3.375-6.762 7.5-6.986V.5A.5.5 0 0 1 8 0M6.577 2.123c-2.833.5-4.99 2.458-5.474 4.854A4.1 4.1 0 0 1 3 6.5c.806 0 1.48.25 1.962.511a9.7 9.7 0 0 1 .344-2.358c.242-.868.64-1.765 1.271-2.53m-.615 4.93A4.16 4.16 0 0 1 8 6.5a4.16 4.16 0 0 1 2.038.553 8.7 8.7 0 0 0-.307-2.13C9.434 3.858 8.898 2.83 8 2.117c-.898.712-1.434 1.74-1.731 2.804a8.7 8.7 0 0 0-.307 2.131zm3.46-4.93c.631.765 1.03 1.662 1.272 2.53.233.833.328 1.66.344 2.358A4.14 4.14 0 0 1 13 6.5c.77 0 1.42.23 1.897.477-.484-2.396-2.641-4.355-5.474-4.854z"/>
        </svg>&nbsp; 
        Tipologie della misura
      </h5>
      <ul class="line bgcolor-minimo">
      <c:forEach var="tipo" items="${meas.tipologie}">
        <li class="line">&nbsp;&nbsp; <i class="fa-solid fa-umbrella" title="misura di prevenzione"></i>&nbsp; <c:out value="${tipo.nome}" /></li>
      </c:forEach>
      </ul>
      <hr class="separatore" />
      <h5 class="fw-bold text-dark border-bottom border-2 border-secondary">
        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="currentColor" class="bi bi-exclamation-triangle ico-home" viewBox="0 0 16 16">
          <path d="M7.938 2.016A.13.13 0 0 1 8.002 2a.13.13 0 0 1 .063.016.15.15 0 0 1 .054.057l6.857 11.667c.036.06.035.124.002.183a.2.2 0 0 1-.054.06.1.1 0 0 1-.066.017H1.146a.1.1 0 0 1-.066-.017.2.2 0 0 1-.054-.06.18.18 0 0 1 .002-.183L7.884 2.073a.15.15 0 0 1 .054-.057m1.044-.45a1.13 1.13 0 0 0-1.96 0L.165 13.233c-.457.778.091 1.767.98 1.767h13.713c.889 0 1.438-.99.98-1.767z"/>
          <path d="M7.002 12a1 1 0 1 1 2 0 1 1 0 0 1-2 0M7.1 5.995a.905.905 0 1 1 1.8 0l-.35 3.507a.552.552 0 0 1-1.1 0z"/>
        </svg>&nbsp; 
        Rischi cui questa misura &egrave; applicata
      </h5>
      <c:if test="${empty risks}">
      <div id="noneMessage" class="alert alert-danger alert-dismissible" role="alert">
        La misura non &egrave; stata applicata ancora ad alcun rischio
      </div>
      </c:if>      
      <ul class="line bgAct30">
      <c:forEach var="risk" items="${risks}">
        <li class="line">
          &nbsp;&nbsp; <i class="fas fa-triangle-exclamation" title="rischio"></i>
          &nbsp; <a href="${initParam.appName}/?q=ri&idR=${risk.id}&r=${param['r']}">
            &nbsp; <c:out value="${risk.nome}" />&nbsp;
          </a> 
          &nbsp;(<strong class="text-success">PROCESSO:</strong>&nbsp; <a href="${initParam.appName}/?q=pr&p=pro&pliv=${risk.cod1}&liv=2&r=${param['r']}">
              <c:out value="${risk.extraInfo1}" />
           </a>)
        </li>
      </c:forEach>
      </ul>
      <section>
        <h5 class="fw-bold text-dark border-bottom border-2 border-secondary">
          <i class="fa-solid fa-building-flag ico-home"></i>&nbsp; Strutture Capofila
        </h5>
        <ul class="line">
        <c:forEach var="cp" items="${meas.capofila}" varStatus="innerStatus">
          <c:if test="${innerStatus.count eq meas.capofila.size()}">
            <li class="line">
              <img src="${initParam.urlDirectoryImmagini}str-l${cp.livello}.png" class="ico-small" alt="icona" title="Struttura di livello ${cp.livello}" /> 
              <c:out value="${cp.prefisso}" /> <c:out value="${cp.nome}" />
              <span class="badge badge-pill bg-primary btn-small lightTable marginLeft align-middle">CAPOFILA</span>
            </li>
          </c:if>
        </c:forEach>
        <c:forEach var="cp2" items="${meas.capofila2}" varStatus="innerStatus">
          <c:if test="${innerStatus.count eq meas.capofila2.size()}">
            <li class="line">
              <img src="${initParam.urlDirectoryImmagini}str-l${cp2.livello}.png" class="ico-small" alt="icona" title="Struttura di livello ${cp2.livello}" /> 
              <c:out value="${cp2.prefisso}" /> <c:out value="${cp2.nome}" />
              <span class="badge badge-pill bg-warning text-black btn-small lightTable marginLeft align-middle">CAPOFILA 2</span>
            </li>
          </c:if>
        </c:forEach>
        <c:forEach var="cp3" items="${meas.capofila3}" varStatus="innerStatus">
          <c:if test="${innerStatus.count eq meas.capofila3.size()}">
            <li class="line">
              <img src="${initParam.urlDirectoryImmagini}str-l${cp3.livello}.png" class="ico-small" alt="icona" title="Struttura di livello ${cp3.livello}" /> 
              <c:out value="${cp3.prefisso}" /> <c:out value="${cp3.nome}" />
              <span class="badge badge-pill bg-info btn-small lightTable marginLeft align-middle">CAPOFILA 3</span>
            </li>
          </c:if>
        </c:forEach>
        </ul>
        <h5 class="fw-bold text-dark border-bottom border-2 border-secondary">
          <i class="fa-solid fa-building-circle-check ico-home"></i>&nbsp; Strutture Coinvolte
        </h5>
        <c:if test="${empty meas.gregarie}">
        <div id="noneMessage" class="alert alert-warning alert-dismissible" role="alert">
          Nessuna struttura gregaria
        </div>
        </c:if> 
        <ul class="line">
        <c:forEach var="gr" items="${meas.gregarie}" varStatus="innerStatus">
          <li class="line">
            <img src="${initParam.urlDirectoryImmagini}str-l${gr.livello}.png" class="ico-small" alt="icona" title="Struttura di livello ${gr.livello}" /> 
            <c:out value="${gr.prefisso}" /> <c:out value="${gr.nome}" />
          </li>
        </c:forEach>
        </ul>
      </section>      
    </div>
</c:catch>
<c:out value="${exception}" />
