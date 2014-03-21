package ch.bfh.mobilecomputing.fs2014.ratingapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import ch.bfh.mobilecomputing.fs2014.ratingapp.entities.SurveyRepository;

public class EnterCodeFragment extends Fragment {
	private final SurveyRepository surveyRepo = SurveyRepository.getInstance();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_enter_code,
				container, false);

		final TextView codeText = (TextView) rootView
				.findViewById(R.id.code_text);
		codeText.setText(surveyRepo.getSurveyId());

		final Button startButton = (Button) rootView
				.findViewById(R.id.start_survey_button);
		startButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String surveyId = codeText.getText().toString();
				if (!surveyId.isEmpty()) {
					surveyRepo.setSurveyId(surveyId);
					getFragmentManager()
							.beginTransaction()
							.replace(R.id.content_frame,
									new ItemListAndDetailFragment())
							.addToBackStack("list").commit();
				}
			}
		});

		final Button scanButton = (Button) rootView
				.findViewById(R.id.scan_code_button);
		scanButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					Intent intent = new Intent(
							"com.google.zxing.client.android.SCAN");
					intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
					startActivityForResult(intent, 0);
				} catch (Exception e) {
					Uri marketUri = Uri
							.parse("market://details?id=com.google.zxing.client.android");
					Intent marketIntent = new Intent(Intent.ACTION_VIEW,
							marketUri);
					startActivity(marketIntent);
				}
			}
		});

		return rootView;
	}
}
