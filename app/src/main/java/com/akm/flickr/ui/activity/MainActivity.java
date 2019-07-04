package com.akm.flickr.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.databinding.DataBindingUtil;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.akm.flickr.R;
import com.akm.flickr.adapter.DataBindingAdapter;
import com.akm.flickr.adapter.RecyclerViewArrayAdapter;
import com.akm.flickr.base.BaseActivity;
import com.akm.flickr.databinding.ActivityMainBinding;
import com.akm.flickr.model.FlickrPublicPhotosModel;
import com.akm.flickr.utils.NetworkUtility;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity implements RecyclerViewArrayAdapter.OnItemClickListener<FlickrPublicPhotosModel.Item> {

    private ActivityMainBinding binding;
    private Animator currentAnimator;
    private int shortAnimationDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        init();
    }

    private void init(){
        if(NetworkUtility.isNetworkConnected(this))
            getImages();
        else
            binding.tvMsg.setText((getString(R.string.no_internet)));

        shortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);
    }

    public void onClick(View view){
        if(NetworkUtility.isNetworkConnected(this))
            getImages();
        else
            binding.tvMsg.setText((getString(R.string.no_internet)));
    }

    public void getImages(){
        enableLoader();
        mApiService.getImages("nature", "json", "1").enqueue(new Callback<FlickrPublicPhotosModel>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<FlickrPublicPhotosModel> call, Response<FlickrPublicPhotosModel> response) {

                disableLoader();
                if(response.code() == 200) {
                    try {
                        List<FlickrPublicPhotosModel.Item> arrayList = response.body().getItems();
                        rView(arrayList);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    binding.tvMsg.setText("No data found. Click to reload");
                }
            }

            @Override
            public void onFailure(Call<FlickrPublicPhotosModel> call, Throwable t) {
                disableLoader();
                binding.tvMsg.setText("No data found. Click to reload");
            }
        });
    }

    private void rView(List arrayList) {

        RecyclerViewArrayAdapter adapter = new RecyclerViewArrayAdapter( arrayList, this);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        binding.rView.setLayoutManager(mLayoutManager);
        binding.rView.setItemAnimator(new DefaultItemAnimator());
        binding.rView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(View view, int position, FlickrPublicPhotosModel.Item object) {
        zoomImageFromThumb(view, object.getMedia().getM());

    }

    public void zoomImageFromThumb(final View thumbView, String url) {
        if (currentAnimator != null) {
            currentAnimator.cancel();
        }
        DataBindingAdapter.setSrc(binding.ivFullView, url);

        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        thumbView.getGlobalVisibleRect(startBounds);
        findViewById(R.id.container)
                .getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        float startScale;
        if ((float) finalBounds.width() / finalBounds.height() > (float) startBounds.width() / startBounds.height()) {
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        thumbView.setAlpha(0f);
        binding.ivFullView.setVisibility(View.VISIBLE);
        binding.ivFullView.setPivotX(0f);
        binding.ivFullView.setPivotY(0f);

        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(binding.ivFullView, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(binding.ivFullView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(binding.ivFullView, View.SCALE_X,
                        startScale, 1f))
                .with(ObjectAnimator.ofFloat(binding.ivFullView,
                        View.SCALE_Y, startScale, 1f));
        set.setDuration(shortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                currentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                currentAnimator = null;
            }
        });
        set.start();
        currentAnimator = set;
        final float startScaleFinal = startScale;
        binding.ivFullView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentAnimator != null) {
                    currentAnimator.cancel();
                }
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(binding.ivFullView, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(binding.ivFullView,
                                        View.Y,startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(binding.ivFullView,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(binding.ivFullView,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(shortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        binding.ivFullView.setVisibility(View.GONE);
                        currentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        binding.ivFullView.setVisibility(View.GONE);
                        currentAnimator = null;
                    }
                });
                set.start();
                currentAnimator = set;
            }
        });
    }
}
