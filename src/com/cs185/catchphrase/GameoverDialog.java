package com.cs185.catchphrase;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

public class GameoverDialog extends DialogFragment {
	
	private Button newGameButton;
	private Button exitButton;	
	private OutlineTextView title;
	private MainActivity activity;
	
	public interface GameOverDialogListener {
		public void newGameClicked();
		public void exitClicked();
	}
	
	GameOverDialogListener mListener;
	
	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity); 
		
		this.activity = (MainActivity) activity;
			
		mListener = (GameOverDialogListener) activity;
	}
	
	public Dialog onCreateDialog(Bundle SavedInstanceState) {
		
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.game_over_dialog_layout, null);
		newGameButton = (Button) view.findViewById(R.id.new_game);
		exitButton = (Button) view.findViewById(R.id.exit);
		title = (OutlineTextView) view.findViewById(R.id.title);
		title.setText(activity.getWinningTeamString());
		
		newGameButton.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
		    	mListener.newGameClicked();
		    	GameoverDialog.this.dismiss();
		    }
		});
		exitButton.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
		    	mListener.exitClicked();
		    	GameoverDialog.this.dismiss();
		    }
		});
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setView(view);

		return builder.create();
	}
	
}