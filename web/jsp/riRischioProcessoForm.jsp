<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="URL.jspf" %>
<c:set var="process" value="${requestScope.processo}" scope="page" />
<c:set var="risks" value="${requestScope.rischi}" scope="page" />
    <h3 class="mt-1 m-0 font-weight-bold">Associazione Processo-Rischio</h3>
    <hr class="riga"/>
    <h4 class="p-2 bgAct3 rounded popupMenu heading"> 
      <i class="fas fa-cogs" title="processo"></i>&nbsp;
      Processo:
      <a href="${initParam.appName}/?q=pr&p=pro&pliv=${process.id}&liv=${process.livello}&r=${param['r']}">
        <c:out value="${process.nome}" />
      </a>
    </h4>
    <div class="p-3 p-md-4 border rounded-3 icon-demo-examples bgAct24">
      <h4 class="p-2  popupMenu"> 
        <i class="fa-solid fa-triangle-exclamation" title="rischi"></i>&nbsp; 
        Rischi cui gi&agrave; risulta esposto questo processo:
        <span class="badge badge-danger float-right">
          <c:out value="${process.rischi.size()}" />
        </span>
      </h4>
      <ul class="list-group">
      <c:forEach var="risk" items="${process.rischi}" varStatus="status">
        <c:set var="bgAct" value="bgAct4" scope="page" />
        <c:if test="${status.index mod 2 eq 0}">
          <c:set var="bgAct" value="bgAct20" scope="page" />
        </c:if>
        <li class="list-group-item ${bgAct}">
          <a href="${initParam.appName}/?q=ri&idR=${risk.id}&r=${param['r']}">
            <c:out value="${risk.nome}" />
          </a> 
          <span class="float-right">
            <code class="text-dark">
              (Fattori: <strong><c:out value="${risk.fattori.size()}" /></strong> |
               Misure: <strong><c:out value="${risk.misure.size()}" /></strong>)
            </code>
          </span>
        </li>
      </c:forEach>
      </ul>
      <c:if test="${process.rischi.size() eq zero}">
        <span class="pHeader heading bgAct13 alert-danger" title="Per associare un rischio a questo processo usa la form sottostante">Ancora nessuno</span>
      </c:if>
    </div>
    <!-- Error -->
    <c:if test="${param['msg'] eq 'dupKey'}">
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
      <button type="button" class="close" data-dismiss="alert" aria-label="Close">
        <span aria-hidden="true">&times;</span>
      </button>
      <strong>ATTENZIONE: </strong>
      il rischio scelto &egrave; gi&agrave; presente tra i rischi associati al processo!<br />
      Impossibile associarlo nuovamente.
    </div>
    </c:if>
    <!-- Form -->
    <form id="add-pat-form" class="form-horizontal" action="" method="post">
      <input type="hidden" id="pat-id" name="pliv2" value="${process.id}" />
      <div class="form-custom form-group" id="adp-form">
        <h4 class="btn-lightgray">Rischio corruttivo da collegare al processo (verr&agrave; aggiunto all'elenco dei rischi cui il processo e' esposto)</h4>
        <div class="panel-body form-group">
          <!--  Rischio corruttivo -->
          <div class="row alert">
            <div class="content-holder col-sm-12">
              <div class="column">
                <div class="mandatory-thin"><strong> &nbsp;Rischio da associare</strong></div>
                <div class="text-center"><div id="custom-error-location-2"></div></div>
              </div>
              <div class="row">
                <div class="text-center lastMenuContent">
                  <select id="risk-add" name="r-id">
                    <option value="">-- scelta rischio corruttivo -- </option>
                    <c:forEach var="risk" items="${risks}">
                      <option value="${risk.id}"><c:out value="${risk.nome}" /></option>
                    </c:forEach>
                  </select>
                </div>
                &nbsp;
              </div>
            </div>
            &nbsp;
          </div>
          &nbsp;&nbsp;
          <div class="centerlayout">
            <button type="submit" id="ris-sub" class="btn btn-success" value="Save">
              <i class="far fa-save"></i> Salva
            </button>
          </div>
        </div>
        <br />
      </div>
    </form>
    <script>
    $(document).ready(function() {
        $("#ris-sub").click(function() {
            if ($("#risk-add").val() == 0) {
                alert("Occorre selezionare un rischio corruttivo");
                return false;
            }
        });
    });
    </script>