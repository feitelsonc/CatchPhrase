package com.cs185.catchphrase;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
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
	
	public void translateText(float deltaX, float deltaY)
	{
		// move text
		int originalPos[] = new int[2];
		getLocationOnScreen(originalPos);
		// now holds original position
		TranslateAnimation anim = new TranslateAnimation( 0, deltaX , 0, 0 );
		anim.setDuration(0);
		startAnimation(anim);
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
		float pX, pY, sX;
		int pEvent = 0;		// previous event number
		float gestureLength = 100;	// arbitrary gesture length to be determined
		
		void doTranslation(MotionEvent event)
		{
			int action = MotionEventCompat.getActionMasked(event);
			
			if( action != MotionEvent.ACTION_DOWN && ( pEvent + 1 ) % 10000 != getEventNum() )	// Input Stream was interrupted
			{
				//Log.d("MyLog", "Failed: Internal: " + pEvent + "Total: " + motions);
				return;	// do nothing
			}
			
			pEvent = getEventNum();	// set previous event for future checking
			//Log.d("MyLog", "Translation Test Passes");
			
			switch(action)
			{
				case MotionEvent.ACTION_DOWN:
					sX = event.getX();	// to track entire x movement
					pX = event.getX();	// to calculate next movement
					pY = event.getY();	// to calculate next movement
					break;
				case MotionEvent.ACTION_UP:
					translateText(event.getX() - pX, event.getY() - pY);
					if( event.getX() - sX > gestureLength )
					{
						// gesture is large enough to warrant a new word
						newWord();	// get new word
					}
					else
					{
						// recenter word
						translateText( sX - event.getX(), 0);
					}
					break;
				default: // Same for last and intermediate steps
					translateText(event.getX() - pX, event.getY() - pY );
					pX = event.getX();
					pY = event.getY();
					break;
			}
		}
	}
		
}
