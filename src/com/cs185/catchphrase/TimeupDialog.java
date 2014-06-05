package com.cs185.catchphrase;



import com.cs185.catchphrase.PausedDialog.PauseDialogListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class TimeupDialog extends DialogFragment{
	
private String[] choices = new String[3];
	
	public interface TimeupDialogListener {
		public void timeup_getChoice(int which);
	}
	
	TimeupDialogListener mListener;
	
	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity); 
			
		mListener = (TimeupDialogListener)activity;
	}
	
	public Dialog onCreateDialog(Bundle SavedInstanceState){
		
		choices[0] = getResources().getString(R.string.team1);
		choices[1] = getResources().getString(R.string.team2);
		choices[2] = getResources().getString(R.string.no_score);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.times_up)
		.setItems(choices, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				mListener.timeup_getChoice(which);
			}
		});

		return builder.create();
	}
		
	}
	


