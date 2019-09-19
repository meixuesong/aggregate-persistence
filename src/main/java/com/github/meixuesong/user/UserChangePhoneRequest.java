package com.github.meixuesong.user;

public class UserChangePhoneRequest {
    private final String id;
    private final String newPhone;

    public UserChangePhoneRequest(String id, String newPhone) {

        this.id = id;
        this.newPhone = newPhone;
    }

    public String getId() {
        return id;
    }

    public String getNewPhone() {
        return newPhone;
    }
}
