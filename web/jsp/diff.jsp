<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html>
  <head>
    <link rel="stylesheet" href="<c:out value="${requestScope.baseHref}${initParam.urlDirectoryStili}" />style.css" type="text/css" />
    <style>
      table {
        border-spacing: 1;
        border-collapse: collapse;
        background: white;
        border-radius: 6px;
        overflow: hidden;
        /*max-width: 900px;*/
        width: 90%;
        margin: 0 auto;
        position: relative;
      }
      table * {
        position: relative;
      }
      table td, table th {
        padding-left: 8px;
      }
      table thead tr {
        height: 60px;
        background: #FFED86;
        font-size: 16px;
      }
      table tbody tr {
        height: 48px;
        border-bottom: 1px solid #E3F1D5;
      }
      table tbody tr:last-child {
        border: 0;
      }
      table td, table th {
        text-align: center;
      }
      table td.l, table th.l {
        text-align: right;
      }
      table td.c, table th.c {
        text-align: center;
      }
      table td.r, table th.r {
        text-align: center;
      }
      
      @media screen and (max-width: 35.5em) {
        table {
          display: block;
        }
        table > *, table tr, table td, table th {
          display: block;
        }
        table thead {
          display: none;
        }
        table tbody tr {
          height: auto;
          padding: 8px 0;
        }
        table tbody tr td {
          padding-left: 45%;
          margin-bottom: 12px;
        }
        table tbody tr td:last-child {
          margin-bottom: 0;
        }
        table tbody tr td:before {
          position: absolute;
          font-weight: 700;
          width: 40%;
          left: 10px;
          top: 0;
        }
        table tbody tr td:nth-child(1):before {
          content: "Code";
        }
        table tbody tr td:nth-child(2):before {
          content: "Stock";
        }
        table tbody tr td:nth-child(3):before {
          content: "Cap";
        }
        table tbody tr td:nth-child(4):before {
          content: "Inch";
        }
        table tbody tr td:nth-child(5):before {
          content: "Box Type";
        }
      }
      body {
        background: #9BC86A;
        font: 400 14px 'Calibri','Arial';
        padding: 20px;
      }
      
      blockquote {
        color: white;
        text-align: center;
      }
    </style>
  </head>
  <body>
    <table>
      <thead>
        <tr>
          <th width="*">PROCESSO</th>
          <th width="6%">I</th>
          <th width="6%">I1</th>
          <th width="6%">I2</th>
          <th width="6%">I3</th>
          <th width="6%">I4</th>
          <th width="6%">P</th>
          <th width="6%">P1</th>
          <th width="6%">P2</th>
          <th width="6%">P3</th>
          <th width="6%">P4</th>
          <th width="6%">P5</th>
          <th width="6%">P6</th>
          <th width="6%">P7</th>
          <th width="6%">PxI</th>
          <th width="6%"></th>
        </tr>
      <thead>
      <tbody>
  <c:set var="oldValues" value="${requestScope.lista.get('C')}" scope="page" />
  <c:set var="newValues" value="${requestScope.lista.get('A')}" scope="page" />
  <c:set var="patsMap" value="${requestScope.lista.get('P')}" scope="page" />
  <c:set var= "keys" value ="${patsMap.keySet()} " scope ="page" />

  <c:forEach var="entry" items ="${patsMap}">
    <c:forEach var="pat" items="${entry.value}">
    <tr>
      <td rowspan="2">
        <a href="${requestScope.baseHref}?q=pr&p=pro&pliv=${pat.id}&liv=2&r=${param['r']}">
          <c:out value="${pat.nome}" />
        </a>
      </td>
      <c:set var="patInd" value="${pat.indicatori}" />
      <c:forEach var="ind" items="${patInd}">
        <td><c:out value="${ind.value.informativa}" /></td>
      </c:forEach>
      <td>
        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-arrow-left" viewBox="0 0 16 16">
          <path fill-rule="evenodd" d="M15 8a.5.5 0 0 0-.5-.5H2.707l3.147-3.146a.5.5 0 1 0-.708-.708l-4 4a.5.5 0 0 0 0 .708l4 4a.5.5 0 0 0 .708-.708L2.707 8.5H14.5A.5.5 0 0 0 15 8"/>
        </svg>
        <em>Valori precedenti</em>
      </td>
    </tr>
    <tr>
      <c:forEach var="newEntry" items="${newValues}">
        <c:if test="${pat.id eq newEntry.key}">
          <c:forEach var="newIndMap" items="${newEntry.value}">
          <c:choose>
            <c:when test="${oldValues.get(newEntry.key).value.informativa ne newIndMap.value.informativa}">
              <td class="bgcolor-${fn:toLowerCase(newIndMap.value.informativa)}"><c:out value="${newIndMap.value.informativa}" /></td>
            </c:when>
          </c:choose>
          
          </c:forEach>
        </c:if>
      </c:forEach>
      <td>
        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-arrow-left" viewBox="0 0 16 16">
          <path fill-rule="evenodd" d="M15 8a.5.5 0 0 0-.5-.5H2.707l3.147-3.146a.5.5 0 1 0-.708-.708l-4 4a.5.5 0 0 0 0 .708l4 4a.5.5 0 0 0 .708-.708L2.707 8.5H14.5A.5.5 0 0 0 15 8"/>
        </svg>
        <em>Valori aggiornati</em>
      </td>
    </tr>
    </c:forEach>
  </c:forEach>  

    
  <%--
    
      <c:forEach var="newEntry" items="${newValues}">
        <c:out value="${newEntry.key}" />
        
        <c:forEach var="newIndMap" items="${newEntry.value}">
        
          <c:out value="${newIndMap.key}" /> = <c:out value="${newIndMap.value.informativa}" />
        </c:forEach>
        <br />
      </c:forEach>  
  
  <c:forEach var= "entry" items ="${patsMap}">
    <c:forEach var="pat" items="${entry.value}">
    <tr>
      <td><c:out value="${pat.nome} (${pat.id })" /></td>
      <c:set var="patInd" value="${pat.indicatori}" />
      <c:forEach var="newEntry" items="${newValues}">
      @@<c:out value="${pat.id}" />@@
      ##<c:out value="${newEntry.key}" />##<br />
        <c:if test="${pat.id eq newEntry.key}">
          <c:forEach var="ind" items="${patInd}">
            <td><c:out value="${ind.value.informativa}" /></td>
            <c:forEach var="newMap" items="${newEntry.value}">
              <c:choose>
                <c:when test="${ind.key eq newMap.key}">
                <c:choose>
                  <c:when test="${newMap.value.informativa eq ind.value.informativa}">
                    &ndash; - 
                  </c:when>
                  <c:otherwise>
                    <c:out value="${pat.id} : ${newMap.key}" /> = <c:out value="${newMap.value.informativa}" />
                  </c:otherwise>
                </c:choose>
                </c:when>
              </c:choose>
            </c:forEach>

          </c:forEach>

        </c:if>
      </c:forEach>
    </tr>
    </c:forEach>
  </c:forEach>
  --%>
    
      </tbody>
    </table>
    <blockquote> Log delle modifiche </blockquote>
  </body>
</html>   
