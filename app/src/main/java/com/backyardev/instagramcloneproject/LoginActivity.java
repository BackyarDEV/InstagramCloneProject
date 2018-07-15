package com.backyardev.instagramcloneproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;


public class LoginActivity extends AppCompatActivity {

    EditText editName,editPass,editEmail;
    Button btnLogin;
    TextInputLayout idNameTIP;
    private FirebaseAuth mAuth;
    private static final String TAG = "LoginActivity";
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_login );
        btnLogin=findViewById( R.id.btnLogin );
        editPass=findViewById( R.id.editPass );
        editName=findViewById( R.id.editName );
        editEmail=findViewById( R.id.editEmail );
        idNameTIP=findViewById( R.id.idNameTIP );
        progressBar =findViewById( R.id.registerProgress);
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
        progressBar.setVisibility( View.VISIBLE );
        mAuth.signInWithEmailAndPassword(Email, Pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility( View.INVISIBLE );
                        if (!task.isSuccessful()) {
                            try {

                                Log.d( "loginTest", String.valueOf( task.getException() ) );
                                throw task.getException();

                            } catch (FirebaseAuthInvalidUserException e) {
                                Log.d( "login test", "onComplete: invalid email" );
                                showToast( "Account not registered yet!" );
                                idNameTIP.setVisibility( View.VISIBLE );
                                btnLogin.setText( getResources().getString( R.string.register ) );
                                register( Email, Pass );
                            } catch (FirebaseAuthInvalidCredentialsException wrongPassword) {
                                Log.d( "loginTest", "onComplete: wrong_password" );
                                editPass.setError( "Invalid password" );
                                editPass.requestFocus();
                            } catch (Exception e) {
                                Log.d( "loginTest extra", e.getMessage() );
                            }
                        }
                            else
                            {
                                SharedPreferences sp = getSharedPreferences( "local_data",0 );
                                sp=getSharedPreferences( "local_data",MODE_PRIVATE );
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putInt("key",1);
                                editor.apply();
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
                progressBar.setVisibility( View.VISIBLE );
                mAuth.createUserWithEmailAndPassword(Email, Pass)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                progressBar.setVisibility( View.INVISIBLE );
                                if (!task.isSuccessful()) {
                                    showToast( "Authentication failed." + task.getException() );
                                } else {
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(Name)
                                            .build();

                                    user.updateProfile(profileUpdates)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d("username", "User profile updated.");
                                                    }
                                                    else{
                                                        Log.d("username", "User profile update error.");
                                                    }
                                                }
                                            });
                                    showToast( "Registration Successful, Signing In...." );
                                    login( Email,Pass );
                                }
                            }
                        });}
            }
        } );

    }

    void showToast(String msg){
        Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
    }
}
