package com.nwitty.helpers;

import java.util.HashMap;

public enum TweetResultLimit {
	MICRO(3),
	SMALL(5),
	MEDIUM(7),
	LONG(11),
	EXTRA_LONG(13);
	
	public int value;
	
	TweetResultLimit(int value) {
		this.value = value;
	}

	
	public static HashMap<TweetResultLimit, Integer> pageCount(int itemCount) {
		HashMap<TweetResultLimit, Integer> pageCounts = new HashMap<TweetResultLimit, Integer>();
		
		TweetResultLimit[] limits = {MICRO, SMALL, MEDIUM, LONG, EXTRA_LONG};
		for(int i=0; i<limits.length; i++) {
			TweetResultLimit limit = limits[i];
			int pageCount = (int) Math.ceil((double)itemCount/limit.value);
			pageCounts.put(limit, pageCount);
		}
		return pageCounts;
	}
	
}
