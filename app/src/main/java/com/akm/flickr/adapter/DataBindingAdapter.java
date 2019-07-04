package com.akm.flickr.adapter;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;


public class DataBindingAdapter {

    @BindingAdapter("app:src")
    public static void setSrc(ImageView imageView, String imageURL) {

        if(imageURL != null){
            Glide.with(imageView.getContext()).load(imageURL).into(imageView);
        }
    }
}
