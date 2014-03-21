package ch.bfh.mobilecomputing.fs2014.ratingapp;

import java.lang.reflect.Method;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import ch.bfh.mobilecomputing.fs2014.ratingapp.entities.Survey;
import ch.bfh.mobilecomputing.fs2014.ratingapp.entities.Survey.Item;
import ch.bfh.mobilecomputing.fs2014.ratingapp.entities.SurveyRepository;
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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_item_detail,
				container, false);

		if (getArguments().containsKey(ARG_ITEM_ID)) {
			// Load the dummy content specified by the fragment
			// arguments. In a real-world scenario, use a Loader
			// to load content from a content provider.
			SurveyRepository.getInstance().requestSurvey(surveyId,
					new RepositoryCallback<Survey>() {
						@Override
						public void onReceived(Survey entity) {
							item = entity.getItem(itemId);
							showItem(rootView);
						}

						@Override
						public void onError(Exception e) {
							Utils.showToast(getActivity(),
									R.string.survey_load_error,
									Toast.LENGTH_LONG);
						}
					});
		}
		
		Button rateButton = (Button) rootView.findViewById(R.id.rate_button);
		rateButton.setOnClickListener(rateHandler);
		
		ratingBar = (RatingBar) rootView.findViewById(R.id.rating_bar);
		 
		return rootView;
	}
	
	/**
	 * OnClickListener which is called when the "Rate Item"-Button was clicked
	 */
	View.OnClickListener rateHandler = new View.OnClickListener() {
	    public void onClick(View v) {
	    	
	    	final double rating = (double)ratingBar.getRating();
	    	if (rating == 0) {
	    		Utils.showToast(getActivity(),
						R.string.rate_zero_error,
						Toast.LENGTH_LONG);
	    	} else {
	    		SurveyRepository.getInstance().writeItemRating(surveyId, itemId, rating);
	    	}
			
	    }
	  };

	private void showItem(View rootView) {
		if (item != null) {
			((TextView) rootView.findViewById(R.id.title)).setText(item
					.getTitle());
			if (item.getImage() != null) {
				Utils.setImage(
						((ImageView) rootView.findViewById(R.id.item_logo)),
						item.getImage());
			}
			if (item.getDescription() != null) {
				((TextView) rootView.findViewById(R.id.detail_text))
						.setText(item.getDescription());
			}
		}
	}
	
	
	
}
