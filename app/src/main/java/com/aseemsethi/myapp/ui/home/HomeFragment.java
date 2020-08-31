package com.aseemsethi.myapp.ui.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.aseemsethi.myapp.MainActivity;
import com.aseemsethi.myapp.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;
import java.util.concurrent.Executor;

public class HomeFragment extends Fragment implements
        View.OnClickListener {

    private HomeViewModel homeViewModel;
    private GoogleSignInClient mGoogleSignInClient;
    private TextView mStatusTextView;
    final String TAG = "MyApp: Home";
    private static final int RC_SIGN_IN = 9001;
    View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        root = inflater.inflate(R.layout.fragment_home, container, false);
        /*
        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
         */
        FirebaseApp.initializeApp(getActivity().getApplicationContext());
        mStatusTextView = root.findViewById(R.id.status);
        // Button listeners
        root.findViewById(R.id.sign_in_button).setOnClickListener(this);
        root.findViewById(R.id.sign_out_button).setOnClickListener(this);
        root.findViewById(R.id.disconnect_button).setOnClickListener(this);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity().getApplicationContext(), gso);
        return root;
    }
    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart called");
        // [START on_start_sign_in]
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getActivity().getApplicationContext());
        updateUI(account);
        // [END on_start_sign_in]
    }

    private void updateDB(GoogleSignInAccount account) {
        Log.d(TAG, "Updating DB");
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        myRef.child("users").child("name").setValue(account.getDisplayName());
        myRef.child("users").child("email").setValue(account.getEmail());
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                Log.d(TAG, "Value is: " + map);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.d(TAG, "Failed to read value.", error.toException());
            }
        });
        /*
        myRef.child("users").setValue(account.getDisplayName()).
                addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){
                    //Task was successful, data written!
                    Toast.makeText(getActivity().getApplicationContext(), "Data saved!",
                            Toast.LENGTH_SHORT).show();
                }else{
                    //Task was not successful,
                    Toast.makeText(getActivity().getApplicationContext(), "Something went wrong",
                            Toast.LENGTH_SHORT).show();
                    //Log the error message
                    Log.e(TAG, "onComplete: ERROR: " + task.getException().getLocalizedMessage() );
                }
            }
        });
         */
    }

    private void updateUI(@Nullable GoogleSignInAccount account) {
        if (account != null) {
            homeViewModel.setLoggedin("true");
            mStatusTextView.setText(getString(R.string.signed_in_fmt, account.getDisplayName()));
            Log.d(TAG, "Signed in: " + account.getDisplayName());
            root.findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            root.findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
            updateDB(account);
        } else {
            homeViewModel.setLoggedin("false");
            mStatusTextView.setText(R.string.signed_out);
            Log.d(TAG, "Signed out");
            root.findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            root.findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
        }
    }

    // [START onActivityResult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
    // [END onActivityResult]

    // [START handleSignInResult]
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }
    // [END handleSignInResult]

    // [START signIn]
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signIn]

    // [START signOut]
    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(getActivity(),
                        new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        updateUI(null);
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END signOut]

    // [START revokeAccess]
    private void revokeAccess() {
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(getActivity(),
                        new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        updateUI(null);
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END revokeAccess]
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                Log.d(TAG, "signin");
                signIn();
                break;
            case R.id.sign_out_button:
                Log.d(TAG, "signout");
                signOut();
                break;
            case R.id.disconnect_button:
                Log.d(TAG, "disconnect");
                revokeAccess();
                break;
        }
    }
}