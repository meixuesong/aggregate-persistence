package com.github.meixuesong.user;

import com.github.meixuesong.common.Aggregate;
import com.github.meixuesong.common.AggregateFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityNotFoundException;
import javax.persistence.OptimisticLockException;


@Repository
public class UserRepository {

    private UserMapper mapper;

    @Autowired
    public UserRepository(UserMapper mapper) {
        this.mapper = mapper;
    }

    public void save(Aggregate<User> userAggregate) {
        if (userAggregate.isNew()) {
            mapper.insert(userAggregate.getRoot());
        } else if (userAggregate.isChanged()) {
            if (mapper.update(userAggregate.getRoot()) != 1) {
                throw new OptimisticLockException(
                        String.format("Update user (%s) error, it's not found or changed by another user", userAggregate.getRoot().getId()));
            }
        }
    }

    public Aggregate<User> findById(String id) {
        User user = mapper.findById(id);

        if (user == null) {
            throw new EntityNotFoundException("User("+id+") not found");
        }

        return AggregateFactory.createAggregate(user);
    }

    public void remove(String id) {
        mapper.remove(id);
    }
}
