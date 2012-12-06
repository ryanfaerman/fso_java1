package com.nwitty.java1.twitterquicksearch;

import java.util.ArrayList;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class SearchResults extends LinearLayout {
	Context _context;
	ListView _list;
	ArrayList<String> _results = new ArrayList<String>();
	
	public SearchResults(Context context) {
		super(context);
		_context = context;
		
		LayoutParams lp;
		_list = new ListView(_context);
		lp = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1.0f);
		_list.setLayoutParams(lp);
		
		ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(_context, android.R.layout.simple_list_item_1, _results);
		listAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
		_list.setAdapter(listAdapter);
		
		this.addView(_list);
	}
	
	public ListView getResultList() {
		return _list;
	}
	
	public void addRow(String text) {
		_results.add(text);
		_list.invalidateViews();
	}
	
	public void reset() {
		_results.clear();
		_list.invalidateViews();
	}
}
