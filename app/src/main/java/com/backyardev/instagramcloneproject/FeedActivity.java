package com.backyardev.instagramcloneproject;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class FeedActivity extends AppCompatActivity {

    ListView listFeed;
    RelativeLayout progRel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_feed );
        String name = getIntent().getExtras().getString( "name" );
        getSupportActionBar().setTitle( Html.fromHtml( "<font color=\"black\">" + "Feed: " + name + "</font>" ) );

        progRel=findViewById( R.id.progRel );
        progRel.setVisibility( View.VISIBLE );
        listFeed = findViewById( R.id.listFeed );
        feedListFeed();

    }

    private void feedListFeed() {



        progRel.setVisibility( View.INVISIBLE );
    }
}
