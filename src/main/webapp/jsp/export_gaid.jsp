<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html>
<head>
<title>Administration</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">

</head>
<script src="/js/jquery/jquery.min.js"></script> 

<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
<link rel="stylesheet" href="/css/bootstrap/bootstrap.min.css">         
<script src="/js/bootstrap/bootstrap.min.js"></script> 
 
<body>
	<jsp:include page="/jsp/fragments/header.jsp">
		<jsp:param name="activeMenuItem" value="exportgaid" />
	</jsp:include>

    <div class="container">
    <div class="row">
        <div class="col-md-12">
            <div class="card card-body bg-light">
             <c:if test="${success!=null}">
				<div class="alert alert-success" role="alert">
  					${success}
				</div>	
			 </c:if> 
                <form class="form-horizontal" id="search-form" method="post" action="/administration/exportgaid/run">
                    <fieldset>
                    <!-- 
                        <legend class="text-center header">Contact us</legend>
 -->
  				<div class="form-label-group">
  					<div class="col-3">
  					    <label for="startDateId">Start Date</label>
    					<input class="form-control" type="date"  value="${webform.startDateAsText}" data-date-format="MM/DD/YYYY"  name="startDate" id="startDateId">
  					</div>
				</div>
 				<div class="form-label-group">
  					<div class="col-3">
  					<label for="endDateId">End Date</label>
    					<input class="form-control" type="date" value="${webform.endDateAsText}" data-date-format="MM/DD/YYYY" name="endDate" id="endDateId">
  					</div>  					
				</div>
                        <div class="form-label-group">
                            
                            <div class="col-md-3">
                            	<label for="countryId">Country</label>                                
                                <select name="country" class="form-control" id="countryId">
      						
      					    	<c:forEach var="item" items="${countries}">
                                	<option <c:if test="${webform.countryCode.equals(item)}">selected</c:if>>${item}</option>
                            	</c:forEach>
    							</select>
                            </div>
                        </div>
                        <div class="form-label-group mt-3">
                        <div class="col-md-3">
                           	<label for="packageNameId">Package Name</label>                                
                            <input name="packageName" class="form-control" id="packageNameId" value="${webform.packageName}">      						      					    	
                        </div>
                        </div>
                        <div class="row justify-content-center">                   
                        <div class="form-group">
                            <div class="col-md-12 text-center">
                                <button  type="submit" class="btn btn-primary btn-lg mt-5" name="submit" value="export">Export</button>
                            </div>
                        </div>         
                        </div>               
                    </fieldset>
                </form>
            </div>
        </div>
    </div>
    </div>


  </body>
</html>