package com.learning.SpringSecurity.Repo;

import com.learning.SpringSecurity.Dao.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Integer> {

    Optional<User> findByUsername(String username);
    boolean existsByEmail (String email);
    Optional<User> findByEmail (String email);

}
