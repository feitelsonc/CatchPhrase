package com.cs185.catchphrase;



import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class TimeupDialog extends DialogFragment{
	
private String[] choices = new String[3];
	
	public interface PauseDialogListener {
		public void getChoice(int which);
	}
	
	PauseDialogListener mListener;
	
	public Dialog onCreateDialog(Bundle SavedInstanceState){
		
		choices[0] = getResources().getString(R.string.team1);
		choices[1] = getResources().getString(R.string.team2);
		choices[2] = getResources().getString(R.string.no_score);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.paused)
		.setItems(choices, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				mListener.getChoice(which);
			}
		});

		return builder.create();
	}
		
	}
	


