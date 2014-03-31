package ch.bfh.mobilecomputing.fs2014.ratingapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import ch.bfh.mobilecomputing.fs2014.ratingapp.entities.DatabaseConnector;
import ch.bfh.mobilecomputing.fs2014.ratingapp.entities.Rating;
import ch.bfh.mobilecomputing.fs2014.ratingapp.entities.Survey;
import ch.bfh.mobilecomputing.fs2014.ratingapp.entities.Survey.Item;
import ch.bfh.mobilecomputing.fs2014.ratingapp.entities.SurveyRepository;
import ch.bfh.mobilecomputing.fs2014.ratingapp.entities.SurveyRepository.CallbackMode;
import ch.bfh.mobilecomputing.fs2014.ratingapp.entities.SurveyRepository.RepositoryCallback;

/**
 * A fragment representing a single Item detail screen. This fragment is either
 * contained in a {@link ItemListActivity} in two-pane mode (on tablets) or a
 * {@link ItemDetailActivity} on handsets.
 */
public class ItemDetailFragment extends Fragment {

	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_SURVEY_ID = "survey_id";
	public static final String ARG_ITEM_ID = "item_id";

	private String surveyId;
	private int itemId;
	private Item item;

	private RatingBar ratingBar;
	private View rootView;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public ItemDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments().containsKey(ARG_SURVEY_ID)) {
			surveyId = getArguments().getString(ARG_SURVEY_ID);
		}
		if (getArguments().containsKey(ARG_ITEM_ID)) {
			itemId = getArguments().getInt(ARG_ITEM_ID);
		}
	}

	RepositoryCallback<Survey> surveyRepoCallback = new RepositoryCallback<Survey>() {
		@Override
		public void onReceived(Survey entity) {
			item = entity.getItem(itemId);
			showItem();
		}

		@Override
		public void onError(Exception e) {
			Utils.showToast(getActivity(), R.string.survey_load_error,
					Toast.LENGTH_LONG);
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_item_detail, container,
				false);

		if (getArguments().containsKey(ARG_ITEM_ID)) {
			// Load the survey and their items via REST-Service
			SurveyRepository.getInstance().requestSurvey(surveyId,
					surveyRepoCallback, CallbackMode.CACHED);
		}
		return rootView;
	}

	/**
	 * OnClickListener which is called when the "Rate"-Button was clicked
	 */
	View.OnClickListener rateHandler = new View.OnClickListener() {
		public void onClick(final View v) {
			final double rating = (double) ratingBar.getRating();

			if (rating == 0) {
				Utils.showToast(getActivity(), R.string.item_rate_zero_error,
						Toast.LENGTH_LONG);
			} else {
				// Write the item rating via. REST-Service
				SurveyRepository.getInstance().writeItemRating(v.getContext(),
						surveyId, itemId, rating,
						new RepositoryCallback<String>() {
							@Override
							public void onReceived(String response) {
								// Calculate new Average Rating
								double oldRating = item.getRating();
								double newRating = Math
										.round(((oldRating * item.getVotes()) + rating)
												/ (item.getVotes() + 1) * 1000) / 1000.0;

								displayRatingElementsAfterRating(newRating, item.getVotes() + 1);
								Utils.showToast(getActivity(),
										R.string.item_rate_success,
										Toast.LENGTH_LONG);
							}

							@Override
							public void onError(Exception e) {
								Utils.showToast(getActivity(),
										R.string.item_rate_error,
										Toast.LENGTH_LONG);
							}
						});
			}
		}
	};

	private void showItem() {
		if (item != null) {
			TextView title = (TextView) rootView.findViewById(R.id.title);
			title.setText(item.getTitle());
			title.setVisibility(View.VISIBLE);

			if (item.getImage() != null) {
				ImageView image = (ImageView) rootView
						.findViewById(R.id.item_logo);
				ProgressBar loadImage = (ProgressBar) rootView.findViewById(R.id.progress_bar);
				
				Utils.setImage(image, loadImage, item.getImage());
			}

			if (item.getDescription() != null) {
				TextView detail = (TextView) rootView
						.findViewById(R.id.detail_text);
				detail.setText(item.getDescription());
				detail.setVisibility(View.VISIBLE);
			}

			ratingBar = (RatingBar) rootView.findViewById(R.id.rating_bar);

			// If User has rated the item
			Rating rating = new Rating(surveyId, itemId);
			DatabaseConnector.getInstance().open();
			if (DatabaseConnector.getInstance().isRatingExist(rating)) {
				displayRatingElementsAfterRating(item.getRating(), item.getVotes());
			} else {
				Button rateButton = (Button) rootView
						.findViewById(R.id.rate_button);
				rateButton.setOnClickListener(rateHandler);
				rateButton.setVisibility(View.VISIBLE);
			}
			ratingBar.setVisibility(View.VISIBLE);
			DatabaseConnector.getInstance().close();
		}
	}

	private void displayRatingElementsAfterRating(double rating, int votes) {
		String averageRating = " " + rating;

		Button rateButton = (Button) rootView.findViewById(R.id.rate_button);
		rateButton.setVisibility(View.INVISIBLE);

		TextView ratingText = (TextView) rootView
				.findViewById(R.id.rating_text);
		
		String votesText = "";
		if (votes == 1) {
			votesText = " (" + votes + " " + getString(R.string.vote)+")";
		} else {
			votesText = " (" + votes + " " + getString(R.string.votes)+")";
		}
		
		ratingText.setText(getString(R.string.text_already_rated)
				+ averageRating + votesText);

		ratingBar.setEnabled(false);
		ratingBar.setRating((float) rating);
	}
}
