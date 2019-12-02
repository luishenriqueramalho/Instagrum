package com.example.instagrum.helper;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.instagrum.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class UserFirebase {

    public static FirebaseUser getUserAtual() {

        FirebaseAuth user = ConfigFirebase.getFirebaseAuth();
        return user.getCurrentUser();

    }

    public static String getIdentifyUser() {
        return getUserAtual().getUid();
    }

    public static void attNameUser(String name) {

        try {

            // Recuperar uuário logado no app
            FirebaseUser userlogado = getUserAtual();

            // Config obj para alterar profile
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder().setDisplayName( name ).build();
            userlogado.updateProfile( profile ).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if ( !task.isSuccessful() ) {
                        Log.d("Perfil", "Erro ao atualizar o nome de perfil");
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void attPhotoUser(Uri url) {

        try {

            // Recuperar uuário logado no app
            FirebaseUser userlogado = getUserAtual();

            // Config obj para alterar profile
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder().setPhotoUri( url ).build();
            userlogado.updateProfile( profile ).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if ( !task.isSuccessful() ) {
                        Log.d("Perfil", "Erro ao atualizar a foto de perfil");
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static User getDadosUserLogado() {

        FirebaseUser firebaseUser = getUserAtual();

        User user = new User();
        user.setEmail( firebaseUser.getEmail() );
        user.setName( firebaseUser.getDisplayName() );
        user.setId( firebaseUser.getUid() );

        if ( firebaseUser.getPhotoUrl() == null ) {
            user.setPhoto("");
        } else {
            user.setPhoto( firebaseUser.getPhotoUrl().toString() );
        }

        return user;

    }

}
