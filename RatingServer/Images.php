<?php
class Images
{
    /**
     * @param string $name
     */
    function get($name)
    {
		$type = exif_imagetype("data/$name");
		if (!$type)
			throw new RestException(404, "Image $name was not found");
		
		header("Cache-Control: public, max-age=31536000"); // I give it a year
		header("Content-Type: $type");
		$extension = substr(strrchr($type, "/"), 1);
		header("Content-Disposition: inline; filename=\"$name.$extension\"");

		readfile("data/$name");
	}

 	/**
     * @access protected
     * @class  AccessControl {@requires admin}
     */
	function post($request_data = NULL) {
		if (!isset($request_data['fileUpload']))
			throw new RestException(400, "The field fileUpload doesn't exist, there's something wrong with the sent data.");

		$image = $request_data['fileUpload'];

		$name = uniqid();

		if (!move_uploaded_file($image['tmp_name'], "data/$name"))
			throw new RestException(500, "There was a problem with the uploaded file, please try again.");

		return array("name" => $name);
	}
}