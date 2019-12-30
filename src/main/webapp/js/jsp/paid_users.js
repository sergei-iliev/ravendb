
var payment = {};
payment.PaidUsersView = Backbone.View.extend({
  // el - stands for element. Every view has a element associate in with HTML
  //      content will be rendered.
  el: '#contentid',
  // It's the first function called when this view it's instantiated.
  initialize: function(){
		$('#filterTabId a').on('click', function (e) {		
			  e.preventDefault();
			  $("table").remove("#searchResultTable");
			  $(this).tab('show')
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