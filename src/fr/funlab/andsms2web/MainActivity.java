package fr.funlab.andsms2web;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {

	public static final String PREFS_NAME = "AndSms2WebPref";
	public static final String PREF_URL = "url";
	public static final String DEFAULT_URL = "http://smswall.local.comptoir.net/api/message_put";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

		EditText editTextIp = (EditText) findViewById(R.id.edittextIp);
		editTextIp.setText(settings.getString(PREF_URL, DEFAULT_URL));

		final Button buttonFakeSms = (Button) findViewById(R.id.buttonFakeSms);
		buttonFakeSms.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Log.i("", "buttonFakeSms click()");

				SmsReceiver smsr = new SmsReceiver();
				smsr.processMessage("0123456789", "Hello world",
						System.currentTimeMillis(), "+330123456789", 123L);
			}
		});

		final Button buttonSetIp = (Button) findViewById(R.id.buttonSetIp);
		buttonSetIp.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Log.i("", "buttonSetIp click()");

				EditText editTextIp = (EditText) findViewById(R.id.edittextIp);
				SharedPreferences.Editor editor = settings.edit();
				editor.putString("url", editTextIp.getText().toString());
				// Commit the edits!
				editor.commit();

			}
		});

	}
}
