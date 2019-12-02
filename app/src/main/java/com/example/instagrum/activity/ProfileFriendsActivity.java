package com.example.instagrum.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.instagrum.R;
import com.example.instagrum.adapter.AdapterGrid;
import com.example.instagrum.helper.ConfigFirebase;
import com.example.instagrum.helper.UserFirebase;
import com.example.instagrum.model.Post;
import com.example.instagrum.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFriendsActivity extends AppCompatActivity {

    private User userSelect;
    private User userLogado;
    private Button btnActionProfile;
    private CircleImageView imgEditProfile;
    private TextView txtPost, txtFollowers, txtFollowing;
    private GridView gridProfile;
    private AdapterGrid adapterGrid;

    private DatabaseReference firebaseRef;
    private DatabaseReference userRef;
    private DatabaseReference userFriendRef;
    private DatabaseReference userLogadoRef;
    private DatabaseReference followRef;
    private ValueEventListener valueEventListener;
    private DatabaseReference postUserRef;

    private String idUserLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_friends);

        // Config Initial
        firebaseRef     = ConfigFirebase.getRefFirebase();
        userRef         = firebaseRef.child("usuarios");
        followRef       = firebaseRef.child("seguidores");
        idUserLogado    = UserFirebase.getIdentifyUser();

        // Initial Components
        initialComponents();

        // Configurar a toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Perfil");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);

        // Recuperar user select
        Bundle bundle = getIntent().getExtras();
        if ( bundle != null ) {
            userSelect = (User) bundle.getSerializable("userSelect");

            // Config Ref post user
            postUserRef = ConfigFirebase.getRefFirebase().child("posts").child(userSelect.getId());

            // Config name user toolbar
            getSupportActionBar().setTitle( userSelect.getName() );

            //Recuperar photo do user
            String photo = userSelect.getPhoto();
            if ( photo != null ) {
                Uri url = Uri.parse( photo );
                Glide.with(ProfileFriendsActivity.this).load( url ).into( imgEditProfile );
            }

        }

        // Carregar as fotos da postagem
        loadingPhotoPost();

    }

    public void loadingPhotoPost() {

        postUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                List<String> urlPhotos = new ArrayList<>();
                for ( DataSnapshot ds: dataSnapshot.getChildren() ) {
                    Post post = ds.getValue(Post.class);
                    urlPhotos.add( post.getPhoto() );
                    //Log.i("Post", "url: " +post.getPhoto());

                }

                int qtdPost = urlPhotos.size();
                txtPost.setText( String.valueOf( qtdPost ) );

                // Config adapter
                adapterGrid = new AdapterGrid(getApplicationContext(), R.layout.grid_post, urlPhotos);
                gridProfile.setAdapter( adapterGrid );

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void recDadosUserLogado() {

        userLogadoRef = userRef.child( idUserLogado );
        userLogadoRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        userLogado = dataSnapshot.getValue( User.class );

                        // Verificar se o user já segue
                        verifyFollowFriend();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );

    }

    // Verificar se o usuário logado está seguindo a pessoa
    private void verifyFollowFriend() {

        DatabaseReference segueRef = followRef.child( idUserLogado ).child( userSelect.getId() );
        segueRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if ( dataSnapshot.exists() ) {
                            //Ja está seguindo
                            habilityButtonFollow( true );

                        } else {
                            // Ainda não está seguindo
                            habilityButtonFollow( false );
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );

    }

    private void habilityButtonFollow( boolean followUser ) {

        if ( followUser ){
            btnActionProfile.setText("Seguindo");
        } else {

            btnActionProfile.setText("Seguir");

            // Add event follow user
            btnActionProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // Save follow
                    saveFollow(userLogado, userSelect);
                }
            });
        }

    }

    private void saveFollow(User uLogado, User uFriend) {

        HashMap<String, Object> dadosFriend = new HashMap<>();
        dadosFriend.put("name", uFriend.getName());
        dadosFriend.put("photo", uFriend.getPhoto());
        DatabaseReference seguidorRef = followRef.child( uLogado.getId() ).child( uFriend.getId() );
        seguidorRef.setValue( dadosFriend );

        btnActionProfile.setText("Seguindo");
        btnActionProfile.setOnClickListener(null);

        // Increment follow user logado
        int seguindo = uLogado.getFollowing() +1;
        HashMap<String, Object> dadosSeguindo = new HashMap<>();
        dadosSeguindo.put("following", seguindo);
        DatabaseReference userSeguindo = userRef.child( uLogado.getId() );
        userSeguindo.updateChildren( dadosSeguindo );

        // Increment followers user friend
        int seguidores = uFriend.getFollowers() +1;
        HashMap<String, Object> dadosSeguidores = new HashMap<>();
        dadosSeguidores.put("followers", seguidores);
        DatabaseReference userSeguidores = userRef.child( uFriend.getId() );
        userSeguidores.updateChildren( dadosSeguidores );

    }

    @Override
    protected void onStart() {
        super.onStart();
        recDadosProfileFriend();

        recDadosUserLogado();
    }

    @Override
    protected void onStop() {
        super.onStop();
        userFriendRef.removeEventListener( valueEventListener );
    }

    private void recDadosProfileFriend() {

        userFriendRef = userRef.child( userSelect.getId() );
        valueEventListener = userFriendRef.addValueEventListener(
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

    private void initialComponents() {

        imgEditProfile   = findViewById(R.id.imgEditProfile);
        gridProfile      = findViewById(R.id.gridProfile);
        btnActionProfile = findViewById(R.id.btnActionProfile);
        txtPost          = findViewById(R.id.txtPost);
        txtFollowers     = findViewById(R.id.txtFollowers);
        txtFollowing     = findViewById(R.id.txtFollowing);
        btnActionProfile.setText("Carregando");

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}
