package ch.bfh.mobilecomputing.fs2014.ratingapp.entities;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings.Secure;

public class SurveyRepository {
	private static SurveyRepository INSTANCE;

	private static String RATING_API_URI = "http://ratingapi.dissem.ch";

	private static ContentResolver contentResolver;
	private static SharedPreferences sharedPref;
	private static String surveyId;

	private SurveyRepository(Activity activity) {
		sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
		contentResolver = activity.getContentResolver();
	}

	public static synchronized void init(Activity activity) {
		if (INSTANCE == null)
			INSTANCE = new SurveyRepository(activity);
	}

	public void requestSurvey(final String id,
			final RepositoryCallback<Survey> callback) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					URL url = new URL(RATING_API_URI + "/surveys/" + id);
					BufferedReader in = new BufferedReader(
							new InputStreamReader(url.openStream()));

					StringBuilder json = new StringBuilder();
					String inputLine;
					while ((inputLine = in.readLine()) != null)
						json.append(inputLine);
					in.close();

					final JSONObject data = new JSONObject(json.toString());
					final Survey survey = new Survey(data);

					new Handler(Looper.getMainLooper()).post(new Runnable() {
						@Override
						public void run() {
							callback.onReceived(survey);
						}
					});
				} catch (final Exception e) {
					new Handler(Looper.getMainLooper()).post(new Runnable() {
						@Override
						public void run() {
							callback.onError(e);
						}
					});
				}
			}
		}).start();
	}

	public String getSurveyId() {
		if (surveyId == null) {
			surveyId = sharedPref.getString("surveyId", null);
		}
		return surveyId;
	}

	public void setSurveyId(String surveyId) {
		Editor editor = sharedPref.edit();
		editor.putString("surveyId", surveyId);
		editor.commit();
		SurveyRepository.surveyId = surveyId;
	}

	public void writeItemRating(final Context context, final String surveyId, final int itemId, final double rating, final RepositoryCallback<String> callback) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					URL url = new URL(RATING_API_URI + "/surveys/" + surveyId
							+ "/items/" + itemId + "/rate");
					
					JSONObject data = new JSONObject();
					JSONObject item = new JSONObject();
					item.put("rating", rating);
					item.put("userId", getAndroidId());
					data.put("data", item);

					DefaultHttpClient httpclient = new DefaultHttpClient();
					HttpPost httpPost = new HttpPost(url.toString());
					StringEntity jsonEntity = new
					StringEntity(data.toString());
					httpPost.setEntity(jsonEntity);
					
					httpPost.setHeader("Accept", "application/json");
					httpPost.setHeader("Content-type", "application/json");
					
					DatabaseConnector.getInstance().open();
					Rating rating = new Rating(surveyId,itemId);
					DatabaseConnector.getInstance().createRating(rating);
					DatabaseConnector.getInstance().close();
					
					final String response = httpclient.execute(httpPost, new BasicResponseHandler());
					
					new Handler(Looper.getMainLooper()).post(new Runnable() {
						@Override
						public void run() {
							callback.onReceived(response);
						}
					});
				} catch (final Exception e) {
					new Handler(Looper.getMainLooper()).post(new Runnable() {
						@Override
						public void run() {
							callback.onError(e);
						}
					});
				}
			}
		}).start();
	}

	public String getAndroidId() {
		return Secure.getString(contentResolver, Secure.ANDROID_ID);
	}

	public interface RepositoryCallback<E> {
		public void onReceived(E entity);

		public void onError(Exception e);
	}

	public static SurveyRepository getInstance() {
		return INSTANCE;
	}
}
