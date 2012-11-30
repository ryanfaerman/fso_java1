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

	// this just determines the number of pages a itemcount would be broken down into
	public static HashMap<TweetResultLimit, Integer> pageCount(int itemCount) {
		HashMap<TweetResultLimit, Integer> pageCounts = new HashMap<TweetResultLimit, Integer>();
		
		TweetResultLimit[] limits = {MICRO, SMALL, MEDIUM, LONG, EXTRA_LONG};
		for(int i=0; i<limits.length; i++) {
			TweetResultLimit limit = limits[i];
			// we round up since you can't have a fraction of page, this "adds the page" for the fraction
			int pageCount = (int) Math.ceil((double)itemCount/limit.value);
			pageCounts.put(limit, pageCount);
		}
		return pageCounts;
	}
	
}
