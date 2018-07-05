package com.example.msi.ps4nepal.account;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.msi.ps4nepal.MainActivity;
import com.example.msi.ps4nepal.SearchActivity;
import com.example.msi.ps4nepal.util.UniversalImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.nostra13.universalimageloader.core.ImageLoader;

import com.example.msi.ps4nepal.R;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity{



    private static final String TAG="LoginActivity";

    //Firebase auth
    private FirebaseAuth.AuthStateListener mAuthListener;

    // widgets
       private TextView mRegister;
        private EditText mEmail, mPassword;
        private Button mLogin;
        private ProgressBar mProgressBar;
        private ImageView mLogo;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mRegister = (TextView) findViewById(R.id.link_register);
        mEmail = (EditText) findViewById(R.id.input_email);
        mPassword = (EditText) findViewById(R.id.input_password);
        mLogin = (Button) findViewById(R.id.btn_login);
        mLogo = (ImageView) findViewById(R.id.logo);


        initImageLoader();
        initProgressBar();
        setupFirebaseAuth();
        init();

    }


    private void init(){
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isEmpty(mEmail.getText().toString())
                        && !isEmpty(mPassword.getText().toString())){
                    Log.d(TAG, "onClick: attempting to authenticate.");

                    showProgressBar();

                    FirebaseAuth.getInstance().signInWithEmailAndPassword(mEmail.getText().toString(),
                            mPassword.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    hideProgressBar();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(LoginActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                            hideProgressBar();
                        }
                    });
                }
                else{
                    Toast.makeText(LoginActivity.this, "You didn't fill in all the fields.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"on click: Navigating to Register Screen");
                Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
        UniversalImageLoader.setImage("https://instagram.fktm8-1.fna.fbcdn.net/vp/eb8e968c0bc076a86d4903a0b20c0212/5BBAC901/t51.2885-15/sh0.08/e35/p640x640/26277267_1727091520687750_7870451819581800448_n.jpg",mLogo);
        hideSoftKeyboard();
    }

    /**
     * Return true if the @param is null
     * @param string
     * @return
     */
    private boolean isEmpty(String string){
        return string.equals("");
    }


    private void showProgressBar(){
        mProgressBar.setVisibility(View.VISIBLE);

    }

    private void hideProgressBar(){
        if(mProgressBar.getVisibility() == View.VISIBLE){
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void initImageLoader(){
        UniversalImageLoader imageLoader = new UniversalImageLoader(LoginActivity.this);
        ImageLoader.getInstance().init(imageLoader.getConfig());
    }


    private void initProgressBar(){
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.INVISIBLE);
    }


    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }


        /*
        ----------------------------- Firebase setup ---------------------------------
     */


        private void setupFirebaseAuth(){
            Log.d(TAG,"setupFirebaseAuth: started");

            mAuthListener=new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user=firebaseAuth.getCurrentUser();
                    if(user!=null){

                        //check if email is verified
                        if(user.isEmailVerified()){
                            Log.d(TAG,"onAuthStateChanged: signed_in" + user.getUid());
                            Toast.makeText(LoginActivity.this,"Authenticated with: "+user.getUid(),Toast.LENGTH_SHORT).show();

                            Intent intent=new Intent(LoginActivity.this,SearchActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();

                        }
                        else {
                            Toast.makeText(LoginActivity.this,"Email is not verified Check your inbox",Toast.LENGTH_SHORT).show();
                            FirebaseAuth.getInstance().signOut();
                        }

                    }
                    else {
                        //User is signed out
                        Log.d(TAG,"onAuthStateChanged: signed_out");
                    }
                }
            };


        }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
        }
    }
}
