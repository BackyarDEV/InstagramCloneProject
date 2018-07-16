package com.backyardev.instagramcloneproject;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    FirebaseAuth fAuth;
    Handler handler = new Handler();

    ProgressBar splashProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_splash );
        splashProgress = findViewById( R.id.splashProgress );
        splashProgress.setVisibility( View.VISIBLE );
        fAuth=FirebaseAuth.getInstance();
        if(fAuth.getCurrentUser()!=null)
            handler.postDelayed(cont, 500);
        else{
            handler.postDelayed(login, 500);
        }
    }
    Runnable login = new Runnable() {
        @Override
        public void run() {
            Intent i = new Intent(SplashActivity.this,LoginActivity.class );
            startActivity( i );
            finish();
        }
    };
    Runnable cont = new Runnable() {
        @Override
        public void run() {
            Intent i = new Intent(SplashActivity.this,HomeActivity.class );
            startActivity( i );
            finish();
        }
    };
}
