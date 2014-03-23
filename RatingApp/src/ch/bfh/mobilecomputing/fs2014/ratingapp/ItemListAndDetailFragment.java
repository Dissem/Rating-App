package ch.bfh.mobilecomputing.fs2014.ratingapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ch.bfh.mobilecomputing.fs2014.ratingapp.entities.Survey.Item;

/**
 * An activity representing a list of Items. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link ItemDetailActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link ItemListFragment} and the item details (if present) is a
 * {@link ItemDetailFragment}.
 * <p>
 * This activity also implements the required {@link ItemListFragment.Callbacks}
 * interface to listen for item selections.
 */
public class ItemListAndDetailFragment extends Fragment implements
		ItemListFragment.Callbacks {

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean twoPane;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// FIXME
		// // Create the detail fragment and add it to the activity
		// // using a fragment transaction.
		// Bundle arguments = new Bundle();
		// arguments.putString(ItemListFragment.ARG_SURVEY_ID, getIntent()
		// .getStringExtra(ItemListFragment.ARG_SURVEY_ID));
		// getFragmentManager().findFragmentById(R.id.item_list).setArguments(
		// arguments);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_item_list,
				container, false);
		ItemListFragment fragment = new ItemListFragment();
		getFragmentManager().beginTransaction().add(R.id.item_list, fragment)
				.commit();

		// The detail container view will be present only in the
		// large-screen layouts (res/values-large and
		// res/values-sw600dp). If this view is present, then the
		// activity should be in two-pane mode.
		twoPane = (rootView.findViewById(R.id.item_detail_container) != null);

		return rootView;
	}

	/**
	 * Callback method from {@link ItemListFragment.Callbacks} indicating that
	 * the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(Item item) {
		Bundle arguments = new Bundle();
		arguments.putString(ItemDetailFragment.ARG_SURVEY_ID,
				item.getSurveyId());
		arguments.putInt(ItemDetailFragment.ARG_ITEM_ID, item.getId());
		ItemDetailFragment fragment = new ItemDetailFragment();
		fragment.setArguments(arguments);

		int container;
		if (twoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			container = R.id.item_detail_container;
		} else {
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			container = R.id.content_frame;
		}
		if (getFragmentManager() != null) {
			FragmentTransaction tx = getFragmentManager().beginTransaction();
			tx.replace(container, fragment);
			tx.addToBackStack("detail");
			tx.commit();
		}
	}
}
