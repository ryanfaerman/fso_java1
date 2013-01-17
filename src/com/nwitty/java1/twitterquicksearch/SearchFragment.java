package com.nwitty.java1.twitterquicksearch;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

public class SearchFragment extends Fragment {
	Context _context;
	
	private SearchListener listener;
	
	public interface SearchListener {
		public void onSearch(String searchTerm);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		Log.i("TRACE", "searchFragment initializing...");
		
		LinearLayout view = (LinearLayout) inflater.inflate(R.layout.form, container, false);
		
		((Button) view.findViewById(R.id.searchButton)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i("TRACE", "searchFragment searchbutton clicked");
				
				EditText searchField = (EditText) getActivity().findViewById(R.id.searchField);
				String searchTerm = searchField.getText().toString();
				
//				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//				imm.hideSoftInputFromWindow(searchField.getWindowToken(), 0);
				
				
				if (searchTerm.length() > 0) {

					listener.onSearch(searchTerm);
					
				}
			}
		});
		
//		((Button) view.findViewById(R.id.historyButton)).setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				Log.i("TRACE", "searchFragment historybutton clicked");
//				listener.onHistoryList();
//			}
//		});
		

		
		return view;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.i("TRACE", "attaching SearchFragment");
		try {
			listener = (SearchListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement SearchListener");
		}
	}
}
