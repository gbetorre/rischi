<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="fase" value="${requestScope.phase}" scope="page" />
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
        </div>
      </div>
      <hr class="separatore" />
      <div class="panel-body">
        <div class="row"> 
          <div class="content-holder col-sm-10 bgAct">
            <strong> &nbsp;Fase Indicatore:</strong> 
            <c:out value="${fase.nome}" escapeXml="false" />
          </div>
        </div>
        <hr class="separatore">
        <div class="row">
          <div class="col-sm-1">&nbsp;</div>
          <div class="col-sm-4 mandatory-thin bgAct13">
            <label for="ind-tipo"><strong>Tipo Indicatore</strong></label>
          </div>
          <div class="col-sm-6">
            <select class="form-custom large-4" id="ind-tipo" name="ind-tipo">
              <option value=''>-- seleziona un tipo --</option>
            <c:forEach var="tipo" items="${requestScope.tipi}" varStatus="status">
              <option value="${tipo.id}"><c:out value="${tipo.nome}" /></option>
            </c:forEach>
            </select>
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
          <div class="col-sm-4 bg-note"><strong>Descrizione</strong></div>
          <div class="col-sm-6">
            <textarea class="form-control" name="ind-descr" placeholder="Inserisci una descrizione per l'indicatore"></textarea>
          </div>
        </div>
        <hr class="separatore">
        <div class="row">
          <div class="col-sm-1">&nbsp;</div>
          <div class="col-sm-4 mandatory-thin bgAct13"><strong>Baseline</strong></div>
          <div class="col-sm-5" id="displayBase">
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
          <div class="col-sm-5" id="displayTarget">
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
