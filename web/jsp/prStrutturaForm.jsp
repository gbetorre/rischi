<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="URL.jspf" %>
<c:set var="process" value="${requestScope.pat}" scope="page" />
<c:set var="fasi" value="${requestScope.listaFasi}" scope="page" />
    <link rel="stylesheet" href="//code.jquery.com/ui/1.13.2/themes/base/jquery-ui.css">
    <c:if test="${not empty param['msg']}">
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
      <button type="button" class="close" data-dismiss="alert" aria-label="Close">
        <span aria-hidden="true">&times;</span>
      </button>
      <strong>ATTENZIONE: </strong>
      <c:out value="${param['msg']}" />
    </div>
    </c:if>
    <div class="form-custom bg-note">
      <form accept-charset="ISO-8859-1" id="uas-form" action="" method="post">
        <input type="hidden" id="pat-id" name="pliv2" value="${process.id}" />
        <div class="panel-heading bgAct6" id="details">
          <h5 class="fw-bold text-dark">
            <i class="fa-solid fa-file-circle-plus"></i>
            <c:out value="${requestScope.tP}" />
          </h5>
        </div>
        <div class="panel-body">
          <div class="row">  <!-- MACRO -->
            <div class="content-holder col-sm-10 bgAct15">
              <strong> &nbsp;Macroprocesso:</strong>
              <c:out value="${process.padre.nome}" escapeXml="false" />
            </div>
          </div>
          <hr class="separapoco" />
          <div class="row">  <!-- PROCESSO -->
            <div class="content-holder col-sm-10 bgAct19">
              <strong> &nbsp;Processo:</strong>
              <a href="${initParam.appName}/?q=pr&p=pro&pliv=${process.id}&liv=2&r=${param['r']}" title="${process.codice}">
                <c:out value="${process.nome}" escapeXml="false" />
              </a>
            </div>
          </div>
          <hr class="separatore" />
        <c:forEach var="fase" items="${fasi}" varStatus="status">
          <div class="row">
            <c:if test="${fase.id eq param['idA']}">
            <input type="hidden" id="act-id" name="ac-id" value="${fase.id}" />
            <div class="col-sm-10 mx-auto px-0">
              <div class="str-container">
                <div class="reportRow form-control bgAct3" id="fase${status.count}">
                  <a href="${initParam.appName}/?q=pr&p=ias&liv=${param['liv']}&pliv=${process.id}&pliv1=${param['pliv1']}&pliv0=${param['pliv0']}&idA=${fase.id}&r=${param['r']}#fase${status.count}">
                    <span class="bgAct">&nbsp;Fase <c:out value="${status.count}" />:</span>
                    &nbsp;<c:out value="${fase.nome}" />
                  </a>
                </div>
                <div class="bgAct26 text-white form-custom marginNarrow">
                  <strong> 
                    &nbsp;Strutture
                    <span class="badge badge-pill float-right"><c:out value="${fase.strutture.size()}" /> </span>
                  </strong>
                </div>
                <div class="content-holder bgAct31">
                  <span class="bgAct2">&nbsp;
                    Strutture di organigramma gi&agrave; associate:&nbsp;
                  </span>
                  <c:if test="${empty fase.strutture}">
                    <span class="textcolormaroon"><em>Nessuna</em></span>
                  </c:if>
                  <ul class="list-group">
                  <c:forEach var="str" items="${fase.strutture}">
                    <li class="list-group-item reportAct bgAct28 d-flex justify-content-between align-items-center py-1">
                      <span class="marginLeft d-flex align-items-center">
                        <img src="${initParam.urlDirectoryImmagini}str-l${str.livello}.png" class="ico-small" alt="icona" title="Struttura di livello ${str.livello}" />
                        &nbsp;
                        <c:out value="${str.prefisso}" escapeXml="false" />&nbsp; 
                        <c:out value="${str.nome}" escapeXml="false" />
                      </span>
                      
              <span class="float-right ">
                <button type="submit" class="btn btnNav btn-danger bgAct10 text-dark badge lightTable align-center" id="btn-str-${str.id}" name="detach" value="id_struttura_liv${str.livello}-${str.id}">
                  Elimina
                </button>
              </span>
                      
                    </li>
                  </c:forEach>
                  </ul>                
                </div>
                
              </div>
              <div class="str-container">
                <div class="bgAct26 text-white form-custom marginNarrow">
                  <strong> 
                    &nbsp;Soggetti
                    <span class="badge badge-pill float-right"><c:out value="${fase.soggetti.size()}" /> </span>
                  </strong>
                </div>
                <div class="content-holder bgAct31">
                  <span class="bgAct2">&nbsp;
                    Soggetti contingenti gi&agrave; associati:
                  </span>
                  <c:if test="${empty fase.soggetti}">
                    <span class="textcolormaroon"><em>Nessuno</em></span>
                  </c:if>                
                  <ul class="list-group">
                  <c:forEach var="sub" items="${fase.soggetti}">
                    <li class="list-group-item bgAct29 d-flex justify-content-between align-items-center py-1" title="${sub.informativa}">
                      <span class="marginLeft">
                        <img src="${initParam.urlDirectoryImmagini}person-fill.png" class="ico-small" alt="icona" title="Soggetto contingente" /> 
                        <c:out value="${sub.nome}" escapeXml="false" />
                      </span>
                      
                                  <span class="float-right ">
                <button type="submit" class="btn btnNav btn-danger bgAct10 text-dark badge lightTable align-center" id="btn-sub-${sub.id}" name="detach" value="id_soggetto_contingente-${sub.id}">
                  Elimina
                </button>
              </span>
                      
                    </li>
                  </c:forEach>
                  </ul>
                </div>
              </div>
              <hr class="separapoco" />
            </div>
            <hr class="separatore" />
            </c:if>
            </div>
        </c:forEach>
        </div>
        <hr class="separatore" />
      </form>
    </div>
