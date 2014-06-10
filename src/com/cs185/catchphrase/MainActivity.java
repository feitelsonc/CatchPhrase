package com.cs185.catchphrase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.cs185.catchphrase.Beeper.LocalBinder;
import com.cs185.catchphrase.GameoverDialog.GameOverDialogListener;
import com.cs185.catchphrase.PausedDialog.PauseDialogListener;
import com.cs185.catchphrase.TimeupDialog.TimeupDialogListener;

public class MainActivity extends FragmentActivity implements OnItemSelectedListener, PauseDialogListener, GameOverDialogListener, TimeupDialogListener{
	
	private static String TEAM_1_NAME_EXTRA = "team1nameextra";
	private static String TEAM_2_NAME_EXTRA = "team2nameextra";
	private static String CATEGORY_EXTRA = "categoryextra";
	private static String POINTS_EXTRA = "pointsextra";
	
	private Uri beeperTrackUri = null;
	private Beeper beeper;
	private TouchView2 start;
	private TextView team1NameTextView;
	private TextView team2NameTextView;
	private String team1Name;
	private String team2Name;
	private int team1Score = 0;
	private int team2Score = 0;
	private TextView team1ScoreTextView;
	private TextView team2ScoreTextView;
	private int scoreToWin;
	private Spinner categorySpinner;
	private int selectedCategory = 0;
	private TextView pauseButton;
	private View mainView;
	private TextView incrementTeam1Score;
	private TextView decrementTeam1Score;
	private TextView incrementTeam2Score;
	private TextView decrementTeam2Score;
	private String[] wordsArray;
	private ArrayList<String> words;
	private TimeChecker timeChecker = null;
	private Handler handler = new Handler();

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
        
        wordsArray = getResources().getStringArray(R.array.all);
        words = new ArrayList<String>(Arrays.asList(wordsArray));
        
        // initialize widgets and set onclick listeners
        team1NameTextView = (TextView) findViewById(R.id.team1);
        team2NameTextView = (TextView) findViewById(R.id.team2);
        mainView = (View) findViewById(R.id.main_layout);
        pauseButton = (TextView) findViewById(R.id.pause_button);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	if (beeper != null) {
                		stopBeeper();
            	}
            	pauseButton.setVisibility(View.GONE);
            	
            	// popup paused game dialog
            	DialogFragment pauseDialog = new PausedDialog();
            	pauseDialog.show(getSupportFragmentManager(), "paused");
            	
            }
        });
        
        incrementTeam1Score = (TextView) findViewById(R.id.add_button_team_one);
        decrementTeam1Score = (TextView) findViewById(R.id.subtract_button_team_one);
        incrementTeam2Score = (TextView) findViewById(R.id.add_button_team_two);
        decrementTeam2Score = (TextView) findViewById(R.id.subtract_button_team_two);
        
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

        start = (TouchView2) findViewById(R.id.start);
        start.setActivity(this);

        if (!Beeper.isServiceStarted()) {
	  		Intent intent = new Intent(this, Beeper.class);
	  		startService(intent);
		}
        bindToMusicPlayerService();
        
        timeChecker = new TimeChecker();
        timeChecker.start();
        
        // extract extras and update private variables accordingly
        Bundle extras = getIntent().getExtras();
        team1Name = extras.getString(TEAM_1_NAME_EXTRA);
        team2Name = extras.getString(TEAM_2_NAME_EXTRA);
        team1NameTextView.setText(team1Name);
        team2NameTextView.setText(team2Name);
        selectedCategory = extras.getInt(CATEGORY_EXTRA);
        updateArraylistAndSpinner();
        scoreToWin = extras.getInt(POINTS_EXTRA);  
    }
    
    public void requestNewWord()
    {
    	if (beeper != null) {
    		if (!beeper.isPlaying()) 
    		{
    			beeper.play();
        		pauseButton.setVisibility(View.VISIBLE);
        	}
    		getNextWord();
    	}
    }
    
    @Override
	public void onPause() {
		super.onPause();

		if (timeChecker != null) {
			timeChecker.stopTimeChecker();
			timeChecker = null;
		}
	}
    
    @Override
    protected void onResume() {
    	super.onResume();
    	
        hideSystemBars();
        
        bindToMusicPlayerService();
        
        if (timeChecker == null && Beeper.isServiceStarted()) {
        	timeChecker = new TimeChecker();
        	timeChecker.start();
		}
    }
    
    @Override
	public void onDestroy(){
		super.onDestroy();

		if (Beeper.isServiceStarted()) {
			beeper.releasePlayer();
			stopService(new Intent(this, Beeper.class));
		}
		unbindToMusicPlayerService();
		
		if (timeChecker != null) {
			timeChecker.stopTimeChecker();
			timeChecker = null;
		}
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
    
    // display dialog when round ends
    public void displayRoundOverDialog() {
    	pauseButton.setVisibility(View.GONE);
    	DialogFragment timeupDialog = new TimeupDialog();
    	timeupDialog.show(getSupportFragmentManager(), "timeup");
    }
    
    // add 1 to team 1 score
    private void incrementTeam1Score() {
    	++team1Score;
    	team1ScoreTextView.setText(Integer.valueOf(team1Score).toString());
    	
    	if (team1Score >= scoreToWin) {
    		if (beeper != null) {
        		beeper.pause();
        	}
    		// popup game over  dialog
        	DialogFragment gameOverDialog = new GameoverDialog();
        	gameOverDialog.show(getSupportFragmentManager(), "gameover");
    	}
    }
    
    // add 1 to team 2 score
    private void incrementTeam2Score() {
    	++team2Score;
    	team2ScoreTextView.setText(Integer.valueOf(team2Score).toString());
    	
    	if (team2Score >= scoreToWin) {
    		if (beeper != null) {
        		beeper.pause();
        	}
    		// popup game over  dialog
        	DialogFragment gameOverDialog = new GameoverDialog();
        	gameOverDialog.show(getSupportFragmentManager(), "gameover");
    	}
    }
    
    private void resetScores() {
    	team1Score = team2Score = 0;
    	team2ScoreTextView.setText(Integer.valueOf(team2Score).toString());
    	team1ScoreTextView.setText(Integer.valueOf(team1Score).toString());
    	
    	
    }
    
    // subtract 1 from team 1 score
    private void decrementTeam1Score() {
    	if (team1Score > 0) {
    		--team1Score;
        	team1ScoreTextView.setText(Integer.valueOf(team1Score).toString());
    	}
    }
    
    // subtract 1 from team 2 score
    private void decrementTeam2Score() {
    	if (team2Score > 0) {
	    	--team2Score;
	    	team2ScoreTextView.setText(Integer.valueOf(team2Score).toString());
    	}
    }
    
    public String getWinningTeamString() {
    	if (team1Score > team2Score) {
    		return team1Name + " Won!";
    	}
    	else {
    		return team2Name + " Won!";
    	}
    }
    
    public String getTeam1Name() {
    	return team1Name;
    }
    
    public String getTeam2Name() {
    	return team2Name;
    }
    
    private void initializeCategorySpinner() {
    	categorySpinner = (Spinner) findViewById(R.id.categories);
    	// Create an ArrayAdapter using the string array and a default spinner layout
    	ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.categories, R.layout.spinner_layout);
    	// Specify the layout to use when the list of choices appears
    	adapter.setDropDownViewResource(R.layout.spinner_item_layout);
    	// Apply the adapter to the spinner
    	categorySpinner.setAdapter(adapter);
    	categorySpinner.setOnItemSelectedListener(this);
    }
    
    // initialize word list based on user selected category
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
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
    	
    	words = new ArrayList<String>(Arrays.asList(wordsArray));
    	selectedCategory = pos;
    }
    
    // initialize word list based on user selected category
    public void updateArraylistAndSpinner() {
    	switch(selectedCategory) {
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

    	words = new ArrayList<String>(Arrays.asList(wordsArray));
    	
    	categorySpinner.setSelection(selectedCategory);
    }
    
    @Override
	public void onNothingSelected(AdapterView<?> parent) {
    	wordsArray = getResources().getStringArray(R.array.all);
    	words = new ArrayList<String>(Arrays.asList(wordsArray));
    	selectedCategory = 0;
	}
    
    // call when new random word is needed but all words have already been used and the list should be recycled
    private void repopulateArraylist() {
    	words = new ArrayList<String>(Arrays.asList(wordsArray));
    }
    
    // get a new randomly selected word
    private void getNextWord() {
    	if (words.size() == 0) {
    		repopulateArraylist();
    	}
    	
    	Random randomNumberGenerator = new Random();
    	int randomNumber = randomNumberGenerator.nextInt(words.size());
    	String randomWord = words.get(randomNumber);
    	words.remove(randomNumber);
    	start.setText(randomWord);
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
			
			beeper.setActivity(MainActivity.this);
			intializeBeeper();
			
			if (beeper.isPlaying()) {
				timeChecker = new TimeChecker();
				timeChecker.start();
			}
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
    private void intializeBeeper() {
    	beeperTrackUri = Uri.parse("android.resource://com.cs185.catchphrase/" + R.raw.beeping);
    	beeper.initializeBeeper(beeperTrackUri);
    }
    
    // recalculate color for background
    private void setBackgroundColor() {
    	if (beeper != null) {
    		int baseColor = Color.parseColor("#0099cc");
    		int baseBlue = Color.blue(baseColor);
    		int baseGreen = Color.green(baseColor);
    		
    		float percentDone = (float) beeper.getCurrentPosition() / (float) beeper.getDuration();
    		float blueComponent = (baseBlue*(1-percentDone));
        	float redComponent = (255*percentDone);
        	float greenComponent = (baseGreen*(1-percentDone));
        	
        	int backgroundColor = Color.argb(255, (int) redComponent, (int) greenComponent, (int) blueComponent);
        	mainView.setBackgroundColor(backgroundColor);
    	}	
    }
    
    private class TimeChecker extends Thread {
    	TimeCheckerRunnable runnable = null;
    	
    	public TimeChecker() {
    		this(new TimeCheckerRunnable());
     	}
    	
    	private TimeChecker(TimeCheckerRunnable runnable) {
    		super(runnable, "time_checker");
    		this.runnable = runnable;
    	}
    	
    	public void stopTimeChecker() {
    		runnable.stopTicker();
    	}
    }

	private class TimeCheckerRunnable implements Runnable {
	   	private final int TICKER_TIME = 100;
    	
    	private boolean canceled = false; 
    	
 
    	@Override
    	public void run() {
     		
     		while(!canceled) {
	    		try {
	    			Thread.sleep(TICKER_TIME);
	    		} catch (InterruptedException e) {
	    			return;
	    		} catch (Exception e) {
	    			return;
	    		}

	    		handler.post(new Runnable() {
	    			@Override
	    			public void run() {
	    				
	    				if(!canceled && beeper != null) {
	    					if (beeper.isPlaying()) {
	    						setBackgroundColor();
	    					}
	    				}
	    			}
	    		});
     		}
    	}
    	
    	public void stopTicker() {
    		canceled = true;
    	}
	}
	
	private void startNewGame() {
		Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
		MainActivity.this.startActivity(intent);
		this.finish();
	}

	@Override
	public void newGameClicked() {
		startNewGame();
	}

	@Override
	public void resetScoreClicked() {
		pauseButton.setVisibility(View.GONE);
		resetScores();
	}

	@Override
	public void resumeClicked() {
		pauseButton.setVisibility(View.VISIBLE);
		beeper.play();
		requestNewWord();
	}

	@Override
	public void exitClicked() {
		this.finish();
	}
	
	@Override
	public void team1Selected() {
		incrementTeam1Score();
	}
	
	@Override
	public void team2Selected() {
		incrementTeam2Score();
	}

	@Override
	public void neitherTeamSelected() {
	}
	
}
