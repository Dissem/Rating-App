package ch.bfh.mobilecomputing.fs2014.ratingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import ch.bfh.mobilecomputing.fs2014.ratingapp.entities.Survey.Item;
import ch.bfh.mobilecomputing.fs2014.ratingapp.entities.SurveyRepository;

public class MainActivity extends FragmentActivity implements
		ItemListFragment.Callbacks {
	private DrawerLayout drawerLayout;
	private ListView drawerList;
	private ItemListAndDetailFragment itemListAndDetailFragment;
	private SurveyRepository surveyRepo;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SurveyRepository.init(this);
		surveyRepo = SurveyRepository.getInstance();

		setContentView(R.layout.activity_main);

		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerList = (ListView) findViewById(R.id.left_drawer);

		// Set the adapter for the list view
		drawerList.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, new String[] {
						"Enter Code", "Survey" }));
		// Set the list's click listener
		drawerList.setOnItemClickListener(new DrawerItemClickListener());

		String surveyId = surveyRepo.getSurveyId();
		if (surveyId == null)
			showEnterCode(false);
		else
			showSurvey(false);
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
	private void selectItem(int position) {
		switch (position) {
		case 0:
			showEnterCode(true);
			break;
		case 1:
			showSurvey(true);
			break;
		}

		// Highlight the selected item, update the title, and close the drawer
		drawerList.setItemChecked(position, true);
		// setTitle(mPlanetTitles[position]);
		drawerLayout.closeDrawer(drawerList);
	}

	private void showEnterCode(boolean addToStack) {
		Fragment fragment = new EnterCodeFragment();
		FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
		tx.replace(R.id.content_frame, fragment);
		if (addToStack)
			tx.addToBackStack("enter code");
		tx.commit();
	}

	private void showSurvey(boolean addToStack) {
		itemListAndDetailFragment = new ItemListAndDetailFragment();
		FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
		tx.replace(R.id.content_frame, itemListAndDetailFragment);
		if (addToStack)
			tx.addToBackStack("detail");
		tx.commit();
	}

	@Override
	public void setTitle(CharSequence title) {
		// mTitle = title;
		getActionBar().setTitle(title);
	}

	@Override
	public void onItemSelected(Item id) {
		if (itemListAndDetailFragment != null)
			itemListAndDetailFragment.onItemSelected(id);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {
				String surveyId = data.getStringExtra("SCAN_RESULT");
				surveyRepo.setSurveyId(surveyId);
				selectItem(1);
			}
			if (resultCode == RESULT_CANCELED) {
				// handle cancel
			}
		}
	}
}
