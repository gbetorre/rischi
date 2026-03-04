<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="URL.jspf" %>
<c:set var="structs" value="${requestScope.strutture}" scope="page" />
<c:set var="structsByCode" value="${requestScope.elencoStrutture}" scope="page" />
<c:set var="subjects" value="${requestScope.soggetti}" scope="page" />
<c:set var="dir" value="${structs.get(zero)}" />
<c:set var="cen" value="${structs.get(zero+1)}" />
<c:set var="dip" value="${structs.get(zero+2)}" />
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
      <form accept-charset="ISO-8859-1" id="sub-form" action="" method="post">
        <div class="panel-heading bgAct29" id="details">
          <h5 class="fw-bold text-dark">
            <i class="fa-solid fa-file-circle-plus"></i>
            <c:out value="Aggiunta Soggetto Terzo / Contingente" />
          </h5>
        </div>
        <div class="panel-body">
          <!-- Elenco soggetti -->
          <div class="row"> 
            <div class="accordion content-holder col-sm-10 bg-note" id="accordion1">
              <div class="bgAct26 text-white form-custom" data-toggle="collapse" data-target="#collapse1" aria-expanded="false" aria-controls="collapse1" id="heading1">
                <strong> &nbsp;Elenco soggetti esistenti:</strong>
                <span class="float-right" title="Clicca per espandere/raccogliere"><i class="fa-regular fa-square-caret-down"></i></span>
              </div>
              <div class="collapse" id="collapse1" aria-labelledby="heading1" data-parent="#accordion1">
                <ul>
                <c:forEach var="subject" items="${subjects}">
                  <c:set var="bold" value="" scope="page" />
                  <c:if test="${subject.urlInterno}">
                    <c:set var="bold" value="reportWpRow" scope="page" />
                  </c:if>
                  <li class="${bold}">
                    <c:out value="${subject.nome}" escapeXml="false" />
                    <c:if test="${not empty subject.informativa}"> &ndash; </c:if>
                    <c:out value="${subject.informativa}" escapeXml="false" />
                  </li>
                </c:forEach>
                </ul>
              </div>
            </div>
          </div>
          <br>
          <hr class="separatore" />          
          <div class="content-holder col-sm-10">
            <!-- Estremi nuovo soggetto -->
            <div class="fas-container">
              <div class="reportRow bgAct31">&nbsp;&nbsp;Inserimento nuovo soggetto master</div>
              <hr class="separapoco" />
              <div id="callable-row">
                <div class="row">
                  <div class="col-12 large-4">
                    <input type="text" class="form-control" id="su-nu" name="su-name" placeholder="Inserisci nome soggetto" required>
                  </div>
                  <hr class="separapoco" />
                  <div class="row">
                    <div class="col-12 large-4">
                      <textarea class="form-control" id="su-desc" name="su-desc" placeholder="Inserisci una descrizione"></textarea>
                    </div>
                  </div>
                  <hr class="separatore" />
                </div>
              </div>
            </div>
            <!-- Scelta struttura collegata -->
            <div class="accordion col-sm-12" id="accordion2">
              <div class="bgAct26 text-white form-custom" data-toggle="collapse" data-target="#collapse2" aria-expanded="false" aria-controls="collapse2" id="heading2">
                <strong>Struttura Collegata</strong>&nbsp;
                <span class="float-right" title="Clicca per espandere/raccogliere"><i class="fa-regular fa-square-caret-down"></i></span>
              </div>
              <div class="collapse" id="collapse2" aria-labelledby="heading2" data-parent="#accordion2">
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
            </div>
          </div>
          <hr class="separatore" />
          <div class="d-flex justify-content-center">
            <button type="submit" class="btn btnNav bgAct22" id="btn-save" name="action" value="load">
              <i class="far fa-save"></i> &nbsp; Salva &nbsp;
            </button>
          </div>
          <hr class="separatore" />
        </div>
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
    <script>
    <!-- Struttura Collegata -->
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
    </script>
