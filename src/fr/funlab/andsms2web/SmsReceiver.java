package fr.funlab.andsms2web;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsMessage;
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

	@Override
	public void onReceive(Context context, Intent intent) {

		// Get the SMS map from Intent
		// The Bundle object is a simple map. It contains pairs of keys and
		// values. SMS are placed in this bundle.
		// The key of SMS is SMS_EXTRA_NAME.
		Bundle extras = intent.getExtras();

		if (extras != null) {
			// Get received SMS array
			Object[] smsExtra = (Object[]) extras.get(SMS_EXTRA_NAME);

			// Get ContentResolver object for pushing encrypted SMS to the
			// incoming folder
			ContentResolver contentResolver = context.getContentResolver();

			for (int i = 0; i < smsExtra.length; ++i) {
				SmsMessage sms = SmsMessage.createFromPdu((byte[]) smsExtra[i]);

				String address = sms.getOriginatingAddress();
				String body = sms.getMessageBody().toString();
				String serviceCenterAddress = sms.getServiceCenterAddress();
				String smsClass = sms.getMessageClass().toString();

				String message = "";
				message += "SMS class:"
						+ (smsClass == null ? "null" : smsClass)
						+ " from "
						+ (address == null ? "null" : address)
						+ " by "
						+ (serviceCenterAddress == null ? "null"
								: serviceCenterAddress) + " :\n";
				message += body + "\n";

				// Display SMS message
				Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

				// Here you can add any your code to work with incoming SMS
				// I added encrypting of all received SMS

				smsProcess(contentResolver, sms);
			}

		}

		// WARNING!!!
		// If you uncomment the next line
		// then received SMS will not be put to incoming.
		//
		// this.abortBroadcast();
	}

	protected void smsProcess(ContentResolver contentResolver, SmsMessage sms) {

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
		int indexAddr = cursor.getColumnIndex(ADDRESS);

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
