package com.cs185.catchphrase;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TouchView2 extends TextView {
	
	private GestureDetector detector;
	int motions = 0;
	final long time = 500;
	int width;
	
	MainActivity activity;
	// velocity needed to get a new word
	final float exitVelocity = 6000;
	
	public void setActivity(MainActivity activity) {
		this.activity = activity;
	}
	
	private void setDefault()
	{
		detector = new GestureDetector(getContext(),new myGestureListener());
		setText("START");
		WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		width = display.getWidth();
		Log.d("MyLog","Width: " + width);
	}

	public TouchView2(Context context) {
		super(context);
		setDefault();
		// TODO Auto-generated constructor stub
	}
	
	public TouchView2(Context context, AttributeSet attrs)
	{
		super(context,attrs);
		setDefault();
	}
	
	public TouchView2(Context context, AttributeSet attrs, int defStyle)
	{
		super(context,attrs,defStyle);
		setDefault();
	}
	
	@Override
	protected void onDraw(Canvas pCanvas) {
	    int textColor = getTextColors().getDefaultColor();
	    setTextColor(getResources().getColor(R.color.black)); // your stroke's color
	    getPaint().setStrokeWidth(8);
	    getPaint().setStyle(Paint.Style.STROKE);
	    super.onDraw(pCanvas);
	    setTextColor(textColor);
	    getPaint().setStrokeWidth(0);
	    getPaint().setStyle(Paint.Style.FILL);
	    super.onDraw(pCanvas);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		
		if ( event.getPointerCount() == 1 )
		{
			detector.onTouchEvent(event);
		}
		
		motions = ( motions + 1) % 10000;
		return true;
	}
	
	// Listen for Flings
	class myGestureListener extends GestureDetector.SimpleOnGestureListener
	{	
		
		
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
		{
			Log.d("MyLog","Velocity is: " + velocityX);
			if(  Math.abs(velocityX)  > exitVelocity )	// get a new word
			{
				strongAnimation(velocityX);
			}
			else
			{
				weakAnimation(velocityX);
			}
			
			
			return true;
		}
	}
	
	private void strongAnimation(float velocity)
	{	
		//modify time by velocity, resulting in a faster animation
		long animTime = (long) (time / (Math.abs(velocity)/(exitVelocity%(3*exitVelocity))));
		
		float flingDist = width;
		if( velocity < 0)
		{
			flingDist *= -1;
		}
		
		TranslateAnimation anim = new TranslateAnimation(0,flingDist,0,0);
		anim.setDuration(animTime);
		
		// recenter->happens automatically
		anim.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
            	// set opacity to zero
            	setVisibility(View.INVISIBLE);
            	Log.d("MyLog","Setting Invisible");
            	clearAnimation();
            	activity.requestNewWord();
            	setVisibility(View.VISIBLE);
            }
        });
		startAnimation(anim);
	}
	
	private void weakAnimation( float velocity )
	{
		long struggleTime = 50;
		float struggleDist = width/25;
		if ( velocity < 0 )
		{
			struggleDist *= -1;
		}
		
		TranslateAnimation anim = new TranslateAnimation(0,struggleDist,0,0);
		anim.setDuration(struggleTime);
		
		// recenter->happens automatically
		anim.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
            	// set opacity to zero
            }
        });
		startAnimation(anim);	
		// calculate distance based on velocity
		
		// move that distance and return
	}

	

	
//	public void newWord()
//	{
//		// get a new word
//		setText( "" + motions);
//	}
		
}
