<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>


<html>
<head>
<title>Import</title>
</head>
<link rel="stylesheet" href="/css/bootstrap.min.css">
<script src="/js/bootstrap.min.js"></script>
<body>
	<jsp:include page="/WEB-INF/jsp/fragments/header.jsp">
		<jsp:param name="activeMenuItem" value="import" />
	</jsp:include>
	<div class="container">
	 <div class="row"><div class="col-12">
		<c:if test="${error}">
			<div class="alert alert-danger" role="alert">
			 <c:out value="${error}"/>
			</div>
		</c:if>
	 </div></div>
	 <div class="row mt-5">
	 <div class="col-2"> 	
		<a href="/import/generateFile" class="btn btn-primary btn-lg active" role="button" aria-pressed="true">Generate File</a>
	 </div>
	 <div class="col-10">
	   <h2>Generate file in a bucket to test Google storage</h2>
	 </div>
	</div>
	
	 <div class="row mt-5"><div class="col-12"> 	
		<a href="/import/RedeemingRequestsNew" class="btn btn-primary btn-lg active" role="button" aria-pressed="true">Download</a>
	</div></div>
	</div>


</body>
</html>