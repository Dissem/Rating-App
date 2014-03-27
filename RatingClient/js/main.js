$(document).ready(function() {
	var items = [];

	$('#form-survey').submit(function() {
		// Don't actually submit, JavaScript will handle this
		return false;
	});

	$('#btnAddItem').click(function() {
		if (!$('#form-survey')[0].checkValidity()) {
			return false;
		}

		var imageFile = $('#item-image')[0].files[0];

		var item = {
			surveyId: $('#survey-id').val(),
			itemId: (items.length+1),
			title: $('#item-title').val(),
			description: $('#item-description').val(),
			imageUrl: imageFile,
			rating: 0,
			votes: 0
		};
		items.push(item);
		$('#configured-items').append(itemDOM(item, imageFile));

		$('#item-title').val('');
		$('#item-description').val('');
	});

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
});