package in.digitalchakra.profilechameleon;

import java.util.ArrayList;
import java.util.Date;

import in.digitalchakra.profilechameleon.R;

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
		//System.out.println("service runing .....");
		int is_event_active  = settings.getInt("is_event_active", 0);
		
    	AudioManager audio = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        int vibrate = 0;
        int i=0;
        int current_mode=2;
        int previous_mode=2;
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
				    Events.TITLE,
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
								if(is_event_active  == 0)
								{
									//get the current mode
									previous_mode = audio.getRingerMode();
									//System.out.println("get previous_mode");
									//System.out.println(previous_mode);
									settingsEditor.putInt("previous_mode", previous_mode);
									settingsEditor.commit();
								}
								
								Change_profile(modeToSet);
							 }
							 else
							 {
								//set to previous mode when there is no event
								//System.out.println("going normal");
								//System.out.println(previous_mode);
								 if(is_event_active == 1)
								 {
									 settingsEditor.putInt("is_event_active", 0);
									 settingsEditor.commit();
									 previous_mode = settings.getInt("previous_mode", 2);
									 current_mode = audio.getRingerMode();
									 if(current_mode != previous_mode)
									 {
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
										     default:
										    	 Change_profile("NORMAL");
										         break;
										 }
									 }
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
					if(is_event_active  == 0)
					{
						//get the current mode
						previous_mode = audio.getRingerMode();
						//System.out.println("get previous_mode");
						//System.out.println(previous_mode);
						settingsEditor.putInt("previous_mode", previous_mode);
						settingsEditor.commit();
					}
					
					Change_profile(modeToSet);
			 }
			 else
			 {
				//set to previous mode when there is no event
				//System.out.println("going normal");
				//System.out.println(previous_mode);
				 if(is_event_active == 1)
				 {
					 settingsEditor.putInt("is_event_active", 0);
					 settingsEditor.commit();
					 previous_mode = settings.getInt("previous_mode", 2);
					 current_mode = audio.getRingerMode();
					 if(current_mode != previous_mode)
					 {
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
						     default:
						    	 Change_profile("NORMAL");
						         break;
						 }
					 }
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
    
    private int Check_event(Cursor mCursor)
    {
    	mCursor.moveToFirst();
    	String event_title = "";
    	 int vibrate=0;
    	 SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
    	 int selectedHrs = settings.getInt("selectedHrs", 10);
		 while(mCursor.moveToNext())
		 {
			 long currentTime = new Date().getTime();
			 long eventSTime = mCursor.getLong(mCursor.getColumnIndex("dtstart"));
			 long eventETime = mCursor.getLong(mCursor.getColumnIndex("dtend"));
			 //String eventTitle  = mCursor.getColumnIndex("title");
			 //System.out.println("event title");
			 //System.out.println(mCursor.getString(mCursor.getColumnIndex("title"))); 
			 
			/*convert to Hrs*/
			 long eventTime = (eventETime - eventSTime)/3600000;
			 /*System.out.println("=======title======="+mCursor.getString(mCursor.getColumnIndex("title")));
			 System.out.println("=======ssssssss======="+eventSTime);
			 System.out.println("=======eeeeee========"+eventETime);
			 System.out.println("=======ttttttt========"+eventTime);*/
				 if((currentTime >= eventSTime ) && (currentTime <= eventETime) && (eventTime <= selectedHrs))
				 {
						 vibrate=1;
						 event_title = mCursor.getString(mCursor.getColumnIndex("title"));
						 //System.out.println("thissssss eventttttttt");
				 }

		 }
		 return  vibrate;
    }
    private String Change_profile(String modeToSet)
    {
    	SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor settingsEditor = settings.edit();
    	settingsEditor.putInt("is_event_active", 1);
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
					 notification_message = "Phone set to Silent";
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
					 notification_message = "Phone set to Vibrate";
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
				     notification_message = "Phone set to Normal";
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
					 notification_message = "Phone set to Vibrate";
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

}