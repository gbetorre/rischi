<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="URL.jspf" %>
<c:set var="peName" value="" scope="page" />
<c:set var="peSurn" value="" scope="page" />
<c:set var="peFunz" value="" scope="page" />
<c:set var="peGiur" value="" scope="page" />
<c:set var="peResp" value="" scope="page" />
<c:set var="laFunz" value="" scope="page" />
<c:set var="laGiur" value="" scope="page" />
<c:set var="laResp" value="" scope="page" />
<c:set var="resp" value="Resp. Organizzativa,Funz. Specialistica,Tecn. Laboratorio" scope="page" />
<c:if test="${not empty requestScope.tokens}">
  <c:set var="peForm" value="${requestScope.tokens.get('pes')}" scope="page" />
  <c:if test="${peForm.get('pe-name') ne 'true'}"><c:set var="peName" value="${peForm.get('pe-name')}" scope="page" /></c:if>
  <c:if test="${peForm.get('pe-surn') ne 'true'}"><c:set var="peSurn" value="${peForm.get('pe-surn')}" scope="page" /></c:if>
  <c:if test="${peForm.get('pe-funz') ne 'true'}"><c:set var="peFunz" value="${peForm.get('pe-funz')}" scope="page" /></c:if>
  <c:if test="${peForm.get('pe-giur') ne 'true'}"><c:set var="peGiur" value="${peForm.get('pe-giur')}" scope="page" /></c:if>
  <c:if test="${peForm.get('pe-resp') ne 'true'}"><c:set var="peResp" value="${peForm.get('pe-resp')}" scope="page" /></c:if>
  <c:set var="peFormFields" value="NOME,COGNOME,AREA FUNZIONALE,CATEGORIA,RESPONSABILITA" scope="page" />
</c:if>
    <div class="row">
      <h3 class="mt-1 m-0 font-weight-bold">Ricerca nel database</h3>
      <hr class="riga"/>
      <div id="search_div">
        <form id="pe_form" method="post" action="${pes}" class="panel reportWp">
          <input type="hidden" id="act-id" name="act-id" value="${actInstance.id}" />
          <div class="panel-body">
            <hr class="separatore" />
            <div class="form-row">
              <div class="col-sm-5">Quesito:</div>
              <div class="col-sm-3">
                <input type="text" class="form-control" id="pe-name" name="pe-name" value="${peName}" placeholder="test">
              </div>
              <div class="col-sm-3">
                <input type="text" class="form-control" id="pe-surn" name="pe-surn" value="${peSurn}" placeholder="test">
              </div>
            </div>
            <br />
            <hr class="separatore" />
            <div class="row">
              <div class="col-sm-5">Struttura:</div>
              <div class="col-sm-6">
                <select class="custom-select" id="pe-funz" name="pe-funz">
                <option value="">TUTTE</option>
                <c:forEach var="area" items="${requestScope.areeFunz}">
                  <c:set var="selected" value="" scope="page" />
                  <c:if test="${area.codice eq peFunz}">
                    <c:set var="selected" value="selected" scope="page" />
                    <c:set var="laFunz" value="${area.nome}" scope="page" />
                  </c:if>
                  <option value="${area.codice}" ${selected}>
                    <c:out value="${area.nome}" />
                  </option>
                </c:forEach>
                </select>
              </div>
            </div>
            <br />
            <hr class="separatore" />
            <div class="row">
              <div class="col-sm-5">Processo:</div>
              <div class="col-sm-6">
                <select class="custom-select" id="pe-giur" name="pe-giur">
                <option value="">TUTTE</option>
                <c:forEach var="qualifica" items="${requestScope.ruoliGiur}">
                  <c:set var="selected" value="" scope="page" />
                  <c:if test="${qualifica.codice eq peGiur}">
                    <c:set var="selected" value="selected" scope="page" />
                    <c:set var="laGiur" value="${qualifica.nome}" scope="page" />
                  </c:if>
                  <option value="${qualifica.codice}" ${selected}>
                    <c:out value="${qualifica.nome}" />
                  </option>
                </c:forEach>
                </select>
              </div>
            </div>
            <br />
            <hr class="separatore" />
            <div class="row">
              <div class="col-sm-5">Ambito di analisi:</div>
              <div class="col-sm-6">
                <select class="custom-select" id="pe-resp" name="pe-resp">
                <option value="">--</option>
                <c:forEach var="tipoResp" items="${requestScope.tipiResp}" varStatus="status">
                  <c:set var="selected" value="" scope="page" />
                  <c:if test="${tipoResp eq peResp}">
                    <c:set var="selected" value="selected" scope="page" />
                    <c:forTokens var="labelResp" items="${resp}" delims="," begin="${status.index}" end="${status.index}">
                      <c:set var="laResp" value="${labelResp}" scope="page" />
                    </c:forTokens>
                  </c:if>
                  <option value="${tipoResp}" ${selected}>
                    <c:forTokens var="labelResp" items="${resp}" delims="," begin="${status.index}" end="${status.index}">
                      <c:out value="${labelResp}" />
                    </c:forTokens>
                  </option>
                </c:forEach>
                </select>
              </div>
            </div>
            <br />
            <hr class="separatore" />
            <div class="row">
              <div class="col-sm-5">&nbsp;</div>
              <div class="col-sm-7">
                <button type="submit" class="btn btn-success text-white" value="pe_search" title="Effettua ricerca">
                  <i class="fas fa-search"></i> &nbsp;Cerca
                </button>
                <a href="${pe}" class="btn btn-warning" title="Ripulisce la pagina da campi e risultati">
                  <i class="fa fa-times" aria-hidden="true"></i> &nbsp;Ripristina
                </a>
                <a href="${project}" class="btn btn-primary" title="Imposta i valori trovati all'insieme pi&uacute; grande, se esiste">
                  <i class="fas fa-home"></i> &nbsp;Reimposta
                </a>
              </div>
            </div>
          </div>
        </form>
      </div>
      <br />
    </div>
  <c:if test="${not empty peForm}">
    <div>
      <em>Hai cercato persone che si chiamano:</em>
      <code>
      <c:forEach var="kAnd" items="${peForm.keySet()}" begin="0" end="1" varStatus="status">
        <c:set var="token" value="${peForm.get(kAnd)}" scope="page" />
        <c:if test="${peForm.get(kAnd) eq 'true'}">
          <c:set var="token" value="TUTTI" scope="page" />
        </c:if>
        <c:forTokens var="field" items="${peFormFields}" delims="," begin="${status.index}" end="${status.index}">
          <span class="badge badge-pill badge-primary"><c:out value="${field}" />:</span>
        </c:forTokens>
        <c:out value="${token}" />
      </c:forEach>
      </code>
      <br />
      <em>Nei seguenti contesti:</em>
      <code>
      <c:forEach var="kAnd" items="${peForm.keySet()}" begin="2" varStatus="status">
        <c:set var="token" value="${peForm.get(kAnd)}" scope="page" />
        <c:choose>
          <c:when test="${not empty laFunz and status.count eq 1}">
            <c:set var="token" value="${laFunz}" scope="page" />
          </c:when>
          <c:when test="${not empty laGiur and status.count eq 2}">
            <c:set var="token" value="${laGiur}" scope="page" />
          </c:when>
          <c:when test="${not empty peResp and status.count eq 3}">
            <c:set var="token" value="${laResp}" scope="page" />
          </c:when>
          <c:when test="${empty peResp and status.count eq 3}">
            <c:set var="token" value="Responsabili e non responsabili" scope="page" />
          </c:when>
          <c:when test="${peForm.get(kAnd) eq 'true'}">
            <c:set var="token" value="TUTTE" scope="page" />
          </c:when>
        </c:choose>
        <c:forTokens var="field" items="${peFormFields}" delims="," begin="${status.index}" end="${status.index}">
          <span class="badge badge-pill badge-primary"><c:out value="${field}" />:</span>
        </c:forTokens>
        <c:out value="${token}" />
        <c:if test="${status.index lt peForm.size()-1}"><i class="fas fa-plus"></i></c:if>
      </c:forEach>
      </code>
      <br />
    </div>
    <hr class="dot" />
  </c:if>

  <c:if test="${not empty peForm}">
    <!-- Persone trovate -->
    <h3 class="bordo">Persone trovate <button type="button" class="btn btn-success float-right"><span class="badge badge-pill  badge-light">${requestScope.persone.size()}</span></button></h3>
    <table class="table table-striped risultati" id="foundPerson">
      <thead>
        <tr>
          <th class="Content_Medio" width="10%"> </th>
          <th class="Content_Medio" width="60%"> </th>
          <th class="Content_Medio" width="10%"> </th>
          <th class="Content_Medio" width="10%"> </th>
          <th class="Content_Medio" width="10%"> </th>
        </tr>
      </thead>
      <c:forEach var="persona" items="${requestScope.persone}" varStatus="status">
      <tr>
        <td width="10%">
          <c:out value="${status.count}" />
        </td>
        <td width="60%">
          <!-- Crea il link alla persona -->
          <a href="${pes}&idp=${persona.id}">
            <c:out value="${persona.nome}" escapeXml="false" />&nbsp;
            <c:out value="${persona.cognome}" escapeXml="false" />
          </a>
        </td>
        <td width="10%" align="center">
          -
        </td>
        <td width="10%" align="center">
          -
        </td>
        <td width="10%" align="center">
          -
        </td>
      </tr>
      </c:forEach>
    </table>
  </c:if>
  <c:if test="${param.keywords ne null}">
    <div id="results" class="tab-container" style="display: none;">
      <!-- Voci delle Tabs -->
      <ul class="nav nav-tabs nav-big" role="tablist">
        <li role="presentation"><a href="#tab-pubbDip" role="tab" data-toggle="tab">Pubblicazioni trovate <span class="badge">0</span></a></li>
        <li role="presentation"><a href="#tab-prjDip" role="tab" data-toggle="tab">Progetti trovati <span class="badge">0</span></a></li>
        <%--<li role="presentation"><a href="#tab-oi" role="tab" data-toggle="tab">Insegnamenti attinenti <span class="badge">${requestScope.insegnamenti.size()}</span></a></li> --%>
      </ul>
      <!-- Tab panes -->
      <div class="tab-content">
        <!-- Pubblicazioni trovate -->
        <div role="tabpanel" class="tab-pane fade in active" id="tab-pubbDip">&nbsp;</div>
        <!-- Progetti trovati -->
        <div role="tabpanel" class="tab-pane fade" id="tab-prjDip">&nbsp;</div>
        <!-- Occorrenze Insegnamento trovate -->
        <div role="tabpanel" class="tab-pane fade" id="tab-oi">
          <table class="table table-striped risultati" data-count="${requestScope.insegnamenti.size()}" id="foundOIs">
            <thead>
              <tr>
                <th class="Content_Medio" width="5%">N&deg;</th>
                <th class="Content_Medio" width="30%">Corso di studi</th>
                <th class="Content_Medio" width="25%">Nome insegnamento</th>
                <th class="Content_Medio" width="30%">Nome modulo</th>
                <th class="Content_Medio" width="5%">Rilevanza sul corso di studi</th>
                <th class="Content_Medio" width="5%">Rilevanza sull' insegnamento</th>
              </tr>
            </thead>
            <c:forEach var="oi" items="${requestScope.insegnamenti}" varStatus="status">
            <tr>
              <td width="5%">
                <c:out value="${status.count}" />
              </td>
              <td width="30%">
                ${oi.codiceCs}
              </td>
              <td width="30%">
                <a href="<uol:dataurl getEnt="oi" data="id=${oi.id}" />">
                ${oi.nomeInsegnamento}
                </a>
              </td>
              <td width="30%">
                <a href="<uol:dataurl getEnt="oi" data="id=${oi.id}" />">
                ${oi.nomeModulo}
                </a>
              </td>
              <td align="center" width="5%">
                ${oi.codiceEsame}
              </td>
              <td align="center" width="5%">
                ${oi.misura}
              </td>
            </tr>
            </c:forEach>
          </table>
        </div>
      </div>
    </div>
    <div id='loading-indicator' style="position: fixed; left: 50%; top: 50%; display: none;">
               <i class="fa fa-spinner fa-spin fa-3x fa-fw"></i>
          <span class="sr-only">Loading...</span>
    </div>
  </c:if>
