package ch.bfh.mobilecomputing.fs2014.ratingapp;

import org.json.JSONException;

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

	private Survey survey;
	private Item item;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public ItemDetailFragment() {
		try {
			survey = new SurveyRepository().getSurvey("test"); // FIXME
		} catch (JSONException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments().containsKey(ARG_ITEM_ID)) {
			// Load the dummy content specified by the fragment
			// arguments. In a real-world scenario, use a Loader
			// to load content from a content provider.
			item = survey.getItem(getArguments().getInt(ARG_ITEM_ID));
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_item_detail,
				container, false);

		// Show the dummy content as text in a TextView.
		if (item != null) {
			((TextView) rootView.findViewById(R.id.title)).setText(item
					.getTitle());
			Utils.setImage(((ImageView) rootView.findViewById(R.id.item_logo)),
					item.getImage());
			((TextView) rootView.findViewById(R.id.detail_text)).setText(item
					.getDescription());
		}

		return rootView;
	}
}
