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
<script src="/js/jsp/eur_currency_tool.js"></script> 
 
<body>
	<jsp:include page="/jsp/fragments/header.jsp">
		<jsp:param name="activeMenuItem" value="tools" />
	</jsp:include>

    <div class="container">
    <div class="row">
        <div class="col-md-12">
            <div class="card card-body bg-light">
			 <c:if test="${error!=null}">
			    <div class="alert alert-danger" role="alert">
				   ${error}
				</div>	
			 </c:if> 
			 <h4>Update EURO currency column</h4>			 
                <form class="form-horizontal" id="run-form" method="post" action="/administration/tools/eurocurrency/run">
                    <fieldset>
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
                <div class="row justify-content-center">                   
                        <div class="form-group">
                            <div class="col-md-12 text-center">
                                <button  type="submit" id="submit-button" class="btn btn-primary btn-lg mt-5" name="submit" value="export">Run</button>
                            </div>
                        </div>         
                        </div>               
                    </fieldset>
                </form>
            </div>
        </div>
    </div>
	<div class="row mt-5">
		<div id="error_message" class="alert alert-danger" role="alert">		   
		</div>	
	    <div id="success_message" class="alert alert-success" role="alert">		
		   <div class="row">
		    <div class="col-sm-2">
		      <div class="spinner-border" role="status"></div>
		    </div>
		    <div class="col-sm-10 px-md-5">
		    <div id="message" class="font-weight-bold"></div>
		    </div>  	
		   </div>			
		</div>			
	</div>    
    </div>

  </body>
</html>
