<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
  <c:set var="classSuffix" value="${fn:toLowerCase(requestScope.pxi.labelWeb)}" scope="page" />
  <c:if test="${fn:indexOf(classSuffix, ' ') gt -1}">
    <c:set var="classSuffix" value="${fn:substring(classSuffix, 0, fn:indexOf(classSuffix, ' '))}" scope="page" />
  </c:if>
    <form accept-charset="ISO-8859-1" id="not-form" class="panel subfields" action="" method="post">
      <div class="panel-heading bgAct1">
        <div class="noHeader">
          <i class="fa-solid fa-file-circle-plus"></i>
          <c:out value="Nota giudizio sintetico" escapeXml="false" />
        </div>
      </div>
      <hr class="separatore" />
      <div class="panel-body">
        <br />
        <div class="row">
          <div class="col-sm-5"><br />PxI (valore in cache)</div>
          <div class="col-sm-6 text-center">
            <div class="value bgcolor-${classSuffix} border-${classSuffix}">
              <p class="text-center">
                <c:out value="${requestScope.pxi.labelWeb}" />
              </p>
            </div>
          </div>
        </div>
        <br />
        <div class="row">
          <div class="col-sm-5"><br />PxI (valore attuale)</div>
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
          <div class="col-sm-5"><br />Nota</div>
          <div class="col-sm-6">
            <textarea class="form-control" rows="4" id="pi-note" name="pi-note" placeholder="Inserisci una nota al PxI">${requestScope.pxi.informativa}</textarea>
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
