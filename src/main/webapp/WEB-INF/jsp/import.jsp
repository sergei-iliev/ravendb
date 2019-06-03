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
			 <c:out value="${message}"/>
			</div>
		</c:if>
	 </div></div>
	 <div class="row mt-5">
	 <div class="col-2"> 	
		<a href="/import/createDemoPDFInCloudStore" class="btn btn-primary btn-lg active" role="button" aria-pressed="true">Generate File</a>
	 </div>
	 <div class="col-10">
	   <h2>Generate demo pdf file in a bucket to test Google storage</h2>
	 </div>
	</div>
	 <div class="row mt-5">
	 <div class="col-2"> 	
		<a href="/import/generateDemoRedeemingRequests" class="btn btn-primary btn-lg active" role="button" aria-pressed="true">Generate Entities</a>
	 </div>
	 <div class="col-10">
	   <h2>Total count of entities in <i>RedeemingRequestsNew</i> table is <c:out value="${count}"/></h2>
	 </div>
	</div>	
	 <div class="row mt-5"><div class="col-2"> 	
		<a href="/import/csv/RedeemingRequests" class="btn btn-primary btn-lg active" role="button" aria-pressed="true">Import</a>
	</div>
		 <div class="col-10">
	   <h2>Import CSV file, transforms data and create PDF files in Google Storage</h2>
	 </div>
	</div>
	</div>


</body>
</html>