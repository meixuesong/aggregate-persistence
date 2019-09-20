package com.github.meixuesong.user;

import com.github.meixuesong.common.Aggregate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityNotFoundException;


@Repository
public class UserRepository {

    private UserMapper mapper;

    @Autowired
    public UserRepository(UserMapper mapper) {
        this.mapper = mapper;
    }

    public void save(Aggregate<User> userAggregate) {
        if (userAggregate.isNew()) {
            userAggregate.getRoot().increaseVersion();
            mapper.insert(userAggregate.getRoot());
        } else if (userAggregate.isChanged()) {
            int rowsUpdated = mapper.update(userAggregate.getRoot());
            if (rowsUpdated != 1) {
                throw new RuntimeException("修改失败，未找到或者已经被其它用户修改。");
            }
        }
    }

    public Aggregate<User> findById(String id) {
        User user = mapper.findById(id);

        if (user == null) {
            throw new EntityNotFoundException("User("+id+") not found");
        }

        return new Aggregate<>(user);
    }

    public void remove(String id) {
        mapper.remove(id);
    }
}
