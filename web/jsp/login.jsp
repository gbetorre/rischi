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
<%@ include file="about.jspf"%>
</c:catch>
  <p style="color:red;"><c:out value="${exception}" /></p>
