package com.nwitty.java1.twitterquicksearch;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



import com.nwitty.helpers.FileHelpers;
import com.nwitty.helpers.Internet;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;
import android.widget.Spinner;

public class MainActivity extends Activity {
	
	LinearLayout _appLayout;
	LayoutParams _lp;
	Context _context;
	SearchForm _search;
	RecentSearches _history;
	Boolean _connected = false;
	SearchResults _results;
	HashMap<String, String> _queryCache;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _context = this;
        
        _queryCache = loadHistory();
        
        // Initialize the main layout
        _appLayout = new LinearLayout(_context);
        _appLayout.setOrientation(LinearLayout.VERTICAL);
        _lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        _appLayout.setLayoutParams(_lp);
        
        // Add a new search form
        _search = new SearchForm(_context, "Search Query", "Go");
        _appLayout.addView(_search);
        
        // handle the button click
        _search.getButton().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				String searchTerm = _search.getField().getText().toString();
				
				if (searchTerm.length() == 0) {
					// refuse to search if the query is blank
					_results.addRow("BLURGH! GIMME DER QWERI!!1!");
					Toast toast = Toast.makeText(_context, "OMEGERD! DER QWERI!", Toast.LENGTH_SHORT);
					toast.show();
				} else {
					// clear any previous results
					_results.reset();
					
					if (_connected) {
						// Do the actual search fool!
						getSearchResults(searchTerm);
					} else {
						// no network, let's try to get it from cache
						String cachedResults = _queryCache.get(searchTerm);
						if (cachedResults == null || cachedResults.length() == 0) {
							// no cached results for this query, throw some Ds
							_results.addRow("boo hoo, no cached results for your query!");
							Toast toast = Toast.makeText(_context, "OMEGERD! NUN DEM KASHD RIZULTS!", Toast.LENGTH_SHORT);
							toast.show();
						} else {
							// looks like the cache hit, show it
							Toast toast = Toast.makeText(_context, "OMEGERD! GOT DER KASHD QWERIS!", Toast.LENGTH_SHORT);
							toast.show();
							displayResults(cachedResults);
						}
						
					}
					// Add the search query to the history
					_history.addQuery(searchTerm);
					
					
					
				}
				
			}
		});
        
        _history = new RecentSearches(_context);
        // get the previous search terms
        for (String key: _queryCache.keySet()) {
        	_history.addQuery(key);
        }
        
        _appLayout.addView(_history);
        
        // internet connection logic
        _connected = Internet.getConnectionStatus(_context);
        
        if (_connected.booleanValue() == true) {
        	// just let the user know internets were detected
        	Toast toast = Toast.makeText(_context, "OMEGERD! INDERNETS!", Toast.LENGTH_LONG);
			toast.show();
		} else {
			// alert the user that there is no internet
			Toast toast = Toast.makeText(_context, "OMG! NO INTERNET CONNECTIONS", Toast.LENGTH_LONG);
			toast.show();
			
			_results.addRow("NO INTERBLAG CONNECTION!");
			
			if (_queryCache.isEmpty()) {
				// looks like there are 0 cached queries
				_results.addRow("why bother... no internet and no cache makes search results a dull app");
			} else {
				_results.addRow("GOT DER KASHD QWERIS");
			}
		}
        
        _results = new SearchResults(_context);
        _appLayout.addView(_results);
  
        
        setContentView(_appLayout);
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
			
	    	Toast toast = Toast.makeText(_context, "OMEGERD! SERCHIN", Toast.LENGTH_SHORT);
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
		
    		for (int i = 0; i < results.length(); i++) {
    			// build the tweet and add it
    			JSONObject tweet = results.getJSONObject(i);
    			String r = "@"+tweet.getString("from_user")+": "+tweet.getString("text");
    			_results.addRow(r);
    		
			}
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
    		history = (HashMap<String, String>) stored;
    	}
    	
    	return history;
    }
}
