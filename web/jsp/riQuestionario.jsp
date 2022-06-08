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
    <form id="select_ent_form" class="form-horizontal" action="" method="post">
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
            <div class="card">
              <div class="card-header" id="heading${status.count}">
                <h2 class="mb-0">
                  <button class="btn btn-link btn-block text-left" type="button" data-toggle="collapse" data-target="#collapse${status.count}" aria-expanded="false" aria-controls="collapse${status.count}">
                    <c:out value="${status.count}. ${entry.key.nome}" />
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
                        <input type="radio" id="Q${quesitoFiglio.id}-Y" name="Q${itCounts}" value="SI" disabled>
                        <label for="Q${quesitoFiglio.id}-Y"> SI &nbsp;</label>
                        <input type="radio" id="Q${quesitoFiglio.id}-N" name="Q${itCounts}" value="NO" disabled>
                        <label for="Q${quesitoFiglio.id}-N"> NO &nbsp;</label>
                      </c:when>
                      <c:when test="${quesitoFiglio.tipo.nome eq 'Quantitativo'}">
                        <input type="text" class="form-custom" id="Q${quesitoFiglio.id}-V" name="Q${itCounts}" size="4" placeholder="#" disabled>
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
      
      <%--
          <div class="accordion" id="accordionExample">
      <c:forEach var="entry" items="${quests}" varStatus="status">
      <c:if test="${status.count gt 1}"><c:set var="show" value="" scope="page" /></c:if>
          <!--  Ambito di Analisi -->
            <div class="card">
              <div class="card-header" id="heading${status.count}">
                <h2 class="mb-0">
                  <button class="btn btn-link btn-block text-left" type="button" data-bs-toggle="collapse" data-bs-target="#collapse${status.count}" aria-expanded="true" aria-controls="collapse${status.count}">
                    <c:out value="${status.count}. ${entry.key.nome}" />
                  </button>
                </h2>
              </div>
              <div id="collapse${status.count}" class="collapse" aria-labelledby="heading${status.count}" data-bs-parent="#accordionExample">
                <div class="card-body">
        <c:forEach var="quesito" items="${quests.get(entry.key)}">
          <c:if test="${empty quesito.parentQuestion}">
            <div class="panel panel-default subfields">
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
                  <input type="radio" id="Q${quesitoFiglio.id}-Y" name="Q${itCounts}" value="SI" disabled>
                  <label for="Q${quesitoFiglio.id}-Y"> SI &nbsp;</label>
                  <input type="radio" id="Q${quesitoFiglio.id}-N" name="Q${itCounts}" value="NO" disabled>
                  <label for="Q${quesitoFiglio.id}-N"> NO &nbsp;</label>
                </c:when>
                <c:when test="${quesitoFiglio.tipo.nome eq 'Quantitativo'}">
                  <input type="text" class="form-custom" id="Q${quesitoFiglio.id}-V" name="Q${itCounts}" size="4" placeholder="#" disabled>
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
        <hr class="riga" />
      </c:forEach>
        </div>--%>
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
      </c:choose>
      </c:forEach>
    </c:forEach>
    });
    </script>
