package com.cs185.catchphrase;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;

public class TimeupDialog extends DialogFragment{
	
private String[] choices = new String[3];
	
	public interface TimeupDialogListener {
		public void team1Selected();
		public void team2Selected();
		public void neitherTeamSelected();
	}
	
	TimeupDialogListener mListener;
	MainActivity activity;
	
	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity); 
		this.activity = (MainActivity) activity;
			
		mListener = (TimeupDialogListener)activity;
	}
	
	public Dialog onCreateDialog(Bundle SavedInstanceState){
		
//		choices[0] = activity.getTeam1Name();
//		choices[1] = activity.getTeam2Name();
//		choices[2] = getResources().getString(R.string.no_score);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.times_up);
		
		LayoutInflater inflater = getActivity().getLayoutInflater();
		final View v = inflater.inflate(R.layout.timeupfrag, null);
		builder.setView(v);
		
		
			

		return builder.create();
	}
		
	}
	


