package com.doohee.mediaserver.service;

import com.doohee.mediaserver.entity.User;
import com.doohee.mediaserver.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository){
        this.userRepository = userRepository;
    }
    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String username){
        return userRepository.findOneWithAuthoritiesByUsername(username)
                .map(user->createUser(username, user))
                .orElseThrow(()->new UsernameNotFoundException(username+" 을 데이터베이스에서 찾을 수 없습니다"));
    }

    private org.springframework.security.core.userdetails.User createUser(String username, User user){
        List<GrantedAuthority> grantedAuthorities =user.getAuthorities().stream()
                .map(authority->new SimpleGrantedAuthority(authority.getAuthorityName()))
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), grantedAuthorities);
    }
}
