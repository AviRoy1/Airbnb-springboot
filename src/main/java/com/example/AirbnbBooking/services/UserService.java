package com.example.AirbnbBooking.services;

import com.example.AirbnbBooking.models.User;
import com.example.AirbnbBooking.repositories.writes.UserWriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collector;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserWriteRepository userWriteRepository;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userWriteRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Email is not valid!!"));
        return org.springframework.security.core.userdetails.User
                .builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(mapRolesToAuthorities(user))
                .build();
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(User user) {
        return user.getRoles()
                .stream()
                .map(e -> new SimpleGrantedAuthority(e.getRole().getRole()))
                .toList();
    }

}
