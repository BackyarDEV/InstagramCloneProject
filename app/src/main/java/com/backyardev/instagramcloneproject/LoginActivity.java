package com.backyardev.instagramcloneproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    EditText editName,editPass;
    Button btnLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_login );
        btnLogin=findViewById( R.id.btnLogin );
        editPass=findViewById( R.id.editPass );
        editName=findViewById( R.id.editName );

    }

    public void btnLoginAction(View view) {

        boolean login=true;
        String Name, Pass;
        Name=editName.getText().toString().trim();
        Pass=editPass.getText().toString().trim();
        if (Name.isEmpty()){
            editName.setError( "Required Field!" );
            login=false;
        }
        if (Pass.isEmpty()){
            editPass.setError( "Required Field!" );
            login=false;
        }
        if(login){
            Intent i = new Intent( LoginActivity.this,HomeActivity.class );
            startActivity( i );
            finish();
        }
    }
}
