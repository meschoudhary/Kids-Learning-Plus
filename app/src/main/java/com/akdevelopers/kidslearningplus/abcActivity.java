package com.akdevelopers.kidslearningplus;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.ActionBar;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

public class abcActivity extends Activity {

    static ImageHandler imageBundle;
    private GestureDetectorCompat mDetector;
    private boolean isBelowKitkat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abc_activity);

        initialise();
        mDetector = new GestureDetectorCompat(abcActivity.this, new SwipeGesture());
        isBelowKitkat = (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Setting immersive sticky flag for android kitkat and above
        if (!isBelowKitkat) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                    View.SYSTEM_UI_FLAG_FULLSCREEN);
        } else {
            //Make Navigation bar dim on api 18 and below
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.mDetector.onTouchEvent(event);
        if (isBelowKitkat) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        }
        return super.onTouchEvent(event);
    }

    public void launchMainMenu(View view) {
        imageBundle = null;
        Intent intent = new Intent(abcActivity.this, MainMenu.class);
        startActivity(intent);
    }

    /**
     * To Initialise some things on new thread
     */
    private void initialise() {
        Thread load = new Thread(new Runnable() {
            @Override
            public void run() {
                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

                ImageSwitcher alphabet_switcher = (ImageSwitcher) findViewById(R.id.alphabet_switcher);
                alphabet_switcher.setFactory(new ViewSwitcher.ViewFactory() {
                    @Override
                    public View makeView() {
                        ImageView myView = new ImageView(abcActivity.this);
                        myView.setScaleType(ImageView.ScaleType.FIT_XY);
                        myView.setLayoutParams(new ImageSwitcher.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT));
                        return myView;
                    }
                });
                imageBundle = new ImageHandler(abcActivity.this, alphabet_switcher);

                int[] imagesId = {
                        R.drawable.alphabet_a, R.drawable.alphabet_b, R.drawable.alphabet_c, R.drawable.alphabet_d,
                        R.drawable.alphabet_e, R.drawable.alphabet_f, R.drawable.alphabet_g, R.drawable.alphabet_h,
                        R.drawable.alphabet_i, R.drawable.alphabet_j, R.drawable.alphabet_k, R.drawable.alphabet_l,
                        R.drawable.alphabet_m, R.drawable.alphabet_n, R.drawable.alphabet_o, R.drawable.alphabet_p,
                        R.drawable.alphabet_q, R.drawable.alphabet_r, R.drawable.alphabet_s, R.drawable.alphabet_t,
                        R.drawable.alphabet_u, R.drawable.alphabet_v, R.drawable.alphabet_w, R.drawable.alphabet_x,
                        R.drawable.alphabet_y, R.drawable.alphabet_z};

                imageBundle.setImageLibrary(imagesId);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageBundle.loadFirst();
                    }
                });
            }
        });
        load.start();
    }
}


// Used to detect Gestures and changing images accordingly
class SwipeGesture extends GestureDetector.SimpleOnGestureListener {
    @Override
    public boolean onDown(MotionEvent event) {
        return true;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        float sensitivity = 100;

        if ((e1.getX() - e2.getX()) > sensitivity) {                   //On swiping left
            abcActivity.imageBundle.mNextImage();
        } else if ((e2.getX() - e1.getX()) > sensitivity) {            //On swiping right
            abcActivity.imageBundle.mPrevImage();
        }
        return true;
    }
}