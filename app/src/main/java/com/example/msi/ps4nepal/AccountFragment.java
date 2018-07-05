package com.example.msi.ps4nepal;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.msi.ps4nepal.account.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AccountFragment extends Fragment {

    private static final String TAG="AccountFragment";

    //firebase
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    //widgets
    private Button mSignOut;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_account,container,false);
        mSignOut=(Button)view.findViewById(R.id.sign_out);

        setupFirebaseListener();

        mSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"onClick: attempting to sign outh the user");
                FirebaseAuth.getInstance().signOut();
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthStateListener);

    }

    @Override
    public void onStop() {
        super.onStop();
        if(mAuthStateListener!=null){
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthStateListener);
        }
    }

    //continuously listening for changes in user login state
    private void setupFirebaseListener() {
        Log.d(TAG,"setipFirebaseListener: setting up the auth state listener.");
        mAuthStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user=firebaseAuth.getCurrentUser();
                if(user!=null)
                {
                    Log.d(TAG,"onAuthStateChanged: signed_in" + user.getUid());
                }
                else {
                    Log.d(TAG,"onAuthStateChanged: signed_out");
                    Toast.makeText(getActivity(),"Signed out",Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(getActivity(), LoginActivity.class);

                    //flags will help to clear activity on stack .. if logged out and moved to login activity...... then pressing back button doesnt go back to sign out page it clears the stack of activity

                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        };
    }

}
