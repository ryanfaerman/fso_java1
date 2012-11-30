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
        
        currentPage = 1;
        
        ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        ll.setLayoutParams(lp);
        
        LinearLayout queryRow = Form.entryRowWithButton(this, "Search Query", "Go");
        ll.addView(queryRow);
        Button searchButton = (Button) queryRow.findViewById(Form.PRIMARY_BUTTON);
        searchButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ll.removeView(resultLayout);
				ll.removeView(paginator);
				ll.addView(paginator);
				ll.addView(resultLayout);
			}
		});
        
       
        tweets = new ArrayList<Tweet>();
        for(int i=0; i<50; i++) {
        	tweets.add(new SearchResult(getString(R.string.twitter_user), getString(R.string.twitter_text)));
        }
        
        
        paginator = new LinearLayout(this);
        lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        paginator.setLayoutParams(lp);
        
        Button prevButton = new Button(this);
        prevButton.setText("<-");
        lp = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1.0f);
        prevButton.setLayoutParams(lp);
        prevButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(currentPage > 1) {
					currentPage--;
					
				}
				tootsForPage(currentPage);
			}
		});
        paginator.addView(prevButton);
        
        tv = new TextView(this);
        limits = TweetResultLimit.pageCount(tweets.size());
        tv.setText("Page 1 of "+limits.get(TweetResultLimit.SMALL).toString());
        lp = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 2.0f);
        tv.setLayoutParams(lp);
        paginator.addView(tv);
        
        Button nextButton = new Button(this);
        nextButton.setText("->");
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
        paginator.addView(nextButton);
        
        
        
        resultLayout = new LinearLayout(this);
        lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        resultLayout.setLayoutParams(lp);
        
        
    	tootsForPage(currentPage);
        
        
        setContentView(ll);
    }
    
    public void setToots(String toots) {
    	TextView toot = new TextView(this);
    	
    	lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    	toot.setLayoutParams(lp);

        toot.setText(toots);
        resultLayout.removeAllViews();
        resultLayout.addView(toot);
    }
    
    public void tootsForPage(int p) {
    	tv.setText("Page "+Integer.toString(p)+" of "+limits.get(TweetResultLimit.SMALL).toString());
    	String tootLoot = "";
    	int k = ((p-1)*TweetResultLimit.SMALL.value);
    	Log.i("OFFSET: ", Integer.toString(k));
        for(int i=k; i< (TweetResultLimit.SMALL.value*p); i++) {
        	SearchResult r = (SearchResult) tweets.get(i);
        	tootLoot = tootLoot + Integer.toString(p) +" @"+r.getUser()+": "+r.getText()+"\r\n----\r\n";
        }
        
        setToots(tootLoot);
    }
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
}
