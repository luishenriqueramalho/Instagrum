package com.example.instagrum.fragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.instagrum.R;
import com.example.instagrum.activity.EditProfileActivity;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private TextView txtPost, txtFollowers, txtFollowing;
    private Button btnActionProfile;
    private CircleImageView imgProfile;
    private ProgressBar progressBar;
    private GridView gridProfile;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Config dos components
        gridProfile     = view.findViewById(R.id.gridProfile);
        progressBar     = view.findViewById(R.id.progressBarProfile);
        imgProfile      = view.findViewById(R.id.imgEditProfile);
        txtPost         = view.findViewById(R.id.txtPost);
        txtFollowers    = view.findViewById(R.id.txtFollowers);
        txtFollowing    = view.findViewById(R.id.txtFollowing);
        btnActionProfile       = view.findViewById(R.id.btnActionProfile);

        // Open edit profile
        btnActionProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(i);
            }
        });

        return view;
    }

}
