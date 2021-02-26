<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html>
<head>
<title>QA</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">

</head>
<script src="/js/jquery/jquery.min.js"></script> 

<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
<link rel="stylesheet" href="/css/bootstrap/bootstrap.min.css">         
<script src="/js/bootstrap/bootstrap.min.js"></script> 
<body>
	<jsp:include page="/jsp/fragments/qa/header.jsp">
		<jsp:param name="activeMenuItem" value="index" />
	</jsp:include>

    <div class="container">
    <div class="row">
        <div class="col-md-12">
            <div class="card card-body bg-light">             			 
			 <div class="mt-5">
			 <c:if test="${error!=null}">
				<span class="error text-danger">${error}</span>	
			 </c:if> 
                <form class="form-horizontal" id="search-form" method="post" action="/administration/qa/experiment">
                    <fieldset>
						<div class="form-label-group mt-3">
                         <div class="col-md-3">
                           	<label for="userGuidId">User GUID</label>                                
                            <input name="userGuid" class="form-control" id="userGuidId" value="${webform.userGuid}">      						      					    	
                         </div>
                        </div>                    
                        <div class="form-label-group mt-3">
                        <div class="col-md-3">
                           	<label for="experimentId">Experiment</label>                                
                            <input name="experiment" class="form-control" id="experimentId" value="${webform.experiment}">      						      					    	
                        </div>
                        </div>
                        <div class="row justify-content-center">
                        <div class="form-group">
                            <div class="col-md-12 text-center">
                                <button type="submit" class="btn btn-primary btn-lg mt-5" name="submit" value="search">View</button>
                            </div>
                        </div>                     
                        <div class="form-group">
                            <div class="col-md-12 text-center">
                                <button  type="submit" class="btn btn-primary btn-lg mt-5" name="submit" value="set-experiment">Set experiment</button>
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="col-md-12 text-center">
                                <button  type="submit" class="btn btn-primary btn-lg mt-5" name="submit" value="remove-experiment">Remove experiment</button>
                            </div>
                        </div>
                        </div>               
                    </fieldset>
                </form>
                </div>
            </div>
        </div>
    </div>
		<!-- Table  -->
		<c:if test="${affs!=null}">
		<div class="row mt-5">
		<table class="table">
					<thead>
						<tr>							
							<th scope="col">user_guid</th>
							<th scope="col">experiment</th>							
						</tr>
					</thead>
					<tbody>
						<c:forEach var="aff" items="${affs}">
							<tr>
								<td>${aff.userGuid}</td>
								<td>${aff.experiment}</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>				
		</div>
		</c:if>    
    </div>


  </body>
</html>
