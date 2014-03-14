package ch.bfh.mobilecomputing.fs2014.ratingapp.entities;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import org.json.JSONObject;

import android.os.Handler;
import android.os.Looper;

public class SurveyRepository {
	private static SurveyRepository INSTANCE = new SurveyRepository();

	private static String RATING_API_URI = "http://ratingapi.dissem.ch";

	private SurveyRepository() {
		// use getInstance()
	}

	public void getSurvey(final String id,
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

	public static interface RepositoryCallback<E> {
		public void onReceived(E entity);

		public void onError(Exception e);
	}

	public static SurveyRepository getInstance() {
		return INSTANCE;
	}
}
