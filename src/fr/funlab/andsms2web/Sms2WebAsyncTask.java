package fr.funlab.andsms2web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.InvalidMarkException;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.os.AsyncTask;

public class Sms2WebAsyncTask
		extends
		AsyncTask<Sms2WebAsyncTask.URLWithPostData, Integer, Sms2WebAsyncTask.URLResponse> {

	public class URLWithPostData {
		String uid;
		URL url;
		Map<String, String> postData;
	}

	public class URLResponse {
		String uid;
		Exception error;
		JSONObject result;
	}

	/**
	 * S'exécute dans un autre Thread que ceclui de l'IHM
	 */
	@Override
	protected URLResponse doInBackground(
			Sms2WebAsyncTask.URLWithPostData... urls) {
		int count = urls.length;

		if (count > 1)
			throw new IllegalArgumentException("Only one URL at a time !");

		JSONObject json;
		for (int i = 0; i < count; i++) {
			// totalSize += Downloader.downloadFile(urls[i]);
			publishProgress((int) ((i / (float) count) * 100));

			json = makeRequest();
		}
		return json;
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
	protected void onPostExecute(JSONObject json) {
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
		HttpResponse httpResponse = httpclient.execute(httpPost);
		InputStream is = httpResponse.getEntity().getContent();
		String jsonString = inputStreamToString(is);

		return null;
	}

	private static String inputStreamToString(InputStream inputStream)
			throws IOException {
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(inputStream));
		String line = "";
		String result = "";
		while ((line = bufferedReader.readLine()) != null)
			result += line;

		inputStream.close();
		return result;

	}

}