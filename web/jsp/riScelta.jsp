<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="URL.jspf" %>
<c:set var="structs" value="${requestScope.strutture}" scope="page" />
<c:set var="dir" value="${structs.get(zero)}" />
<c:set var="cen" value="${structs.get(zero+1)}" />
<c:set var="dip" value="${structs.get(zero+2)}" />
<form class="form-custom" id="select_ent_form" action="${initParam.appName}/" method="post">
  <h4 class="btn-lightgray">Scelta struttura intervistata</h4>
  <div class="form-custom" id="status_form">
    <div>
      <select id="liv1">
        <option value="0">-- scelta struttura -- </option>
        <option value="${dir.id}"><c:out value="${dir.nome}" /></option>
        <option value="${cen.id}"><c:out value="${cen.nome}" /></option>
        <option value="${dip.id}"><c:out value="${dip.nome}" /></option>
      </select>
      &nbsp;
      <select id="liv2">
        <option value="">-- select one -- </option>
      </select>
      &nbsp;
      <select id="liv3">
        <option value="">-- select one -- </option>
      </select>
      &nbsp;
      <select id="liv4">
        <option value="">-- select one -- </option>
      </select>
    </div>
    <br />
  </div>  
</form>
<script>
    function blank() {
        return "<option value=''>-- scegli una --</option>";
    }
    
    function fill(structs) {
        for (var i=0; i<structs.length; i++) {
            alert("<option value='" + structs[i].id +"'>" + structs[i].nome + "</option>");
        }
            
        return "<option value=''>-- scegli una --</option>";
     }
</script>
<c:forEach var="l1" items="${structs}">
         ===
            <c:forEach var="l2" items="${l1.figlie}">***
            <input type="text" value='${l2.id}'>${l2.nome}
            </c:forEach>

        </c:forEach>
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
        case "${l1.id}": 
            <c:forEach var="l2" items="${l1.figlie}">
            innerHTML_L2 = innerHTML_L2 + "<option value='${l2.id}'>${l2.nome}</option>";
            </c:forEach>
            $(child2).html(innerHTML_L2);
            $(child3).html("<option value='are1'>Area 1</option><option value='are2'>Area 2</option>");
            $(child4).html("<option value='uo1'>UO 1</option><option value='uo2'>UO 2</option>");
            break;
        </c:forEach>
          case "nove": 
              $(child2).html("<option value='dir1'>Direzione 1</option><option value='dir2'>Direzione 2</option>");
              $(child3).html("<option value='are1'>Area 1</option><option value='are2'>Area 2</option>");
              $(child4).html("<option value='uo1'>UO 1</option><option value='uo2'>UO 2</option>");
              break;
          case "dip":  
              $(child2).html("<option value='dip1'>Dipartimento 1</option><option value='dip2'>Dipartimento 2</option>");
              $(child3).html("<option value='seg1'>Segreteria 1</option><option value='seg2'>Segreteria 2</option>");
              break;
          case "bib":
              $(child2).html("<option value='bib1'>BiblioCR 1</option><option value='bib2'>BiblioCR 2</option>");
              break;
        }
    });
    
    $("#liv2").change(function() {
        var parent = $(this).val();
        var child3 = "#liv3";
        switch (parent) {
          case "dir1": 
              $(child3).html("<option value='are1'>Area 1</option><option value='are2'>Area 2</option>");
              break;
          case "dir2":  
              $(child3).html("<option value='are3'>Area 3</option><option value='are4'>Area 4</option>");
              break;
          case "dip1":
              $(child3).html("<option value='seg1'>Segreteria 1</option><option value='seg2'>Segreteria 2</option>");
              break;
        }
    });
    
    $("#liv3").change(function() {
        var parent = $(this).val();
        var child4 = "#liv4";
        switch (parent) {
          case "are1": 
              $(child4).html("<option value='uo1'>UO 1</option><option value='uo2'>UO 2</option>");
              break;
          case "are2":  
              $(child4).html("<option value='uo3'>UO 3</option><option value='uo4'>UO 4</option>");
              break;
          case "seg1":
              $(child4).html("<option value='sez1'>Sezione 1</option><option value='sez2'>Sezione 2</option>");
              break;
        }
    });

    
    
});
</script>