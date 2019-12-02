package com.example.instagrum.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagrum.R;
import com.zomato.photofilters.utils.ThumbnailItem;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterThumbnails extends RecyclerView.Adapter<AdapterThumbnails.MyViewHolder> {

    private List<ThumbnailItem> listFilter;
    private Context context;

    public AdapterThumbnails(List<ThumbnailItem> listFilter, Context context) {
        this.listFilter = listFilter;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_filter, parent, false);
        return new AdapterThumbnails.MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        ThumbnailItem item = listFilter.get( position );

        holder.photo.setImageBitmap( item.image );
        holder.nameFilter.setText( item.filterName );

    }

    @Override
    public int getItemCount() {
        return listFilter.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView photo;
        TextView nameFilter;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            photo = itemView.findViewById(R.id.imgPhotoFilter);
            nameFilter = itemView.findViewById(R.id.txtNameFilter);

        }
    }

}
