package com.cs185.catchphrase;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

public class TimeupDialog extends DialogFragment{
	
	private Button team1button;
	private Button team2button;
	private Button neitherteambutton;
	private MainActivity activity;

	public interface TimeupDialogListener {
		public void team1Selected();
		public void team2Selected();
		public void neitherTeamSelected();
	}
	
	TimeupDialogListener mListener;
	
	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity); 
		
		this.activity = (MainActivity) activity;
			
		mListener = (TimeupDialogListener)activity;
	}
	
	public Dialog onCreateDialog(Bundle SavedInstanceState){
		
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.timeup_dialog_layout, null);
		
		team1button = (Button) view.findViewById(R.id.team1button);
		team2button = (Button) view.findViewById(R.id.team2button);
		neitherteambutton = (Button) view.findViewById(R.id.noscorebutton);
		
		team1button.setText(activity.getTeam1Name());
		team2button.setText(activity.getTeam2Name());
		
		team1button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mListener.team1Selected();
				TimeupDialog.this.dismiss();
				
			}
		});
		
		team2button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mListener.team2Selected(); 
				TimeupDialog.this.dismiss();
			}
		});
		
		neitherteambutton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mListener.neitherTeamSelected();
				TimeupDialog.this.dismiss();
				
			}
		});
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setView(view);
		
		return builder.create();
	}
		
}
	


