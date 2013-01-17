package com.nwitty.java1.twitterquicksearch;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class HistoryFragment extends Fragment {
	private ResultListener listener;
	
	public interface ResultListener {
		public void onSelection(String data);
		public void initializeHistory();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		Log.i("TRACE", "HistoryFragment initializing...");
		
		LinearLayout view = (LinearLayout) inflater.inflate(R.layout.history, container, false);
		
		
		ListView results = (ListView) view.findViewById(R.id.history_list);
		results.setTextFilterEnabled(true);
		results.setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView<?> parent, View view,
        					int pos, long id) {
        		Log.i("TRACE", "HistoryFragment item selected");
        		listener.onSelection(parent.getItemAtPosition(pos).toString());
        		
        	}
        });
		
		
		return view;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		Log.i("TRACE", "HistoryFragment getting history");
		listener.initializeHistory();
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.i("TRACE", "attaching HistoryFragment");
		try {
			listener = (ResultListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement ResultListener");
		}
	}

}
