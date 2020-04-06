<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html>
<head>
<title>Amazon login</title>
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
<div id="amazon-root"></div>
 <script type="text/javascript">

    window.onAmazonLoginReady = function() {
      amazon.Login.setClientId('amzn1.application-oa2-client.9ca6cdbd136840bdacf23b21ff77d79a');
    };
    (function(d) {
      var a = d.createElement('script'); a.type = 'text/javascript';
      a.async = true; a.id = 'amazon-login-sdk';
      a.src = 'https://assets.loginwithamazon.com/sdk/na/login1.js';
      d.getElementById('amazon-root').appendChild(a);
    })(document);

</script>
<a href id="LoginWithAmazon">
    <img border="0" alt="Login with Amazon"
        src="https://images-na.ssl-images-amazon.com/images/G/01/lwa/btnLWA_gold_156x32.png"
        width="156" height="32" />
</a>
<script type="text/javascript">
    document.getElementById('LoginWithAmazon').onclick = function() {
        options = {}
        options.scope = 'profile';
        options.scope_data = {
            'profile' : {'essential': false} 
        };
        amazon.Login.authorize(options,
            'https://luee-wally-dev-admin.appspot.com/administration/api/login/amazon/token');
        return false;
    };
</script>
</div>
</div>    
</body>