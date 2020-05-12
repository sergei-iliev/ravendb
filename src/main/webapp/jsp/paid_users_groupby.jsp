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
<script src="/js/jsp/paid_users_groupby.js"></script>
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
						action="/administration/payment/paidusers/search/groupby">
					<c:if test="${success!=null}">
						<div class="alert alert-success" role="alert">${success}</div>
					</c:if>
						<fieldset>

							<div class="form-label-group mt-3">
								<div class="col-md-3">
									<label for="typesId">Payment type</label> <input name="types"
										class="form-control" id="typesId"
										placeholder="${webform.typesAsText}">
								</div>
							</div>
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
								<div class="col-3">
									<div class="card card-body">
										<div class="row">

											<div class="col-md-12">
												<label for="countryId">Country</label> <select
													name="country" class="form-control" id="countryId">
													<c:forEach var="item" items="${countries}">
														<option ${item}>${item}</option>
													</c:forEach>
												</select>
											</div>
										</div>
										<div class="row mt-3">
											<div class="col-md-2">
												<button class="btn" id="add-btn">
													<i class="fa fa-arrow-down"></i>
												</button>
												<button class="btn" id="remove-btn">
													<i class="fa fa-arrow-up"></i>
												</button>
											</div>
										</div>
										<div class="row mt-3">
											<div class="col-md-12">
												<select class="form-control" name="countries"
													id="countriesId" multiple>
													<c:forEach var="item" items="${webform.countryCodes}">
														<option ${item}>${item}</option>
													</c:forEach>
												</select>
											</div>
										</div>
									</div>
								</div>
							</div>
							<div class="form-label-group mt-3">
								<div class="col-md-3">
									<label for="packageNamesId">Package Name</label> <input
										name="packageNames" class="form-control" id="packageNamesId"
										value="${webform.packageNamesAsText}">
								</div>
							</div>
							<div class="form-label-group mt-3">
							<div class="col-md-3">
							    <label for="amount-row">Amount</label>
 								<div class="form-row" id="amount-row">
    								<div class="col">
      									<input type="text" name="amountFrom" class="form-control" value="${webform.amountFrom}">
    								</div>
    								<div class="col">
      									<input type="text" name="amountTo" class="form-control" value="${webform.amountTo}">
    								</div>
  								</div>
  							</div>
							</div>						
							<div class="form-label-group mt-3">
							<div class="col-md-3">
							<label>Group By Time</label> 
							<div class="form-check">
								<input class="form-check-input timegroup" type="checkbox"
									name="groupByTime" id="groupByTime1" value="day"
									<c:if test="${(webform.groupByTime != null)&&(webform.groupByTime eq 'day')}">
										checked
									</c:if>										
									> 
									<label class="form-check-label"
									for="groupByTime1"> Day </label>
							</div>
							<div class="form-check">
								<input class="form-check-input timegroup" type="checkbox"
									name="groupByTime" id="groupByTime2" value="month" 
									<c:if test="${(webform.groupByTime != null)&&(webform.groupByTime eq 'month')}">
										checked
									</c:if>										
									>
								<label class="form-check-label" for="groupByTime2"> Month </label>
							</div>
							<div class="form-check disabled">
								<input class="form-check-input timegroup" type="checkbox"
									name="groupByTime" id="groupByTime3" value="year"
									<c:if test="${(webform.groupByTime != null)&&(webform.groupByTime eq 'year')}">
										checked
									</c:if>	
									> 
									<label class="form-check-label"	 for="groupByTime3"> Year </label>
							</div>
							</div>
							</div>
							<div class="form-label-group mt-3">
							<div class="col-md-3">
							<label>Group by locale</label> 
							<div class="form-check">
								<input class="form-check-input localegroup" type="checkbox"
									name="groupByLocale" id="groupByLocale1" value="country"
									<c:if test="${(webform.groupByLocale != null)&&(webform.groupByLocale eq 'country')}">
										checked
									</c:if>	
									> 
									<label class="form-check-label"
									for="groupByLocale1"> Country </label>
							</div>
							<div class="form-check">
								<input class="form-check-input localegroup" type="checkbox"
									name="groupByLocale" id="groupByLocale2" value="currency"
									<c:if test="${(webform.groupByLocale != null)&&(webform.groupByLocale eq 'currency')}">
										checked
									</c:if>		
									>
								<label class="form-check-label" for="groupByLocale2"> Currency </label>
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
		        <c:choose>
				 <c:when test="${webform.groupByType eq 'ALL'}">
				 <table class="table">
					<thead>
						<tr>							
							<th scope="col">Group By Time</th>
							<th scope="col">Group By Locale</th>							
							<th scope="col">Amount</th>							
						</tr>
					</thead>
					<tbody>
						<c:forEach var="entity" items="${entities}">
							<tr>							 			
								<td>${entity.groupByTimeValue}</td>																			
								<td>${entity.groupByLocaleValue}</td>
								<td>${entity.amount}</td>								
							</tr>
						</c:forEach>
					</tbody>
				</table>				 
				 </c:when>
				 <c:otherwise>
				 <table class="table">
					<thead>
						<tr>							
							<th scope="col">GroupBy</th>							
							<th scope="col">Amount</th>							
						</tr>
					</thead>
					<tbody>
						<c:forEach var="entity" items="${entities}">
							<tr>							 															
								<td>${webform.groupByType eq 'TIME' ? entity.groupByTimeValue :entity.groupByLocaleValue}</td>
								<td>${entity.amount}</td>								
							</tr>
						</c:forEach>
					</tbody>
				</table>
				</c:otherwise>
				</c:choose>
						
		</div>
		</c:if>	
	</div>
		<script type="text/javascript">
			'use strict';

			new payment.PaidUsersGroupByView();
		</script>	
</body>
</html>