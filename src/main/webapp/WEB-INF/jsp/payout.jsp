<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@ page import = "com.paypal.api.payments.Payout" %>

<html>
<head><title>Paypal</title></head>
<link rel="stylesheet" href="/css/bootstrap.min.css">         
<script src="/js/bootstrap.min.js"></script> 
<body>
<jsp:include page="/WEB-INF/jsp/fragments/header.jsp" >
<jsp:param name="activeMenuItem" value="payout" />
</jsp:include>
<div class="container">
<c:if test="${error}">
<div class="alert alert-danger" role="alert">
    Payout form data is incomplete
</div>
</c:if>
<form:form method="POST"  action="/payout/send" modelAttribute="payoutForm">
  <div class="form-group row">
    <label for="receiver" class="col-sm-2 col-form-label">Receiver address</label>
    <div class="col-sm-10">
       <form:input type="email" class="form-control" id="receiver" path="receiver" />
    </div>
  </div>
  <div class="form-group row">
    <label for="amount" class="col-sm-2 col-form-label">Amount</label>
    <div class="col-sm-10">
    <form:input type="text" class="form-control" id="amount" path="amount" />
    </div>
  </div>
  <div class="form-group row">
    <label for="currency" class="col-sm-2 col-form-label">Currency</label>
    <div class="col-sm-10">
     <form:select path="currency" items="${payoutForm.currencyList}" class="form-control"/>
    </div>
  </div>
  

 <button type="submit" class="btn btn-primary">Submit</button>  
</form:form>	
</div>
</body>
</html>
