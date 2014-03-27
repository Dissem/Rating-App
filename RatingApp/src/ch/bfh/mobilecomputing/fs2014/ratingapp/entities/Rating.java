package ch.bfh.mobilecomputing.fs2014.ratingapp.entities;

/**
 * Class needed for rating-entries in SQLite-DB
 *
 */
public class Rating {
	private String surveyId;
	private int itemId;
	
	public Rating(String surveyId, int itemId) {
		this.surveyId = surveyId;
		this.itemId = itemId;
	}
	
	public String getSurveyId() {
		return surveyId;
	}
	public void setSurveyId(String surveyId) {
		this.surveyId = surveyId;
	}
	public int getItemId() {
		return itemId;
	}
	public void setItemId(int itemId) {
		this.itemId = itemId;
	}
	
}
