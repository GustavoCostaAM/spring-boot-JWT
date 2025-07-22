package org.app1.treinoauth.Service;

import org.app1.treinoauth.Model.UserModel;
import org.app1.treinoauth.Model.UserPrincipal;
import org.app1.treinoauth.Repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserModel userModel = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
        return new UserPrincipal(userModel);
    }
}
