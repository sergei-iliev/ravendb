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
	 $('#all_experiments_id').change(function() {
	        if(this.checked) {	        	
	        	$('#experimentsId').attr('disabled', 'disabled');
	        	$('#experimentId').attr('disabled', 'disabled');
	        	$('#add-btn').attr('disabled', 'disabled');
	        	$('#remove-btn').attr('disabled', 'disabled');
	        }else{
	        	$('#experimentsId').removeAttr('disabled');
	        	$('#experimentId').removeAttr('disabled');
	        	$('#add-btn').removeAttr('disabled');
	        	$('#remove-btn').removeAttr('disabled');
	        }	                
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
	 
	 if($('#all_experiments_id').is(":checked")){
     	$('#experimentsId').attr('disabled', 'disabled');
    	$('#experimentId').attr('disabled', 'disabled');
    	$('#add-btn').attr('disabled', 'disabled');
    	$('#remove-btn').attr('disabled', 'disabled');
	 }
});