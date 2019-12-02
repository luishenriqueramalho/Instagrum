package com.example.instagrum.adapter;

import android.content.Context;
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

        ViewHolder viewHolder;
        if  ( convertView != null ) {

            viewHolder = new ViewHolder();
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(layoutResource, parent, false);
            viewHolder.image = convertView.findViewById(R.id.imageViewGrid);
            viewHolder.progressBar = convertView.findViewById(R.id.progressBarGrid);

            convertView.setTag( viewHolder );

        } else {

            viewHolder = (ViewHolder) convertView.getTag();

        }

        return convertView;
    }
}
