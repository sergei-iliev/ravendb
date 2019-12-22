
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
	
  },

  render: function(){
	  
    //this.$el.html("Hello World");
  }
});