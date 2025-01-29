<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="URL.jspf" %>
<c:set var="aree" value="${requestScope.aree}" scope="page" />
<c:set var="macros" value="${requestScope.listaAree}" scope="page" />
<c:set var="macro" value="${requestScope.mat}" scope="page" />
<c:set var="liv" value="${param['liv']}" scope="page" />
<c:set var="readonly" value="" scope="page" />
<c:if test="${not empty macro}">
  <c:set var="readonly" value="disabled" scope="page" />
</c:if>
    <form accept-charset="ISO-8859-1" id="procat_form" class="panel subfields" action="" method="post">
      <div class="panel-heading bgAct3">
        <div class="noHeader">
          <i class="fa-solid fa-square-pen"></i>&nbsp;
          <c:out value="${requestScope.tP}" />
        </div>
      </div>
      <hr class="separatore" />
      <div class="panel-body">
    <c:choose>
      <%-- MACROPROCESSO --%>
      <c:when test="${liv eq 1}">
        <div class="row">
          <div class="col-sm-1">&nbsp;</div>
          <div class="col-sm-4 mandatory-thin bgAct8">
            <label for="ind-tipo"><strong>Area di Rischio</strong></label>&nbsp;&nbsp;
          </div>
          <div class="col-sm-6">
            <select class="form-custom total-wide" id="mat-area" name="p-area">
            <c:forEach var="area" items="${aree}" varStatus="status">
              <option value="${area.id}.${area.codice}">
                <c:out value="${area.codice}" /> -- <c:out value="${area.nome}" />
              </option>
            </c:forEach>
            </select>
          </div>
        </div>
        <hr class="separatore">
        <div class="row">
          <div class="col-sm-1">&nbsp;</div>
          <div class="col-sm-4 mandatory-thin bgAct8">
            <label for="mat-nome"><strong>Nome Macroprocesso</strong></label>&nbsp;
          </div>
          <div class="col-sm-6">
            <input type="text" class="form-control" id="mat-nome" name="pliv1" placeholder="Inserisci nome del Macroprocesso" required minlength="5">
          </div>
        </div>
      </c:when>
      <%-- PROCESSO --%>
      <c:when test="${liv eq 2}">
        <div class="row">
          <div class="col-sm-1">&nbsp;</div>
          <div class="col-sm-4 mandatory-thin bgAct8">
            <label for="ind-tipo"><strong>Area di Rischio</strong></label>&nbsp;&nbsp;
          </div>
          <div class="col-sm-6">
            <select class="form-custom total-wide" id="sel-area" name="p-area" ${readonly}>
            <c:forEach var="area" items="${aree}" varStatus="status">
            <c:choose>
              <c:when test="${area.id eq fn:substring(macro.areaRischio, zero, fn:indexOf(macro.areaRischio, '.'))}">
                <c:set var="selected" value="selected" scope="page" />
              </c:when>
              <c:otherwise>
                <c:set var="selected" value="" scope="page" />
              </c:otherwise>
            </c:choose>
              <option value="${area.id}.${area.codice}" ${selected}>
                <c:out value="${area.codice}" /> -- <c:out value="${area.nome}" />
              </option>
            </c:forEach>
            </select>
            <input type="text" id="mat-area" name="mat-area" value="${macro.areaRischio}" />
          </div>
        </div>
        <hr class="separapoco" />
        <div class="row">
          <div class="col-sm-1">&nbsp;</div>
          <div class="col-sm-4 mandatory-thin bgAct8">
            <label for="ind-tipo"><strong>Macroprocesso</strong></label>&nbsp;&nbsp;
          </div>
          <div class="col-sm-6">
          <c:choose>
            <c:when test="${not empty macro}">
            <input type="text" class="form-control" id="mat-nome" name="pliv1" value="${macro.nome}" readonly>
            <input type="text" id="mat-id" name="pliv1" value="${macro.id}">
            </c:when>
            <c:otherwise>
            <select class="form-custom total-wide monospace" id="pat-liv1" name="pliv1">
              
            <c:forEach var="m" items="${macros.get(aree.get(zero))}">
              <option value="${m.id}.${m.codice}">
                <c:out value="${m.codice}" /> -- <c:out value="${m.nome}" />
              </option>
            </c:forEach>
            <c:if test="${empty macros.get(aree.get(zero))}">
              <option value="0"> -- AREA SENZA MACROPROCESSI -- </option>
            </c:if>
            </select>
            </c:otherwise>
          </c:choose>

          </div>
        </div>
        <hr class="separatore">
        <div class="row">
          <div class="col-sm-1">&nbsp;</div>
          <div class="col-sm-4 mandatory-thin bgAct8">
            <label for="mat-nome"><strong>Nome Processo</strong></label>&nbsp;
          </div>
          <div class="col-sm-6">
            <input type="text" class="form-control" id="pat-nome" name="pliv2" placeholder="Per inserire un processo &egrave; necessario selezionare un macroprocesso" required minlength="5">
          </div>
        </div>
      </c:when>
      <%-- SOTTOPROCESSO --%>
      <c:when test="${liv eq 3}">
        <div class="alert alert-danger">
          <p>
            Per il momento l'inserimento diretto del sottoprocesso non &egrave; ancora gestito.<br />
          </p>
        </div>
      </c:when>
    </c:choose>
        <hr class="separatore" /><br>
        <div class="row">

          <div class="col-sm-12">
          <button type="submit" class="btn btnNav align-left" id="btn-save" name="action" value="save">
            <i class="far fa-save"></i> Salva
          </button>
          <button type="submit" class="btn btnNav bgAct14 float-right" id="btn-cont" name="action" value="cont">
            <i class="far fa-save"></i>  Salva e continua  <i class="fa-solid fa-circle-chevron-right"></i>
          </button>
          </div>
        </div>
      </div>
    </form>
    <c:set var="singleQuote" value="'" scope="page" />
    <c:set var="singleQuoteEsc" value="''" scope="page" />
    <c:set var="doubleQuote" value='"' scope="page" />
    <c:set var="doubleQuoteEsc" value='\\"' scope="page" />
    <script>
    $(document).ready(function() {
        // Call the function to set up the event listener
        disableTextField();
        
        $("#pat-sub").click(function() {
            if ($("#pat-liv1").val() == 0) {
                alert("Occorre selezionare almeno un macroprocesso");
                return false;
            }
        });
        
        $("#sel-area").change(function() {
            const $textField = $('#pat-nome');
            var parent = $(this).val();
            var child = "#pat-liv1";
            //var child3 = "#pat-liv3";
            //$(child).html(blank());
            //$(child3).html(blank());
            switch (parent) {
            <c:forEach var="area" items="${aree}">
              case "${area.id}.${area.codice}":
                var innerHTML = "";
                <c:choose>
                  <c:when test="${!empty area.processi}">
                    <c:forEach var="mat" items="${area.processi}">
                      <c:set var="p1" value="${fn:replace(mat.nome, singleQuote, singleQuoteEsc)}" scope="page" />
                      innerHTML += "<option value='${mat.id}.${mat.codice}'>${mat.codice} -- ${fn:replace(p1, doubleQuote, doubleQuoteEsc)}</option>"
                    </c:forEach>
                    $textField.prop('disabled', false);
                  </c:when>
                  <c:otherwise>
                      innerHTML = blank();
                      $textField.prop('disabled', true);
                  </c:otherwise>
                </c:choose>
                $(child).html(innerHTML);
              break;
            </c:forEach>
            }
        });
        
    });
    
    function blank() {
        return "<option value='0'>-- AREA SENZA MACROPROCESSI --</option>";
    }
    
    // Disable text field when select is void
    function disableTextField() {
        const selectElement = document.getElementById('pat-liv1');
        const textField = document.getElementById('pat-nome');
        const selectedValue = selectElement.value;
        if (selectedValue === '0') {
            textField.disabled = true;
        } else {
            textField.disabled = false;
        }
    }
    </script>
