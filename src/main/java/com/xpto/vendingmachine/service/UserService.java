package com.xpto.vendingmachine.service;

import com.xpto.vendingmachine.persistence.model.Role;
import com.xpto.vendingmachine.persistence.model.Session;
import com.xpto.vendingmachine.persistence.model.TokenRevoke;
import com.xpto.vendingmachine.persistence.model.UserAuth;
import com.xpto.vendingmachine.persistence.repository.SessionRepository;
import com.xpto.vendingmachine.persistence.repository.TokenRevokeRepository;
import com.xpto.vendingmachine.persistence.repository.UserRepository;
import com.xpto.vendingmachine.web.controller.BadRequestException;
import com.xpto.vendingmachine.web.dto.UserAuthDTO;
import com.xpto.vendingmachine.web.dto.UserDTO;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {
    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private SessionRepository sessionRepository;
    private TokenRevokeRepository tokenRevokeRepository;

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

    public void logoutAll(UserAuthDTO userAuthDTO) {
        UserAuth userAuth = userRepository.searchByUsername(userAuthDTO.getUsername())
                .orElseThrow(() -> new BadRequestException("Invalid Credentials."));
        if(!bCryptPasswordEncoder.matches(userAuthDTO.getPassword(), userAuth.getPassword())) {
            throw new BadRequestException("Invalid Credentials.");
        }

        Optional<Session> session = sessionRepository.findById(userAuthDTO.getUsername());
        session.ifPresent(s -> {
            sessionRepository.delete(s);
            tokenRevokeRepository.save(TokenRevoke.builder().jti(s.getJti()).expiration(1L).build());
        });

    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username);
    }
}
