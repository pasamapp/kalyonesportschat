package com.pasamapp.kalyonesportschat.VideoRecyclerView.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import androidx.appcompat.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.pasamapp.kalyonesportschat.R;


/**
 * Created by Pasam on 18.04.2020.
 */

public class CameraAnimation extends AppCompatImageView {
    AnimationDrawable frameAnimation;

    public CameraAnimation(Context context) {
        super(context);
        initView(context);
    }

    public CameraAnimation(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public CameraAnimation(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        setImageResource(R.drawable.fram_camera);
        frameAnimation = (AnimationDrawable) getDrawable();
    }

    public void start() {
        frameAnimation.start();
    }

    public void stop() {
        frameAnimation.stop();
    }
}
