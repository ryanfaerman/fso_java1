package com.nwitty.java1.twitterquicksearch;

import java.util.ArrayList;
import java.util.HashMap;



import com.nwitty.helpers.Form;
import com.nwitty.helpers.SearchResult;
import com.nwitty.helpers.Tweet;
import com.nwitty.helpers.TweetResultLimit;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class MainActivity extends Activity {
	// predefine these for use in callbacks and such
	LinearLayout ll;
	LayoutParams lp;
	TextView hiddenView;
	LinearLayout resultLayout;
	ArrayList<Tweet> tweets;
	int currentPage;
	HashMap<TweetResultLimit, Integer> limits;
	LinearLayout paginator;
	TextView tv;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // always start on the first page of search results
        currentPage = 1;
        
        // initialize our main linear layout
        ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        ll.setLayoutParams(lp);
        
        // Build an form entry with the form helper
        LinearLayout queryRow = Form.entryRowWithButton(this, "Search Query", "Go");
        ll.addView(queryRow);
        // isolate the search button for binding
        Button searchButton = (Button) queryRow.findViewById(Form.PRIMARY_BUTTON);
        // setup the click handler
        searchButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// remove these views so they don't get duplicated or anything like that
				ll.removeView(resultLayout);
				ll.removeView(paginator);
				
				// load dem toots!
				tootsForPage(currentPage);
				
				// add the views "refreshing" them with the new results
				ll.addView(paginator);
				ll.addView(resultLayout);
			}
		});
        
        // Build the tweet list
        tweets = new ArrayList<Tweet>();
        for(int i=0; i<50; i++) {
        	// these come from some static stuff in the resources
        	tweets.add(new SearchResult(getString(R.string.twitter_user), getString(R.string.twitter_text)));
        }
        
        // prepare the paginator
        paginator = new LinearLayout(this);
        lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        paginator.setLayoutParams(lp);
        
        // this will be the previous button
        Button prevButton = new Button(this);
        prevButton.setText("<-");
        // 1.0f so it will take 1/4 of the row
        lp = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1.0f);
        prevButton.setLayoutParams(lp);
        prevButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(currentPage > 1) {
					// don't let the current page go to 0
					currentPage--;
					
				}
				// load the required toots
				tootsForPage(currentPage);
			}
		});
        // this is part of the paginator
        paginator.addView(prevButton);
        
        // setup the header showing current location in the toot results
        tv = new TextView(this);
        limits = TweetResultLimit.pageCount(tweets.size());
        tv.setText("Page 1 of "+limits.get(TweetResultLimit.SMALL).toString());
        // 2.0f so it will take 2/4 of the row
        lp = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 2.0f);
        tv.setLayoutParams(lp);
        // this is part of the paginator
        paginator.addView(tv);
        
        // this will be the next button
        Button nextButton = new Button(this);
        nextButton.setText("->");
        // 1.0f so it will take 1/4 of the row
        lp = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1.0f);
        nextButton.setLayoutParams(lp);
        nextButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(currentPage < limits.get(TweetResultLimit.SMALL)) {
					currentPage++;
				}
				tootsForPage(currentPage);
			}
		});
        // this is part of the paginator
        paginator.addView(nextButton);
        // we don't add it to the main LL since we don't want it displayed until there are results!
        
        // prepare the results layout
        resultLayout = new LinearLayout(this);
        lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        resultLayout.setLayoutParams(lp);
        // we don't add it to the main LL since we don't want it displayed until there are results!
        
    	
        
        // show the LL, DUH!
        setContentView(ll);
    }
    
    // build the toots textview
    public void setToots(String toots) {
    	TextView toot = new TextView(this);
    	
    	lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    	toot.setLayoutParams(lp);

        toot.setText(toots);
        // clear any existing toots from the results and show our new toots
        resultLayout.removeAllViews();
        resultLayout.addView(toot);
    }
    
    public void tootsForPage(int p) {
    	// adjust the header to be "page X of PAGES"
    	tv.setText("Page "+Integer.toString(p)+" of "+limits.get(TweetResultLimit.SMALL).toString());
    	
    	// init the string, easier this way, trust me
    	String tootLoot = "";
    	// calculate the number of toots to skip
    	int k = ((p-1)*TweetResultLimit.SMALL.value);
    	
    	// starting at the offset, go to the limit
        for(int i=k; i< (TweetResultLimit.SMALL.value*p); i++) {
        	// retreive the result from the array
        	SearchResult r = (SearchResult) tweets.get(i);
        	tootLoot = tootLoot + Integer.toString(i) +" @"+r.getUser()+": "+r.getText()+"\r\n----\r\n";
        }
        // set dem toots
        setToots(tootLoot);
    }
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
}
