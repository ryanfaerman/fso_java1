package com.nwitty.java1.week1;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.text.InputType;
import android.util.Log;

public class MainActivity extends Activity {
	
	LinearLayout ll;
	LinearLayout.LayoutParams lp;
	EditText et;
	TextView result;
	
	private static final String TAG = "Java1Week1";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Setup Base layout
        ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        ll.setLayoutParams(lp);
        
        // Setup Intro text
        TextView tv = new TextView(this);
        tv.setText(getString(R.string.intro_text));
        ll.addView(tv);
        
        // Setup Edit field
        et = new EditText(this);
        et.setHint(getString(R.string.hint_text));
        et.setInputType(InputType.TYPE_CLASS_NUMBER);
        
        Button b = new Button(this);
        b.setText(getString(R.string.button_text));
        b.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// Get the decimal entry
				int decimal = Integer.parseInt(et.getText().toString());
				
				// Prepare a flag and output container
				String output = "";
				Boolean hasRemainder = false;
				
				// Edge case of decimal being 0 and not making it through the loop
				if(decimal == 0) {
					output = "0";
				}
				
				while(decimal > 0) {
					hasRemainder = decimal % 2 > 0;
					decimal = decimal / 2;
					
					if(hasRemainder) {
						output = "1" + output;
					} else {
						output = "0" + output;
					}
				}
				
				result.setText(output);
			}
		});
        
        
        // Setup form layout
        LinearLayout form = new LinearLayout(this);
        form.setOrientation(LinearLayout.HORIZONTAL);
        lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        form.setLayoutParams(lp);
        
        // Add form elements
        form.addView(et);
        form.addView(b);
        
        
        // Add form to main view
        ll.addView(form);
        
        // Setup result text
        result = new TextView(this);
        ll.addView(result);
        
        // Display all the things
        setContentView(ll);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
}
