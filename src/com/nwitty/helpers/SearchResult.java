package com.nwitty.helpers;

public class SearchResult implements Tweet {
	String user;
	String text;
	
	public SearchResult(String user, String text) {
		setUser(user);
		setText(text);
	}
	
	@Override
	public boolean setUser(String u) {
		this.user = u;
		return true;
	}

	@Override
	public boolean setText(String text) {
		this.text = text;
		return true;
	}


	@Override
	public String getUser() {
		return this.user;
	}

	@Override
	public String getText() {
		return this.text;
	}
	
	

}
