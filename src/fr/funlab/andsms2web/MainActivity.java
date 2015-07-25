package fr.funlab.andsms2web;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final Button buttonFakeSms = (Button) findViewById(R.id.buttonFakeSms);

		buttonFakeSms.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Log.i("", "buttonFakeSms click()");

				SmsReceiver.processMessage("0123456789", "Hello world",
						System.currentTimeMillis(), "+330123456789", 123L);
			}
		});

	}

}
