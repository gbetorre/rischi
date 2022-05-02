<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="URL.jspf" %>
<c:set var="structs" value="${requestScope.strutture}" scope="page" />
<c:set var="structsByCode" value="${requestScope.elencoStrutture}" scope="page" />
<c:set var="dir" value="${structs.get(zero)}" />
<c:set var="cen" value="${structs.get(zero+1)}" />
<c:set var="dip" value="${structs.get(zero+2)}" />
    <form id="select_ent_form" action="${initParam.appName}/" method="post">
      <h4 class="btn-lightgray">Scelta struttura intervistata</h4>
      <div class="form-custom" id="status_form">
        <div class="row">
          <select id="liv1">
            <option value="0">-- scelta tipologia struttura -- </option>
            <option value="${dir.extraInfo.codice}"><c:out value="${dir.nome}" /></option>
            <option value="${cen.extraInfo.codice}"><c:out value="${cen.nome}" /></option>
            <option value="${dip.extraInfo.codice}"><c:out value="${dip.nome}" /></option>
          </select>
          &nbsp;
        </div>
        <div>
          <select id="liv2">
            <option value="">-- struttura II livello -- </option>
          </select>
          &nbsp;
          <select id="liv3">
            <option value="">-- struttura III livello -- </option>
          </select>
          &nbsp;
          <select id="liv4">
            <option value="">-- struttura IV livello -- </option>
          </select>
        </div>
        <br />
      </div>  
    </form>
    <script>
    function blank() {
        return "<option value=''>-- scegli una struttura --</option>";
    }
    
    function fill(structs) {
        for (var i=0; i<structs.length; i++) {
            alert("<option value='" + structs[i].id +"'>" + structs[i].nome + "</option>");
        }
            
        return "<option value=''>-- scegli una --</option>";
     }
</script>
<%--
<c:forEach var="entry" items="${structsByCode}">
  <div class="substatus">
    <h4>Dipartimento di ${entry.key}</h4>
    <c:forEach var="dip" items="${entry.value}" varStatus="loop">
    ${dip.nome}
    </c:forEach>
   </div>
</c:forEach>

<c:forEach var="entry" items="${structsByCode}">
  <div class="substatus">
    <h4>codice ${entry.key}</h4>
    <c:forEach var="prj" items="${entry.value}" varStatus="loop">
    ${prj.extraInfo.codice}=${prj.nome}<br />
    </c:forEach>
   </div>
</c:forEach>
  --%>
<c:set var="singleQuote" value="'" scope="page" />
<c:set var="singleQuoteEsc" value="''" scope="page" />
<c:set var="doubleQuote" value='"' scope="page" />
<c:set var="doubleQuoteEsc" value='\\"' scope="page" />
<%--*****
         <c:forEach var="l1" items="${structs}">
          <c:forEach var="l2" items="${l1.figlie}">
          case "${l2.extraInfo.codice}": 
              <c:forEach var="dept" items="${structsByCode.get(l2.extraInfo.codice)}">
                <c:forEach var="l3" items="${dept.figlie}">--<c:out value="${l3.nome }" escapeXml="false" />--
                  <c:set var="l3nome" value="${fn:replace(l3.nome, singleQuote, singleQuoteEsc)}" scope="page" />
                    option value='${l3.extraInfo.codice}' ${fn:replace(l3nome, doubleQuote, doubleQuoteEsc)}
                </c:forEach>
              </c:forEach>
              break;
          </c:forEach>
        </c:forEach>
        ***** --%>
<script>
$(document).ready(function() {

    $("#liv1").change(function() {
        
        const struct1 = {
                id: 10,
                nome: 'Direzione Didattica'
              };
        const struct2 = {
                id: 20,
                nome: 'Direzione Scientifica'
              };
        const struct3 = {
                id: 30,
                nome: 'Direzione Patetica'
              };

        let l2 = new Array(struct1,struct2,struct3);
        var innerHTML_L2 = "";
        //fill(l2);
        var parent = $(this).val();
        var child2 = "#liv2";
        var child3 = "#liv3";
        var child4 = "#liv4";
        $(child2).html(blank());
        $(child3).html(blank());
        $(child4).html(blank());
        switch (parent) {
        <c:forEach var="l1" items="${structs}">
        case "${l1.extraInfo.codice}":
            $(child2).html("<c:forEach var="l2" items="${l1.figlie}"><c:set var="l2nome" value="${fn:replace(l2.nome, singleQuote, singleQuoteEsc)}" scope="page" /><option value='${l2.extraInfo.codice}'>${fn:replace(l2nome, doubleQuote, doubleQuoteEsc)}</option></c:forEach>");
            $(child3).html("<c:forEach var="l2" items="${l1.figlie}" begin="0" end="0"><c:forEach var="l3" items="${l2.figlie}"><c:set var="l3nome" value="${fn:replace(l3.nome, singleQuote, singleQuoteEsc)}" scope="page" /><option value='${l3.extraInfo.codice}'>${fn:replace(l3nome, doubleQuote, doubleQuoteEsc)}</option></c:forEach></c:forEach>");
            $(child4).html("<c:forEach var="l2" items="${l1.figlie}" begin="0" end="0"><c:forEach var="l3" items="${l2.figlie}" begin="0" end="0"><c:forEach var="l4" items="${l3.figlie}"><c:set var="l4nome" value="${fn:replace(l4.nome, singleQuote, singleQuoteEsc)}" scope="page" /><option value='${l4.extraInfo.codice}'>${fn:replace(l4nome, doubleQuote, doubleQuoteEsc)}</option></c:forEach></c:forEach></c:forEach>");
            break;
        </c:forEach>
        }
    });
    
    $("#liv2").change(function() {
        var parent = $(this).val();
        var child3 = "#liv3";
        var child4 = "#liv4";
        switch (parent) {
        <c:forEach var="entry" items="${structsByCode}">
          <c:if test="${fn:endsWith(entry.key, '-2')}">
          case "${entry.key}": 
              $(child3).html("<c:forEach var="str" items="${entry.value}"><c:set var="l3nome" value="${fn:replace(str.nome, singleQuote, singleQuoteEsc)}" scope="page" /><option value='${str.extraInfo.codice}'>${fn:replace(l3nome, doubleQuote, doubleQuoteEsc)}</option></c:forEach>");
              break;
          </c:if>
        </c:forEach>
        }
    });
    
    $("#liv3").change(function() {
        var parent = $(this).val();
        var child4 = "#liv4";
        switch (parent) {
        <c:forEach var="l1" items="${structs}">
          <c:forEach var="l2" items="${l1.figlie}">
            <c:forEach var="l3" items="${l2.figlie}">
          case "${l3.extraInfo.codice}": 
              $(child4).html("<c:forEach var="dept" items="${structsByCode.get(l3.extraInfo.codice)}"><c:forEach var="l4" items="${l3.figlie}"><c:set var="l4nome" value="${fn:replace(l4.nome, singleQuote, singleQuoteEsc)}" scope="page" /><option value='${l4.extraInfo.codice}'>${fn:replace(l4nome, doubleQuote, doubleQuoteEsc)}</option></c:forEach></c:forEach>");
              break;
            </c:forEach>
          </c:forEach>
        </c:forEach>
        }
    });

    
    
});
</script>