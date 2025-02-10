<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="URL.jspf" %>
<fmt:formatDate var="datasistema" value="${requestScope.now}" pattern="dd/MM/yyyy" scope="page"/>
    <link rel="stylesheet" href="//code.jquery.com/ui/1.13.2/themes/base/jquery-ui.css">
    <style>
        .form-control::placeholder {
            color: #6c757d; /* Default placeholder color */
            opacity: 1; /* Ensure full opacity by default */
        }
        .form-control:focus::placeholder {
            visibility: hidden; /* Hide placeholder on focus */
        }
        input[type="search"] {
            padding-left: 30px; /* Adjust for icon width */
            background-image: url("${initParam.urlDirectoryImmagini}ico-search.png"); /* Replace with your icon */
            background-repeat: no-repeat;
            background-position: 5px center; /* Adjust position as needed */
            background-size: 20px 20px; /* Set width and height of the icon */
            border: none;
        }
        
        input[type="search"]::placeholder {
            color: #bbb;
        }
        
        .dropdown {
          padding-left: 30px; /* Adjust for icon width */
          background-image: url("${initParam.urlDirectoryImmagini}ico-dropdown.png"); /* Replace with your icon */
          background-repeat: no-repeat;
          background-position: 5px center; /* Adjust position as needed */
          background-size: 20px 20px; /* Set width and height of the icon */
          border: none;
        }

    </style>
    <div class="form-custom bg-note">
      <form accept-charset="ISO-8859-1" id="inm-form" action="" method="post">
        <input type="hidden" id="ms-code" name="ms-code" value="${meas.codice}" />
        <div class="panel-heading bgAct17" id="details">
          <h5 class="fw-bold text-dark">
            <i class="fa-solid fa-file-circle-plus"></i>
            <c:out value="Aggiunta Input" />
          </h5>
        </div>
        <div class="panel-body">
          <br />
          <div class="row">
            <div class="col-sm-1">&nbsp;</div>
            <div class="col-sm-4 mandatory-thin"><strong>Input</strong></div>
            <div class="col-sm-6">
            <input type="search" class="form-control sInp" id="ms-date" name="ms-date" placeholder="Cerca Input...">
            
            </div>
            <div class="col-sm-1">&nbsp;</div>
          </div>
          <br />
          <hr class="separatore" />
          <div class="content-holder col-sm-10">
            <div class="fas-container">
              <div id="callable-row">
                <div class="row">
                  <div class="col-12 large-4">
                    <input type="text" class="form-control sInp" id="ms-fasi" name="ms-fasi" placeholder="Inserisci nome Input">
                    <div id="custom-error-location-2"></div>
                  </div>
                  <hr class="separapoco" />
                  <div class="row">
                    <div class="col-8 large-4">
                      <textarea class="form-control" id="ms-piao" name="ms-piao" placeholder="Inserisci una descrizione"></textarea>
                      <div class="charNum"></div>
                    </div>
                    <div class="col-4 large-4">
            <select id="ms-char" name="ms-char" class="form-control custom-label dropdown">
              <option value="0">Input esterno</option>
              <option value="1">Input interno</option>
            
            </select>
                    </div>
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
  
            <div class="col-sm-12">
            <button type="submit" class="btn btnNav align-left" id="btn-save" name="action" value="save">
              <i class="far fa-save"></i> Salva ed esci
            </button>
            <button type="submit" class="btn btnNav bgAct14 float-right" id="btn-cont" name="action" value="cont">
              <i class="far fa-save"></i>  Salva e continua  <i class="fa-solid fa-circle-chevron-right"></i>
            </button>
            </div>
          </div>
        </div>
      </form>
    </div>
    <script>
    $(document).ready(function() {
        let inputs = [
            <c:forEach var="inp" items="${requestScope.listaInput}">
              "${inp.nome} (${inp.id})",
            </c:forEach>
            ];
        $( function() {
            $(".sInp").autocomplete({
                source: inputs,
                minLength: 1
            });
        });    
        $('.js-add-row').on('click', function () {
            $('.fas-container').append($('#callable-row').html());
            $(".sInp").autocomplete({
                source: inputs,
                minLength: 1
            });
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
              /*'ms-piao': {
                required: true
              },*/
              'ms-fasi': {
                required: true
              }
            }, 
            messages: {
              //'ms-piao': "Inserire l'obiettivo PIAO",
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
