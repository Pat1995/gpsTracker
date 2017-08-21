package com.ahmadrosid.drawroutemaps;

/**
 * Created by pat95 on 20.08.2017.
 */

public class UserInfo {
    private String name;
    private String email;

    public UserInfo(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getEmail(){
        return email;
    }

    @Override
    public String toString() {
        return name;
    }
}
