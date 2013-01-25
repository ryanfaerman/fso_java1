package com.nwitty.java1.twitterquicksearch;

import java.util.ArrayList;
import java.util.HashMap;

import com.nwitty.helpers.FileHelpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;

public class HistorySelector extends Activity implements HistoryFragment.ResultListener {
	
	Context _context;
	LayoutParams _lp;
	SearchForm _search;
	RecentSearches _history;
	String _query = "";
	HashMap<String, String> _queryCache;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("TRACE", "history initializing");
		
		setContentView(R.layout.historyfrag);
		
		_context = this;
        
        
        
	}
	
	public void finish() {
		Log.i("TRACE", "history finishing");
		Intent data = new Intent();
	    data.putExtra("query", _query);
	    setResult(RESULT_OK, data);
	    
	    Log.i("TRACE", "history super finishing");
	    super.finish();
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

	@Override
	public void onSelection(String data) {
		Log.i("TRACE", "history on selection");
		_query = data;
		finish();
		
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
}
