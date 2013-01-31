package com.nwitty.java1.twitterquicksearch;


import com.markupartist.android.widget.PullToRefreshListView;
import com.markupartist.android.widget.PullToRefreshListView.OnRefreshListener;
import com.nwitty.java1.twitterquicksearch.HistoryFragment.ResultListener;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class ResultsFragment extends Fragment {
	private RefreshListener listener;
	
	public interface RefreshListener {
		public void onRefresh();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		Log.i("TRACE", "resultFragment initializing...");
		
		LinearLayout view = (LinearLayout) inflater.inflate(R.layout.results, container, false);
		
		((PullToRefreshListView) view.findViewById(R.id.searchResults)).setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Do work to refresh the list here.
               Log.i("TRACE", "REFRESH BITCHES");
               listener.onRefresh();
               
            }
        });
		
		return view;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.i("TRACE", "attaching resultsfragment");
		try {
			listener = (RefreshListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement RefreshListener");
		}
	}
}
