<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="URL.jspf" %>
<fmt:formatDate var="datasistema" value="${requestScope.now}" pattern="dd/MM/yyyy" scope="page"/>
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
      <form accept-charset="ISO-8859-1" id="inm-form" action="" method="post">
        <input type="hidden" id="mat-area" name="pliv0" value="${param['pliv0']}" />
        <input type="hidden" id="mat-code" name="pliv1" value="${param['pliv1']}" />
        <input type="hidden" id="pat-code" name="pliv2" value="${process.id}" />
        <div class="panel-heading bgAct17" id="details">
          <h5 class="fw-bold text-dark">
            <i class="fa-solid fa-file-circle-plus"></i>
            <c:out value="Aggiunta Input" />
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
            <div class="content-holder col-sm-10 bgAct23">
              <strong> &nbsp;Input gi&agrave; collegati:</strong>
              <c:if test="${empty process.inputs}">NESSUNO</c:if>
              <ul>
              <c:forEach var="input" items="${process.inputs}">
                <li><c:out value="${input.nome}" escapeXml="false" /></li>
              </c:forEach>
              </ul>
            </div>
          </div>
          <hr class="separatore" />
          <div class="row">
            <div class="content-holder col-sm-10 bgAct4">
              <div class="inp-container">
                <div class="reportRow">&nbsp;&nbsp;Scelta Input esistenti</div>
                <hr class="separapoco" />
                <div id="callable-inp">
                  <div class="row">
                    <div class="col-sm-4 bgAct23 lastMenuContent marginLeftSmall">
                      <strong>Input</strong>
                    </div>
                    <div class="col-sm-7">
                      <input type="search" class="form-control sInp" id="in-nome" name="in-name" placeholder="Cerca Input...">
                    </div>
                    <hr class="separatore" />
                  </div>
                </div>
              </div>
              <div class="row lblca">
                <div class="col-sm-1">&nbsp;</div>
                <div class="large-4 column">
                  <a href="javascript:void(0);" type="button" class="js-add-inp btn bgAct14" title="Aggiungi un input di processo">+ Aggiungi</a>
                </div>
                <div class="large-4 column">
                  <a href="javascript:void(0);" type="button" class="js-remove-inp btn bgAct25" title="Elimina l'ultimo input di processo">- Elimina</a>
                </div>
              </div>
            </div>
          </div>
          <br />
          <hr class="separatore" />
          <div class="content-holder col-sm-10">
            <div class="fas-container">
              <div class="reportRow bgAct31">&nbsp;&nbsp;Inserimento nuovi Input</div>
              <hr class="separapoco" />
              <div id="callable-row">
                <div class="row">
                  <div class="col-12 large-4">
                    <input type="text" class="form-control sIns" id="in-nuovo" name="in-newn" placeholder="Inserisci nome Input">
                    <div id="custom-error-location-2"></div>
                  </div>
                  <hr class="separapoco" />
                  <div class="row">
                    <div class="col-8 large-4">
                      <textarea class="form-control" id="in-desc" name="in-desc" placeholder="Inserisci una descrizione"></textarea>
                      <div class="charNum"></div>
                    </div>
                    <div class="col-4 large-4">
                      <select id="in-tipo" name="in-type" class="form-control custom-label dropdown">
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
                <a href="javascript:void(0);" type="button" class="js-add-row btn bgAct14" title="Aggiungi un input di processo">+ Aggiungi</a>
              </div>
              <div class="large-4 column">
                <a href="javascript:void(0);" type="button" class="js-remove-row btn bgAct25" title="Elimina l'ultimo input di processo">- Elimina</a>
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
            <c:forEach var="inp" items="${requestScope.listaInput}">
              "${inp.nome}",
            </c:forEach>
            ];
        let inputs = [
            <c:forEach var="inp" items="${requestScope.listaInput}">
              "${inp.nome} \u200B \u200B \u200B \u200B \u200B \u200B (${inp.id})",
            </c:forEach>
            ];
        
        $( function() {
            $(".sInp").autocomplete({
                source: inputs,
                minLength: 1
            });
            $(".sIns").autocomplete({
                source: names,
                minLength: 1
            });
        });
        
        $('.js-add-inp').on('click', function () {
            $('.inp-container').append($('#callable-inp').html());
            $(".sInp").autocomplete({
                source: inputs,
                minLength: 1
            });
        });
        $('.js-remove-inp').on('click', function () {
            var LastSibling = $('#callable-inp').siblings('.row:last-child');
            if (LastSibling.length != 0) {
                LastSibling.remove();
            } else {
                alert('Occorre prima aggiungere un campo');
            }
        });        
        
        $('.js-add-row').on('click', function () {
            $('.fas-container').append($('#callable-row').html());
            $(".sIns").autocomplete({
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

