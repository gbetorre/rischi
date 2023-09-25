<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="URL.jspf" %>
<c:set var="indics" value="${requestScope.indicatori}" scope="page" />
<c:set var="structs" value="${requestScope.strutture}" scope="page" />
<c:set var="macros" value="${requestScope.processi}" scope="page" />
<c:set var="quests" value="${requestScope.elencoRisposte}" scope="page" />
<c:set var="selStr" value="${requestScope.params.get('str')}" scope="page" />
<c:set var="selPro" value="${requestScope.params.get('pro')}" scope="page" />
<c:set var="codLiv1" value="${fn:substring(selStr.get('liv1'), (fn:indexOf(selStr.get('liv1'), '.')+1), fn:length(selStr.get('liv1')))}" scope="page" />
<c:set var="codLiv2" value="${fn:substring(selStr.get('liv2'), (fn:indexOf(selStr.get('liv2'), '.')+1), fn:length(selStr.get('liv2')))}" scope="page" />
<c:set var="codLiv3" value="${fn:substring(selStr.get('liv3'), (fn:indexOf(selStr.get('liv3'), '.')+1), fn:length(selStr.get('liv3')))}" scope="page" />
<c:set var="codLiv4" value="${fn:substring(selStr.get('liv4'), (fn:indexOf(selStr.get('liv4'), '.')+1), fn:length(selStr.get('liv4')))}" scope="page" />
<fmt:formatDate var="selD" value="${requestScope.dataRisposte}" pattern="yyyy-MM-dd" />
<fmt:formatDate var="selH" value="${requestScope.oraRisposte}"  pattern="HH_mm_ss" />
<c:url var="rqsInstance" context="${initParam.appName}" value="/" scope="page">
  <c:param name="q" value="in" />
  <c:param name="p" value="rqs" />
  <c:param name="sliv1" value="${selStr.get('liv1')}" />
  <c:param name="sliv2" value="${selStr.get('liv2')}" />
  <c:param name="sliv3" value="${selStr.get('liv3')}" />
  <c:param name="sliv4" value="${selStr.get('liv4')}" />
  <c:param name="pliv1" value="${param['pliv1']}" />
  <c:param name="pliv2" value="${param['pliv2']}" />
  <c:param name="pliv3" value="${param['pliv3']}" />
  <c:param name="d" value="${selD}" />
  <c:param name="t" value="${selH}" />
  <c:param name="r" value="${ril}" />
</c:url>
<c:set var="checked" value="" />
  <c:catch var="exception">
    <style>
        .loader,
        .loader:after {
            border-radius: 50%;
            width: 10em;
            height: 10em;
        }
        .loader {            
            margin: 60px auto;
            font-size: 10px;
            position: relative;
            text-indent: -9999em;
            border-top: 1.1em solid rgba(255, 255, 255, 0.2);
            border-right: 1.1em solid rgba(255, 255, 255, 0.2);
            border-bottom: 1.1em solid rgba(255, 255, 255, 0.2);
            border-left: 1.1em solid #ffffff;
            -webkit-transform: translateZ(0);
            -ms-transform: translateZ(0);
            transform: translateZ(0);
            -webkit-animation: load8 1.1s infinite linear;
            animation: load8 1.1s infinite linear;
        }
        @-webkit-keyframes load8 {
            0% {
                -webkit-transform: rotate(0deg);
                transform: rotate(0deg);
            }
            100% {
                -webkit-transform: rotate(360deg);
                transform: rotate(360deg);
            }
        }
        @keyframes load8 {
            0% {
                -webkit-transform: rotate(0deg);
                transform: rotate(0deg);
            }
            100% {
                -webkit-transform: rotate(360deg);
                transform: rotate(360deg);
            }
        }
        #loadingDiv {
            position:absolute;;
            top:0;
            left:0;
            width:100%;
            height:150%;
            background-color: rgba(0, 0, 0, .25);
        }
    </style>
    <h4 class="btn-lightgray">Riepilogo struttura selezionata</h4>
    <div class="info">
    <c:forEach var="strLiv1" items="${structs}">
      <c:if test="${fn:substring(strLiv1.extraInfo.codice, (fn:indexOf(strLiv1.extraInfo.codice, '.')+1), fn:length(strLiv1.extraInfo.codice)) eq codLiv1}">
        <c:out value="${strLiv1.nome}" />
        <c:forEach var="strLiv2" items="${strLiv1.figlie}">
          <c:if test="${fn:substring(strLiv2.extraInfo.codice, (fn:indexOf(strLiv2.extraInfo.codice, '.')+1), fn:length(strLiv2.extraInfo.codice)) eq codLiv2}"><br />
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-dash-square-dotted" viewBox="0 0 16 16">
              <path d="M2.5 0c-.166 0-.33.016-.487.048l.194.98A1.51 1.51 0 0 1 2.5 1h.458V0H2.5zm2.292 0h-.917v1h.917V0zm1.833 0h-.917v1h.917V0zm1.833 0h-.916v1h.916V0zm1.834 0h-.917v1h.917V0zm1.833 0h-.917v1h.917V0zM13.5 0h-.458v1h.458c.1 0 .199.01.293.029l.194-.981A2.51 2.51 0 0 0 13.5 0zm2.079 1.11a2.511 2.511 0 0 0-.69-.689l-.556.831c.164.11.305.251.415.415l.83-.556zM1.11.421a2.511 2.511 0 0 0-.689.69l.831.556c.11-.164.251-.305.415-.415L1.11.422zM16 2.5c0-.166-.016-.33-.048-.487l-.98.194c.018.094.028.192.028.293v.458h1V2.5zM.048 2.013A2.51 2.51 0 0 0 0 2.5v.458h1V2.5c0-.1.01-.199.029-.293l-.981-.194zM0 3.875v.917h1v-.917H0zm16 .917v-.917h-1v.917h1zM0 5.708v.917h1v-.917H0zm16 .917v-.917h-1v.917h1zM0 7.542v.916h1v-.916H0zm15 .916h1v-.916h-1v.916zM0 9.375v.917h1v-.917H0zm16 .917v-.917h-1v.917h1zm-16 .916v.917h1v-.917H0zm16 .917v-.917h-1v.917h1zm-16 .917v.458c0 .166.016.33.048.487l.98-.194A1.51 1.51 0 0 1 1 13.5v-.458H0zm16 .458v-.458h-1v.458c0 .1-.01.199-.029.293l.981.194c.032-.158.048-.32.048-.487zM.421 14.89c.183.272.417.506.69.689l.556-.831a1.51 1.51 0 0 1-.415-.415l-.83.556zm14.469.689c.272-.183.506-.417.689-.69l-.831-.556c-.11.164-.251.305-.415.415l.556.83zm-12.877.373c.158.032.32.048.487.048h.458v-1H2.5c-.1 0-.199-.01-.293-.029l-.194.981zM13.5 16c.166 0 .33-.016.487-.048l-.194-.98A1.51 1.51 0 0 1 13.5 15h-.458v1h.458zm-9.625 0h.917v-1h-.917v1zm1.833 0h.917v-1h-.917v1zm1.834 0h.916v-1h-.916v1zm1.833 0h.917v-1h-.917v1zm1.833 0h.917v-1h-.917v1zM4.5 7.5a.5.5 0 0 0 0 1h7a.5.5 0 0 0 0-1h-7z"/>
            </svg>&nbsp;&nbsp;
            <c:out value="${strLiv2.prefisso}" /> <c:out value="${strLiv2.nome}" />
            <c:forEach var="strLiv3" items="${strLiv2.figlie}">
              <c:if test="${fn:substring(strLiv3.extraInfo.codice, (fn:indexOf(strLiv3.extraInfo.codice, '.')+1), fn:length(strLiv3.extraInfo.codice)) eq codLiv3}"><br />
                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-dash-square-dotted" viewBox="0 0 16 16">
                  <path d="M2.5 0c-.166 0-.33.016-.487.048l.194.98A1.51 1.51 0 0 1 2.5 1h.458V0H2.5zm2.292 0h-.917v1h.917V0zm1.833 0h-.917v1h.917V0zm1.833 0h-.916v1h.916V0zm1.834 0h-.917v1h.917V0zm1.833 0h-.917v1h.917V0zM13.5 0h-.458v1h.458c.1 0 .199.01.293.029l.194-.981A2.51 2.51 0 0 0 13.5 0zm2.079 1.11a2.511 2.511 0 0 0-.69-.689l-.556.831c.164.11.305.251.415.415l.83-.556zM1.11.421a2.511 2.511 0 0 0-.689.69l.831.556c.11-.164.251-.305.415-.415L1.11.422zM16 2.5c0-.166-.016-.33-.048-.487l-.98.194c.018.094.028.192.028.293v.458h1V2.5zM.048 2.013A2.51 2.51 0 0 0 0 2.5v.458h1V2.5c0-.1.01-.199.029-.293l-.981-.194zM0 3.875v.917h1v-.917H0zm16 .917v-.917h-1v.917h1zM0 5.708v.917h1v-.917H0zm16 .917v-.917h-1v.917h1zM0 7.542v.916h1v-.916H0zm15 .916h1v-.916h-1v.916zM0 9.375v.917h1v-.917H0zm16 .917v-.917h-1v.917h1zm-16 .916v.917h1v-.917H0zm16 .917v-.917h-1v.917h1zm-16 .917v.458c0 .166.016.33.048.487l.98-.194A1.51 1.51 0 0 1 1 13.5v-.458H0zm16 .458v-.458h-1v.458c0 .1-.01.199-.029.293l.981.194c.032-.158.048-.32.048-.487zM.421 14.89c.183.272.417.506.69.689l.556-.831a1.51 1.51 0 0 1-.415-.415l-.83.556zm14.469.689c.272-.183.506-.417.689-.69l-.831-.556c-.11.164-.251.305-.415.415l.556.83zm-12.877.373c.158.032.32.048.487.048h.458v-1H2.5c-.1 0-.199-.01-.293-.029l-.194.981zM13.5 16c.166 0 .33-.016.487-.048l-.194-.98A1.51 1.51 0 0 1 13.5 15h-.458v1h.458zm-9.625 0h.917v-1h-.917v1zm1.833 0h.917v-1h-.917v1zm1.834 0h.916v-1h-.916v1zm1.833 0h.917v-1h-.917v1zm1.833 0h.917v-1h-.917v1zM4.5 7.5a.5.5 0 0 0 0 1h7a.5.5 0 0 0 0-1h-7z"/>
                </svg>&nbsp;&nbsp;
                <c:out value="${strLiv3.prefisso}" /> <c:out value="${strLiv3.nome}" />
                <c:forEach var="strLiv4" items="${strLiv3.figlie}">
                  <c:if test="${fn:substring(strLiv4.extraInfo.codice, (fn:indexOf(strLiv4.extraInfo.codice, '.')+1), fn:length(strLiv4.extraInfo.codice)) eq codLiv4}"><br />
                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-dash-square-dotted" viewBox="0 0 16 16">
                      <path d="M2.5 0c-.166 0-.33.016-.487.048l.194.98A1.51 1.51 0 0 1 2.5 1h.458V0H2.5zm2.292 0h-.917v1h.917V0zm1.833 0h-.917v1h.917V0zm1.833 0h-.916v1h.916V0zm1.834 0h-.917v1h.917V0zm1.833 0h-.917v1h.917V0zM13.5 0h-.458v1h.458c.1 0 .199.01.293.029l.194-.981A2.51 2.51 0 0 0 13.5 0zm2.079 1.11a2.511 2.511 0 0 0-.69-.689l-.556.831c.164.11.305.251.415.415l.83-.556zM1.11.421a2.511 2.511 0 0 0-.689.69l.831.556c.11-.164.251-.305.415-.415L1.11.422zM16 2.5c0-.166-.016-.33-.048-.487l-.98.194c.018.094.028.192.028.293v.458h1V2.5zM.048 2.013A2.51 2.51 0 0 0 0 2.5v.458h1V2.5c0-.1.01-.199.029-.293l-.981-.194zM0 3.875v.917h1v-.917H0zm16 .917v-.917h-1v.917h1zM0 5.708v.917h1v-.917H0zm16 .917v-.917h-1v.917h1zM0 7.542v.916h1v-.916H0zm15 .916h1v-.916h-1v.916zM0 9.375v.917h1v-.917H0zm16 .917v-.917h-1v.917h1zm-16 .916v.917h1v-.917H0zm16 .917v-.917h-1v.917h1zm-16 .917v.458c0 .166.016.33.048.487l.98-.194A1.51 1.51 0 0 1 1 13.5v-.458H0zm16 .458v-.458h-1v.458c0 .1-.01.199-.029.293l.981.194c.032-.158.048-.32.048-.487zM.421 14.89c.183.272.417.506.69.689l.556-.831a1.51 1.51 0 0 1-.415-.415l-.83.556zm14.469.689c.272-.183.506-.417.689-.69l-.831-.556c-.11.164-.251.305-.415.415l.556.83zm-12.877.373c.158.032.32.048.487.048h.458v-1H2.5c-.1 0-.199-.01-.293-.029l-.194.981zM13.5 16c.166 0 .33-.016.487-.048l-.194-.98A1.51 1.51 0 0 1 13.5 15h-.458v1h.458zm-9.625 0h.917v-1h-.917v1zm1.833 0h.917v-1h-.917v1zm1.834 0h.916v-1h-.916v1zm1.833 0h.917v-1h-.917v1zm1.833 0h.917v-1h-.917v1zM4.5 7.5a.5.5 0 0 0 0 1h7a.5.5 0 0 0 0-1h-7z"/>
                    </svg>&nbsp;&nbsp;
                    <c:out value="${strLiv4.prefisso}" /> <c:out value="${strLiv4.nome}" />
                  </c:if>
                </c:forEach>
              </c:if>
            </c:forEach>
          </c:if>
        </c:forEach>
      </c:if>
    </c:forEach>
    </div>
    <hr class="separatore" />
    <h4 class="btn-lightgray">Riepilogo processo selezionato</h4>
    <div class="successPwd">
    <c:forEach var="mac" items="${macros}">
      <c:set var="idPro1" value="${mac.id}.${mac.codice}" scope="page" />
      <c:if test="${idPro1 eq selPro.get('liv1')}">
        <c:out value="${mac.nome}" />
        <c:forEach var="proc" items="${mac.processi}">
          <c:set var="idPro2" value="${proc.id}.${proc.codice}" scope="page" />
          <c:if test="${idPro2 eq selPro.get('liv2')}"><br />
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-dash-square" viewBox="0 0 16 16">
              <path d="M14 1a1 1 0 0 1 1 1v12a1 1 0 0 1-1 1H2a1 1 0 0 1-1-1V2a1 1 0 0 1 1-1h12zM2 0a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V2a2 2 0 0 0-2-2H2z"/>
              <path d="M4 8a.5.5 0 0 1 .5-.5h7a.5.5 0 0 1 0 1h-7A.5.5 0 0 1 4 8z"/>
            </svg>&nbsp;&nbsp;
            <c:out value="${proc.codice}" /> <c:out value="${proc.nome}" />
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
    </div>
    <br>
    <div id="rischi">
      <h4 class="btn-lightgray">Indicatori di rischio</h4>
      <table class="table table-bordered table-hover">
        <thead class="thead-light">
          <tr>
            <th>Indicatore</th>
            <th>Quesiti connessi</th>
            <th>Tipologia</th>
            <th>Livello</th>
          </tr>
        </thead>
        <tbody>
    <c:set var="keys" value="${indics.keySet()}" />
      <c:forEach var="key" items="${keys}">
        <c:set var="ind" value="${indics.get(key)}" />
          <tr>
            <td><c:out value="${ind.nome}" />: <c:out value="${ind.descrizione}" /></td>
            <td>
          <c:set var="comma" value="," scope="page" />
          <c:forEach var="quesito" items="${ind.risposte}" varStatus="status">
            <c:if test="${status.count eq ind.risposte.size()}">
              <c:set var="comma" value="" scope="page" />
            </c:if>
              <a href="#${quesito.id}" title="ID${quesito.id} = ${quesito.formulazione} (RISPOSTA: &quot;${quesito.answer.nome}&quot;)"><c:out value="${quesito.codice}" /></a><c:out value="${comma}" />
          </c:forEach>
            </td>
            <td><c:out value="${ind.processo.tipo}" /></td>
            <c:set var="classSuffix" value="${fn:toLowerCase(ind.informativa)}" scope="page" />
            <c:if test="${fn:indexOf(classSuffix, ' ') gt -1}">
              <c:set var="classSuffix" value="${fn:substring(classSuffix, zero, fn:indexOf(classSuffix, ' '))}" scope="page" />
            </c:if>
            <td class="text-center bgcolor-${classSuffix}" title="${ind.processo.descrizioneStatoCorrente}">
              <c:out value="${ind.informativa}" />
            </td>
          </tr>
      </c:forEach>
        </tbody>
      </table>
    </div>
    <hr class="separatore" />
    <h4 class="btn-lightgray float-left">
      Riepilogo risposte questionario registrato in data
      <fmt:formatDate value="${requestScope.dataRisposte}" pattern="dd/MM/yyyy" /> alle ore
      <fmt:formatDate value="${requestScope.oraRisposte}"  pattern="HH:mm" />
    </h4>
    <div class="actstate">
      <label for="all" class="float-right lightTable bgAct13" title="Seleziona per mostrare anche le domande senza risposta">
      <c:if test="${param['msg'] eq 'getAll'}">
        <c:set var="checked" value="checked" />
      </c:if>
        <input class="checkbox single" type="checkbox" value="all" name="all" id="all" ${checked} onclick="showToggle();">
        &nbsp;Mostra tutte le domande&nbsp;
      </label>
    </div>
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
          <td class="bgAct${answer.ambito.id}" width="25%">
            <c:out value="${answer.ambito.nome}" />
          </td>
          <td class="text-justify" width="30%">
          <c:if test="${not empty answer.parentQuestion}">&nbsp;
            <big style="font-size:x-large">&#8614;</big>&nbsp;
          </c:if>
            <cite id="${answer.id}"><c:out value="${answer.formulazione}" /></cite>
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
      function showToggle(){
          showLoader();
      <c:choose>
        <c:when test="${param['msg'] eq 'getAll'}">
          window.self.location = '${rqsInstance}';
        </c:when>
        <c:otherwise>
          window.self.location = '${rqsInstance}&msg=getAll';
        </c:otherwise>
      </c:choose>
      }
      
      function showLoader(){
          $('body').append('<div style="" id="loadingDiv"><div class="loader">Loading...</div></div>');
      }
      
      function change(value, ref, type, question){
        document.getElementById("q-id").value = ref;
        document.getElementById("q-risp").value = value;
        document.getElementById("q-note").value = document.getElementById("note" + ref).innerHTML;
        document.getElementById("q-question").innerHTML = "<cite>&quot;" + question + "&quot; <strong class='textcolorred'>(TIPO: " + type + ")</strong></cite>";
      }
      
      $(window).on('load', function(){
          setTimeout(removeLoader, 200); //wait for page load PLUS two seconds.
      });

      function removeLoader(){
          $( "#loadingDiv" ).fadeOut(500, function() {
            // fadeOut complete. Remove the loading div
            $( "#loadingDiv" ).remove(); //makes page more lightweight 
          });  
      }
    </script>
    <form accept-charset="ISO-8859-1" id="upd-form" method="post" action="" class="modal" style="height:440px;">
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
  </c:catch>
  <c:out value="${exception}" />
