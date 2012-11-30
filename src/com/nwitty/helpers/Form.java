package com.nwitty.helpers;

import android.content.Context;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class Form {
	public static int PRIMARY_BUTTON = 1;
	public static int TEXT_FIELD = 2;
	
	
	// show a basic row, a la the videos
	public static LinearLayout entryRowWithButton(Context context, String hint, String buttonText) {
		LinearLayout ll = new LinearLayout(context);
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		ll.setLayoutParams(lp);
		
		EditText et = new EditText(context);
		lp = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1.0f);
		et.setHint(hint);
		et.setId(TEXT_FIELD);
		et.setLayoutParams(lp);
		
		Button b = new Button(context);
		b.setText(buttonText);
		b.setId(PRIMARY_BUTTON);
		b.setTag(et);
		
		ll.addView(et);
		ll.addView(b);
		
		return ll;
	}
}
