<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="URL.jspf" %>
<c:set var="meas" value="${requestScope.misura}" scope="page" />
    <style>
    section {
      margin-top: 20px;
      padding: 20px;
      background-color: #f1f1f1;
      border-radius: 10px;
    }
    </style>
<c:catch var="exception">
    <h3 class="mt-1 m-0 font-weight-bold">Misura di prevenzione</h3>
    <hr class="riga"/>
    <div class="form-custom">
      <div class="panel-heading bgAct22">
        <div class="noHeader text-white">
          <i class="fa-solid fa-umbrella" title="misura di prevenzione"></i>&nbsp; 
          <c:out value="${meas.nome}" />
        </div>
      </div>
      <hr class="separatore" />
      <h3>Rischi a cui questa misura &egrave; applicata</h3>


        <ul class="line bgAct24">
            <li class="line">&nbsp;<i class="fas fa-triangle-exclamation" title="rischi corruttivi"></i>&nbsp; Rischio 1</li>
            <li class="line">&nbsp;<i class="fas fa-triangle-exclamation" title="rischi corruttivi"></i>&nbsp; Rischio 2</li>
            <li class="line">&nbsp;<i class="fas fa-triangle-exclamation" title="rischi corruttivi"></i>&nbsp; Rischio 3</li>
        </ul>
          

        <ul class="list-group">
        <c:forEach var="pat" items="${risk.processi}" varStatus="status">
          <c:set var="bgAct" value="bgAct4" scope="page" />
          <c:if test="${status.index mod 2 eq 0}">
            <c:set var="bgAct" value="bgAct20" scope="page" />
          </c:if>
          <li class="list-group-item ${bgAct}">
            <a href="${initParam.appName}/?q=pr&p=pro&pliv=${pat.id}&liv=${pat.livello}&r=${param['r']}">
              <c:out value="${pat.nome}" />
            </a> 
            <span class="float-right">
              (Macroprocesso: <c:out value="${pat.padre.nome}" />)
            </span>
          </li>
        </c:forEach>
        </ul>
        <hr class="separatore" />
    <div class="">
        <h3>Tipologie della misura</h3>
        
        <ul class="line">
            <li class="line">Tipologia 1</li>
            <li class="line">Tipologia 2</li>
            <li class="line">Tipologia 3</li>
        </ul>
        
        <section>
            <h3>Strutture Capofila</h3>
            <p>Direzione X</p>
        </section>
        
        <section>
            <h3>Strutture Gregarie</h3>
            <p>Area Y</p>
        </section>
        
        <section>
            <h3>Altre informazioni</h3>
            <p>Carattere specifico; non onerosa</p>
        </section>
    </div>
      <hr class="separatore" />
    </div>

</c:catch>
<c:out value="${exception}" />