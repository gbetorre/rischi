<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="URL.jspf" %>
<c:set var="structs" value="${requestScope.strutture}" scope="page" />
<c:set var="structsByCode" value="${requestScope.elencoStrutture}" scope="page" />
<c:set var="dir" value="${structs.get(zero)}" />
<c:set var="cen" value="${structs.get(zero+1)}" />
<c:set var="dip" value="${structs.get(zero+2)}" />
<c:set var="types" value="${requestScope.tipiMisure}" scope="page" />
<c:set var="characters" value="${requestScope.caratteriMisure}" scope="page" />
    <link rel="stylesheet" href="//code.jquery.com/ui/1.13.2/themes/base/jquery-ui.css">
    <form accept-charset="ISO-8859-1" id="inm-form" class="panel subfields errorPwd" action="" method="post">
      <div class="panel-heading bgAct26">
        <div class="noHeader text-white">
          <i class="fa-solid fa-file-circle-plus"></i>
          <c:out value="Inserimento misura di mitigazione" />
        </div>
      </div>
      <hr class="separatore" />
      <div class="panel-body">
        <br />
        <div class="row">
          <div class="col-sm-1">&nbsp;</div>
          <div class="col-sm-4 mandatory-thin"><strong>Nome Misura</strong></div>
          <div class="col-sm-6">
            <input type="text" class="form-control" id="ms-name" name="ms-name" placeholder="Inserisci nome della nuova misura">
          </div>
        </div>
        <br />
        <div class="row">
          <div class="col-sm-1">&nbsp;</div>
          <div class="col-sm-4 bgAct26 text-white form-custom">Descrizione misura</div>
          <div class="col-sm-6">
            <textarea class="form-control" id="ms-desc" name="ms-desc" placeholder="Inserisci una descrizione della nuova misura"></textarea>
            <div class="charNum"></div>
          </div>
        </div>
        <br />
        <div class="row">
          <div class="col-sm-1">&nbsp;</div>
          <div class="col-sm-4 mandatory-thin"><strong>Carattere misura</strong></div>
          <div class="col-sm-6">
            <select id="ms-char" name="ms-char" class="custom-label">
              <option value="">-- scelta carattere della misura -- </option>
            <c:forEach var="character" items="${characters}" varStatus="status">
              <option value="${character.informativa}"><c:out value="${character.nome}" /></option>
            </c:forEach>
            </select>
          </div>
        </div>
        <br />
        <div class="row">
          <div class="col-sm-1">&nbsp;</div>
          <div class="col-sm-4 mandatory-thin"><strong>Sostenibilit&agrave; economica</strong></div>
          <div class="col-sm-6">
            <select id="ms-eco" name="ms-eco" class="custom-label">
              <option value="">--&nbsp; la misura comporta spese? &nbsp;-- </option>
              <option value="SI">SI</option>
              <option value="NO">NO</option>
              <option value="ND">Non Determinabile (al momento)</option>
            </select>
          </div>
        </div>
        <br />
        <div class="col-sm-1">&nbsp;</div>
        <div class="content-holder col-sm-10">
          <div class="column">
            <div class="mandatory-thin"><strong> &nbsp;Tipologia Misura</strong></div>
            <div class="text-center"><div id="custom-error-location"></div></div>
          </div>
          <div class="row">
            <div class="text-center">
            <c:forEach var="type" items="${types}" varStatus="status">
              <label class="custom-label">
                <input type="checkbox" id="ms-type${type.id}" name="ms-type${type.id}" value="${type.id}">
                <c:out value="${type.nome}" />
              </label>
            </c:forEach>
            </div>
          </div>
        </div>
        <br />
        <div class="col-sm-1">&nbsp;</div>
        <div class="content-holder col-sm-10">
          <div class="column">
            <div class="mandatory-thin"><strong> &nbsp;Struttura Capofila</strong></div>
            <div class="text-center"><div id="custom-error-location-2"></div></div>
          </div>
          <div class="row">
            <div class="text-center lastMenuContent">
              <select id="str-liv1" name="sliv1">
                <option value="">-- scelta tipologia struttura -- </option>
                <option value="${dir.extraInfo.codice}"><c:out value="${dir.nome}" /></option>
                <option value="${cen.extraInfo.codice}"><c:out value="${cen.nome}" /></option>
                <option value="${dip.extraInfo.codice}"><c:out value="${dip.nome}" /></option>
              </select>
            </div>
            <div class="text-center lastMenuContent">
              <select id="str-liv2" name="sliv2">
                <option value="">-- struttura II livello -- </option>
              </select>
            </div>
            <div class="text-center lastMenuContent">
              <select id="str-liv3" name="sliv3">
                <option value="">-- struttura III livello -- </option>
              </select>
            </div>
            <div class="text-center lastMenuContent">
              <select id="str-liv4" name="sliv4">
                <option value="">-- struttura IV livello -- </option>
              </select>
            </div>
          </div>
        </div>
        <div class="col-sm-1">&nbsp;</div>
        <div class="accordion content-holder col-sm-10" id="accordion1">
          <div class="bgAct26 text-white form-custom" data-toggle="collapse" data-target="#collapse1" aria-expanded="false" aria-controls="collapse1" id="heading1">
            <strong>Struttura Capofila 2</strong>&nbsp;
            <span class="float-right" title="Clicca"><i class="fa-regular fa-square-caret-down"></i></span>
          </div>
          <div class="collapse" id="collapse1" aria-labelledby="heading1" data-parent="#accordion1">
            <div class="row">
              <div class="text-center lastMenuContent">
                <select id="str-liv5" name="sliv5">
                  <option value="">-- scelta tipologia struttura -- </option>
                  <option value="${dir.extraInfo.codice}"><c:out value="${dir.nome}" /></option>
                  <option value="${cen.extraInfo.codice}"><c:out value="${cen.nome}" /></option>
                  <option value="${dip.extraInfo.codice}"><c:out value="${dip.nome}" /></option>
                </select>
              </div>
              <div class="text-center lastMenuContent">
                <select id="str-liv6" name="sliv6">
                  <option value="">-- struttura II livello -- </option>
                </select>
              </div>
              <div class="text-center lastMenuContent">
                <select id="str-liv7" name="sliv7">
                  <option value="">-- struttura III livello -- </option>
                </select>
              </div>
              <div class="text-center lastMenuContent">
                <select id="str-liv8" name="sliv8">
                  <option value="">-- struttura IV livello -- </option>
                </select>
              </div>
            </div>
          </div>
        </div>
        <div class="col-sm-1">&nbsp;</div>
        <div class="accordion content-holder col-sm-10" id="accordion2">
          <div class="bgAct26 text-white form-custom" data-toggle="collapse" data-target="#collapse2" aria-expanded="false" aria-controls="collapse1" id="heading2">
            <strong>Struttura Capofila 3</strong>&nbsp;
            <span class="float-right" title="Clicca"><i class="fa-regular fa-square-caret-down"></i></span>
          </div>
          <div class="collapse" id="collapse2" aria-labelledby="heading2" data-parent="#accordion2">
            <div class="row">
              <div class="text-center lastMenuContent">
                <select id="str-liv9" name="sliv9">
                  <option value="">-- scelta tipologia struttura -- </option>
                  <option value="${dir.extraInfo.codice}"><c:out value="${dir.nome}" /></option>
                  <option value="${cen.extraInfo.codice}"><c:out value="${cen.nome}" /></option>
                  <option value="${dip.extraInfo.codice}"><c:out value="${dip.nome}" /></option>
                </select>
              </div>
              <div class="text-center lastMenuContent">
                <select id="str-liv10" name="sliv10">
                  <option value="">-- struttura II livello -- </option>
                </select>
              </div>
              <div class="text-center lastMenuContent">
                <select id="str-liv11" name="sliv11">
                  <option value="">-- struttura III livello -- </option>
                </select>
              </div>
              <div class="text-center lastMenuContent">
                <select id="str-liv12" name="sliv12">
                  <option value="">-- struttura IV livello -- </option>
                </select>
              </div>
            </div>
          </div>
        </div>
        <div class="col-sm-1">&nbsp;</div>
        <div class="content-holder col-sm-10">
          <div class="str-container">
            <div class="bgAct26 text-white form-custom">
              <strong> &nbsp;Strutture Gregarie</strong>
            </div>
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
                <hr class="separatore" />
              </div>
            </div>
          </div>
          <hr class="separatore" />
          <div class="row lblca">
            <div class="large-4 column ">
              <a href="javascript:void(0);" type="button" class="js-add-row btn btn-primary btn-small badge-pill">+ Strutture</a>
            </div>
            <div class="large-4 column">
              <a href="javascript:void(0);" type="button" class="js-remove-row btn btn-danger btn-small badge-pill">- Elimina</a>
            </div>
          </div>
        </div>
        <br />
        &nbsp;
        <div class="centerlayout">
          <button type="submit" class="btn btn-success" id="btn-save" value="Save">
            <i class="far fa-save"></i> Salva
          </button>
        </div>
      </div>
      <hr class="separatore" />
    </form>
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
    <!-- Struttura Capofila 1 -->
    $("#str-liv1").change(function() {
        var parent = $(this).val();
        var child2 = "#str-liv2";
        var child3 = "#str-liv3";
        var child4 = "#str-liv4";
        $(child2).html(blank());
        $(child3).html(blank());
        $(child4).html(blank());
        switch (parent) {
        <c:forEach var="l1" items="${structs}">
        case "${l1.extraInfo.codice}":
            $(child2).html("<c:forEach var="l2" items="${l1.figlie}"><c:set var="l2nome" value="${fn:replace(l2.nome, singleQuote, singleQuoteEsc)}" scope="page" /><option value='${l2.extraInfo.codice}'>${l2.prefisso} ${fn:replace(l2nome, doubleQuote, doubleQuoteEsc)}</option></c:forEach>");
            $(child3).html("<c:forEach var="l2" items="${l1.figlie}" begin="0" end="0"><c:forEach var="l3" items="${l2.figlie}"><c:set var="l3nome" value="${fn:replace(l3.nome, singleQuote, singleQuoteEsc)}" scope="page" /><option value='${l3.extraInfo.codice}'>${l3.prefisso} ${fn:replace(l3nome, doubleQuote, doubleQuoteEsc)}</option></c:forEach></c:forEach><option value=''>-- Nessuna --</option>");
            $(child4).html("<c:forEach var="l2" items="${l1.figlie}" begin="0" end="0"><c:forEach var="l3" items="${l2.figlie}" begin="0" end="0"><c:forEach var="l4" items="${l3.figlie}"><c:set var="l4nome" value="${fn:replace(l4.nome, singleQuote, singleQuoteEsc)}" scope="page" /><option value='${l4.extraInfo.codice}'>${l4.prefisso} ${fn:replace(l4nome, doubleQuote, doubleQuoteEsc)}</option></c:forEach></c:forEach></c:forEach><option value=''>-- Nessuna --</option>");
            break;
        </c:forEach>
        }
    });
    $("#str-liv2").change(function() {
        var parent = $(this).val();
        var child3 = "#str-liv3";
        var child4 = "#str-liv4";
        $(child3).html(blank());
        $(child4).html(blank());
        switch (parent) {
        <c:forEach var="entry" items="${structsByCode}">
        <c:choose>
          <c:when test="${fn:endsWith(entry.key, '-2') and not empty entry.value}">
          case "${entry.key}": 
              $(child3).html("<c:forEach var="str" items="${entry.value}"><c:set var="l3nome" value="${fn:replace(str.nome, singleQuote, singleQuoteEsc)}" scope="page" /><option value='${str.extraInfo.codice}'>${str.prefisso} ${fn:replace(l3nome, doubleQuote, doubleQuoteEsc)}</option></c:forEach><option value=''>-- Nessuna --</option>");
              $(child4).html("<c:forEach var="str" items="${entry.value}" begin="0" end="0"><c:forEach var="l4" items="${str.figlie}"><c:set var="l4nome" value="${fn:replace(l4.nome, singleQuote, singleQuoteEsc)}" scope="page" /><option value='${l4.extraInfo.codice}'>${l4.prefisso} ${fn:replace(l4nome, doubleQuote, doubleQuoteEsc)}</option></c:forEach></c:forEach><option value=''>-- Nessuna --</option>");
              break;
          </c:when>
          <c:when test="${fn:endsWith(entry.key, '-3') and not empty entry.value}">
          case "${entry.key}": 
              $(child4).html("<c:forEach var="l4" items="${entry.value}"><c:set var="l4nome" value="${fn:replace(l4.nome, singleQuote, singleQuoteEsc)}" scope="page" /><option value='${l4.extraInfo.codice}'>${l4.prefisso} ${fn:replace(l4nome, doubleQuote, doubleQuoteEsc)}</option></c:forEach><option value=''>-- Nessuna --</option>");
              break;
          </c:when>
          </c:choose>
        </c:forEach>
        }
    });
    $("#str-liv3").change(function() {
        var parent = $(this).val();
        var child4 = "#str-liv4";
        $(child4).html(blank());
        switch (parent) {
        <c:forEach var="entry" items="${structsByCode}">
        <c:choose>
          <c:when test="${fn:endsWith(entry.key, '-3') and not empty entry.value}">
          case "${entry.key}": 
              $(child4).html("<c:forEach var="l4" items="${entry.value}"><c:set var="l4nome" value="${fn:replace(l4.nome, singleQuote, singleQuoteEsc)}" scope="page" /><option value='${l4.extraInfo.codice}'>${l4.prefisso} ${fn:replace(l4nome, doubleQuote, doubleQuoteEsc)}</option></c:forEach><option value=''>-- Nessuna --</option>");
              break;
          </c:when>
        </c:choose>
        </c:forEach>
        }
    });
    <!-- Struttura Capofila 2 -->
    $("#str-liv5").change(function() {
        var parent = $(this).val();
        var child2 = "#str-liv6";
        var child3 = "#str-liv7";
        var child4 = "#str-liv8";
        $(child2).html(blank());
        $(child3).html(blank());
        $(child4).html(blank());
        switch (parent) {
        <c:forEach var="l1" items="${structs}">
        case "${l1.extraInfo.codice}":
            $(child2).html("<c:forEach var="l2" items="${l1.figlie}"><c:set var="l2nome" value="${fn:replace(l2.nome, singleQuote, singleQuoteEsc)}" scope="page" /><option value='${l2.extraInfo.codice}'>${l2.prefisso} ${fn:replace(l2nome, doubleQuote, doubleQuoteEsc)}</option></c:forEach>");
            $(child3).html("<c:forEach var="l2" items="${l1.figlie}" begin="0" end="0"><c:forEach var="l3" items="${l2.figlie}"><c:set var="l3nome" value="${fn:replace(l3.nome, singleQuote, singleQuoteEsc)}" scope="page" /><option value='${l3.extraInfo.codice}'>${l3.prefisso} ${fn:replace(l3nome, doubleQuote, doubleQuoteEsc)}</option></c:forEach></c:forEach><option value=''>-- Nessuna --</option>");
            $(child4).html("<c:forEach var="l2" items="${l1.figlie}" begin="0" end="0"><c:forEach var="l3" items="${l2.figlie}" begin="0" end="0"><c:forEach var="l4" items="${l3.figlie}"><c:set var="l4nome" value="${fn:replace(l4.nome, singleQuote, singleQuoteEsc)}" scope="page" /><option value='${l4.extraInfo.codice}'>${l4.prefisso} ${fn:replace(l4nome, doubleQuote, doubleQuoteEsc)}</option></c:forEach></c:forEach></c:forEach><option value=''>-- Nessuna --</option>");
            break;
        </c:forEach>
        }
    });
    $("#str-liv6").change(function() {
        var parent = $(this).val();
        var child3 = "#str-liv7";
        var child4 = "#str-liv8";
        $(child3).html(blank());
        $(child4).html(blank());
        switch (parent) {
        <c:forEach var="entry" items="${structsByCode}">
        <c:choose>
          <c:when test="${fn:endsWith(entry.key, '-2') and not empty entry.value}">
          case "${entry.key}": 
              $(child3).html("<c:forEach var="str" items="${entry.value}"><c:set var="l3nome" value="${fn:replace(str.nome, singleQuote, singleQuoteEsc)}" scope="page" /><option value='${str.extraInfo.codice}'>${str.prefisso} ${fn:replace(l3nome, doubleQuote, doubleQuoteEsc)}</option></c:forEach><option value=''>-- Nessuna --</option>");
              $(child4).html("<c:forEach var="str" items="${entry.value}" begin="0" end="0"><c:forEach var="l4" items="${str.figlie}"><c:set var="l4nome" value="${fn:replace(l4.nome, singleQuote, singleQuoteEsc)}" scope="page" /><option value='${l4.extraInfo.codice}'>${l4.prefisso} ${fn:replace(l4nome, doubleQuote, doubleQuoteEsc)}</option></c:forEach></c:forEach><option value=''>-- Nessuna --</option>");
              break;
          </c:when>
          <c:when test="${fn:endsWith(entry.key, '-3') and not empty entry.value}">
          case "${entry.key}": 
              $(child4).html("<c:forEach var="l4" items="${entry.value}"><c:set var="l4nome" value="${fn:replace(l4.nome, singleQuote, singleQuoteEsc)}" scope="page" /><option value='${l4.extraInfo.codice}'>${l4.prefisso} ${fn:replace(l4nome, doubleQuote, doubleQuoteEsc)}</option></c:forEach><option value=''>-- Nessuna --</option>");
              break;
          </c:when>
          </c:choose>
        </c:forEach>
        }
    });
    $("#str-liv7").change(function() {
        var parent = $(this).val();
        var child4 = "#str-liv8";
        $(child4).html(blank());
        switch (parent) {
        <c:forEach var="entry" items="${structsByCode}">
        <c:choose>
          <c:when test="${fn:endsWith(entry.key, '-3') and not empty entry.value}">
          case "${entry.key}": 
              $(child4).html("<c:forEach var="l4" items="${entry.value}"><c:set var="l4nome" value="${fn:replace(l4.nome, singleQuote, singleQuoteEsc)}" scope="page" /><option value='${l4.extraInfo.codice}'>${l4.prefisso} ${fn:replace(l4nome, doubleQuote, doubleQuoteEsc)}</option></c:forEach><option value=''>-- Nessuna --</option>");
              break;
          </c:when>
        </c:choose>
        </c:forEach>
        }
    });
    <!-- Struttura Capofila 3 -->
    $("#str-liv9").change(function() {
        var parent = $(this).val();
        var child2 = "#str-liv10";
        var child3 = "#str-liv11";
        var child4 = "#str-liv12";
        $(child2).html(blank());
        $(child3).html(blank());
        $(child4).html(blank());
        switch (parent) {
        <c:forEach var="l1" items="${structs}">
        case "${l1.extraInfo.codice}":
            $(child2).html("<c:forEach var="l2" items="${l1.figlie}"><c:set var="l2nome" value="${fn:replace(l2.nome, singleQuote, singleQuoteEsc)}" scope="page" /><option value='${l2.extraInfo.codice}'>${l2.prefisso} ${fn:replace(l2nome, doubleQuote, doubleQuoteEsc)}</option></c:forEach>");
            $(child3).html("<c:forEach var="l2" items="${l1.figlie}" begin="0" end="0"><c:forEach var="l3" items="${l2.figlie}"><c:set var="l3nome" value="${fn:replace(l3.nome, singleQuote, singleQuoteEsc)}" scope="page" /><option value='${l3.extraInfo.codice}'>${l3.prefisso} ${fn:replace(l3nome, doubleQuote, doubleQuoteEsc)}</option></c:forEach></c:forEach><option value=''>-- Nessuna --</option>");
            $(child4).html("<c:forEach var="l2" items="${l1.figlie}" begin="0" end="0"><c:forEach var="l3" items="${l2.figlie}" begin="0" end="0"><c:forEach var="l4" items="${l3.figlie}"><c:set var="l4nome" value="${fn:replace(l4.nome, singleQuote, singleQuoteEsc)}" scope="page" /><option value='${l4.extraInfo.codice}'>${l4.prefisso} ${fn:replace(l4nome, doubleQuote, doubleQuoteEsc)}</option></c:forEach></c:forEach></c:forEach><option value=''>-- Nessuna --</option>");
            break;
        </c:forEach>
        }
    });
    $("#str-liv10").change(function() {
        var parent = $(this).val();
        var child3 = "#str-liv11";
        var child4 = "#str-liv12";
        $(child3).html(blank());
        $(child4).html(blank());
        switch (parent) {
        <c:forEach var="entry" items="${structsByCode}">
        <c:choose>
          <c:when test="${fn:endsWith(entry.key, '-2') and not empty entry.value}">
          case "${entry.key}": 
              $(child3).html("<c:forEach var="str" items="${entry.value}"><c:set var="l3nome" value="${fn:replace(str.nome, singleQuote, singleQuoteEsc)}" scope="page" /><option value='${str.extraInfo.codice}'>${str.prefisso} ${fn:replace(l3nome, doubleQuote, doubleQuoteEsc)}</option></c:forEach><option value=''>-- Nessuna --</option>");
              $(child4).html("<c:forEach var="str" items="${entry.value}" begin="0" end="0"><c:forEach var="l4" items="${str.figlie}"><c:set var="l4nome" value="${fn:replace(l4.nome, singleQuote, singleQuoteEsc)}" scope="page" /><option value='${l4.extraInfo.codice}'>${l4.prefisso} ${fn:replace(l4nome, doubleQuote, doubleQuoteEsc)}</option></c:forEach></c:forEach><option value=''>-- Nessuna --</option>");
              break;
          </c:when>
          <c:when test="${fn:endsWith(entry.key, '-3') and not empty entry.value}">
          case "${entry.key}": 
              $(child4).html("<c:forEach var="l4" items="${entry.value}"><c:set var="l4nome" value="${fn:replace(l4.nome, singleQuote, singleQuoteEsc)}" scope="page" /><option value='${l4.extraInfo.codice}'>${l4.prefisso} ${fn:replace(l4nome, doubleQuote, doubleQuoteEsc)}</option></c:forEach><option value=''>-- Nessuna --</option>");
              break;
          </c:when>
          </c:choose>
        </c:forEach>
        }
    });
    $("#str-liv11").change(function() {
        var parent = $(this).val();
        var child4 = "#str-liv12";
        $(child4).html(blank());
        switch (parent) {
        <c:forEach var="entry" items="${structsByCode}">
        <c:choose>
          <c:when test="${fn:endsWith(entry.key, '-3') and not empty entry.value}">
          case "${entry.key}": 
              $(child4).html("<c:forEach var="l4" items="${entry.value}"><c:set var="l4nome" value="${fn:replace(l4.nome, singleQuote, singleQuoteEsc)}" scope="page" /><option value='${l4.extraInfo.codice}'>${l4.prefisso} ${fn:replace(l4nome, doubleQuote, doubleQuoteEsc)}</option></c:forEach><option value=''>-- Nessuna --</option>");
              break;
          </c:when>
        </c:choose>
        </c:forEach>
        }
    });
    $('#btn-save').click(function (e){
        e.preventDefault;
      });

    $('#inm-form').validate ({
        rules: {
          'ms-name': {
            required: true
          },
          'ms-char': {
            required: true
          },
          'ms-eco': {
            required: true
          },
          "ms-type1": {
            checkAtLeastOneChecked: true
          },
          "sliv1": {
            required: true
          }
        }, 
        messages: {
          'ms-name': "Inserire il nome della misura",
          'ms-char': "Specificare il carattere della misura",
          'ms-eco': "Specificare se la misura comporta spese",
          'sliv1': "Specificare almeno una struttura capofila",
        },
        errorPlacement: function(error, element) {
            if (element.attr("name") == "ms-type1") {
                error.insertAfter("#custom-error-location"); // Place error message after a specific element
            } else if (element.attr("name") == "sliv1") {
                error.insertAfter("#custom-error-location-2"); // Place error message after another specific element
            } else {
                error.insertAfter(element); // Default placement for other elements
            }
        },
        submitHandler: function (form) {
          return true;
        }
      });
    
      $('#ms-desc').keyup(function (e) {
          var chars = $(this).val().length;
          $(this).next('div').text(chars + ' caratteri inseriti');
      });
      
      $.validator.addMethod("checkAtLeastOneChecked", function(value, element) {
        if (
        <c:set var="separator" value="||" scope="page" />
        <c:forEach var="type" items="${types}" varStatus="status">
             $('input[name="ms-type${type.id}"]:checked').length > 0
          <c:if test="${status.count eq types.size()}">
            <c:set var="separator" value=")" scope="page" />
          </c:if>
          <c:out value="${separator}" />
        </c:forEach>
          return true;
        return false;
      }, "Specificare almeno una tipologia");
    
});
</script>
