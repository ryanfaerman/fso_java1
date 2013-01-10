package com.nwitty.java1.twitterquicksearch;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



import com.nwitty.helpers.FileHelpers;
import com.nwitty.helpers.Internet;

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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

public class MainActivity extends Activity implements OnItemSelectedListener {
	
	LinearLayout _appLayout;
	LayoutParams _lp;
	Context _context;
	SearchForm _search;
	RecentSearches _history;
	Boolean _connected = false;
	SearchResults _results;
	HashMap<String, String> _queryCache;
	String _helpText = "Enter a search term and touch \"Go\".";
	ImageView _logo;
	Intent _historyActivity;
	JSONArray _jsonResults;
	
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
        _search = new SearchForm(_context, "Search Query", "Go", "History");
        _appLayout.addView(_search);
        
        
        // Drop in the big form of the logo
        _logo = new ImageView(_context);
        _logo.setImageResource(R.drawable.quick_search);
        _appLayout.addView(_logo);
        
        // Initialize the history viewer
        _historyActivity = new Intent(_context, HistorySelector.class);
        
        // handle the search button click
        _search.getButton().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				String searchTerm = _search.getField().getText().toString();
				
				if (searchTerm.length() == 0) {
					// refuse to search if the query is blank
					_results.addRow(_helpText);
					
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
							_results.addRow("No cached results for your query");
							Toast toast = Toast.makeText(_context, "No cached results found", Toast.LENGTH_SHORT);
							toast.show();
						} else {
							// looks like the cache hit, show it
							Toast toast = Toast.makeText(_context, "Using a cached query result", Toast.LENGTH_SHORT);
							toast.show();
							displayResults(cachedResults);
						}
						
					}
					// Add the search query to the history
					_history.addQuery(searchTerm);
					
					
					
				}
				
			}
		});
        
        // handle history button click
        _search.getHistoryButton().setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivityForResult(_historyActivity, 0);
			}
		});
        
        _history = new RecentSearches(_context);
        // get the previous search terms
        for (String key: _queryCache.keySet()) {
        	_history.addQuery(key);
        }
        _history._list.setOnItemSelectedListener(this);
        //_appLayout.addView(_history);
        
        _results = new SearchResults(_context);
        _appLayout.addView(_results);
        
        // internet connection logic
        _connected = Internet.getConnectionStatus(_context);
        
        _results.addRow(_helpText);
        
        if (_connected.booleanValue() == true) {
        	// no need to let the user know we detected internet since this is the assumed state
        	// this is kept for any internet specific logic
		} else {
			// alert the user that there is no internet
			Toast toast = Toast.makeText(_context, "NO INTERNET CONNECTION", Toast.LENGTH_LONG);
			toast.show();
			
			_results.addRow("Unable to detect an internet connection.");
			
			if (_queryCache.isEmpty()) {
				// looks like there are 0 cached queries
				_results.addRow("You have no cached queries and will be unable to search for anything");
			} else {
				_results.addRow("You have "+_queryCache.size() + "cached queries. Some results may be found.");
			}
		}
        
        ListView listView = _results.getResultList();
        listView.setTextFilterEnabled(true);
        listView.setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView<?> parent, View view,
        					int pos, long id) {
        		try {
        			JSONObject tweet = _jsonResults.getJSONObject(pos);
            		//Toast toast = Toast.makeText(_context, tweet.getString("text"), Toast.LENGTH_SHORT);
            		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(tweet.getString("profile_image_url").replace("_normal", "")));
            		startActivity(browserIntent);
            		//toast.show();
        		} catch (JSONException e) {
        			Log.e("JSON", "JSON OBJECT EXCEPTION");
        		}
        		
        	}
        });
  
        
        setContentView(_appLayout);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      if (resultCode == RESULT_OK && requestCode == 0) {
        Bundle result = data.getExtras();
        _search.getField().setText(result.get("query").toString());
        _search.getButton().performClick();
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
    			
    			// hide the logo
    			_appLayout.removeView(_logo);
    			
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
		
    		for (int i = 0; i < results.length(); i++) {
    			// build the tweet and add it
    			JSONObject tweet = results.getJSONObject(i);
    			String r = "@"+tweet.getString("from_user")+": "+tweet.getString("text");
    			_results.addRow(r);
    		
			}
    		if(results.length() == 0) {
    			_results.addRow("No tweets found.");
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
    
    public void onItemSelected(AdapterView<?> parent, View view, 
            int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        Log.i("SELECTED", parent.getItemAtPosition(pos).toString());
        Toast toast = Toast.makeText(_context, "OMEGERD! SLEKTED", Toast.LENGTH_SHORT);
		toast.show();
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    	Log.i("SELECTED", "NOTHING");
    }
}
