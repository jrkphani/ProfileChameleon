package in.digitalchakra.profilechameleon;

import java.util.Calendar;

import in.digitalchakra.profilechameleon.R;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
	public boolean onCreateOptionsMenu(Menu menu)
		{
	     menu.add(1, 1, 0, "Settings").setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				// TODO Auto-generated method stub
				Intent Configintent = new Intent(getBaseContext(),ConfigActivity.class);
	        	startActivity(Configintent);
				return false;
			}
		});
	     menu.add(1, 1, 0, R.string.rate_me).setOnMenuItemClickListener(new OnMenuItemClickListener() {
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					Intent intent = new Intent(Intent.ACTION_VIEW);
					///Try Google play
					intent.setData(Uri.parse("market://details?id=in.digitalchakra.profilechameleon"));
					if (!MyStartActivity(intent)) {
					    //Market (Google play) app seems not installed, let's try to open a webbrowser
					    intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=in.digitalchakra.profilechameleon"));
					    if (!MyStartActivity(intent)) {
					        //Well if this also fails, we have run out of options, inform the user.
					        Toast.makeText(getBaseContext(), "Could not open Android market, please install the market app.", Toast.LENGTH_SHORT).show();
					    }
					}
					return false;
				}
			});
		 return true;
		}
	
	private boolean MyStartActivity(Intent aIntent) {
	    try
	    {
	        startActivity(aIntent);
	        return true;
	    }
	    catch (ActivityNotFoundException e)
	    {
	        return false;
	    }
	}

}
