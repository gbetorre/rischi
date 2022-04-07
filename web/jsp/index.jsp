<%@ page contentType="text/html;" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<title>Redirect page</title>
</head>
<body>
    <script type="text/javascript">
          window.location="${initParam.appName}".replace("&amp;", "&");
    </script> 
</body>
</html>