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
							<th scope="col">V</th>
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
						<c:forEach var="entity" items="${entities}" varStatus="loop">
							<tr id='row_${loop.index}'>
								<td>
								<c:if test="${entity.ruleStatus eq 'red'}">
					            	<a href="#" rule-status="true" data-entitykey="${entity.redeemingRequest.key}"> 
								    	<svg width="24px" height="24px" viewBox="0 0 24 24" fit="" preserveAspectRatio="xMidYMid meet" focusable="false"><path d="M12 2C6.5 2 2 6.5 2 12s4.5 10 10 10 10-4.5 10-10S17.5 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z" fill="#D50000"></path></svg>
									</a>    
								</c:if>
								<c:if test="${entity.ruleStatus eq 'yellow'}">
									<a href="#" rule-status="true" data-entitykey="${entity.redeemingRequest.key}">
								    	<svg fill="#FF9E00" width="24px" height="24px" viewBox="0 0 24 24" fit="" preserveAspectRatio="xMidYMid meet" focusable="false"><path d="M9 21c0 .55.45 1 1 1h4c.55 0 1-.45 1-1v-1H9v1zm3-19C8.13 2 5 5.13 5 9c0 2.38 1.19 4.47 3 5.74V17c0 .55.45 1 1 1h6c.55 0 1-.45 1-1v-2.26c1.81-1.27 3-3.36 3-5.74 0-3.87-3.13-7-7-7z" fill-rule="evenodd"></path></svg>
								    </a>	
							    </c:if>
							    <c:if test="${entity.ruleStatus eq 'green'}">	    
									<svg width="24" height="24" viewBox="0 0 24 24" fit="" preserveAspectRatio="xMidYMid meet" focusable="false"><path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z" fill="#0F9D58"></path></svg>
								</c:if>									    
								</td>
								<td><a href="${entity.redeemingRequest.link1}"
									class="btn btn-primary btn-sm active" role="button"
									aria-pressed="true" target="_blank">Find</a>
									
									<button type="button" class="btn btn-primary btn-sm" data-entitykey="${entity.redeemingRequest.key}" remove-reason-button="true">Remove</button>									 																									
									
									</td>
								<td>${entity.redeemingRequest.userGuid}</td>
								<td>${entity.redeemingRequest.date}</td>
								<td>${entity.redeemingRequest.amount}</td>
								<td>
								${entity.redeemingRequest.type} 								
								<c:if test="${entity.redeemingRequest.amazonType eq true}">
								  <c:if test="${isSendGCVisible eq true}">
								    <br>
								    <button type="button" class="btn btn-primary btn-sm" data-href="${entity.redeemingRequest.link2}" 
									  pay-gc-button="true" data-entitykey="${entity.redeemingRequest.key}" data-amount="${entity.redeemingRequest.amount}">Send GC</button>
								  </c:if>	   
							    </c:if>
								<c:if test="${entity.redeemingRequest.payPalType eq true}">
								  <c:if test="${isPayPalVisible eq true}">
								    <br>
								    <button type="button" class="btn btn-primary btn-sm" data-href="${entity.redeemingRequest.link2}"
									  pay-paypal-button="true" data-entitykey="${entity.redeemingRequest.key}" data-amount="${entity.redeemingRequest.amount}">PayPal</button>
								   </c:if>	   
							    </c:if>
							    								
								</td>
								<td>
								 	<div id='email_field_${loop.index}'>${entity.redeemingRequest.email}</div>
								    <br>
								    <a href="#" class="edit_email" data-index="${loop.index}" data-entitykey="${entity.redeemingRequest.key}"><i class="fa fa-pencil-square-o"></i></a>
								     
								</td>
								<td>${entity.redeemingRequest.countryCode}</td>
								<td>
									<div id='paypal_account_field_${loop.index}'>${entity.redeemingRequest.paypalAccount}</div>
									<br>
								    <a href="#"  class="edit_paypal_acount" data-index="${loop.index}" data-entitykey="${entity.redeemingRequest.key}"><i class="fa fa-pencil-square-o"></i></a>
								    <c:if test="${entity.redeemingRequest.payPalType eq true}">								     
								    	<a href="#"  class="send_paypal_validation_email" data-index="${loop.index}" data-entitykey="${entity.redeemingRequest.key}"><i class="fa fa-paper-plane"></i></a> 							
								    </c:if>
								</td>
								<td>
								<button type="button" class="btn btn-primary btn-sm" data-href="${entity.redeemingRequest.link2}" value="Paid"
									 paid-button="true" data-paymentType="${entity.redeemingRequest.type}" data-entitykey="${entity.redeemingRequest.key}">Paid</button>
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
<!-- Edit Email -->
<div class="modal fade" id="editEmailDialog" tabindex="-1" role="dialog" aria-labelledby="editEmailDialogLabel" aria-hidden="true">
  	<div class="modal-dialog"  role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="editEmailDialogLabel">Edit Email</h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body">
      <div class="container">
		<div class="row mt-3">
				<div class="col-md-4">
					<label for="emailFieldId">Email</label> 
				</div>
				<div class="col-md-8">
					<input name="types" class="form-control" id="emailFieldId" value="">
				</div>
		</div>
      </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-primary" id="saveEmailFieldBtn">Save</button> 
        <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>    
      </div>
    </div>
  </div>
</div>	
<!-- Edit PayPal Account -->
<div class="modal fade" id="editPayPalAccountDialog" tabindex="-1" role="dialog" aria-labelledby="editPayPalAccountDialogLabel" aria-hidden="true">
  	<div class="modal-dialog"  role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="editPayPalAccountDialogLabel">Edit PayPal Account</h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body">
      <div class="container">
		<div class="row mt-3">
				<div class="col-md-4">
					<label for="payPalAccountFieldId">PayPal Account</label> 
				</div>
				<div class="col-md-8">
					<input name="types" class="form-control" id="payPalAccountFieldId" value="">
				</div>
		</div>
      </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-primary" id="savePayPalAccountFieldBtn">Save</button> 
        <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>    
      </div>
    </div>
  </div>
</div>	
<!-- Send PayPal Validation email -->
<div class="modal fade" id="sendPayPalValidationEmailDialog" tabindex="-1" role="dialog" aria-labelledby="sendPayPalValidationEmailDialogLabel" aria-hidden="true">
  	<div class="modal-dialog"  role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="sendPayPalValidationEmailDialogLabel">Send PayPal Validation email</h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body">
      <div class="container">
		<div class="row mt-3">
				<div class="col-md-12">
					<span>Send user an email to report issue with PayPal account?</span> 
				</div>			
		</div>
      </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-primary" id="sendPayPalValidationEmailBtn">Send</button> 
        <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>    
      </div>
    </div>
  </div>
</div>
<!-- Rules -->
<div class="modal fade" id="ruleStatusDialog" tabindex="-1" role="dialog" aria-labelledby="ruleStatusDialogLabel" aria-hidden="true">
  	<div class="modal-dialog"  role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="ruleStatusDialogLabel">Redeeming Request Rule Status</h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body">
      <div class="container">
		<div class="row mt-3">
				<div class="col-md-12" id="ruleStatusResultId">
					 
				</div>			
		</div>
      </div>
      </div>
      <div class="modal-footer">
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