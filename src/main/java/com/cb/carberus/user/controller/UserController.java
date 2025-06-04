package com.cb.carberus.user.controller;

import com.cb.carberus.user.dto.AddUserDTO;
import com.cb.carberus.user.dto.CurrentUserResponseDTO;
import com.cb.carberus.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<CurrentUserResponseDTO> getMe() {
        CurrentUserResponseDTO user = userService.getCurrentUser();
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/deleteUser/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable String userId) {
        boolean deleted = userService.deleteUser(userId);
        return deleted
                ? ResponseEntity.ok().build()
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/users")
    public ResponseEntity<CurrentUserResponseDTO[]> getUsers() {
        var users =  userService.getUsers();
        return ResponseEntity.ok(users);
    }

    @PostMapping("/user")
    public ResponseEntity<Void> addUser(AddUserDTO addUserDTO) {
        userService.addUser(addUserDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
