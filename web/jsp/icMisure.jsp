<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="URL.jspf" %>
<c:set var="structs" value="${requestScope.strutture}" scope="page" />
    <style>
    .module {
      background-image: linear-gradient(to top, #ccff66, white);
      border-style: groove;
      border-radius: 10px 10px 10px 10px;
      padding: 10px 10px 0px 10px;
      margin-bottom: 15px;
    }
    .avvisiTot {
      font-weight: bold; 
      font-style: italic;
      padding: 3px;
      margin-bottom: 10px;
      border-top: 1px dotted #ddd;
      background: #cccc99;
    }
    .table td {
        padding-bottom: 0;
    }
    </style>
  <c:catch var="exception">
    <h3 class="mt-1 m-0 font-weight-bold float-left">Monitoraggio</h3>
    <hr class="riga"/>
    <c:forEach var="d" items="${structs}">
    <div class="module">
      <section id="${d.id}">
        <h6 class="mt-md-1 m-1 font-weight-bold"><c:out value="${d.prefisso} ${d.nome}" /></h6>
        <div class="avvisiTot text-right">
          <c:out value="Tot misure: ${d.misure.size()}" />
        </div>
      </section>
      <table class="table table-hover">
        <thead class="thead-light">
          <tr>
            <th width="40%" scope="col">Misura</th>
            <th width="40%" scope="col">Funzioni</th>
            <th width="10%" scope="col">&nbsp; Ruolo</th>
            <th width="10%" scope="col">Monitorata</th>
          </tr>
        </thead>
        <tbody>
        <c:forEach var="ms" items="${d.misure}" varStatus="loop">
          <c:set var="bgActDigit" value="${fn:substring(ms.ruolo, fn:length(ms.ruolo)-1, fn:length(ms.ruolo))+3}" scope="page" />
          <c:set var="bgAct" value="bgAct${bgActDigit}" scope="page" />
          <c:choose>
          <c:when test="${ms.dettagli}">
            <c:set var="details" value="Presenza" scope="page" />
          </c:when>
          <c:otherwise>
            <c:set var="details" value="Assenza" scope="page" />
          </c:otherwise>
          </c:choose>
          <tr>
            <td scope="row" class="align-middle">
              <img src="${initParam.urlDirectoryImmagini}${ms.dettagli}.png" class="ico-small" alt="icona" title="${details} Dettagli Monitoraggio" /> &nbsp;
              <a href="${initParam.appName}/?q=ic&p=mes&mliv=${ms.codice}&r=${param['r']}" title="${ms.codice}">
                <c:out value="${ms.nome}" escapeXml="false" />
              </a>
            </td>
            <td scope="row">
              <a href="${initParam.appName}/?q=ic&p=ind&mliv=${ms.codice}&r=${param['r']}" class="btn bgAct14 btn-spacer"><i class="fas fa-ruler-combined"></i> Indicatori</a>
              <a href="${initParam.appName}/?q=ic&p=mon&mliv=${ms.codice}&r=${param['r']}" class="btn bgAct11 btn-spacer text-black"><i class="fas fa-bars"></i> Misurazioni</a>              
              <a href="" class="btn bgAct22 btn-spacer"><i class="fas fa-chart-line"></i> Report</a>
            </td>
            <td class="align-middle">
              <span class="align-middle badge-pill ${bgAct} btn-small lightTable"><c:out value="${ms.ruolo}" /></span>
            </td>
            <td class="align-middle">
              
            </td>
          </tr>
        </c:forEach>
        </tbody>
      </table>
    </div>
    </c:forEach>
  </c:catch>
  <c:if test= "${not empty exception}">
    <div class= "alert alert-danger alert-dismissible" role= "alert">
      <button  type="button" class= "close fadeout" data-dismiss ="alert" aria-label="Close" >
        <span aria-hidden="true" >&times;</ span>
      </button>
      <strong> Attenzione</ strong><br />
      <em> <c:out value=" ${exception}" /></em><hr />
    </div >
  </c:if>
