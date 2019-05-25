package com.example.yummy.Model;

import com.example.yummy.Utils.Node;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class User {
    private String userId;
    private String username;
    private String password;
    private int role;
    private DatabaseReference dataNode;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public void AddUser(User user, String uid){
        dataNode = FirebaseDatabase.getInstance().getReference().child(Node.user);
        dataNode.child(uid).setValue(user);
    }
}
