var payment = {};
payment.PaymentEligibleUsersView = Backbone.View.extend({
  // el - stands for element. Every view has a element associate in with HTML
  //      content will be rendered.
  el: '#contentid',
  // It's the first function called when this view it's instantiated.
  initialize: function(){
	  $('[data-toggle="tooltip"]').tooltip();	

	  //********PayPal
	  $('[pay-paypal-button="true"]').click(function(e) {
		  e.preventDefault();
		  var key = $(this).data('entitykey');
		  var url = $(this).data('href');
		  
		  $(e.target).prop('disabled', true);
			var formData={
					   key:key					
				  };
			//1.register record in paid_user table
			$.ajax({
				url:'/administration/payment/user/paypal',
				type:'post',
				data:formData,
				success: function(data, textStatus, jQxhr ){			          
	    			if(!data.startsWith("OK")){
	    				$(e.target).css("background-color","red");
	    				 if(data.startsWith("+")){	//payment done!
					 	   //disable Paid button
						   var $tr = $(e.target).closest('tr');
						   $tr.find($(':button[value="Paid"]')).prop('disabled', true);	 
	    				   
						   var response=JSON.parse(data.substring(1));
				    	   								  
				    	   var text="Could not add user payment because user payment already exists:\r\n"+
				    		   "date: "+response.properties.date+"\r\n"+
				    		   "user_guid: "+response.properties.user_guid+"\r\n"+
				    		   "paid_currency: "+response.properties.paid_currency+"\r\n"+
				    		   "amount: "+response.properties.amount+"\r\n"+
				    		   "type: "+response.properties.type+"\r\n"+
				    		   "eur_currency: "+response.properties.eur_currency+"\r\n"+
				    		   "email_address: "+response.properties.email_address+"\r\n"+
				    		   "paypal_account: "+response.properties.paypal_account+"\r\n"+
				    		   "paid_user_success: "+response.properties.paid_user_success+"\r\n"+
				    		   "email_sent_success: "+response.properties.email_sent_success+"\r\n";							    	    
				    	       alert(text);	
	    				 }else{
	    					 alert(data);
	    				 }
	    			}else{
	    				//register LINK
	    				$.ajax({
	    					    url: url,
	    					    type: 'get',			    	   
	    					    success: function(data, textStatus, jQxhr ){	
	    					    	var $tr = $(e.target).closest('tr');
	    					    	if(data.email_sent_successfully&&data.paid_successfully){				    	 
								 		//disable Paid button								    	
										$tr.find($(':button[value="Paid"]')).prop('disabled', true);	
										$tr.find($(':button[value="Paid"]')).css('background-color', "green");
										
										$(e.target).css("background-color","green");					    				
	    						    }else{
										$tr.find($(':button[value="Paid"]')).prop('disabled', true);	
										$tr.find($(':button[value="Paid"]')).css('background-color', "red");
										
					    				$(e.target).css("background-color","green");	    						    	
	    						        alert("paid_successfully: "+data.paid_successfully+"\r\n"+"email_sent_successfully: "+data.email_sent_successfully);
	    						    }
	    					    },
	    					    error: function( jqXhr, textStatus, errorThrown ){
	    					        alert(errorThrown);
	    					    	console.log( errorThrown );
	    					    }
	    					    });     
	    		    }	
				},
				error: function( jqXhr, textStatus, errorThrown ){
					$(e.target).css("background-color","red");
					console.log( errorThrown );
				}
	  
			});
		  
		  
	  });

	  //********send gift card
	  $('[pay-gc-button="true"]').click(function(e) {	
		  e.preventDefault();
		  var key = $(this).data('entitykey');
		  var url = $(this).data('href');
		  
		
		  var amount = Number($(this).data('amount'));
		  if(amount>50){
			  var result = confirm("Are you sure you want to send the user a gift card of 50");
		      if(!result){
		    	  return;
		      }
		  }
		  $(e.target).prop('disabled', true);
			var formData={
					   key:key					
				  };
			//1.register record in paid_user table
	    	$.ajax({
	    		url:'/administration/payment/user/giftcard',
	    		type:'post',
	    		data:formData,
	    		success: function(data, textStatus, jQxhr ){			        

	    			if(!data.startsWith("OK")){
	    				$(e.target).css("background-color","red");
	    				 if(data.startsWith("200")){	//payment done!
					 	   //disable Paid button
						   var $tr = $(e.target).closest('tr');
						   $tr.find($(':button[value="Paid"]')).prop('disabled', true);	 
	    				   
						   var response=JSON.parse(data.substring(4));
				    	   								  
				    	   var text="Could not add user payment because user payment already exists:\r\n"+
				    		   "date: "+response.properties.date+"\r\n"+
				    		   "user_guid: "+response.properties.user_guid+"\r\n"+
				    		   "paid_currency: "+response.properties.paid_currency+"\r\n"+
				    		   "amount: "+response.properties.amount+"\r\n"+
				    		   "type: "+response.properties.type+"\r\n"+
				    		   "eur_currency: "+response.properties.eur_currency+"\r\n"+
				    		   "email_address: "+response.properties.email_address+"\r\n"+
				    		   "paypal_account: "+response.properties.paypal_account+"\r\n"+
				    		   "paid_user_success: "+response.properties.paid_user_success+"\r\n"+
				    		   "email_sent_success: "+response.properties.email_sent_success+"\r\n";							    	    
				    	       alert(text);	
	    				 }else{
	    					 alert(data);
	    				 }
	    			}else{
	    				//register LINK
	    				  $.ajax({
	    					    url: url,
	    					    type: 'get',			    	   
	    					    success: function(data, textStatus, jQxhr ){
	    					    	var $tr = $(e.target).closest('tr');
	    					    	if(data.email_sent_successfully&&data.paid_successfully){				    	 
								 		//disable Paid button								    	
										$tr.find($(':button[value="Paid"]')).prop('disabled', true);	
										$tr.find($(':button[value="Paid"]')).css('background-color', "green");
										
										$(e.target).css("background-color","green");					    				
	    						    }else{
										$tr.find($(':button[value="Paid"]')).prop('disabled', true);	
										$tr.find($(':button[value="Paid"]')).css('background-color', "red");
										
					    				$(e.target).css("background-color","green");	    						    	
	    						        alert("paid_successfully: "+data.paid_successfully+"\r\n"+"email_sent_successfully: "+data.email_sent_successfully);
	    						    }
	    					    },
	    					    error: function( jqXhr, textStatus, errorThrown ){
	    					        alert(errorThrown);
	    					    	console.log( errorThrown );
	    					    }
	    					    });
	    			}			    					    			
	    			
	    		},
	    		error: function( jqXhr, textStatus, errorThrown ){
	    			$(e.target).css("background-color","red");
	    			console.log( errorThrown );
	    		}
	    	});
	  });
	  $('[paid-button="true"]').click(function(e) {	  
		  e.preventDefault();
		  $('#savePaidUserBtn').prop('disabled', false);
		  $('#paidUserDialog').modal('show');	
		  var paymentType = $(this).data('paymenttype');
		  var key = $(this).data('entitykey');
		  var url = $(this).data('href');
		  
		
		  $("#paidTypesId").val(paymentType);
		  $("#amountId").val('');
		  
		  //disconnect event handler and enable it		  
		  $('#savePaidUserBtn').off();
		  $('#savePaidUserBtn').on( "click", function(ee) {	
			  if($("#amountId").val().length==0){
				  console.log('empty field');
				  return;
			  }
			  
			  if(isNaN($("#amountId").val())){
				  console.log($("#amountId").val()+' is not a number');
				  return; 
			  }
			  
			  $('#savePaidUserBtn').prop('disabled', true);
			  $(e.target).prop('disabled', true);
			  $.ajax({
				    url: url,
				    type: 'get',			    	   
				    success: function(data, textStatus, jQxhr ){			        				       
				        var result=data;
						var formData={
								  amount:$("#amountId").val(),
								  key:key,
								  paymentType:$("#paidTypesId").val(),
								  currencyCode:$("#paidCurrencyCodeId").val(),
								  email_sent_success:result.email_sent_successfully,
								  paid_user_success:result.paid_successfully,  
						};
						
			    		//1.register record in paid_user table
						  $.ajax({
							    url: '/administration/payment/user/pay',
							    type: 'post',
							    data:formData,	    
							    success: function(data, textStatus, jQxhr ){
							       if(!data.startsWith("OK")){
							    	   var response=JSON.parse(data);
							    	   
							    	   var text="Could not add user payment because user payment already exists:\r\n"+
							    		   "date: "+response.properties.date+"\r\n"+
							    		   "user_guid: "+response.properties.user_guid+"\r\n"+
							    		   "paid_currency: "+response.properties.paid_currency+"\r\n"+
							    		   "amount: "+response.properties.amount+"\r\n"+
							    		   "type: "+response.properties.type+"\r\n"+
							    		   "eur_currency: "+response.properties.eur_currency+"\r\n"+
							    		   "email_address: "+response.properties.email_address+"\r\n"+
							    		   "paypal_account: "+response.properties.paypal_account+"\r\n"+
							    		   "paid_user_success: "+response.properties.paid_user_success+"\r\n"+
							    		   "email_sent_success: "+response.properties.email_sent_success+"\r\n";							    	   

							    	   
							    	   alert(text);
							       }	
							       $('#paidUserDialog').modal('hide');									        
							    },
							    error: function( jqXhr, textStatus, errorThrown ){
							       console.log( errorThrown );
							    }
							  
							});
						//2.mark table row  
				    	if(result.email_sent_successfully&&result.paid_successfully){				    	 
				    	  $(e.target.parentNode.parentNode).css('background-color','#4DDF0B');
				        }else{
				          $(e.target.parentNode.parentNode).css('background-color','#ff0000');
				          alert("paid_successfully: "+result.paid_successfully+"\r\n"+"email_sent_successfully: "+result.email_sent_successfully);
				        }
				    	
				    },
				    error: function( jqXhr, textStatus, errorThrown ){
				        console.log( errorThrown );
				    }
				  
				});			  
			  
			  
		  });
		  			
	  } );
	  
	  $('[remove-reason-button="true"]').click(function(e) {		  
		  e.preventDefault();
	
		  var key = $(this).data('entitykey');

		  $('#removalReasonDialog').modal('show');	 
		  
		  //disconnect event handler
		  $('#saveRemovalReasonBtn').off();
		  $('#saveRemovalReasonBtn').on( "click", function() {			  
			  if($("#removeReasonId").val().trim().length==0){
				  return;
			  }
			  var formData={
					  reason:$("#removeReasonId").val(),
					  key:key,
			  };
			  
			  $.ajax({
			    url: '/administration/payment/removal/reason',
			    type: 'post',
			    data:formData,	    
			    success: function(data, textStatus, jQxhr ){
			    	$(e.target.parentNode.parentNode).css('background-color','#ffff00');
			        console.log(data);
			    },
			    error: function( jqXhr, textStatus, errorThrown ){
			        console.log( errorThrown );
			        $(e.target.parentNode.parentNode).css('background-color','#ffff00');
			    }
			  
			});
			  
			  $('#removalReasonDialog').modal('hide');
			});		  
		  
	  } );	  
  },
  events: {
	  "submit #search-form":"onSubmit",
      "click  #add-btn" : "onAdd",	
	  "click  #remove-btn" : "onRemove",
	  "click  #save-template-btn" : "onSaveTemplate",
	  "click  #load-template-btn" : "onLoadTemplate",
  },

  onSaveTemplate:function(e){
	  e.preventDefault();
	  var self=this;
	  
	  $('#saveSearchTemplateDialog').modal('show');	 
	  $("#searchTemplateNameId").val('');
	  //disconnect event handler
	  $('#saveSearchTemplateBtn').off();
	  
	  $('#saveSearchTemplateBtn').on( "click", function() {
		  if($("#searchTemplateNameId").val().length==0){
			  console.log('empty field');
			  return;
		  }
		  
		  $("#countriesId option").prop("selected", "selected");
		  var formData={
				  name:$("#searchTemplateNameId").val(),
				  types:$('#typesId').val(),
				  startDate:$('#startDateId').val(),
				  endDate:$('#endDateId').val(),		  
				  countries:$('#countriesId').val(),
				  packageNames:$('#packageNamesId').val(),
				  confirmedEmail:$("input:radio[name ='confirmedEmail']:checked").val()
		  };
		  $.ajax({
		    url: '/administration/search/template',
		    type: 'post',
		    data:formData,	    
		    success: function(data, textStatus, jQxhr ){
		        console.log(data);
		    },
		    error: function( jqXhr, textStatus, errorThrown ){
		        console.log( errorThrown );
		    }
		  
		});
		  
		  $('#saveSearchTemplateDialog').modal('hide');
		});

  },
  onLoadTemplate:function(e){
	  e.preventDefault();
	  var self=this;
	  $('#loadSearchTemplateDialog').modal('show');
	  $("#templatesTableBodyId").empty();
	  $.ajax({
		    url: '/administration/search/templates',
		    type: 'get',		    	   
		    success: function(data, textStatus, jQxhr ){
		    	var map=JSON.parse(data);
		        console.log(map.result);
		        var html='';
		        
			        $.each(map.result, function(i, item) {
		            html += "<tr onclick=\"window.location='/administration/search/template/filter?key="+item.key+"'\"><td>" + item.name + "</td><td>" + item.dateAsText + "</td></tr>";
		        });		        
		        $('#templatesTableBodyId').append(html);

		    },
		    error: function( jqXhr, textStatus, errorThrown ){
		        console.log( errorThrown );
		    }
		  
		});	  
	
	  
  },
  onSubmit:function(e){ 
	  $("#countriesId option").prop("selected", "selected");
  },
  onAdd:function(e){
	  e.preventDefault();
	  var option=$('#countryId').val();
	       $('#countriesId').append($('<option>', {
	    	    value: option,
	    	    text: option
	    	}));
  },
  onRemove:function(e){
	  e.preventDefault();
	  $('#countriesId').find('option:selected').remove();	
  },  
  // $el - it's a cached jQuery object (el), in which you can use jQuery functions
  //       to push content. Like the Hello World in this case.
  render: function(){
	  
    //this.$el.html("Hello World");
  }
});