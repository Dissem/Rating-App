<?php
class Images
{
    /**
     * @param string $name
     */
    function get($name)
    {
		include 'config.php';
		
		$engine = 'mysql';
		
		$dns = $engine.':dbname='.$mysql_db.';host='.$mysql_server;
		$db = new PDO( $dns, $mysql_user, $mysql_password );
		
		$stmt = $db->prepare("select type, data from Image where name=?");
		$stmt->execute(array($name));
		$stmt->bindColumn(1, $type, PDO::PARAM_STR, 256);
		$stmt->bindColumn(2, $data, PDO::PARAM_LOB);
		$stmt->fetchAll(PDO::FETCH_BOUND);
		
		header("Cache-Control: public, max-age=31536000"); // I give it a year
		header("Content-Type: $type");
		$extension = substr(strrchr($type, "/"), 1);
		header("Content-Disposition: inline; filename=\"$name.$extension\"");

		echo($data);
	}

 	/**
     * @access protected
     * @class  AccessControl {@requires admin}
     */
	function post($request_data = NULL) {
		include 'config.php';

		$image = $request_data['fileUpload'];

		$name = uniqid();
		$data = fopen($image['tmp_name'], 'rb');

		$engine = 'mysql';
		
		$dns = $engine.':dbname='.$mysql_db.';host='.$mysql_server;
		$db = new PDO( $dns, $mysql_user, $mysql_password );
		
		$stmt = $db->prepare("insert into Image (name, type, data) values (?, ?, ?)");
		$stmt->execute(array($name, $image['type'], $data));

		return array("name" => $name);
	}
}