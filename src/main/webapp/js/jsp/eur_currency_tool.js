jQuery(document).ready(function(){
	 var tid;
	 var key;

	 $('#error_message').hide();
	 $('#success_message').hide();
	 
	function onTimerCallback() {
		$.ajax({
			    url: '/administration/monitor/progress',
			    dataType: 'json',
			    data:"key="+key,
			    type: 'get',		    	   
			    success: function(data, textStatus, jQxhr ){
			    	if(data.success.status==='FINISHED'){
			    		cancel();
					    $('#error_message').hide();
					    $('#success_message').hide();
			    	}else if(data.success.status==='ERROR'){
			    		cancel();
					    $('#error_message').hide();
					    $('#success_message').hide();
					    alert("Remote server error. Check server logs.");
			        }else{
			   		    $("#message").html("In progress "+data.success.progress+" of "+data.success.total);  
			    	}
			    	

			    },
			    error: function( jqXhr, textStatus, errorThrown ){
			    	console.log( errorThrown );
			    	cancel();			       
			    }
			  
			});	
 
	}
	function cancel() { // to be called when you want to stop the timer
		$("#submit-button").prop('disabled', false); 
		clearInterval(tid);
	}
		
	 $("#run-form").submit(function(e) {
		    $('#error_message').hide();
		    $('#success_message').hide();
		    cancel();
	        //prevent Default functionality
	        e.preventDefault();
	        //get the action-url of the form
	        var actionurl = e.currentTarget.action;
	        //do your own request an handle the results
	        $.ajax({
	                url: actionurl,
	                type: 'post',
	                dataType: 'json',
	                data: $("#run-form").serialize(),	               
					success: function(data, textStatus, jQxhr ){
					  if(data.error){
						 $('#error_message').show();
						 $("#error_message").html(data.error);  
					  }else if(data.success){
						 key=data.success;
						 $("#submit-button").prop('disabled', true);
						 //enable
						 tid = setInterval(onTimerCallback, 3000);
						 $('#success_message').show();
						 $("#message").html("Processing..."); 
					  }	
					  else{
						  alert( errorThrown );
						  cancel();
					  } 					 			 		
					},
					error: function( jqXhr, textStatus, errorThrown ){
						alert( errorThrown );
						cancel();
					}
	        });

	    });
});