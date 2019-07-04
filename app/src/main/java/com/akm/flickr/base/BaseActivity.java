package com.akm.flickr.base;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.akm.flickr.AkmApp;
import com.akm.flickr.api.ApiService;


public class BaseActivity extends AppCompatActivity {

    public ApiService mApiService;
    private ProgressDialog dialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mApiService = ((AkmApp) getApplication()).getApiService();

    }

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /*
    * Print log in the Monitor window
    * @param tag
    * @param message : which is to be displayed
    */
    public void log(String tag, String message) {
        Log.d(tag, message);
    }

    /*
     * Adds/replaces a fragment within an activity
     * @param container : resource id of the container view within which the fragment is required to be added/replaced
     * @param fragment : which is to be added/replaced
     * @param arguments : bundled arguments required to be provided during fragment setup
     * */
    public void replaceFragment(@IdRes int container, Fragment fragment, Bundle arguments) {
        if (arguments != null) {
            fragment.setArguments(arguments);
        }
        getSupportFragmentManager().beginTransaction().replace(container, fragment).commitAllowingStateLoss();
    }

    protected void actionRetry() {

    }

    protected void acceptedNetworkFailure() {

    }

    public void enableLoader(){
        dialog = ProgressDialog.show(this, "", "Please Wait...");
    }

    public void disableLoader(){
        if(dialog !=null)
            dialog.dismiss();
    }

}
