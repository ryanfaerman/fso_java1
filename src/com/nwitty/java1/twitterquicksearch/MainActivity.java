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



import com.markupartist.android.widget.PullToRefreshListView;
import com.markupartist.android.widget.PullToRefreshListView.OnRefreshListener;

import com.nwitty.helpers.FileHelpers;
import com.nwitty.helpers.Internet;
import com.nwitty.java1.twitterquicksearch.HistoryButtonFragment.HistoryButtonListener;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.app.Activity;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.support.v4.widget.SimpleCursorAdapter;
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

public class MainActivity extends Activity implements SearchFragment.SearchListener, HistoryFragment.ResultListener, HistoryButtonFragment.HistoryButtonListener, ResultsFragment.RefreshListener {
	
	Context _context;
	Boolean _connected = false;
	HashMap<String, String> _queryCache;
	JSONArray _jsonResults;
	String _searchTerm;
	
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
    

    
    private void displayResults(String result) {
    	((PullToRefreshListView) findViewById(R.id.searchResults)).onRefreshComplete();
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
//		getSearchResults(searchTerm);
		_searchTerm = searchTerm;
		Messenger messenger = new Messenger(messageHandler);
		Intent myIntent = new Intent(getApplicationContext(), SearchService.class);
		myIntent.putExtra("MESSENGER", messenger);
		myIntent.putExtra("query", searchTerm);
		startService(myIntent);
		Log.i("TRACE", "attempting to search for "+searchTerm);
		
	}
	
	private Handler messageHandler = new Handler() {
		public void handleMessage(Message message){
		    //HANDLER CODE BODY
			Object result = message.obj;
			if(message.arg1 == RESULT_OK && result != null) {
				
				displayResults((String) result);
			} else {
				Log.i("TRACE", "SEARCH FAILED");
			}
		  }
	};

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
		onSearch(data);
		((EditText) findViewById(R.id.searchField)).setText(data);
	}
	
	@Override
	public void initializeHistory() {
		Log.i("TRACE", "main init history");
		ArrayList<String> recentSearchList = new ArrayList<String>();

        
        String[] projection = new String[] {
        	    HistoryProvider._ID,
        	    HistoryProvider.TERM
        	};
        Log.i("TRACE", "built projection");
        
        Cursor cur = getContentResolver().query(HistoryProvider.CONTENT_URI, projection, null, null, null);
        Log.i("TRACE", "cursor size count" + cur.getCount());
        
        if (cur.moveToFirst()) {
        	Log.i("TRACE", "moved to first");
            String term; 
            int termColumn = cur.getColumnIndex(HistoryProvider.TERM); 
            Log.i("TRACE", "found term column "+ termColumn);
            do {
                // Get the field values
                term = cur.getString(termColumn);
               Log.i("TRACE", "TERM: "+term);
               recentSearchList.add(term);
   
            } while (cur.moveToNext());

        }
        Log.i("TRACE", "done with this cursor");
        ((ListView) findViewById(R.id.history_list)).setAdapter(new ArrayAdapter<String>(this, R.layout.list_tweet,recentSearchList));
		
	}

	@Override
	public void onRefresh() {
		onSearch(_searchTerm);
		
	}
    
}
