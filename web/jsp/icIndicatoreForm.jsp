<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="meas" value="${requestScope.misura}" scope="page" />
<c:set var="fase" value="${requestScope.fase}" scope="page" />
<c:set var="type" value="" scope="page" />
<c:set var="selected" value="" scope="page" />
<c:set var="master" value="" scope="page" />
<c:set var="slave" value="" scope="page" />
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
    <form accept-charset="ISO-8859-1" id="ind-form" class="panel subfields-green" action="" method="post">
      <input type="hidden" id="ind-fase" name="ind-fase" value="${fase.id}" />
      <div class="panel-heading bgAct15">
        <div class="noHeader">
          <i class="fa-solid fa-pen-ruler"></i>&nbsp;
          <c:out value="Inserimento indicatore" />
          <a href="${initParam.appName}/?q=ic&p=ind&mliv=${meas.codice}&r=${param['r']}&y=${param['y']}" >
            <span class="badge badge-primary float-right">
              <c:out value="${param['y']}" />
            </span>
          </a>
        </div>
      </div>
      <hr class="separatore" />
      <div class="panel-body">
        <div class="row"> 
          <div class="content-holder col-sm-10 bgAct1">
            <strong> &nbsp;Misura:</strong> 
            <a href="${initParam.appName}/?q=ic&p=mes&mliv=${meas.codice}&r=${param['r']}" title="Dettagli della misura ${meas.codice}">
              <c:out value="${meas.nome}" />
            </a>
          </div>
        </div>
        <div class="row"> 
          <div class="content-holder col-sm-10 bgAct">
            <strong> &nbsp;Fase Indicatore:</strong>
            <a href="${initParam.appName}/?q=ic&p=mes&mliv=${meas.codice}&r=${param['r']}#details" title="Dettagli monitoraggio">
              <c:out value="${fase.nome}" escapeXml="false" />
            </a>
          </div>
        </div>
        <div class="row"> 
          <div class="content-holder col-sm-10 bgAct19">
         <c:choose>
          <c:when test="${not empty fase.indicatoreOld}">
            <c:set var="type" value="${fase.indicatoreOld.tipo.id}" scope="page" />
            <c:choose>
            <c:when test="${fase.indicatoreOld.master}">
              <c:set var="master" value="selected" scope="page" />
            </c:when>
            <c:when test="${not fase.indicatoreOld.master}">
              <c:set var="slave" value="selected" scope="page" />
            </c:when>
            </c:choose>
            <strong> &nbsp;Indicatore (${fase.indicatoreOld.targetRivisto}):</strong>
            <a href="${initParam.appName}/?q=ic&p=ind&idI=${fase.indicatoreOld.id}&idF=${fase.id}&mliv=${meas.codice}&r=${param['r']}" title="Dettagli dell`indicatore precedente">
              <c:out value="${fase.indicatoreOld.nome}" escapeXml="false" />
            </a>
            <c:if test="${fase.indicatoreOld.master}">
              <img src="${initParam.urlDirectoryImmagini}ind-master.png" class="imgTop" alt="icona master" title="Indicatore di riferimento ai fini del monitoraggio" /> &nbsp;
            </c:if>
           </c:when>
           <c:otherwise>
           <strong> &nbsp;Indicatore (${param['y'] - 1}):</strong>
            Nessun indicatore precedente
           </c:otherwise>
         </c:choose>
          </div>
        </div>
        <hr class="separatore">
        <div class="row">
          <div class="col-sm-1">&nbsp;</div>
          <div class="col-sm-4 mandatory-thin bgAct13">
            <label for="ind-tipo"><strong>Tipo Indicatore</strong></label>
          </div>
          <div class="col-sm-6">
            <select class="form-custom large-4" id="ind-tipo" name="ind-tipo" required>
              <option value=''>-- seleziona un tipo --</option>
            <c:forEach var="tipo" items="${requestScope.tipi}" varStatus="status">
              <c:if test="${tipo.id eq type}">
                <c:set var="selected" value="selected" scope="page" />
              </c:if>
              <option value="${tipo.id}" ${selected}>
                <c:out value="${tipo.nome}" />
              </option>
              <c:set var="selected" value="" scope="page" />
            </c:forEach>
            </select>
          </div>
        </div>
        <hr class="separatore">
        <div class="row">
          <div class="col-sm-1">&nbsp;</div>
          <div class="col-sm-4 mandatory-thin bgAct13"><strong>Nome Indicatore</strong></div>
          <div class="col-sm-6">
            <input type="text" class="form-control" id="ind-nome" name="ind-nome" value="${fase.indicatoreOld.nome} (${param['y']})" placeholder="Inserisci una nome per l`indicatore" required>
          </div>
        </div>
        <hr class="separatore">
        <div class="row">
          <div class="col-sm-1">&nbsp;</div>
          <div class="col-sm-4 bg-note"><strong>Descrizione</strong></div>
          <div class="col-sm-6">
            <textarea class="form-control" name="ind-descr" placeholder="Inserisci una descrizione per l`indicatore"><c:out value="${fase.indicatoreOld.descrizione}" /></textarea>
          </div>
        </div>
        <hr class="separatore">
        <div class="row">
          <div class="col-sm-1">&nbsp;</div>
          <div class="col-sm-4 mandatory-thin bgAct13"><strong>Baseline</strong></div>
          <div class="col-sm-5" id="displayBase">
            <input type="text" class="form-control" id="ind-baseline" name="ind-baseline" placeholder="Inserisci valore baseline" required>
          </div>
        </div>
        <hr class="separatore">
        <div class="row">
          <div class="col-sm-1">&nbsp;</div>
          <div class="col-sm-4 mandatory-thin bgAct13"><strong>Data Baseline</strong></div>
          <div class="col-sm-5">
            <input type="text" class="form-control calendarData" id="ind-database" name="ind-database" placeholder="Inserisci data baseline" required>
          </div>
        </div>
        <hr class="separatore">
        <div class="row">
          <div class="col-sm-1">&nbsp;</div>
          <div class="col-sm-4 mandatory-thin bgAct13"><strong>Target</strong></div>
          <div class="col-sm-5" id="displayTarget">
            <input type="text" class="form-control" id="ind-target" name="ind-target" placeholder="Inserisci valore target" required>
          </div>
        </div>
        <hr class="separatore">
        <div class="row">
          <div class="col-sm-1">&nbsp;</div>
          <div class="col-sm-4 mandatory-thin bgAct13"><strong>Data Target</strong></div>
          <div class="col-sm-5">
            <input type="text" class="form-control calendarData" id="ind-datatarget" name="ind-datatarget" placeholder="Inserisci data target" required>
          </div>
        </div>
        <hr class="separatore">
        <div class="row">
          <div class="col-sm-1">&nbsp;</div>
          <div class="col-sm-4 mandatory-thin bgAct13"><strong>Indicatore Semplice/Master</strong></div>
          <div class="col-sm-5">
            <select class="form-custom large-4" id="ind-master" name="ind-master" required>
              <option value="">-- seleziona un valore --</option>
              <option value="0" ${slave}>semplice</option>
              <option value="1" ${master}>master</option>
            </select>
          </div>
        </div>
        <hr class="separatore" />
        <div class="centerlayout">
          <button type="submit" class="btn btnNav" id="btn-save" value="Save">
            <i class="far fa-save"></i> Salva
          </button>
        </div>
      </div>
    </form>
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
            },
            'ind-master': {
                required: true
            }
          }, 
          messages: {
            'ind-tipo':       "Inserire il tipo dell'indicatore",
            'ind-nome':       "Inserire almeno " + offsetcharacter + " caratteri",
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
            'ind-datatarget': "Inserire la data target",
            'ind-master':     "Indicare se trattasi di indicatore master"
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
