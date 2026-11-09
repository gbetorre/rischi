<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pat" value="${requestScope.pat}" scope="page" />
    <form accept-charset="ISO-8859-1" id="nos-form" class="panel subfields" action="" method="post">
      <input type="hidden" id="pat-id" name="pliv2" value="${pat.id}" />
      <div class="panel-heading bgAct3">
        <div class="noHeader">
          <i class="fa-solid fa-file-circle-plus"></i>
          Note del processo:
          <a href="${initParam.appName}/?q=pr&p=pro&pliv=${pat.id}&liv=${pat.livello}&r=${param['r']}">
            <c:out value="${pat.nome}" escapeXml="false" />
          </a>
        </div>
      </div>
      <hr class="separatore" />
      <div class="panel-body">
        <br>
        <div class="row">
          <!-- <div class="col-sm-5"><br />Note</div> -->
          <div class="col-sm-1"></div>
          <div class="col-sm-10 text-center">
            <textarea class="form-control" rows="6" id="pi-note" name="pi-note" placeholder="Inserisci le note di processo"><c:out value="${pat.informativa}" escapeXml="false" /></textarea>
            <div class="charNum text-center"></div>
          </div>
        </div>
        <br><br>&nbsp;
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
        $('#nos-form').validate ({
          rules: {
            'pi-notes': {
              required: true
            },
          }, 
          messages: {
            'pi-notes': "Inserire le note per il processo",
          },
          submitHandler: function (form) {
            return true;
          }
        });
      
        $('#pi-notes').keyup(function (e) {
          var chars = $(this).val().length;
          $(this).next('div').text(chars + ' caratteri inseriti');
        });
      });
    </script>

