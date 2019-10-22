var payment = {};
payment.PaymentEligibleUsersView = Backbone.View.extend({
  // el - stands for element. Every view has a element associate in with HTML
  //      content will be rendered.
  el: '#contentid',
  // It's the first function called when this view it's instantiated.
  initialize: function(){
	  $('[data-toggle="tooltip"]').tooltip();	

	  
	  $('[data-button="true"]').click(function(e) {	  
		  e.preventDefault();
		  var url = $(this).data('href');
		  //console.log(url);
		  //window.open(url, '_blank');
		  $.ajax({
			    url: url,
			    type: 'get',			    	   
			    success: function(data, textStatus, jQxhr ){			        
			        //var result=JSON.parse(data);
			        var result=data
			    	if(result.email_sent_successfully&&result.paid_successfully){
			  		  $(e.target).prop('disabled', true);
			  		  $(e.target.parentNode.parentNode).css('background-color','#c9cbcf');	
			        }else{
			          $(e.target.parentNode.parentNode).css('background-color','#ff0000');
			          alert("paid_successfully: "+result.paid_successfully+"\r\n"+"email_sent_successfully: "+result.email_sent_successfully);
			        }			        
			    },
			    error: function( jqXhr, textStatus, errorThrown ){
			        console.log( errorThrown );
			    }
			  
			});
	  } );
	  
	  $('[remove-reason-button="true"]').click(function(e) {	  
		  e.preventDefault();
		  var key = $(this).data('entitykey');
		 
		  $('#removalReasonDialog').modal('show');	 
		  //disconnect event handler
		  $('#saveRemovalReasonBtn').off();
		  $('#saveRemovalReasonBtn').on( "click", function() {			  

			  var formData={
					  reason:$("#removeReasonId").val(),
					  key:key,
			  };
			  
			  $.ajax({
			    url: '/administration/payment/removal/reason',
			    type: 'post',
			    data:formData,	    
			    success: function(data, textStatus, jQxhr ){
			        console.log(data);
			    },
			    error: function( jqXhr, textStatus, errorThrown ){
			        console.log( errorThrown );
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