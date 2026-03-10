<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="URL.jspf" %>
<c:set var="process" value="${requestScope.pat}" scope="page" />
<c:set var="fasi" value="${requestScope.listaFasi}" scope="page" />
<c:set var="fase" value="" scope="page" />
<c:forEach var="act" items="${fasi}">
  <c:if test="${act.id eq param['aliv']}">
    <c:set var="fase" value="${act}" scope="page" />
  </c:if>
</c:forEach>
    <style>
        .form-control::placeholder {
            color: #6c757d; /* Default placeholder color */
            opacity: 1; /* Ensure full opacity by default */
        }
        .form-control:focus::placeholder {
            visibility: hidden; /* Hide placeholder on focus */
        }
    </style>
    <div class="form-custom bg-note">
      <form accept-charset="ISO-8859-1" id="uac-form" action="" method="post">
        <input type="hidden" id="mat-area" name="pliv0" value="${param['pliv0']}" />
        <input type="hidden" id="mat-code" name="pliv1" value="${param['pliv1']}" />
        <input type="hidden" id="pat-id" name="pliv2" value="${param['pliv']}" />
        <input type="hidden" id="pat-code" name="pat-code" value="${process.codice}" />
        <input type="hidden" id="act-id" name="ac-id" value="${param['aliv']}" />
        <div class="panel-heading bgAct11" id="details">
          <h5 class="fw-bold text-dark">
            <i class="fa-solid fa-file-circle-plus"></i>
            <c:out value="${requestScope.tP}" />: 
            <a href="${initParam.appName}/?q=pr&p=uac&aliv=${fase.id}&liv=${param['liv']}&pliv=${param['pliv']}&pliv1=&pliv0=&r=${param['r']}" class="" title="">
              <c:out value="${fase.nome}" />
            </a>
          </h5>
        </div>
        <div class="panel-body">
          <div class="row">  <!-- MACRO -->
            <div class="content-holder col-sm-10 bgAct15">
              <strong> &nbsp;Macroprocesso:</strong>
              <c:out value="${process.padre.nome}" escapeXml="false" />
            </div>
          </div>
          <hr class="separapoco" />
          <div class="row">  <!-- PROCESSO -->
            <div class="content-holder col-sm-10 bgAct19">
              <strong> &nbsp;Processo:</strong>
              <a href="${initParam.appName}/?q=pr&p=pro&pliv=${process.id}&liv=2&r=${param['r']}" title="${process.codice}">
                <c:out value="${process.nome}" escapeXml="false" />
              </a>
            </div>
          </div>
          <hr class="separapoco" />
          <div class="row">  <!-- ATTIVITA' -->
            <div class="content-holder col-sm-10 bgAct">
              <strong> &nbsp;Fasi di questo processo:</strong>
              <ol>
              <c:forEach var="act" items="${fasi}">
                <li>
                <c:choose>
                <c:when test="${act.id ne param['aliv']}">
                <a href="${initParam.appName}/?q=pr&p=uac&aliv=${act.id}&liv=${param['liv']}&pliv=${param['pliv']}&pliv1=&pliv0=&r=${param['r']}" class="" title="Clicca per visualizzare o modificare la descrizione">
                <c:out value="${act.nome}" escapeXml="false" />
                </a>
                </c:when>
                <c:otherwise>
                <span class="badge heading bgAct5 textcolormaroon"><c:out value="${act.nome}" escapeXml="false" /></span>
                </c:otherwise>
                </c:choose>
                </li>
              </c:forEach>
              </ol>
            </div>
          </div>
          <c:set var="desc" value="Nessuna descrizione inserita" scope="page" />
          <c:if test="${not empty fase.descrizione}">
            <c:set var="desc" value="${fase.descrizione}" scope="page" />
          </c:if>
          <hr class="separatore" />
          <div class="row">
            <div class="content-holder col-sm-10 bgAct4">
              <div class="fas-container">
                <div class="reportRow">&nbsp;&nbsp;Descrizione</div>
                <hr class="separapoco" />
                <div id="callable-row">
                  <div class="row">
                    <div class="col-12 large-4">
                      <textarea class="form-control" rows="4" id="at-desc" name="ac-desc" placeholder="${pageScope.desc}"><c:out value="${fase.descrizione}" /></textarea>
                    </div>
                  </div>
                  <hr class="separatore" />
                </div>
              </div>
            </div>
          </div>
          <br>
          <div class="d-flex justify-content-center">
            <button type="submit" class="btn btnNav bgAct22" id="btn-save" name="action" value="load">
              <i class="far fa-save"></i> &nbsp; Salva &nbsp;
            </button>
          </div>
          <hr class="separatore" />
        </div>
      </form>
    </div>
