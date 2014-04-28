package in.digitalchakra.profilechameleon;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class ItudeMobileBroadcastReceiver extends BroadcastReceiver
{
	private Context mContext;
	@Override
	public void onReceive(Context context, Intent intent)
	{
		if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()))
		{
			mContext = context;
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext());
			int serviceStatus = settings.getInt("serviceStatus", 0);
			if(serviceStatus == 1)
			{
				Calendar cal = Calendar.getInstance();
			    cal.add(Calendar.SECOND, 10);
				Intent newintent = new Intent(mContext.getApplicationContext(), Service_class.class);
			    final PendingIntent pintent = PendingIntent.getService(mContext.getApplicationContext(), 0, newintent,
			            0);
				AlarmManager alarm = (AlarmManager) mContext.getSystemService(mContext.ALARM_SERVICE);
				alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
	            		1*120*1000, pintent);
				Log.d("BroadcastReceiver","BroadcastReceiver ....run .......");
			}
		}
	}

}
