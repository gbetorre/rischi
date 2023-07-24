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
<c:set var="codLiv1" value="${fn:substring(selStr.get('liv1'), (fn:indexOf(selStr.get('liv1'), '.')+1), fn:length(selStr.get('liv1')))}" scope="page" />
<c:set var="codLiv2" value="${fn:substring(selStr.get('liv2'), (fn:indexOf(selStr.get('liv2'), '.')+1), fn:length(selStr.get('liv2')))}" scope="page" />
<c:set var="codLiv3" value="${fn:substring(selStr.get('liv3'), (fn:indexOf(selStr.get('liv3'), '.')+1), fn:length(selStr.get('liv3')))}" scope="page" />
<c:set var="codLiv4" value="${fn:substring(selStr.get('liv4'), (fn:indexOf(selStr.get('liv4'), '.')+1), fn:length(selStr.get('liv4')))}" scope="page" />
    <h4 class="btn-lightgray">Riepilogo struttura selezionata</h4>
    <div class="info">
    <c:forEach var="strLiv1" items="${structs}">
      <c:if test="${fn:substring(strLiv1.extraInfo.codice, (fn:indexOf(strLiv1.extraInfo.codice, '.')+1), fn:length(strLiv1.extraInfo.codice)) eq codLiv1}">
        <c:out value="${strLiv1.nome}" />
        <c:forEach var="strLiv2" items="${strLiv1.figlie}">
          <c:if test="${fn:substring(strLiv2.extraInfo.codice, (fn:indexOf(strLiv2.extraInfo.codice, '.')+1), fn:length(strLiv2.extraInfo.codice)) eq codLiv2}"><br />
            <big style="font-size:x-large">&#746;</big>&nbsp;&nbsp;<c:out value="${strLiv2.prefisso}" /> <c:out value="${strLiv2.nome}" />
            <c:forEach var="strLiv3" items="${strLiv2.figlie}">
              <c:if test="${fn:substring(strLiv3.extraInfo.codice, (fn:indexOf(strLiv3.extraInfo.codice, '.')+1), fn:length(strLiv3.extraInfo.codice)) eq codLiv3}"><br />
                &nbsp;&nbsp;&nbsp;&nbsp;<big style="font-size:x-large">&#746;</big>&nbsp;&nbsp;<c:out value="${strLiv3.prefisso}" /> <c:out value="${strLiv3.nome}" />
                <c:forEach var="strLiv4" items="${strLiv3.figlie}">
                  <c:if test="${fn:substring(strLiv4.extraInfo.codice, (fn:indexOf(strLiv4.extraInfo.codice, '.')+1), fn:length(strLiv4.extraInfo.codice)) eq codLiv4}"><br />
                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<big style="font-size:x-large">&#746;</big>&nbsp;&nbsp;<c:out value="${strLiv4.prefisso}" /> <c:out value="${strLiv4.nome}" />
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
            <big style="font-size:x-large">&#746;</big>&nbsp;&nbsp;<c:out value="${proc.codice}" /> <c:out value="${proc.nome}" />
            <c:forEach var="sub" items="${proc.processi}">
              <c:set var="idPro3" value="${sub.id}.${sub.codice}" scope="page" />
              <c:if test="${idPro3 eq selPro.get('liv3')}"><br />
                &nbsp;&nbsp;&nbsp;&nbsp;<big style="font-size:x-large">&#746;</big>&nbsp;&nbsp;<c:out value="${sub.codice}" /> <c:out value="${sub.nome}" />
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
          <th class="bg-primary text-white" scope="col" width="25%">Ambito</th>
          <th class="bg-primary text-white" scope="col" width="30%">Quesito</th>
          <th class="bg-primary text-white" scope="col" width="5%">Risposta</th>
          <th class="bg-primary text-white text-center" scope="col" width="*">Note</th>
          <th class="bg-primary text-white" width="10%"><div class="text-center">Funzioni</div></th>
        </tr>
      </thead>
      <tbody>
    <c:set var="singleQuote" value="'" scope="page" />
    <c:set var="singleQuoteEsc" value="\\'" scope="page" />
    <c:set var="status" value="" scope="page" />
    <c:forEach var="answer" items="${quests}" varStatus="loop">
      <c:set var="status" value="${loop.index}" scope="page" />
        <tr class="active">
          <td class="bgAct${answer.ambito.id}" width="25%"><c:out value="${answer.ambito.nome}" /></td>
          <td class="text-justify" width="30%">
          <c:if test="${not empty answer.parentQuestion}">&nbsp;
            <big style="font-size:x-large">&#8614;</big>&nbsp;
          </c:if>
            <cite><c:out value="${answer.formulazione}" /></cite>
          </td>
          <td class="text-center" width="5%">
        <c:choose>
          <c:when test="${not empty answer.answer.nome}">
            <strong><c:out value="${answer.answer.nome}" /></strong>
          </c:when>
          <c:otherwise>
            <img src="${initParam.urlDirectoryImmagini}ico-del.png" class="ico-home" alt="Nessuna Risposta" title="Nessuna Risposta" />
          </c:otherwise>
        </c:choose>
          </td>
          <td class="text-justify" width="*%"><span id="note${answer.id}"><c:out value="${answer.answer.informativa}" /></span></td>
          <td class="text-center" width="10%">
            <a href="#upd-form" rel="modal:open" onclick="change('${answer.answer.nome}',${answer.id},'${answer.tipo.informativa}','${fn:replace(answer.formulazione, singleQuote, singleQuoteEsc)}');">
              <img src="${initParam.urlDirectoryImmagini}ico-save.png" class="ico-home" alt="Modifica Risposta" title="Modifica" />
            </a>
          </td>
        </tr>
    </c:forEach>
      </tbody>
    </table>
    <script>
      function change(value, ref, type, question){
        document.getElementById("q-id").value = ref;
        document.getElementById("q-risp").value = value;
        document.getElementById("q-note").value = document.getElementById("note" + ref).innerHTML;
        document.getElementById("q-question").innerHTML = "<cite>&quot;" + question + "&quot; <strong class='textcolorred'>(TIPO: " + type + ")</strong></cite>";
      }
    </script>
    <form id="upd-form" method="post" action="" class="modal" style="height:440px;">
      <input type="hidden" class="form-control" id="q-id" name="q-id" value="">
      <h3 class="heading">Edita Risposta</h3>
      <div class="row bgcolor2" id="q-question"></div>
      <hr class="separatore" />
      <div class="row">
        <div class="col-sm-5">Risposta</div>
        <div class="col-sm-5">  
          <input type="text" class="form-control" id="q-risp" name="q-risp" value="">
        </div>
      </div>
      <hr class="separatore" />
      <div class="row">
        <div class="col-sm-5">
          Descrizione
        </div>
        <div class="col-sm-5">  
          <textarea class="form-control" id="q-note" name="q-note" aria-label="With textarea" maxlength="8104" rows="8"></textarea>
        </div>
      </div>
      <hr class="separatore" />
      <div class="row">
        <div class="col-sm-5">
          &nbsp;
        </div>
        <div class="col-sm-5">  
          <button type="submit" class="btn btn-success" value="Salva">
          <i class="far fa-save"></i>
          Salva
        </button>
        </div>
      </div>
    </form>
