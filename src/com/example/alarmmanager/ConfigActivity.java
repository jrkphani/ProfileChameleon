package com.example.alarmmanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
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
		
		final ArrayList<String> Accounts_list=new ArrayList<String>();
		String to_push="";
		String acc_name="";
		int i=0;
		int j=0;
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this); 
		String modeToSet = settings.getString("configMode", "VIBRATE");
		final CheckBox all_acount = (CheckBox)findViewById(R.id.all_acount);
		final LinearLayout parentLinear = (LinearLayout)findViewById(R.id.account_list);
		/*Log.d("cuurent mode","=======================" );
		Log.d("cuurent mode",modeToSet );*/
		 RadioButton vibrateRadio = (RadioButton) findViewById(R.id.radio_vibrate);
		 RadioButton normalRadio = (RadioButton) findViewById(R.id.radio_normal);
		 RadioButton silentRadio = (RadioButton) findViewById(R.id.radio_silent);
		switch(Modes.valueOf(modeToSet))
		{
        case NORMAL:
        	normalRadio.setChecked(true);
            break;
        case VIBRATE:
        	vibrateRadio.setChecked(true);
        	break;
        case SILENT:
        	silentRadio.setChecked(true);
        	break;
        default:
        	break;
		}
		
		Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
		Account[] accounts = AccountManager.get(getBaseContext()).getAccounts();
		for (Account account : accounts) {
		    if (emailPattern.matcher(account.name).matches()) {
		    	if(!Accounts_list.contains(account.name))
				 {
					 Accounts_list.add(account.name);
				 }
		    }
		}
		
		
		all_acount.setOnClickListener(new OnClickListener() {
			
			@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
				
				if (((CheckBox) v).isChecked())
				{
					//parentLinear.setVisibility(View.INVISIBLE);
					for (int i = 0; i < parentLinear.getChildCount(); i++){
						   View view = parentLinear.getChildAt(i);
						   ((CheckBox) view).setChecked(true);
						}
				}
				else
				{
					//parentLinear.setVisibility(View.VISIBLE);
					for (int i = 0; i < parentLinear.getChildCount(); i++){
						   View view = parentLinear.getChildAt(i);
						   ((CheckBox) view).setChecked(false);
						}
				}
								
				}
			});

		
		
		

		//create checkbox for account list
		CheckBox rb ;
		int account_selected_all = settings.getInt("accounts_selected_all", 0);
		int account_selected_size = settings.getInt("accounts_selected_size", 0);

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
					    		  // Log.d("selected acc",acc_name);
					    		   //Log.d("selected aaacc",Accounts_list.get(i));
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
		else
		{
			TextView Account_list_title = (TextView)findViewById(R.id.Account_list_title);
			Account_list_title.setText("No accounts");
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
			
			 //Alert user  on first install
			account_selected_size = settings.getInt("accounts_selected_size", -1);
			account_selected_all = settings.getInt("accounts_selected_all", -1);
				if(account_selected_size ==-1 && account_selected_all ==-1)
				{
					//Toast.makeText(getBaseContext(), "Fisrt", Toast.LENGTH_LONG).show();
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
			 
						// set title
						alertDialogBuilder.setTitle("Guide");
			 
						// set dialog message
						alertDialogBuilder
							.setMessage("Choose accounts and profile mode !")
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
			
	}
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);
	    View mainLayout = findViewById(R.id.config_page); // getting the layout
	    int orientation = getResources().getConfiguration().orientation;
	    // Checks the orientation of the screen
	    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {    
	        mainLayout.setBackgroundResource(R.drawable.bg1_change);
	    } else {
	        mainLayout.setBackgroundResource(R.drawable.bg1);
	    }
	  }
	public void saveClicked(View view)
	{
		RadioButton vibrateRadio = (RadioButton) findViewById(R.id.radio_vibrate);
		RadioButton normalRadio = (RadioButton) findViewById(R.id.radio_normal);
		RadioButton silentRadio = (RadioButton) findViewById(R.id.radio_silent);
		String selectedMode;
		if(normalRadio.isChecked())
		{
			selectedMode = "NORMAL";
		}
		else if(vibrateRadio.isChecked())
		{
			selectedMode = "VIBRATE";
		}
		else if(silentRadio.isChecked())
		{
			selectedMode = "SILENT";
		}
		else
		{
			selectedMode = "VIBRATE";
		}

		int i=0;		
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
						//Log.d("------------",checkedAccountslist[i].toString()); 
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
	
	public void onRadioButtonClicked(View v) {
		RadioButton vibrateRadio = (RadioButton) findViewById(R.id.radio_vibrate);
		 RadioButton normalRadio = (RadioButton) findViewById(R.id.radio_normal);
		 RadioButton silentRadio = (RadioButton) findViewById(R.id.radio_silent);
		switch (v.getId()) {
        case R.id.radio_vibrate:
        	silentRadio.setChecked(false);
        	normalRadio.setChecked(false);
        	//vibrateRadio.setChecked(false);
            break;
        case R.id.radio_normal:
        	silentRadio.setChecked(false);
        	//normalRadio.setChecked(false);
        	vibrateRadio.setChecked(false);
            break;
        case R.id.radio_silent:
        	//silentRadio.setChecked(false);
        	normalRadio.setChecked(false);
        	vibrateRadio.setChecked(false);
            break;
    }
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
	public void onBackButtonClicked(View v)
	{
		onBackPressed();
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
		 return true;
		}

}