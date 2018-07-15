package com.backyardev.instagramcloneproject;

import android.content.Intent;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {

    ListView listAllUsers;
    FloatingActionButton button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_home );

        listAllUsers=findViewById( R.id.listAllUsers );
        String user=FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        Toast.makeText( this,"Welcome "+user,Toast.LENGTH_SHORT ).show();


        FloatingActionButton idLogoutFab = findViewById( R.id.idLogoutFab );
        FloatingActionButton idCameraFab = findViewById( R.id.idCameraFab );
        FloatingActionButton idGalleryFab = findViewById( R.id.idGalleryFab );

        idCameraFab.setBackground( getDrawable( R.drawable.camera ) );
        idLogoutFab.setBackground( getDrawable( R.drawable.logout ) );
        idGalleryFab.setBackground( getDrawable( R.drawable.gallery ) );

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
}
