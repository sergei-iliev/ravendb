<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html>
<html>
<head>
<title>Administration</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">

</head>

<script src="/js/jquery/jquery.min.js"></script>
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
		
<link rel="stylesheet" href="/css/bootstrap/bootstrap.min.css">

<script src="/js/bootstrap/popper.min.js"></script>
<script src="/js/bootstrap/bootstrap.min.js"></script>


<script src="/js/backbone/underscore.min.js"></script>
<script src="/js/backbone/backbone.min.js"></script>
<script src="/js/jsp/payment_eligible_users.js"></script>

<body>
	<jsp:include page="/jsp/fragments/header.jsp">
		<jsp:param name="activeMenuItem" value="payments" />
	</jsp:include>
	<div class="container">
	<div class="card card-body bg-light">
		  <ul class="nav nav-tabs mt-2">
    		<li class="nav-item"><a class="nav-link active" data-toggle="tab" href="#home">General Search</a></li>
    		<li class="nav-item"><a class="nav-link" data-toggle="tab" href="#menu1">Users by email address Search</a></li>
    		<li class="nav-item"><a class="nav-link" data-toggle="tab" href="#menu2">Users by guid Search</a></li>
  			</ul>

  			<div class="tab-content mt-4">
    		<div id="home" class="tab-pane fade active show">
      			<h3>HOME</h3>
      			<div class="row">
      				<div class="col-md-12">
      				<form class="form-horizontal" id="search-form" method="post"
						action="/administration/payment/eligibleusers/search">
						<fieldset>
						
						</fieldset>
					</form>	
      				</div>
      			</div>
    		</div>
    		<div id="menu1" class="tab-pane fade">
      			<h3>Menu 1</h3>
      			<p>Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.</p>
    		</div>
    		<div id="menu2" class="tab-pane fade">
      			<h3>Menu 2</h3>
      			<p>Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam.</p>
    		</div>
  			</div>
	</div>
	</div>	
</body>
</html>