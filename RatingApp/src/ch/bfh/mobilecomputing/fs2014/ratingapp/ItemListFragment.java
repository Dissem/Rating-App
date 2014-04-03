package ch.bfh.mobilecomputing.fs2014.ratingapp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
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
 * A list fragment representing a list of Items. This fragment also supports
 * tablet devices by allowing list items to be given an 'activated' state upon
 * selection. This helps indicate which item is currently being viewed in a
 * {@link ItemDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class ItemListFragment extends ListFragment {
	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * activated item position. Only used on tablets.
	 */
	private static final String STATE_ACTIVATED_POSITION = "activated_position";

	private final SurveyRepository surveyRepo = SurveyRepository.getInstance();

	private String surveyId;
	private Survey survey;

	/**
	 * The fragment's current callback object, which is notified of list item
	 * clicks.
	 */
	private Callbacks mCallbacks = sDummyCallbacks;

	/**
	 * The current activated item position. Only used on tablets.
	 */
	private int mActivatedPosition = ListView.INVALID_POSITION;

	/**
	 * A callback interface that all activities containing this fragment must
	 * implement. This mechanism allows activities to be notified of item
	 * selections.
	 */
	public interface Callbacks {
		/**
		 * Callback for when an item has been selected.
		 */
		public void onItemSelected(Item id);
	}

	/**
	 * A dummy implementation of the {@link Callbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */
	private static Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onItemSelected(Item id) {
		}
	};

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public ItemListFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		surveyId = surveyRepo.getSurveyId();

		surveyRepo.requestSurvey(surveyId, surveyRepoCallback,
				CallbackMode.BOTH);
	}

	RepositoryCallback<Survey> surveyRepoCallback = new RepositoryCallback<Survey>() {
		@Override
		public void onReceived(Survey entity) {
			DatabaseConnector.getInstance().open();
			survey = entity;
			survey.setItems(getSortedData(survey));
			try {
				ListView listView = getListView();
				LayoutInflater inflater = getLayoutInflater(null);

				initListHeader(listView, inflater);
				setListAdapter(new ItemAdapter(getActivity(),
						android.R.id.text1, survey.getItems()));
			} catch (Exception e) {
				// It's possible that we're too late, e.g. the activity is
				// already destroyed when the callback is called. Let's just
				// log and ignore this case:
				Log.e(getClass().getSimpleName(), e.getMessage());
			}
		}

		@Override
		public void onError(Exception e) {
			Utils.showToast(getActivity(), R.string.survey_load_error,
					Toast.LENGTH_LONG);
		}
	};

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		setActivateOnItemClick(true);

		// Restore the previously serialized activated item position.
		if (savedInstanceState != null
				&& savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
			setActivatedPosition(savedInstanceState
					.getInt(STATE_ACTIVATED_POSITION));
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof Callbacks)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}

		mCallbacks = (Callbacks) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();

		// Reset the active callbacks interface to the dummy implementation.
		mCallbacks = sDummyCallbacks;
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position,
			long id) {
		super.onListItemClick(listView, view, position, id);

		// Notify the active callbacks interface (the activity, if the
		// fragment is attached to one) that an item has been selected.
		if (survey != null)
			mCallbacks.onItemSelected(survey.getItems().get((int) id));
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mActivatedPosition != ListView.INVALID_POSITION) {
			// Serialize and persist the activated item position.
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		}
	}

	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be
	 * given the 'activated' state when touched.
	 */
	public void setActivateOnItemClick(boolean activateOnItemClick) {
		// When setting CHOICE_MODE_SINGLE, ListView will automatically
		// give items the 'activated' state when touched.
		getListView().setChoiceMode(
				activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
						: ListView.CHOICE_MODE_NONE);
	}

	private void setActivatedPosition(int position) {
		if (position == ListView.INVALID_POSITION) {
			getListView().setItemChecked(mActivatedPosition, false);
		} else {
			getListView().setItemChecked(position, true);
		}

		mActivatedPosition = position;
	}

	private void initListHeader(ListView listView, LayoutInflater inflater) {
		if (listView.getHeaderViewsCount() == 0) {
			ViewGroup header = (ViewGroup) inflater.inflate(
					R.layout.list_header, listView, false);
			listView.addHeaderView(header, null, false);

			TextView txtHeader = (TextView) header
					.findViewById(R.id.txtListHeader);
			txtHeader.setText(survey.getTitle());
		}
	}

	private List<Item> getSortedData(Survey survey) {
		List<Item> itemsRated = new ArrayList<Item>();
		List<Item> itemsNotRated = new ArrayList<Item>();
		Rating internalRating = null;

		for (Item item : survey.getItems()) {
			internalRating = new Rating(item.getSurveyId(), item.getId());
			if (DatabaseConnector.getInstance().isRatingExist(internalRating)) {
				itemsRated.add(item);
			} else {
				itemsNotRated.add(item);
			}
		}
		return sortMergeData(itemsRated, itemsNotRated);
	}

	private List<Item> sortMergeData(List<Item> itemsRated,
			List<Item> itemsNotRated) {
		List<Item> mergedList = new ArrayList<Item>();

		Collections.sort(itemsRated, new Comparator<Item>() {
			@Override
			public int compare(Item item1, Item item2) {
				if (item1.getRating() > item2.getRating()) {
					return -1;
				} else if (item1.getRating() == item2.getRating()) {
					if (item1.getVotes() < item2.getVotes()) {
						return -1;
					} else if (item1.getVotes() > item2.getVotes()) {
						return 1;
					} else {
						return 0;
					}
				}
				return 1;
			}
		});

		Collections.sort(itemsNotRated, new Comparator<Item>() {
			@Override
			public int compare(Item item1, Item item2) {
				return item1.getTitle().compareTo(item2.getTitle());
			}
		});

		itemsRated = addRanksToItems(itemsRated);

		mergedList.addAll(itemsNotRated);
		mergedList.addAll(itemsRated);

		return mergedList;
	}

	private List<Item> addRanksToItems(List<Item> itemsRated) {
		int rank = 0;
		int offset = 0;
		double prevRating = 0.0;
		int prevVotes = 0;

		for (Item item : itemsRated) {
			if (item.getRating() != prevRating) {
				rank += (1 + offset);
				offset = 0;
			} else {
				if (item.getVotes() < prevVotes) {
					rank += (1 + offset);
					offset = 0;
				} else {
					offset++;
				}
			}
			prevRating = item.getRating();
			prevVotes = item.getVotes();
			item.setRank(rank);
		}
		return itemsRated;
	}
}
