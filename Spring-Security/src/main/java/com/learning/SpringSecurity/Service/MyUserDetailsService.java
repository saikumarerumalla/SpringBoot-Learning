package com.learning.SpringSecurity.Service;

import com.learning.SpringSecurity.Dao.User;
import com.learning.SpringSecurity.Dao.UserPrincipal;
import com.learning.SpringSecurity.Repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepo repo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = repo.findByUsername(username);

        if(user==null){
            System.out.println("User not found");
            throw new UsernameNotFoundException("User not found");
        }else{
            return new UserPrincipal(user);
        }
    }
}
