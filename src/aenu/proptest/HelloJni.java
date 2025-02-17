/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package aenu.proptest;

import android.app.Activity;
import android.widget.TextView;
import android.os.Bundle;
import android.widget.*;
import android.app.*;
import android.content.*;
import android.view.*;
import java.io.*;
import android.text.*;
import android.text.style.*;
import android.net.*;
import android.text.method.*;
import android.graphics.*;


public class HelloJni extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		/* Create a TextView and set its content.
         * the text is retrieved by calling a native
         * function.
         */
		String _text=stringFromJNI();
		String auther="路人aenu";
		
		SpannableString text=new SpannableString(_text);
		
		int s=_text.indexOf(auther);
		int e=s+auther.length();
		
		text.setSpan(new ClickableSpan() {
			@Override
			public void onClick(View widget) {
				String url = "https://b23.tv/GvWfCmG";
				
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				if (intent.resolveActivity(getPackageManager())!= null) {
					startActivity(intent);
				}
			}
		}, s,e, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  
		
		s=_text.indexOf("https://");
		e=_text.indexOf(" ",s);
		
		final String url = _text.substring(s,e);
		
		text.setSpan(new ClickableSpan() {
				@Override
				public void onClick(View widget) {
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
					if (intent.resolveActivity(getPackageManager())!= null) {
						startActivity(intent);
					}
				}
			}, s,e, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  
		
        TextView  tv = new TextView(this);
        tv.setText(text);
		
		tv.setMovementMethod(LinkMovementMethod.getInstance());
		tv.setHighlightColor(Color.TRANSPARENT);
		//setContentView(tv);
		ScrollView sv=new ScrollView(this);
		sv.addView(tv);
        setContentView(sv);
    }

    /* A native method that is implemented by the
     * 'hello-jni' native library, which is packaged
     * with this application.
     */
    public native String  stringFromJNI();
}
