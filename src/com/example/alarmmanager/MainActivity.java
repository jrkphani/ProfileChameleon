package com.example.alarmmanager;

import java.util.Calendar;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

	public boolean serviceStatus;
@Override
public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    
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
    serviceStatus = true;
    // click listener for the button to start service
    Button btnStart = (Button) findViewById(R.id.startserviceBtn);
    btnStart.setOnClickListener(new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            startService(new Intent(getBaseContext(), Service_class.class));
            alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                    1000 * 10, pintent);
            serviceStatus = true;

        }
    });

    // click listener for the button to stop service
    Button btnStop = (Button) findViewById(R.id.stopserviceBtn);
    btnStop.setOnClickListener(new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            stopService(new Intent(getBaseContext(), Service_class.class));
            //stop service repeating
            alarm.cancel(pintent);
            serviceStatus = false;
        }
    });
    
 // click config for to go to configuration intent 
    Button btnConfig = (Button) findViewById(R.id.configBtn);
    btnConfig.setOnClickListener(new View.OnClickListener() {

        @Override
        public void onClick(View v) {
        	Intent Configintent = new Intent(getBaseContext(),ConfigActivity.class);
        	//Log.d("Click status", "config clicked");
        	//Toast.makeText(getBaseContext(),"SELECTED", Toast.LENGTH_SHORT).show();
        	startActivity(Configintent);
        }
    });
    
    if(serviceStatus)
    {
    	//Toast.makeText(getBaseContext(),"service runing", Toast.LENGTH_SHORT).show();
    }
    
}
}