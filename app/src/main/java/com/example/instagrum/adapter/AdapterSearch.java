package com.example.instagrum.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagrum.R;
import com.example.instagrum.model.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterSearch extends RecyclerView.Adapter<AdapterSearch.MyViewHolder> {

    private List<User> userList;
    private Context context;

    public AdapterSearch(List<User> l, Context c) {
        this.userList = l;
        this.context = c;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_search_user, parent, false);
        return new MyViewHolder(itemLista);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        User user = userList.get(position);

        holder.name.setText( user.getName() );

        if ( user.getPhoto() != null ) {
            Uri uri = Uri.parse( user.getPhoto() );
            Glide.with(context).load(uri).into(holder.photo);
        } else {
            holder.photo.setImageResource(R.drawable.avatar);
        }

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

       CircleImageView photo;
       TextView name;


       public MyViewHolder(@NonNull View itemView) {
           super(itemView);

           photo = itemView.findViewById(R.id.imageViewSearch);
           name  = itemView.findViewById(R.id.txtViewSearch);

       }
   }

}
