package com.example.instagrum.model;

import com.example.instagrum.helper.ConfigFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class User implements Serializable {

    private String id;
    private String name;
    private String email;
    private String password;
    private String photo;
    private int followers = 0;
    private int following = 0;
    private int post = 0;

    public User() {
    }

    public void save() {
        DatabaseReference firebaseRef = ConfigFirebase.getRefFirebase();
        DatabaseReference userRef = firebaseRef.child("usuarios").child( getId() );
        userRef.setValue( this );
    }

    public void att() {

        DatabaseReference firebaseRef = ConfigFirebase.getRefFirebase();
        DatabaseReference userRef = firebaseRef.child("usuarios").child( getId() );

        // setValue = sobrescreve o nome
        // updateChildren = atualiza o nome
        Map<String, Object> valueUser = convertParaMap();
        userRef.updateChildren( valueUser );

    }

    public Map<String, Object> convertParaMap() {

        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("email", getEmail());
        userMap.put("name", getName());
        userMap.put("id", getId());
        userMap.put("photo", getPhoto());
        userMap.put("followers", getFollowers());
        userMap.put("following", getFollowing());
        userMap.put("post", getPost());

        return userMap;

    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public int getFollowing() {
        return following;
    }

    public void setFollowing(int following) {
        this.following = following;
    }

    public int getPost() {
        return post;
    }

    public void setPost(int post) {
        this.post = post;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name)
    {
        this.name = name.toUpperCase();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
