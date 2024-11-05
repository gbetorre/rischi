<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"  %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="URL.jspf" %>
<c:set var="openSeason" value="true" scope="page" />
<c:set var="labelResult" value="" scope="page" />
    <h4>
      Indicatori dell'obiettivo strategico <br />
      <strong><c:out value="${p.titolo}" escapeXml="false" /></strong>
    </h4>
    <hr class="separatore" />
    <span class="float-right form-row">
      <c:set var="activeOpe" value="" scope="page" />
      <c:set var="activeClo" value="" scope="page" />
      <c:set var="activeAll" value="" scope="page" />
      <c:choose>
        <c:when test="${param['v'] eq 'o'}">
          <c:set var="activeOpe" value="selected" scope="page" />
          <c:set var="labelResult" value="aperti" scope="page" />
        </c:when>
        <c:when test="${param['v'] eq 'c'}">
          <c:set var="activeClo" value="selected" scope="page" />
          <c:set var="labelResult" value="chiusi" scope="page" />
        </c:when>
        <c:when test="${param['v'] eq 'a'}">
          <c:set var="activeAll" value="selected" scope="page" />
        </c:when>
        <c:otherwise>
          <c:set var="activeAll" value="selected" scope="page" />
        </c:otherwise>
      </c:choose>
      <label for="indicatorSelection" class="col-form-label linkStatus">Seleziona indicatori:</label>
      <select id="indicatorSelection" onchange="viewSome()">
        <option value="o" ${activeOpe}>Aperti</option>
        <option value="c" ${activeClo}>Chiusi</option>
        <option value="a" ${activeAll}>Tutti</option>
      </select>
      &nbsp;
      <a class="btn btnNav" href="${project}">
        <i class="fas fa-home"></i>
        Progetti
      </a>
    </span>
    <ul class="nav nav-tabs responsive" role="tablist" id="tabs-0">
      <li class="nav-item"><a class="nav-link active tabactive" data-toggle="tab" href="#">Indicatori</a></li>
      <li class="nav-item"><a class="nav-link" data-toggle="tab" href="${monInd}${p.id}&v=o">Misurazioni</a></li>
      <li class="nav-item"><a class="nav-link" data-toggle="tab" href="${repInd}${p.id}">Report</a></li>
    </ul>
    <hr class="separatore" />
  <c:choose>
    <c:when test="${not empty requestScope.indicatori}">
    <table class="table table-bordered table-hover" id="listInd">
      <thead class="thead-light">
        <tr>
          <th scope="col" width="20%">Azione (Progetto)</th>
          <th scope="col" width="*">Nome Indicatore</th>
          <th scope="col" width="6%">Baseline</th>
          <th scope="col" width="9%">Data Baseline</th>
          <th scope="col" width="6%">
            Target 
            <c:if test="${openSeason}">
              <span class="bg-warning" title="In questa fase del monitoraggio il target e' modificabile. Clicca sul bottone 'Modifica' in corrispondenza di un Target per andare alla pagina di modifica di quel target.">*</span>
            </c:if>
          </th>
          <th scope="col" width="9%">Data Target</th>
          <th scope="col" width="5%">Tipo Indicatore</th>
          <th scope="col" width="5%"><div class="text-center">Misurato</div></th>
        </tr>
      </thead>
      <tbody>
      <c:set var="status" value="" scope="page" />
      <c:forEach var="in" items="${requestScope.indicatori}" varStatus="loop">
        <c:set var="status" value="${loop.index}" scope="page" />
        <fmt:formatDate var="lastModified" value="${in.dataUltimaModifica}" pattern="dd/MM/yyyy" />
        <input type="hidden" id="ind-id${status}" name="ind-id${status}" value="<c:out value="${in.id}"/>">
        <tr>
          <td scope="row">
        <c:choose>
          <c:when test="${not empty in.wbs}">
            <a href="${modWbs}${p.id}&idw=${in.wbs.id}">
              <c:out value="${in.wbs.nome}"/>
            </a>
          </c:when>
          <c:otherwise>
            Nessuna azione associata
          </c:otherwise>
        </c:choose>
          </td>
          <td scope="row" id="nameColumn" class="success bgAct${in.tipo.id} bgFade">
            <a href="${modInd}${p.id}&idi=${in.id}" title="Modificato:${lastModified} ${fn:substring(in.oraUltimaModifica,0,5)}">
              <c:out value="${in.nome}"/>
            </a>
            <c:if test="${in.idStato eq 4}">
            <span class="badge badge-secondary">Chiuso</span>
            </c:if>
          </td>
          <td scope="row">
            <c:out value="${in.baseline}" />
          </td>
          <td scope="row">
            <fmt:formatDate value="${in.dataBaseline}" pattern="dd/MM/yyyy" />
          </td>
          <td scope="row">
          <c:choose>
            <c:when test="${empty in.targetRivisto}">
              <c:out value="${in.target}" />
            </c:when>
            <c:otherwise>
            <c:set var="abbrevia" value="..." scope="page" />
            <fmt:formatDate var="lastReview" value="${in.dataRevisione}" pattern="dd/MM/yyyy" />
            <span class="badge badge-warning" title="Attenzione: Il target originale ('${in.target}') &egrave; stato modificato in: '${in.targetRivisto}' il ${lastReview} da ${in.autoreUltimaRevisione} con la seguente motivazione: '${in.noteRevisione}'">
            <c:if test="${fn:length(in.targetRivisto) lt 32}">
              <c:set var="abbrevia" value="" scope="page" />
            </c:if>
              <c:out value="${fn:substring(in.targetRivisto, 0, 32)}${abbrevia}" />
            </span>
            </c:otherwise>
          </c:choose>
          <c:if test="${openSeason}"> 
            <a href="<c:out value="${extInd}${p.id}&idi=${in.id}" escapeXml="false" />" id='btn-tar'>
              <button type="button" class="btn btn-sm badge-primary" title="In questa fase del monitoraggio il target di questo indicatore è modificabile. Clicca per andare alla pagina di modifica di questo target.">Modifica</button>
            </a>
          </c:if>
          </td>
          <td scope="row">
            <fmt:formatDate value="${in.dataTarget}" pattern="dd/MM/yyyy" />
          </td>
          <td scope="row">
            <c:out value="${in.tipo.nome}"/>
          </td>
        <c:choose>
          <c:when test="${in.totMisurazioni gt 0}">
            <td scope="row" class="bgcolorgreen">
              <div class="form-check text-center">
                <span>
                  <a href="${monInd}${p.id}&v=a" title="Clicca per visualizzare le misurazioni">SI</a>
                  <span class="badge badge-dark">
                    <c:out value="${in.totMisurazioni}" />
                  </span>
                </span>
              </div>
            </td>
          </c:when>
          <c:otherwise>
            <td scope="row" class="bgcolorred">
              <div class="form-check text-center">
                <span>NO</span>
              </div>
            </td>
          </c:otherwise>
        </c:choose>
        </tr>
      </c:forEach>
      </tbody>
    </table>
    <div class="avvisiTot"><c:out value="${fn:length(requestScope.indicatori)} indicatori " /><c:out value="${labelResult}" /></div>
    </c:when>
    <c:otherwise>
    <div class="alert alert-danger">
      <p>
        Non sono stati trovati indicatori <strong><c:out value="${labelResult}" /></strong> associati all'obiettivo strategico.<br />
        <c:if test="${labelResult ne ''}">
          Forse cercavi <strong><a href="${ind}${p.id}&v=a">tutti gli indicatori</a></strong><br />
        </c:if>
        Se si ritiene che ci&ograve; sia un errore, &egrave; possibile rivolgersi al <a href="mailto:trasparenza@ateneo.univr.it">PMO di Ateneo</a>.
      </p>
    </div>
    </c:otherwise>
  </c:choose>
    <div id="container-fluid">
      <div class="row">
        <div class="col-2">
          <span class="float-left">
            <a class="btn btnNav" href="${project}">
              <i class="fas fa-home"></i>
              Progetti
            </a>
          </span>
        </div>
        <div class="col-8 text-center">

        </div>
      </div>
    </div>

    <script>
    function viewSome() {
      var v = document.getElementById("indicatorSelection");
      //y.value = x.value.toUpperCase();
      window.self.location.href = '${ind}${p.id}&v=' + v.value;
    }
    </script>
    <script type="text/javascript">
      $(document).ready(function() {
        $('#listInd').DataTable({
          "columnDefs": [
            { /*"orderable": false, "targets": -1*/ }
          ]
        });
      });
    </script>
