<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="URL.jspf" %>
<c:set var="itCounts" value="${zero}" scope="page" />
<c:set var="structs" value="${requestScope.strutture}" scope="page" />
<c:set var="macros" value="${requestScope.processi}" scope="page" />
<c:set var="quests" value="${requestScope.elencoQuesiti}" scope="page" />
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
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-dash-square-dotted" viewBox="0 0 16 16">
              <path d="M2.5 0c-.166 0-.33.016-.487.048l.194.98A1.51 1.51 0 0 1 2.5 1h.458V0H2.5zm2.292 0h-.917v1h.917V0zm1.833 0h-.917v1h.917V0zm1.833 0h-.916v1h.916V0zm1.834 0h-.917v1h.917V0zm1.833 0h-.917v1h.917V0zM13.5 0h-.458v1h.458c.1 0 .199.01.293.029l.194-.981A2.51 2.51 0 0 0 13.5 0zm2.079 1.11a2.511 2.511 0 0 0-.69-.689l-.556.831c.164.11.305.251.415.415l.83-.556zM1.11.421a2.511 2.511 0 0 0-.689.69l.831.556c.11-.164.251-.305.415-.415L1.11.422zM16 2.5c0-.166-.016-.33-.048-.487l-.98.194c.018.094.028.192.028.293v.458h1V2.5zM.048 2.013A2.51 2.51 0 0 0 0 2.5v.458h1V2.5c0-.1.01-.199.029-.293l-.981-.194zM0 3.875v.917h1v-.917H0zm16 .917v-.917h-1v.917h1zM0 5.708v.917h1v-.917H0zm16 .917v-.917h-1v.917h1zM0 7.542v.916h1v-.916H0zm15 .916h1v-.916h-1v.916zM0 9.375v.917h1v-.917H0zm16 .917v-.917h-1v.917h1zm-16 .916v.917h1v-.917H0zm16 .917v-.917h-1v.917h1zm-16 .917v.458c0 .166.016.33.048.487l.98-.194A1.51 1.51 0 0 1 1 13.5v-.458H0zm16 .458v-.458h-1v.458c0 .1-.01.199-.029.293l.981.194c.032-.158.048-.32.048-.487zM.421 14.89c.183.272.417.506.69.689l.556-.831a1.51 1.51 0 0 1-.415-.415l-.83.556zm14.469.689c.272-.183.506-.417.689-.69l-.831-.556c-.11.164-.251.305-.415.415l.556.83zm-12.877.373c.158.032.32.048.487.048h.458v-1H2.5c-.1 0-.199-.01-.293-.029l-.194.981zM13.5 16c.166 0 .33-.016.487-.048l-.194-.98A1.51 1.51 0 0 1 13.5 15h-.458v1h.458zm-9.625 0h.917v-1h-.917v1zm1.833 0h.917v-1h-.917v1zm1.834 0h.916v-1h-.916v1zm1.833 0h.917v-1h-.917v1zm1.833 0h.917v-1h-.917v1zM4.5 7.5a.5.5 0 0 0 0 1h7a.5.5 0 0 0 0-1h-7z"/>
            </svg>&nbsp;&nbsp;
            <c:out value="${strLiv2.prefisso}" /> <c:out value="${strLiv2.nome}" />
            <c:forEach var="strLiv3" items="${strLiv2.figlie}">
              <c:if test="${strLiv3.extraInfo.codice eq codLiv3}"><br />
                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-dash-square-dotted" viewBox="0 0 16 16">
                  <path d="M2.5 0c-.166 0-.33.016-.487.048l.194.98A1.51 1.51 0 0 1 2.5 1h.458V0H2.5zm2.292 0h-.917v1h.917V0zm1.833 0h-.917v1h.917V0zm1.833 0h-.916v1h.916V0zm1.834 0h-.917v1h.917V0zm1.833 0h-.917v1h.917V0zM13.5 0h-.458v1h.458c.1 0 .199.01.293.029l.194-.981A2.51 2.51 0 0 0 13.5 0zm2.079 1.11a2.511 2.511 0 0 0-.69-.689l-.556.831c.164.11.305.251.415.415l.83-.556zM1.11.421a2.511 2.511 0 0 0-.689.69l.831.556c.11-.164.251-.305.415-.415L1.11.422zM16 2.5c0-.166-.016-.33-.048-.487l-.98.194c.018.094.028.192.028.293v.458h1V2.5zM.048 2.013A2.51 2.51 0 0 0 0 2.5v.458h1V2.5c0-.1.01-.199.029-.293l-.981-.194zM0 3.875v.917h1v-.917H0zm16 .917v-.917h-1v.917h1zM0 5.708v.917h1v-.917H0zm16 .917v-.917h-1v.917h1zM0 7.542v.916h1v-.916H0zm15 .916h1v-.916h-1v.916zM0 9.375v.917h1v-.917H0zm16 .917v-.917h-1v.917h1zm-16 .916v.917h1v-.917H0zm16 .917v-.917h-1v.917h1zm-16 .917v.458c0 .166.016.33.048.487l.98-.194A1.51 1.51 0 0 1 1 13.5v-.458H0zm16 .458v-.458h-1v.458c0 .1-.01.199-.029.293l.981.194c.032-.158.048-.32.048-.487zM.421 14.89c.183.272.417.506.69.689l.556-.831a1.51 1.51 0 0 1-.415-.415l-.83.556zm14.469.689c.272-.183.506-.417.689-.69l-.831-.556c-.11.164-.251.305-.415.415l.556.83zm-12.877.373c.158.032.32.048.487.048h.458v-1H2.5c-.1 0-.199-.01-.293-.029l-.194.981zM13.5 16c.166 0 .33-.016.487-.048l-.194-.98A1.51 1.51 0 0 1 13.5 15h-.458v1h.458zm-9.625 0h.917v-1h-.917v1zm1.833 0h.917v-1h-.917v1zm1.834 0h.916v-1h-.916v1zm1.833 0h.917v-1h-.917v1zm1.833 0h.917v-1h-.917v1zM4.5 7.5a.5.5 0 0 0 0 1h7a.5.5 0 0 0 0-1h-7z"/>
                </svg>&nbsp;&nbsp;                
                <c:out value="${strLiv3.prefisso}" /> <c:out value="${strLiv3.nome}" />
                <c:forEach var="strLiv4" items="${strLiv3.figlie}">
                  <c:if test="${strLiv4.extraInfo.codice eq codLiv4}"><br />
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
    </div><br />
    <form accept-charset="ISO-8859-1" id="select_ent_form" class="form-horizontal" action="" method="post">
      <input type="hidden" name="r" value="${param['r']}" />
      <input type="hidden" name="sliv1" value="${codLiv1}" />
      <input type="hidden" name="sliv2" value="${codLiv2}" />
      <input type="hidden" name="sliv3" value="${codLiv3}" />
      <input type="hidden" name="sliv4" value="${codLiv4}" />
      <input type="hidden" name="pliv1" value="${idPro1}" />
      <input type="hidden" name="pliv2" value="${idPro2}" />
      <input type="hidden" name="pliv3" value="${idPro3}" />
      <h4 class="btn-lightgray">Compilazione quesiti</h4>
      <div class="form-custom form-group" id="str_form">
        <div class="panel-body form-group">      
          <div class="accordion" id="accordionExample">     
    <c:forEach var="entry" items="${quests}" varStatus="status">
            <div class="reportRow">
              <div class="card-header" id="heading${status.count}">
                <h2 class="mb-0">
                  <button class="btn btn-link btn-block text-left text-black" type="button" data-toggle="collapse" data-target="#collapse${status.count}" aria-expanded="false" aria-controls="collapse${status.count}">
                    <c:out value="${status.count}. ${entry.key.nome}" />
                    <span class="badge badge-info float-right"><c:out value="${entry.key.livello}" /></span>
                  </button>
                </h2>
              </div>
              <div id="collapse${status.count}" class="collapse" aria-labelledby="heading${status.count}" data-parent="#accordionExample">
                <div class="card-body">
        <c:forEach var="quesito" items="${quests.get(entry.key)}">
          <c:if test="${empty quesito.parentQuestion}">
                  <div class="panel panel-default bgAct${entry.key.id}">
                    <div class="row panel-heading">
                      <div class="col-sm-10">
                        <cite><c:out value="${quesito.formulazione}" /></cite>
                      </div>
                      <div class="col-sm-2">
                        <input type="hidden" name="Q${itCounts}-id" value="${quesito.id}">
                    <c:choose>
                      <c:when test="${quesito.tipo.nome eq 'On/Off'}">
                        <input type="radio" id="Q${quesito.id}-Y" name="Q${itCounts}" value="SI">
                        <label for="Q${quesito.id}-Y"> SI &nbsp;</label>
                        <input type="radio" id="Q${quesito.id}-N" name="Q${itCounts}" value="NO">
                        <label for="Q${quesito.id}-N"> NO &nbsp;</label>
                      </c:when>
                      <c:when test="${quesito.tipo.nome eq 'Quantitativo'}">
                        <input type="text" class="form-custom" id="Q${quesito.id}-V" name="Q${itCounts}" size="4" placeholder="#">
                      </c:when>
                      <c:when test="${quesito.tipo.nome eq 'Percentuale'}">
                        <input type="text" class="form-custom" id="Q${quesito.id}-P" name="Q${itCounts}" size="4" placeholder="%">
                      </c:when>
                    </c:choose>
                      </div>
                    </div>
                    <div class="panel-body contractedTree">
                      <textarea class="form-control" name="Q${itCounts}-note" aria-label="With textarea" maxlength="8104" placeholder="Inserisci facoltativamente una descrizione"></textarea>  
                    </div>
                    <c:set var="itCounts" value="${itCounts + 1}" scope="page" />
                  <c:forEach var="quesitoFiglio" items="${quesito.childQuestions}">
                    <div class="row panel-heading">
                      <div class="col-sm-10">
                        <cite><c:out value="${quesitoFiglio.formulazione}" /></cite>
                      </div>
                      <div class="col-sm-2" id="T${quesitoFiglio.parentQuestion.id}">
                      <input type="hidden" name="Q${itCounts}-id" value="${quesitoFiglio.id}">
                    <c:choose>
                      <c:when test="${quesitoFiglio.tipo.nome eq 'On/Off'}">
                        <input type="radio" id="Q${quesitoFiglio.id}-Y" name="Q${itCounts}" value="SI">
                        <label for="Q${quesitoFiglio.id}-Y"> SI &nbsp;</label>
                        <input type="radio" id="Q${quesitoFiglio.id}-N" name="Q${itCounts}" value="NO">
                        <label for="Q${quesitoFiglio.id}-N"> NO &nbsp;</label>
                      </c:when>
                      <c:when test="${quesitoFiglio.tipo.nome eq 'Quantitativo'}">
                        <input type="text" class="form-custom" id="Q${quesitoFiglio.id}-V" name="Q${itCounts}" size="4" placeholder="#" disabled>
                      </c:when>
                      <c:when test="${quesitoFiglio.tipo.nome eq 'Percentuale'}">
                        <input type="text" class="form-custom" id="Q${quesitoFiglio.id}-P" name="Q${itCounts}" size="4" placeholder="%" disabled>
                      </c:when>
                    </c:choose>
                      </div>
                    </div>
                    <div class="panel-body contractedTree">
                      <textarea class="form-control" id="A${quesitoFiglio.parentQuestion.id}" name="Q${itCounts}-note" aria-label="With textarea" maxlength="8104" placeholder="Inserisci facoltativamente una descrizione" readonly></textarea>  
                    </div>
                    <c:set var="itCounts" value="${itCounts + 1}" scope="page" />
                  </c:forEach>
                  </div>
          </c:if>
        </c:forEach>
                </div>
              </div>
            </div>
    </c:forEach>      
          </div>     
          <br />
          &nbsp;
          <div class="centerlayout">
            <button type="submit" class="btn btn-success" value="Save">
              <i class="far fa-save"></i> Salva
            </button>
          </div>
        </div>
        <hr class="separatore" />
      </div>
    </form>
    <script>
    $(document).ready(function() {
    <c:forEach var="entry" items="${quests}" varStatus="status">
      <c:forEach var="quesito" items="${quests.get(entry.key)}">
      <c:choose>
      <c:when test="${quesito.tipo.nome eq 'On/Off'}">
        <c:if test="${not empty quesito.childQuestions}"> 
        $("#Q${quesito.id}-Y").click(function() {
          if ($("#Q${quesito.id}-Y").is(':checked')) {
            $("#T${quesito.id} :input[type=text]").prop( "disabled", false );
            $("#T${quesito.id} :input[type=text]").removeClass("form-custom");
            $("#T${quesito.id} :input[type=text]").addClass("btnNav");
            $("#A${quesito.id}").prop("readonly", false);
          }
        });
        </c:if>
      </c:when>
      <c:when test="${quesito.tipo.nome eq 'Quantitativo'}">
        $("#Q${quesito.id}-V").change(function() {
            $("#Q${quesito.id}-V").removeClass("form-custom");
            $("#Q${quesito.id}-V").removeClass("bgcolorred");
            var textValue = this.value; 
            if (isNaN(textValue)) {
                alert("Attenzione: la risposta a questa domanda deve essere un valore numerico! Correggere, prego.");
                $("#Q${quesito.id}-V").addClass("bgcolorred");
            }
            else {
                $("#Q${quesito.id}-V").addClass("bgcolor1");
            }
        });
      </c:when>
      <c:when test="${quesito.tipo.nome eq 'Percentuale'}">
      $("#Q${quesito.id}-P").change(function() {
          $("#Q${quesito.id}-P").removeClass("form-custom");
          $("#Q${quesito.id}-P").removeClass("bgcolorred");
          var textValue = this.value; 
          if (isNaN(textValue)) {
              alert("Attenzione: la risposta a questa domanda deve essere un valore numerico! Correggere, prego.");
              $("#Q${quesito.id}-P").addClass("bgcolorred");
          }
          else {
              $("#Q${quesito.id}-P").addClass("bgcolor1");
          }
      });
      </c:when>
      </c:choose>
      </c:forEach>
    </c:forEach>
    });
    </script>
