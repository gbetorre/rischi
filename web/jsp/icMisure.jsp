<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="URL.jspf" %>
<c:set var="measures" value="${requestScope.misure}" scope="page" />
<c:choose>
  <c:when test="${not empty measures}">
    <c:catch var="exception">
    <div class="row">
      <div class="col-10">
        <h3>Elenco delle misure monitorate di ${sessionScope.usr.nome} ${sessionScope.usr.cognome}</h3>
      </div>
    </div>
    <c:forEach var="entry" items="${requestScope.progetti}">
      <c:if test="${not empty entry.value}">
      <div class="module">
        <c:set var="key" value="${entry.key}" scope="page" />
        <c:set var="d" value="${requestScope.dipart.get(key)}" />
        <section id="${d.acronimo}">
          <h4>${d.prefisso} ${d.nome}</h4>
          <div class="avvisiTot text-right">
          <c:set var="totObj" value="" scope="page" />
          <c:choose>
            <c:when test="${entry.value.size() eq 1}">
              <c:set var="totObj" value="1 obiettivo" scope="page" />
            </c:when>
            <c:when test="${entry.value.size() gt 1}">
              <c:set var="totObj" value="${entry.value.size()} obiettivi" scope="page" />
            </c:when>
          </c:choose>
            <c:out value="${totObj}" />
          </div>
        </section>
        <table class="table table-hover">
          <thead class="thead-light">
            <tr>
              <th width="50%" scope="col">Titolo</th>
              <!-- <th width="5%" scope="col">Stato</th> -->
              <th width="45%" scope="col">Funzioni</th>
              <th width="5%"scope="col">&nbsp;</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="prj" items="${entry.value}" varStatus="loop">
            <c:set var="btnColor" value="btn-success" scope="page" />
            <c:if test="${prj.descrizioneStatoCorrente eq 5}">
              <c:set var="btnColor" value="btn-dark" scope="page" />
            </c:if>
            <c:set var="wbsURI" value="${urlWbs}${prj.id}" scope="page" />
            <c:if test="${(prj.tipo eq 'P') and (not empty param['y'])}">
              <c:set var="wbsURI" value="${urlWbs}${prj.id}&y=${param['y']}" scope="page" />
            </c:if>
            <c:set var="actURI" value="${act}${prj.id}" scope="page" />
            <c:if test="${(prj.tipo eq 'P') and (not empty param['y'])}">
              <c:set var="actURI" value="${act}${prj.id}&y=${param['y']}" scope="page" />
            </c:if>
            <tr>
              <td scope="row"><a href="<c:out value="${progetto}${prj.id}" />"><c:out value="${prj.titolo}" escapeXml="false" /></a></td>
              <!-- <td scope="row"><c:out value="${prj.statoProgetto.nome}" escapeXml="false" /></td> -->
              <td width="45%" scope="row">
            <c:if test="${prj.tipo ne 'P'}">
                <a href="<c:out value= "${vision}${prj.id}" />" class="btn ${btnColor} btn-spacer"><i class="fas fa-file-invoice"></i> Project Charter</a>
                <a href="<c:out value= "${lastStatus}${prj.id}" />" class="btn ${btnColor} btn-spacer"><i class="far fa-clock"></i> Status</a>
            </c:if>
                <a href="<c:out value= "${wbsURI}" />" class="btn ${btnColor} btn-spacer"><i class="fas fa-sitemap"></i> WBS</a>
                <a href="<c:out value= "${actURI}" />" class="btn ${btnColor} btn-spacer"><i class="fas fa-bars"></i> Attivit&agrave;</a>              
              <c:if test="${prj.tipo eq 'P'}">
                <a href="<c:out value= "${ind}${prj.id}&v=o" />" class="btn ${btnColor} btn-spacer"><i class="fas fa-ruler"></i> Indicatori</a>
                <a href="<c:out value= "${monInd}${prj.id}&v=o" />" class="btn ${btnColor} btn-spacer"><i class="fas fa-weight"></i> Misurazioni</a>&nbsp;
                <c:set var="totPPPrj" value="${totPPPrj + 1}" scope="page" />
              </c:if>
              <a href="<c:out value= "${report}${prj.id}" />" class="btn ${btnColor} btn-spacer"><i class="fas fa-chart-line"></i> Report</a>
              </td>
              <td><cite><c:out value="${prj.tag}" /></cite></td>
            </tr>
            <c:set var="totPrj" value="${totPrj + 1}" scope="page" />
            </c:forEach>
            <c:if test="${(not empty sessionScope.writableDeparments) and (totPrj gt totPPPrj)}">
            <tr>
              <td colspan="3" align="left">
                <a href="${mon}${d.id}"  class="btn btn-success btn-spacer"><i class="fas fa-tv"></i> Monitoraggio MIUR</a>
                &nbsp;
                <a href="${monAte}" class="btn btn-success btn-spacer"><i class="fas fa-desktop"></i> Monitoraggio Ateneo</a>
              </td>
            </tr>
            </c:if>
          </tbody>
        </table>
      </div>
      </c:if>
      <c:set var="totPrj" value="${0}" scope="page" />
      <c:set var="totPPPrj" value="${0}" scope="page" />
    </c:forEach>
    <script>
    function viewPlan() {
      var y = document.getElementById("myPlans");
      //y.value = x.value.toUpperCase();
      window.self.location.href = '${projects}' + y.value;
    }
    </script>
  </c:catch>
    <c:if test= "${not empty exception}">
      <div class= "alert alert-danger alert-dismissible" role= "alert">
        <button  type="button" class= "close fadeout" data-dismiss ="alert" aria-label="Close" >
          <span aria-hidden="true" >&times;</ span>
        </button>
        <strong> <fmt:message key="Attenzione" /></ strong><br />
        <em> <fmt:message key="ErroreDol" /></em><hr />
        Dettagli: <br />
        <c:out value=" ${exception}" />
      </div >
    </c:if>
  </c:when>
  <c:otherwise>
      <div class="alert alert-danger">
        <strong>Spiacente 
          <c:out value="${sessionScope.usr.nome}" />
          <c:out value="${sessionScope.usr.cognome}" />!<br />
        </strong>
        <p>
          Non sono stati trovati progetti a te associati nel triennio ${param['y']}&ndash;${param['y']+2}.<br />
          <a href="${project}"><i class="fas fa-home"></i> Torna alla home</a>
        </p>
      </div>
  </c:otherwise>
</c:choose>