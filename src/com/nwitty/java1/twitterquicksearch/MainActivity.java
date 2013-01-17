package com.nwitty.java1.twitterquicksearch;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



import com.nwitty.helpers.FileHelpers;
import com.nwitty.helpers.Internet;
import com.nwitty.java1.twitterquicksearch.HistoryButtonFragment.HistoryButtonListener;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

public class MainActivity extends Activity implements SearchFragment.SearchListener, HistoryFragment.ResultListener, HistoryButtonFragment.HistoryButtonListener {
	
	Context _context;
	Boolean _connected = false;
	HashMap<String, String> _queryCache;
	JSONArray _jsonResults;
	
	static final String[] FRUITS = new String[] { "Apple", "Avocado", "Banana",
		"Blueberry", "Coconut", "Durian", "Guava", "Kiwifruit",
		"Jackfruit", "Mango", "Olive", "Pear", "Sugar-apple" };
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Log.i("TRACE", "initializing");
        setContentView(R.layout.searchfrag);
        
        
        _context = this;
        
        Log.i("TRACE", "priming _queryCache");
        _queryCache = loadHistory();
        
        Log.i("TRACE", "determining internet status");
        _connected = Internet.getConnectionStatus(_context);
                
        if (_connected.booleanValue() == true) {
        	Log.i("TRACE", "connection seems OKAY");
        	// no need to let the user know we detected internet since this is the assumed state
        	// this is kept for any internet specific logic
		} else {
			// alert the user that there is no internet
			Log.i("TRACE", "connection seems unavailable");
			Toast toast = Toast.makeText(_context, "NO INTERNET CONNECTION", Toast.LENGTH_LONG);
			toast.show();
		}
        
        
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	Log.i("TRACE", "back from activity");
    if (resultCode == RESULT_OK && requestCode == 0) {
    	Log.i("TRACE", "RESULT OK");
        Bundle result = data.getExtras();
        Log.i("TRACE", "got back "+result.get("query").toString());
        ((EditText) findViewById(R.id.searchField)).setText(result.get("query").toString());
        ((Button) findViewById(R.id.searchButton)).performClick();
        Log.e("ACTIVITY RESULT", result.get("query").toString());
      }
    }
 
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
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
			
	    	Toast toast = Toast.makeText(_context, "Searching Twitter", Toast.LENGTH_SHORT);
			toast.show();
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
    			displayResults(result);
    			
    			
    			// we need to pull the query from the JSON for the cache 
    			JSONObject json = new JSONObject(result);
    			    			
    			// cache the query and results for later use
    			_queryCache.put(json.getString("query"), result);
    			FileHelpers.storeObjectFile(_context, "query_history", _queryCache, false);
			} catch (JSONException e) {
				Log.e("JSON", "JSON OBJECT EXCEPTION");
			}
    		
    	}
    }
    
    private void displayResults(String result) {
    	
    	try {
    		JSONObject json = new JSONObject(result);
			JSONArray results = json.getJSONArray("results");
			_jsonResults = results;
			ArrayList<String> outputList = new ArrayList<String>();
    		for (int i = 0; i < results.length(); i++) {
    			// build the tweet and add it
    			JSONObject tweet = results.getJSONObject(i);
    			outputList.add("@"+tweet.getString("from_user")+": "+tweet.getString("text"));
			}
    		if(results.length() == 0) {
//    			_results.addRow("No tweets found.");
    		}
    		Log.i("TRACE", "changing list adapter");
    		((ListView) findViewById(R.id.searchResults)).setAdapter(new ArrayAdapter<String>(this, R.layout.list_tweet,outputList));
    		
    	} catch (JSONException e) {
			Log.e("JSON", "JSON OBJECT EXCEPTION");
		}
    }
    
    private HashMap<String, String> loadHistory() {
    	Object stored = FileHelpers.readObjectFile(_context, "query_history", false);
    	
    	HashMap<String, String> history;
    	if(stored == null) {
    		Log.i("HISTORY", "NOT HISTORY FILE FOUND");
    		history = new HashMap<String, String>();
    	} else {
    		history = ((HashMap<String, String>) stored);
    	}
    	
    	return history;
    }

	@Override
	public void onSearch(String searchTerm) {
		getSearchResults(searchTerm);
	}

	@Override
	public void onHistoryList() {
		Log.i("TRACE", "prepping history intent");
		Intent _historyActivity = new Intent(_context, HistorySelector.class);
		
		Log.i("TRACE", "popping history intent");
		startActivityForResult(_historyActivity, 0);
		
	}

	@Override
	public void onSelection(String data) {
		// TODO Auto-generated method stub
		// do nothing
		getSearchResults(data);
		((EditText) findViewById(R.id.searchField)).setText(data);
	}
	
	@Override
	public void initializeHistory() {
		Log.i("TRACE", "main init history");
		ArrayList<String> recentSearchList = new ArrayList<String>();
        
        
        
        // get the previous search terms
        for (String key: _queryCache.keySet()) {
        	recentSearchList.add(key);
        }
        
        ((ListView) findViewById(R.id.history_list)).setAdapter(new ArrayAdapter<String>(this, R.layout.list_tweet,recentSearchList));
		
	}
    
}
