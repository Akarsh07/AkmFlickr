package com.akm.flickr.api;


import com.akm.flickr.model.FlickrPublicPhotosModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;


public interface ApiService {

    @GET("services/feeds/photos_public.gne")
    @Headers("Content-Type: application/json")
    Call<FlickrPublicPhotosModel> getImages(@Query("tags") String tag,@Query("format") String format, @Query("nojsoncallback") String cb);

}
