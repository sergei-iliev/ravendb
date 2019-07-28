
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
<title>Login</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">

</head>
<link rel="stylesheet" href="../css/bootstrap/bootstrap.min.css">         
<script src="../js/bootstrap/bootstrap.min.js"></script> 
<body>

  <div class="container">

    <div class="row">
      <div class="col-sm-9 col-md-7 col-lg-5 mx-auto">
        <div class="card card-signin my-5">
          <div class="card-body">
            <h5 class="card-title text-center">Sign In</h5>
            <form class="form-signin" action="/administration/login" method="POST">
			
			<c:if test="${error!=null}">
				<span class="error text-danger">${error}</span>	
			</c:if>                
              <div class="form-label-group">
                <label for="inputEmail">Email address</label>
                <input type="email" id="inputEmail" name="email" class="form-control" placeholder="Email address" required autofocus>                
              </div>

              <div class="form-label-group">
                <label for="inputPassword">Password</label>
                <input type="password" id="inputPassword" name="password" class="form-control" placeholder="Password" required>                
              </div>
              <button class="btn btn-lg btn-primary btn-block text-uppercase mt-5" type="submit">Sign in</button>
             </form>
          </div>
        </div>
      </div>
    </div>
  </div>

</body>
</html>