package com.cs185.catchphrase;

import java.util.ArrayList;
import java.util.Arrays;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.cs185.catchphrase.Beeper.LocalBinder;

public class MainActivity extends Activity {
	
	private Uri beeperTrackUri = null;
	private Beeper beeper;
	private TextView start;
	private int team1Score = 0;
	private int team2Score = 0;
	private TextView team1ScoreTextView;
	private TextView team2ScoreTextView;
	private Spinner categorySpinner;
	private int selectedCategory = 0;
	private Button pauseButton;
	private Button incrementTeam1Score;
	private Button decrementTeam1Score;
	private Button incrementTeam2Score;
	private Button decrementTeam2Score;
	private ArrayList<String> words;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        hideSystemBars();
        UiChangeListener();
        ActionBar actionBar = getActionBar();
        actionBar.hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        
        initializeCategorySpinner();
        
        // initialize widgets and set onclick listeners
        pauseButton = (Button) findViewById(R.id.pause_button);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	if (beeper != null) {
                		stopBeeper();
            	}
            }
        });
        
        incrementTeam1Score = (Button) findViewById(R.id.add_button_team_one);
        decrementTeam1Score = (Button) findViewById(R.id.subtract_button_team_one);
        incrementTeam2Score = (Button) findViewById(R.id.add_button_team_two);
        decrementTeam2Score = (Button) findViewById(R.id.subtract_button_team_two);
        
        incrementTeam1Score.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	decrementTeam1Score.setEnabled(true);
            	incrementTeam1Score();
            }
        });
        
        decrementTeam1Score.setEnabled(false);
        decrementTeam1Score.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	decrementTeam1Score();
            }
        });
        
        incrementTeam2Score.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	decrementTeam2Score.setEnabled(true);
            	incrementTeam2Score();
            }
        });
        
        decrementTeam2Score.setEnabled(false);
        decrementTeam2Score.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	decrementTeam2Score();
            }
        });
        team1ScoreTextView = (TextView) findViewById(R.id.team1_score);
        team2ScoreTextView = (TextView) findViewById(R.id.team2_score);
        start = (TextView) findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	if (beeper != null) {
            		if (beeper.isPlaying()) {
                		stopBeeper();
                	}
                	else {
                		startBeeper();
                	}
            	}
            }
        });
        
        if (!Beeper.isServiceStarted()) {
	  		Intent intent = new Intent(this, Beeper.class);
	  		startService(intent);
		}
        bindToMusicPlayerService();
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	
        hideSystemBars();
        
        bindToMusicPlayerService();
    }
    
    @Override
	public void onDestroy(){
		super.onDestroy();

		if (Beeper.isServiceStarted()) {
			beeper.releasePlayer();
			stopService(new Intent(this, Beeper.class));
		}
		unbindToMusicPlayerService();
    }

    // not currently used because we aren't using the actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @SuppressLint("InlinedApi")
	public void hideSystemBars() {
    	
    	// prevent screen from dimming
    	getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // Navigation bar hiding:  Backwards compatible to ICS.
        if (Build.VERSION.SDK_INT >= 14) {
        	getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }

        // Status bar hiding: Backwards compatible to Jellybean
        if (Build.VERSION.SDK_INT >= 16) {
        	getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN);
        }

        // Immersive mode: Backward compatible to KitKat.
        // Note that there are two immersive mode UI flags, one of which is referred to as "sticky".
        // Sticky immersive mode differs in that it makes the navigation and status bars
        // semi-transparent, and the UI flag does not get cleared when the user interacts with
        // the screen.
        if (Build.VERSION.SDK_INT >= 18) {
        	getWindow().getDecorView().setSystemUiVisibility(
        			View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        			| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                    | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        }
    }
    
    // re-enables immersive mode after volume change or menu selection
    public void UiChangeListener() {
        final View decorView = getWindow().getDecorView();
            decorView.setOnSystemUiVisibilityChangeListener (new View.OnSystemUiVisibilityChangeListener() {
			@Override
            public void onSystemUiVisibilityChange(int visibility) {
				hideSystemBars();
            }
        });
    }
    
    // commented out teams editing overflow button code
//    public void editTeamOne(View v) {
//        PopupMenu popup = new PopupMenu(this, v);
//        popup.setOnMenuItemClickListener(this);
//        popup.inflate(R.menu.edit_team_one);
//        popup.show();
//    }
//    
//    public void editTeamTwo(View v) {
//        PopupMenu popup = new PopupMenu(this, v);
//        popup.setOnMenuItemClickListener(this);
//        popup.inflate(R.menu.edit_team_two);
//        popup.show();
//    }
//    
//    @Override
//    public boolean onMenuItemClick(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.renameTeamOne:
//                return true;
//            case R.id.renameTeamTwo:
//                return true;
//            default:
//                return false;
//        }
//    }
    
    private void incrementTeam1Score() {
    	++team1Score;
    	team1ScoreTextView.setText(Integer.valueOf(team1Score).toString());
    }
    
    private void incrementTeam2Score() {
    	++team2Score;
    	team2ScoreTextView.setText(Integer.valueOf(team2Score).toString());
    }
    
    private void decrementTeam1Score() {
    	if (team1Score > 0) {
    		--team1Score;
        	team1ScoreTextView.setText(Integer.valueOf(team1Score).toString());
    	}
    }
    
    private void decrementTeam2Score() {
    	if (team2Score > 0) {
	    	--team2Score;
	    	team2ScoreTextView.setText(Integer.valueOf(team2Score).toString());
    	}
    }
    
    private void initializeCategorySpinner() {
    	categorySpinner = (Spinner) findViewById(R.id.categories);
    	// Create an ArrayAdapter using the string array and a default spinner layout
    	ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.categories, R.layout.spinner_layout);
    	// Specify the layout to use when the list of choices appears
    	adapter.setDropDownViewResource(R.layout.spinner_item_layout);
    	// Apply the adapter to the spinner
    	categorySpinner.setAdapter(adapter);
    }
    
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
    	String[] wordsArray;
    	switch(pos) {
    	case 0:
    		wordsArray = getResources().getStringArray(R.array.all);
    		break;
    	case 1:
    		wordsArray = getResources().getStringArray(R.array.easy);
    		break;
    	case 2:
    		wordsArray = getResources().getStringArray(R.array.medium);
    		break;
    	case 3:
    		wordsArray = getResources().getStringArray(R.array.actions);
    		break;
    	case 4:
    		wordsArray = getResources().getStringArray(R.array.animals);
    		break;
    	case 5:
    		wordsArray = getResources().getStringArray(R.array.food);
    		break;
    	case 6:
    		wordsArray = getResources().getStringArray(R.array.holiday);
    		break;
    	case 7:
    		wordsArray = getResources().getStringArray(R.array.household_items);
    		break;
    	case 8:
    		wordsArray = getResources().getStringArray(R.array.idioms);
    		break;
    	case 9:
    		wordsArray = getResources().getStringArray(R.array.movies);
    		break;
    	case 10:
    		wordsArray = getResources().getStringArray(R.array.people);
    		break;
    	case 11:
    		wordsArray = getResources().getStringArray(R.array.travel);
    		break;
    	default:
    		wordsArray = getResources().getStringArray(R.array.all);
    		break;
    	}
    	
    	words = new ArrayList<String>( Arrays.asList(wordsArray));
    	selectedCategory = pos;
    }
    
    private void bindToMusicPlayerService() {
		Intent intent = new Intent(this, Beeper.class);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}
    
    private void unbindToMusicPlayerService() {
		if (beeper != null) {
			beeper = null;
		}
		unbindService(mConnection);
	}
    
    private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			// We've bound to LocalService, cast the IBinder and get LocalService instance
			LocalBinder binder = (LocalBinder) service;
			beeper = binder.getService();
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			beeper = null;
		}
	};
    
	// call if user wants to prematurely stop beeper
    private void stopBeeper() {
    	beeper.pause();
    }
    
    // call when user begins round
    private void startBeeper() {
    	beeperTrackUri = Uri.parse("android.resource://com.cs185.catchphrase/" + R.raw.beeping);
    	beeper.initializeBeeper(beeperTrackUri);
    }

}
