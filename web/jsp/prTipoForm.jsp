<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="URL.jspf" %>
    <form accept-charset="ISO-8859-1" id="proctype_form" class="panel form-horizontal" action="" method="post">
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
              <input type="radio" name="liv" value="1" required>
              Macroprocesso
            </label><br>
            <label>
              <input type="radio" name="liv" value="2">
              Processo
            </label><br>
            <label>
              <input type="radio" name="liv" value="3">
              Sottoprocesso
            </label>
          </div>
          <hr class="separatore" />
          <button type="submit" class="btn btn-info" value="Save">
            <i class="far fa-save"></i>&nbsp;
            Continua
          </button>
        </div><br>
      </div>
    </form>
    
    
