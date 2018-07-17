package com.backyardev.instagramcloneproject;


import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class FeedActivity extends AppCompatActivity {

    ListView listFeed;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_feed );
        String name = getIntent().getExtras().getString( "name" );
        getSupportActionBar().setTitle( Html.fromHtml( "<font color=\"black\">" + "Feed: " + name + "</font>" ) );

        swipeRefreshLayout=findViewById( R.id.swipeRefresh );
        listFeed = findViewById( R.id.listFeed );
        feedListFeed();

        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i("refreshLog", "onRefresh called from SwipeRefreshLayout");
                        feedListFeed();
                    }
                }
        );
    }

    private void feedListFeed() {



        if(swipeRefreshLayout.isRefreshing()){
            swipeRefreshLayout.setRefreshing( false );
        }
    }
}
