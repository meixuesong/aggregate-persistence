package com.github.meixuesong.user;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Service;

@Mapper
@Service
public interface UserMapper {
    User findById(String id);

    void insert(User user);

    int update(User user);

    void remove(String id);
}
