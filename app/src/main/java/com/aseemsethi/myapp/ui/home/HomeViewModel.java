package com.aseemsethi.myapp.ui.home;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class HomeViewModel extends ViewModel {
    final String TAG = "MyApp: HomeViewModel";

    private static MutableLiveData<String> loggedin = new MutableLiveData<>();
    /*
    public HomeViewModel() {
        loggedin = new MutableLiveData<>();
        loggedin.setValue("false");
    }
     */
    public LiveData<String> getLoggedin() {
        return loggedin;
    }
    public void setLoggedin(String val) {
        Log.d(TAG, "Home View Model: set val to:" + val);
        loggedin.setValue(val);}
}