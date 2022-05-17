<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="URL.jspf" %>
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
      <h4 class="btn-lightgray">Compilazione quesiti</h4>
      <div class="form-custom form-group" id="str_form">
        <div class="panel-body form-group">
      <c:forEach var="entry" items="${quests}" varStatus="status">
          <!--  Ambito di Analisi -->
          <div class="row substatus">
            <h5 class="heading"><c:out value="${status.count}. ${entry.key.nome}" /></h5>
        <c:forEach var="quesito" items="${quests.get(entry.key)}">
          <c:if test="${empty quesito.parentQuestion}">
            <div class="panel panel-default subfields">
              <div class="row panel-heading">
                <div class="col-sm-10">
                  <cite><c:out value="${quesito.formulazione}" /></cite>
                </div>
                <div class="col-sm-2">
              <c:choose>
                <c:when test="${quesito.tipo.nome eq 'On/Off'}">
                  <input type="radio" id="Q.${quesito.id}-Y" name="Q${quesito.id}" value="SI">
                  <label for="Q.${quesito.id}-Y"> SI &nbsp;</label>
                  <input type="radio" id="Q.${quesito.id}-N" name="Q${quesito.id}" value="NO">
                  <label for="Q.${quesito.id}-N"> NO &nbsp;</label>
                </c:when>
                <c:when test="${quesito.tipo.nome eq 'Quantitativo'}">
                  <input type="text" class="form-custom" id="Q${quesito.id}-V" name="Q${quesito.id}" size="4" placeholder="#">
                </c:when>
              </c:choose>
                </div>
              </div>
              <div class="panel-body contractedTree">
                <textarea class="form-control" name="Q${quesito.id}-note" aria-label="With textarea" maxlength="8104" placeholder="Inserisci facoltativamente una descrizione"></textarea>  
              </div>
            <c:forEach var="quesitoFiglio" items="${quesito.childQuestions}">
              <div class="row panel-heading">
                <div class="col-sm-10">
                  <cite><c:out value="${quesitoFiglio.formulazione}" /></cite>
                </div>
                <div class="col-sm-2">
              <c:choose>
                <c:when test="${quesitoFiglio.tipo.nome eq 'On/Off'}">
                  <input type="radio" id="Q.${quesitoFiglio.id}-Y" name="Q${quesitoFiglio.id}" value="SI" disabled>
                  <label for="Q.${quesitoFiglio.id}-Y"> SI &nbsp;</label>
                  <input type="radio" id="Q.${quesitoFiglio.id}-N" name="Q${quesitoFiglio.id}" value="NO" disabled>
                  <label for="Q.${quesitoFiglio.id}-N"> NO &nbsp;</label>
                </c:when>
                <c:when test="${quesitoFiglio.tipo.nome eq 'Quantitativo'}">
                  <input type="text" class="form-custom" id="Q${quesitoFiglio.id}-V" name="Q${quesitoFiglio.id}" size="4" placeholder="#" disabled>
                </c:when>
              </c:choose>
                </div>
              </div>
              <div class="panel-body contractedTree">
                <textarea class="form-control" name="Q${quesitoFiglio.id}-note" aria-label="With textarea" maxlength="8104" placeholder="Inserisci facoltativamente una descrizione" readonly></textarea>  
              </div>
              
            </c:forEach>


              
            </div>
          </c:if>
        </c:forEach>
          </div>
          <hr class="riga" />
      </c:forEach>
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
        <c:if test="${quesito.tipo.nome eq 'Quantitativo'}">
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
        </c:if>
      </c:forEach>
    </c:forEach>
    });
    </script>
