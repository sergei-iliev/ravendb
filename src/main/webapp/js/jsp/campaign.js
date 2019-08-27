jQuery(document).ready(function(){ 
	
	 $("#add-btn").click(function(e){
		 e.preventDefault();
		 var option=$('#adnetworkId').val();
	       $('#adnetworksId').append($('<option>', {
	    	    value: option,
	    	    text: option
	    	}));
	 });
	 $("#remove-btn").click(function(e){
		 e.preventDefault();
		 $('#adnetworksId').find('option:selected').remove();		 
	 });
	 
	 $("#add-btn-campaign").click(function(e){
		 e.preventDefault();
		 var option=$('#campaignId').val();
	       $('#campaignsId').append($('<option>', {
	    	    value: option,
	    	    text: option
	    	}));
	 });
	 $("#remove-btn-campaign").click(function(e){
		 e.preventDefault();
		 $('#campaignsId').find('option:selected').remove();		 
	 });
	 $("#add-btn-source").click(function(e){
		 e.preventDefault();
		 var option=$('#sourceId').val();
	       $('#sourcesId').append($('<option>', {
	    	    value: option,
	    	    text: option
	    	}));
	 });
	 $("#remove-btn-source").click(function(e){
		 e.preventDefault();
		 $('#sourcesId').find('option:selected').remove();		 
	 });
	 $('#search-form').on('submit', function(e) { 
	       // e.preventDefault();  
	        $("#sourcesId option").prop("selected", "selected");
	        $("#campaignsId option").prop("selected", "selected");
	        $("#adnetworksId option").prop("selected", "selected");
	        
	 });
});