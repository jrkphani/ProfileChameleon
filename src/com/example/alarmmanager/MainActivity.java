package com.example.alarmmanager;

import java.util.Calendar;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

	public boolean serviceStatus;
@Override
public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
	final SharedPreferences.Editor settingsEditor = settings.edit();
    // Start service using AlarmManager

    final Calendar cal = Calendar.getInstance();
    cal.add(Calendar.SECOND, 10);
    Intent intent = new Intent(MainActivity.this, Service_class.class);
    final PendingIntent pintent = PendingIntent.getService(MainActivity.this, 0, intent,
            0);
    final AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
    /*every 1 min*/
    alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
    		1*60*1000, pintent);
    /*every 5 secs
    alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
            1000 * 10, pintent);*/
  //setting value 0 , to specify the service status is not running 
    // click listener for the button to start service
    final Button btnStart = (Button) findViewById(R.id.startserviceBtn);
    final Button btnStop = (Button) findViewById(R.id.stopserviceBtn);
    btnStart.setOnClickListener(new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            startService(new Intent(getBaseContext(), Service_class.class));
            alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                    1000 * 10, pintent);
            settingsEditor.putInt("serviceStatus", 1);
            settingsEditor.commit();
            btnStart.setVisibility(View.INVISIBLE);
        	btnStop.setVisibility(View.VISIBLE);

        }
    });

    // click listener for the button to stop service
  
    btnStop.setOnClickListener(new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            stopService(new Intent(getBaseContext(), Service_class.class));
            //stop service repeating
            alarm.cancel(pintent);
            settingsEditor.putInt("serviceStatus", 0);
            settingsEditor.commit();
            btnStart.setVisibility(View.VISIBLE);
        	btnStop.setVisibility(View.INVISIBLE);
        }
    });
    
 // click config for to go to configuration intent 
    final Button btnConfig = (Button) findViewById(R.id.configBtn);
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
    	btnStart.setVisibility(View.VISIBLE);
    	btnStop.setVisibility(View.INVISIBLE);
    	Toast.makeText(getBaseContext(),"service not runing"+serviceStatus, Toast.LENGTH_SHORT).show();
    }
    else
    {
    	btnStart.setVisibility(View.INVISIBLE);
    	btnStop.setVisibility(View.VISIBLE);
    	Toast.makeText(getBaseContext(),"service runing"+serviceStatus, Toast.LENGTH_SHORT).show();
    }
    
}
@Override
public boolean onCreateOptionsMenu(Menu menu)
	{
	 menu.add(1, 1, 0, "Item1").setIcon(R.drawable.notification_icon).setOnMenuItemClickListener(new OnMenuItemClickListener() {
		
		@Override
		public boolean onMenuItemClick(MenuItem item) {
			// TODO Auto-generated method stub
			Toast.makeText(getApplicationContext(), "optin meni Item1 clicked", Toast.LENGTH_LONG).show();
			return false;
		}
	});
     menu.add(1, 2, 1, "Item2").setIcon(R.drawable.notification_icon);
     menu.add(1, 3, 2, "Item3").setIcon(R.drawable.notification_icon);
     menu.add(1, 4, 3, "Item4").setIcon(R.drawable.notification_icon);
     menu.add(1, 5, 4, "Item5").setIcon(R.drawable.notification_icon);
	 return true;
	}
}