<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<html>
<head><title>Paypal</title></head>
<link rel="stylesheet" href="/css/bootstrap.min.css">         
<script src="/js/bootstrap.min.js"></script> 
<body>
<jsp:include page="/WEB-INF/jsp/fragments/header.jsp" >
<jsp:param name="activeMenuItem" value="email" />
</jsp:include>
<div class="container">
<c:if test="${error}">
<div class="alert alert-danger" role="alert">
    Email form data is incomplete
</div>
</c:if>
<form:form method="POST"  action="/email/send" modelAttribute="email" enctype="multipart/form-data">
  <div class="form-group row">
    <label for="exampleFormControlInput1" class="col-sm-2 col-form-label">Email address</label>
    <div class="col-sm-10">
       <form:input type="email" class="form-control" id="exampleFormControlInput1" path="to" />
    </div>
  </div>
  <div class="form-group row">
    <label for="exampleFormControlInput2" class="col-sm-2 col-form-label">Subject</label>
    <div class="col-sm-10">
    <form:input type="text" class="form-control" id="exampleFormControlInput2" path="subject" />
    </div>
  </div>
  <div class="form-group row">
    <label for="exampleFormControlTextarea1" class="col-sm-2 col-form-label">Content</label>
    <div class="col-sm-10">
    <form:textarea class="form-control" id="exampleFormControlTextarea1" rows="3" path="content" />
    </div>
  </div>
 <div class="form-group row">
    <label for="attachmentFile" class="col-sm-2 col-form-label">Attachment</label>
    <div class="col-sm-10">
    <input type="file" class="form-control-file" id="attachmentFile" name="attachmentFile">
    </div>
  </div>  
 <button type="submit" class="btn btn-primary">Submit</button>  
</form:form>	
</div>
</body>
</html>