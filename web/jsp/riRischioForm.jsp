<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>


      <form id="adr-form" action="" method="post" class="panel subfields errorPwd">
        <input type="hidden" id="wbs-id" name="wbs-id" value="${wbsId}" />
        <div class="panel-heading bgAct24">
          <div class="noHeader"><em><c:out value="Nuovo rischio corruttivo" escapeXml="false" /></em></div>
        </div>
        <hr class="separatore" />
        <div class="panel-body">
          <br />
          <div class="row">
            <div class="col-sm-5"><strong>Nome rischio</strong><sup>*</sup></div>
            <div class="col-sm-6">
              <input type="text" class="form-control" id="wbs-name" name="wbs-name" value="${wbsNome}" placeholder="Inserisci nome nuovo rischio corruttivo">
              <div class="charNum"></div> 
            </div>
          </div>
          <br />
          <div class="row">
            <div class="col-sm-5">Descrizione rischio</div>
            <div class="col-sm-6">
              <textarea class="form-control" name="wbs-descr" placeholder="Inserisci una descrizione del nuovo rischio">${wbsDescr}</textarea>
              <div class="charNum"></div>
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
