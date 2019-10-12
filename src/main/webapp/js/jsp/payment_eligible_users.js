var payment = {};
payment.PaymentEligibleUsersView = Backbone.View.extend({
  // el - stands for element. Every view has a element associate in with HTML
  //      content will be rendered.
  el: '#contentid',
  // It's the first function called when this view it's instantiated.
  initialize: function(){
    
  },
  events: {
	  "submit #search-form":"onSubmit",
      "click  #add-btn" : "onAdd",	
	  "click  #remove-btn" : "onRemove",
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