package fr.funlab.andsms2web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.InvalidMarkException;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class Sms2WebAsyncTask
		extends
		AsyncTask<Sms2WebAsyncTask.Sms2WebCall, Integer, Sms2WebAsyncTask.Sms2WebResponse> {

	final static String LOG_TAG = Sms2WebAsyncTask.class.getName();

	public class Sms2WebCall {
		public String uid;
		public String url;
		public JSONObject json;
	}

	public class Sms2WebResponse {
		public String uid;
		public Exception error = null;
		public JSONObject result = null;
	}

	/**
	 * S'exécute dans un autre Thread que ceclui de l'IHM
	 */
	@Override
	protected Sms2WebResponse doInBackground(
			Sms2WebAsyncTask.Sms2WebCall... uwpds) {

		int count = uwpds.length;

		if (count > 1) {
			Log.e(LOG_TAG, "Only one task at a time !()");
			throw new IllegalArgumentException("Only one task at a time !");
		}

		Sms2WebResponse resp = new Sms2WebResponse();
		resp.uid = uwpds[0].uid;

		try {
			JSONObject json = makeRequest(uwpds[0].url, uwpds[0].json);
			resp.result = json;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resp.error = e;
		}

		return resp;
	}

	/**
	 * S'exécute dans le Thread de l'IHM
	 */
	@Override
	protected void onProgressUpdate(Integer... progress) {
		// setProgressPercent(progress[0]);
	}

	/**
	 * S'exécute dans le Thread de l'IHM
	 */
	@Override
	protected void onPostExecute(Sms2WebResponse json) {
		// showDialog("Downloaded " + result + " bytes");
	}

	public static JSONObject makeRequest(String url, JSONObject json)
			throws Exception {

		Log.d(LOG_TAG, "makeRequest() at " + url);

		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);

		// passes the results to a string builder/entity
		StringEntity se = new StringEntity(json.toString());
		httpPost.setEntity(se);

		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Content-type", "application/json");

		HttpResponse response = httpclient.execute(httpPost);
		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == 200) {
			Log.d(LOG_TAG, "makeRequest() successed");
			InputStream is = response.getEntity().getContent();
			String content = inputStreamToString(is);
			return new JSONObject(content);
		} else {
			Log.i(LOG_TAG, "makeRequest() failed code " + statusCode);
			throw new Exception("Network failed with code " + statusCode);
		}
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

}