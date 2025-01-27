<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="URL.jspf" %>
<c:set var="aree" value="${requestScope.aree}" scope="page" />
<c:set var="macros" value="${requestScope.macro}" scope="page" />
<c:set var="liv" value="${param['liv']}" scope="page" />
    <form accept-charset="ISO-8859-1" id="procat_form" class="panel subfields" action="" method="post">
      <div class="panel-heading bgAct3">
        <div class="noHeader">
          <i class="fa-solid fa-square-pen"></i>&nbsp;
          <c:out value="${requestScope.tP}" />
        </div>
      </div>
      <hr class="separatore" />
      <div class="panel-body">
      <c:choose>
        <c:when test="${liv eq 1}">
        <div class="row">
          <div class="col-sm-1">&nbsp;</div>
          <div class="col-sm-4 mandatory-thin bgAct8">
            <label for="ind-tipo"><strong>Area di Rischio</strong></label>&nbsp;&nbsp;
          </div>
          <div class="col-sm-6">
            <select class="form-custom extra-wide" id="mat-area" name="p-area">
            <c:forEach var="area" items="${aree}" varStatus="status">
              <option value="${area.id}.${area.codice}">
                <c:out value="${area.codice}" /> -- <c:out value="${area.nome}" />
              </option>
            </c:forEach>
            </select>
          </div>
        </div>
        <hr class="separatore">
        <div class="row">
          <div class="col-sm-1">&nbsp;</div>
          <div class="col-sm-4 mandatory-thin bgAct8">
            <label for="mat-nome"><strong>Nome Macroprocesso</strong></label>&nbsp;
          </div>
          <div class="col-sm-6">
            <input type="text" class="form-control" id="mat-nome" name="pliv1" placeholder="Inserisci nome del Macroprocesso" required minlength="5">
          </div>
        </div>
        </c:when>
        <c:when test="${liv eq 2}">
        <div class="row">
          <div class="col-sm-1">&nbsp;</div>
          <div class="col-sm-4 mandatory-thin bgAct8">
            <label for="ind-tipo"><strong>Area di Rischio</strong></label>&nbsp;&nbsp;
          </div>
          <div class="col-sm-6">
            <select class="form-custom extra-wide" id="mat-area" name="p-area">
            <c:forEach var="area" items="${aree}" varStatus="status">
              <option value="${area.id}.${area.codice}">
                <c:out value="${area.codice}" /> -- <c:out value="${area.nome}" />
              </option>
            </c:forEach>
            </select>
          </div>
        </div>
        <hr class="separapoco" />
        <div class="row">
          <div class="col-sm-1">&nbsp;</div>
          <div class="col-sm-4 mandatory-thin bgAct8">
            <label for="ind-tipo"><strong>Macroprocesso</strong></label>&nbsp;&nbsp;
          </div>
          <div class="col-sm-6">
            <select id="pat-liv1" name="pliv1">
              <option value="0">-- macroprocesso -- </option>
            <c:forEach var="macro" items="${macros}">
              <option value="${macro.id}.${macro.codice}">${macro.nome}</option>
            </c:forEach>
            </select>
          
            <input type="text" class="form-control" id="mat-nome" name="pliv1" placeholder="Inserisci nome del Macroprocesso" required minlength="5">
          </div>
        </div>
        <hr class="separatore">
        <div class="row">
          <div class="col-sm-1">&nbsp;</div>
          <div class="col-sm-4 mandatory-thin bgAct8">
            <label for="mat-nome"><strong>Nome Processo</strong></label>&nbsp;
          </div>
          <div class="col-sm-6">
            <input type="text" class="form-control" id="pat-nome" name="pliv2" placeholder="Inserisci nome del Processo" required minlength="5">
          </div>
        </div>
        </c:when>
        <c:when test="${liv eq 3}">
        <div class="alert alert-danger">
          <p>
            Per il momento l'inserimento diretto del sottoprocesso non &egrave; ancora gestito.<br />
          </p>
        </div>
        </c:when>
      </c:choose>
        <hr class="separatore" /><br>
        <div class="row">

          <div class="col-sm-12">
          <button type="submit" class="btn btnNav align-left" id="btn-save" name="action" value="save">
            <i class="far fa-save"></i> Salva
          </button>
          <button type="submit" class="btn btnNav bgAct14 float-right" id="btn-cont" name="action" value="cont">
            <i class="far fa-save"></i>  Salva e continua  <i class="fa-solid fa-circle-chevron-right"></i>
          </button>
          </div>
        </div>
      </div>
    </form>
    
    
