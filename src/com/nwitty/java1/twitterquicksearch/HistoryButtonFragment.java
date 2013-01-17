package com.nwitty.java1.twitterquicksearch;

import com.nwitty.java1.twitterquicksearch.SearchFragment.SearchListener;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class HistoryButtonFragment extends Fragment {
	private HistoryButtonListener listener;
	
	public interface HistoryButtonListener {
		public void onHistoryList();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		Log.i("TRACE", "historyButtonFragment initializing...");
		
		LinearLayout view = (LinearLayout) inflater.inflate(R.layout.historybutton, container, false);
		Log.i("TRACE", "historyButtonFragment inflated.");
		
		((Button) view.findViewById(R.id.historyButton)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.i("TRACE", "historyButtonFragment historybutton clicked");
				listener.onHistoryList();
			}
		});
		

		Log.i("TRACE", "historyButtonFragment done.");
		return view;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.i("TRACE", "attaching historyButtonFrag");
		try {
			listener = (HistoryButtonListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement HistoryButtonListener");
		}
	}
}
