package com.nwitty.java1.twitterquicksearch;

import android.content.Context;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class SearchForm extends LinearLayout {
	
	EditText _searchField;
	Button _searchButton;
	Button _historyButton;
	
	public SearchForm(Context context, String hint, String buttonText, String historyText) {
		super(context);
		
		LayoutParams lp;
		
		_searchField = new EditText(context);
		lp = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1.0f);
		_searchField.setLayoutParams(lp);
		_searchField.setHint(hint);
		
		_historyButton = new Button(context);
		_historyButton.setText(historyText);
		
		_searchButton = new Button(context);
		_searchButton.setText(buttonText);
		
		
		this.addView(_searchField);
		this.addView(_searchButton);
		this.addView(_historyButton);
	}
	
	public EditText getField() {
		return _searchField;
	}
	
	public Button getButton() {
		return _searchButton;
	}
	
	public Button getHistoryButton() {
		return _historyButton;
	}
}
