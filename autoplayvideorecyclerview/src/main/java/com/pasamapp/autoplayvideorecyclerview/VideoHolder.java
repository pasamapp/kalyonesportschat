package com.pasamapp.autoplayvideorecyclerview;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

/**
 * Created by Pasam on 18.04.2020.
 */

public abstract class VideoHolder extends RecyclerView.ViewHolder {

    public VideoHolder(View itemView) {
        super(itemView);
    }

    public abstract View getVideoLayout();

    public abstract void playVideo();

    public abstract void stopVideo();

}