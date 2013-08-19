package com.example.alarmmanager;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class ConfigActivity extends Activity {
	//public String profileMode;
	//public final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this); 
	public HashMap<String,String> checkedAccounts = new HashMap<String,String>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_config);
		
		ArrayList<String> Accounts_list=new ArrayList<String>();
		String to_push="";
		String acc_name="";
		int i=0;
		int j=0;
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this); 
		String modeToSet = settings.getString("configMode", "VIBRATE");
		final CheckBox all_acount = (CheckBox)findViewById(R.id.all_acount);
		final LinearLayout parentLinear = (LinearLayout)findViewById(R.id.parent_linear);
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
		
		/*all_acount.setOnClickListener(new OnClickListener() {
			
			@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (((CheckBox) v).isChecked())
					{
						parentLinear.setVisibility(0);
					}
					else
					{
						parentLinear.setVisibility(1);
					}				
				}
			});*/
		
		//create checkbox for account list
		CheckBox rb ;
		int account_selected_all = settings.getInt("accounts_selected_all", 0);
		
		//get array size of stored account list
		Uri calendaruri = Uri.parse("content://com.android.calendar/events");
		Cursor mCursor = getContentResolver().query(calendaruri, null, null, null, null);
		if(mCursor.getCount()>0)
		 {
			mCursor.moveToFirst();
			while(mCursor.moveToNext())
			 {
				if(!to_push.equals(mCursor.getString(mCursor.getColumnIndex("account_name"))))
				 {
					 to_push =mCursor.getString(mCursor.getColumnIndex("account_name"));
					//check if account exist in array
					 if(!Accounts_list.contains(to_push))
					 {
						 Accounts_list.add(to_push);
					 }
				 }
			 }
			if(Accounts_list.size() >0)
			 {
				all_acount.setVisibility(1);
				int accounts_selected_size = settings.getInt("accounts_selected_size", 0);
				 for(i=0;i<Accounts_list.size();i++)
				 {
					 //settingsEditor.putString("acc"+i,Accounts_list.get(i));
					 rb = new CheckBox(this);
				       rb.setText(Accounts_list.get(i));
				       rb.setId(i);
				       if(account_selected_all<=0)
				       {
				    	   if(accounts_selected_size>0)
							{
					    	   for(j=0; j<accounts_selected_size; j++)
								{
					    		   acc_name = settings.getString("acc_selected"+j, null);
					    		   Log.d("selected acc",acc_name);
					    		   Log.d("selected aaacc",Accounts_list.get(i));
					    		   if(Accounts_list.get(i).equals(acc_name))
					    		   {
					    			   checkedAccounts.put(acc_name,acc_name);
					    			   rb.setChecked(true);
								      // break;
					    		   }
								}
							}
				       }
				       else
				       {
				    	   all_acount.setChecked(true);
				    	   rb.setChecked(true);
				       }
				       
				       rb.setOnClickListener(new OnClickListener() {
						
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								String acc_txt = ((CheckBox) v).getText().toString();
								all_acount.setChecked(false);
								if (((CheckBox) v).isChecked())
								{
									checkedAccounts.put(acc_txt,acc_txt);
									//Toast.makeText(getBaseContext(),"added",Toast.LENGTH_SHORT).show();
								}
								else
								{
									checkedAccounts.remove(acc_txt);
									//Toast.makeText(getBaseContext(),"removed",Toast.LENGTH_SHORT).show();
								}
							}
				       });
				       parentLinear.addView(rb);
				 }
			 }
			
		 }
		else
		{
			TextView Account_list_title = (TextView)findViewById(R.id.Account_list_title);
			Account_list_title.setText("No accounts with events");
		}
				/*
				//texting to get save selected account list
				//create checkbox for account list
				CheckBox rbtest ;
				
				LinearLayout test = (LinearLayout)findViewById(R.id.parent_linear);
				//get array size of stored account list
						int accounts_selected_size = settings.getInt("accounts_selected_size", 0);
						if(accounts_selected_size>0)
						{
							for(int i=0; i<accounts_selected_size; i++)
							{
								TextView tv = new TextView(this);
								tv.setText(settings.getString("acc_selected"+i, null));
								parentLinear.addView(tv);
								
							}
						}
				*/
	}
	public void saveClicked(View view)
	{
		RadioGroup g = (RadioGroup) findViewById(R.id.radiogroup_mode);
		int selected = g.getCheckedRadioButtonId();
		int i=0;
		RadioButton b = (RadioButton) findViewById(selected);
		String selectedMode = (String) b.getText();
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this); 
		SharedPreferences.Editor settingsEditor = settings.edit();
		settingsEditor.putString("configMode", selectedMode);
		settingsEditor.remove("%acc_selected");
		settingsEditor.remove("accounts_selected_size");
		settingsEditor.remove("accounts_selected_all");
		//Store the account list in to SharedPreferences
		CheckBox all_acount = (CheckBox)findViewById(R.id.all_acount);
		if(all_acount.isChecked())
		{
			settingsEditor.putInt("accounts_selected_all",1);
			//Log.d("all account","all account");
		}
		else
		{
			 if(checkedAccounts.size() >0)
			 {
				 Object[] checkedAccountslist = checkedAccounts.keySet().toArray();
				 for(i=0;i<checkedAccounts.size();i++)
				 {
						settingsEditor.putString("acc_selected"+i,checkedAccountslist[i].toString());
						Log.d("------------",checkedAccountslist[i].toString()); 
				 }
				 //to remove if any account is stored previous
				 settingsEditor.putInt("accounts_selected_size",checkedAccounts.size());
			 }
		}
		settingsEditor.commit();
		//Toast.makeText(getBaseContext(),selectedMode + " Saved",Toast.LENGTH_SHORT).show();
		Toast.makeText(getBaseContext(),"Saved",Toast.LENGTH_SHORT).show();
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