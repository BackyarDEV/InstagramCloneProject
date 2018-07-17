package com.backyardev.instagramcloneproject;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE=1;
    private static final int GALLERY_REQUEST_CODE=2;
    static final int REQUEST_TAKE_PHOTO = 1;

    ListView listAllUsers;
    FloatingActionButton button;
    RelativeLayout relProg,imageRel;
    private ProgressDialog progressDialog;
    private StorageReference storage;
    FloatingActionsMenu idOpsFab;
    Button btnUpload,btnCancel;
    ImageView uploadImageView;
    String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_home );

        progressDialog=new ProgressDialog( this );
        relProg=findViewById( R.id.relProg );
        imageRel=findViewById( R.id.imageRel );
        idOpsFab=findViewById( R.id.idOpsFab );
        btnCancel=findViewById( R.id.btnCancel );
        btnUpload=findViewById( R.id.btnUpload );
        listAllUsers=findViewById( R.id.listAllUsers );
        uploadImageView=findViewById( R.id.uploadImageView );
        user=FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        Toast.makeText( this,"Welcome "+user,Toast.LENGTH_SHORT ).show();
        relProg.setVisibility( View.VISIBLE );
        getSupportActionBar().setTitle( Html.fromHtml("<font color=\"black\">" + getString(R.string.home_title) + "</font>"));
            populateList();


            storage=FirebaseStorage.getInstance().getReference();
        DatabaseReference mDB = FirebaseDatabase.getInstance().getReference().child( "users" );
        FirebaseAuth mAuth = FirebaseAuth.getInstance();


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

                if (ContextCompat.checkSelfPermission(HomeActivity.this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale( HomeActivity.this,
                            Manifest.permission.CAMERA )) {
                    } else {
                        ActivityCompat.requestPermissions( HomeActivity.this,
                                new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE );
                    }
                }
                dispatchTakePictureIntent();

            }
        } );
        idGalleryFab.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, GALLERY_REQUEST_CODE);
            }
        } );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult( requestCode, resultCode, data );

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            imageRel.setVisibility( View.VISIBLE );
            idOpsFab.setVisibility( View.INVISIBLE );
            File imageFile = getImageFile();

            Bitmap photo = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            uploadImageView.setImageBitmap( photo );
        }else if(requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            imageRel.setVisibility( View.VISIBLE );
            idOpsFab.setVisibility( View.INVISIBLE );
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            uploadImageView.setImageBitmap( BitmapFactory.decodeFile(picturePath));
        }
        btnCancel.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageRel.setVisibility( View.INVISIBLE );
                idOpsFab.setVisibility( View.VISIBLE );
            }
        } );
        btnUpload.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage( "Uploading..." );
                progressDialog.show();
                uploadImageView.setDrawingCacheEnabled(true);
                uploadImageView.buildDrawingCache();
                Bitmap bitmap = ((BitmapDrawable) uploadImageView.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 75, baos);
                byte[] data = baos.toByteArray();
                String timestamp=String.valueOf( new Date().getTime() );
                StorageReference storRef =storage.child( user+"'s Photos" ).child( "JPEG_"+timestamp+".jpg" );
                UploadTask uploadTask = (UploadTask) storRef.putBytes(data).addOnFailureListener( new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Log.d( "upload","upload failed!"+ e.getMessage() );

                        Toast.makeText( HomeActivity.this,"Upload Failed!!"+ e.getMessage(),Toast.LENGTH_SHORT ).show();
                    }
                } ).addOnSuccessListener( new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        Toast.makeText( HomeActivity.this,"Upload Successful!!",Toast.LENGTH_SHORT ).show();
                        imageRel.setVisibility( View.INVISIBLE );
                        idOpsFab.setVisibility( View.VISIBLE );
                    }
                } );
            }
        } );


    }
    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir( Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
    private File getImageFile() {
        File f = getExternalFilesDir( Environment.DIRECTORY_PICTURES);
        File imageFiles[] = f.listFiles();

        if (imageFiles == null || imageFiles.length == 0) {
            return null;
        }

        File lastModifiedFile = imageFiles[0];
        for (int i = 1; i < imageFiles.length; i++) {
            if (lastModifiedFile.lastModified() < imageFiles[i].lastModified()) {
                lastModifiedFile = imageFiles[i];
            }
        }
        return lastModifiedFile;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.d( "IOlog",ex.getMessage() );
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.backyardev.instagramcloneproject.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private void populateList() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");
        ref.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        collectUserNames((Map<String,Object>) dataSnapshot.getValue());
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("dbError",databaseError.getMessage());
                    }
                });
    }

    private void collectUserNames(Map<String, Object> name) {
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