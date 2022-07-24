package com.xpto.vendingmachine.web.controller;

import com.xpto.vendingmachine.persistence.model.Role;
import com.xpto.vendingmachine.service.UserService;
import com.xpto.vendingmachine.web.dto.UserDTO;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class UserController {

    private UserService userService;

    @PostMapping("/api/users/buyers")
    public UserDTO registerBuyer(@RequestBody UserDTO userDTO) {
        return userService.saveUser(userDTO, new Role(Role.BUYER));
    }

    @PostMapping("/api/users/sellers")
    public UserDTO registerSeller(@RequestBody UserDTO userDTO) {
        return userService.saveUser(userDTO, new Role(Role.SELLER));
    }
}
