<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="URL.jspf" %>
<c:set var="measures" value="${requestScope.misure}" scope="page" />
    <style>
        table {
            border-collapse: collapse;
            width: 100%;
        }
        th, td {
            border: 1px solid black;
            padding: 8px;
            text-align: left;
        }
        th {
            background-color: #330099;
            color: white;
        }
        tr:nth-child(odd) {
            background-color: #f2f2f2;
        }
        tr:nth-child(even) {
            background-color: #e1e1ff;
        }
        tr:hover {
            background-color: #ffff33;
        }
        ul {
            list-style-type: none;
            padding: 0;
        }
        ul li {
            padding: 4px 0;
            border-bottom: 1px solid #e9ecef; /* Add a bottom border between list items */
        }
        ul li:last-child {
            border-bottom: none; /* Remove bottom border from the last list item */
        }
    </style>
    <h3 class="mt-1 m-0 font-weight-bold float-left"><c:out value="${requestScope.tP}" /></h3>
    <hr class="riga"/>
    <c:if test="${param['msg'] eq 'newMes'}">
    <script>
      $(function () { 
          var duration = 4000; // 4 seconds
          setTimeout(function () { $('#mainAlertMessage').hide(); }, duration);
      });
    </script>
    <div id="mainAlertMessage" class="alert alert-success alert-dismissible" role="alert">
      Nuova misura creata con successo
    </div>
    </c:if>
    <div class="p-3 p-md-4 border rounded-3 icon-demo-examples bgAct26">
      <div class="fs-2 mb-3 text-white">
        Misure &nbsp;
        <a href="${mnm}" class="btn btn-success btn-lg" title="Aggiungi una nuova misura di contenimento o prevenzione al registro delle misure">
          <i class="fa-solid fa-file-circle-plus"></i> &nbsp;Aggiungi Misura
        </a>
      </div>
      <table class="" id="regMis">
        <thead>
          <tr>
            <th width="20%">Misura</th>
            <th width="6%">Carattere</th>
            <th width="5%">Comporta spese?</th>
            <th width="20%">Tipologie</th>
            <th width="20%">Struttura Capofila</th>
            <th width="20%">Strutture Coinvolte</th>
            <th width="4%">Usi</th>
            <th width="5%">Funzioni</th>
          </tr>
        </thead>
        <tbody>
        <c:forEach var="ms" items="${measures}" varStatus="status">
          <tr>
            <td>
              <img src="${initParam.urlDirectoryImmagini}mis-${ms.carattere.informativa}.png" class="ico-small" alt="icona" title="Misura ${ms.carattere.nome}" /> &nbsp;
              <a href="${initParam.appName}/?q=${param['q']}&p=mes&mliv=${ms.codice}&r=${param['r']}" title="${ms.codice}">
                <c:out value="${ms.nome}" />
              </a>
            </td>
            <td><c:out value="${ms.carattere.nome}" /></td>
            <td><c:out value="${fn:toUpperCase(ms.getOnerosa(ms.onerosa))}" /></td>
            <td><ul class="list-group">
            <c:forEach var="tm" items="${ms.tipologie}" varStatus="innerStatus">
              <li><c:out value="${tm.nome}" /></li>
            </c:forEach>
            </ul></td>
            <td>
              <ul class="list-group">
            <c:forEach var="cp" items="${ms.capofila}" varStatus="innerStatus">
              <c:if test="${innerStatus.count eq ms.capofila.size()}">
                <li>
                  <img src="${initParam.urlDirectoryImmagini}str-l${cp.livello}.png" class="ico-small" alt="icona" title="Struttura di livello ${cp.livello}" /> 
                  <c:out value="${cp.prefisso}" /> <c:out value="${cp.nome}" />
                </li>
              </c:if>
            </c:forEach>
            <c:forEach var="cp2" items="${ms.capofila2}" varStatus="innerStatus">
              <c:if test="${innerStatus.count eq ms.capofila2.size()}">
                <li>
                  <img src="${initParam.urlDirectoryImmagini}str-l${cp2.livello}.png" class="ico-small" alt="icona" title="Struttura di livello ${cp2.livello}" /> 
                  <c:out value="${cp2.prefisso}" /> <c:out value="${cp2.nome}" />
                </li>
              </c:if>
            </c:forEach>
            <c:forEach var="cp3" items="${ms.capofila3}" varStatus="innerStatus">
              <c:if test="${innerStatus.count eq ms.capofila3.size()}">
                <li>
                  <img src="${initParam.urlDirectoryImmagini}str-l${cp3.livello}.png" class="ico-small" alt="icona" title="Struttura di livello ${cp3.livello}" /> 
                  <c:out value="${cp3.prefisso}" /> <c:out value="${cp3.nome}" />
                </li>
              </c:if>
            </c:forEach>
              </ul>
            </td>
            <td>
              <ul class="list-group">
            <c:forEach var="gr" items="${ms.gregarie}" varStatus="innerStatus">
                <li>
                  <img src="${initParam.urlDirectoryImmagini}str-l${gr.livello}.png" class="ico-small" alt="icona" title="Struttura di livello ${gr.livello}" /> 
                  <c:out value="${gr.prefisso}" /> <c:out value="${gr.nome}" />
                </li>
            </c:forEach>
              </ul>
            </td>
            <td class="text-center"><c:out value="${ms.uso}" /></td>
            <c:choose>
            <c:when test="${ms.dettagli}">
              <c:set var="bg" value="bgAct14" scope="page" />
              <c:set var="title" value="Dettagli &nbsp; inseriti" scope="page" />
              <c:set var="url" value="?q=ic&p=mes&mliv=${ms.codice}" scope="page" />
            </c:when>
            <c:otherwise>
              <c:set var="bg" value="bgcolor3" scope="page" />
              <c:set var="title" value="Aggiungi dettagli" scope="page" />
              <c:set var="url" value="?q=ic&p=add&mliv=${ms.codice}" scope="page" />
            </c:otherwise>
            </c:choose>
            <td class="text-center">
              <a href="${initParam.appName}/${url}&r=${param['r']}" class="btn ${bg} badge-pill refresh" title="${title}">
                Dettagli
              </a>&nbsp;
            </td>
          </tr>
        </c:forEach>
        </tbody>
      </table>
    </div>
    <h4 class="reportStateAct">&nbsp; N. misure registro: 
      <button type="button" class="btn bgAct26">
        <span class="badge">${measures.size()}</span>
      </button>
    </h4>
    <a href="${mnm}" class="btn btn-success btn-lg btn-block" title="Aggiungi una nuova misura di contenimento o prevenzione al registro delle misure">
      <i class="fa-solid fa-file-circle-plus"></i> &nbsp;Aggiungi Misura
    </a>
