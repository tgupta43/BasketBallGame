package edu.sjsu.android.accelerometer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Insets;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.util.Size;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.WindowMetrics;

import androidx.annotation.RequiresApi;

public class SimulationView extends View implements SensorEventListener
{
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private Display mDisplay;

    private Bitmap mField;
    private Bitmap field;
    private Bitmap mBasket;
    private Bitmap mBitmap;
    private static final int BALL_SIZE=64;
    private static final int BASKET_SIZE=175;

    private float mXOrigin;
    private float mYOrigin;
    private float mHorizontalBound;
    private float mVerticalBound;

    private Particle mBall =new Particle();
    private float mSensorX=0;
    private long mSensorTimeStamp=0;
    private float mSensorY=0;
    private float mSensorZ=0;


    public SimulationView(Context context) {
        super(context);
        // Initialize images from drawable
        Bitmap ball = BitmapFactory.decodeResource(getResources(), R.drawable.ball);
        mBitmap = Bitmap.createScaledBitmap(ball, BALL_SIZE, BALL_SIZE, true);
        Bitmap basket = BitmapFactory.decodeResource(getResources(), R.drawable.basket);
        mBasket = Bitmap.createScaledBitmap(basket, BASKET_SIZE, BASKET_SIZE, true);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        field = BitmapFactory.decodeResource(getResources(), R.drawable.field, opts);




        WindowManager mWindowManager = (WindowManager)
                context.getSystemService(Context.WINDOW_SERVICE);
        mDisplay = mWindowManager.getDefaultDisplay();
        Point size = new Point();
        mDisplay.getSize(size);
        int width = size.x;
        int height = size.y;
        mField = Bitmap.createScaledBitmap(field, width, height, true);

        mXOrigin= (float) (width*0.5);
        mYOrigin=(float) (height*0.5);
       mHorizontalBound = (float) ((width - BALL_SIZE) *0.5);
        mVerticalBound = (float) ((height - BALL_SIZE)*0.5 );



        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    public void onSizeChanged (int w, int h, int oldw, int oldh) {
        Bitmap ball = BitmapFactory.decodeResource(getResources(), R.drawable.ball);
        mBitmap = Bitmap.createScaledBitmap(ball, BALL_SIZE, BALL_SIZE, true);
        Bitmap basket = BitmapFactory.decodeResource(getResources(), R.drawable.basket);
        mBasket = Bitmap.createScaledBitmap(basket, BASKET_SIZE, BASKET_SIZE, true);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        field = BitmapFactory.decodeResource(getResources(), R.drawable.field, opts);

    }


        @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
            if (sensorEvent.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
                return;

            int x=mDisplay.getRotation();
                if(x==Surface.ROTATION_0)
                { mSensorX = sensorEvent.values[0];
                    mSensorY = sensorEvent.values[1];}
                else if (x==Surface.ROTATION_90)
                {mSensorX = -sensorEvent.values[1];
                    mSensorY = sensorEvent.values[0];}
                else if (x==Surface.ROTATION_180)
                {mSensorX = -sensorEvent.values[0];
                    mSensorY = -sensorEvent.values[1];}
               else {

                    mSensorX = sensorEvent.values[1];
                    mSensorY = -sensorEvent.values[0];}

            mSensorZ = sensorEvent.values[2];
            mSensorTimeStamp = sensorEvent.timestamp;

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(mField, 0, 0, null);
        canvas.drawBitmap(mBasket, mXOrigin - BASKET_SIZE / 2, mYOrigin - BASKET_SIZE / 2, null);

        mBall.updatePosition(mSensorX, mSensorY, mSensorZ, mSensorTimeStamp);
        mBall.resolveCollisionWithBounds(mHorizontalBound, mVerticalBound);

        canvas.drawBitmap(mBitmap,
                (mXOrigin - BALL_SIZE / 2) + mBall.mPosX,
                (mYOrigin - BALL_SIZE / 2) - mBall.mPosY, null);

        invalidate();
    }

    public void startSimulation() {
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);

    }

    public void stopSimulation() {
        mSensorManager.unregisterListener(this);

    }
}
