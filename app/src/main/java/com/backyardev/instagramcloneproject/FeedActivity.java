package com.backyardev.instagramcloneproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;

public class FeedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_feed );
        String name= getIntent().getExtras().getString( "name" );
        getSupportActionBar().setTitle( Html.fromHtml("<font color=\"black\">" + "Feed: "+name + "</font>"));
    }
}
