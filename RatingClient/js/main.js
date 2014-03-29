$(document).ready(function() {
	// Configuration
	var apiurl = $.cookie('rating_apiurl') || 'https://ratingapi.dissem.ch';
	var apisecret = $.cookie('rating_apisecret') || '67890';

	// Initialization:
	var items = [];
	$('#api-secret').val(apisecret);
	$('#api-secret').hover(function(){
		$('#api-secret').attr('type', 'text');
	}, function(){
		$('#api-secret').attr('type', 'password');
	});
	$('#api-secret').blur(function(){
		$.cookie('rating_apisecret', $('#api-secret').val());
	});

	$('#api-url').val(apiurl);
	$('#api-url').blur(function(){
		$.cookie('rating_apiurl', $('#api-url').val());
	});

	// And now for some action:
	$('#form-survey').submit(function(e) {
		// Don't actually submit, JavaScript will handle this
		// A 'pre-submit' should happen though, so the fields
		// will be validated.
		return false;
	});

	$('#btnAddItem').click(addItem);

	function addItem() {
		if (!$('#form-survey')[0].checkValidity())
			return true;

		var imageFile = $('#item-image')[0].files[0];

		var item = {
			surveyId: $('#survey-id').val(),
			itemId: (items.length+1),
			title: $('#item-title').val(),
			subtitle: $('#item-subtitle').val(),
			description: $('#item-description').val(),
			imageUrl: imageFile,
			rating: 0,
			votes: 0
		};
		items.push(item);
		$('#configured-items').append(itemDOM(item, imageFile));

		$('#item-title').val('');
		$('#item-subtitle').val('');
		$('#item-description').val('');
		return false;
	}

	function itemDOM(item, imageFile) {

		var div = $('<div class="row col-sm-10 col-sm-offset-2 item bg-info">');
		div.append($('<div class="col-xs-1 text-right">'+item.itemId+'</div>'));
		var divRight = $('<div class="col-xs-11">');
		divRight.append(imgDOM(imageFile));
		divRight.append($('<p>'+item.title+' <span class="text-muted">'+item.subtitle+'</span></p>'))
		divRight.append($('<p class="text-info">'+item.subtitle+'</p>'))
		div.append(divRight);
		return div;
	}

	function imgDOM(imageFile) {
		if (imageFile) {
	        var reader = new FileReader();

	        var img = $('<img src="#" class="item-image img-rounded">');
	        reader.onload = function (e) {
	            img.attr('src', e.target.result);
	        }

	        reader.readAsDataURL(imageFile);
	        return img;
	    }
		return false;
	}

	$('#btnCreateSurvey').click(function(){
		apisecret = $('#api-secret').val();

		if ($('#item-title').val() != '') {
			bootbox.dialog({
				message: "There is still text in the item dialog, do you want to create an item?",
				// title: "Custom title",
				buttons: {
					tes: {
						label: "Yes",
						className: "btn-default",
						callback: function() {
							addItem();
							submitSurvey();
						}
					},
					no: {
						label: "No",
						className: "btn-default",
						callback: function() {
							submitSurvey();
						}
					},
					cancel: {
						label: "Let me look at it again",
						className: "btn-primary",
					}
				}
			});
		} else {
			bootbox.confirm("Are you sure?", function(result) {
				submitSurvey();
			});
		}
	});

	function submitSurvey() {
		uploadDeferred = $.Deferred();
		uploadDeferred.imagesTODO = 0;
		uploadDeferred.imagesDone = 0;
		uploadDeferred.imagesError = 0;

		$(items).each(function(){
			uploadImage(this);
		});
		var survey = {
			surveyId: $('#survey-id').val(),
			title: $('#survey-title').val(),
			subtitle: $('#survey-subtitle').val(),
			description: $('#survey-description').val(),
			imageUrl: $('#survey-image')[0].files[0],
			items: items
		}
		uploadImage(survey);

		uploadDeferred.done(function(){
			$.ajax({
				url: apiurl+'/surveys/?secret='+apisecret,
				type: 'post',
				data: survey,
				success: function(){
					bootbox.alert('Survey successfully submitted');
				},
				error: function(data){
					bootbox.alert('There was an error submitting the survey: '+data);
					console.log(arguments);
				}
			});
			console.log(survey);
		});
	}

	var uploadDeferred;

	function uploadImage(item) {
		if (!item.imageUrl)
			return;

		uploadDeferred.imagesTODO++;
		var fd = new FormData();

		// HTML file input user's choice...
		fd.append("fileUpload", item.imageUrl);

		$.ajax({
			url: apiurl+'/images/?secret='+apisecret,
			type: 'POST',
			data: fd,
			processData: false,  // tell jQuery not to process the data
			contentType: false,   // tell jQuery not to set contentType
			success: function(data){
				item.imageUrl = apiurl+'/images/'+data.name;
				// bootbox.alert(item.imageUrl);
				deferred(true);
			},
			error: function(data){
				// bootbox.alert('Image upload failed for item '+item.title+', cause: '+data);
				console.log(arguments);
				deferred(false);
			}
		});

		function deferred(success) {
			if (success)
				uploadDeferred.imagesDone++;
			else
				uploadDeferred.imagesError++;
			uploadDeferred.imagesTODO--;
			if (uploadDeferred.imagesTODO == 0) {
				if (uploadDeferred.imagesError>0){
					uploadDeferred.reject();
				} else {
					uploadDeferred.resolve();
				}
			}
		}
	}

});