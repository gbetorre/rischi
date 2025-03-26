<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="URL.jspf" %>
<c:set var="process" value="${requestScope.pat}" scope="page" />
<c:choose>
  <c:when test="${not empty process}">
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
      <form accept-charset="ISO-8859-1" id="out-form" action="" method="post">
        <input type="hidden" id="mat-area" name="pliv0" value="${param['pliv0']}" />
        <input type="hidden" id="mat-code" name="pliv1" value="${process.padre.id}" />
        <input type="hidden" id="pat-code" name="pliv2" value="${process.id}" />
        <div class="panel-heading bgAct13" id="details">
          <h5 class="fw-bold text-dark">
            <i class="fa-solid fa-file-circle-plus"></i>
            <c:out value="Aggiunta Output" />
          </h5>
        </div>
        <div class="panel-body">
          <div class="row"> 
            <div class="content-holder col-sm-10 bgAct15">
              <strong> &nbsp;Macroprocesso:</strong>
              <c:out value="${process.padre.nome}" escapeXml="false" />
            </div>
          </div>
          <hr class="separapoco" />
          <div class="row"> 
            <div class="content-holder col-sm-10 bgAct19">
              <strong> &nbsp;Processo:</strong>
              <a href="${initParam.appName}/?q=pr&p=pro&pliv=${process.id}&liv=2&r=${param['r']}" title="${process.codice}">
                <c:out value="${process.nome}" escapeXml="false" />
              </a>
            </div>
          </div>
          <hr class="separapoco" />
          <div class="row"> 
            <div class="content-holder col-sm-10 bg-note">
              <strong> &nbsp;Output gi&agrave; collegati:</strong>
              <c:if test="${empty process.outputs}">NESSUNO</c:if>
              <ul>
              <c:forEach var="output" items="${process.outputs}">
                <li><c:out value="${output.nome}" escapeXml="false" /></li>
              </c:forEach>
              </ul>
            </div>
          </div>
          <hr class="separatore" />
          <div class="row">
            <div class="content-holder col-sm-10 bgAct4">
              <div class="out-container">
                <div class="reportRow">&nbsp;&nbsp;Scelta Output esistenti</div>
                <hr class="separapoco" />
                <div id="callable-out">
                  <div class="row">
                    <div class="col-sm-4 bgAct16 lastMenuContent marginLeftSmall">
                      <strong>Output</strong>
                    </div>
                    <div class="col-sm-7">
                      <input type="search" class="form-control sOut" id="ou-nome" name="ou-name" placeholder="Cerca Output...">
                    </div>
                    <hr class="separatore" />
                  </div>
                </div>
              </div>
              <div class="row lblca">
                <div class="col-sm-1">&nbsp;</div>
                <div class="large-4 column">
                  <a href="javascript:void(0);" type="button" class="js-add-out btn bgAct14" title="Aggiungi un output di processo">+ Aggiungi</a>
                </div>
                <div class="large-4 column">
                  <a href="javascript:void(0);" type="button" class="js-remove-out btn bgAct25" title="Elimina l'ultimo output di processo">- Elimina</a>
                </div>
              </div>
            </div>
          </div>
          <br />
          <hr class="separatore" />
          <div class="content-holder col-sm-10">
            <div class="fas-container">
              <div class="reportRow bgAct31">&nbsp;&nbsp;Inserimento nuovi Output</div>
              <hr class="separapoco" />
              <div id="callable-row">
                <div class="row">
                  <div class="col-12 large-4">
                    <input type="text" class="form-control sIno" id="ou-nuovo" name="ou-newn" placeholder="Inserisci nome Output">
                    <div id="custom-error-location-2"></div>
                  </div>
                  <hr class="separapoco" />
                  <div class="row">
                    <div class="col-12 large-4">
                      <textarea class="form-control" id="ou-desc" name="ou-desc" placeholder="Inserisci una descrizione"></textarea>
                      <div class="charNum"></div>
                    </div>
                  </div>
                  <hr class="separatore" />
                </div>
              </div>
            </div>
            <div class="row lblca">
              <div class="col-sm-1">&nbsp;</div>
              <div class="large-4 column">
                <a href="javascript:void(0);" type="button" class="js-add-row btn bgAct14" title="Aggiungi un output di processo">+ Aggiungi</a>
              </div>
              <div class="large-4 column">
                <a href="javascript:void(0);" type="button" class="js-remove-row btn bgAct25" title="Elimina l'ultimo output di processo">- Elimina</a>
              </div>
            </div>
          </div>
          <hr class="separatore" />
          <%@ include file="btnSaveCont.jspf"%>
        </div>
        <hr class="separatore" />
      </form>
    </div>
    <script>
    $(document).ready(function() {
        let names = [
            <c:forEach var="out" items="${requestScope.listaOutput}">
              "${out.nome}",
            </c:forEach>
            ];
        let inputs = [
            <c:forEach var="out" items="${requestScope.listaOutput}">
              "${out.nome} \u200B \u200B \u200B \u200B \u200B \u200B (${out.id})",
            </c:forEach>
            ];
        
        $( function() {
            $(".sOut").autocomplete({
                source: inputs,
                minLength: 1
            });
            $(".sIno").autocomplete({
                source: names,
                minLength: 1
            });
        });
        
        $('.js-add-out').on('click', function () {
            $('.out-container').append($('#callable-out').html());
            $(".sOut").autocomplete({
                source: inputs,
                minLength: 1
            });
        });
        $('.js-remove-out').on('click', function () {
            var LastSibling = $('#callable-out').siblings('.row:last-child');
            if (LastSibling.length != 0) {
                LastSibling.remove();
            } else {
                alert('Occorre prima aggiungere un campo');
            }
        });        
        
        $('.js-add-row').on('click', function () {
            $('.fas-container').append($('#callable-row').html());
            $(".sIno").autocomplete({
                source: names,
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
          
    });
    </script>
  </c:when>
  <c:otherwise>
  <div class="alert alert-danger">
    <strong>Non esiste un processo con questo identificativo (${param['pliv']}).</strong>
    <hr class="separapoco" />
    <p>
      Dati fittizi o inconsistenti: non &egrave; possibile mostrare la pagina.<br/>
    </p>
  </div>
  </c:otherwise>
</c:choose>

