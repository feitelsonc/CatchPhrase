package com.cs185.catchphrase;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class SplashActivity extends Activity implements OnItemSelectedListener {
	
	private static String TEAM_1_NAME_EXTRA = "team1nameextra";
	private static String TEAM_2_NAME_EXTRA = "team2nameextra";
	private static String CATEGORY_EXTRA = "categoryextra";
	private static String POINTS_EXTRA = "pointsextra";

	private Spinner categorySpinner;
	private Button startButton;
	
	private EditText team1NameEditText;
	private EditText team2NameEditText;
	private EditText pointsToWinEditText;
	
	private String team1Name;
	private String team2Name;
	private int selectedCategory = 0;
	private int pointsToWin = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ActionBar actionBar = getActionBar();
        actionBar.hide();
        setContentView(R.layout.activity_splash);
        
        initializeCategorySpinner();
        
        team1Name = getResources().getString(R.string.team1);
        team2Name = getResources().getString(R.string.team2);
        
        team1NameEditText = (EditText) findViewById(R.id.edit_team_one_name);
        team2NameEditText = (EditText) findViewById(R.id.edit_team_two_name);
        pointsToWinEditText = (EditText) findViewById(R.id.edit_score_to_win);
        
        startButton = (Button) findViewById(R.id.start_game);
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            	
            	// add extras
            	if (team1NameEditText.getText().toString().equals("")) {
            		intent.putExtra(TEAM_1_NAME_EXTRA, team1Name);
            	}
            	else {
            		intent.putExtra(TEAM_1_NAME_EXTRA, team1NameEditText.getText().toString());
            	}
            	
            	if (team2NameEditText.getText().toString().equals("")) {
            		intent.putExtra(TEAM_2_NAME_EXTRA, team2Name);
            	}
            	else {
            		intent.putExtra(TEAM_2_NAME_EXTRA, team2NameEditText.getText().toString());
            	}
            	
            	intent.putExtra(CATEGORY_EXTRA, selectedCategory);
            	
            	if (pointsToWinEditText.getText().toString().equals("")) {
            		intent.putExtra(POINTS_EXTRA, pointsToWin);
            	}
            	else {
            		intent.putExtra(POINTS_EXTRA, pointsToWinEditText.getText());
            	}
            	
            	SplashActivity.this.startActivity(intent);
//            	SplashActivity.this.finish();
            }
        });
    }
    
    private void initializeCategorySpinner() {
    	categorySpinner = (Spinner) findViewById(R.id.categories);
    	// Create an ArrayAdapter using the string array and a default spinner layout
    	ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.categories, R.layout.spinner_layout_splash);
    	// Specify the layout to use when the list of choices appears
    	adapter.setDropDownViewResource(R.layout.spinner_item_layout);
    	// Apply the adapter to the spinner
    	categorySpinner.setAdapter(adapter);
    	categorySpinner.setOnItemSelectedListener(this);
    }
    
    // initialize word list based on user selected category
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
    	selectedCategory = pos;
    }
    
    @Override
	public void onNothingSelected(AdapterView<?> parent) {
    	selectedCategory = 0;
	}

}
