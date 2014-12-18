package in.digitalchakra.profilechameleon;

import java.util.Calendar;

import in.digitalchakra.profilechameleon.R;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.Settings.System;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	public boolean serviceStatus;
@Override
public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    int orientation = getResources().getConfiguration().orientation;
    View mainLayout = findViewById(R.id.home_page);
    //TextView status_txt = (TextView) findViewById(R.id.status_txt);
    

    // Checks the orientation of the screen
    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {    
        mainLayout.setBackgroundResource(R.drawable.bg_change);
     //   btnStop_p.topMargin=50;
    } else {
        mainLayout.setBackgroundResource(R.drawable.bg);
       // btnStop_p.topMargin=90;
    }
    
    ///
    final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
	final SharedPreferences.Editor settingsEditor = settings.edit();
    // Start service using AlarmManager

    final Calendar cal = Calendar.getInstance();
    cal.add(Calendar.SECOND, 10);
    Intent intent = new Intent(MainActivity.this, Service_class.class);
    final PendingIntent pintent = PendingIntent.getService(MainActivity.this, 0, intent,
            0);
    final AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
    /*every 1 min*/
    /*
     * disable to stop service by default when they install first time
     
    alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
    		1*60*1000, pintent);
    		*/
    /*every 5 secs
    alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
            1000 * 10, pintent);*/
  //setting value 0 , to specify the service status is not running 
    // click listener for the button to start service
    //final Button btnStart = (Button) findViewById(R.id.startserviceBtn);
    final Button btnStartStop = (Button) findViewById(R.id.startstopserviceBtn);
    final TextView status_txt = (TextView) findViewById(R.id.status_txt);
    Bundle extras = getIntent().getExtras();
    if(extras != null)
    {
        String show_popup = extras.getString("show_popup");
    	//Log.d("extrs",show_popup);
        if(show_popup.equals("yes"))
        {
        	rate_me_popup();
        }
    }
    btnStartStop.setOnClickListener(new View.OnClickListener() {

        @Override
        public void onClick(View v) {
        	
        	////
        	
        	int serviceStatus = settings.getInt("serviceStatus", 0);
            if(serviceStatus == 0)
            {
            	btnStartStop.setBackgroundResource(R.drawable.start);
            	startService(new Intent(getBaseContext(), Service_class.class));
            	/*
            	 1*10*1000 -> every 10 sec
            	 
            	 if changing the timer here, also change it in ItudeMobileBroadcastReceiver.java class
            	 */
                alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                		1*120*1000, pintent);
                settingsEditor.putInt("serviceStatus", 1);
                settingsEditor.commit();
                status_txt.setText(R.string.description_main2);
            	Toast.makeText(getBaseContext(),"Service started", Toast.LENGTH_SHORT).show();
            }
            else
            {
            	btnStartStop.setBackgroundResource(R.drawable.stop);
            	stopService(new Intent(getBaseContext(), Service_class.class));
            	//stop service and role back profile
            	int is_event_active  = settings.getInt("is_event_active", 0);
            	if(is_event_active == 1)
            	{
            		settingsEditor.putInt("is_event_active", 0);
					settingsEditor.commit();
					int previous_mode = settings.getInt("previous_mode", 2);
					switch (previous_mode)
					 {
					     case AudioManager.RINGER_MODE_SILENT:
					    	 Change_profile("SILENT");
					         break;
					     case AudioManager.RINGER_MODE_VIBRATE:
					    	 Change_profile("VIBRATE");
					    	 break;
					     case AudioManager.RINGER_MODE_NORMAL:
					    	 Change_profile("NORMAL");
					    	 break;
					 }
            	}
                alarm.cancel(pintent);
                settingsEditor.putInt("serviceStatus", 0);
                settingsEditor.putString("event_title",null);
                settingsEditor.commit();
                status_txt.setText(R.string.description_main1);
                Toast.makeText(getBaseContext(),"Service stopped", Toast.LENGTH_SHORT).show();
            }
            int account_selected_size = settings.getInt("accounts_selected_size", -1);
        	int account_selected_all = settings.getInt("accounts_selected_all", -1);
        	if(account_selected_size ==-1 && account_selected_all ==-1)
        	{
        		Intent Configintent = new Intent(getBaseContext(),ConfigActivity.class);
            	startActivity(Configintent);
        	}

        }
    });

    
 // click config for to go to configuration intent 
    final ImageView btnConfig = (ImageView) findViewById(R.id.configBtn);
    btnConfig.setOnClickListener(new View.OnClickListener() {

        @Override
        public void onClick(View v) {
        	Intent Configintent = new Intent(getBaseContext(),ConfigActivity.class);
        	//Log.d("Click status", "config clicked");
        	//Toast.makeText(getBaseContext(),"SELECTED", Toast.LENGTH_SHORT).show();
        	startActivity(Configintent);
        }
    });
    int serviceStatus = settings.getInt("serviceStatus", 0);
    if(serviceStatus == 0)
    {
    	btnStartStop.setBackgroundResource(R.drawable.stop);
    	//Toast.makeText(getBaseContext(),"service not runing"+serviceStatus, Toast.LENGTH_SHORT).show();
    	status_txt.setText(R.string.description_main1);
    }
    else
    {
    	btnStartStop.setBackgroundResource(R.drawable.start);
    //	Toast.makeText(getBaseContext(),"service runing"+serviceStatus, Toast.LENGTH_SHORT).show();
    	status_txt.setText(R.string.description_main2);
    }
    
    //Alert user  on first install
    int account_selected_size = settings.getInt("accounts_selected_size", -1);
	int account_selected_all = settings.getInt("accounts_selected_all", -1);
	if(account_selected_size ==-1 && account_selected_all ==-1)
	{
		//Toast.makeText(getBaseContext(), "Fisrt", Toast.LENGTH_LONG).show();
		serviceStatus = settings.getInt("serviceStatus", -1);
		if(serviceStatus == -1)
		{
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
 
			// set title
			alertDialogBuilder.setTitle("Guide");
 
			// set dialog message
			alertDialogBuilder
				.setMessage("Click on the Chameleon icon to turn the services on!")
				.setCancelable(false)
				/*.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						// if this button is clicked, close
						// current activity
						MainActivity.this.finish();
					}
				  })*/
				.setNegativeButton("OK",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						// if this button is clicked, just close
						// the dialog box and do nothing
						dialog.cancel();
					}
				});
 
				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();
 
				// show it
				alertDialog.show();
		}
		else
		{
			Intent Configintent = new Intent(getBaseContext(),ConfigActivity.class);
        	startActivity(Configintent);
		}
	}
    
}
public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    int orientation = getResources().getConfiguration().orientation;
    View mainLayout = findViewById(R.id.home_page);
    //TextView status_txt = (TextView) findViewById(R.id.status_txt);
    

    // Checks the orientation of the screen
    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {    
        mainLayout.setBackgroundResource(R.drawable.bg_change);
     //   btnStop_p.topMargin=50;
    } else {
        mainLayout.setBackgroundResource(R.drawable.bg);
       // btnStop_p.topMargin=90;
    }
  }
@Override
public boolean onCreateOptionsMenu(Menu menu)
	{
	 //menu.add(1, 1, 0, "About").setIcon(R.drawable.notification_icon).setOnMenuItemClickListener(new OnMenuItemClickListener() {
	menu.add(1, 1, 0, "About").setOnMenuItemClickListener(new OnMenuItemClickListener() {
		@Override
		public boolean onMenuItemClick(MenuItem item) {
			// TODO Auto-generated method stub
			//Toast.makeText(getApplicationContext(), "About Digitalchakra page ...", Toast.LENGTH_LONG).show();
			Intent Configintent = new Intent(getBaseContext(),AboutActivity.class);
        	startActivity(Configintent);
			return false;
		}
	});
	menu.add(1, 1, 0, R.string.rate_me).setOnMenuItemClickListener(new OnMenuItemClickListener() {
		@Override
		public boolean onMenuItemClick(MenuItem item) {
			String appPackage = getBaseContext().getPackageName();
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackage));
			startActivity(intent);
			return false;
		}
	});
  /*   menu.add(1, 2, 1, "Developer").setIcon(R.drawable.notification_icon).setOnMenuItemClickListener(new OnMenuItemClickListener() {
		
		@Override
		public boolean onMenuItemClick(MenuItem item) {
			// TODO Auto-generated method stub
			Toast.makeText(getApplicationContext(), "Manikandan R, S P Balaji & Prabakaran ", Toast.LENGTH_LONG).show();
			return false;
		}
	});*/
     menu.add(1, 1, 1, "Settings").setOnMenuItemClickListener(new OnMenuItemClickListener() {
		
		@Override
		public boolean onMenuItemClick(MenuItem item) {
			// TODO Auto-generated method stub
			Intent Configintent = new Intent(getBaseContext(),ConfigActivity.class);
        	startActivity(Configintent);
			return false;
		}
	});
     //menu.add(1, 4, 3, "Item4").setIcon(R.drawable.notification_icon);
     //menu.add(1, 5, 4, "Item5").setIcon(R.drawable.notification_icon);
	 return true;
	}

private String Change_profile(String modeToSet)
{
	SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
	SharedPreferences.Editor settingsEditor = settings.edit();
	settingsEditor.putInt("is_event_active", 0);
	settingsEditor.commit();
	int notify = 0;
	String notification_message="";
	AudioManager audio = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
	//change profile mode to user preferred mode when there is an event
	int current_mode = audio.getRingerMode();
	//System.out.println(current_mode);
	 //refer the comparing string ConfigActivity
	 if(modeToSet.equals("SILENT"))
	 {
		 //change mode if mode is not in 'SILENT' 
		// Toast.makeText(getApplicationContext(), "SILET CON", Toast.LENGTH_SHORT).show();
		 switch (current_mode)
		 {
		     case AudioManager.RINGER_MODE_SILENT:
		    	 notify=0;
		         break;
		     default:
		    	 audio.setRingerMode(AudioManager.RINGER_MODE_SILENT);
				 notify=1;
				 notification_message = "Service stopped, phone set to Silent";
		         break;
		 }
	 }
	 else if(modeToSet.equals("VIBRATE"))
	 {
		//change mode if mode is not in 'VIBRATE'
		// Toast.makeText(getApplicationContext(), "VIBRATE CON", Toast.LENGTH_SHORT).show();
		 switch (current_mode)
		 {
		     case AudioManager.RINGER_MODE_VIBRATE:
		    	 notify=0;
		         break;
		     default:
		    	 audio.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
				 Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);				 
				// Vibrate for 300 milliseconds
				 v.vibrate(300);
				 notify=1;
				 notification_message = "Service stopped, phone set to Vibrate";
		         break;
		 }
	 }
	 else if(modeToSet.equals("NORMAL"))
	 {
		//change mode if mode is not in 'NORMAL'
		// Toast.makeText(getApplicationContext(), "NORMAL CON", Toast.LENGTH_SHORT).show();
		 switch (current_mode)
		 {
		     case AudioManager.RINGER_MODE_NORMAL:
		    	 notify=0;
		         break;
		     default:
		    	 audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
				 Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			     Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
			     r.play();
			     notification_message = "Service stopped, phone set to Normal";
				 notify=1;
		         break;
		 }
	 }
	 else
	 {
		//change profile mode default case to vibrate if any error occurs
		 switch (current_mode)
		 {
		     case AudioManager.RINGER_MODE_VIBRATE:
		    	 notify=0;
		         break;
		     default:
		    	 audio.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
				 Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);				 
				// Vibrate for 300 milliseconds
				 v.vibrate(300);
				 notify=1;
				// Event "[EVENT_NAME]" in progress. Phone set to [Vibrate, Silent, Normal] mode.
				 notification_message = "Service stopped, phone set to Vibrate";
		         break;
		 }
	 }
	 if(notify == 1)
	 {
		//notification
		 notification_top(modeToSet, notification_message);
	 }
	return modeToSet;
	 
	 
}
private void notification_top(String mode_changed, String notification_message)
{
	//this md will be used to modify the notification
    final int mId = 1014;
    Resources appR = this.getResources();
    CharSequence notif_title  = appR.getText(appR.getIdentifier("app_name",
    "string", this.getPackageName()));
    Intent resultIntent = new Intent(getBaseContext(), MainActivity.class);
	PendingIntent resultPendingIntent =
	    PendingIntent.getActivity(getBaseContext(), 0,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);

	NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
	        getApplicationContext()).setSmallIcon(R.drawable.ic_launcher)
	        .setContentTitle(notif_title)
	        .setContentText(notification_message)
	        .setContentIntent(resultPendingIntent)
	        .setAutoCancel(true);

	NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
	manager.notify(mId, mBuilder.build());
}
private void rate_me_popup()
{
	AlertDialog alertDialog = new AlertDialog.Builder(this).create();

    alertDialog.setTitle("Rate Profile Chameleon");

    alertDialog.setMessage("We hope you are enjoying Profile Chameleon. Please take a moment to rate us on the Play Store. Thanks for your support!");

    
    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Later", new DialogInterface.OnClickListener() {

        public void onClick(DialogInterface dialog, int id) {
        	SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        	SharedPreferences.Editor settingsEditor = settings.edit();
			Calendar c = Calendar.getInstance(); 
			int thisday = c.get(Calendar.DAY_OF_MONTH);
        	settingsEditor.putInt("previous_day", thisday);
			settingsEditor.commit();
      }});
    
    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "No Thanks", new DialogInterface.OnClickListener() {

        public void onClick(DialogInterface dialog, int id) {
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        	SharedPreferences.Editor settingsEditor = settings.edit();
        	settingsEditor.putInt("show_rate_me_notification", 0);
			settingsEditor.commit();

      }});
    
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Rate Now", new DialogInterface.OnClickListener() {

      public void onClick(DialogInterface dialog, int id) {
    	SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
      	SharedPreferences.Editor settingsEditor = settings.edit();
      	settingsEditor.putInt("show_rate_me_notification", 0);
      	settingsEditor.commit();
    	String appPackage = getBaseContext().getPackageName();
    	Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackage));
    	startActivity(intent);

    } });     
    alertDialog.show();
}
}