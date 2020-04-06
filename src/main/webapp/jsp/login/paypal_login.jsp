<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html>
<head>
<title>PayPal login</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<style>
html, body {
    height: 100%;
}
</style>

</head>
<script src="/js/jquery/jquery.min.js"></script> 

<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
<link rel="stylesheet" href="/css/bootstrap/bootstrap.min.css">         
<script src="/js/bootstrap/bootstrap.min.js"></script> 

<body>
<div class="container h-100 d-flex justify-content-center">
<div class="my-auto">
	<span id='cwppButton'></span>
	<script src='https://www.paypalobjects.com/js/external/connect/api.js'></script>
	<script>
		paypal.use( ['login'], function (login) {
  		login.render ({
    		"appid":"AeIMl71AyLnR3sgUL341TD22bfGTdp-KZFw1ZxQ7QJmQtjE4AdD3CiR9W6oxvHXMNbTXa_rNtQw6ZDZR",
    		"authend":"sandbox",
    		"scopes":"openid profile email",
    		"containerid":"cwppButton",
    		"responseType":"code id_Token",
    		"locale":"en-us",
    		"buttonType":"CWP",
    		"buttonShape":"pill",
    		"buttonSize":"lg",
    		"fullPage":"true",
    		"returnurl":"https://luee-wally-dev-admin.appspot.com/administration/api/login/paypal/token"
  	});
});
</script>
</div>
</div>    
</body>