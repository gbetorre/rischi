<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="meas" value="${requestScope.misura}" scope="page" />
<c:set var="mon" value="${requestScope.misurazione}" scope="page" />
<c:set var="ind" value="${mon.indicatore}" scope="page" />
<c:set var="phas" value="${ind.fase}" scope="page" />
<fmt:formatDate var="indLastMod" value="${ind.dataUltimaModifica}" pattern="dd/MM/yyyy" /> 
  <c:catch var="exception">
    <link rel="stylesheet" href="https://code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
    <style>
        .form-control::placeholder {
            color: #6c757d; /* Default placeholder color */
            opacity: 1; /* Ensure full opacity by default */
        }
        .form-control:focus::placeholder {
            visibility: hidden; /* Hide placeholder on focus */
        }
        .error {
            color: maroon;      /* Change text color */
            font-weight: bold;  /* Make text bold */
            /*font-size: 14px;  /* Adjust font size */
            /*margin-top: 5px;  /* Add some space above the message */
            /*background: #cccccc;*/
        }
    </style>
    <form accept-charset="ISO-8859-1" id="mon-form" class="panel subfields-green" action="" method="post">
      <input type="hidden" id="mon-meas" name="mon-meas" value="${meas.codice}" />
      <input type="hidden" id="mon-fase" name="mon-fase" value="${phas.id}" />
      <input type="hidden" id="mon-ind" name="mon-ind" value="${ind.id}" />
      <div class="panel-heading bgAct13">
        <div class="noHeader">
          <i class="fa-regular fa-pen-to-square"></i>&nbsp;
          <c:out value="Aggiornamento monitoraggio" />
        </div>
      </div>
      <hr class="separatore" />
      <div class="panel-body">
        <div class="row"> 
          <div class="content-holder col-sm-10 bgAct1">
            <strong> &nbsp;Misura:</strong>
            <a href="${initParam.appName}/?q=ic&p=mes&mliv=${meas.codice}&r=${param['r']}" title="${meas.codice}">
              <c:out value="${meas.nome}" escapeXml="false" />
            </a>
          </div>
        </div>
        <div class="row"> 
          <div class="content-holder col-sm-10 bgAct">
            <strong> &nbsp;Fase:</strong> 
            <c:out value="${phas.nome}" escapeXml="false" />
          </div>
        </div>
        <div class="row"> 
          <div class="content-holder col-sm-10 bgAct19">
            <strong> &nbsp;Indicatore:</strong>
            <a href="${initParam.appName}/?q=ic&p=ind&idI=${ind.id}&idF=${phas.id}&mliv=${meas.codice}&r=${param['r']}" title="Modificato:${indLastMod} ${fn:substring(ind.oraUltimaModifica,0,5)}">
              <c:out value="${ind.nome}" escapeXml="false" />
            </a>
            <dl class="sezioneElenco custom-dl marginBottom">
              <dt class="text-primary">Tipo Indicatore</dt>
              <dd>
                <span class="badge border-basso textcolormaroon">
                  <c:out value="${ind.tipo.nome}" />
                </span>
              </dd>
              <dt class="text-primary">Baseline</dt>
              <dd>
                <span class="textcolormaroon">
                  <c:out value="${fn:toUpperCase(ind.getLabel(ind.baseline))}" />
                </span>
              </dd>
              <dt class="text-primary">Data Baseline</dt>
              <dd>
                <fmt:formatDate value="${ind.dataBaseline}" pattern="dd/MM/yyyy" />
              </dd>
              <dt class="text-primary">Target</dt>
              <dd>
                <span class="textcolormaroon">
                  <c:out value="${fn:toUpperCase(ind.getLabel(ind.target))}" />
                </span>
              </dd>
              <dt class="text-primary">Data Target</dt>
              <dd>
                <fmt:formatDate value="${ind.dataTarget}" pattern="dd/MM/yyyy" />
              </dd>
            </dl>
          </div>
        </div>
        <hr class="separatore" />
        <div class="row">
          <div class="col-sm-1">&nbsp;</div>
          <div class="col-sm-4 mandatory-xthin bgAct13"><strong>Data Monitoraggio</strong></div>
          <div class="col-sm-5">
            <input type="text" class="form-custom" id="mon-data" name="mon-data" value="<fmt:formatDate value="${mon.dataMisurazione}" pattern="dd/MM/yyyy" />" title="data della misurazione" readonly>
          </div>
        </div>
        <hr class="separatore">
        <div class="row">
          <div class="col-sm-1">&nbsp;</div>
            <div class="col-sm-4 mandatory-xthin bgAct13"><strong>Risultato</strong></div>
            <div class="col-sm-5">
              <input type="text" class="form-custom" id="mon-value" name="mon-value" value="${fn:toUpperCase(ind.getLabel(mon.valore))}" title="risultato della misurazione" readonly>
            </div>
        </div>
        <hr class="separatore">
        <div class="row">
          <div class="col-sm-1">&nbsp;</div>
          <div class="col-sm-4 mandatory bgAct13"><strong>Azioni svolte per raggiungere l'obiettivo</strong></div>
          <div class="col-sm-6">
            <textarea class="form-control" id="mon-descr" name="mon-descr" placeholder="${mon.informativa}" readonly></textarea>
          </div>
        </div>
        <hr class="separatore">
        <div class="row">
          <div class="col-sm-1">&nbsp;</div>
          <div class="col-sm-4 bg-note"><br><strong>Motivazioni ritardo / mancato raggiungimento</strong></div>
          <div class="col-sm-6">
            <textarea class="form-control" id="mon-infos" name="mon-infos" placeholder="${mon.descrizione}" readonly></textarea>
          </div>
        </div>
        <hr class="separatore" />
        <div class="centerlayout">
          <button type="submit" class="btn btnNav" id="btn-save" value="Save">
            <i class="fa-solid fa-paperclip"></i>&nbsp;  Aggiungi allegato 
          </button>
        </div>
      </div>
    </form>
    <div id="popup1" class="popup">
      <div id="popup1Under" class="popupundertitle">
        <div id="titolopopup1" class="popuptitle" ></div>
        <div id="titolopopup1Under">
          <a href="Javascript:popupWindow('','popup1',false,'');"><img src="web/img/close-icon.gif" border="0" width="15" height="15" alt="Chiudi" title="Chiudi" /></a>
        </div>
      </div>
      <div class="popupbody" id="popup1Text" ></div>
    </div>
  </c:catch>
  <c:out value="${exception}" />
