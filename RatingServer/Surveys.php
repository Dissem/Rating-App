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

	    $stmt = $mysql->prepare("select title, description, image_url from Survey where id=?");
	    $stmt->bind_param("s", $id);
		$survey->surveyId = $id;
	    $stmt->bind_result($survey->title, $survey->description, $survey->imageUrl);
	    $stmt->execute();
	    $stmt->fetch();
	    $stmt->close();

	    $stmt = $mysql->prepare("select item_id, title, description, image_url, rating, votes from Item where survey_id=?");
		$stmt->bind_param("s", $id);
	    $stmt->bind_result($itemId, $title, $description, $imageUrl, $rating, $votes);
	    $stmt->execute();

	    $items = array();
	    while ($stmt->fetch()) {
	      $item = new stdClass();
          $item->surveyId = $id;
	      $item->itemId = $itemId;
	      $item->title = $title;
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
}
