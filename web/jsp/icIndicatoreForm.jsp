<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="fase" value="${requestScope.fase}" scope="page" />
<c:set var="tipi" value="${requestScope.types}" scope="page" />
<c:catch var="exception">
    <style>
        .form-control::placeholder {
            color: #6c757d; /* Default placeholder color */
            opacity: 1; /* Ensure full opacity by default */
        }
        .form-control:focus::placeholder {
            visibility: hidden; /* Hide placeholder on focus */
        }
    </style>
    <form accept-charset="ISO-8859-1" id="ind-form" class="panel subfields-green" action="" method="post">
      <div class="panel-heading bgAct15">
        <div class="noHeader">
          <i class="fa-solid fa-pen-ruler"></i>&nbsp;
          <c:out value="Inserimento indicatore" />
        </div>
      </div>
      <hr class="separatore" />
      <div class="panel-body">
        <div class="row"> 
          <div class="content-holder col-sm-10">
            <div class="column">
              <div class=""><strong> &nbsp;Fase Indicatore</strong></div>
              <div class="text-center"><div id="custom-error-location"></div></div>
            </div>
            <div class="row">
              <div class="text-center"><c:out value="${fase.nome}" /></div>
            </div>
          </div>
        </div>
        <hr class="separatore">
        <div class="row">
          <div class="col-sm-1">&nbsp;</div>
          <div class="col-sm-4 mandatory-thin bgAct13"><strong>Nome Indicatore</strong></div>
          <div class="col-sm-6">
            <input type="text" class="form-control" id="ind-nome" name="ind-nome" placeholder="Inserisci nome dell'indicatore">
          </div>
        </div>
        <hr class="separatore">
        <div class="row">
          <div class="col-sm-1">&nbsp;</div>
          <div class="col-sm-4 mandatory-thin bgAct13"><strong>Tipo Indicatore</strong></div>
          <div class="col-sm-6">
            <select class="form-custom large-4" id="ind-tipo" name="ind-tipo">
              <c:if test="${indicatore.tipo.id ne -3}">
                <option value="${indicatore.tipo.id}">${indicatore.tipo.nome}</option>
              </c:if>
              <c:forEach var="tipo" items="${tipi}" varStatus="status">
                <c:set var="selected" value="" scope="page" />
                <c:if test="${tipo.id eq indicatore.idTipo}">
                  <c:set var="selected" value="selected" scope="page" />
                </c:if>
                <option value="${tipo.id}" ${selected}>${tipo.nome}</option>
              </c:forEach>
            </select>
          </div>
        </div>
        <hr class="separatore">
        <div class="row">
          <div class="col-sm-1">&nbsp;</div>
          <div class="col-sm-4 bg-note"><strong>Descrizione</strong></div>
          <div class="col-sm-6">
            <textarea class="form-control" name="ind-descr" placeholder="Inserisci una descrizione per l'indicatore"></textarea>
          </div>
        </div>
        <hr class="separatore">
        <div class="row">
          <div class="col-sm-1">&nbsp;</div>
          <div class="col-sm-4 mandatory-thin bgAct13"><strong>Baseline</strong></div>
          <div class="col-sm-5">
            <input type="text" class="form-control" id="ind-baseline" name="ind-baseline" placeholder="Inserisci valore baseline">
          </div>
        </div>
        <hr class="separatore">
        <div class="row">
          <div class="col-sm-1">&nbsp;</div>
          <div class="col-sm-4 mandatory-thin bgAct13"><strong>Data Baseline</strong></div>
          <div class="col-sm-5">
            <input type="text" class="form-control calendarData" id="ind-database" name="ind-database" placeholder="Inserisci data baseline">
          </div>
        </div>
        <hr class="separatore">
        <div class="row">
          <div class="col-sm-1">&nbsp;</div>
          <div class="col-sm-4 mandatory-thin bgAct13"><strong>Target</strong></div>
          <div class="col-sm-5">
            <input type="text" class="form-control" id="ind-target" name="ind-target" placeholder="Inserisci valore target">
          </div>
        </div>
        <hr class="separatore">
        <div class="row">
          <div class="col-sm-1">&nbsp;</div>
          <div class="col-sm-4 mandatory-thin bgAct13"><strong>Data Target</strong></div>
          <div class="col-sm-5">
            <input type="text" class="form-control calendarData" id="ind-datatarget" name="ind-datatarget" placeholder="Inserisci data target">
          </div>
        </div>
        <hr class="separatore">
        <div class="row">
          <div class="col-sm-5">
            <a id="btnBack" class="btn btnNav" onclick="goBack()"><i class="fas fa-chevron-left"></i> Indietro</a>
            <a href="<c:out value="${ind}${requestScope.progetto.id}&v=o" escapeXml="false" />" id='btn-close' class="btn btnNav"><i class="fas fa-ruler"></i> Indicatori</a>
          </div>
          <div class="col-sm-5">          
          </div>
        </div>
        <hr class="separatore" />
        <div class="centerlayout">
          <button type="submit" class="btn btnNav"" id="btn-save" value="Save">
            <i class="far fa-save"></i> Salva
          </button>
        </div>
      </div>
    </form>

      </c:catch>
      <c:out value="${exception}" />

      <script type="text/javascript">
        var offsetcharacter = 5;
        // Variabili per formattazione stringhe
        var limNoteTitleOpen = "\n - ";
        var limNoteTitleClose = " -\n";
        var newLine = "\n";
        var separatore = "\n================================\n";
        $(document).ready(function () {
          $('#ind-form').validate ({
            rules: {
              'ind-tipo': {
                required: true
              },
              'ind-nome': {
                required: true,
                minlength: offsetcharacter
              },
              'ind-baseline': {
                required: true
              },
              'ind-database': {
                  required: true
              },
              'ind-target': {
                required: true
              },
              'ind-datatarget': {
                  required: true
              }
            }, 
            messages: {
              'ind-tipo':     "Inserire il tipo dell'indicatore",
              'ind-nome':     "Inserire almeno " + offsetcharacter + " caratteri.",
              'ind-baseline': "Inserire il valore baseline",
              'ind-database': "Inserire la data baseline",
              'ind-target':   "Inserire il valore target",
              'ind-datatarget': "Inserire la data target"
            },
            submitHandler: function (form) {
              return true;
            }
          });
          $('input[type=\'text\'].calendarData').datepicker();
        });
      </script> 
