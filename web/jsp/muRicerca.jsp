<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="URL.jspf" %>
    <div>
      <h3 class="mt-1 m-0 font-weight-bold">Ricerche</h3>
      <hr class="riga"/>
      <ol class="breadcrumb mb-4">
        <li class="breadcrumb-item active">Ricerche predefinite</li>
      </ol>
      <div class="row">
        <div class="col-xl-3 col-md-6">
          <div class="card bgAct20 mb-4 text-dark">
            <div class="card-body">
              <i class="fa-solid fa-cloud-bolt fa-4x"></i>&nbsp;&nbsp; 
              Fattori abilitanti
            </div>
            <div class="card-footer d-flex align-items-center justify-content-between">
              <a class="small stretched-link" href="${fat}">Vedi elenco</a>
              <div class="small text-white"><i class="fas fa-angle-right"></i></div>
            </div>
          </div>
        </div>
        <div class="col-xl-3 col-md-6">
          <div class="card bgAct11 mb-4 text-dark">
            <div class="card-body">
              <i class="fa-solid fa-right-from-bracket fa-4x"></i>&nbsp;&nbsp; 
              Output dei processi
            </div>
            <div class="card-footer d-flex align-items-center justify-content-between">
              <a class="small stretched-link" href="${out}">Vedi elenco</a>
              <div class="small text-white"><i class="fas fa-angle-right"></i></div>
            </div>
          </div>
        </div>
        <div class="col-xl-3 col-md-6">
          <div class="card bgAct22 mb-4 text-white">
            <div class="card-body">
              <i class="fa-solid fa-desktop fa-4x"></i>&nbsp;&nbsp; 
              <span class="text-white">Misure monitorate</span>
            </div>
            <div class="card-footer d-flex align-items-center justify-content-between">
              <a class="small stretched-link text-white" href="${ic}">Vedi elenco</a>
              <div class="small text-white"><i class="fas fa-angle-right"></i></div>
            </div>
          </div>
        </div>
      </div>
      <br><hr class="separatore" />
      <ol class="breadcrumb mb-4">
        <li class="breadcrumb-item active">Ricerca libera</li>
      </ol>
      <div id="search_div">
        <form id="pe_form" method="post" action="${pes}" class="section reportWp">
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
                <a href="${pe}" class="btn btn-warning text-black" title="Ripulisce la pagina da campi e risultati">
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
