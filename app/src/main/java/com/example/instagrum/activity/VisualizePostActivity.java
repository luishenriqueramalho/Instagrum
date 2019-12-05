package com.example.instagrum.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.instagrum.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class VisualizePostActivity extends AppCompatActivity {

    private TextView txtProfilePost, txtProfilePostLike, txtProfilePostDescription, ProfilePostComent;
    private ImageView imgProfilePost;
    private CircleImageView imageView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualize_post);

        // Initial Componets
        initialComponents();

    }

    private void initialComponents() {

        txtProfilePost              = findViewById(R.id.txtProfilePost);
        txtProfilePostLike          = findViewById(R.id.txtProfilePostLike);
        txtProfilePostDescription   = findViewById(R.id.txtProfilePostDescription);
        ProfilePostComent           = findViewById(R.id.ProfilePostComent);
        imgProfilePost              = findViewById(R.id.imgProfilePost);

    }

}
