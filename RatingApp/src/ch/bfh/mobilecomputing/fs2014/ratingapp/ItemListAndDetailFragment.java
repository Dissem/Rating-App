package ch.bfh.mobilecomputing.fs2014.ratingapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import ch.bfh.mobilecomputing.fs2014.ratingapp.entities.Survey;
import ch.bfh.mobilecomputing.fs2014.ratingapp.entities.Survey.Item;
import ch.bfh.mobilecomputing.fs2014.ratingapp.entities.SurveyRepository;
import ch.bfh.mobilecomputing.fs2014.ratingapp.entities.SurveyRepository.CallbackMode;
import ch.bfh.mobilecomputing.fs2014.ratingapp.entities.SurveyRepository.RepositoryCallback;

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
	private ItemListFragment listFragment;
	private ItemDetailFragment detailFragment;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_item_list,
				container, false);
		listFragment = new ItemListFragment();
		getFragmentManager().beginTransaction()
				.add(R.id.item_list, listFragment).commit();

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
		detailFragment = new ItemDetailFragment();
		detailFragment.setArguments(arguments);

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
			tx.replace(container, detailFragment);
			tx.addToBackStack("detail");
			tx.commit();
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu_survey, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_reload:
			SurveyRepository repo = SurveyRepository.getInstance();
			repo.requestSurvey(repo.getSurveyId(),
					new RepositoryCallback<Survey>() {
						@Override
						public void onReceived(Survey entity) {
							if (listFragment != null
									&& listFragment.isVisible())
								listFragment.surveyRepoCallback
										.onReceived(entity);
							if (detailFragment != null
									&& detailFragment.isVisible())
								detailFragment.surveyRepoCallback
										.onReceived(entity);
						}

						@Override
						public void onError(Exception e) {
							Utils.showToast(getActivity(),
									R.string.survey_load_error,
									Toast.LENGTH_LONG);
						}
					}, CallbackMode.FRESH_ONLY);

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
