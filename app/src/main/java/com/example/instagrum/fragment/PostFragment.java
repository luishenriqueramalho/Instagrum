package com.example.instagrum.fragment;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.instagrum.R;
import com.example.instagrum.activity.FiltroActivity;
import com.example.instagrum.helper.Permissao;

import java.io.ByteArrayOutputStream;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostFragment extends Fragment {

    private Button openGalery, openCamera;
    private static final int SELECT_CAMERA = 100;
    private static final int SELECT_GALERY = 200;

    private String[] permissionNecessarias = new String[] {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    public PostFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post, container, false);

        // Validar permission
        Permissao.validarPermissoes(permissionNecessarias, getActivity(), 1);

        //Initial components
        openGalery = view.findViewById(R.id.btnOpenGalery);
        openCamera = view.findViewById(R.id.btnOpenCamera);

        // Abrir câmera
        openCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if ( i.resolveActivity( getActivity().getPackageManager() ) != null ) {
                    startActivityForResult( i, SELECT_CAMERA );
                }
            }
        });

        // Abrir Galeria
        openGalery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if ( i.resolveActivity( getActivity().getPackageManager() ) != null ) {
                    startActivityForResult( i, SELECT_GALERY );
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ( resultCode == getActivity().RESULT_OK ) {

            Bitmap img = null;

            try {

                // Validar tipo de select
                switch ( requestCode ) {

                    case SELECT_CAMERA:
                        img = ( Bitmap ) data.getExtras().get("data");
                        break;
                    case SELECT_GALERY :
                        Uri localImgSelect = data.getData();
                        img = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), localImgSelect);
                        break;

                }

                // Validar img select
                if ( img != null ) {

                    // Convert img byte array
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    img.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImg = baos.toByteArray();

                    // Enviar para app de filtro
                    Intent i = new Intent(getActivity(), FiltroActivity.class);
                    i.putExtra("photoSelect", dadosImg);
                    startActivity(i);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }
}
