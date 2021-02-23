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
          		QA
        	</a>
        	<div class="dropdown-menu" aria-labelledby="navbarDropdown">
          		<a class="dropdown-item" href="/administration/qa/experiment">Remove or Set Experiment</a>          		           		       
        	</div>
      	  </li>        
        </ul>
        <a class="btn btn-outline-primary" href="/administration/logout">Sign out</a>
        
      </div>
    </nav>