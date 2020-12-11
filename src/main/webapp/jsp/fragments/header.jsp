<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
    
    <nav class="navbar navbar-expand-md navbar-dark bg-dark mb-4">
      <a class="navbar-brand" href="#">Soft Backed</a>
      <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarCollapse" aria-controls="navbarCollapse" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
      </button>
      <div class="collapse navbar-collapse" id="navbarCollapse">
        <ul class="navbar-nav mr-auto">
		  <li class="nav-item dropdown ${param.activeMenuItem eq 'index' ? 'active' : ''}">
        	<a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
          		Affs Revenue
        	</a>
        	<div class="dropdown-menu" aria-labelledby="navbarDropdown">
          		<a class="dropdown-item" href="/administration">Affs Revenue</a>
          		<a class="dropdown-item" href="/administration/revenue/fb">Affs Revenue with FB</a>           		       	
        	</div>
      	  </li>        
        <!-- 
          <li class="nav-item ${param.activeMenuItem eq 'index' ? 'active' : ''}">        
            <a class="nav-link" href="/administration">Affs Revenue<span class="sr-only">(current)</span></a>
          </li>
         -->  
          <li class="nav-item ${param.activeMenuItem eq 'exportgaid' ? 'active' : ''}">   
            <a class="nav-link" href="/administration/exportgaid">Export Gaid</a>
          </li>
          <li class="nav-item dropdown ${param.activeMenuItem eq 'campaign' ? 'active' : ''}">
        	<a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
          		Campaign Revenue
        	</a>
        	<div class="dropdown-menu" aria-labelledby="navbarDropdown">
          		<a class="dropdown-item" href="/administration/campaign">Campaign Revenue</a>
          		<a class="dropdown-item" href="/administration/campaign/fb">Campaign Revenue with FB</a>           		       	
        	</div>
      	  </li>  
      	  <!-- 
          <li class="nav-item ${param.activeMenuItem eq 'campaign' ? 'active' : ''}">   
            <a class="nav-link" href="/administration/campaign">Campaign Revenue</a>
          </li>
           -->          
          <li class="nav-item dropdown">
        	<a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
          		Import
        	</a>
        	<div class="dropdown-menu" aria-labelledby="navbarDropdown">
          		<a class="dropdown-item" href="#">User Revenue 2018</a>
          		<a class="dropdown-item" href="/administration/import/user/revenue/2019">User Revenue 2019</a>          	
        	</div>
      	</li>
          <li class="nav-item dropdown ${param.activeMenuItem eq 'payments' ? 'active' : ''}">
        	<a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
          		Payments
        	</a>
        	<div class="dropdown-menu" aria-labelledby="navbarDropdown">
          		<a class="dropdown-item" href="/administration/payment/eligibleusers">Eligible Users</a>
          		<a class="dropdown-item" href="/administration/payment/paidusers">Paid Users</a> 
          		<a class="dropdown-item" href="/administration/payment/paidusers?groupby">Paid Users Group By</a>   
          		<a class="dropdown-item" href="/administration/payment/confirmemail">Confirm Email</a>       	
        	</div>
      	</li>
          <li class="nav-item dropdown ${param.activeMenuItem eq 'users' ? 'active' : ''}">
        	<a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
          		Users
        	</a>
        	<div class="dropdown-menu" aria-labelledby="navbarDropdown">
          		<a class="dropdown-item" href="/administration/users/data/delete">User data removal</a>
          		       	
        	</div>
      	</li>      	
      	<li class="nav-item ${param.activeMenuItem eq 'settings' ? 'active' : ''}">   
            <a class="nav-link" href="/administration/settings">Application Settings</a>
        </li>
       </ul>
        
        <a class="btn btn-outline-primary" href="/administration/logout">Sign out</a>
        
      </div>
    </nav>