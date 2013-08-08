package com.example.alarmmanager;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;


public class Service_class extends Service {


    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
    	AudioManager audio = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        String flag = "need genral profile";
        int general = 0;
        int vibrate = 0;
        final int mId = 1014;
		String str_disp = "";
		String mode_changed = "no";
		String to_push="";
		ArrayList<String> Accounts_list=new ArrayList<String>();
		//String prev_mode_changed = "no";
		final NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(this)
				.setContentTitle("My notification")
		        .setSmallIcon(R.drawable.notification_icon);
		Uri calendaruri = Uri.parse("content://com.android.calendar/events");
		Cursor mCursor = getContentResolver().query(calendaruri, null, null, null, null);
		if(mCursor.getCount()>0)
		 {
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
			SharedPreferences.Editor settingsEditor = settings.edit();
			//get prefer mode
			String modeToSet = settings.getString("configMode", "VIBRATE");
			
			
			mCursor.moveToFirst();
			/////////
			/*
			//to get list fo index 
            for (int i = 0; i < mCursor.getCount(); i++) {
                 // retrieve the calendar names and ids
                 Log.d("==***********====",mCursor.getColumnName(i));
             }
            /////////
             */
			
			 while(mCursor.moveToNext())
			 {
				 long currentTime = new Date().getTime();
				 
				 /*str_disp = str_disp+"Title : "+mCursor.getString(mCursor.getColumnIndex("title"))
						 +"\nStart -Date : "+ mCursor.getLong(mCursor.getColumnIndex("dtstart"))
						 +"\nEnd -- Date : "+ mCursor.getLong(mCursor.getColumnIndex("dtend"))
						 +"\ncurrentTime: "+currentTime+"\n __________________ \n\n";*/
				 //store the account in to array list
				 if(!to_push.equals(mCursor.getString(mCursor.getColumnIndex("account_name"))))
				 {
					 to_push =mCursor.getString(mCursor.getColumnIndex("account_name"));
					 if(!Accounts_list.contains(to_push))
					 {
						 Accounts_list.add(to_push);
					 }
					 /*for (String s : Accounts_list) { 
						 //check if account exist in array
						   if (!s.contains(to_push)) {
							   Accounts_list.add(mCursor.getString(mCursor.getColumnIndex("account_name")));
						   }
						}*/
				 }
				 
					 if((currentTime >= mCursor.getLong(mCursor.getColumnIndex("dtstart")) ) && (currentTime <= mCursor.getLong(mCursor.getColumnIndex("dtend"))))
					 {
							 //audio.setRingerMode(audio.RINGER_MODE_VIBRATE);
							 vibrate=1;
						 //flag ="need silent profile";
						 //audio.setRingerMode(audio.RINGER_MODE_VIBRATE);
						 //Toast.makeText(getApplicationContext(), mCursor.getString(mCursor.getColumnIndex("account_name")), Toast.LENGTH_SHORT).show();
					 }
					/* else
					 {
						 if(vibrate !=1)
						 {
							 general=1;
						 //audio.setRingerMode(audio.RINGER_MODE_NORMAL);
						// Toast.makeText(getApplicationContext(), "n", Toast.LENGTH_SHORT).show();
						 }
					 }*/

			 }
			 
			 //Store the account list in to SharedPreferences
			 if(Accounts_list.size() >0)
			 {
				 for(int i=0;i<Accounts_list.size();i++)
				 {
					 settingsEditor.putString("acc"+i,Accounts_list.get(i));
				 }
				 settingsEditor.putInt("accounts_list_size",Accounts_list.size());
				 settingsEditor.commit();
			 }
			 
			 if(vibrate==1)
			 {
				//change profile mode to user preferred mode when there is an event
				 
				 //refer the comparing string ConfigActivity
				 if(modeToSet.equals("SILENT"))
				 {
					 //change mode if mode is not in 'SILENT' 
					// Toast.makeText(getApplicationContext(), "SILET CON", Toast.LENGTH_SHORT).show();
					 if(audio.getRingerMode() !=0)
					 {
						 audio.setRingerMode(audio.RINGER_MODE_SILENT);
						 mode_changed = "SILENT";
					 }
				 }
				 else if(modeToSet.equals("VIBRATE"))
				 {
					//change mode if mode is not in 'VIBRATE'
					// Toast.makeText(getApplicationContext(), "VIBRATE CON", Toast.LENGTH_SHORT).show();
					 if(audio.getRingerMode() !=1)
					 {
						 audio.setRingerMode(audio.RINGER_MODE_VIBRATE);
						 mode_changed = "VIBRATE";
					 }
				 }
				 else if(modeToSet.equals("NORMAL"))
				 {
					//change mode if mode is not in 'NORMAL'
					// Toast.makeText(getApplicationContext(), "NORMAL CON", Toast.LENGTH_SHORT).show();
					 if(audio.getRingerMode() !=2)
					 {
						 audio.setRingerMode(audio.RINGER_MODE_NORMAL);
						 mode_changed = "NORMAL";
					 }
				 }
				 else
				 {
					//change profile mode default case to vibrate if any error occurs
					 if(audio.getRingerMode() !=1)
					 {
						 audio.setRingerMode(audio.RINGER_MODE_VIBRATE);
						 mode_changed = "VIBRATE";
					 }
				 }
				 
				 
				 
				 
			 }
			 else
			 {
				 if(audio.getRingerMode() !=2)
				 {
					 //set to normal mode when there is no event
					 audio.setRingerMode(audio.RINGER_MODE_NORMAL);
					 //prev_mode_changed = "yes";
					 mode_changed = "NORMAL";
				 }
				 
			 }
			 //Log.d("stored mode", modeToSet);
			// Toast.makeText(getApplicationContext(), "stored mode "+modeToSet, Toast.LENGTH_SHORT).show();
			 
			 //notification
			 //if((!mode_changed.equals("no")) || (!prev_mode_changed.equals("no")))
			if(!mode_changed.equals("no"))
			 {
				// Toast.makeText(getApplicationContext(), "Changing mode", Toast.LENGTH_SHORT).show();
					
				 mBuilder.setContentText("Profile changed to "+mode_changed).setAutoCancel(true);

					// Creates an explicit intent for an Activity in your app
					Intent resultIntent = new Intent(this, MainActivity.class);

					// The stack builder object will contain an artificial back stack for the
					// started Activity.
					// This ensures that navigating backward from the Activity leads out of
					// your application to the Home screen.
					TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
					// Adds the back stack for the Intent (but not the Intent itself)
					stackBuilder.addParentStack(MainActivity.class);
					// Adds the Intent that starts the Activity to the top of the stack
					stackBuilder.addNextIntent(resultIntent);
					PendingIntent resultPendingIntent =
					        stackBuilder.getPendingIntent(
					            0,
					            PendingIntent.FLAG_UPDATE_CURRENT
					        );
					mBuilder.setContentIntent(resultPendingIntent);
					NotificationManager mNotificationManager =
						    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
						// mId allows you to update the notification later on.
						mNotificationManager.notify(mId, mBuilder.build());
				 
			 }
		 }
		 else
		 {
			// Toast.makeText(getApplicationContext(), "No Events", Toast.LENGTH_SHORT).show();
		 }

		//Toast.makeText(getApplicationContext(), "Service Runing", Toast.LENGTH_SHORT).show();
		//System.out.println("wegggggg");
		
		//System.out.println(getString(audio.getRingerMode()));
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

}