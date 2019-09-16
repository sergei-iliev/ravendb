jQuery(document).ready(function(){ 
	
	 $("#add-btn").click(function(e){
		 e.preventDefault();
		 var option=$('#experimentId').val();
	       $('#experimentsId').append($('<option>', {
	    	    value: option,
	    	    text: option
	    	}));
	 });
	 $("#remove-btn").click(function(e){
		 e.preventDefault();
		 $('#experimentsId').find('option:selected').remove();		 
	 });
	 $("#p-add-btn").click(function(e){
		 e.preventDefault();
		 var option=$('#packageNameId').val();
	       $('#packageNamesId').append($('<option>', {
	    	    value: option,
	    	    text: option
	    	}));
	 });
	 $("#p-remove-btn").click(function(e){
		 e.preventDefault();
		 $('#packageNamesId').find('option:selected').remove();		 
	 });	 
	 $('#search-form').on('submit', function(e) { 
	       // e.preventDefault();  
	        $("#experimentsId option").prop("selected", "selected");
	        //$("#packageNamesId option").prop("selected", "selected");
	        
	 });
});