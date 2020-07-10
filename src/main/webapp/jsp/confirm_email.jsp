<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html>
<head>
<title>Administration</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">

</head>
<script src="/js/jquery/jquery.min.js"></script> 

<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
<link rel="stylesheet" href="/css/bootstrap/bootstrap.min.css">         
<script src="/js/bootstrap/bootstrap.min.js"></script> 
 
<body>
	<jsp:include page="/jsp/fragments/header.jsp">
		<jsp:param name="activeMenuItem" value="payments" />
	</jsp:include>
	<div class="container" id="contentid">
	<div class="card card-body bg-light">
      			<div class="row">
      				<div class="col-md-12">
      				<form class="form-horizontal" id="search-general-form" method="post"
						action="/administration/payment/confirmemail">
					<c:if test="${error!=null}">
						<div class="alert alert-danger" role="alert">${error}</div>
					</c:if>
						<fieldset>
							<div class="form-label-group mt-3">
								<div class="col-md-3">
									<label for="emailId">Please only enter email addresses that we received emails from</label> <input name="email"
										class="form-control" id="emailId"
										value="${email}">
								</div>
							</div>
					

							
							<div class="form-group">
								<div class="col-md-12 text-center">
									<button type="submit" class="btn btn-primary btn-lg mt-5" >Apply</button>
								</div>
							</div>												
					
					</fieldset>
					</form>	
      				</div>
      			</div>
	</div>
		<c:if test="${message!=null}">
		   <div class="alert alert-success" role="alert">${message}</div>
		</c:if>
		
	</div>
	
</body>
</html>