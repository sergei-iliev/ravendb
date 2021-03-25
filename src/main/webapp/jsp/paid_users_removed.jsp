<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
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

</head>
<body>
	<jsp:include page="/jsp/fragments/header.jsp">
		<jsp:param name="activeMenuItem" value="payments" />
	</jsp:include>
	<div class="container" id="contentid">
	<div class="card card-body bg-light">
      			<div class="row">
      				<div class="col-md-12">
      				<form class="form-horizontal" id="search-general-form" method="post"
						action="/administration/payment/paidusers/search/removed">
					<c:if test="${success!=null}">
						<div class="alert alert-success" role="alert">${success}</div>
					</c:if>
 					<h4>Removed Redeeming Requests</h4>					
						<fieldset>
							<div class="form-label-group">
								<div class="col-3">
									<label for="startDateId">Start Date</label> <input
										class="form-control" type="date" data-date-format="MM/DD/YYYY"
										name="startDate" value="${webform.startDateAsText}"
										id="startDateId">
								</div>
							</div>
							<div class="form-label-group">
								<div class="col-3">
									<label for="endDateId">End Date</label> <input
										class="form-control" type="date" data-date-format="MM/DD/YYYY"
										name="endDate" value="${webform.endDateAsText}" id="endDateId">
								</div>
							</div>
							<div class="form-label-group mt-3">
								<div class="col-md-3">
									<label for="packageNamesId">Package Name</label> <input
										name="packageName" class="form-control" id="packageNameId"
										value="${webform.packageName}">
								</div>
							</div>
						
							<div class="form-label-group mt-3">
							<div class="col-md-3">							 
							<div class="form-check">
								<input class="form-check-input localegroup" type="checkbox" value="true"
									name="paid" id="paidId" 
									<c:if test="${(webform.paid==true)}">
										checked
									</c:if>	
									> 
									<label class="form-check-label"
									for="paidId"> Already paid </label>
							</div>
							</div>
							</div>							
							<div class="form-group">
								<div class="col-md-12 text-center">
									<button type="submit" class="btn btn-primary btn-lg mt-5" >Search</button>
								</div>
							</div>												
					
					</fieldset>
					</form>	
      				</div>
      			</div>
	</div>
		<!-- Table  -->
		<c:if test="${entities!=null}">
		<div class="row mt-5">

				 <table class="table">
					<thead>
						<tr>							
							<th scope="col">Name</th>
							<th scope="col">Guid</th>							
							<th scope="col">Date</th>
							<th scope="col">Amount</th>
							<th scope="col">Country</th>
							<th scope="col">Email</th>
							<th scope="col">Paypal</th>												
						</tr>
					</thead>
					<tbody>
						<c:forEach var="entity" items="${entities}">
							<tr>							 			
								<td>${entity.fullName}</td>																			
								<td>${entity.userGuid}</td>
								<td>${entity.date}</td>
								<td>${entity.amount}</td>
								<td>${entity.countryCode}</td>
								<td>${entity.email}</td>
								<td>${entity.paypalAccount}</td>								
							</tr>
						</c:forEach>
					</tbody>
				</table>				 
				
						
		</div>
		</c:if>	
	</div>
</body>
</html>