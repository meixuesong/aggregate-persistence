package com.github.meixuesong.user;

import com.github.meixuesong.common.Aggregate;
import com.github.meixuesong.common.AggregateFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private UserRepository repository;

    @Autowired
    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public User save(User user) {
        repository.save(AggregateFactory.createAggregate(user));

        return repository.findById(user.getId()).getRoot();
    }

    public User findUserById(String id) {
        Aggregate<User> userAggregate = repository.findById(id);

        return userAggregate.getRoot();
    }

    public void updatePhone(String id, String newPhone) {
        Aggregate<User> userAggregate = repository.findById(id);

        if (userAggregate == null) {
            return;
        }

        User user = userAggregate.getRoot();
        user.setPhone(newPhone);

        repository.save(userAggregate);
    }

    public void deleteUser(String id) {
        repository.remove(id);
    }
}
