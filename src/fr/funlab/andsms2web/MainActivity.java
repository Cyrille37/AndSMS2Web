package fr.funlab.andsms2web;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

	public static final String PREFS_NAME = "AndSms2WebPref";
	public static final String PREF_URL = "url";
	public static final String PREF_PHONENUMBER = "phone";
	public static final String DEFAULT_URL = "http://smswall.local.comptoir.net/api/message_put";
	
	final static String LOG_TAG = SmsReceiver.class.getName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();

		String urlTmp = settings.getString(PREF_URL, null);
		if( urlTmp == null )
		{
			urlTmp = DEFAULT_URL;
			editor.putString(PREF_URL, urlTmp);
		}
		EditText editTextIp = (EditText) findViewById(R.id.edittextIp);
		editTextIp.setText(urlTmp);
		final String url = urlTmp ; 

		final String phoneNumber = getPhoneNumber();
		editor.putString(PREF_PHONENUMBER, phoneNumber);

		editor.commit(); // Commit the preference edits!
		
		Log.d(LOG_TAG, "onCreate() phoneNumber: "+phoneNumber);

		final Button buttonFakeSms = (Button) findViewById(R.id.buttonFakeSms);
		buttonFakeSms.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Log.i("", "buttonFakeSms click()");

				try{
				SmsReceiver smsr = new SmsReceiver();
				smsr.setUrl(url);

				smsr.processMessage("0123456789", "Hello world", phoneNumber,
						System.currentTimeMillis(), "+330123456789", 123L);

				}catch(Exception ex){
					Log.e(LOG_TAG,"ERROR buttonFakeSms(): "+ex.getMessage());
					Toast.makeText(MainActivity.this, "ERROR buttonFakeSms(): "+ex.getMessage(), Toast.LENGTH_LONG).show();
				}
			}
		});

		final Button buttonSetIp = (Button) findViewById(R.id.buttonSetIp);
		buttonSetIp.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Log.i("", "buttonSetIp click()");

				EditText editTextIp = (EditText) findViewById(R.id.edittextIp);
				SharedPreferences.Editor editor = settings.edit();
				editor.putString(PREF_URL, editTextIp.getText().toString());
				// Commit the edits!
				editor.commit();

			}
		});

	}

	String getPhoneNumber() {
		String phoneNumber;
		TelephonyManager tMgr = (TelephonyManager) this
				.getSystemService(MainActivity.TELEPHONY_SERVICE);
		phoneNumber = tMgr.getLine1Number();
		if (phoneNumber == null) {
			// String getSimSerialNumber = telemamanger.getSimSerialNumber();
		}
		return phoneNumber;
	}
}
