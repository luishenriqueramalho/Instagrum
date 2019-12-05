package com.example.instagrum.fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.instagrum.R;
import com.example.instagrum.activity.EditProfileActivity;
import com.example.instagrum.adapter.AdapterGrid;
import com.example.instagrum.helper.ConfigFirebase;
import com.example.instagrum.helper.UserFirebase;
import com.example.instagrum.model.Post;
import com.example.instagrum.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private TextView txtPost, txtFollowers, txtFollowing;
    private Button btnActionProfile;
    private CircleImageView imgProfile;
    private ProgressBar progressBar;
    private GridView gridViewProfile;
    private User userLogado;

    private DatabaseReference firebaseRef;
    private DatabaseReference userRef;
    private DatabaseReference userLogadoRef;
    private ValueEventListener valueEventListener;
    private DatabaseReference postUserRef;
    private AdapterGrid adapterGrid;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Config initial
        userLogado      = UserFirebase.getDadosUserLogado();
        firebaseRef     = ConfigFirebase.getRefFirebase();
        userRef         = firebaseRef.child("usuarios");

        // Config Ref post user
        postUserRef = ConfigFirebase.getRefFirebase().child("posts").child(userLogado.getId());

        // Config dos components
        initialComponents(view);

        // Rec user logado
        //Recuperar photo do user
        String photo = userLogado.getPhoto();
        if ( photo != null ) {
            Uri url = Uri.parse( photo );
            Glide.with(getActivity()).load( url ).into( imgProfile );
        }



        // Open edit profile
        btnActionProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(i);
            }
        });

        // Initial img loader
        initialImageLoader();

        // Carregar as fotos da postagem
        loadingPhotoPost();

        return view;
    }

    public void initialImageLoader() {

        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder( getActivity() )
                /* https://github.com/nostra13/Android-Universal-Image-Loader/wiki/Configuration
                Colocar o memoryCache e displayCache para carregamento mais rápido nas fotos
                */
                .build();
        ImageLoader.getInstance().init( config );

    }

    public void loadingPhotoPost() {

        postUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // Config size grid
                int sizeGrid = getResources().getDisplayMetrics().widthPixels;
                int sizeImg  = sizeGrid / 3;
                gridViewProfile.setColumnWidth( sizeImg );

                List<String> urlPhotos = new ArrayList<>();
                for ( DataSnapshot ds: dataSnapshot.getChildren() ) {
                    Post post = ds.getValue(Post.class);
                    urlPhotos.add( post.getPhoto() );
                    //Log.i("Post", "url: " +post.getPhoto());

                }

                int qtdPost = urlPhotos.size();
                txtPost.setText( String.valueOf( qtdPost ) ); // conta a qnt de itens no array

                // Config adapter
                adapterGrid = new AdapterGrid(getActivity(), R.layout.grid_post, urlPhotos);
                gridViewProfile.setAdapter( adapterGrid );

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void initialComponents(View view) {

        gridViewProfile     = view.findViewById(R.id.gridProfile);
        progressBar         = view.findViewById(R.id.progressBarProfile);
        imgProfile          = view.findViewById(R.id.imgProfilePost);
        txtPost             = view.findViewById(R.id.txtPost);
        txtFollowers        = view.findViewById(R.id.txtFollowers);
        txtFollowing        = view.findViewById(R.id.txtFollowing);
        btnActionProfile    = view.findViewById(R.id.btnActionProfile);

    }

    private void recDadosUserLogado() {

        userLogadoRef = userRef.child( userLogado.getId() );
        valueEventListener = userLogadoRef.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        User user = dataSnapshot.getValue( User.class );

                        //String posts     = String.valueOf( user.getPost() );
                        String followers = String.valueOf( user.getFollowers() );
                        String following = String.valueOf( user.getFollowing() );

                        // Config value rec
                        //txtPost.setText( posts );
                        txtFollowers.setText( followers );
                        txtFollowing.setText( following );

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );

    }

    @Override
    public void onStart() {
        super.onStart();
        recDadosUserLogado();
    }

    @Override
    public void onStop() {
        super.onStop();
        userLogadoRef.removeEventListener( valueEventListener );
    }
}