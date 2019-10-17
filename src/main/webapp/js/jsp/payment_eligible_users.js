var payment = {};
payment.PaymentEligibleUsersView = Backbone.View.extend({
  // el - stands for element. Every view has a element associate in with HTML
  //      content will be rendered.
  el: '#contentid',
  // It's the first function called when this view it's instantiated.
  initialize: function(){
	  $('[data-toggle="tooltip"]').tooltip();	  
  },
  events: {
	  "submit #search-form":"onSubmit",
      "click  #add-btn" : "onAdd",	
	  "click  #remove-btn" : "onRemove",
	  "click  #save-template-btn" : "onSaveTemplate",
	  "click  #load-template-btn" : "onLoadTemplate",
	 // "click  #saveSearchTemplateBtn" :"onSaveSearchTemplate"
  },
//  onSaveSearchTemplate:function(e){
//	  var formData = {name:"ravi",age:"31"};
//	  $.ajax({
//		    url: '/administration/search/template',
//		    type: 'post',
//		    data:formData,		    
//		    success: function( data, textStatus, jQxhr ){
//		        console.log(data);
//		    },
//		    error: function( jqXhr, textStatus, errorThrown ){
//		        console.log( errorThrown );
//		    }
//		});
//
//  },
//	toJSONString:function(form) {
//		var obj = {};
//		var elements = form.querySelectorAll( "input, select, textarea" );
//		for( var i = 0; i < elements.length; ++i ) {
//			var element = elements[i];
//			var name = element.name;
//			var value = element.value;
//
//			if( name ) {
//				obj[ name ] = value;
//			}
//		}
//
//		return JSON.stringify( obj );
//  },
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