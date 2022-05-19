<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html>
<html>
<head>
<title>Administration</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<link rel="stylesheet" href="/css/main.css">
<script src="/js/jquery/jquery.min.js"></script>
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
		
<link rel="stylesheet" href="/css/bootstrap/bootstrap.min.css">
<script src="/js/bootstrap/bootstrap.min.js"></script>

</head>
<body>
	<jsp:include page="/jsp/fragments/header.jsp">
		<jsp:param name="activeMenuItem" value="users" />
	</jsp:include>
	<div class="container" id="contentid">
	<div class="card card-body bg-light">
	<h4>Wrong Package Name</h4>
		<div class="row">
			<div class="col-md-12">					
					<form class="form-horizontal" id="form" method="post"
						action="/administration/users/package/wrong/name">
						<fieldset>
							<div class="form-label-group mt-3">
								<div class="col-md-3">
									<label for="userGuid">User GUID</label> <input name="userGuid" value="${userGuid}"
										class="form-control"
										value="">
								</div>
							</div>													
							<div class="form-group">
								<div class="col-md-12 text-center">
									<button type="submit" class="btn btn-primary btn-lg mt-5" >Execute</button>
								</div>
							</div>
						</fieldset>
					</form>
				</div>
			</div>			
	</div>
	<div class="row mt-5">
	<div class="col-md-12">
			<c:if test="${success!=null}">
			  <div class="alert alert-success" role="alert">${success}</div>
			</c:if>
			<c:if test="${error!=null}">
			  <div class="alert alert-danger" role="alert">${error}</div>
			</c:if>	
	</div>				
	</div>	
	</div>	
</body>
</html>