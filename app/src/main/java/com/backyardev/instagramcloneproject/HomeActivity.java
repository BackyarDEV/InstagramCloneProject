package com.backyardev.instagramcloneproject;

import android.content.Intent;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    ListView listAllUsers;
    FloatingActionButton button;
    RelativeLayout relProg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_home );

        relProg=findViewById( R.id.relProg );
        listAllUsers=findViewById( R.id.listAllUsers );
        String user=FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        Toast.makeText( this,"Welcome "+user,Toast.LENGTH_SHORT ).show();
        relProg.setVisibility( View.VISIBLE );
        getSupportActionBar().setTitle( Html.fromHtml("<font color=\"black\">" + getString(R.string.home_title) + "</font>"));
            populateList();

        FloatingActionButton idLogoutFab = findViewById( R.id.idLogoutFab );
        FloatingActionButton idCameraFab = findViewById( R.id.idCameraFab );
        FloatingActionButton idGalleryFab = findViewById( R.id.idGalleryFab );


        listAllUsers.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(HomeActivity.this,FeedActivity.class);
                i.putExtra( "name",String.valueOf(listAllUsers.getItemAtPosition( position )) );
                startActivity( i );
            }
        } );
        idLogoutFab.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent( HomeActivity.this,LoginActivity.class );
                startActivity( i );
                finish();
            }
        } );
        idCameraFab.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePicture = new Intent( MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, 0);
            }
        } );
        idGalleryFab.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , 1);
            }
        } );
    }

    private void populateList() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");
        ref.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        collectPhoneNumbers((Map<String,Object>) dataSnapshot.getValue());
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("dbError",databaseError.getMessage());
                    }
                });
    }

    private void collectPhoneNumbers(Map<String, Object> name) {
        ArrayList<String> userNames = new ArrayList<>();
        for (Map.Entry<String, Object> entry : name.entrySet()){

            Map singleUser = (Map) entry.getValue();
            userNames.add((String) singleUser.get("name"));
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                userNames );
        listAllUsers.setAdapter(arrayAdapter);

        relProg.setVisibility( View.INVISIBLE );
    }

}
