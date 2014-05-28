package se.chalmers.balmung.smartcar;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

public class CameraJoystickView extends JoystickView {
	public CameraJoystickView(Context context) {
		super(context);
	}

	public CameraJoystickView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public CameraJoystickView(Context context, AttributeSet attrs,
			int defaultStyle) {
		super(context, attrs, defaultStyle);
	}

	protected void initJoystickView() {
		mainCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
		mainCircle.setColor(Color.BLACK);
		mainCircle.setStyle(Paint.Style.FILL_AND_STROKE);
		mainCircle.setAlpha(0);
		
		button = new Paint(Paint.ANTI_ALIAS_FLAG);
		button.setColor(Color.BLACK);
		button.setStyle(Paint.Style.FILL);
		button.setAlpha(10);
	}
}
