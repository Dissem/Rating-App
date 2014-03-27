<?php
class Items
{
    /**
     * @param string $id
     *
     * @return null|stdClass
     * @url GET surveys/{surveyId}/items/{itemId}
     * @url GET survey/{surveyId}/item/{itemId}
     */
    function get($surveyId, $itemId)
    {
		mysqli_report(MYSQLI_REPORT_ERROR);

	    require 'config.php';

    	$mysql =  new mysqli($mysql_server, $mysql_user, $mysql_password);
		$mysql->select_db($mysql_db);
		$mysql->set_charset('utf8');

	    $stmt = $mysql->prepare("select title, subtitle, description, image_url, rating, votes from Item where survey_id=? and item_id=?");
		$stmt->bind_param("si", $surveyId, $itemId);
	    $stmt->bind_result($title, $subtitle, $description, $imageUrl, $rating, $votes);
	    $stmt->execute();

		if (!$stmt->fetch()) {
			throw new RestException(400, 'Item not found');
		}
		$item = new stdClass();
		$item->surveyId = $surveyId;
		$item->itemId = $itemId;
		$item->title = $title;
		$item->subtitle = $subtitle;
		$item->description = $description;
		$item->imageUrl = $imageUrl;
		$item->rating = $rating;
		$item->votes = $votes;

	    $stmt->close();

        return $item;
    }

    /**
     * @param string $id
     * @param int $rating
     *
     * @return double
     * @url POST surveys/{surveyId}/items/{itemId}/rate
     * @url POST survey/{surveyId}/item/{itemId}/rate
     */
    function rate($surveyId, $itemId, $data)
    {
		mysqli_report(MYSQLI_REPORT_ERROR);

	    require 'config.php';

    	$mysql =  new mysqli($mysql_server, $mysql_user, $mysql_password);
		$mysql->select_db($mysql_db);
		$mysql->set_charset('utf8');

	    $stmt = $mysql->prepare("select count(*) from Vote where survey_id=? and item_id=? and user_id=?");
	    $stmt->bind_param("sis", $surveyId, $itemId, $data['userId']);
	    $stmt->bind_result($count);
	    $stmt->execute();
	    $stmt->fetch();
        $stmt->close();

		if ($count > 0) {
			throw new RestException(400, 'User already voted');
        }

	    $stmt = $mysql->prepare("update Item set rating=(((votes*rating)+?)/(votes+1)), votes=(votes+1) where survey_id=? and item_id=?");
	    $stmt->bind_param("dsi", $data['rating'], $surveyId, $itemId);
	    $stmt->execute();
	    $stmt->close();

	    $stmt = $mysql->prepare("insert into Vote values (?, ?, ?)");
	    $stmt->bind_param("sis", $surveyId, $itemId, $data['userId']);
	    $stmt->execute();
	    $stmt->close();

        return $this->get($surveyId, $itemId);
    }
}
