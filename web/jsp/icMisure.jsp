<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="URL.jspf" %>
<c:set var="structs" value="${requestScope.strutture}" scope="page" />
<c:set var="selectedYear" value="${param['y']}" />
<c:if test="${empty selectedYear}">
  <c:set var="selectedYear" value="${requestScope.theCurrentYear}" />
</c:if>
<c:choose>
  <c:when test="${selectedYear eq '2026'}">
    <c:set var="sel2026" value="selected" scope="page" />
  </c:when>
  <c:when test="${selectedYear eq '2025'}">
    <c:set var="sel2025" value="selected" scope="page" />
  </c:when>
  <c:when test="${selectedYear eq '2024'}">
    <c:set var="sel2024" value="selected" scope="page" />
  </c:when>
  <c:when test="${selectedYear eq '2023'}">
    <c:set var="sel2023" value="selected" scope="page" />
  </c:when>
  <c:when test="${selectedYear eq '2022'}">
    <c:set var="sel2022" value="selected" scope="page" />
  </c:when>
</c:choose>
    <style>
    /* Overwrite bootstrap 4 behaviour */
    .card:hover {
      box-shadow: none !important;
      transform: none !important;
      background-color: inherit !important;
    }
    /* Loader */
    .loader,
    .loader:after {
        border-radius: 50%;
        width: 10em;
        height: 10em;
    }
    .loader {            
        margin: 60px auto;
        font-size: 10px;
        position: relative;
        text-indent: -9999em;
        border-top: 1.1em solid rgba(255, 255, 255, 0.2);
        border-right: 1.1em solid rgba(255, 255, 255, 0.2);
        border-bottom: 1.1em solid rgba(255, 255, 255, 0.2);
        border-left: 1.1em solid #ffffff;
        -webkit-transform: translateZ(0);
        -ms-transform: translateZ(0);
        transform: translateZ(0);
        -webkit-animation: load8 1.1s infinite linear;
        animation: load8 1.1s infinite linear;
    }
    @-webkit-keyframes load8 {
        0% {
            -webkit-transform: rotate(0deg);
            transform: rotate(0deg);
        }
        100% {
            -webkit-transform: rotate(360deg);
            transform: rotate(360deg);
        }
    }
    @keyframes load8 {
        0% {
            -webkit-transform: rotate(0deg);
            transform: rotate(0deg);
        }
        100% {
            -webkit-transform: rotate(360deg);
            transform: rotate(360deg);
        }
    }
    #loadingDiv {
        position:absolute;;
        top:0;
        left:0;
        width:100%;
        height:150%;
        background-color: rgba(0, 0, 0, .25);
    }
    </style>
  <c:catch var="exception">
    <h3 class="mt-1 m-0 font-weight-bold float-left">
      Monitoraggio <c:out value="${selectedYear}" />
    </h3>
    <!-- TODO: make dynamic -->
    <span class="form-custom float-right">
      <select id="myPlans" class="wide" onchange="viewPlan()">
        <option value="2026" ${sel2026}>2026</option>
        <option value="2025" ${sel2025}>2025</option>
        <option value="2024" ${sel2024}>2024</option>
        <option value="2023" ${sel2023}>2023</option>
        <option value="2022" ${sel2022}>2022</option>
      </select>
    </span>
    <hr class="riga"/>
    <!-- Accordions -->
    <div id="structsAccordion">
    <c:set var="flag" value="false" scope="page" />
    <c:forEach var="d" items="${structs}" varStatus="status">
      <c:if test="${d.misure.size() gt zero}">
      <c:set var="flag" value="true" scope="page" />
      <c:set var="label" value="${d.prefisso} ${d.nome}" scope="page" />
      <c:choose>
        <c:when test="${not empty d.padre.padre}">
          <c:set var="label" value="${d.padre.padre.prefisso} ${d.padre.padre.nome} - ${d.padre.prefisso} ${d.padre.nome} &ndash; ${d.prefisso} ${d.nome}" scope="page" />
        </c:when>     
        <c:when test="${not empty d.padre}">
          <c:set var="label" value="${d.padre.prefisso} ${d.padre.nome} &ndash; ${d.prefisso} ${d.nome}" scope="page" />
        </c:when>
      </c:choose>
      <!-- Card -->
      <div class="card module">
        <!-- OuterCard -->
        <div class="card-header p-0" id="heading-${d.id}">
          <h6 class="mb-0">
            <a class="d-block text-decoration-none text-dark p-2" data-toggle="collapse" href="#collapse-${d.id}" role="button" aria-expanded="false" aria-controls="collapse-${d.id}">
              <span class="mt-md-1 m-1 font-weight-bold">
                <c:out value="${label}" escapeXml="false" />
              </span>
              <span class="float-right avvisiTot text-right">
                <c:out value="Tot misure: ${d.misure.size()}" />&nbsp;
              </span>
            </a>
          </h6>
        </div>
        <!-- InnerCard -->
        <div id="collapse-${d.id}" class="collapse" aria-labelledby="heading-${d.id}" data-parent="#structsAccordion">
          <div class="card-body p-0">
            <table class="table table-hover mb-0">
              <thead class="thead-light">
                <tr>
                  <th width="40%" scope="col">Misura</th>
                  <th width="40%" scope="col">Funzioni</th>
                  <th width="10%" scope="col">&nbsp; Ruolo</th>
                  <th width="10%" scope="col" class="text-center" title="La completezza del monitoraggio &egrave; piena se tutti gli indicatori definiti per la misura hanno ottenuto almeno una misurazione (indipendentemente dal fatto che siano indicatore master o meno) mentre &egrave; parziale negli altri casi.">
                    Completezza
                  </th>
                </tr>
              </thead>
              <tbody>
              <c:forEach var="ms" items="${d.misure}" varStatus="loop">
                <c:set var="bgActDigit" value="${fn:substring(ms.ruolo, fn:length(ms.ruolo)-1, fn:length(ms.ruolo))+3}" scope="page" />
                <c:set var="bgAct" value="bgAct${bgActDigit}" scope="page" />
                <!-- Conditional formatting -->
                <c:choose>
                  <c:when test="${ms.totIndicatori gt zero}">
                    <c:set var="badgeStyle" value="border-basso bgAct8" scope="page" />
                  </c:when>
                  <c:otherwise>
                    <c:set var="badgeStyle" value="border-alto bg-warning" scope="page" />
                    <c:set var="completezza" value="La misura ha 0 Indicatori e quindi non puo' avere misurazioni" scope="page" />
                  </c:otherwise>
                </c:choose>
                <!-- Conditional labels -->
                <c:choose>
                  <c:when test="${ms.monitorata}">
                    <c:set var="completezza" value="La misura ha ${ms.totIndicatori} indicatori e ciascuno ha ricevuto almeno una misurazione" scope="page" />
                  </c:when>
                  <c:when test="${not ms.monitorata and ms.totIndicatori gt zero}">
                    <c:set var="completezza" value="La misura ha ${ms.totIndicatori} indicatori ma almeno uno di essi non e' stato misurato" scope="page" />
                  </c:when>
                </c:choose>
                <!-- Card row -->
                <tr>
                  <td scope="row" class="align-middle" id="${ms.codice}">
                    <a href="${initParam.appName}/?q=ic&p=mes&mliv=${ms.codice}&r=${param['r']}" title="${ms.codice}">
                      <c:out value="${ms.nome}" escapeXml="false" />
                    </a>
                  </td>
                  <td scope="row">
                    <a href="${initParam.appName}/?q=ic&p=ind&mliv=${ms.codice}&r=${param['r']}&y=${selectedYear}" class="btn bgAct14 btn-spacer">
                      <i class="fas fa-ruler-combined"></i> Indicatori &nbsp;
                      <span class="badge badge-pill badge-light ${badgeStyle}" title="${ms.totIndicatori} Indicatori su ${ms.fasi.size()} Fasi">
                        <c:out value="${ms.totIndicatori}" />
                        &#47;
                        <c:out value="${ms.fasi.size()}" />
                      </span>
                    </a>
                    <a href="${initParam.appName}/?q=ic&p=mon&mliv=${ms.codice}&r=${param['r']}" class="btn bgAct11 btn-spacer text-black">
                      <i class="fas fa-bars"></i> Misurazioni &nbsp;
                      <span class="badge badge-pill badge-light" title="Ci sono ${ms.totMisurazioni} misurazioni totali">
                        <c:out value="${ms.totMisurazioni}" />
                      </span>
                    </a>
                  </td>
                  <td class="align-middle">
                    <span class="align-middle badge-pill ${bgAct} btn-small lightTable" title="La struttura &quot;${d.nome}&quot; &egrave; ${fn:toUpperCase(ms.ruolo)} della misura &quot;${ms.nome}&quot;">
                      <c:out value="${ms.ruolo}" />
                    </span>
                  </td>
                  <td class="align-middle text-center">
                    <img src="${initParam.urlDirectoryImmagini}${ms.monitorata}.png" class="ico-small" alt="icona" title="${completezza}" /> &nbsp;
                  </td>
                </tr>
              </c:forEach>
              </tbody>
            </table>
          </div>
        </div>
      </div>
      </c:if>
    </c:forEach>
    </div>
    <c:if test="${not flag}">
    <div class="alert alert-danger">
      <h4>Attenzione</h4>
      <p>
        Non sono stati trovate misure monitorate per l'anno:
        <strong><c:out value="${selectedYear}" /></strong>
      </p>
      <br>
    </div>
    </c:if>
    <script>
    function viewPlan() {
      var y = document.getElementById("myPlans");
      showLoader();
      window.self.location.href = '${initParam.appName}/?q=ic&p=mes&r=${param['r']}&y=' + y.value;
    }
      
    function showLoader(){
        $('body').append('<div style="" id="loadingDiv"><div class="loader">Loading...</div></div>');
    }

    $(window).on('load', function(){
        setTimeout(removeLoader, 200); //wait for page load PLUS two seconds.
    });

    function removeLoader(){
        $( "#loadingDiv" ).fadeOut(500, function() {
          // fadeOut complete. Remove the loading div
          $( "#loadingDiv" ).remove(); //makes page more lightweight 
        });  
      }
    </script>
  </c:catch>
  <c:if test= "${not empty exception}">
    <div class= "alert alert-danger alert-dismissible" role= "alert">
      <button  type="button" class= "close fadeout" data-dismiss ="alert" aria-label="Close" >
        <span aria-hidden="true" >&times;</span>
      </button>
      <strong> Attenzione</strong><br />
      <em> <c:out value=" ${exception}" /></em><hr/>
    </div >
  </c:if>
