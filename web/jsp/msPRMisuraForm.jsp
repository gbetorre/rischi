<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="URL.jspf" %>
<c:set var="pat" value="${requestScope.processo}" scope="page" />
<c:set var="risk" value="${requestScope.rischio}" scope="page" />
<c:set var="measures" value="${requestScope.misure}" scope="page" />
<c:set var="advMeasures" value="${requestScope.suggerimenti}" scope="page" />
<c:set var="fatMeasures" value="${requestScope.misureDaFattori}" scope="page" />
<c:set var="types" value="${requestScope.misureTipo.keySet()}" scope="page" />
    <h3 class="mt-1 m-0 font-weight-bold">Applicazione di misura a rischio</h3>
    <hr class="riga"/>
    <div class="form-custom">
      <div class="panel-heading successPwd">
        <div class="noHeader">
          <i class="fas fa-cogs" title="Processo (contesto)"></i>&nbsp;
          <kbd>Processo</kbd>&nbsp; 
          <hr class="separapoco" />
          <div id="processName" class="alert-success">
            &nbsp;<c:out value="${pat.nome}" />
          </div>
        </div>
      </div>
      <hr class="separapoco" />
      <div class="panel-heading bgAct24">
        <div class="noHeader">
          <i class="fa-solid fa-triangle-exclamation" title="Rischio corruttivo"></i>&nbsp; 
          <kbd>Rischio</kbd>&nbsp; 
          <hr class="separapoco" />
          <div id="processName" class="alert-danger">
            &nbsp;<c:out value="${risk.nome}" />
          </div>
        </div>
      </div>
      <hr class="separapoco" />
      <div class="p-3 p-md-4 border rounded-3 icon-demo-examples bgAct13">
        <div class="fs-2 mb-3">&nbsp;
          <i class="fa-solid fa-bolt-lightning" title="Fattori abilitanti (gi&agrave; associati al rischio ed al processo)"></i>&nbsp; 
          <kbd>Fattori abilitanti</kbd>
          <span class="float-right" title="I fattori abilitanti mostrati nell'elenco sono quelli gi&agrave; associati al rischio ed al processo">
            <i class="fa-solid fa-circle-question"></i>
          </span> 
        </div>
        <ul class="list-group">
        <c:forEach var="fat" items="${risk.fattori}" varStatus="status">
          <c:set var="bgAct" value="bgAct4" scope="page" />
          <c:if test="${status.index mod 2 eq 0}">
            <c:set var="bgAct" value="bgAct20" scope="page" />
          </c:if>
          <li class="list-group-item ${bgAct}">
            <span class="textcolormaroon">
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-check2-square" viewBox="0 0 16 16">
                <path d="M3 14.5A1.5 1.5 0 0 1 1.5 13V3A1.5 1.5 0 0 1 3 1.5h8a.5.5 0 0 1 0 1H3a.5.5 0 0 0-.5.5v10a.5.5 0 0 0 .5.5h10a.5.5 0 0 0 .5-.5V8a.5.5 0 0 1 1 0v5a1.5 1.5 0 0 1-1.5 1.5H3z"/>
                <path d="m8.354 10.354 7-7a.5.5 0 0 0-.708-.708L8 9.293 5.354 6.646a.5.5 0 1 0-.708.708l3 3a.5.5 0 0 0 .708 0z"/>
              </svg>&nbsp;
              <c:out value="${fat.nome}" />
            </span>
          </li>
        </c:forEach>
        </ul>
      <c:if test="${empty risk.fattori}">
        <span class="pHeader heading btn-warning alert-danger">Ancora nessuno</span>
      </c:if>
      </div>
      <hr class="separapoco" />
      <div class="p-3 p-md-4 border rounded-3 icon-demo-examples bgAct12">
        <div class="fs-2 mb-3">&nbsp;
          <i class="fa-solid fa-umbrella" title="Misure di prevenzione (gi&agrave; associate al rischio ed al processo)"></i>&nbsp; 
          <kbd>Misure di mitigazione</kbd>
          <span class="float-right" title="Le misure mostrate nell'elenco sono quelle gi&agrave; associate al rischio nel contesto del processo">
            <i class="fa-solid fa-circle-question"></i>
          </span> 
        </div>
        <ul class="list-group">
        <c:forEach var="mis" items="${risk.misure}" varStatus="status">
          <c:set var="bgAct" value="bgAct4" scope="page" />
          <c:if test="${status.index mod 2 eq 0}">
            <c:set var="bgAct" value="bgAct20" scope="page" />
          </c:if>
          <li class="list-group-item ${bgAct}">
            <span>
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-bag-plus" viewBox="0 0 16 16">
                <path fill-rule="evenodd" d="M8 7.5a.5.5 0 0 1 .5.5v1.5H10a.5.5 0 0 1 0 1H8.5V12a.5.5 0 0 1-1 0v-1.5H6a.5.5 0 0 1 0-1h1.5V8a.5.5 0 0 1 .5-.5"/>
                <path d="M8 1a2.5 2.5 0 0 1 2.5 2.5V4h-5v-.5A2.5 2.5 0 0 1 8 1m3.5 3v-.5a3.5 3.5 0 1 0-7 0V4H1v10a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V4zM2 5h12v9a1 1 0 0 1-1 1H3a1 1 0 0 1-1-1z"/>
              </svg>
              <c:out value="${mis.nome}" />
            </span>
          </li>
        </c:forEach>
        </ul>
      <c:if test="${empty risk.misure}">
        <div id="noneMessage" class="alert alert-danger alert-dismissible" role="alert" title="Per associare una misura a questo rischio usa la form sottostante">
          Ancora da definire
          <span class="float-right" title="Per associare una misura a questo rischio usa la form sottostante">
            <i class="fa-regular fa-circle-question"></i>
          </span> 
        </div>
      </c:if>
      </div>
    </div>
    <c:if test="${param['msg'] eq 'dupKey'}">
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
      <button type="button" class="close" data-dismiss="alert" aria-label="Close">
        <span aria-hidden="true">&times;</span>
      </button>
      <strong>ATTENZIONE: </strong>
      la misura scelta &egrave; gi&agrave; presente tra le misure associate al rischio!<br />
      Impossibile associarla nuovamente nel contesto dello stesso processo.
    </div>
    </c:if>
    <hr class="separatore" />
    
    <form accept-charset="ISO-8859-1" id="add-mis-form" class=" subfields errorPwd" action="" method="post">
      <input type="hidden" id="rsk-id" name="r-id" value="${risk.id}" />
      <input type="hidden" id="pat-id" name="pliv2" value="${pat.id}" />
      <div class="panel-heading bgAct26">
        <div class="noHeader text-white">
          <i class="fa-solid fa-file-circle-plus"></i>
          <c:out value="Applicazione nuova misura di mitigazione" />
        </div>
      </div><br />
      <div class="content-holder">
        <div class="bgAct26 text-white form-custom">
          <strong> &nbsp;Misure suggerite</strong>
          <span title="Le misure suggerite vengono ricavate tramite l'associazione tra la tipologia della misura e i fattori abilitanti riportati sopra">
            <i class="fa-regular fa-circle-question"></i>
          </span> 
        </div>
        <c:if test="${advMeasures.size() gt 1}">
        <label class="my-auto me-auto custom-label" title="Il numero totale di misure suggerite è ${fatMeasures.size()} di cui risultano ${fatMeasures.size() - advMeasures.size()} misure già applicate" >
          <strong><input type="checkbox" id="select-all"> Seleziona tutte</strong>
          (<c:out value="${advMeasures.size()}" /> &#47; <c:out value="${fatMeasures.size()}" />)
        </label>
        </c:if>
        
        <c:forEach var="type" items="${pageScope.types}">
        <c:if test="${not empty requestScope.misureTipo.get(type)}">
        <hr class="riga" />
        <section class="section">
          <h4><c:out value="${type}" /></h4>
          <p class="text-center">
          <c:forEach var="adv" items="${requestScope.misureTipo.get(type)}" varStatus="status">
            <label class="custom-label">
              <input type="checkbox" id="ms-${adv.codice}" name="ms-adv${adv.codice}" value="${adv.codice}" class="checkbox-item">
              <c:out value="${adv.nome}" />
            </label>
          </c:forEach>
          </p>
        </section>
        
        </c:if>
        </c:forEach>
      </div>
      
      <hr class="separapoco" />
      <div class="form-custom" id="adp-form">
        <div class="verticalCenter">
          <!--  Misure da associare -->
          <div class="row">
            <div class="col-sm-3 bgAct26 text-white"><hr class="separapoco" />
              Scelta misura&nbsp;
            </div>
            <div class="col-sm-9 bgAct4">
              <select id="ms-mrp" name="mrp" class="custom-label extra-wide">
                <option value="">-- misura di mitigazione -- </option>
              <c:forEach var="mis" items="${measures}">
                <option value="${mis.codice}"><c:out value="${mis.nome}" /></option>
              </c:forEach>
              </select>
            </div>
            &nbsp;
          </div>
          &nbsp;&nbsp;
          <div class="centerlayout">
            <button type="submit" id="fat-sub" class="btn btn-success" value="Save">
              <i class="far fa-save"></i> Salva
            </button>
          </div>
        </div>
        <br />
      </div>
    </form>
    <script>
    // JavaScript Function
    document.getElementById('select-all').addEventListener('change', function() {
        var checkboxes = document.querySelectorAll('.checkbox-item');
        checkboxes.forEach(function(checkbox) {
            checkbox.checked = this.checked;
        }, this);
    });
    </script>
<c:set var="singleQuote" value="'" scope="page" />
<c:set var="singleQuoteEsc" value="''" scope="page" />
<c:set var="doubleQuote" value='"' scope="page" />
<c:set var="doubleQuoteEsc" value='\\"' scope="page" />
<script>
$(document).ready(function() {
    $("#fat-sub").click(function() {
        if ($("#fat-liv1").val() == 0) {
            alert("Occorre selezionare un fattore abilitante");
            return false;
        }
    });    
});
</script>
