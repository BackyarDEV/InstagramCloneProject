package com.backyardev.instagramcloneproject;

import com.backyardev.instagramcloneproject.adapter.CustomListAdapter;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.backyardev.instagramcloneproject.model.ImagePosts;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Map;

public class FeedActivity extends AppCompatActivity {

    ListView listFeed;
    SwipeRefreshLayout swipeRefreshLayout;
    String imageName,name,uid;
    DatabaseReference db ,dbIn;
    private CustomListAdapter adapter;
    ArrayList <ImagePosts> postsList=new ArrayList <>(  );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_feed );
        name = getIntent().getExtras().getString( "name" );

        getSupportActionBar().setTitle( Html.fromHtml( "<font color=\"black\">" + "Feed: " + name + "</font>" ) );

        swipeRefreshLayout=findViewById( R.id.swipeRefresh );
        listFeed = findViewById( R.id.listFeed );
        swipeRefreshLayout.setRefreshing( true );
        feedListFeed();
        adapter = new CustomListAdapter(this,postsList);

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

            dbIn = FirebaseDatabase.getInstance().getReference(name+" pics");
            dbIn.addListenerForSingleValueEvent( new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                  if(dataSnapshot.hasChildren()){
                  collectUserImages((Map<String,Object>) dataSnapshot.getValue());
                }else {
                      Toast.makeText( FeedActivity.this, "No posts found", Toast.LENGTH_LONG ).show();
                        swipeRefreshLayout.setRefreshing( false );
                  }
              }
              @Override
              public void onCancelled(@NonNull DatabaseError databaseError) {

              }
          } );
        }

    private void collectUserImages(Map<String, Object> value) {
        ArrayList<String> imageList = new ArrayList<>(  );
        for (Map.Entry<String, Object> entry : value.entrySet()){
            Map singleUser = (Map) entry.getValue();
            imageName=(String)singleUser.get( "image_name" );
            Log.d( "imageName",imageName );
            imageList.add(imageName);
        }
        postsList.clear();
        for(int i=0;i<imageList.size();i++){
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            storageRef.child( name+" Photos/"+ imageList.get( i ) ).getDownloadUrl()
                    .addOnSuccessListener( new OnSuccessListener <Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    String url=String.valueOf( uri );
                    ImagePosts im = new ImagePosts();
                    im.setThumbnailUrl( url );
                    postsList.add( im );
                        Log.d( "url",url );
                    if(swipeRefreshLayout.isRefreshing()){
                        swipeRefreshLayout.setRefreshing( false );
                    }
                    listFeed.setAdapter(adapter);
                }
            } );
        }


    }
}