<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="URL.jspf" %>
<c:set var="structs" value="${requestScope.strutture}" scope="page" />
<c:set var="structsByCode" value="${requestScope.elencoStrutture}" scope="page" />
<c:set var="dir" value="${structs.get(zero)}" />
<c:set var="cen" value="${structs.get(zero+1)}" />
<c:set var="dip" value="${structs.get(zero+2)}" />
<c:set var="process" value="${requestScope.pat}" scope="page" />
<c:set var="fasi" value="${requestScope.listaFasi}" scope="page" />
    <link rel="stylesheet" href="//code.jquery.com/ui/1.13.2/themes/base/jquery-ui.css">
    <div class="form-custom bg-note">
      <form accept-charset="ISO-8859-1" id="ias-form" action="" method="post">
        <div class="panel-heading bgAct6" id="details">
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
        <c:forEach var="fase" items="${fasi}" varStatus="status">
          
          <div class="row ">&nbsp;</div>
          <div class="content-holder col-sm-10">
            <div class="str-container">
              <div class="reportRow ">
                <span class="bgAct2">&nbsp;Fase <c:out value="${status.count}" />:</span>
                &nbsp;<c:out value="${fase.nome}" />
              </div>
              <hr class="separapoco" />
              <div class="bgAct26 text-white form-custom">
                <strong> 
                  &nbsp;Strutture
                  <span class="badge badge-pill float-right"><c:out value="${fase.strutture.size()}" /> </span>
                </strong>
              </div>
              <ul>
              <c:forEach var="str" items="${fase.strutture}">
                <li><c:out value="${str.nome}" escapeXml="false" /></li>
              </c:forEach>
              </ul>
              <div id="callable-row">
                <div class="row">
                  <div class="col-3 large-4 ui-widget">
                    <input class="sLiv1" name="sgliv1" type="text" placeholder="Struttura I livello">
                  </div>
                  <div class="col-3 large-4 ui-widget">
                    <input class="sLiv2" name="sgliv2" type="text" placeholder="Struttura II livello">
                  </div>
                  <div class="col-3 large-4 ui-widget">
                    <input class="sLiv3" name="sgliv3" type="text" placeholder="Struttura III livello">
                  </div>
                  <div class="col-3 large-4 ui-widget">
                    <input class="sLiv4" name="sgliv4" type="text" placeholder="Struttura IV livello">
                  </div>
                </div>
              </div>
              <hr class="separatore" />
            </div>
            <div class="str-container">
              <div class="bgAct26 text-white form-custom">
                <strong> 
                  &nbsp;Soggetti
                  <span class="badge badge-pill float-right"><c:out value="${fase.soggetti.size()}" /> </span>
                </strong>
              </div>
              <ul>
              <c:forEach var="sub" items="${fase.soggetti}">
                <li><c:out value="${sub.nome}" escapeXml="false" />: <c:out value="${sub.informativa}" escapeXml="false" /></li>
              </c:forEach>
              </ul>
              <div id="callable-row">
                <div class="row">
                  <div class="col-12 large-4 ui-widget">
                    <input class="sLiv1" name="sgliv1" type="text" placeholder="Soggetto Contingente">
                  </div>
                </div>
              </div>
            </div>
            <hr class="separapoco" />
            
          </div>
      </c:forEach>
          &nbsp;
          <div class="centerlayout">
            <button type="submit" class="btn btn-success bgAct18" id="btn-save" value="Save">
              <i class="far fa-save"></i> Salva
            </button>
          </div>
        </div>
        <hr class="separatore" />
      </form>
    </div>
    <script>
    function blank() {
        return "<option value=''>-- Nessuna --</option>";
    }
    </script>
<c:set var="singleQuote" value="'" scope="page" />
<c:set var="singleQuoteEsc" value="''" scope="page" />
<c:set var="doubleQuote" value='"' scope="page" />
<c:set var="doubleQuoteEsc" value='\\"' scope="page" />
<c:set var="nullQuoteEsc" value="" scope="page" />
<script>
$(document).ready(function() {
    let struttureLiv1 = [
      "${dir.nome} (${dir.id})",
      "${cen.nome} (${cen.id})",
      "${dip.nome} (${dip.id})"
    ];
    let struttureLiv2 = [
    <c:forEach var="l2" items="${dir.figlie}">
      "<c:if test="${not empty l2.prefisso}">${l2.prefisso} </c:if>${l2.nome} (${l2.id})",
    </c:forEach>
    <c:forEach var="l2" items="${cen.figlie}">
      <c:set var="l2nome" value="${fn:replace(l2.nome, singleQuote, singleQuoteEsc)}" scope="page" />
      <c:set var="l2nome" value="${fn:replace(l2nome, doubleQuote, nullQuoteEsc)}" scope="page" />
      "<c:if test="${not empty l2.prefisso}">${l2.prefisso} </c:if>${l2nome} (${l2.id})",
    </c:forEach>
    <c:forEach var="l2" items="${dip.figlie}">
      "<c:if test="${not empty l2.prefisso}">${l2.prefisso} </c:if>${l2.nome} (${l2.id})",
    </c:forEach>
    ];
    let struttureLiv3 = [
        <c:forEach var="l2" items="${dir.figlie}">
          <c:forEach var="l3" items="${l2.figlie}">
          "<c:if test="${not empty l3.prefisso}">${l3.prefisso} </c:if>${l3.nome} (${l3.id})",
          </c:forEach>
        </c:forEach>
        <c:forEach var="l2" items="${cen.figlie}">
          <c:forEach var="l3" items="${l2.figlie}">
          <c:set var="l3nome" value="${fn:replace(l3.nome, doubleQuote, nullQuoteEsc)}" scope="page" />
          "<c:if test="${not empty l3.prefisso}">${l3.prefisso} </c:if>${l3nome} (${l3.id})",
          </c:forEach>
        </c:forEach>
        <c:forEach var="l2" items="${dip.figlie}">
          <c:forEach var="l3" items="${l2.figlie}">
          "<c:if test="${not empty l3.prefisso}">${l3.prefisso} </c:if>${l3.nome} (${l3.id})",
          </c:forEach>
        </c:forEach>
        ];
    let struttureLiv4 = [
        <c:forEach var="l2" items="${dir.figlie}">
          <c:forEach var="l3" items="${l2.figlie}">
            <c:forEach var="l4" items="${l3.figlie}">
            "<c:if test="${not empty l4.prefisso}">${l4.prefisso} </c:if>${l4.nome} (${l4.id})",
            </c:forEach>
          </c:forEach>
        </c:forEach>
        <c:forEach var="l2" items="${cen.figlie}">
          <c:forEach var="l3" items="${l2.figlie}">
            <c:forEach var="l4" items="${l3.figlie}">
            <c:set var="l4nome" value="${fn:replace(l4.nome, doubleQuote, nullQuoteEsc)}" scope="page" />
            "<c:if test="${not empty l4.prefisso}">${l4.prefisso} </c:if>${l4nome} (${l4.id})",
            </c:forEach>
          </c:forEach>
        </c:forEach>
        <c:forEach var="l2" items="${dip.figlie}">
          <c:forEach var="l3" items="${l2.figlie}">
            <c:forEach var="l4" items="${l3.figlie}">
            "<c:if test="${not empty l4.prefisso}">${l4.prefisso} </c:if>${l4.nome} (${l4.id})",
            </c:forEach>
          </c:forEach>
        </c:forEach>
        ];
    $( function() {
      $(".sLiv1").autocomplete({
          source: struttureLiv1,
          minLength: 1
        });
      $(".sLiv2").autocomplete({
          source: struttureLiv2,
          minLength: 2
        });
      $(".sLiv3").autocomplete({
          source: struttureLiv3,
          minLength: 2
        });
      $(".sLiv4").autocomplete({
          source: struttureLiv4,
          minLength: 2
        });
    });
    $('.js-add-row').on('click', function () {
        $('.str-container').append($('#callable-row').html());
        $('.sLiv1').autocomplete({
            source: struttureLiv1
          });
        $(".sLiv2").autocomplete({
            source: struttureLiv2,
            minLength: 2
          });
        $(".sLiv3").autocomplete({
            source: struttureLiv3,
            minLength: 2
          });
        $(".sLiv4").autocomplete({
            source: struttureLiv4,
            minLength: 2
          });
    });
    $('.js-remove-row').on('click', function () {
        var LastSiblings = $('#callable-row').siblings('.row:last-child');
        if (LastSiblings.length != 0) {
            LastSiblings.remove();
        } else {
            alert('Occorre prima aggiungere i campi');
        }
    });

    $('#btn-save').click(function (e){
        e.preventDefault;
      });
    
});
</script>
