package com.example.prashantgajera.tallyerpsystem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;


public class Login_screen extends AppCompatActivity {
    private static final int RC_SIGN_IN = 234;
    FirebaseAuth firebaseauth;
    Button signinButton;
    ACProgressFlower acdialog;
    ProgressDialog dialog;
    public AlertDialog alertDialog;
    GoogleSignInClient signInClient;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    public String email=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        getSupportActionBar().hide();
        firebaseauth = FirebaseAuth.getInstance();
        sharedPreferences =getApplication().getSharedPreferences("mypref",MODE_PRIVATE);
        editor = sharedPreferences.edit();

        GoogleSignInOptions signinOptions  = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        signInClient = GoogleSignIn.getClient(this,signinOptions);
        signinButton = findViewById(R.id.signin);

        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mySignin();
            }
        });
    }

    @Override
    protected  void onStart()
    {

        super.onStart();

        if(firebaseauth.getCurrentUser()!= null)
        {

            editor.putBoolean("login",false);
            editor.apply();
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }
        else  if(firebaseauth.getCurrentUser()==null)
        {
            editor.putBoolean("login",true);
            editor.apply();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        acdialog.dismiss();

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                //Toast.makeText(Login_screen.this,"this"+ e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("login", "firebaseAuthWithGoogle:" + acct.getId());

        acdialog = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.BLUE)
                .text("Please Wait..")
                .bgAlpha(1)
                .speed(15)
                .textAlpha(1)
                .textColor(Color.BLACK)
                .bgColor(Color.WHITE)
                .fadeColor(Color.WHITE).build();
        acdialog.show();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        firebaseauth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            acdialog.dismiss();
                            Log.d("login", "signInWithCredential:success");
                            FirebaseUser user = firebaseauth.getCurrentUser();
                            email = user.getEmail();

                            sharedPreferences = getSharedPreferences("mypref",MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("Emailid",email);
                            editor.commit();

                            Intent intent = new Intent(Login_screen.this,MainActivity.class);
                            startActivity(intent);
                            Toast.makeText(Login_screen.this, "User Signed In", Toast.LENGTH_SHORT).show();

                        } else {
                            Log.w("login", "signInWithCredential:failure", task.getException());
                            Toast.makeText(Login_screen.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    @SuppressWarnings("deprecation")

    public void mySignin()
    {
        acdialog = new ACProgressFlower.Builder(this)
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.BLUE)
                .text("Please Wait..")
                .bgAlpha(1)
                .speed(15)
                .textAlpha(1)
                .textColor(Color.BLACK)
                .bgColor(Color.WHITE)
                .fadeColor(Color.WHITE).build();
        acdialog.show();

        Intent signinIntent = signInClient.getSignInIntent();
        startActivityForResult(signinIntent, RC_SIGN_IN);
    }


}
