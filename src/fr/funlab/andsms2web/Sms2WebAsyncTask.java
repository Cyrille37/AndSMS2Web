package fr.funlab.andsms2web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
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

public class Sms2WebAsyncTask
		extends
		AsyncTask<Sms2WebAsyncTask.Sms2WebCall, Integer, Sms2WebAsyncTask.Sms2WebResponse> {

	public class Sms2WebCall {
		public String uid;
		public URL url;
		public JSONObject json;
	}

	public class Sms2WebResponse {
		public String uid;
		public Exception error = null ;
		public JSONObject result = null ;
	}

	/**
	 * S'exécute dans un autre Thread que ceclui de l'IHM
	 */
	@Override
	protected Sms2WebResponse doInBackground(
			Sms2WebAsyncTask.Sms2WebCall... uwpds) {
		int count = uwpds.length;

		if (count > 1)
			throw new IllegalArgumentException("Only one URL at a time !");

		Sms2WebAsyncTask.Sms2WebResponse resp = new Sms2WebAsyncTask.Sms2WebResponse();
		resp.uid = uwpds[0].uid ;

		try {
			JSONObject json = makeRequest(uwpds[0].url.toString(), uwpds[0].json);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resp.error = e ;
		}

		return resp ;
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

	public static JSONObject makeRequest(String path, JSONObject json)
			throws Exception {

		// instantiates httpclient to make request
		DefaultHttpClient httpclient = new DefaultHttpClient();

		// url with the post data
		HttpPost httpPost = new HttpPost(path);

		// passes the results to a string builder/entity
		StringEntity se = new StringEntity(json.toString());

		// sets the post request as the resulting string
		httpPost.setEntity(se);
		// sets a request header so the page receving the request
		// will know what to do with it
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Content-type", "application/json");

		HttpResponse response = httpclient.execute(httpPost);
		StatusLine statusLine = response.getStatusLine();
		int statusCode = statusLine.getStatusCode();
		if (statusCode == 200) {
			HttpEntity entity = response.getEntity();
			InputStream is = entity.getContent();
			String content = inputStreamToString(is);
			json = new JSONObject(content);
		} else {
			json = null;
		}

		return json;
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