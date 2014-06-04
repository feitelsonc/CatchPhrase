package com.cs185.catchphrase;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;

public class Beeper extends Service implements MediaPlayer.OnCompletionListener{

	private MediaPlayer player = new MediaPlayer();
	private final IBinder binder = new LocalBinder();
	private MainActivity activity;
	static private boolean serviceStarted = false;

	static public boolean isServiceStarted() {
		return serviceStarted;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		serviceStarted = true;
		return binder;
	}

	public class LocalBinder extends Binder {
		public Beeper getService() {
			// Return this instance of LocalService so clients can call public methods
			return Beeper.this;
		}
	}
	
	public void setActivity(MainActivity activity) {
		this.activity = activity;
	}

	public void initializeBeeper(Uri uri) {
		player.reset();
		player = new MediaPlayer();
		try {
			player.setAudioStreamType(AudioManager.STREAM_MUSIC);
			player.setDataSource(getApplicationContext(), uri);
			player.prepare();
			player.start();
			
			player.setOnCompletionListener(this);
		}
		catch(Exception e) {
		}
	}

	public void pause() {
		if (player.isPlaying()) {
			player.pause();
		}
	}

	public void play() {
		if (!player.isPlaying()) {
			player.start();
		}
	}
	
	public boolean isPlaying() {
		if (player != null) {
			return player.isPlaying();
		}
		else {
			return false;
		}
	}

	public int getCurrentPosition() {
		return player.getCurrentPosition();
	}

	public int getDuration() {
		return player.getDuration();
	}

	void releasePlayer() {
		player.release();
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		activity.displayRoundOverDialog();
	}

}