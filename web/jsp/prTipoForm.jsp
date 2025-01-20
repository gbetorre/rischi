<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ include file="URL.jspf" %>
<c:set var="structs" value="${requestScope.strutture}" scope="page" />
<c:set var="structsByCode" value="${requestScope.elencoStrutture}" scope="page" />
<c:set var="dir" value="${structs.get(zero)}" />
<c:set var="cen" value="${structs.get(zero+1)}" />
<c:set var="dip" value="${structs.get(zero+2)}" />
    <form accept-charset="ISO-8859-1" id="proctype_form" class="panel  form-horizontal" action="" method="post">
    <div class="form-custom form-group">
        <hr class="separapoco" />
        <div class="noHeader bgAct">
          <i class="fa-solid fa-gear"></i>
          Scelta livello nuovo processo
        </div>
      <hr class="riga">

        <div class="panel-body form-group">
          <div class="radio-group">
              <label>
                  <input type="radio" name="option" value="Option 1" required>
                  Macroprocesso
              </label><br>
              <label>
                  <input type="radio" name="option" value="Option 2">
                  Processo
              </label><br>
              <label>
                  <input type="radio" name="option" value="Option 3">
                  Sottoprocesso
              </label>
          </div>
      <hr class="separatore" />

          <button type="submit" class="btn btn-info" value="Save">
            <i class="far fa-save"></i>
            Continua
          </button>
        </div>
        <br />
          
        </div>

    </form>
    
    
