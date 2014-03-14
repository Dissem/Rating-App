package ch.bfh.mobilecomputing.fs2014.ratingapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
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
	public static final String ARG_ITEM_ID = "item_id";

	private String surveyId = "test"; // TODO
	private int itemId;
	private Item item;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public ItemDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

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
			SurveyRepository.getInstance().getSurvey(surveyId,
					new RepositoryCallback<Survey>() {
						@Override
						public void onReceived(Survey entity) {
							item = entity.getItem(itemId);
							showItem(rootView);
						}

						@Override
						public void onError(Exception e) {
							// TODO Auto-generated method stub
						}
					});
		}

		return rootView;
	}

	private void showItem(View rootView) {
		if (item != null) {
			((TextView) rootView.findViewById(R.id.title)).setText(item
					.getTitle());
			Utils.setImage(((ImageView) rootView.findViewById(R.id.item_logo)),
					item.getImage());
			if (item.getDescription() != null) {
				((TextView) rootView.findViewById(R.id.detail_text))
						.setText(item.getDescription());
			}
		}
	}
}
