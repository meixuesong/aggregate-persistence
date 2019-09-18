package com.github.meixuesong.user;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Service;

@Mapper
@Service
public interface UserMapper {
    User findById(String id);

    void save(User user);
}
