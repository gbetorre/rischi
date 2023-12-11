<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <form accept-charset="ISO-8859-1" id="adr-form" class="panel subfields errorPwd" action="" method="post">
      <div class="panel-heading bgAct24">
        <div class="noHeader">
          <i class="fa-solid fa-file-circle-plus"></i>
          <c:out value="Inserimento rischio corruttivo" escapeXml="false" />
        </div>
      </div>
      <hr class="separatore" />
      <div class="panel-body">
        <br />
        <div class="row">
          <div class="col-sm-5"><strong>Nome rischio</strong><sup>*</sup></div>
          <div class="col-sm-6">
            <input type="text" class="form-control" id="r-name" name="r-name" placeholder="Inserisci nome nuovo rischio corruttivo">
            <div class="charNum"></div> 
          </div>
        </div>
        <br />
        <div class="row">
          <div class="col-sm-5">Descrizione rischio</div>
          <div class="col-sm-6">
            <textarea class="form-control" id="r-descr" name="r-descr" placeholder="Inserisci una descrizione del nuovo rischio">${wbsDescr}</textarea>
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
    <script type="text/javascript">
      $(document).ready(function () {
        $('#adr-form').validate ({
          rules: {
            'r-name': {
              required: true
            },
          }, 
          messages: {
            'r-name': "Inserire il nome del rischio",
          },
          submitHandler: function (form) {
            return true;
          }
        });
      
        $('#r-descr').keyup(function (e) {
          var chars = $(this).val().length;
          $(this).next('div').text(chars + ' caratteri inseriti');
        });
      });
    </script> 
