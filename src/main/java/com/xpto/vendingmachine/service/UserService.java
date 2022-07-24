package com.xpto.vendingmachine.service;

import com.xpto.vendingmachine.persistence.model.Role;
import com.xpto.vendingmachine.persistence.model.UserAuth;
import com.xpto.vendingmachine.persistence.repository.UserRepository;
import com.xpto.vendingmachine.web.controller.BadRequestException;
import com.xpto.vendingmachine.web.dto.UserDTO;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {
    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserDTO saveUser(UserDTO userDTO, Role role) {

        if(userRepository.searchByUsername(userDTO.getUsername()).isPresent()) {
            throw new BadRequestException("Username already exists.");
        }
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        UserAuth user = UserAuth.builder()
                .username(userDTO.getUsername())
                .password(bCryptPasswordEncoder
                        .encode(userDTO.getPassword()))
                .authorities(roles)
                .build();
        UserAuth saved = userRepository.save(user);
        UserDTO userDTOSaved = userDTO.builder()
                .username(saved.getUsername())
                .build();
        return userDTOSaved;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username);
    }
}
