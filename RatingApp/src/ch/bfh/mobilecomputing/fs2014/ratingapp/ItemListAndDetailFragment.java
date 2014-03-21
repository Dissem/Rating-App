package ch.bfh.mobilecomputing.fs2014.ratingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
		getFragmentManager().beginTransaction()
				.add(R.id.item_list, new ItemListFragment()).commit();

		if (rootView.findViewById(R.id.item_detail_container) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			twoPane = true;

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			((ItemListFragment) getFragmentManager().findFragmentById(
					R.id.item_list)).setActivateOnItemClick(true);
		}
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

		if (twoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			getFragmentManager().beginTransaction()
					.replace(R.id.item_detail_container, fragment)
					.addToBackStack("detail").commit();
		} else {
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			getFragmentManager().beginTransaction()
					.replace(R.id.content_frame, fragment)
					.addToBackStack("detail").commit();
		}
	}
}
