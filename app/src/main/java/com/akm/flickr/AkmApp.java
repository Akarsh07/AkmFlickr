package com.akm.flickr;

import android.app.Application;
import android.content.Context;

import com.akm.flickr.api.ApiService;
import com.akm.flickr.utils.NetworkUtility;


public class AkmApp extends Application {

    private Context mContext;
    private AkmApp mInstance;
    private String TAG = AkmApp.class.getName();
    private ApiService mApiService;
    private String BASE_URL = "https://www.flickr.com/";

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        mInstance = this;
        basicSetupForAPI();
    }



    public ApiService getApiService() {
        return mApiService;
    }

    public Context getAppContext() {
        return mContext;
    }

    public AkmApp getInstance() {
        return mInstance;
    }

    private void basicSetupForAPI() {

        NetworkUtility.Builder networkBuilder = new NetworkUtility.Builder(BASE_URL);
        networkBuilder.withConnectionTimeout(60)
                .withReadTimeout(10)
                .withShouldRetryOnConnectionFailure(true)
                .build();
        mApiService = networkBuilder.getRetrofit().create(ApiService.class);

    }
}
