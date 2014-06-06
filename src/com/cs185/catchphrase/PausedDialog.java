package com.cs185.catchphrase;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

public class PausedDialog extends DialogFragment {
	
	private Button newGameButton;
	private Button resetScoreButton;
	private Button resumeButton;
	
	
	public interface PauseDialogListener {
		public void newGameClicked();
		public void resetScoreClicked();
		public void resumeClicked();
	}
	
	
	PauseDialogListener mListener;
	
	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity); 
			
		mListener = (PauseDialogListener)activity;
	}
	
	public Dialog onCreateDialog(Bundle SavedInstanceState) {
		
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.paused_dialog_layout, null);
		newGameButton = (Button) view.findViewById(R.id.new_game);
		resetScoreButton = (Button) view.findViewById(R.id.reset_score);
		resumeButton = (Button) view.findViewById(R.id.resume);
		
		newGameButton.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
		    	mListener.newGameClicked();
		    	PausedDialog.this.dismiss();
		    }
		});
		resetScoreButton.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
		    	mListener.resetScoreClicked();
		    	PausedDialog.this.dismiss();
		    }
		});
		resumeButton.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
		    	mListener.resumeClicked();
		    	PausedDialog.this.dismiss();
		    }
		});
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setView(view);

		return builder.create();
	}
	
}
