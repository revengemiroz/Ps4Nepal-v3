package com.example.msi.ps4nepal.models;

public class User {


private String user_id;
private String name;

//Alt + insert-->Constructor-->Select none
    public User() {
    }

//Alt + insert-->Getter and Setter-->select all string needed
    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


// displays/returns user_id and name from the input field
    @Override
    public String toString() {

        return "User{" +
                "user_id='" + user_id +'\'' +
                ", name='" +name+'\'' +
                        '}';
    }
}
