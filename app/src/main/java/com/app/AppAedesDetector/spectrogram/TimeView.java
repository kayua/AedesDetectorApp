
package com.app.AppAedesDetector.spectrogram;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Class associated with the wave form view
 * Handles events:
 *  onTouchEvent, onScroll, onDraw
 */
public class TimeView extends View {
	
	// Attributes
    private Paint paint = new Paint();
    private GestureDetector detector;
    private float gain = 1.0f;
    private int fftResolution;
    private float[] wave;
    
    // Window
    public TimeView(Context context) {
        super(context);
        detector = new GestureDetector(getContext(), new GestureListener());
    }
    public TimeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        detector = new GestureDetector(getContext(), new GestureListener());
    }
    
    /**
     * Touch event handling
     */
    @SuppressLint("ClickableViewAccessibility")
	@Override
    public boolean onTouchEvent(MotionEvent event) {
	    detector.onTouchEvent(event);
	    invalidate();
	    return true;
    }
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
    	@Override
        public boolean onScroll (MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
    		gain *= (1.0f+distanceY*0.01f);
        	return true;
        }
    }
    
    /**
     * Simple sets
     */
    public void setFFTResolution(int res) {
    	fftResolution = res;
    	wave = new float[res];
    }
    public void setWave(float[] w) {
    	System.arraycopy(w, 0, wave, 0, w.length);
    }

    /**
     * Called whenever a redraw is needed
     * Renders wave form as a series of lines
     */
    @Override
    public void onDraw(Canvas canvas) {
    	int width = canvas.getWidth();
    	int height = canvas.getHeight();
    	Activity a = (Activity) Misc.getAttribute("activity");
	   	boolean nightMode = Misc.getPreference(a, "night_mode", true);
    	
    	// Draw axis
		paint.setStrokeWidth(1);
		if (!nightMode) paint.setColor(Color.LTGRAY);
		else			paint.setColor(Color.DKGRAY);
    	canvas.drawLine(0, height/2, width, height/2, paint);
    	
    	// Draw wave
    	paint.setStrokeWidth(Integer.valueOf(Misc.getPreference(a, "line_width", "1")));
    	if (!nightMode) paint.setColor(Color.BLACK);
		else		 	paint.setColor(Color.WHITE);
    	float x1 = 0;
    	float y1 = height*(0.5f+0.5f*gain*wave[0]);
    	for (int i=1; i<fftResolution; i++) {
    		float x2 = width*i/(fftResolution);
    		float y2 = height*(0.5f+0.5f*gain*wave[i]);
    		if ((x1>0 && x1<width) && (x2>0 && x2<width))
    			canvas.drawLine(x1, height-y1, x2, height-y2, paint);
    		x1 = x2;
    		y1 = y2;
    	}
    }
}