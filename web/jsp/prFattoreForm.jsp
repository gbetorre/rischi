<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="URL.jspf" %>
<c:set var="facts" value="${requestScope.fattori}" scope="page" />
<c:set var="risk" value="${requestScope.rischio}" scope="page" />
<c:set var="pat" value="${requestScope.output}" scope="page" />
    <h3 class="mt-1 m-0 font-weight-bold">Aggiunta fattore abilitante </h3>
    <hr class="riga"/>
    <div class="form-custom">
      <div class="panel-heading successPwd">
        <div class="noHeader">
          <i class="fas fa-cogs" title="Processo (contesto)"></i>&nbsp;
          <kbd>Processo</kbd>&nbsp; 
          <c:out value="${pat.nome}" />
        </div>
      </div>
      <hr class="separapoco" />
      <div class="panel-heading bgAct24">
        <div class="noHeader">
          <i class="fa-solid fa-triangle-exclamation" title="Rischio corruttivo"></i>&nbsp; 
          <kbd>Rischio</kbd>&nbsp; 
          <c:out value="${risk.nome}" />
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
              <c:out value="${fat.nome}" />
            </span>
          </li>
        </c:forEach>
        </ul>
      <c:if test="${empty risk.fattori}">
        <span class="pHeader heading btn-warning alert-danger" title="Per associare un fattore a questo rischio usa la form sottostante">Ancora nessuno</span>
      </c:if>
      </div>
    </div>
    <c:if test="${param['msg'] eq 'dupKey'}">
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
      <button type="button" class="close" data-dismiss="alert" aria-label="Close">
        <span aria-hidden="true">&times;</span>
      </button>
      <strong>ATTENZIONE: </strong>
      il fattore scelto &egrave; gi&agrave; presente tra i fattori abilitanti associati al rischio!<br />
      Impossibile associarlo nuovamente nel contesto dello stesso processo.
    </div>
    </c:if>
    <hr class="separatore" />
    <form id="add-fat-form" class="form-horizontal" action="" method="post">
      <input type="hidden" id="rsk-id" name="r-id" value="${risk.id}" />
      <input type="hidden" id="pat-id" name="pliv2" value="${pat.id}" />
      <h4 class="btn-lightgray">Nuovo fattore abilitante da collegare al rischio e al processo</h4>
      <div class="form-custom form-group" id="adp-form">
        <div class="panel-body form-group">
          <!--  Fattori abilitanti -->
          <div class="row alert">
            <div class="col-sm-3 mandatory">
              Scelta fattore abilitante&nbsp;
            </div>
            <div class="col-sm-9 mandatory">
              <select id="fat-liv1" name="fliv1">
                <option value="0">-- fattore abilitante -- </option>
              <c:forEach var="fat" items="${facts}">
                <option value="${fat.id}">${fat.nome}</option>
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
    function blank() {
        return "<option value=''>-- Nessuno --</option>";
    }
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
