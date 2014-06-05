package com.cs185.catchphrase;

import android.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class PausedDialog extends DialogFragment{
	
	public interface DialogListener {
		public void changeText(DialogFragment dialog);
	}
	
	DialogListener mListener;
	
	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity); //I was missing this in the live demo,
			// and this is what caused it to crash
		mListener = (DialogListener)activity;
	}
	
	public Dialog onCreateDialog(Bundle SavedInstanceState){
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
//		final View v = inflater.inflate(R.layout.pause_layout, null);
//		builder.setView(v);
//		builder.setTitle();
//		
//		//New Game button
//		 Button new_game_button = (Button)v.findViewById(R.id.new_game_button);
//	        new_game_button.setOnClickListener(new OnClickListener() {
//	            public void onClick(View v) {
//	                
//	            	
//	            }
//	        });
//	        
//	        
//	    //Reset score button
//	     Button reset_score_button = (Button)v.findViewById(R.id.new_game_button);
//	        reset_score_button.setOnClickListener(new OnClickListener() {
//	            public void onClick(View v) {
//	                
//	            	
//	            }
//	        });
//	        
//	    //Edit score button
//	     Button edit_score_button = (Button)v.findViewById(R.id.new_game_button);
//	        edit_score_button.setOnClickListener(new OnClickListener() {
//	            public void onClick(View v) {
//	                
//	            	
//	            }
//	        });
//	        
//	   //Resume button
//	   Button resume_button = (Button)v.findViewById(R.id.new_game_button);
//	        resume_button.setOnClickListener(new OnClickListener() {
//	            public void onClick(View v) {
//	                
//	            	
//	            }
//	        });
		
		return builder.create();
		
	}

	

}
