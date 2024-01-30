<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
  <c:set var="pxi" value="${requestScope.pxi}" scope="page" />
  <c:set var="classSuffix" value="${fn:toLowerCase(pxi.labelWeb)}" scope="page" />
  <c:if test="${fn:indexOf(classSuffix, ' ') gt -1}">
    <c:set var="classSuffix" value="${fn:substring(classSuffix, 0, fn:indexOf(classSuffix, ' '))}" scope="page" />
  </c:if>
  <fmt:setLocale value="it_IT"/>
  <c:set var="cacheSQLDate" value="${pxi.extraInfo4}" scope="page" />
  <fmt:parseDate var="cacheDate" value="${cacheSQLDate}" pattern="yyyy-MM-dd" scope="page" />
  <fmt:formatDate var="dataRicalcolo" value="${cacheDate}" pattern="dd/MM/yyyy" />
    <form accept-charset="ISO-8859-1" id="not-form" class="panel subfields" action="" method="post">
      <input type="hidden" id="pat-id" name="pliv2" value="${pxi.cod1}" />
      <div class="panel-heading bgAct1">
        <div class="noHeader">
          <i class="fa-solid fa-file-circle-plus"></i>
          <c:out value="Motivazione giudizio sintetico" escapeXml="false" />
        </div>
      </div>
      <hr class="separatore" />
      <div class="panel-body">
        <br />
        <div class="row">
          <div class="col-sm-5"><br />PxI (valore precalcolato)&nbsp;
            <span title="Il valore precalcolato del PxI risale all'ultimo aggiornamento manuale dei dati (${dataRicalcolo})">
              <i class="fa-solid fa-circle-question"></i>
            </span>
          </div>
          <div class="col-sm-6 text-center">
            <div class="value bgcolor-${classSuffix} border-${classSuffix}">
              <p class="text-center">
                <c:out value="${pxi.labelWeb}" />
              </p>
            </div>
          </div>
        </div>
        <br />
        <div class="row">
          <div class="col-sm-5"><br />PxI (valore effettivo)&nbsp;
            <span title="Il valore effettivo del PxI &egrave; quello calcolato in questo istante in base agli ultimi dati disponibili">
              <i class="fa-solid fa-circle-question"></i>
            </span>
          </div>
            <div class="col-sm-6 text-center">
            <div class="value bgcolor-${fn:toLowerCase(param['pxi'])}">
              <p class="text-center">
                <c:out value="${param['pxi']}" />
              </p>
            </div>
          </div>
        </div>
        <br />
        <div class="row">
          <div class="col-sm-5"><br />Motivazione</div>
          <div class="col-sm-6">
            <textarea class="form-control" rows="4" id="pi-note" name="pi-note" placeholder="Inserisci una motivazione al PxI"><c:out value="${pxi.informativa}" escapeXml="false" /></textarea>
            <div class="charNum text-center"></div>
          </div>
        </div>
        <br />
        <br />
        &nbsp;
        <div class="centerlayout">
          <button type="submit" class="btn btn-success" value="Save">
            <i class="far fa-save"></i> Salva
          </button>
        </div>
      </div>
      <hr class="separatore" />
    </form>
    <script type="text/javascript">
      $(document).ready(function () {
        $('#not-form').validate ({
          rules: {
            'pi-note': {
              required: true
            },
          }, 
          messages: {
            'pi-note': "Inserire una nota per il giudizio sintetico",
          },
          submitHandler: function (form) {
            return true;
          }
        });
      
        $('#pi-note').keyup(function (e) {
          var chars = $(this).val().length;
          $(this).next('div').text(chars + ' caratteri inseriti');
        });
      });
    </script> 
