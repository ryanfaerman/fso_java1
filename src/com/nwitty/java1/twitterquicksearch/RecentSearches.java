package com.nwitty.java1.twitterquicksearch;

import java.util.ArrayList;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

public class RecentSearches extends LinearLayout {
	
	Context _context;
	public Spinner _list;
	ArrayList<String> _queries = new ArrayList<String>();
	
	public RecentSearches(Context context) {
		super(context);
		_context = context;
		
		LayoutParams lp;
		_list = new Spinner(_context);
		lp = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1.0f);
		_list.setLayoutParams(lp);
		
		ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(_context, android.R.layout.simple_spinner_item, _queries);
		listAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
		_list.setAdapter(listAdapter);
		
		this.addView(_list);
	}
	
	public Spinner getList() {
		return _list;
	}
	
	public void addQuery(String q) {
		_queries.add(0, q);
		
		// Only store the last 10 queries
		if(_queries.size() > 10) {
			_queries.remove(10);
		}
	}
}
