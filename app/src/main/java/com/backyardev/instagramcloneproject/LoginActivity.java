package com.backyardev.instagramcloneproject;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class LoginActivity extends AppCompatActivity {

    EditText editName,editPass,editEmail;
    Button btnLogin;
    TextView forgotPass;
    TextInputLayout idNameTIP;
    private FirebaseAuth mAuth;
    RelativeLayout relProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_login );
        btnLogin=findViewById( R.id.btnLogin );
        editPass=findViewById( R.id.editPass );
        editName=findViewById( R.id.editName );
        editEmail=findViewById( R.id.editEmail );
        idNameTIP=findViewById( R.id.idNameTIP );
        relProgress =findViewById( R.id.relProgress);
        forgotPass=findViewById( R.id.forgotPass );
        mAuth = FirebaseAuth.getInstance();
    }

    public void btnLoginAction(View view) {

        boolean login=true;
        String Email, Pass;
        Email=editEmail.getText().toString().trim();
        Pass=editPass.getText().toString().trim();
        if (Email.isEmpty()){
            editEmail.setError( "Required Field!" );
            login=false;
        }
        if (Pass.isEmpty()){
            editPass.setError( "Required Field!" );
            login=false;
        }
        if(login){
                login(Email,Pass);
        }
    }

    void login(final String Email, final String Pass){
        relProgress.setVisibility( View.VISIBLE );
        mAuth.signInWithEmailAndPassword(Email, Pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        relProgress.setVisibility( View.INVISIBLE );
                        if (!task.isSuccessful()) {
                            try {

                                Log.d( "loginTest", String.valueOf( task.getException() ) );
                                throw task.getException();

                            } catch (FirebaseAuthInvalidUserException e) {
                                Log.d( "login test", "onComplete: invalid email" );
                                showToast( "Account not registered yet!" );
                                idNameTIP.setVisibility( View.VISIBLE );
                                btnLogin.setText( getResources().getString( R.string.register ) );
                                editName.requestFocus();
                                register( Email, Pass );
                            } catch (FirebaseAuthInvalidCredentialsException wrongPassword) {
                                Log.d( "loginTest", "onComplete: wrong_password" );
                                forgotPass.setVisibility( View.VISIBLE );
                                editPass.setError( "Invalid password" );
                                editPass.requestFocus();
                            } catch (Exception e) {
                                Log.d( "loginTest extra", e.getMessage() );
                            }
                        }
                            else
                            {
                                Log.d("loginTest", "signInWithEmail:success");
                                Intent i = new Intent( LoginActivity.this,HomeActivity.class );
                                startActivity( i );
                                finish();
                            }


                    }
                });
    }
    void register(final String Email,final String Pass){
        btnLogin.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String Name=editName.getText().toString().trim();
                if(Name.isEmpty()) editName.setError( "Required Field!" );
                else{
                relProgress.setVisibility( View.VISIBLE );
                mAuth.createUserWithEmailAndPassword(Email, Pass)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                relProgress.setVisibility( View.INVISIBLE );
                                if (!task.isSuccessful()) {
                                    showToast( "Authentication failed." + task.getException() );
                                } else {
                                    uploadDataToFirebase(Name);
                                    showToast( "Registration Successful, Signing In...." );
                                    login( Email,Pass );
                                }
                            }
                        });}
            }
        } );
    }

    private void uploadDataToFirebase(String Name) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName( Name ).build();

        user.updateProfile( profileUpdates ).addOnCompleteListener( new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d( "username", "User profile updated." );
                } else {
                    Log.d( "username", "User profile update error." );
                }
            }
        } );
        String uID = mAuth.getCurrentUser().getUid();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = database.getReference();
        dbRef.child( "users" ).push().setValue( new UserInfo( Name, uID ) );
    }

    void showToast(String msg){
        Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    public void resetPass(View view) {
        relProgress.setVisibility( View.VISIBLE );
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String emailAddress = editEmail.getText().toString();
        Log.d( "mail",emailAddress );
        auth.sendPasswordResetEmail( emailAddress ).addOnCompleteListener( new OnCompleteListener <Void>() {
            @Override
            public void onComplete(@NonNull Task <Void> task) {
                relProgress.setVisibility( View.INVISIBLE );
                if (task.isSuccessful()) {
                    Log.d("reset mail", "Email sent.");
                    showToast( "A password reset email has been sent! Check your inbox" );
                }else{
                    try {
                        throw task.getException();
                    } catch (Exception e) {
                        showToast( e.getMessage() );
                    }
                }
            }
        } );
    }
}
