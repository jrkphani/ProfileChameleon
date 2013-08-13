package com.example.alarmmanager;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class ConfigActivity extends Activity {
	//public String profileMode;
	//public final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this); 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_config);
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this); 
		String modeToSet = settings.getString("configMode", "VIBRATE");
		/*Log.d("cuurent mode","=======================" );
		Log.d("cuurent mode",modeToSet );*/
		RadioButton radioToCkeck;
		switch(Modes.valueOf(modeToSet))
		{
        case NORMAL:
        	radioToCkeck = (RadioButton) findViewById(R.id.radio_normal);
        	radioToCkeck.setChecked(true);
            break;
        case VIBRATE:
        	radioToCkeck = (RadioButton) findViewById(R.id.radio_vibrate);
        	radioToCkeck.setChecked(true);
        	break;
        case SILENT:
        	radioToCkeck = (RadioButton) findViewById(R.id.radio_silent);
        	radioToCkeck.setChecked(true);
        	break;
        default:
        	break;
		}
		//create radio button for account list
		
		/*RadioGroup account_rgroup=(RadioGroup)findViewById(R.id.radiogroup_account_list);
		RadioButton rb;
		//get array size of stored account list
		int account_list_size = settings.getInt("accounts_list_size", 0);
		if(account_list_size>0)
		{
			String acc_name="";
			for(int i=0; i<account_list_size; i++)
			{
				//get account name
				acc_name = settings.getString("acc"+i, null);
				if(acc_name != null)
				{
					rb=new RadioButton(this);
				       rb.setText(acc_name);
				       rb.setId(i);
				       account_rgroup.addView(rb);
				}
				           
			}
		}*/
		
		
		
		//create checkbox for account list
		CheckBox rb ;
		LinearLayout parentLinear = (LinearLayout)findViewById(R.id.parent_linear);
		//get array size of stored account list
				int account_list_size = settings.getInt("accounts_list_size", 0);
				if(account_list_size>0)
				{
					String acc_name="";
					for(int i=0; i<account_list_size; i++)
					{
						//get account name
						acc_name = settings.getString("acc"+i, null);
						if(acc_name != null)
						{
							rb = new CheckBox(this);
						       rb.setText(acc_name);
						       rb.setId(i);
						       parentLinear.addView(rb);
						       //this.setContentView(rb);
						}
						           
					}
				}
	}
	public void saveClicked(View view)
	{
		RadioGroup g = (RadioGroup) findViewById(R.id.radiogroup_mode);
		int selected = g.getCheckedRadioButtonId();
		RadioButton b = (RadioButton) findViewById(selected);
		String selectedMode = (String) b.getText();
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this); 
		SharedPreferences.Editor settingsEditor = settings.edit();
		settingsEditor.putString("configMode", selectedMode);
		settingsEditor.commit();
		Toast.makeText(getBaseContext(),selectedMode + " Saved",Toast.LENGTH_SHORT).show();
	}
	public enum Modes
	 {
		VIBRATE, NORMAL, SILENT; 
	 }
	public void onRadioButtonClicked(View view) {
		//Toast.makeText(getBaseContext()," Click.",Toast.LENGTH_SHORT).show();
		/*
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this); 
		String modeToSet = settings.getString("configMode", "VIBRATE");
		Log.d("cuurent mode","=======================" );
		Log.d("cuurent mode",modeToSet );
		RadioButton button = (RadioButton) view;
		profileMode= button.getText().toString();
		
		Log.d("cuurent mode","++++++++++++++++++++" );
		 RadioButton button = (RadioButton) view;
		 profileMode= button.getText().toString();
		 
		 Log.d("cuurent mode",profileMode );
		 */
	    
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}