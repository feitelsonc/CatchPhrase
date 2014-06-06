package com.cs185.catchphrase;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

public class TouchView extends TextView {
	
	private TranslationHandler tDetector;
	int motions = 0;
	
	private void setDefault()
	{
		tDetector = new TranslationHandler();
		setText("START");
	}

	public TouchView(Context context) {
		super(context);
		setDefault();
		// TODO Auto-generated constructor stub
	}
	
	public TouchView(Context context, AttributeSet attrs)
	{
		super(context,attrs);
		setDefault();
	}
	
	public TouchView(Context context, AttributeSet attrs, int defStyle)
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
			tDetector.doTranslation(event);
		}
		
		motions = ( motions + 1) % 10000;
		return true;
	}
	// Moves text in accordance with gesture
	public void move(float deltaX)
	{
		Log.d("MyLog","Moving " + deltaX );

		setTranslationX(deltaX);
	}
	
	// Not used, but could be used to animate new word/old word animations
	public void translateText(float deltaX, float deltaY)
	{
		Log.d("Log", "Delta X is: " + deltaX);
		// move text
		int originalPos[] = new int[2];
		getLocationOnScreen(originalPos);
		// now holds original position
		TranslateAnimation anim = new TranslateAnimation( 0, deltaX , 0, 0 );
		anim.setDuration(5);
		anim.setFillEnabled(true);
		anim.setFillAfter(true);
		anim.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
            	// need to set these layout parameters to keep the animation in place
             //   RelativeLayout.LayoutParams rootlp = (RelativeLayout.LayoutParams) getView().getLayoutParams();
             //   rootlp.topMargin = -rootlp.height*75/100;
           //     rootWrapper.setLayoutParams(rootlp);
            }
        });

		startAnimation(anim);
		//new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.ABSOLUTE, heightOfRootView-excuseContainer.getHeight(), Animation.ABSOLUTE, currentYPoint);
	}
	
	public int getEventNum()
	{
		return motions;
	}
	
	public void newWord()
	{
		// get a new word
	}
	
	class TranslationHandler
	{
		float sX;
		int pEvent = 0;		// previous event number
		final float gestureLength = 120;	// arbitrary gesture length to be determined
		
		void doTranslation(MotionEvent event)
		{
			int action = MotionEventCompat.getActionMasked(event);
			
			if( action != MotionEvent.ACTION_DOWN && ( pEvent + 1 ) % 10000 != getEventNum() )	// Input Stream was interrupted
			{
				return;	// do nothing
			}
			
			pEvent = getEventNum();	// set previous event for future checking
			
			switch(action)
			{
				case MotionEvent.ACTION_DOWN:
					sX = event.getX();	// to track entire x movement
					break;
				case MotionEvent.ACTION_UP:
					if( event.getX() - sX > gestureLength || event.getX() - sX < -gestureLength )
					{
						// gesture is large enough to warrant a new word
						Log.d("MyLog","NEW WORD: " + (event.getX() - sX) );
						// make invisible
						// maybe animate off-screen
						// then upon completion in animation listener get new word and recenter
						newWord();	// get new word
					}
			
					// recenter word, can be done elsewhere
					move(0);
					break;
				default:    // intermediate steps
					move(event.getX() - sX);
					break;
			}
		}
	}
		
}
