package in.digitalchakra.profilechameleon;

import java.util.Calendar;

import in.digitalchakra.profilechameleon.R;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
    ////
    
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
    btnStartStop.setOnClickListener(new View.OnClickListener() {

        @Override
        public void onClick(View v) {
        	
        	////
        	
        	int serviceStatus = settings.getInt("serviceStatus", 0);
            if(serviceStatus == 0)
            {
            	btnStartStop.setBackgroundResource(R.drawable.start);
            	startService(new Intent(getBaseContext(), Service_class.class));
            	/*every 2 min*/
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
                alarm.cancel(pintent);
                settingsEditor.putInt("serviceStatus", 0);
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
				.setMessage("Click on the Chameleon Icon to turn the services on!")
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
}