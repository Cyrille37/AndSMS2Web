package fr.funlab.andsms2web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class SmsReceiver extends BroadcastReceiver {

	/**
	 * Bundle key for SMS
	 * 
	 * PDU: Protocol Description Unit
	 */
	public static final String SMS_EXTRA_NAME = "pdus";
	public static final String SMS_URI = "content://sms";

	public static final int MESSAGE_TYPE_INBOX = 1;
	public static final int MESSAGE_TYPE_SENT = 2;

	public static final int MESSAGE_IS_NOT_READ = 0;
	public static final int MESSAGE_IS_READ = 1;

	public static final int MESSAGE_IS_NOT_SEEN = 0;
	public static final int MESSAGE_IS_SEEN = 1;

	public static final String MESSAGE_SRV_NAME = "sms";

	final static String LOG_TAG = SmsReceiver.class.getName();

	protected String url;
	protected String phoneNumber;

	@Override
	public void onReceive(Context context, Intent intent) {

		Log.i(LOG_TAG, "onReceive()");

		// Get ContentResolver object for pushing SMS to the incoming folder
		// ContentResolver contentResolver = context.getContentResolver();

		// We need each time to retreive "url" & "phonenumber" preferences

		SharedPreferences settings = context.getApplicationContext()
				.getSharedPreferences(MainActivity.PREFS_NAME,
						Context.MODE_PRIVATE);
		String url = settings.getString(MainActivity.PREF_URL,
				MainActivity.DEFAULT_URL);
		this.url = url;
		Log.d(LOG_TAG, "onReceive() url: " + url);

		this.phoneNumber = settings.getString(MainActivity.PREF_PHONENUMBER,
				null);
		Log.d(LOG_TAG, "onReceive() phoneNumber: " + phoneNumber);

		// Get the SMS map from Intent
		// The Bundle object is a simple map. It contains pairs of keys and
		// values. SMS are placed in this bundle.
		// The key of SMS is SMS_EXTRA_NAME.
		Bundle extras = intent.getExtras();

		if (extras == null) {
			return;
		}

		// Get received SMS array
		Object[] smsExtra = (Object[]) extras.get(SMS_EXTRA_NAME);

		for (int i = 0; i < smsExtra.length; ++i) {

			SmsMessage sms = SmsMessage.createFromPdu((byte[]) smsExtra[i]);
			try {

				long rcvTime = System.currentTimeMillis();
				String from = sms.getOriginatingAddress();
				String body = sms.getMessageBody();
				String srvAddr = sms.getServiceCenterAddress();
				long srvTime = sms.getTimestampMillis();

				if (processMessage(from, body, this.phoneNumber, rcvTime,
						srvAddr, srvTime)) {
					// received SMS will not be put to incoming.
					this.abortBroadcast();
				}

			} catch (Exception e) {
				Log.e(LOG_TAG, "ERROR onReceive(): " + e.getMessage());
				Toast.makeText(context, "ERROR onReceive(): " + e.getMessage(),
						Toast.LENGTH_LONG).show();
			}
		}

	}

	public void setUrl(String url)
	{
		this.url = url ;
	}

	public boolean processMessage(String from, String body, String to,
			long rcvTime, String srvAddr, long srvTime) {

		Log.d(LOG_TAG, "processMessage()");

		// String smsClass = sms.getMessageClass().toString();

		try {
			JSONObject json = new JSONObject();
			json.put("from", from);
			json.put("body", body);
			json.put("to", to);
			json.put("rcvTime", rcvTime);
			json.put("srvName", MESSAGE_SRV_NAME);
			json.put("srvAddr", srvAddr);
			json.put("srvTime", srvTime);

			Log.d(LOG_TAG, "processMessage() DefaultHttpClient");

			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(this.url);

			// passes the results to a string builder/entity
			StringEntity se = new StringEntity(json.toString());
			httpPost.setEntity(se);

			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");

			Log.d(LOG_TAG, "processMessage() httpclient.execute");

			HttpResponse response = httpclient.execute(httpPost);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				Log.d(LOG_TAG, "makeRequest() successed");
				InputStream is = response.getEntity().getContent();
				String content = inputStreamToString(is);
				if (content != null)
					return true;
			} else {
				throw new Exception("Network failed with code " + statusCode);
			}
		} catch (Exception ex) {
			Log.e(LOG_TAG, "processMessage() at '" + this.url + "' failed: "
					+ ex.getMessage());
			Toast.makeText(null, "ERROR processMessage(): " + ex.getMessage(),
					Toast.LENGTH_SHORT).show();
		}
		return false;
	}

	private static String inputStreamToString(InputStream inputStream)
			throws IOException {
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(inputStream));
		String line;
		StringBuilder result = new StringBuilder();
		while ((line = bufferedReader.readLine()) != null)
			result.append(line);
		inputStream.close();
		return result.toString();

	}

	// All available column names in SMS table
	// [_id, thread_id, address,
	// person, date, protocol, read,
	// status, type, reply_path_present,
	// subject, body, service_center,
	// locked, error_code, seen]

	public static final String ADDRESS = "address";
	public static final String PERSON = "person";
	public static final String DATE = "date";
	public static final String READ = "read";
	public static final String STATUS = "status";
	public static final String TYPE = "type";
	public static final String BODY = "body";
	public static final String SEEN = "seen";

	protected void readSMSInbox(ContentResolver contentResolver) {
		Cursor cursor = contentResolver.query(Uri.parse(SMS_URI + "/inbox"),
				null, null, null, null);
		int indexBody = cursor.getColumnIndex(BODY);
		// int indexAddr = cursor.getColumnIndex(ADDRESS);

		if (indexBody < 0 || !cursor.moveToFirst())
			return;

		/*
		 * smsList.clear(); do { String str = "Sender: " + cursor.getString(
		 * indexAddr ) + "\n" + cursor.getString( indexBody ); smsList.add( str
		 * ); } while( cursor.moveToNext() ); ListView smsListView = (ListView)
		 * findViewById( R.id.SMSList ); smsListView.setAdapter( new
		 * ArrayAdapter<String>( this, android.R.layout.simple_list_item_1,
		 * smsList) ); smsListView.setOnItemClickListener( this );
		 */
	}
}
