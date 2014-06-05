package com.cs185.catchphrase;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class PausedDialog extends DialogFragment {
	
	private String[] choices = new String[3];
	
	public interface PauseDialogListener {
		public void getChoice(int which);
	}
	
	PauseDialogListener mListener;
	
	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity); //I was missing this in the live demo,
			// and this is what caused it to crash
		mListener = (PauseDialogListener)activity;
	}
	
	public Dialog onCreateDialog(Bundle SavedInstanceState){
		choices[0] = getResources().getString(R.string.new_game);
		choices[1] = getResources().getString(R.string.reset_score);
		choices[2] = getResources().getString(R.string.resume);
		
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
