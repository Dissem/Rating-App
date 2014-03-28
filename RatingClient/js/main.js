$(document).ready(function() {
	var items = [];

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
		var div = $('<div class="row">');
		div.append($('<div class="col-xs-1 text-right">'+item.itemId+'</div>'));
		var divRight = $('<div class="col-xs-11">');
		divRight.append(imgDOM(imageFile));
		divRight.append($('<p>'+item.title+'</p>'))
		divRight.append($('<p class="text-muted">'+item.description+'</p>'))
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
						callback: function() {
							// Example.show("great success");
						}
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
		$(items).each(function(){
			this.imageUrl = uploadImage(this.imageUrl);
		});
		var survey = {
			surveyId: $('#survey-id').val(),
			title: $('#survey-title').val(),
			subtitle: $('#survey-subtitle').val(),
			description: $('#survey-description').val(),
			imageUrl: uploadImage($('#survey-image')[0].files[0]),
			items: items
		}

		$.ajax({
			url: 'http://localhost/ratingapp/RatingServer/surveys/?secret=67890',
			type: 'post',
			data: survey,
			success: function(){
				bootbox.alert('Survey successfully submitted');
			},
			error: function(){
				bootbox.alert('There was an error submitting the survey');
				console.log(arguments);
			}
		});
		console.log(survey);
	}

	function uploadImage(file) {
		if (!file)
			return null;

		return null; // TODO
	}

});