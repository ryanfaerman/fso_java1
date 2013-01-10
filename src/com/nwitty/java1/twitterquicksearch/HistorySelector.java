package com.nwitty.java1.twitterquicksearch;

import java.util.HashMap;

import com.nwitty.helpers.FileHelpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;

public class HistorySelector extends Activity implements OnItemSelectedListener {
	
	Context _context;
	LinearLayout _mainLayout;
	LayoutParams _lp;
	SearchForm _search;
	RecentSearches _history;
	String _query = "";
	HashMap<String, String> _queryCache;
	SearchResults _recentSearchList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		_context = this;
		// Initialize the main layout
        _mainLayout = new LinearLayout(_context);
        _mainLayout.setOrientation(LinearLayout.VERTICAL);
        _lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        _mainLayout.setLayoutParams(_lp);
        
        
        _recentSearchList = new SearchResults(_context);
        _mainLayout.addView(_recentSearchList);
        
        
        _queryCache = loadHistory();
        
        // get the previous search terms
        for (String key: _queryCache.keySet()) {
        	_recentSearchList.addRow(key);
        }
        
        ListView listView = _recentSearchList.getResultList();
        listView.setTextFilterEnabled(true);
        listView.setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView<?> parent, View view,
        					int pos, long id) {
        		_query = parent.getItemAtPosition(pos).toString();
        		finish();
        	}
        });
        
        setContentView(_mainLayout);
	}
	
	public void finish() {
		Intent data = new Intent();
	    data.putExtra("query", _query);
	    setResult(RESULT_OK, data);
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
    
    public void onItemSelected(AdapterView<?> parent, View view, 
            int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        
        _query = parent.getItemAtPosition(pos).toString();
        Log.i("SELECTED", _query);
    }
    
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    	Log.i("SELECTED", "NOTHING");
    }
}
