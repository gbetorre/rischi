<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="URL.jspf" %>
<c:set var="quests" value="${requestScope.elencoInterviste}" scope="page" />
    <h4 class="btn-lightgray">Interviste effettuate</h4>
    <table class="table table-bordered table-hover table-sm" id="listInt">
      <thead class="thead-light">
        <tr>
          <th class="bg-primary text-white" scope="col" width="*"><div class="text-center">Data Intervista</div></th>
          <th class="" scope="col" width="12%">Struttura Liv 1</th>
          <th class="" scope="col" width="12%">Struttura Liv 2</th>
          <th class="" scope="col" width="12%">Struttura Liv 3</th>
          <th class="" scope="col" width="12%">Struttura Liv 4</th>
          <th class="bg-warning" scope="col" width="12%">Macroprocesso</th>
          <th class="bgcolorgreen" scope="col" width="12%">Processo</th>
          <th class="reportWp" scope="col" width="12%">Sottoprocesso</th>
        </tr>
      </thead>
      <tbody>
    <c:set var="status" value="" scope="page" />
    <c:forEach var="iview" items="${quests}" varStatus="loop">
      <fmt:formatDate var="iviewsqldate" value="${iview.dataUltimaModifica}" pattern="yyyy-MM-dd" scope="page" />
      <fmt:formatDate var="iviewsqltime" value="${iview.oraUltimaModifica}" pattern="HH_mm_ss" scope="page" />
      <c:url var="rqsInstance" context="${initParam.appName}" value="/" scope="page">
        <c:param name="q" value="ri" />
        <c:param name="p" value="rqs" />
        <c:param name="sliv1" value="${iview.struttura.informativa}" />
        <c:param name="sliv2" value="${iview.struttura.figlie.get(zero).informativa}" />
        <c:param name="sliv3" value="${iview.struttura.figlie.get(zero).figlie.get(zero).informativa}" />
        <c:param name="sliv4" value="${iview.struttura.figlie.get(zero).figlie.get(zero).figlie.get(zero).informativa}" />
        <c:param name="pliv1" value="${iview.processo.informativa}" />
        <c:param name="pliv2" value="${iview.processo.processi.get(zero).informativa}" />
        <c:param name="pliv3" value="${iview.processo.processi.get(zero).processi.get(zero).informativa}" />
        <c:param name="d" value="${iviewsqldate}" />
        <c:param name="t" value="${iviewsqltime}" />
        <c:param name="r" value="${ril}" />
      </c:url>
        <tr class="active">
          <td class="bgcolor1" width="*">
            <%--a href="${initParam.appName}/?q=ri&p=rqs&sliv1=${iview.struttura.informativa}&sliv2=${iview.struttura.figlie.get(zero).informativa}&sliv3=${iview.struttura.figlie.get(zero).figlie.get(zero).informativa}&sliv4=${iview.struttura.figlie.get(zero).figlie.get(zero).figlie.get(zero).informativa}&r=${param['r']}"--%>
            <a href="${rqsInstance}">
              <c:out value="${loop.count}" /><sup>a</sup> &ndash; 
              <fmt:formatDate value="${iview.dataUltimaModifica}" pattern="dd/MM/yyyy" /> 
              <fmt:formatDate value="${iview.oraUltimaModifica}" pattern="HH:mm" />
            </a>
          </td>
          <td class="bg-primary text-white" width="12%"><strong><c:out value="${iview.struttura.nome}" /></strong></td>
          <td class="bg-primary text-white" width="12%"><strong><c:out value="${iview.struttura.figlie.get(zero).nome}" /></strong></td>
          <td class="bg-primary text-white" width="12%"><div class="text-center"><strong><c:out value="${iview.struttura.figlie.get(zero).figlie.get(zero).nome}" /></strong></div></td>
          <td class="bg-primary text-white" width="12%"><div class="text-center"><strong><c:out value="${iview.struttura.figlie.get(zero).figlie.get(zero).figlie.get(zero).nome}" /></strong></div></td>
          <td class="bgcolor1" width="12%"><strong><c:out value="${iview.processo.nome}" /></strong></td>
          <td class="bgcolor1" width="12%"><c:out value="${iview.processo.processi.get(zero).nome}" /></td>
          <td class="bgcolor1" width="12%"><c:out value="${iview.processo.processi.get(zero).processi.get(zero).nome}" /></td>
        </tr>
    </c:forEach>
      </tbody>
    </table>
    <h4 class="heading avvisiTot">Interviste trovate: <button type="button" class="btn btn-success"><span class="badge badge-pill badge-light">${quests.size()}</span></button></h4>
    <script type="text/javascript">
      $(document).ready(function() {
        $('#listInt').DataTable({
          "ordering": true,  
          "paging": false,
          "bInfo": false,
          "oLanguage": {
              "sSearch": "Filtra:"
              },
          "searchPanes": {
              "viewTotal": false
              }
        });
      });
    </script>
