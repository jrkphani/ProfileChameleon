package com.example.alarmmanager;

import java.util.Calendar;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class AboutActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		TextView footer = (TextView) findViewById(R.id.copyright);
		if(year <= 2014)
		{
			footer.setText("All rights reserved. \u00a9 " + year);
		}
		else
		{
			footer.setText("All rights reserved. \u00a9 2014 - " + year);
		}
		
		ImageView developed_by = (ImageView) findViewById(R.id.dc_logo);
		developed_by.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://digitalchakra.in/"));
				startActivity(browserIntent);
				
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.about, menu);
		return true;
	}

}
