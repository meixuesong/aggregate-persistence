package com.github.meixuesong.user;

import com.github.meixuesong.common.Versionable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class User implements Versionable {
    private String id;
    private String name;
    private String phone;
    private String address;
    private int version;

    public User(String id, String name, String phone, String address, int version) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.version = version;
    }

    @Override
    public int getVersion() {
        return version;
    }

}
