package com.example.instagrum.helper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ConfigFirebase {

    private static DatabaseReference refFirebase;
    private static FirebaseAuth refAuth;
    private static StorageReference storage;

    // Retornar a ref do database
    public static  DatabaseReference getRefFirebase(){
        if (refFirebase == null){
            refFirebase = FirebaseDatabase.getInstance().getReference();
        }
        return refFirebase;
    }

    // Retornar a instancia do Firebase Auth
    public static FirebaseAuth getFirebaseAuth(){
        if (refAuth == null){
            refAuth = FirebaseAuth.getInstance();
        }
        return refAuth;
    }

    // Return instance firebasestorage
    public static StorageReference getFirebaseStorage() {
        if ( storage == null ) {
            storage = FirebaseStorage.getInstance().getReference();
        }
        return storage;
    }

}
