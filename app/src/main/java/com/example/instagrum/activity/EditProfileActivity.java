package com.example.instagrum.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.instagrum.R;
import com.example.instagrum.helper.ConfigFirebase;
import com.example.instagrum.helper.Permissao;
import com.example.instagrum.helper.UserFirebase;
import com.example.instagrum.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    private CircleImageView imgProfile;
    private TextView txtAlterarPhoto;
    private TextInputEditText txtEditNameProfile, txtEditEmailProfile;
    private Button btnSaveProfile;
    private User userlogado;
    private static final int SELECT_GALERY = 200;
    private StorageReference storageRef;
    private String identifyUser;

    private String[] permissionNecessarias = new String[] {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Validar permission
        Permissao.validarPermissoes(permissionNecessarias, this, 1);

        // Config initial
        userlogado = UserFirebase.getDadosUserLogado();
        storageRef = ConfigFirebase.getFirebaseStorage();
        identifyUser = UserFirebase.getIdentifyUser();

        // Configurar a toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Editar Perfil");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);

        // Iniciar Componentes
        initialComponents();

        // Recuperar dados dos users
        FirebaseUser userProfile = UserFirebase.getUserAtual();
        txtEditNameProfile.setText( userProfile.getDisplayName().toUpperCase() );
        txtEditEmailProfile.setText( userProfile.getEmail() );

        Uri url = userProfile.getPhotoUrl();
        if ( url != null ) {
            Glide.with(EditProfileActivity.this).load( url ).into( imgProfile );
        } else {
            imgProfile.setImageResource(R.drawable.avatar);
        }

        // Save alterações
        btnSaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String nameAtt = txtEditNameProfile.getText().toString();

                // Att nome no profile
                UserFirebase.attNameUser( nameAtt );

                // Att name no database
                userlogado.setName( nameAtt );
                userlogado.att();

                Toast.makeText(EditProfileActivity.this, "Dados alterados com sucesso!", Toast.LENGTH_SHORT).show();

            }
        });

        // Alterar Photo
        txtAlterarPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent( Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if ( i.resolveActivity(getPackageManager()) != null ) {
                    startActivityForResult( i, SELECT_GALERY );
                }

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ( resultCode == RESULT_OK ) {
            Bitmap image = null;

            try {

                // Select img galery
                switch ( requestCode ) {
                    case SELECT_GALERY :
                        Uri localImgSelect = data.getData();
                        image = MediaStore.Images.Media.getBitmap(getContentResolver(), localImgSelect);

                        break;
                }

                // Case select img
                if ( image != null ) {

                    // Config img na screen
                    imgProfile.setImageBitmap( image );

                    // Recuperar dados da img
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImage = baos.toByteArray();

                    // Save img no firebase
                    final StorageReference imgRef = storageRef.child("images").child("profile").child( identifyUser + ".jpeg" );
                    UploadTask uploadTask = imgRef.putBytes( dadosImage );
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(EditProfileActivity.this, "Erro ao fazer upload da imagem!", Toast.LENGTH_SHORT).show();

                        }
                    }).addOnSuccessListener(EditProfileActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    attPhotoUser( uri );
                                    Log.i("URI FIREBASE", uri.toString());

                                    Toast.makeText(EditProfileActivity.this, "Sucesso ao fazer upload da imagem!", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    });

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void attPhotoUser(Uri url) {
        // Att photo profile
        UserFirebase.attPhotoUser( url );

        // Att photo firebase
        userlogado.setPhoto( url.toString() );
        userlogado.att();

        Toast.makeText(EditProfileActivity.this, "Sua foto foi atualizada com sucesso!", Toast.LENGTH_SHORT).show();

    }

    public void initialComponents() {

        imgProfile      = findViewById(R.id.imgProfilePost);
        txtAlterarPhoto     = findViewById(R.id.txtEditAlterar);
        txtEditNameProfile  = findViewById(R.id.txtEditName);
        txtEditEmailProfile = findViewById(R.id.txtEditEmail);
        btnSaveProfile      = findViewById(R.id.btnSave);
        txtEditEmailProfile.setFocusable(false);

    }

    @Override
    public boolean onSupportNavigateUp() {

        finish();
        return false;

    }
}
