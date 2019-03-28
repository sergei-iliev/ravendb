<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<nav class="navbar navbar-expand-lg navbar-dark bg-dark static-top">
	<div class="container">
		<button class="navbar-toggler" type="button" data-toggle="collapse"
			data-target="#navbarResponsive" aria-controls="navbarResponsive"
			aria-expanded="false" aria-label="Toggle navigation">
			<span class="navbar-toggler-icon"></span>
		</button>
		<div class="collapse navbar-collapse" id="navbarResponsive">
			<ul class="navbar-nav ml-auto">			 
				<li class="nav-item ${param.activeMenuItem eq 'home' ? 'active' : ''}">
							<a class="nav-link" href="/">Home</a>
				</li>
				<li class="nav-item ${param.activeMenuItem eq 'payment' ? 'active' : ''}"><a class="nav-link" href="/payment">Test
						simple payment</a></li>
				<li class="nav-item ${param.activeMenuItem eq 'email' ? 'active' : ''}">
						<a class="nav-link" href="/email">Test Email</a>			
				</li>
				<li class="nav-item ${param.activeMenuItem eq 'payout' ? 'active' : ''}">
				<a class="nav-link" href="/payout">Test
						Payout</a>
				</li>
				<li class="nav-item ${param.activeMenuItem eq 'accounts' ? 'active' : ''}"><a class="nav-link" href="/account">Accounts</a>
				</li>
			</ul>
		</div>
	</div>
</nav>