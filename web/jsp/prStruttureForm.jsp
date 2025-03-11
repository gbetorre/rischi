<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="URL.jspf" %>
<c:set var="structs" value="${requestScope.strutture}" scope="page" />
<c:set var="subjects" value="${requestScope.soggetti}" scope="page" />
<c:set var="dir" value="${structs.get(zero)}" />
<c:set var="cen" value="${structs.get(zero+1)}" />
<c:set var="dip" value="${structs.get(zero+2)}" />
<c:set var="process" value="${requestScope.pat}" scope="page" />
<c:set var="fasi" value="${requestScope.listaFasi}" scope="page" />
    <link rel="stylesheet" href="//code.jquery.com/ui/1.13.2/themes/base/jquery-ui.css">
    <div class="form-custom bg-note">
      <form accept-charset="ISO-8859-1" id="ias-form" action="" method="post">
        <input type="hidden" id="pat-id" name="pliv2" value="${process.id}" />
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
          <hr class="separapoco" />
          <div class="row">
            <div class="content-holder col-sm-10" style="border:none;">
            <nav class="navbar navbar-expand-sm bg-light justify-content-center">
              <ul class="navbar-nav">
            <c:set var="ids" value="" scope="page" />
            <c:forEach var="fase" items="${fasi}" varStatus="status">
              <c:set var="ids" value="${ids}.${fase.id}" scope="page" />
                <li class="nav-item">
                  <a class="nav-link" href="#fase${status.count}">fase ${status.count} &nbsp; | </a>
                </li>
            </c:forEach>
              </ul>
            </nav>
            </div>
          </div>
          <input type="hidden" id="act-ids" name="ids" value="${ids}" />
        <c:forEach var="fase" items="${fasi}" varStatus="status">
          <input type="hidden" id="act-id${fase.id}" name="id-${fase.id}" value="${fase.id}" />
          <hr class="separatore" />
          <div class="content-holder col-sm-10">
            <div class="str-container">
              <div class="reportRow form-control bgAct3" id="fase${status.count}">
                <span class="bgAct">&nbsp;Fase <c:out value="${status.count}" />:</span>
                &nbsp;<c:out value="${fase.nome}" />
              </div>
              <hr class="separapoco" />
              <div class="bgAct26 text-white form-custom">
                <strong> 
                  &nbsp;Strutture
                  <span class="badge badge-pill float-right"><c:out value="${fase.strutture.size()}" /> </span>
                </strong>
              </div>
              <ul class="line">
              <c:forEach var="str" items="${fase.strutture}">
                <li class="reportAct bg-note">
                  <span class="marginLeft">
                    <img src="${initParam.urlDirectoryImmagini}str-l${str.livello}.png" class="ico-small" alt="icona" title="Struttura di livello ${str.livello}" />
                    <c:out value="${str.prefisso}" escapeXml="false" />&nbsp; 
                    <c:out value="${str.nome}" escapeXml="false" />
                  </span>
                </li>
              </c:forEach>
              </ul>
              <div class="content-holder bgAct19">(Inserire solo una struttura, scelta tra i 4 livelli)
              <div id="callable-row">
                <div class="row">
                  <div class="col-3 large-4 ui-widget">
                    <input class="sLiv1" name="liv1-${fase.id}" type="text" placeholder="Struttura I livello">
                  </div>
                  <div class="col-3 large-4 ui-widget">
                    <input class="sLiv2" name="liv2-${fase.id}" type="text" placeholder="Struttura II livello">
                  </div>
                  <div class="col-3 large-4 ui-widget">
                    <input class="sLiv3" name="liv3-${fase.id}" type="text" placeholder="Struttura III livello">
                  </div>
                  <div class="col-3 large-4 ui-widget">
                    <input class="sLiv4" name="liv4-${fase.id}" type="text" placeholder="Struttura IV livello">
                  </div>
                </div>
              </div></div>
              <hr class="separatore" />
            </div>
            <div class="str-container">
              <div class="bgAct26 text-white form-custom">
                <strong> 
                  &nbsp;Soggetti
                  <span class="badge badge-pill float-right"><c:out value="${fase.soggetti.size()}" /> </span>
                </strong>
              </div>
              <ul class="line">
              <c:forEach var="sub" items="${fase.soggetti}">
                <li class="reportAct bg-note" title="${sub.informativa}">
                  <span class="marginLeft">
                    <img src="${initParam.urlDirectoryImmagini}person-fill.png" class="ico-small" alt="icona" title="Soggetto contingente" /> 
                    <c:out value="${sub.nome}" escapeXml="false" />
                  </span>
                </li>
              </c:forEach>
              </ul>
              <div id="callable-row">
                <div class="row">
                  <div class="col-12 large-4 ui-widget">
                    <input class="sCont" name="sc-${fase.id}" type="text" placeholder="Soggetto Contingente">
                  </div>
                </div>
              </div>
            </div>
            <hr class="separapoco" />
          </div>
          <hr class="separatore" />
      </c:forEach>
          <%@ include file="btnSaveCont.jspf"%>
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
    let soggetti = [
        <c:forEach var="sc" items="${soggetti}">
        "${sc.nome} (${sc.id})",
        </c:forEach>
      ];
    $( function() {
      $(".sLiv1").autocomplete({
          source: struttureLiv1,
          minLength: 1
        });
      $(".sLiv2").autocomplete({
          source: struttureLiv2,
          minLength: 1
        });
      $(".sLiv3").autocomplete({
          source: struttureLiv3,
          minLength: 1
        });
      $(".sLiv4").autocomplete({
          source: struttureLiv4,
          minLength: 1
        });
      $(".sCont").autocomplete({
          source: soggetti,
          minLength: 1
        });
    });

    $('#btn-save').click(function (e){
        e.preventDefault;
    });
    
    $('#ias-form').validate ({
        rules: {
        <c:forEach var="fase" items="${fasi}" varStatus="status">
          'liv1-${fase.id}': {
              checkFormat: true
          },
          'liv2-${fase.id}': {
              checkFormat: true
          },
          'liv3-${fase.id}': {
              checkFormat: true
          },
          'liv4-${fase.id}': {
              checkFormat: true
          },
        </c:forEach>
        }, 
        submitHandler: function (form) {
          return true;
        }
    });
    
    $.validator.addMethod("checkFormat", function(value, element) {
        // The string can be void or must have at least one character before ) AND ) at the end of the string
        const regex = /^(?:\s*|.*\))$/;
        // Test the value against the regex
        if (regex.test(value)) {
          return true; // Valid string
        }
      }, "Inserire un valore scelto dall'elenco");
    
});
</script>
