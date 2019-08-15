<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html>
<head>
<title>Administration</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">

</head>
<script src="../js/jquery/jquery.min.js"></script> 

<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
<link rel="stylesheet" href="../css/bootstrap/bootstrap.min.css">         
<script src="../js/bootstrap/bootstrap.min.js"></script> 
<script src="../js/jsp/index.js"></script> 
<body>
    <nav class="navbar navbar-expand-md navbar-dark bg-dark mb-4">
      <a class="navbar-brand" href="#">Backed Soft</a>
      <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarCollapse" aria-controls="navbarCollapse" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
      </button>
      <div class="collapse navbar-collapse" id="navbarCollapse">
        <ul class="navbar-nav mr-auto">
          <li class="nav-item active">
            <a class="nav-link" href="/administration">Home <span class="sr-only">(current)</span></a>
          </li>
          <li class="nav-item">
            <a class="nav-link" href="#">Reserved</a>
          </li>

        </ul>
        
        <a class="btn btn-outline-primary" href="/administration/logout">Sign out</a>
        
      </div>
    </nav>

    <div class="container">
    <div class="row">
        <div class="col-md-5 offset-md-3">
            <div class="well well-sm">
             <c:if test="${success!=null}">
				<div class="alert alert-success" role="alert">
  					${success}
				</div>	
			 </c:if> 
                <form class="form-horizontal" id="search-form" method="post" action="/administration/search">
                    <fieldset>
                    <!-- 
                        <legend class="text-center header">Contact us</legend>
 -->
  				<div class="form-label-group">
  					<div class="col-6">
  					    <label for="startDateId">Start Date</label>
    					<input class="form-control" type="date"  data-date-format="MM/DD/YYYY"  name="startDate" id="startDateId">
  					</div>
				</div>
 				<div class="form-label-group">
  					<div class="col-6">
  					<label for="endDateId">End Date</label>
    					<input class="form-control" type="date" data-date-format="MM/DD/YYYY" name="endDate" id="endDateId">
  					</div>  					
				</div>
                        <div class="form-label-group">
                            
                            <div class="col-md-12">
                            	<label for="countryId">Country</label>                                
                                <select name="country" class="form-control" id="countryId">
      						
      					    	<c:forEach var="item" items="${countries}">
                                	<option>${item}</option>
                            	</c:forEach>
    							</select>
                            </div>
                        </div>
                        <div class="form-label-group mt-3">
                        <div class="col-md-12">
                        	<div class="row">
                        	
                            <div class="col-md-5">
                               <label for="experimentId">Experiment</label>
                               <input id="experimentId"  type="text" placeholder="Experiment" class="form-control">
                            </div>
                            <div class="col-md-2">
                              <button class="btn" id="add-btn"><i class="fa fa-arrow-right"></i></button>
                              <button class="btn" id="remove-btn"><i class="fa fa-arrow-left"></i></button>
                            </div>
                             
                            <div class="col-md-5">  
                                <select class="form-control" name="experiments" id="experimentsId" multiple>
                                
                                </select>                            									
                            </div>
                          </div>
                          </div>
                        </div>
  						<div class="form-label-group mt-3">
  						  <div class="col-md-12">
  							 <div class="row">                        	
                              <div class="col-md-12">
                              <div class="form-check-inline">
  								<label class="form-check-label">
    								<input type="checkbox" name="groupByExperiment" class="form-check-input" >Group by experiment
  								</label>
							  </div>
  								    							
    						</div>
    						</div>
    						</div>
  						</div>
                        <div class="form-label-group mt-3">
                        <div class="col-md-12">
                           	<label for="packageNameId">Package Name</label>                                
                            <input name="packageName" class="form-control" id="packageNameId">      						      					    	
                        </div>
                        </div>
                        <div class="form-group">
                            <div class="col-md-12 text-center">
                                <button type="submit" class="btn btn-primary btn-lg mt-5">Search</button>
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
