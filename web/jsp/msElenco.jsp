<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="URL.jspf" %>
<c:set var="measures" value="${requestScope.misure}" scope="page" />
<style>
       .custom-button {
            padding: 10px 20px;
            background-color: #2ecc71;
            /*color: white;*/
            font-size: 16px;
            border: none;
            border-radius: 5px;
            text-align: center;
            text-decoration: none;
            cursor: pointer;
            box-shadow: 0 5px 15px rgba(0,0,0,0.3); /* Adding depth with box-shadow */
        }
        .custom-button:hover {
            background-color: #2980b9;
            color: black !important;
        }
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
    <h3 class="mt-1 m-0 font-weight-bold float-left">Registro delle misure di prevenzione</h3>
    <%-- 
    <a href="${riCSV}" class="float-right badge badge-pill lightTable bgAct20" title="Scarica il database completo del registro dei rischi corruttivi">
      <i class="fas fa-download"></i>Scarica tutti i dati
    </a> --%>
    <hr class="riga"/>
    <c:if test="${param['msg'] eq 'newRel'}">
    <script>
      $(function () { 
          var duration = 4000; // 4 seconds
          setTimeout(function () { $('#mainAlertMessage').hide(); }, duration);
      });
    </script>
    <div id="mainAlertMessage" class="alert alert-success alert-dismissible" role="alert">
      Nuova associazione tra rischio e processo creata con successo
    </div>
    </c:if>
    <div class="p-3 p-md-4 border rounded-3 icon-demo-examples bgAct26">
      <div class="fs-2 mb-3 text-white">
        Misure &nbsp;
        <a href="${mnm}" class="custom-button text-white" title="Aggiungi una nuova misura di contenimento o prevenzione al registro delle misure">
          <i class="fa-solid fa-file-circle-plus"></i> &nbsp;Aggiungi Misura
        </a>
        <%-- 
        <span class="float-right panel-body monospace">
          <cite>in parentesi: (n. di processi esposti a questo rischio)</cite>
        </span>        --%> 
      </div>
      <table class="">
        <thead>
          <tr>
            <th>Misura</th>
            <th>Carattere</th>
            <th>Tipologie</th>
          </tr>
        </thead>
        <tbody>
        <c:forEach var="ms" items="${measures}" varStatus="status">
          <tr>
            <td><c:out value="${ms.nome}" /></td>
            <td><c:out value="${ms.carattere.nome}" /></td>
            <td><ul class="list-group">
            <c:forEach var="tm" items="${ms.tipologie}" varStatus="innerStatus">
              <li><c:out value="${tm.nome}" /></li>
            </c:forEach>
            </ul></td>
          </tr>
        </c:forEach>
        </tbody>
      </table>
      
        
        <c:set var="alarm" value="" scope="page" />
        <c:set var="explain" value="Clicca per visualizzare i dettagli del rischio" scope="page" />
        <c:if test="${risk.impatto eq zero}">
          <c:set var="explain" value="Questo rischio &egrave; contrassegnato perch&eacute; non &egrave; ancora associato ad alcun processo" scope="page" />
          <c:set var="alarm" value="pHeader heading bgAct13 alert-danger" scope="page" />
          <i class="fa-solid fa-triangle-exclamation"></i>
        </c:if>
          <a href="${initParam.appName}/?q=ri&idR=${risk.id}&r=${param['r']}" class="${alarm}" title="${explain}">
            <c:out value="${risk.nome}" />
          </a>
          <span class="float-right ${alarm}">
            <a href="${initParam.appName}/?q=ri&p=adp&idR=${risk.id}&r=${param['r']}" id="btn-tar" title="Clicca per associare un processo a questo rischio">
              (<c:out value="${risk.impatto}" />)&nbsp;&nbsp;
              <i class="fa-regular fa-square-plus"></i>&nbsp;
            </a>
          </span>
      

    </div>
    <h4 class="reportStateAct">&nbsp; N. misure registro: 
      <button type="button" class="btn bgAct26">
        <span class="badge badge-pill bgAct9 textcolormaroon">${measures.size()}</span>
      </button>
      <%-- 
      <a href="${riCSV}" class="float-right badge badge-pill lightTable bgAct20" title="Scarica il database completo del registro dei rischi corruttivi">
        <i class="fas fa-download"></i> <span class="sezioneElenco">Scarica tutti i dati&nbsp;</span>
      </a>  --%>
    </h4>
    <a href="${mnm}" class="custom-button btn-lg btn-block" title="Aggiungi una nuova misura di contenimento o prevenzione al registro delle misure">
      <i class="fa-solid fa-file-circle-plus"></i> &nbsp;Aggiungi Misura
    </a>
