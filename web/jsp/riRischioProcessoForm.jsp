<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="URL.jspf" %>
<c:set var="risk" value="${requestScope.rischio}" scope="page" />
<c:set var="macros" value="${requestScope.processi}" scope="page" />
    <h3 class="mt-1 m-0 font-weight-bold">Aggiunta Rischio a Processo</h3>
    <hr class="riga"/>
    <div class="form-custom">
      <div class="panel-heading successPwd">
        <div class="fs-2 mb-3">
            <i class="fas fa-cogs" title="processi"></i>&nbsp; 
            Processo:
          </div>
      </div>
      <hr class="separatore" />
      <div class="p-3 p-md-4 border rounded-3 icon-demo-examples bgAct24">
        <div class="noHeader">
          Rischi già associati al processo:
          <i class="fa-solid fa-triangle-exclamation" title="rischio corruttivo"></i>&nbsp; 
          <c:out value="${risk.nome}" />
        </div>
        <ul class="list-group">
        <c:forEach var="pat" items="${risk.processi}" varStatus="status">
          <c:set var="bgAct" value="bgAct4" scope="page" />
          <c:if test="${status.index mod 2 eq 0}">
            <c:set var="bgAct" value="bgAct20" scope="page" />
          </c:if>
          <li class="list-group-item ${bgAct}">
            <a href="${initParam.appName}/?q=pr&p=pro&pliv=${pat.id}&liv=${pat.livello}&r=${param['r']}">
              <c:out value="${pat.nome}" />
            </a> 
            <span class="float-right">
              (Macroprocesso: <c:out value="${pat.padre.nome}" />)
            </span>
          </li>
        </c:forEach>
        </ul>
      <c:if test="${risk.processi.size() eq zero}">
        <span class="pHeader heading bgAct13 alert-danger" title="Per associare un processo a questo rischio usa la form sottostante">Ancora nessuno</span>
      </c:if>
      </div>
    </div>
    <c:if test="${param['msg'] eq 'dupKey'}">
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
      <button type="button" class="close" data-dismiss="alert" aria-label="Close">
        <span aria-hidden="true">&times;</span>
      </button>
      <strong>ATTENZIONE: </strong>
      il processo scelto &egrave; gi&agrave; presente tra i processi associati al rischio!<br />
      Impossibile associarlo nuovamente.
    </div>
    </c:if>
    <hr class="separatore" />
    <form id="add-pat-form" class="form-horizontal" action="" method="post">
      <input type="hidden" id="rsk-id" name="r-id" value="${risk.id}" />
      <h4 class="btn-lightgray">Rischio corruttivo da collegare al processo (verr&agrave; aggiunto all'elenco dei rischi cui il processo e' esposto)</h4>
      <div class="form-custom form-group" id="adp-form">
        <div class="panel-body form-group">
          <!--  Macroprocesso -->
          <div class="row alert">
            <div class="col-sm-3 mandatory">
              Scelta macroprocesso&nbsp;
            </div>
            <div class="col-sm-9 mandatory">
              <select id="pat-liv1" name="pliv1">
                <option value="0">-- macroprocesso -- </option>
              <c:forEach var="macro" items="${macros}">
                <option value="${macro.id}.${macro.codice}">${macro.nome}</option>
              </c:forEach>
              </select>
            </div>
            &nbsp;
          </div>
          <!--  Processo -->
          <div class="row alert">
            <div class="col-sm-3 mandatory">
              Scelta processo&nbsp;
            </div>
            <div class="col-sm-9 mandatory">
              <select id="pat-liv2" name="pliv2">
                <option value="">-- processo -- </option>
              </select>
            </div>
            &nbsp;
          </div>
          <!--  Sottoprocesso -->
          <div class="row alert">
            <div class="col-sm-3  alert-success">
              Scelta sottoprocesso&nbsp;
            </div>
            <div class="col-sm-9">
              <select id="pat-liv3" name="pliv3">
                <option value="">-- sottoprocesso -- </option>
              </select>
            </div>
            &nbsp;
          </div>
          &nbsp;&nbsp;
          <div class="centerlayout">
            <button type="submit" id="pat-sub" class="btn btn-success" value="Save">
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
    $("#pat-sub").click(function() {
        if ($("#pat-liv1").val() == 0) {
            alert("Occorre selezionare almeno un macroprocesso");
            return false;
        }
        if ($("#pat-liv2").val() == 0) {
            alert("Occorre selezionare almeno un processo");
            return false;
        }
    });
    
    $("#pat-liv1").change(function() {
        var parent = $(this).val();
        var child2 = "#pat-liv2";
        var child3 = "#pat-liv3";
        $(child2).html(blank());
        $(child3).html(blank());
        switch (parent) {
        <c:forEach var="mp" items="${macros}">
        case "${mp.id}.${mp.codice}":
            $(child2).html("<c:forEach var="pp" items="${mp.processi}"><c:set var="ppnome" value="${fn:replace(pp.nome, singleQuote, singleQuoteEsc)}" scope="page" /><option value='${pp.id}.${pp.codice}'>${fn:replace(ppnome, doubleQuote, doubleQuoteEsc)}</option></c:forEach><option value=''>-- Nessuno --</option>");
            $(child3).html("<c:forEach var="pp" items="${mp.processi}" begin="0" end="0"><c:forEach var="sp" items="${pp.processi}"><c:set var="spnome" value="${fn:replace(sp.nome, singleQuote, singleQuoteEsc)}" scope="page" /><option value='${sp.id}.${sp.codice}'>${fn:replace(spnome, doubleQuote, doubleQuoteEsc)}</option></c:forEach></c:forEach><option value=''>-- Nessuno --</option>");
            break;
        </c:forEach>
        }
    });
    
    $("#pat-liv2").change(function() {
        var parent = $(this).val();
        var child3 = "#pat-liv3";
        $(child3).html(blank());
        switch (parent) {
        <c:forEach var="mp" items="${macros}">
          <c:forEach var="pp" items="${mp.processi}" begin="0" end="0">
        case "${pp.id}.${pp.codice}":
            $(child3).html("<c:forEach var="pp" items="${mp.processi}" begin="0" end="0"><c:forEach var="sp" items="${pp.processi}"><c:set var="spnome" value="${fn:replace(sp.nome, singleQuote, singleQuoteEsc)}" scope="page" /><option value='${sp.id}.${sp.codice}'>${fn:replace(spnome, doubleQuote, doubleQuoteEsc)}</option></c:forEach></c:forEach><option value=''>-- Nessuno --</option>");
            break;
          </c:forEach>
        </c:forEach>
        }
    });
    
});
</script>