/*******************************************************************************
 * Copyright (C) 2011 Robert Theis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package org.moocology.ocr;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;

public final class TranslateAsyncTask extends AsyncTask<String, Void, Boolean> {

	
	  private Language sourceLanguage;
	  private Language targetLanguage;
	  private String text;
	  private String translatedText;
	  private TextView textView;
	  private ProgressBar progressBar;
	  private ContentResolver contentResolver;
	  private Uri uri;
	  private LinearLayout layout;


	  public TranslateAsyncTask(String text, Language sourceLanguage, Language targetLanguage,
                                TextView textView, ProgressBar progressBar, ContentResolver contentResolver, Uri uri, LinearLayout layout) {
	    this.sourceLanguage = sourceLanguage;
	    this.targetLanguage = targetLanguage;
	    this.text = text;
	    this.textView = textView;
		this.progressBar=progressBar;
		this.contentResolver = contentResolver;
		this.uri =uri;
		this.layout=layout;

	  }
	  
	  @Override
	  protected void onPreExecute() {
	    super.onPreExecute();
	    
//	    textView.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL), Typeface.NORMAL);
//	    textView.setTextSize(14);
	    textView.setText("Translating...");
	    progressBar.setVisibility(View.VISIBLE);

	  }
	  
	  @Override
	  protected  Boolean doInBackground(String... arg0) {
		 Translate.setClientId(CONST.MICROSOFT_TRANSLATE_ID);
		Translate.setClientSecret(CONST.MICROSOFT_TRANSLATE_SECRET);
	   
	    
	   
	    try {
	      // Request translation
	      translatedText = Translate.execute(text, sourceLanguage, targetLanguage);
	    } catch (Exception e) {
	      e.printStackTrace();
	      return false;
	    }
	    return true;
	  }

	  @Override
	  protected void onPostExecute(Boolean result) {
	    super.onPostExecute(result);
		  progressBar.setVisibility(View.GONE);
		  textView.setVisibility(View.VISIBLE);
	    if (result) {
	      // Reset the text formatting
	      if (textView != null) {
	        textView.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL), Typeface.NORMAL);
	      }
	      // Put the translation into the textview

	      textView.setText(translatedText);
			ContentValues initialValues = new ContentValues();
			initialValues.put(CONST.TRANSLATION, translatedText);
        int updNumber = contentResolver.update(uri,initialValues, null, null);


			layout.setVisibility(View.GONE);
	     /* // Crudely scale betweeen 22 and 32 -- bigger font for shorter text
	      int scaledSize = Math.max(22, 32 - translatedText.length() / 4);
	      textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, scaledSize);

*/
	    } else {
	      textView.setText("Unavailable");
	    }
	  }

	}