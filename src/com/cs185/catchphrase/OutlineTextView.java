package com.cs185.catchphrase;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

public class OutlineTextView extends TextView {
	
	public OutlineTextView(Context context) {
		super(context);
	}
	
	public OutlineTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public OutlineTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
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
}
