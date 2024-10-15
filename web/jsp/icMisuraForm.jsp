<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:formatDate var="datasistema" value="${requestScope.now}" pattern="dd/MM/yyyy" scope="page"/>
<%@ include file="URL.jspf" %>
<%@ include file="msMisura.jsp"%>
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
      <form accept-charset="ISO-8859-1" id="inm-form" action="" method="post">
        <input type="hidden" id="ms-code" name="ms-code" value="${meas.codice}" />
        <div class="panel-heading bgAct19">
          <h5 class="fw-bold text-dark">
            <i class="fa-solid fa-file-circle-plus"></i>
            <c:out value="Inserimento dettagli monitoraggio" />
          </h5>
        </div>
        <div class="panel-body">
          <br />
          <div class="row">
            <div class="col-sm-1">&nbsp;</div>
            <div class="col-sm-4 mandatory-thin"><strong>Data inserimento</strong></div>
            <div class="col-sm-4">
              <input type="text" class="form-control" id="ms-date" name="ms-date" value="${datasistema}" readonly>
            </div>
            <div class="col-sm-1">&nbsp;</div>
          </div>
          <br />
          <div class="row">
            <div class="col-sm-1">&nbsp;</div>
            <div class="col-sm-4 mandatory-thin"><strong>Obiettivo PIAO</strong></div>
            <div class="col-sm-6">
              <textarea class="form-control" id="ms-piao" name="ms-piao" placeholder="Inserisci una descrizione dell'obiettivo PIAO"></textarea>
              <div id="custom-error-location-1"></div>
              <div class="charNum"></div>
            </div>
          </div>
          <div class="content-holder col-sm-10">
            <div class="fas-container">
              <div class="mandatory-thin">
                <strong> &nbsp;Fasi di attuazione</strong>
              </div>
              <hr class="separatore" />
              <div id="callable-row">
                <div class="row">
                  <div class="col-12 large-4">
                    <input type="text" class="form-control" id="ms-fasi" name="ms-fasi" placeholder="Inserisci la fase">
                    <div id="custom-error-location-2"></div>
                  </div>
                  <hr class="separatore" />
                </div>
              </div>
            </div>
            <div class="row lblca">
              <div class="col-sm-1">&nbsp;</div>
              <div class="large-4 column">
                <a href="javascript:void(0);" type="button" class="js-add-row btn bgAct14" title="Aggiungi una fase di attuazione">+ Aggiungi</a>
              </div>
              <div class="large-4 column">
                <a href="javascript:void(0);" type="button" class="js-remove-row btn bgAct25" title="Elimina l'ultima fase di attuazione">- Elimina</a>
              </div>
            </div>
          </div>
          <hr class="separapoco" />
          <div class="row">
            <div class="centerlayout">
<!--               <button type="submit" class="btn btn-success bgAct18" id="btn-save" value="Save" title="Salva i dettagli del monitoraggio senza indicatori"> -->
<!--                 <i class="fa-regular fa-square-caret-right"></i> Salva -->
<!--               </button> -->
              <div class="float-right">
                <button type="submit" class="btn btn-save bgAct18" id="btn-save" value="Save" title="Salva i dettagli del monitoraggio e vai alla pagina di aggiunta indicatori">
                <i class="far fa-save"></i>  Salva e continua 
                </button>
              </div>
            </div>
          </div>
        </div>
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

        $('#inm-form').validate ({
            rules: {
              'ms-date': {
                required: true
              },
              'ms-piao': {
                required: true
              },
              'ms-fasi': {
                required: true
              }
            }, 
            messages: {
              'ms-piao': "Inserire l'obiettivo PIAO",
              'ms-fasi': "Inserire almeno una fase di attuazione"
            },
            errorPlacement: function(error, element) {
                if (element.attr("name") == "ms-piao") {
                    error.insertAfter("#custom-error-location-1"); // Place error message after a specific element
                } else if (element.attr("name") == "ms-fasi") {
                    error.insertAfter("#custom-error-location-2"); // Place error message after another specific element
                } else {
                    error.insertAfter(element); // Default placement for other elements
                }
            },
            submitHandler: function (form) {
              return true;
            }
          });
        
          $('#ms-piao').keyup(function (e) {
              var chars = $(this).val().length;
              $(this).next('div').text(chars + ' caratteri inseriti');
          });
          
    });
    </script>
