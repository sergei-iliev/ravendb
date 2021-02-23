<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html>
<head>
<title>Users</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">

</head>
<script src="/js/jquery/jquery.min.js"></script> 

<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
<link rel="stylesheet" href="/css/bootstrap/bootstrap.min.css">         
<script src="/js/bootstrap/bootstrap.min.js"></script> 
 
<body>
	<jsp:include page="/jsp/fragments/header.jsp">
		<jsp:param name="activeMenuItem" value="users" />
	</jsp:include>
	<div class="container" id="contentid">
	<div class="card card-body bg-light">
		<div class="row">
			<div class="col-md-12">
					<c:if test="${success!=null}">
						<div class="alert alert-success" role="alert">${success}</div>
					</c:if>
					<form class="form-horizontal" id="form" method="post"
						action="/administration/users/guid/exists">
						<fieldset>

							<div class="form-label-group mt-3">
								<div class="col-md-6">
									<label for="userGuidId">User guids</label> <textarea name="userGuids"
										class="form-control" id="userGuidId" rows="10"></textarea>
								</div>
							</div>						
							<div class="form-group">
								<div class="col-md-12 text-center">
									<button type="submit" class="btn btn-primary btn-lg mt-5" >Check</button>
								</div>
							</div>
						</fieldset>
					</form>
				</div>
			</div>
		</div>

		<c:if test="${error!=null}">
		   <div class="alert alert-danger mt-5" role="alert">${error}</div>
		</c:if>	
		<c:if test="${message!=null}">
		   <div class="alert alert-success mt-5" role="alert">${message}</div>
		</c:if>	
	
	</div></body>
</html>
