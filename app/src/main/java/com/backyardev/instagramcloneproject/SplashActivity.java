package com.backyardev.instagramcloneproject;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

public class SplashActivity extends AppCompatActivity {

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Intent i = new Intent(SplashActivity.this,LoginActivity.class );
            startActivity( i );
            finish();
        }
    };
    Runnable run = new Runnable() {
        @Override
        public void run() {
            splashProgress.setVisibility( View.VISIBLE );
            splashProgress.setMax( 100 );
            splashProgress.setProgress( 0 );

            final Thread thread = new Thread(  ){
                @Override
                public void run() {
                    try{
                        for(int i=0;i<100;i++){
                            splashProgress.setProgress( i );
                            sleep( 20 );
                        }
                    }
                    catch ( Exception e){
                        e.printStackTrace();
                    }
                    finally{

                        handler.postDelayed(runnable, 500);

                    }
                }
            };
            thread.start();
        }
    };

    ProgressBar splashProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_splash );
        splashProgress = findViewById( R.id.splashProgress );
        handler.postDelayed(run, 500);
    }
}
