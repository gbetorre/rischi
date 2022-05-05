<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="URL.jspf" %>
<c:set var="structs" value="${requestScope.strutture}" scope="page" />
<c:set var="structsByCode" value="${requestScope.elencoStrutture}" scope="page" />
<c:set var="dir" value="${structs.get(zero)}" />
<c:set var="cen" value="${structs.get(zero+1)}" />
<c:set var="dip" value="${structs.get(zero+2)}" />
    <form id="select_str_form" class="form-horizontal" action="" method="post">
      <h4 class="btn-lightgray">Scelta struttura intervistata</h4>
      <div class="form-custom form-group" id="str_form">
        <div class="panel-body form-group">
          <!--  1° Livello -->
          <div class="row alert">
            <div class="col-sm-3  alert-info">
              Scelta tipologia struttura&nbsp;
            </div>
            <div class="col-sm-9">
              <select id="str-liv1" name="sliv1">
                <option value="0">-- scelta tipologia struttura -- </option>
                <option value="${dir.extraInfo.codice}"><c:out value="${dir.nome}" /></option>
                <option value="${cen.extraInfo.codice}"><c:out value="${cen.nome}" /></option>
                <option value="${dip.extraInfo.codice}"><c:out value="${dip.nome}" /></option>
              </select>
            </div>
            &nbsp;
          </div>
          <!--  2° Livello -->
          <div class="row alert">
            <div class="col-sm-3  alert-info">
              Scelta struttura II livello&nbsp;
            </div>
            <div class="col-sm-9">
              <select id="str-liv2" name="sliv2">
                <option value="">-- struttura II livello -- </option>
              </select>
            </div>
            &nbsp;
          </div>
          <!--  3° Livello -->
          <div class="row alert">
            <div class="col-sm-3  alert-info">
              Scelta struttura III livello&nbsp;
            </div>
            <div class="col-sm-9">
              <select id="str-liv3" name="sliv3">
                <option value="">-- struttura III livello -- </option>
              </select>
            </div>
            &nbsp;
          </div>
          <!--  4° Livello -->
          <div class="row alert">
            <div class="col-sm-3  alert-info">
              Scelta struttura IV livello&nbsp;
            </div>
            <div class="col-sm-9">
              <select id="str-liv4" name="sliv4">
                <option value="">-- struttura IV livello -- </option>
              </select>
            </div>
          </div>
          <br />
          &nbsp;
          <button type="submit" class="btn btn-info" value="Save">
            <i class="far fa-save"></i>
            Continua
          </button>
        </div>
        <br />
      </div>
    </form>
    <script>
    function defaultVal() {
        return "<option value=''>-- scegli una struttura --</option>";
    }
    
    function blank() {
        return "<option value=''>-- Nessuna --</option>";
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

    $("#str-liv1").change(function() {
        
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
        var child2 = "#str-liv2";
        var child3 = "#str-liv3";
        var child4 = "#str-liv4";
        $(child2).html(blank());
        $(child3).html(blank());
        $(child4).html(blank());
        switch (parent) {
        <c:forEach var="l1" items="${structs}">
        case "${l1.extraInfo.codice}":
            $(child2).html("<c:forEach var="l2" items="${l1.figlie}"><c:set var="l2nome" value="${fn:replace(l2.nome, singleQuote, singleQuoteEsc)}" scope="page" /><option value='${l2.extraInfo.codice}'>${l2.prefisso} ${fn:replace(l2nome, doubleQuote, doubleQuoteEsc)}</option></c:forEach>");
            $(child3).html("<c:forEach var="l2" items="${l1.figlie}" begin="0" end="0"><c:forEach var="l3" items="${l2.figlie}"><c:set var="l3nome" value="${fn:replace(l3.nome, singleQuote, singleQuoteEsc)}" scope="page" /><option value='${l3.extraInfo.codice}'>${l3.prefisso} ${fn:replace(l3nome, doubleQuote, doubleQuoteEsc)}</option></c:forEach></c:forEach>");
            $(child4).html("<c:forEach var="l2" items="${l1.figlie}" begin="0" end="0"><c:forEach var="l3" items="${l2.figlie}" begin="0" end="0"><c:forEach var="l4" items="${l3.figlie}"><c:set var="l4nome" value="${fn:replace(l4.nome, singleQuote, singleQuoteEsc)}" scope="page" /><option value='${l4.extraInfo.codice}'>${l4.prefisso} ${fn:replace(l4nome, doubleQuote, doubleQuoteEsc)}</option></c:forEach></c:forEach></c:forEach>");
            break;
        </c:forEach>
        }
    });
    
    $("#str-liv2").change(function() {
        var parent = $(this).val();
        var child3 = "#str-liv3";
        var child4 = "#str-liv4";
        $(child3).html(blank());
        $(child4).html(blank());
        switch (parent) {
        <c:forEach var="entry" items="${structsByCode}">
        <c:choose>
          <c:when test="${fn:endsWith(entry.key, '-2') and not empty entry.value}">
          case "${entry.key}": 
              $(child3).html("<c:forEach var="str" items="${entry.value}"><c:set var="l3nome" value="${fn:replace(str.nome, singleQuote, singleQuoteEsc)}" scope="page" /><option value='${str.extraInfo.codice}'>${str.prefisso} ${fn:replace(l3nome, doubleQuote, doubleQuoteEsc)}</option></c:forEach>");
              $(child4).html("<c:forEach var="str" items="${entry.value}" begin="0" end="0"><c:forEach var="l4" items="${str.figlie}"><c:set var="l4nome" value="${fn:replace(l4.nome, singleQuote, singleQuoteEsc)}" scope="page" /><option value='${l4.extraInfo.codice}'>${l4.prefisso} ${fn:replace(l4nome, doubleQuote, doubleQuoteEsc)}</option></c:forEach></c:forEach>");
              break;
          </c:when>
          <c:when test="${fn:endsWith(entry.key, '-3') and not empty entry.value}">
          case "${entry.key}": 
              $(child4).html("<c:forEach var="l4" items="${entry.value}"><c:set var="l4nome" value="${fn:replace(l4.nome, singleQuote, singleQuoteEsc)}" scope="page" /><option value='${l4.extraInfo.codice}'>${l4.prefisso} ${fn:replace(l4nome, doubleQuote, doubleQuoteEsc)}</option></c:forEach>");
              break;
          </c:when>
          </c:choose>
        </c:forEach>
        }
    });
    
    $("#str-liv3").change(function() {
        var parent = $(this).val();
        var child4 = "#str-liv4";
        $(child4).html(blank());
        switch (parent) {
        <c:forEach var="entry" items="${structsByCode}">
        <c:choose>
          <c:when test="${fn:endsWith(entry.key, '-3') and not empty entry.value}">
          case "${entry.key}": 
              $(child4).html("<c:forEach var="l4" items="${entry.value}"><c:set var="l4nome" value="${fn:replace(l4.nome, singleQuote, singleQuoteEsc)}" scope="page" /><option value='${l4.extraInfo.codice}'>${l4.prefisso} ${fn:replace(l4nome, doubleQuote, doubleQuoteEsc)}</option></c:forEach>");
              break;
          </c:when>
        </c:choose>
        </c:forEach>
        }
    });
    
});
</script>