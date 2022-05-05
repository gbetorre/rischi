<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="URL.jspf" %>
<c:set var="structs" value="${requestScope.strutture}" scope="page" />
<c:set var="macros" value="${requestScope.processi}" scope="page" />
<c:set var="selStr" value="${requestScope.params.get('str')}" scope="page" />
<c:set var="selPro" value="${requestScope.params.get('pro')}" scope="page" />
<c:set var="codLiv1" value="${selStr.get('liv1')}" scope="page" />
<c:set var="codLiv2" value="${selStr.get('liv2')}" scope="page" />
<c:set var="codLiv3" value="${selStr.get('liv3')}" scope="page" />
<c:set var="codLiv4" value="${selStr.get('liv4')}" scope="page" />
    <h4 class="btn-lightgray">Riepilogo struttura selezionata</h4>
    <div class="info">
    <c:forEach var="strLiv1" items="${structs}">
      <c:if test="${strLiv1.extraInfo.codice eq codLiv1}">
        <c:out value="${strLiv1.nome}" />
        <c:forEach var="strLiv2" items="${strLiv1.figlie}">
          <c:if test="${strLiv2.extraInfo.codice eq codLiv2}"><br />
            <big style="font-size:x-large">˪</big>&nbsp;&nbsp;<c:out value="${strLiv2.prefisso}" /> <c:out value="${strLiv2.nome}" />
            <c:forEach var="strLiv3" items="${strLiv2.figlie}">
              <c:if test="${strLiv3.extraInfo.codice eq codLiv3}"><br />
                &nbsp;&nbsp;&nbsp;&nbsp;<big style="font-size:x-large">˪</big>&nbsp;&nbsp;<c:out value="${strLiv3.prefisso}" /> <c:out value="${strLiv3.nome}" />
                <c:forEach var="strLiv4" items="${strLiv3.figlie}">
                  <c:if test="${strLiv4.extraInfo.codice eq codLiv4}"><br />
                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<big style="font-size:x-large">˪</big>&nbsp;&nbsp;<c:out value="${strLiv4.prefisso}" /> <c:out value="${strLiv4.nome}" />
                  </c:if>
                </c:forEach>
              </c:if>
            </c:forEach>
          </c:if>
        </c:forEach>
      </c:if>
    </c:forEach>
    </div><hr class="separatore" />
    <h4 class="btn-lightgray">Riepilogo processo selezionato</h4>
    <div class="successPwd">
    <c:forEach var="proLiv1" items="${macros}">
      <c:if test="${proLiv1.codice eq selPro.get('liv1')}">
        <c:out value="${proLiv1.nome}" />
        <c:forEach var="proLiv2" items="${proLiv1.processi}">
          <c:if test="${proLiv2.codice eq selPro.get('liv2')}"><br />
            <big style="font-size:x-large">˪</big>&nbsp;&nbsp;<c:out value="${proLiv2.codice}" /> <c:out value="${proLiv2.nome}" />
            <c:forEach var="proLiv3" items="${proLiv2.processi}">
              <c:if test="${proLiv3.codice eq selPro.get('liv3')}"><br />
                &nbsp;&nbsp;&nbsp;&nbsp;<big style="font-size:x-large">˪</big>&nbsp;&nbsp;<c:out value="${proLiv3.codice}" /> <c:out value="${proLiv3.nome}" />
              </c:if>
            </c:forEach>
          </c:if>
        </c:forEach>
      </c:if>
    </c:forEach>
    </div><br />
    <form id="select_ent_form" class="form-horizontal" action="" method="post">
      <input type="hidden" name="str-liv1" value="${codLiv1}" />
      <input type="hidden" name="str-liv2" value="${codLiv2}" />
      <input type="hidden" name="str-liv3" value="${codLiv3}" />
      <input type="hidden" name="str-liv4" value="${codLiv4}" />
      <h4 class="btn-lightgray">Scelta processi</h4>
      <div class="form-custom form-group" id="str_form">
        <div class="panel-body form-group">
          <!--  Macroprocesso -->
          <div class="row alert">
            <div class="col-sm-3  alert-success">
              Scelta macroprocesso&nbsp;
            </div>
            <div class="col-sm-9">
              <select id="pat-liv1" name="pat-liv1">
                <option value="0">-- macroprocesso -- </option>
              <c:forEach var="macro" items="${macros}">
                <option value="${macro.codice}">${macro.nome}</option>
              </c:forEach>
              </select>
            </div>
            &nbsp;
          </div>
          <!--  Processo -->
          <div class="row alert">
            <div class="col-sm-3  alert-success">
              Scelta processo&nbsp;
            </div>
            <div class="col-sm-9">
              <select id="pat-liv2" name="pat-liv2">
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
              <select id="pat-liv3" name="pat-liv3">
                <option value="">-- sottoprocesso -- </option>
              </select>
            </div>
            &nbsp;
          </div>
          <br />
          &nbsp;
          <a id="btnBack" class="btn btnNav" href="${str}"><i class="fa-solid fa-angles-left"></i> Inizio</a>
          <button type="submit" class="btn btn-success" value="Save">
            <i class="far fa-save"></i>
            Invio
          </button>
        </div>
        <br />
      </div>
    </form>
    <script>
    function defaultVal() {
        return "<option value=''>-- scegli un processo --</option>";
    }
    
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

    $("#pat-liv1").change(function() {
        var parent = $(this).val();
        var child2 = "#pat-liv2";
        var child3 = "#pat-liv3";
        $(child2).html(blank());
        $(child3).html(blank());
        switch (parent) {
        <c:forEach var="mp" items="${macros}">
        case "${mp.codice}":
            $(child2).html("<c:forEach var="pp" items="${mp.processi}"><c:set var="ppnome" value="${fn:replace(pp.nome, singleQuote, singleQuoteEsc)}" scope="page" /><option value='${pp.codice}'>${fn:replace(ppnome, doubleQuote, doubleQuoteEsc)}</option></c:forEach>");
            $(child3).html("<c:forEach var="pp" items="${mp.processi}" begin="0" end="0"><c:forEach var="sp" items="${pp.processi}"><c:set var="spnome" value="${fn:replace(sp.nome, singleQuote, singleQuoteEsc)}" scope="page" /><option value='${sp.codice}'>${fn:replace(spnome, doubleQuote, doubleQuoteEsc)}</option></c:forEach></c:forEach>");
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
        case "${pp.codice}":
            $(child3).html("<c:forEach var="pp" items="${mp.processi}" begin="0" end="0"><c:forEach var="sp" items="${pp.processi}"><c:set var="spnome" value="${fn:replace(sp.nome, singleQuote, singleQuoteEsc)}" scope="page" /><option value='${sp.codice}'>${fn:replace(spnome, doubleQuote, doubleQuoteEsc)}</option></c:forEach></c:forEach>");
            break;
          </c:forEach>
        </c:forEach>
        }
    });
    
});
</script>