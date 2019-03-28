<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@ page import="com.paypal.api.payments.Payout"%>

<html>
<head>
<title>Paypal</title>
</head>
<link rel="stylesheet" href="/css/bootstrap.min.css">
<script src="/js/bootstrap.min.js"></script>
<body>
	<jsp:include page="/WEB-INF/jsp/fragments/header.jsp">
		<jsp:param name="activeMenuItem" value="accounts" />
	</jsp:include>
	<div class="container">
		<c:if test="${error}">
			<div class="alert alert-danger" role="alert">Error in saving
				account</div>
		</c:if>
		<form:form method="POST" action="/account/save"
			modelAttribute="payPalUser">
			<div class="form-group row">
				<label for="fullname" class="col-sm-2 col-form-label">Full
					Name</label>
				<div class="col-sm-10">
					<form:input type="text" class="form-control" id="fullname"
						path="fullName" />
				</div>
			</div>
			<div class="form-group row">
				<label for="fulladdress" class="col-sm-2 col-form-label">Address</label>
				<div class="col-sm-10">
					<form:input type="text" class="form-control" id="fulladdress"
						path="fullAddress" />
				</div>
			</div>
			<div class="form-group row">
				<label for="amount" class="col-sm-2 col-form-label">Amount</label>
				<div class="col-sm-10">
					<form:input type="text" class="form-control" id="amount"
						path="amount" />
				</div>
			</div>
			<div class="form-group row">
				<label for="paypalaccount" class="col-sm-2 col-form-label">PayPal
					email</label>
				<div class="col-sm-10">
					<form:input type="email" class="form-control" id="paypalaccount"
						path="paypalAccount" />
				</div>
			</div>
			<div class="form-group row">
				<label for="countrycode" class="col-sm-2 col-form-label">Country
					Code</label>
				<div class="col-sm-10">
					<form:input type="text" class="form-control" id="countrycode"
						path="countryCode" />
				</div>
			</div>
			<div class="form-group row">
				<label for="currency" class="col-sm-2 col-form-label">Currency</label>
				<div class="col-sm-10">
					<form:select path="currency" items="${payPalUser.currencyList}"
						class="form-control" />
				</div>
			</div>


			<button type="submit" class="btn btn-primary">Submit</button>
		</form:form>
	</div>
	<table class="table">
		<thead>
			<tr>
				<th>Uuid</th>
				<th>Name</th>
				<th>Account</th>
				<th>Address</th>
				<th>Amount</th>
				<th>Currency</th>
				<th>CountryCode</th>
				<th></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="account" items="${accounts}">
				<tr>
					<td>${account.uuid}</td>
					<td>${account.fullName}</td>
					<td>${account.paypalAccount}</td>
					<td>${account.fullAddress}</td>
					<td>${account.amount}</td>
					<td>${account.currency}</td>
					<td>${account.countryCode}</td>
					<td><a
						href="${pageContext.request.contextPath }/account/delete/${account.uuid}"
						onclick="return confirm('Are you sure?')">Delete</a></td>
					<td><a
						href="${pageContext.request.contextPath }/payout/pay/${account.uuid}">Pay</a></td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<table class="table">
		<thead>
			<tr>
				<th>id</th>
				<th>Account</th>
				<th>PayPal Transaction Id</th>
				<th>Credit Note Number</th>
				<th>Amount</th>
				<th>Currency</th>
				<th>CountryCode</th>
				<th>user</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="payment" items="${payments}">
				<tr>
					<td>${payment.id}</td>
					<td>${payment.paypalAccount}</td>
					<td>${payment.paypalTransactionId}</td>
					<td>${payment.creditNoteNo}</td>
					<td>${payment.amount}</td>
					<td>${payment.currency}</td>
					<td>${payment.countryCode}</td>
					<td>${payment.userKey.id}</td>
			</c:forEach>
		</tbody>
	</table>
</body>
</html>