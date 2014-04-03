package ch.bfh.mobilecomputing.fs2014.ratingapp.test;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.TextView;
import ch.bfh.mobilecomputing.fs2014.ratingapp.MainActivity;
import ch.bfh.mobilecomputing.fs2014.ratingapp.R;

@SuppressLint("NewApi")
public class MainTest extends ActivityInstrumentationTestCase2<MainActivity> {
	private MainActivity activity;
	private TextView codeText;
	private Button startBtn;

	public MainTest() {
		super(MainActivity.class);
	}

	/**
	 * Sets up the test environment before each test.
	 * 
	 * @see android.test.ActivityInstrumentationTestCase2#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		// Call the super constructor (required by JUnit)
		super.setUp();

		// prepare to send key events to the app under test by turning off touch
		// mode. Must be done before the first call to getActivity()
		setActivityInitialTouchMode(false);

		SharedPreferences settings = PreferenceManager
				.getDefaultSharedPreferences(getInstrumentation()
						.getTargetContext());
		settings.edit().clear().commit();

		// Start the app under test by starting its main activity. The test
		// runner already knows which activity this is from the call to the
		// super constructor, as mentioned previously. The tests can now use
		// instrumentation to directly access the main activity through
		// mActivity.
		activity = getActivity();

		// Get references to objects in the application under test. These are
		// tested to ensure that the app under test has initialized correctly.
		codeText = (TextView) activity.findViewById(R.id.code_text);
		startBtn = (Button) activity.findViewById(R.id.start_survey_button);
	}

	/**
	 * Tests the initial values of key objects in the app under test, to ensure
	 * the initial conditions make sense. If one of these is not initialized
	 * correctly, then subsequent tests are suspect and should be ignored.
	 * 
	 * @throws InterruptedException
	 */
	public void testPreconditions() throws InterruptedException {
		// assert that codeText exists and therefor the app shows the
		// "enter code" page
		assertNotNull(codeText);
		activity.runOnUiThread(new Runnable() {
			public void run() {
				codeText.requestFocus();
			}
		});
		sendKeys("T E S T");

		assertNotNull(startBtn);
		activity.runOnUiThread(new Runnable() {
			public void run() {
				startBtn.callOnClick();
			}
		});

		assertEquals("test", codeText.getText().toString());
	}

}
