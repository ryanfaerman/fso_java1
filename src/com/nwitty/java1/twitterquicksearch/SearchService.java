package com.nwitty.java1.twitterquicksearch;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import com.nwitty.helpers.FileHelpers;
import com.nwitty.helpers.Internet;

import android.app.Activity;
import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.widget.Toast;

public class SearchService extends IntentService {

	Messenger _messenger;
	
	public SearchService() {
		super("SearchService");
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		String msg = intent.getStringExtra("query");
		getSearchResults(msg);
		
		Bundle extras = intent.getExtras();
		if(extras != null) {
			_messenger = (Messenger) extras.get("MESSENGER");
			
		}
		
	}
	
	private void getSearchResults(String searchTerm) {
    	String baseURL = "http://search.twitter.com/search.json?rpp=50&result_type=mixed&page=1&q=";
    	String q = "";
    	try {
			q = URLEncoder.encode(searchTerm, "UTF-8");
		} catch (Exception e) {
			Log.e("BAD URL", "ENCODING PROBLEM");
		}
    	
    	URL requestURL;
    	try {
			requestURL = new URL(baseURL+q);
			SearchRequest sr = new SearchRequest();
			sr.execute(requestURL);
		} catch (MalformedURLException e) {
			Log.e("BAD URL", "MALFORMED URL");
			requestURL = null;
		}

    }
	
	private class SearchRequest extends AsyncTask<URL, Void, String> {
    	@Override
    	protected String doInBackground(URL...urls) {
    		String response = "";
    		for(URL url: urls) {
    			// make the request
    			response = Internet.get(url);
    		}
    		return response;
    	}
    	
    	@Override
    	protected void onPostExecute(String result) {
    		Log.i("URL RESPONSE", result);
    		try {
    			// show dem results - duh!
//    			displayResults(result);
    			Message msg = Message.obtain();
    			msg.arg1 = Activity.RESULT_OK;
    			msg.obj = result;
    			try {
    				_messenger.send(msg);
    			} catch (android.os.RemoteException e1) {
    				Log.w(getClass().getName(), "Exception sending message", e1);
    			}
    			
    			
    			// we need to pull the query from the JSON for the cache 
    			JSONObject json = new JSONObject(result);
    			    			
    			// cache the query and results for later use
    			String term = json.getString("query");
//    			FileHelpers.storeObjectFile(_context, "query_history", _queryCache, false);
    			ContentValues values = new ContentValues();
    			values.put(HistoryProvider.TERM, term);
    			values.put(HistoryProvider.CACHE, result);
    			getContentResolver().insert(HistoryProvider.CONTENT_URI, values);
    			
//    			return 
			} catch (JSONException e) {
				Log.e("JSON", "JSON OBJECT EXCEPTION");
			}
    		
    	}
    }

}
