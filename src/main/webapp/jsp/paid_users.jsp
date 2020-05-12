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
		  <ul class="nav nav-tabs mt-2" id="filterTabId">
    		<li class="nav-item"><a class="nav-link ${webform.activeTab==1? 'active' : ''}" data-toggle="tab" href="#search">General Search</a></li>
    		<li class="nav-item"><a class="nav-link ${webform.activeTab==2? 'active' : ''}" data-toggle="tab" href="#searchByEmail">Users by email address Search</a></li>
    		<li class="nav-item"><a class="nav-link ${webform.activeTab==3? 'active' : ''}" data-toggle="tab" href="#searchByGuid">Users by guid Search</a></li>
  			</ul>

  			<div class="tab-content mt-4">
    		<div id="search" class="tab-pane fade ${webform.activeTab==1? 'active show' : ''}">
      			<div class="row">
      				<div class="col-md-12">
      				<form class="form-horizontal" id="search-general-form" method="post"
						action="/administration/payment/paidusers/search/general">
					<c:if test="${success!=null}">
						<div class="alert alert-success" role="alert">${success}</div>
					</c:if>
						<fieldset>

							<div class="form-label-group mt-3">
								<div class="col-md-3">
									<label for="typesId">Payment type</label> <input name="types"
										class="form-control" id="typesId"
										value="${webform.typesAsText}">
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
						<!-- https://codepen.io/lehonti/pen/OzoXVa  -->
						<!-- 	
							 <div class="form-label-group mt-3">
                        		<div class="col-md-3">
						
								<div class="list-group">
  									<input type="checkbox" name="types" value="PayPal" checked/>
  									<label class="list-group-item" for="CheckBox1">PayPal</label>
  
  									<input type="checkbox" name="types" value="Amazon" />
  									<label class="list-group-item" for="CheckBox2">Amazon</label>
  
								</div>
								</div>
							</div>
																	
							 <div class="form-label-group mt-3">
                        		<div class="col-md-3">
                           			<label for="typesId">Type</label>                                
                            		<input name="types" class="form-control" id="typesId" value="${webform.typesAsText}" >      						      					    	
                        		</div>
                        	</div>
                        	 --> 
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
    		<div id="searchByEmail" class="tab-pane fade ${webform.activeTab==2? 'active show' : ''}">
      			<div class="row">
      				<div class="col-md-12">
      				<form class="form-horizontal" id="search-general-form" method="post"
						action="/administration/payment/paidusers/search/byemail">
						<fieldset>
                        	<div class="form-label-group mt-3">
                        	  <div class="col-3">
                           	   <label for="emailId">Email</label>                                
                               <input name="email" class="form-control" id="emailId" value="${webform.email}" >      						      					    	
                              </div>
                            </div>  
                        	<div class="form-label-group mt-3">
                        	  <div class="col-3">
                           	   <label for="emailId">PayPal Account</label>                                
                               <input name="paypalAccount" class="form-control" id="paypalAccountId" value="${webform.paypalAccount}" >      						      					    	
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
    		<div id="searchByGuid" class="tab-pane fade ${webform.activeTab==3? 'active show' : ''}">
      			<div class="row">
      				<div class="col-md-12">
      				<form class="form-horizontal" id="search-general-form" method="post"
						action="/administration/payment/paidusers/search/byguid">
						<fieldset>
                        	<div class="form-label-group mt-3">
                        	  <div class="col-3">
                           	   <label for="emailId">User Guid</label>                                
                               <input name="userGuid" class="form-control" id="userGuidId" value="${webform.userGuid}" >      						      					    	
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
  			</div>
	</div>
	
		<!-- Table  -->
		<c:if test="${entities!=null}">
		<div class="row mt-5">
				<table class="table" id="searchResultTable">
					<thead>
						<tr>							
							<th scope="col">user_guid</th>
							<th scope="col">date</th>
							<th scope="col">amount</th>
							<th scope="col">paid_currency</th>
							<th scope="col">eur_currency</th>
							<th scope="col">email</th>
							<th scope="col">paypal_account</th>
							<th scope="col">paid_user_success</th>
							<th scope="col">email_sent_success</th>
							<th scope="col">Link</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="entity" items="${entities}">
							<tr>
								<td>${entity.userGuid}</td>
								<td>${entity.date}</td>
								<td>${entity.amount}</td>
								<td>${entity.paidCurrency}</td>
								<td>${entity.eurCurrency}</td>
								<td>${entity.email}</td>
								<td>${entity.paypalAccount}</td>
								<td>${entity.paidUserSuccess}</td>
								<td>${entity.emailSentSuccess}</td>
								<td>
								<a href="${entity.link}" class="btn btn-primary btn-sm active" role="button" aria-pressed="true" target="_blank">Find</a>
								<!-- 
								<button type="button" class="btn btn-primary btn-sm" data-href="${entity.link}" value="Find"
									 paid-find="true" data-entitykey="${entity.key}" target="_blank">Find</button>
								 -->	 
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>		
		</div>
		</c:if>	
	</div>
		<script type="text/javascript">
			'use strict';

			new payment.PaidUsersView();
		</script>	
</body>
</html>