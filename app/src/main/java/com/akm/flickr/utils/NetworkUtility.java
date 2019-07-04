package com.akm.flickr.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class NetworkUtility {

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivitymanager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkinfo = connectivitymanager.getActiveNetworkInfo();
        if (networkinfo == null || !networkinfo.isConnectedOrConnecting()) {
            return false;
        } else {
            return true;
        }
    }

    public static final class Builder {

        private String BASE_URL = "";
        private long connectionTimeout;
        private long readTimeout;
        private long writeTimeout;
        private boolean shouldRetryOnConnectionFailure = false;
        private Retrofit mRetrofit;

        public Builder(String baseURL) {
            BASE_URL = baseURL;
        }

        /**
         * Sets the default connect timeout in Seconds for new connections.
         */
        public Builder withConnectionTimeout(long connectionTimeout) {
            this.connectionTimeout = connectionTimeout;
            return this;
        }

        /**
         * Sets the default read timeout in Minutes for new connections.
         */
        public Builder withReadTimeout(long readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }

        /**
         * Sets the default write timeout in Minutes for new connections.
         */
        public Builder withWriteTimeout(long writeTimeout) {
            this.writeTimeout = writeTimeout;
            return this;
        }

        /**
        * Configures the Retrofit client to retry or not when a connectivity problem is encountered.
        */
        public Builder withShouldRetryOnConnectionFailure(boolean shouldRetryOnConnectionFailure) {
            this.shouldRetryOnConnectionFailure = shouldRetryOnConnectionFailure;
            return this;
        }

        /**
         * Create the Retrofit instance using the configured values.
         */
        public NetworkUtility build() {
            setupForAPI();
            return new NetworkUtility();
        }

        public Retrofit getRetrofit() {
            return mRetrofit;
        }

        private void setupForAPI() {

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            builder.connectTimeout(connectionTimeout, TimeUnit.SECONDS)
                    .readTimeout(readTimeout, TimeUnit.MINUTES)
                    .writeTimeout(writeTimeout, TimeUnit.MINUTES)
                    .retryOnConnectionFailure(shouldRetryOnConnectionFailure)
                    .addInterceptor(httpLoggingInterceptor);

            Gson gson = new GsonBuilder().setLenient().create();
            OkHttpClient okHttpClient = builder.build();
            mRetrofit = new Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

        }

    }

}
