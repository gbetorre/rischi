<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"  %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="URL.jspf" %>
<c:set var="meas" value="${requestScope.misura}" scope="page" />
<c:set var="misurazioni" value="${requestScope.monitoraggi}" scope="page" />
<c:catch var="exception">
    <h5 class="p-2 bgAct17 rounded popupMenu heading">
      <i class="fa-solid fa-umbrella ico-home" title="misura di prevenzione"></i>&nbsp; 
      <c:out value="${meas.nome}" />
    </h5>
    <hr class="separatore" />
    <ul class="nav nav-tabs responsive" role="tablist" id="tabs-0">
      <li class="nav-item"><a class="nav-link" href="${initParam.appName}/?q=ic&p=ind&mliv=${meas.codice}&r=${param['r']}">Indicatori</a></li>
      <li class="nav-item"><a class="nav-link active tabactive" data-toggle="tab" href="#">Misurazioni</a></li>
      <li class="nav-item"><a class="nav-link" data-toggle="tab" href="">Report</a></li>
    </ul>
    <hr class="separatore" />
  <c:choose>
    <c:when test="${not empty misurazioni}">
    <table class="table table-bordered table-hover" id="listMes">
      <thead class="thead-light">
        <tr>
          <th scope="col" width="15%">Fase</th>
          <th scope="col" width="20%">Indicatore</th>
          <th scope="col" width="5%">Target</th>
          <th scope="col" width="10%">Risultato</th>
          <th scope="col" width="*">Azioni</th>
          <th scope="col" width="15%">Motivazioni</th>
          <th scope="col" width="8%">Data Monitoraggio</th>
          <th scope="col" width="5%">Ultimo Monitoraggio</th>
        </tr>
      </thead>
      <tbody>
      <c:forEach var="mon" items="${misurazioni}">
        <c:set var="ind" value="${mon.indicatore}" scope="page" />
        <c:set var="fase" value="${ind.fase}" scope="page" />
        <tr>
          <td scope="row">
            <c:out value="${fase.nome}" />
          </td>
          <td scope="row" id="nameColumn" class="success bgAct${ind.tipo.id}">
            <a href="${initParam.appName}/?q=ic&p=ind&idI=${ind.id}&idF=${fase.id}&mliv=${meas.codice}&r=${param['r']}" title="Vedi dettagli indicatore">
              <c:out value="${ind.nome}"/>
            </a>
          </td>
          <td scope="row" class="bgAct${ind.tipo.id} bgFade">
            <a href="${initParam.appName}/?q=ic&p=ind&idI=${ind.id}&idF=${fase.id}&mliv=${meas.codice}&r=${param['r']}" title="Vedi dettagli indicatore">
              <c:out value="${ind.target}" />
            </a>
          </td>
          <td scope="row">
            <a href="${initParam.appName}/?q=ic&p=smm&nliv=${mon.id}&idI=${ind.id}&idF=${fase.id}&mliv=${meas.codice}&r=${param['r']}" title="Consulta la misurazione">
              <c:out value="${mon.valore}"/>
            </a>&nbsp;
            <a class="smooth" href="${initParam.appName}/?q=ic&p=smm&nliv=${mon.id}&idI=${ind.id}&idF=${fase.id}&mliv=${meas.codice}&r=${param['r']}">
              <span class="badge badge-primary" id="add-label" title="Clicca per aggiungere un Allegato alla misurazione (attualmente: ${mon.allegati.size()})">
                <i class="fas fa-plus"></i> Allegato
              </span>
            </a>
          </td>
          <td scope="row" class="small">
            <c:out value="${mon.informativa}"/>
          </td>
          <td scope="row" class="small">
            <c:out value="${mon.descrizione}" />
          </td>
          <td scope="row">
            <fmt:formatDate value="${mon.dataUltimaModifica}"/>
          </td>
<c:choose>
          <c:when test="${mon.ultimo}">
            <td scope="row" class="bgAct21">
              <div class="form-check text-center">
                <span class="badge badge-success">
                  SI
                </span>
              </div>
            </td>
          </c:when>
          <c:otherwise>
            <td scope="row" class="bgAct4">
              <div class="form-check text-center">
                <span class="badge badge-light">NO</span>
              </div>
            </td>
          </c:otherwise>
        </c:choose>
        </tr>
      </c:forEach>
      </tbody>
    </table>
    <div class="avvisiTot text-right">Tot monitoraggi: <c:out value="${misurazioni.size()}" /></div>
    </c:when>
    <c:otherwise>
    <div class="alert alert-danger">
      <p>
        Non sono state trovate misurazioni associate ad indicatori <strong><c:out value="${labelResult}" /></strong>.<br />
      </p>
    </div>
    </c:otherwise>
  </c:choose>
  <%--
    <script type="text/javascript">
      $(document).ready(function() {
        $('#listMes').DataTable({
          "columnDefs": [
            { /*"orderable": false, "targets": -1*/ }
          ]
        });
      });
    </script> --%>
</c:catch>
<c:out value="${exception}" />
