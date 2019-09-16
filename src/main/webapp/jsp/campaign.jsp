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
<script src="/js/jsp/campaign.js"></script> 
<body>
	<jsp:include page="/jsp/fragments/header.jsp">
		<jsp:param name="activeMenuItem" value="campaign" />
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
                <form class="form-horizontal" id="search-form" method="post" action="/administration/campaign/search">
                    <fieldset>
                    <!-- 
                        <legend class="text-center header">Contact us</legend>
 -->
  				<div class="form-label-group">
  					<div class="col-3">
  					    <label for="startDateId">Start Date</label>
    					<input class="form-control" type="date"  data-date-format="MM/DD/YYYY"  name="startDate" id="startDateId">
  					</div>
				</div>
 				<div class="form-label-group">
  					<div class="col-3">
  					<label for="endDateId">End Date</label>
    					<input class="form-control" type="date" data-date-format="MM/DD/YYYY" name="endDate" id="endDateId">
  					</div>  					
				</div>
                        <div class="form-label-group">
                            
                            <div class="col-md-3">
                            	<label for="countryId">Country</label>                                
                                <select name="country" class="form-control" id="countryId">
      						
      					    	<c:forEach var="item" items="${countries}">
                                	<option>${item}</option>
                            	</c:forEach>
    							</select>
                            </div>
                        </div>
                        <div class="form-label-group mt-3">
                        <div class="col-md-3">
                           	<label for="packageNameId">Package Name</label>                                
                            <input name="packageName" class="form-control" id="packageNameId">      						      					    	
                        </div>
                        </div>                        
                        <div class="form-label-group mt-3">
                        <div class="col-md-12">
                        <div class="card card-body">
                           <div class="row">
                        	
                             <div class="col-md-12">
                               <label for="adnetworkId">AdNetwork</label>
                               <input id="adnetworkId"  type="text" class="form-control">
                             </div>
                            </div>
                            <div class="row mt-3">
                            <div class="col-md-2">
                              <button class="btn" id="add-btn"><i class="fa fa-arrow-down"></i></button>
                              <button class="btn" id="remove-btn"><i class="fa fa-arrow-up"></i></button>
                            </div>
                             </div>
                             <div class="row mt-3">
                              <div class="col-md-12">  
                                <select class="form-control" name="adnetworks" id="adnetworksId" multiple>
                                
                                </select>                            									
                              </div>   
                              </div>                         
                          </div>
                          </div>
                          </div>
                        <div class="form-label-group mt-3">
                        <div class="col-md-12">
                        <div class="card card-body">
                           <div class="row">
                        	
                             <div class="col-md-12">
                               <label for="campaignId">Tenjin Campaign Id</label>
                               <input id="campaignId"  type="text" class="form-control">
                             </div>
                            </div>
                            <div class="row mt-3">
                            <div class="col-md-2">
                              <button class="btn" id="add-btn-campaign"><i class="fa fa-arrow-down"></i></button>
                              <button class="btn" id="remove-btn-campaign"><i class="fa fa-arrow-up"></i></button>
                            </div>
                             </div>
                             <div class="row mt-3">
                              <div class="col-md-12">  
                                <select class="form-control" name="campaigns" id="campaignsId" multiple>
                                
                                </select>                            									
                              </div>                            
                          </div>
                          </div>
                          </div>
                        </div>
                        <div class="form-label-group mt-3">
                        <div class="col-md-12">
                            <div class="card card-body">
                            <div class="row">                        	
                             <div class="col-md-12">
                               <label for="sourceId">Source Id</label>
                               <input id="sourceId"  type="text" class="form-control">
                             </div>
                            </div>
                            <div class="row mt-3">
                            <div class="col-md-2">
                              <button class="btn" id="add-btn-source"><i class="fa fa-arrow-down"></i></button>
                              <button class="btn" id="remove-btn-source"><i class="fa fa-arrow-up"></i></button>
                            </div>
                             </div>
                             <div class="row mt-3">
                              <div class="col-md-12">  
                                <select class="form-control" name="sources" id="sourcesId" multiple>
                                
                                </select>                            									
                              </div>                            
                             </div>
                             </div>
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
