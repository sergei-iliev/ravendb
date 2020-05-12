
var payment = {};
payment.PaidUsersGroupByView = Backbone.View.extend({

  el: '#contentid',
  // It's the first function called when this view it's instantiated.
  initialize: function(){
	  $('.form-check-input.timegroup').click(function() {
		  $('.form-check-input.timegroup').not(this).prop('checked', false);
	   });
	  
	  $('.form-check-input.localegroup').click(function() {
		  $('.form-check-input.localegroup').not(this).prop('checked', false);
	   });
  },
  events: {
	  "submit #search-general-form":"onSubmit",
      "click  #add-btn" : "onAdd",	
	  "click  #remove-btn" : "onRemove"	
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
  render: function(){
	  
    //this.$el.html("Hello World");
  }
});