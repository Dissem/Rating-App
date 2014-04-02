package ch.bfh.mobilecomputing.fs2014.ratingapp.entities;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;

public class SurveyRepository {
	private static SurveyRepository INSTANCE;

	static final String RATING_API_URI = "https://ratingapi.dissem.ch";

	private static ContentResolver contentResolver;
	private static SharedPreferences sharedPref;
	private static String surveyId;
	private static CACertHttpsHelper https;
	private static Map<String, Survey> cache = new HashMap<String, Survey>();

	private SurveyRepository(Activity activity) {
		sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
		contentResolver = activity.getContentResolver();
		https = new CACertHttpsHelper(activity);
	}

	public static synchronized void init(Activity activity) {
		if (INSTANCE == null)
			INSTANCE = new SurveyRepository(activity);
	}

	public void requestSurvey(final String id,
			final RepositoryCallback<Survey> callback, CallbackMode mode) {
		boolean cacheSuccessful = cache.containsKey(id);
		if (mode != CallbackMode.FRESH_ONLY && cacheSuccessful)
			callback.onReceived(cache.get(id));

		switch (mode) {
		case CACHE_ONLY:
			break;
		case CACHED:
			if (cacheSuccessful)
				break;
		case BOTH:
		case FRESH_ONLY:
			requestSurvey(id, callback);
		}

	}

	private void requestSurvey(final String id,
			final RepositoryCallback<Survey> callback) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					URL url = new URL(RATING_API_URI + "/surveys/" + id);
					BufferedReader in = new BufferedReader(
							new InputStreamReader(https.inputStream(url)));

					StringBuilder json = new StringBuilder();
					String inputLine;
					while ((inputLine = in.readLine()) != null)
						json.append(inputLine);
					in.close();

					final JSONObject data = new JSONObject(json.toString());
					final Survey survey = new Survey(data);
					cache.put(survey.getId(), survey);

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

	public void writeItemRating(final Context context, final String surveyId,
			final int itemId, final double rating,
			final RepositoryCallback<String> callback) {
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

					// Get response before the rating is saved to the DB, so if
					// it fails, the user will be able to rate again.
					final String response = https.post(url, data.toString());

					DatabaseConnector.getInstance().open();
					Rating rating = new Rating(surveyId, itemId);
					DatabaseConnector.getInstance().createRating(rating);
					DatabaseConnector.getInstance().close();

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

	public enum CallbackMode {
		/** Only display data from the server */
		FRESH_ONLY,
		/**
		 * Only display cached data. If the object isn't in the cache, ignore
		 * the call
		 */
		CACHE_ONLY,
		/**
		 * Only display cached data if it is available. Otherwise, load from
		 * server
		 */
		CACHED,
		/**
		 * Call with cached data if possible. Call again when fresh data is
		 * available.
		 */
		BOTH;
	}

	public static SurveyRepository getInstance() {
		return INSTANCE;
	}
}
