package com.github.meixuesong.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private UserMapper mapper;

    @Autowired
    public UserService(UserMapper mapper) {
        this.mapper = mapper;
    }

    public User save(User user) {
        mapper.save(user);

        return mapper.findById(user.getId());
    }
}
