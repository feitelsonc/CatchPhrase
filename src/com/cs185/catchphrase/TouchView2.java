package com.cs185.catchphrase;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
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
import android.widget.TextView;

public class TouchView2 extends TextView {
	
	private GestureDetector detector;
	final long time = 500;
	int width;
	float currentTranslation = 0;
	MainActivity activity;
	
	// velocity needed to get a new word
	final float exitVelocity = 3000;
	boolean wasFling = false;
	
	public void setActivity(MainActivity activity) {
		this.activity = activity;
	}
	
	private void setDefault()
	{
		detector = new GestureDetector(getContext(),new myGestureListener());
		WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		width = display.getWidth();
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
		setTranslationX(currentTranslation);
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
		
		if (event.getActionMasked() == MotionEvent.ACTION_UP )
		{
			if ( wasFling )
			{
				wasFling = false;
			}
			else
			{
				currentTranslation = 0;
				invalidate();
			}
		}
		return true;
	}
	
	// Listen for Flings
	class myGestureListener extends GestureDetector.SimpleOnGestureListener
	{		
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
		{
			wasFling = true;
			strongAnimation(velocityX);
			return true;
		}
		
		@Override
		public boolean onScroll( MotionEvent e1, MotionEvent e2, float distanceX, float distanceY )
		{
			currentTranslation += e2.getX() - e1.getX();
			setTranslationX(currentTranslation);
			return true;
		}
	}
	
	private void strongAnimation(float velocity)
	{	
		// change time based on velocity
		float averageVelocity = 1800;
		if ( velocity > 1.5 * averageVelocity)
			velocity = (float) (1.5 * averageVelocity);
		long animTime;
		if ( velocity > averageVelocity )
		{
			animTime = (long) ( time / ( velocity / averageVelocity) );
		}
		else
		{
			animTime = time;
		}
		float ratio = (width - Math.abs(currentTranslation)) / width;
		//change time based on x position
		animTime = (long) (animTime * ratio);
		// Change fling distance based on screen position
		float flingDist = width * ratio;
		// Fling will be determined by current X position on screen. Using direction of velocity results in many more mistakes
		if( currentTranslation < 0)
		{
			flingDist *= -1;
		}
		
		TranslateAnimation anim = new TranslateAnimation(currentTranslation,currentTranslation + flingDist,0,0);
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
            	currentTranslation = 0;
            	clearAnimation();
            	activity.requestNewWord();
            	setVisibility(View.VISIBLE);
            }
        });
		startAnimation(anim);
	}
}
