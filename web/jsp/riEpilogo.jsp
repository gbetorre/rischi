<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="URL.jspf" %>
<c:set var="structs" value="${requestScope.strutture}" scope="page" />
<c:set var="macros" value="${requestScope.processi}" scope="page" />
<c:set var="quests" value="${requestScope.elencoRisposte}" scope="page" />
<c:set var="selStr" value="${requestScope.params.get('str')}" scope="page" />
<c:set var="selPro" value="${requestScope.params.get('pro')}" scope="page" />
<c:set var="codLiv1" value="${fn:substring(selStr.get('liv1'), 2, fn:length(selStr.get('liv1')))}" scope="page" />
<c:set var="codLiv2" value="${fn:substring(selStr.get('liv2'), 2, fn:length(selStr.get('liv2')))}" scope="page" />
<c:set var="codLiv3" value="${fn:substring(selStr.get('liv3'), 2, fn:length(selStr.get('liv3')))}" scope="page" />
<c:set var="codLiv4" value="${fn:substring(selStr.get('liv4'), 2, fn:length(selStr.get('liv4')))}" scope="page" />
    <h4 class="btn-lightgray">Riepilogo struttura selezionata</h4>
    <div class="info">
    <c:forEach var="strLiv1" items="${structs}">
      <c:if test="${fn:substring(strLiv1.extraInfo.codice, 2, fn:length(strLiv1.extraInfo.codice)) eq codLiv1}">
        <c:out value="${strLiv1.nome}" />
        <c:forEach var="strLiv2" items="${strLiv1.figlie}">
          <c:if test="${fn:substring(strLiv2.extraInfo.codice, 2, fn:length(strLiv2.extraInfo.codice)) eq codLiv2}"><br />
            <big style="font-size:x-large">˪</big>&nbsp;&nbsp;<c:out value="${strLiv2.prefisso}" /> <c:out value="${strLiv2.nome}" />
            <c:forEach var="strLiv3" items="${strLiv2.figlie}">
              <c:if test="${fn:substring(strLiv3.extraInfo.codice, 2, fn:length(strLiv3.extraInfo.codice)) eq codLiv3}"><br />
                &nbsp;&nbsp;&nbsp;&nbsp;<big style="font-size:x-large">˪</big>&nbsp;&nbsp;<c:out value="${strLiv3.prefisso}" /> <c:out value="${strLiv3.nome}" />
                <c:forEach var="strLiv4" items="${strLiv3.figlie}">
                  <c:if test="${fn:substring(strLiv4.extraInfo.codice, 2, fn:length(strLiv4.extraInfo.codice)) eq codLiv4}"><br />
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
    <c:forEach var="mac" items="${macros}">
      <c:set var="idPro1" value="${mac.id}.${mac.codice}" scope="page" />
      <c:if test="${idPro1 eq selPro.get('liv1')}">
        <c:out value="${mac.nome}" />
        <c:forEach var="proc" items="${mac.processi}">
          <c:set var="idPro2" value="${proc.id}.${proc.codice}" scope="page" />
          <c:if test="${idPro2 eq selPro.get('liv2')}"><br />
            <big style="font-size:x-large">˪</big>&nbsp;&nbsp;<c:out value="${proc.codice}" /> <c:out value="${proc.nome}" />
            <c:forEach var="sub" items="${proc.processi}">
              <c:set var="idPro3" value="${sub.id}.${sub.codice}" scope="page" />
              <c:if test="${idPro3 eq selPro.get('liv3')}"><br />
                &nbsp;&nbsp;&nbsp;&nbsp;<big style="font-size:x-large">˪</big>&nbsp;&nbsp;<c:out value="${sub.codice}" /> <c:out value="${sub.nome}" />
              </c:if>
            </c:forEach>
          </c:if>
        </c:forEach>
      </c:if>
    </c:forEach>
    </div><br />
    <h4 class="btn-lightgray">
      Riepilogo risposte questionario registrato in data
      <fmt:formatDate value="${requestScope.dataRisposte}"  pattern="dd/MM/yyyy" /> alle ore
      <fmt:formatDate value="${requestScope.oraRisposte}"  pattern="HH:mm" />
    </h4>
    <table class="table table-bordered table-hover table-sm">
      <thead class="thead-light">
        <tr>
          <th class="bg-primary text-white" scope="col" width="20%">Ambito</th>
          <th class="bg-primary text-white" scope="col" width="25%">Quesito</th>
          <th class="bg-primary text-white" scope="col" width="5%">Risposta</th>
          <th class="bg-primary text-white text-center" scope="col" width="*">Note</th>
          <th class="bg-primary text-white" width="10%"><div class="text-center">Funzioni</div></th>
        </tr>
      </thead>
      <tbody>
    <c:set var="status" value="" scope="page" />
    <c:forEach var="answer" items="${quests}" varStatus="loop">
      <c:set var="status" value="${loop.index}" scope="page" />
        <tr class="active">
          <td class="bgAct${answer.ambito.id}" width="20%"><c:out value="${answer.ambito.nome}" /></td>
          <td class="text-justify" width="25%"><cite><c:out value="${answer.formulazione}" /></cite></td>
          <td class="text-center" width="5%">
        <c:choose>
          <c:when test="${not empty answer.answer.nome}">
            <strong><c:out value="${answer.answer.nome}" /></strong>
          </c:when>
          <c:otherwise>
            <img src="${initParam.urlDirectoryImmagini}/ico-del.png" class="btn-del" alt="Nessuna Risposta" title="Nessuna Risposta" />
          </c:otherwise>
        </c:choose>
          </td>
          <td class="text-justify" width="*%"><c:out value="${answer.answer.informativa}" /></td>
          <td class="text-center" width="10%">
            <a href="#upd-form" class="btn-del" rel="modal:open" onclick="">
              <img src="${initParam.urlDirectoryImmagini}/ico-save.png" class="btn-del" alt="Modifica Risposta " title="Modifica Risposta" />
            </a>
          </td>
        </tr>
    </c:forEach>
      </tbody>
    </table>
    <script>
      function change(value){
        document.getElementById("q-id").value = value;
        document.getElementById("totalValue").innerHTML= "Total price: $" + 500*value;
      }
    </script>
    <form id="upd-form" method="post" action="file?q=ind&p=mon&id=&idi=" class="modal">
      <input type="text" id="q-id" name="q-id" value="" />
      <input type="hidden" id="mis-id" name="mis-id" value="" />
      <h3 class="heading">Aggiungi un allegato</h3>
      <br />
      <div class="row">
        <div class="col-sm-5">
          <strong>
            Titolo Documento
            <sup>&#10039;</sup>:
          </strong>
        </div>
        <div class="col-sm-5">  
          <input type="text" class="form-control" id="doc-name" name="doc-name" value="" placeholder="Inserisci un titolo documento">
        </div>
      </div>
      <hr class="separatore" />
      <div class="row">
        <div class="col-sm-5">
          <strong>
            Seleziona un file da caricare
            <sup>&#10039;</sup>:
          </strong>
        </div>
        <div class="col-sm-5">  
          <input type="file" name="file" id="file" size="60" placeholder="Inserisci un file da caricare"><br /><br /> 
        </div>
      </div>
      <hr class="separatore" />
      <div class="row">
        <button type="submit" class="btn btn-warning" value="Upload"><i class="fas fa-file-upload"></i> Upload</button>
      </div>
    </form>

