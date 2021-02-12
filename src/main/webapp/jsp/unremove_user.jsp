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

<script src="/js/bootstrap/popper.min.js"></script>
<script src="/js/bootstrap/bootstrap.min.js"></script>


<script src="/js/backbone/underscore.min.js"></script>
<script src="/js/backbone/backbone.min.js"></script>
<script src="/js/jsp/paid_users.js"></script>
</head>
<body>
	<jsp:include page="/jsp/fragments/header.jsp">
		<jsp:param name="activeMenuItem" value="payments" />
	</jsp:include>
	<div class="container" id="contentid">
	<div class="card card-body bg-light">
		<div class="row">
			<div class="col-md-12">					
					<form class="form-horizontal" id="form" method="post"
						action="/administration/payment/user/unremoveuser">
						<fieldset>
							<div class="form-label-group mt-3">
								<div class="col-md-3">
									<label for="userGuidId">User GUID</label> <input name="userGuid"
										class="form-control" id="userGuidId"
										value="${webform.userGuid}">
								</div>
							</div>
							<div class="form-label-group mt-3">
							<div class="col-md-3">
								<label for="removalReasonId">Removal reason</label> <select
													name="removalReason" class="form-control" id="removalReasonId">
									<c:forEach var="item" items="${removalReasons}">
												<option value="${item}" ${item == webform.removalReason ? 'selected="selected"' : ''}>${item}</option>
									</c:forEach>
								</select>							
							</div>
							</div>							
							<div class="form-group">
								<div class="col-md-12 text-center">
									<button type="submit" class="btn btn-primary btn-lg mt-5" >Unremove</button>
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
		<script type="text/javascript">
			'use strict';

			new payment.PaidUsersView();
		</script>	
</body>
</html>