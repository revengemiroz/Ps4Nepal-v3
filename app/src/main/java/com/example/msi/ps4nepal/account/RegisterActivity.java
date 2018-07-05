package com.example.msi.ps4nepal.account;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.msi.ps4nepal.R;
import com.example.msi.ps4nepal.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private Context mContext = RegisterActivity.this;

    //widget
    private EditText mEmail,mName,mPassword,mConfirmPassword;
    private Button mRegister;
    private ProgressBar mProgressBar;

    //vars
    private String email,name,password,confirmpassword;
    private User mUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        mRegister = (Button) findViewById(R.id.btn_register);
        mEmail = (EditText) findViewById(R.id.input_email);
        mPassword = (EditText) findViewById(R.id.input_password);
        mConfirmPassword = (EditText) findViewById(R.id.input_confirm_password);
        mName = (EditText) findViewById(R.id.input_name);

        mUser=new User();
        Log.d(TAG,"onCreate: started");

        initProgressBar();
        setupFirebaseAuth();
        init();
        hideSoftKeyboard();
    }


    private void init(){
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email=mEmail.getText().toString();
                name=mName.getText().toString();
                password=mPassword.getText().toString();
                confirmpassword=mConfirmPassword.getText().toString();

                if(checkInputs(email,name,password,confirmpassword)) {
                    if (doStringsMatch(password, confirmpassword)) {
                        registerNewEmail(email, password);
                    } else {
                        Toast.makeText(mContext, "password do not match", Toast.LENGTH_SHORT).show();
                    }
                }
                    else {
                        Toast.makeText(mContext,"all fields must be filled ",Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    /**
     * Return true if @param 's1' matches @param 's2'
     * @param s1
     * @param s2
     * @return
     */
    private boolean doStringsMatch(String s1, String s2){
        return s1.equals(s2);
    }

    /**
     * Checks all the input fields for null
     * @param email
     * @param username
     * @param password
     * @return
     */


    private boolean checkInputs(String email,String username,String password,String confirmPassword){
        Log.d(TAG,"checkInput:  checking input for null value");

            if (email.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(mContext, "all input must be filled", Toast.LENGTH_SHORT).show();
                return false;

            }

        return true;
    }


    private void showProgressBar(){
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar(){
        mProgressBar.setVisibility(View.GONE);
    }

    private void initProgressBar(){

        mProgressBar=(ProgressBar)findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    //firebase//
    private FirebaseAuth.AuthStateListener mAuthListener;


     /*
    ---------------------------Firebase-----------------------------------------
     */


     private void setupFirebaseAuth(){
         mAuthListener=new FirebaseAuth.AuthStateListener() {
             @Override
             public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                //get status whether the user is logged in or not--> authenticated or not//
                 final FirebaseUser user=firebaseAuth.getCurrentUser();

                 if(user!=null)
                 {
                     //user is authenticated and logged in
                     Log.d(TAG,"onAuthStateChanged: signed_in "+user.getUid());
                 }
                  else
                      {
                          //User is signed_out
                          Log.d(TAG,"onAuthStateChanged: signed_out");
                      }

             }
         };
     }

    /**
     * Adds data to the node: "users"
     */
     public void addNewUser(){
            //adding the user details in database

         //add data to the "users" node in firebase database

            String user_id=FirebaseAuth.getInstance().getCurrentUser().getUid();
            Log.d(TAG,"addNewUser: Adding new User: \n user_id:"+user_id);
            mUser.setName(name);
            mUser.setUser_id(user_id);

         DatabaseReference reference= FirebaseDatabase.getInstance().getReference();

         //insert into the node:"users"
         reference.child("users")
                 .child(user_id)
                 .setValue(mUser);
         FirebaseAuth.getInstance().signOut();
         redirectLoginScreen();
     }

    /**
     * Redirects the user to the login screen
     */

    private void redirectLoginScreen() {
        Log.d(TAG,"redirectLoginScreen: redirecting to login screen after registering");
        Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Register a new email and password to Firebase Authentication
     * @param email
     * @param password
     */

     public void registerNewEmail(String email,String password){
         showProgressBar();

         FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
                 .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                     @Override
                     public void onComplete(@NonNull Task<AuthResult> task) {
                         Log.d(TAG,"registerNewEmail: onComplete :" + task.isSuccessful());

                         if (task.isSuccessful()){

                             //send email verificaiton
                             sendVerificationEmail();
                             //add user details to firebase database
                             addNewUser();

                         }
                         if(!task.isSuccessful()) {
                             Toast.makeText(mContext, "Someone with that email already exists",
                                     Toast.LENGTH_SHORT).show();
                             hideProgressBar();

                         }
                         hideProgressBar();
                         // ...
                     }
                 });
     }



    /**
     * sends an email verification link to the user
     */
     public void sendVerificationEmail(){
         FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
         if (user!=null)
         {
             user.sendEmailVerification()
                     .addOnCompleteListener(new OnCompleteListener<Void>() {
                         @Override
                         public void onComplete(@NonNull Task<Void> task) {
                             if(task.isSuccessful())
                             {
                                Toast.makeText(mContext,"Verification email sent.",Toast.LENGTH_SHORT).show();
                             }
                             else
                             {
                                 Toast.makeText(mContext,"couldn't send the verification email",Toast.LENGTH_SHORT).show();
                                 hideProgressBar();
                             }
                         }
                     });
         }

     }




    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);
    }


    @Override
    protected void onStop() {
        super.onStop();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);
    }
}
