<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="URL.jspf" %>
<c:set var="process" value="${requestScope.pat}" scope="page" />
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
      <form accept-charset="ISO-8859-1" id="ina-form" action="" method="post">
        <input type="hidden" id="mat-area" name="pliv0" value="${param['pliv0']}" />
        <input type="hidden" id="mat-code" name="pliv1" value="${param['pliv1']}" />
        <input type="hidden" id="pat-code" name="pliv2" value="${param['pliv']}" />
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
              <strong> &nbsp;Fasi gi&agrave; definite:</strong>
            <c:choose>
              <c:when test="${not empty fasi}">
              <ul>
              <c:forEach var="act" items="${fasi}">
                <li><c:out value="${act.nome}" escapeXml="false" /></li>
              </c:forEach>
              </ul>
              <div class="col-sm-12 centerlayout">
                <a href="#ord-form" rel="modal:open" type="button" class="badge bg-warning btn-small lightTable text-dark align-center text-decoration-none" id="resort" title="Modifica l'ordine delle fasi">
                  <i class="fa-solid fa-sort"></i>    &nbsp;    Riordina
                </a>&nbsp;&nbsp;
              </div>
              </c:when>
              <c:otherwise>
              NESSUNA
              </c:otherwise>
            </c:choose>
            </div>
          </div>
        <c:if test="${(empty param['ref']) or (param['ref'] ne 'pro')}">
          <hr class="separatore" />
          <div class="row">
            <div class="content-holder col-sm-10 bgAct4">
              <div class="fas-container">
                <div class="reportRow">&nbsp;&nbsp;Inserimento Fasi</div>
                <hr class="separapoco" />
                <div id="callable-row">
                  <div class="row">
                    <div class="col-sm-4 mandatory-thin marginLeftSmall"><strong>Fase</strong></div>
                    <div class="col-sm-7">
                      <input type="text" class="form-control sAct" id="at-nome" name="at-name" placeholder="Inserisci fase...">
                    </div>
                    <hr class="separatore" />
                  </div>
                </div>
              </div>
              <div class="row lblca">
                <div class="col-sm-1">&nbsp;</div>
                <div class="large-4 column">
                  <a href="javascript:void(0);" type="button" class="js-add-row btn bgAct14" title="Aggiungi una fase del processo">+ Aggiungi</a>
                </div>
                <div class="large-4 column">
                  <a href="javascript:void(0);" type="button" class="js-remove-row btn bgAct25" title="Elimina l'ultima fase aggiunta">- Elimina</a>
                </div>
              </div>
            </div>
          </div>
          <br />
        </c:if>
          <hr class="separatore" />
        </div>
        <c:if test="${(empty param['ref']) or (param['ref'] ne 'pro')}">
        <hr class="separapoco" />
        <div class="row">
          <div class="col-sm-12">
            <button type="submit" class="btn btnNav align-left" id="btn-save" name="action" value="save">
              <i class="far fa-save"></i>  Salva ed esci  <i class="fa-solid fa-circle-chevron-up"></i>
            </button>
            <button type="submit" class="btn btnNav bgAct14 float-right" id="btn-cont" name="action" value="cont">
              <i class="far fa-save"></i>  Salva e continua  <i class="fa-solid fa-circle-chevron-right"></i>
            </button>
          </div>
        </div>
        </c:if>
      </form>
    </div>
    <script>
    $(document).ready(function() {
        $('.js-add-row').on('click', function () {
            $('.fas-container').append($('#callable-row').html());
        });
        $('.js-remove-row').on('click', function () {
            var LastSiblings = $('#callable-row').siblings('.row:last-child');
            if (LastSiblings.length != 0) {
                LastSiblings.remove();
            } else {
                alert('Occorre prima aggiungere un campo');
            }
        });
        $('#btn-save').click(function (e){
            e.preventDefault;
          });
    });
    </script>
    <form accept-charset="ISO-8859-1" id="ord-form" method="post" action="" class="modal bgAct" style="height:440px;">
      <input type="hidden" id="pat-code" name="pliv2" value="${param['pliv']}" />
      <div class="heading bgAct13">
        <hr class="separapoco">
        <h5 class="fw-bold text-dark">&nbsp; Riordinamento Fasi</h5>
      </div>
      <hr class="separatore" />
      <div class="row">
      <c:forEach var="act" items="${fasi}" varStatus="status">
        <input type="hidden" name="ac-id" value="${act.id}" />
        <div class="col-sm-2">
          <select class="form-custom" name="ac-ordb">
          <c:forEach var="num" items="${fasi}" varStatus="index">
            <c:set var="selected" value="" scope="page" />
            <c:if test="${index.count eq status.count}">
              <c:set var="selected" value="selected" scope="page" />
            </c:if>
            <option value="${index.count}" ${selected}><c:out value="${index.count}" /></option>
          </c:forEach>
          </select>
        </div>
        <div class="col-sm-10">
          <strong> &nbsp;<c:out value="${act.nome}" escapeXml="false" /></strong>
        </div>
        <hr class="riga">
      </c:forEach>
          
      </div>

      <hr class="separatore" />
      
      <div class="row">
        <div class="col-sm-5">
          &nbsp;
        </div>
        <div class="col-sm-5">  
          <button type="submit" class="btn btn-success" name="action" value="ordb">
            <i class="far fa-save"></i>   Salva
          </button>
        </div>
      </div>
    </form>
