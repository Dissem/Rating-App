package ch.bfh.mobilecomputing.fs2014.ratingapp;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import ch.bfh.mobilecomputing.fs2014.ratingapp.entities.DatabaseConnector;
import ch.bfh.mobilecomputing.fs2014.ratingapp.entities.Survey.Item;
import ch.bfh.mobilecomputing.fs2014.ratingapp.entities.SurveyRepository;

public class MainActivity extends FragmentActivity implements
		ItemListFragment.Callbacks {
	public static final int ENTER_CODE_POSITION = 0;
	public static final int SURVEY_POSITION = 1;

	private int lastPosition = -1;
	private DrawerLayout drawerLayout;
	private ActionBarDrawerToggle drawerToggle;
	private ListView drawerList;
	private ItemListAndDetailFragment itemListAndDetailFragment;
	private SurveyRepository surveyRepo;
	private String title;
	private CharSequence drawerTitle;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SurveyRepository.init(this);
		surveyRepo = SurveyRepository.getInstance();
		DatabaseConnector.init(this.getBaseContext());

		setContentView(R.layout.activity_main);

		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerList = (ListView) findViewById(R.id.left_drawer);
		drawerTitle = getString(R.string.app_name);

		// Set the adapter for the list view
		drawerList.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, new String[] {
						getString(R.string.enter_code),
						getString(R.string.survey) }));
		// Set the list's click listener
		drawerList.setOnItemClickListener(new DrawerItemClickListener());

		String surveyId = surveyRepo.getSurveyId();
		if (surveyId == null) {
			showEnterCode(false);
			lastPosition = ENTER_CODE_POSITION;
		} else {
			showSurvey(false);
			lastPosition = SURVEY_POSITION;
		}

		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {

			/** Called when a drawer has settled in a completely closed state. */
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				getActionBar().setTitle(title);
			}

			/** Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				getActionBar().setTitle(drawerTitle);
			}
		};

		// Set the drawer toggle as the DrawerListener
		drawerLayout.setDrawerListener(drawerToggle);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		drawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		drawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Pass the event to ActionBarDrawerToggle, if it returns
		// true, then it has handled the app icon touch event
		if (drawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle your other action bar items...

		return super.onOptionsItemSelected(item);
	}

	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			selectItem(position);
		}
	}

	/** Swaps fragments in the main content view */
	public void selectItem(int position) {
		if (lastPosition != position) {
			lastPosition = position;

			switch (position) {
			case ENTER_CODE_POSITION:
				showEnterCode(true);
				break;
			case SURVEY_POSITION:
				showSurvey(true);
				break;
			}

			// Highlight the selected item, update the title,
			// and close the drawer
			drawerList.setItemChecked(position, true);
		}
		drawerLayout.closeDrawer(drawerList);
	}

	private void showEnterCode(boolean addToStack) {
		setTitle(getString(R.string.app_name) + " - "
				+ getString(R.string.enter_code));
		Fragment fragment = new EnterCodeFragment();
		FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
		tx.replace(R.id.content_frame, fragment);
		if (addToStack)
			tx.addToBackStack(title);
		tx.commit();
	}

	private void showSurvey(boolean addToStack) {
		setTitle(getString(R.string.app_name) + " - "
				+ getString(R.string.survey));
		itemListAndDetailFragment = new ItemListAndDetailFragment();
		FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
		tx.replace(R.id.content_frame, itemListAndDetailFragment);
		if (addToStack)
			tx.addToBackStack(title);
		tx.commit();
	}

	@Override
	public void setTitle(CharSequence title) {
		this.title = String.valueOf(title);
		getActionBar().setTitle(title);
	}

	@Override
	public void onItemSelected(Item id) {
		if (itemListAndDetailFragment != null)
			itemListAndDetailFragment.onItemSelected(id);
	}
}