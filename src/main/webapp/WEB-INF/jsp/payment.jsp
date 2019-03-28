<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<html>
<head><title>Paypal</title></head>
<link rel="stylesheet" href="/css/bootstrap.min.css">         
<script src="/js/bootstrap.min.js"></script> 
<body>
<jsp:include page="/WEB-INF/jsp/fragments/header.jsp" >
<jsp:param name="activeMenuItem" value="payment" />
</jsp:include>
<div role="main" class="container">

<div class="panel panel-default">
  <div class="panel-body"><c:out value="${result}" /></div>
</div> 
  
<div class="panel panel-default">
  <div class="panel-body"><c:out value="${redirect}" /></div>
</div>   
<div class = "panel panel-default">
   <div class = "panel-body">
      This is a Basic panel
   </div>
</div>
</div>
</body>
</html>