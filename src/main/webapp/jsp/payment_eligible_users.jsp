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

	<div class="container" id="contentid">
	<div class="card card-body bg-light">
		<div class="row mt-3">
		  <div class="col-md-2">
			<button class="btn" id="save-template-btn" data-toggle="tooltip" data-placement="top" title="Save search template">
					<i class="fa fa-floppy-o"></i>
			</button>
			<button class="btn" id="load-template-btn" data-toggle="tooltip" data-placement="top" title="Load search template">
			  <i class="fa fa-search"></i>
			</button>
	   	  </div>
		</div>
		<div class="row">
			<div class="col-md-12">
					<c:if test="${success!=null}">
						<div class="alert alert-success" role="alert">${success}</div>
					</c:if>
					<form class="form-horizontal" id="search-form" method="post"
						action="/administration/payment/eligibleusers/search">
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
							<label>Confirmed Email</label> 
							<div class="form-check">
								<input class="form-check-input" type="radio"
									name="confirmedEmail" id="confirmedEmail1" value="none"
									
									<c:if test="${webform.confirmedEmail == null}">
										checked
									</c:if>
									> <label class="form-check-label"
									for="confirmedEmail1"> None </label>
							</div>
							<div class="form-check">
								<input class="form-check-input" type="radio"
									name="confirmedEmail" id="confirmedEmail2" value="true"
									<c:if test="${webform.confirmedEmail}">
										checked
									</c:if>									
									>
								<label class="form-check-label" for="confirmedEmail2">
									True </label>
							</div>
							<div class="form-check disabled">
								<input class="form-check-input" type="radio"
									name="confirmedEmail" id="confirmedEmail3" value="false"
									<c:if test="${(webform.confirmedEmail != null)&&(!webform.confirmedEmail)}">
										checked
									</c:if>										
									> 
									<label class="form-check-label"
									for="confirmedEmail3"> False </label>
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
				<table class="table" id="searchResultTable">
					<thead>
						<tr>
							<th scope="col">Find</th>
							<th scope="col">user_guid</th>
							<th scope="col">date</th>
							<th scope="col">amount</th>
							<th scope="col">type</th>
							<th scope="col">email</th>
							<th scope="col">country</th>
							<th scope="col">paypal_account</th>
							<th scope="col">Paid</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="entity" items="${entities}">
							<tr>
								<td><a href="${entity.link1}"
									class="btn btn-primary btn-sm active" role="button"
									aria-pressed="true" target="_blank">Find</a>
									
									<button type="button" class="btn btn-primary btn-sm" data-entitykey="${entity.key}" remove-reason-button="true">Remove</button>									 																									
									
									</td>
								<td>${entity.userGuid}</td>
								<td>${entity.date}</td>
								<td>${entity.amount}</td>
								<td>
								${entity.type} 								
								<c:if test="${entity.amazonType eq true}">
								   <br>
								   <button type="button" class="btn btn-primary btn-sm" 
									  pay-gc-button="true" data-entitykey="${entity.key}" data-amount="${entity.amount}">Send GC</button>
							    </c:if>								
								</td>
								<td>${entity.email}</td>
								<td>${entity.countryCode}</td>
								<td>${entity.paypalAccount}</td>
								<td>
								<button type="button" class="btn btn-primary btn-sm" data-href="${entity.link2}"
									 paid-button="true" data-paymentType="${entity.type}" data-entitykey="${entity.key}">Paid</button>
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</c:if>

	</div>
	<!-- Paid dialog -->
	<div class="modal fade" id="paidUserDialog" tabindex="-1" role="dialog"
		aria-labelledby="exampleModalCenterTitle" aria-hidden="true">
		<div class="modal-dialog modal-dialog-centered" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title" id="exampleModalLongTitle">Paid Users</h5>
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<div class="modal-body">
				<div class="row mt-3">							
						<div class="col-md-6">
							<label for="paidTypesId">Payment type</label> 
					         <select
						      class="form-control" id="paidTypesId">
			 			       <c:forEach var="item" items="${paymentTypes}">
								<option>${item}</option>
						       </c:forEach>
					        </select>									
					  </div>
				</div>
				<div class="row mt-3">	
						<div class="col-md-6">
							<label for="paidCurrencyCodeId">Currency</label> 
					         <select
						      class="form-control" id="paidCurrencyCodeId">
			 			       <c:forEach var="item" items="${defaultCurrencyCodes}">
								<option>${item}</option>
						       </c:forEach>
					        </select>									
					  </div>					  
				</div>				
				<div class="row mt-3">	
						<div class="col-md-6">
							<label for="amountId">Amount</label> 
								<input class="form-control" id="amountId">									
					  </div>					  
				</div>				
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-primary" id="savePaidUserBtn">Save</button>
					<button type="button" class="btn btn-secondary"
						data-dismiss="modal">Close</button>
				</div>
			</div>
		</div>
	</div>
	<!-- Save Search Template -->
	<div class="modal fade" id="saveSearchTemplateDialog" tabindex="-1" role="dialog" aria-labelledby="saveSearchTemplateLabel" aria-hidden="true">
  	<div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="saveSearchTemplateLabel">Save Search Template</h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body">
		<div class="row mt-3">
				<div class="col-md-5">
					<label for="searchTemplateNameId">Search Template name</label> 
				</div>
				<div class="col-md-7">
					<input name="types"
										class="form-control" id="searchTemplateNameId"
								value="">
				</div>
		</div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-primary" id="saveSearchTemplateBtn">Save</button>
        <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>       
      </div>
    </div>
  </div>
</div>	
	<!-- Save Search Template -->
<div class="modal fade" id="loadSearchTemplateDialog" tabindex="-1" role="dialog" aria-labelledby="loadSearchTemplateLabel" aria-hidden="true">
  	<div class="modal-dialog modal-lg"  role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="loadSearchTemplateLabel">Load Search Template</h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body">
      <div class="container">
        <table id="templatesTableId" 
        class='table table-bordered table-condensed table-striped table-hover' data-height="460" width="100%">
        <thead>
            <tr>
                <th data-field="id">Name</th>
                <th data-field="name">Date</th>              
            </tr>
        </thead>
        <tbody id="templatesTableBodyId">
        </tbody>
     </table>
    </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>        
      </div>
    </div>
  </div>
</div>	
<!-- Removal Reason -->
<div class="modal fade" id="removalReasonDialog" tabindex="-1" role="dialog" aria-labelledby="removalReasonDialogLabel" aria-hidden="true">
  	<div class="modal-dialog"  role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="removalReasonDialogLabel">Removal Reason</h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body">
      <div class="container">
        	<div class="row">
				<div class="col-md-12">
					<select
						name="removeReason" class="form-control" id="removeReasonId">
			 			 <c:forEach var="reason" items="${reasons}">
								<option>${reason}</option>
						 </c:forEach>
					</select>
				</div>
		   </div>
      </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-primary" id="saveRemovalReasonBtn">Save</button> 
        <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>    
      </div>
    </div>
  </div>
</div>	

		<script type="text/javascript">
			'use strict';

			new payment.PaymentEligibleUsersView();
		</script>	
</body>
</html>