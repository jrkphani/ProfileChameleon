package com.example.alarmmanager;

import java.util.ArrayList;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


public class Service_class extends Service {


    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
    	SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor settingsEditor = settings.edit();
		//setting value 1 , to specify the service status is running 
		settingsEditor.putInt("serviceStatus", 1);
		settingsEditor.commit();
    	AudioManager audio = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        int vibrate = 0;
        int i=0;
		new ArrayList<String>();
		
		Uri calendaruri = Uri.parse("content://com.android.calendar/events");
		Cursor mCursor;
		//String selection = "((" + Calendars.ACCOUNT_NAME + " = ?) AND ("+ Calendars.ACCOUNT_NAME + " = ?) AND ("+ Calendars.ACCOUNT_NAME + " = ?))";
		//String[] selectionArgs = new String[] {"manimani1014@gmail.com","manikandan@digitalchakra.in","test@gmail.com"}; 
		final String[] EVENT_PROJECTION = new String[] {
				    Calendars._ID,
				    Calendars.ACCOUNT_NAME,
				    Calendars.CALENDAR_DISPLAY_NAME,
				    Calendars.OWNER_ACCOUNT,
				    Events.DTEND,
				    Events.DTSTART
				};
		int account_selected_size = settings.getInt("accounts_selected_size", 0);
		int account_selected_all = settings.getInt("accounts_selected_all", 0);
		if(account_selected_all == 0)
		{
			if(account_selected_size>0)
			{
				String acc_name="";
				for(i=0; i<account_selected_size; i++)
				{
					//get account name
					acc_name = settings.getString("acc_selected"+i, null);
					if(acc_name != null && vibrate == 0)
					{
						String selection = "((" + Calendars.ACCOUNT_NAME + " = ?))";
						String[] selectionArgs = new String[] {acc_name};
						mCursor = getContentResolver().query(calendaruri, EVENT_PROJECTION, selection, selectionArgs, null);
						if(mCursor.getCount()>0)
						 {							
							vibrate = Check_event(mCursor);
							 
							 if(vibrate==1)
							 {
								//get prefer mode
								String modeToSet = settings.getString("configMode", "VIBRATE");
								Change_profile(modeToSet);
							 }
							 else
							 {
								 if(audio.getRingerMode() !=2)
								 {
									 //set to normal mode when there is no event
									 Change_profile("NORMAL");
								 }
								 
							 }
						 }
						
					}
					           
				}
			}
    	}
    else
    {
    	//Log.d("all account","all account ===");
    	mCursor = getContentResolver().query(calendaruri, null, null, null, null);
    	if(mCursor.getCount()>0)
		 {							
			vibrate = Check_event(mCursor);
			 
			 if(vibrate==1)
			 {
				//get prefer mode
				String modeToSet = settings.getString("configMode", "VIBRATE");
				Change_profile(modeToSet);
			 }
			 else
			 {
				 if(audio.getRingerMode() !=2)
				 {
					 //set to normal mode when there is no event
					 audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
				 }
				 
			 }
		 }
    	
    }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        //Toast.makeText(this, "Service Stopped", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

	@SuppressLint("NewApi")
	private void notification_top(String mode_changed)
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
		        .setContentText("Your phone is set to "+mode_changed+" mode")
		        .setContentIntent(resultPendingIntent)
		        .setAutoCancel(true);

		NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		manager.notify(mId, mBuilder.build());
    }
    
    private int Check_event(Cursor mCursor)
    {
    	mCursor.moveToFirst();
    	 int vibrate=0;
    	 SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
    	 int selectedHrs = settings.getInt("selectedHrs", 9);
		 while(mCursor.moveToNext())
		 {
			 long currentTime = new Date().getTime();
			 long eventSTime = mCursor.getLong(mCursor.getColumnIndex("dtstart"));
			 long eventETime = mCursor.getLong(mCursor.getColumnIndex("dtend"));
			 
			/*convert to Hrs*/
			 long eventTime = (eventETime - eventSTime)/3600000;
			 /*System.out.println("=======title======="+mCursor.getString(mCursor.getColumnIndex("title")));
			 System.out.println("=======ssssssss======="+eventSTime);
			 System.out.println("=======eeeeee========"+eventETime);
			 System.out.println("=======ttttttt========"+eventTime);*/
				 if((currentTime >= eventSTime ) && (currentTime <= eventETime) && (eventTime <= selectedHrs))
				 {
						 vibrate=1;
						 //System.out.println("thissssss eventttttttt");
				 }

		 }
		 return  vibrate;
    }
    private String Change_profile(String modeToSet)
    {
    	int notify = 0;
    	AudioManager audio = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
    	//change profile mode to user preferred mode when there is an event
    	int current_mode = audio.getRingerMode();
    	//Log.d("eeee","eeee");
    	System.out.println(current_mode);
    	//Log.d("ffff","fffff");
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
			         break;
			 }
		 }
		 if(notify == 1)
		 {
			//notification
			 notification_top(modeToSet);
		 }
		return modeToSet;
		 
		 
    }

}