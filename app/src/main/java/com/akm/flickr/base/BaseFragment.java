package com.akm.flickr.base;

import android.app.ProgressDialog;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

public class BaseFragment extends Fragment {

    /*
     * Display a short Toast on the current page
     * @param message : which is to be displayed
     */

    private ProgressDialog dialog;

    public void showToast(String message) {
        if (getActivity() != null) {
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }
    }

    /*
     * Print log in the Monitor window
     * @param tag
     * @param message : which is to be displayed
     */
    public void log(String tag, String message) {
        Log.d(tag, message);

    }

    public void replaceFragment(@IdRes final int container, final Fragment fragment) {
        final FrameLayout frameLayout = (FrameLayout) getActivity().findViewById(container);
        frameLayout.removeAllViewsInLayout();
        frameLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                getActivity().getSupportFragmentManager().beginTransaction().replace(frameLayout.getId(), fragment).addToBackStack(null).commitAllowingStateLoss();
                frameLayout.forceLayout();
            }
        }, 100);
    }

    protected void actionRetry() {

    }

    protected void acceptedNetworkFailure() {

    }


    public void enableLoader(){
        dialog = ProgressDialog.show(getActivity(), "", "Please Wait...");
    }

    public void disableLoader(){
        if(dialog !=null)
            dialog.dismiss();
    }

}