<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="meas" value="${requestScope.misura}" scope="page" />
<c:forEach var="fase" items="${meas.fasi}">
  <c:if test="${fase.id eq param['idF']}">
    <c:set var="phas" value="${fase}" scope="page" />
    <c:set var="ind" value="${fase.indicatore}" scope="page" />
    <fmt:formatDate var="indLastMod" value="${ind.dataUltimaModifica}" pattern="dd/MM/yyyy" /> 
  </c:if>
</c:forEach>
  <c:catch var="exception">
    <link rel="stylesheet" href="https://code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
    <style>
        .form-control::placeholder {
            color: #6c757d; /* Default placeholder color */
            opacity: 1; /* Ensure full opacity by default */
        }
        .form-control:focus::placeholder {
            visibility: hidden; /* Hide placeholder on focus */
        }
        .error {
            color: maroon;      /* Change text color */
            font-weight: bold;  /* Make text bold */
            /*font-size: 14px;  /* Adjust font size */
            /*margin-top: 5px;  /* Add some space above the message */
            /*background: #cccccc;*/
        }
    </style>
    <form accept-charset="ISO-8859-1" id="mis-form" class="panel subfields-green" action="" method="post">
      <input type="hidden" id="mon-meas" name="mon-meas" value="${meas.codice}" />
      <input type="hidden" id="mon-fase" name="mon-fase" value="${fase.id}" />
      <input type="hidden" id="mon-ind" name="mon-ind" value="${fase.indicatore.id}" />
      <div class="panel-heading bgAct13">
        <div class="noHeader">
          <i class="fa-regular fa-pen-to-square"></i>&nbsp;
          <c:out value="Inserimento monitoraggio" />
        </div>
      </div>
      <hr class="separatore" />
      <div class="panel-body">
        <div class="row"> 
          <div class="content-holder col-sm-10 bgAct1">
            <strong> &nbsp;Misura:</strong>
            <a href="${initParam.appName}/?q=ic&p=mes&mliv=${meas.codice}&r=${param['r']}" title="${meas.codice}">
              <c:out value="${meas.nome}" escapeXml="false" />
            </a>
          </div>
        </div>
        <div class="row"> 
          <div class="content-holder col-sm-10 bgAct">
            <strong> &nbsp;Fase:</strong> 
            <c:out value="${phas.nome}" escapeXml="false" />
          </div>
        </div>
        <div class="row"> 
          <div class="content-holder col-sm-10 bgAct19">
            <strong> &nbsp;Indicatore:</strong>
            <a href="${initParam.appName}/?q=ic&p=ind&idI=${ind.id}&idF=${phas.id}&mliv=${meas.codice}&r=${param['r']}" title="Modificato:${indLastMod} ${fn:substring(ind.oraUltimaModifica,0,5)}">
              <c:out value="${ind.nome}" escapeXml="false" />
            </a>
            <dl class="sezioneElenco custom-dl" style="margin-bottom: 10px;">
              <dt class="text-primary">Tipo Indicatore</dt>
              <dd>
                <span class="badge border-basso textcolormaroon">
                  <c:out value="${ind.tipo.nome}" />
                </span>
              </dd>
              <dt class="text-primary">Baseline</dt>
              <dd><c:out value="${fn:toUpperCase(ind.getLabel(ind.baseline))}" /></dd>
              <dt class="text-primary">Data Baseline</dt>
              <dd><fmt:formatDate value="${ind.dataBaseline}" pattern="dd/MM/yyyy" /></dd>
              <dt class="text-primary">Target</dt>
              <dd><c:out value="${fn:toUpperCase(ind.getLabel(ind.target))}" /></dd>
              <dt class="text-primary">Data Target</dt>
              <dd><fmt:formatDate value="${ind.dataTarget}" pattern="dd/MM/yyyy" /></dd>
            </dl>
          </div>
        </div>
        <hr class="separatore" />
        <div class="row">
          <div class="col-sm-1">&nbsp;</div>
          <div class="col-sm-4 mandatory-xthin bgAct13"><strong>Data Monitoraggio</strong></div>
          <div class="col-sm-5">
            <input type="text" class="form-custom calendarData" id="mon-data" name="mon-data" value="<fmt:formatDate value='${now}' pattern='dd/MM/yyyy' />" title="data di sistema" readonly disabled>
          </div>
        </div>
        <hr class="separatore">
        <div class="row">
          <div class="col-sm-1">&nbsp;</div>
          <c:choose>
            <c:when test="${ind.tipo.id eq 1}">
            <script>
              function saveVal(val) {
                var hiddenValue = document.getElementById("mon-nome");
                hiddenValue.value = val;
              }
            </script>
            <div class="col-sm-4 mandatory-xthin bgAct13"><strong>Risultato</strong></div>
            <div class="col-sm-6">
              <select class="form-custom bgAct2" id="mon-value" name="mon-value" onchange="saveVal(this.value)">
                <option value="0" selected>Off</option>
                <option value="1">On</option>
              </select>
              <input type="hidden" class="form-control" id="mon-nome" name="mon-nome" value="OFF">
            </div>
            </c:when>
            <c:when test="${ind.tipo.id eq 2}">
            <div class="col-sm-4 mandatory-xthin bgAct13"><strong>Risultato</strong>
             <span class="float-right"><a href="javascript:popupWindow('Help sul formato','popup1',true,'Sono ammesse cifre decimali: utilizzare il punto come separatore dei decimali.<br><cite>(Esempio: 3.65)</cite>');" class="helpInfo" id="milestone">
              </a></span>
            </div>
            <div class="col-sm-6">
             
              <input type="text" class="form-custom bg-warning" id="mon-nome" name="mon-nome" value="" placeholder="Inserisci un numero">

              </div>
              

            </c:when>
            <c:when test="${ind.tipo.id eq 3}">
            <script>
              function saveVal(val) {
                var hiddenValue = document.getElementById("mon-nome");
                hiddenValue.value = val + '%';
              }
              function reset(val) {
                var showValue = document.getElementById("mon-value");
                showValue.value = val;
              }
            </script>
            <div class="col-sm-4 mandatory-xthin bgAct13"><strong>Risultato</strong></div>
            <div class="col-sm-6">
              <a href="javascript:popupWindow('Help sul formato','popup1',true,'Inserire un valore compreso tra 0 e 100 (senza il simbolo di percentuale).<br>Sono ammesse cifre decimali: utilizzare il punto come separatore dei decimali.<br><cite>(Esempio: 3.65, diventer&agrave; 3.65%)</cite>');" class="helpInfo" id="milestone">
              ?
              </a>

              <input type="text" class="form-custom bg-warning" id="mon-value" name="mon-value" value="" placeholder="Inserisci una percentuale" onblur="saveVal(this.value)">%
              <i class="fa fa-arrow-right"></i>
              <input type="text" class="form-control-custom" id="mon-nome" name="mon-nome" value="" readonly onclick="reset('')">
            </div>
            </c:when>
            <c:otherwise>
          <div class="col-sm-4 mandatory-xthin bgAct13"><strong>Risultato</strong></div>
          <div class="col-sm-6">
              <input type="text" class="form-control" id="mon-nome" name="mon-nome" value="" placeholder="Inserisci valore misurazione (obbligatorio)">
          </div>
            </c:otherwise>
          </c:choose>


        </div>
        <hr class="separatore">
        <div class="row">
          <div class="col-sm-1">&nbsp;</div>
          <div class="col-sm-4 mandatory bgAct13"><strong>Azioni svolte per raggiungere l'obiettivo</strong></div>
          <div class="col-sm-6">
            <textarea class="form-control" name="ind-descr" placeholder="Inserisci una descrizione delle attivit&agrave; svolte"></textarea>
          </div>
        </div>
        <hr class="separatore">
        <div class="row">
          <div class="col-sm-1">&nbsp;</div>
          <div class="col-sm-4 bg-note"><br><strong>Motivazioni ritardo / mancato raggiungimento</strong></div>
          <div class="col-sm-6">
            <textarea class="form-control" name="ind-descr" placeholder="Inserisci il motivo del mancato raggiungimento del target"></textarea>
          </div>
        </div>
        <hr class="separatore" />
        <div class="centerlayout">
          <button type="submit" class="btn btnNav" id="btn-save" value="Save">
            <i class="far fa-save"></i>  Salva e continua 
          </button>
        </div>
      </div>
    </form>
    <div id="popup1" class="popup">
      <div id="popup1Under" class="popupundertitle">
        <div id="titolopopup1" class="popuptitle" ></div>
        <div id="titolopopup1Under">
          <a href="Javascript:popupWindow('','popup1',false,'');"><img src="web/img/close-icon.gif" border="0" width="15" height="15" alt="Chiudi" title="Chiudi" /></a>
        </div>
      </div>
      <div class="popupbody" id="popup1Text" ></div>
    </div>
    <script type="text/javascript">
      var offsetcharacter = 5;
      
      $('input[type="text"].calendarData').datepicker();        
      
      $('#btn-save').click(function (e){
        e.preventDefault; 
      });
      
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
              required: true,
              checkNumber: true,
              checkPercent: true
            },
            'ind-database': {
                required: true
            },
            'ind-target': {
              required: true,
              checkNumber: true,
              checkPercent: true
            },
            'ind-datatarget': {
                required: true
            }
          }, 
          messages: {
            'ind-tipo':       "Inserire il tipo dell'indicatore",
            'ind-nome':       "Inserire almeno " + offsetcharacter + " caratteri.",
            'ind-baseline': {  
              required:       "Inserire il valore baseline",
              checkNumber:    "Inserire un numero",
              checkPercent:   "Inserire una percentuale nel formato ##.##"
            },
            'ind-database':   "Inserire la data baseline",
            'ind-target':   {  
              required:       "Inserire il valore target",
              checkNumber:    "Inserire un numero",
              checkPercent:   "Inserire una percentuale nel formato ##.##"
            },
            'ind-datatarget': "Inserire la data target"
          },
          submitHandler: function (form) {
            return true;
          }
        });
          
        $('#ind-tipo').change(function() {
          var selectedValue = $(this).val();
          var displayB = $('#displayBase');
          var displayT = $('#displayTarget');
          switch (selectedValue) {
            case '1':
              displayB.html('<select class="form-custom large-4" name="ind-baseline"><option value="0">Off</option><option value="1">On</option></select>');
              displayT.html('<select class="form-custom large-4" name="ind-target"><option value="0">Off</option><option value="1">On</option></select>');
              $.validator.addMethod("checkNumber", function(value, element) {
                return true;
              });
              $.validator.addMethod("checkPercent", function(value, element) {
                return true;
              });
              break;
            case '2':
              displayB.html('<input type="text" class="form-control large-4" id="ind-baseline" name="ind-baseline" placeholder="Inserisci un numero">');
              displayT.html('<input type="text" class="form-control large-4" id="ind-target" name="ind-target" placeholder="Inserisci un numero">');
              $.validator.addMethod("checkNumber", function(value, element) {
                if (this.optional(element) || parseInt(value) || value == 0)
                  return true;
              }, "Inserire un numero");
              $.validator.addMethod("checkPercent", function(value, element) {
                return true;
              });
              break;
            case '3':
              displayB.html('<input type="text" class="form-control large-4" id="ind-baseline" name="ind-baseline" placeholder="Inserisci una percentuale">');
              displayT.html('<input type="text" class="form-control large-4" id="ind-target" name="ind-target" placeholder="Inserisci una percentuale">');
              $.validator.addMethod("checkNumber", function(value, element) {
                return true;
              });
              $.validator.addMethod("checkPercent", function(value, element) {
                // Regular expression to match the percentage format
                const regex = /^(100\.00|[0-9]{1,2}\.[0-9]{2})$/;
                // Test the value against the regex
                if (regex.test(value)) {
                  return true; // Valid percentage
                }
              }, "Inserire una percentuale nel formato ##.##");
              break;
            default:
              displayB.html('<p class="alert alert-danger">Scegliere un tipo indicatore!</p>');
              displayT.html('<p class="alert alert-danger">Scegliere un tipo indicatore!</p>');
          }
        });

      });
    </script> 
  </c:catch>
  <c:out value="${exception}" />
