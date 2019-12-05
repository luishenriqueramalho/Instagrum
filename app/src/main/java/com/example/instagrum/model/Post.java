package com.example.instagrum.model;

import android.provider.ContactsContract;

import com.example.instagrum.helper.ConfigFirebase;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;

public class Post implements Serializable {

    private String id;
    private String idUser;
    private String description;
    private String photo;

    public Post() {

        DatabaseReference firebaseRef = ConfigFirebase.getRefFirebase();
        DatabaseReference postRef = firebaseRef.child("posts");
        String idPost = postRef.push().getKey();
        setId( idPost );

    }

    public boolean save() {

        DatabaseReference firebaseRef = ConfigFirebase.getRefFirebase();
        DatabaseReference postRef = firebaseRef.child("posts").child(getIdUser()).child(getId());
        postRef.setValue(this);
        return true;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
