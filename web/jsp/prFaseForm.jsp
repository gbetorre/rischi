<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="URL.jspf" %>
<c:set var="process" value="${requestScope.pat}" scope="page" />
<c:set var="act" value="${requestScope.fase}" scope="page" />
<c:set var="fasi" value="${requestScope.listaFasi}" scope="page" />
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
        <div class="panel-heading bgAct11" id="details">
          <h5 class="fw-bold text-dark">
            <i class="fa-solid fa-file-circle-plus"></i>
            <c:out value="${requestScope.tP}" />
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
              <strong> &nbsp;Altre fasi dello stesso processo:</strong>
            <c:choose>
              <c:when test="${not empty fasi}">
              <ul>
              <c:forEach var="act" items="${fasi}">
                <li><c:out value="${act.nome}" escapeXml="false" /></li>
              </c:forEach>
              </ul>
              </c:when>
              <c:otherwise>
              NESSUN'altra fase
              </c:otherwise>
            </c:choose>
            </div>
          </div>
        <c:if test="${(empty param['ref']) or (param['ref'] ne 'pro')}">
          <hr class="separatore" />
          <div class="row">
            <div class="content-holder col-sm-10 bgAct4">
              <div class="fas-container">
                <div class="reportRow">&nbsp;&nbsp;Fase</div>
                <hr class="separapoco" />
                <div id="callable-row">
                  <div class="row">
                    <div class="col-sm-4 mandatory-thin marginLeftSmall"><strong>Fase</strong></div>
                    <div class="col-sm-7">
                      <c:out value="${fase.nome}" />
                    </div>
                  </div>
                  <hr class="separatore" />
                  <%--
                  <div class="row">
                    <div class="col-sm-4 bgAct28 marginLeftSmall"><strong>Descrizione</strong></div>
                    <div class="col-7 large-4">
                      <textarea class="form-control" id="at-desc" name="ac-desc" placeholder="Inserisci una descrizione"></textarea>
                    </div>
                    <hr class="separatore" />
                  </div>--%>
                </div>
              </div>
            </div>
          </div>
          <br>
        </c:if>
          <hr class="separatore" />
        <c:if test="${(empty param['ref']) or (param['ref'] ne 'pro')}">
        <%@ include file="btnSaveCont.jspf"%>
          <hr class="separatore" />
        </c:if>
        </div>
      </form>
    </div>
