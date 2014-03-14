package ch.bfh.mobilecomputing.fs2014.ratingapp.entities;

import org.json.JSONException;
import org.json.JSONObject;

public class SurveyRepository {
	public Survey getSurvey(String id) throws JSONException {
		JSONObject data = new JSONObject(
				"{ \"surveyId\": \"test\", \"title\": \"A Test Survey\", \"description\": null, \"imageUrl\": null, \"items\": [ { \"surveyId\": \"test\", \"itemId\": 1, \"title\": \"How's that?\", \"description\": \"... for a first item to rate?\", \"imageUrl\": null, \"rating\": \"4.800\", \"votes\": 6 }, { \"surveyId\": \"test\", \"itemId\": 2, \"title\": \"And now?\", \"description\": \"How about this second item?\", \"imageUrl\": null, \"rating\": \"0.000\", \"votes\": 0 }, { \"surveyId\": \"test\", \"itemId\": 3, \"title\": \"One with a picture\", \"description\": \"\", \"imageUrl\": \"http://ratingapi.dissem.ch/images/androidplaceholder\", \"rating\": \"0.000\", \"votes\": 0 } ] }");
		return new Survey(data);
	}
}
