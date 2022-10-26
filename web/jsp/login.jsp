<%@ page contentType="text/html;" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:if test="${error}">
  <div class="alert alert-danger alert-dismissible fade show" role="alert">
    <button type="button" class="close" data-dismiss="alert" aria-label="Close">
      <span aria-hidden="true">&times;</span>
    </button>
    <strong>ATTENZIONE: </strong>${msg}
  </div>
</c:if>
<c:catch var="exception">
  <form action="${initParam.appName}/auth" method="post">
    <br />
    <div class="row justify-content-center">
      <div class="col-3 text-center">
        <img class="logo" src="${initParam.urlDirectoryImmagini}/logo1.png" />
      </div>
    </div>
    <br />
    <div class="row justify-content-center">
      <div class="col text-center">
        <h1>Mappatura dei rischi corruttivi</h1>
        <hr class="separatore" />
        <br />
        <div class="container justify-content-center">
          <br /><br />
          <div class="row justify-content-center">
            <div class="col-11">
              <div class="input-group">
                <input id="usr" type="text" class="form-control" name="usr" placeholder="Username">
              </div>
              <br />
              <div class="input-group">
                <input id="pwd" type="password" class="form-control" name="pwd" placeholder="Password">
              </div>
            </div>
          </div>
          <br />
          <div class="row justify-content-center">
            <div class="col text-center">
              <input type="submit" class="btn btn-primary" value="LOGIN">
            </div>
          </div>
        </div>
      </div>
    </div>
  </form>
  <!-- Nome e versione del software -->
  <div aria-live="polite" aria-atomic="true" class="d-flex justify-content-center align-items-center" style="min-height: 200px;">
    <div class="toast" role="alert" aria-live="assertive" aria-atomic="true" data-delay="5000" id="about">
      <div class="toast-header bgAddAct">
        <%-- <img src="..." class="rounded mr-2" alt="...">--%>
        <strong class="mr-auto">ROL [Rischi On Line]</strong>
        <small><span id="countdown">5</span></small>
        <button type="button" class="ml-2 mb-1 close" data-dismiss="toast" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="toast-body">
        Versione: 1.18
      </div>
    </div>
  </div>
  <script>
    $(document).ready(function(){
        $('#about').toast('show');
    });
  </script>
  <script type="text/javascript">
    var ss = 6;
    function justcountdown() {
      ss = ss-1;
        document.getElementById("countdown").innerHTML = ss;
        window.setTimeout("justcountdown()", 1000);
    }
    // Avvia il countdown
    justcountdown();
  </script> 
</c:catch>
  <p style="color:red;"><c:out value="${exception}" /></p>
