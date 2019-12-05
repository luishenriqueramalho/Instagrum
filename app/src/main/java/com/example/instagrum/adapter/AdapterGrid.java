package com.example.instagrum.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagrum.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

public class AdapterGrid extends ArrayAdapter<String> {

    private Context context;
    private int layoutResource;
    private List<String> urlPhotos;

    public AdapterGrid(@NonNull Context context, int resource, @NonNull List<String> objects) {
        super(context, resource, objects);

        this.context        = context;
        this.layoutResource = resource;
        this.urlPhotos      = objects;

    }

    public class ViewHolder {
        ImageView image;
        ProgressBar progressBar;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final ViewHolder viewHolder;
        if  ( convertView == null ) {

            viewHolder = new ViewHolder();
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(layoutResource, parent, false);
            viewHolder.image = convertView.findViewById(R.id.imageViewGrid);
            viewHolder.progressBar = convertView.findViewById(R.id.progressBarGrid);

            convertView.setTag( viewHolder );

        } else {

            viewHolder = (ViewHolder) convertView.getTag();

        }

        // Rec dados img
        String urlImage = getItem( position );
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(urlImage, viewHolder.image, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {
                viewHolder.progressBar.setVisibility( View.VISIBLE );
            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
                viewHolder.progressBar.setVisibility( View.GONE );
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                viewHolder.progressBar.setVisibility( View.GONE );
            }

            @Override
            public void onLoadingCancelled(String s, View view) {
                viewHolder.progressBar.setVisibility( View.GONE );
            }
        });

        return convertView;
    }
}
