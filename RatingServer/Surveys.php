<?php
class Surveys
{
    /**
     * @param string $id
     *
     * @return null|stdClass
     * @url GET surveys/{id}
     * @url GET survey/{id}
     */
    function get($id)
    {
		$survey = new stdClass();

		mysqli_report(MYSQLI_REPORT_ERROR);

	    require 'config.php';

    	$mysql =  new mysqli($mysql_server, $mysql_user, $mysql_password);
		$mysql->select_db($mysql_db);
		$mysql->set_charset('utf8');

	    $stmt = $mysql->prepare("select title, subtitle, description, image_url from Survey where id=?");
	    $stmt->bind_param("s", $id);
		$survey->surveyId = $id;
	    $stmt->bind_result($survey->title, $survey->subtitle, $survey->description, $survey->imageUrl);
	    $stmt->execute();
	    $stmt->fetch();
	    $stmt->close();

	    $stmt = $mysql->prepare("select item_id, title, subtitle, description, image_url, rating, votes from Item where survey_id=?");
		$stmt->bind_param("s", $id);
	    $stmt->bind_result($itemId, $title, $subtitle, $description, $imageUrl, $rating, $votes);
	    $stmt->execute();

	    $items = array();
	    while ($stmt->fetch()) {
	      $item = new stdClass();
          $item->surveyId = $id;
	      $item->itemId = $itemId;
	      $item->title = $title;
	      $item->subtitle = $subtitle;
	      $item->description = $description;
	      $item->imageUrl = $imageUrl;
	      $item->rating = $rating;
	      $item->votes = $votes;
	      $items[] = $item;
	    }
		$survey->items = $items;
	    $stmt->close();

        return $survey;
    }

    /**
     * @url POST surveys
     * @url POST survey
     *
     * @access protected
     * @class  AccessControl {@requires admin}
     */
    function post($request_data = NULL) {
    	return $this->_validate($request_data);
    }

    private function _validate($data)
    {
    	$survey = new stdClass();

        $survey->surveyId    = $this->_check($data, 'surveyId');
	    $survey->title       = $this->_check($data, 'title');
	    $survey->subtitle    = $data['subtitle'];
	    $survey->description = $data['description'];
	    $survey->imageUrl    = $data['imageUrl'];
	    $survey->items       = array();

	    foreach ($data['items'] as $i) {
	    	$item              = new stdClass();

	    	$item->surveyId    = $survey->surveyId;
	        $item->itemId      = $this->_check($i, 'itemId');
		    $item->title       = $this->_check($i, 'title');
		    $item->subtitle    = $i['subtitle'];
		    $item->description = $i['description'];
		    $item->imageUrl    = $i['imageUrl'];

		    $survey->items[]   = $item;
	    }

	    require 'config.php';

    	$mysql =  new mysqli($mysql_server, $mysql_user, $mysql_password);
		$mysql->select_db($mysql_db);
		$mysql->set_charset('utf8');

		$stmt = $mysql->prepare("select count(*) from Survey where id=?");
	    $stmt->bind_param("s", $survey->surveyId);
	    $stmt->bind_result($exists);
	    $stmt->execute();
	    $stmt->fetch();
	    $stmt->close();
	    if ($exists)
	    	throw new RestException(400, "A survey with id $survey->surveyId already exists");

	    $stmt = $mysql->prepare("insert into Survey (id, title, subtitle, description, image_url) values (?, ?, ?, ?, ?)");
	    $stmt->bind_param("sssss", $survey->surveyId, $survey->title, $survey->subtitle, $survey->description, $survey->imageUrl);
	    $stmt->execute();
	    $stmt->close();

	    foreach ($survey->items as $item) {
		    $stmt = $mysql->prepare("insert into Item (survey_id, item_id, title, subtitle, description, image_url) values (?, ?, ?, ?, ?, ?)");
		    $stmt->bind_param("ssssss", $survey->surveyId, $item->itemId, $item->title, $item->subtitle, $item->description, $item->imageUrl);
		    $stmt->execute();
		    $stmt->close();
	    }

        return $survey;
    }

    function _check($data, $field) {
        if (!isset($data[$field]))
            throw new RestException(400, "$field field missing");
        return $data[$field];
    }
}
