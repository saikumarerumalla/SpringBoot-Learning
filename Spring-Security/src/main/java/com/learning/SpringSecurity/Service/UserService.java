package com.learning.SpringSecurity.Service;

import com.learning.SpringSecurity.Dao.User;
import com.learning.SpringSecurity.Repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepo userRepo;

    public User addUser(User user) {
        return userRepo.save(user);
    }
}
