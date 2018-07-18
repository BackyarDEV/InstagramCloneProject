package com.backyardev.instagramcloneproject.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.backyardev.instagramcloneproject.R;
import com.backyardev.instagramcloneproject.app.AppController;
import com.backyardev.instagramcloneproject.model.ImagePosts;

import java.util.List;


public class CustomListAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<ImagePosts> postsList;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public CustomListAdapter(Activity activity, List<ImagePosts> postsList) {
        this.activity=activity;
        this.postsList=postsList;

    }

    @Override
    public int getCount() {
        return postsList.size();
    }

    @Override
    public Object getItem(int location) {
        return location;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService( Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate( R.layout.list_row, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        NetworkImageView thumbNail = (NetworkImageView) convertView
                .findViewById(R.id.listImage);

        ImagePosts m = postsList.get(position);

        thumbNail.setImageUrl(m.getThumbnailUrl(), imageLoader);


        return convertView;
    }
}
