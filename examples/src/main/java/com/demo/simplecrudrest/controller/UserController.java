package com.demo.simplecrudrest.controller;

import com.demo.simplecrudrest.entity.User;
import com.demo.simplecrudrest.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/demo")
@AllArgsConstructor
public class UserController {

    UserService userService;

    @GetMapping("/users/{id}")
    public ResponseEntity<User> readUserById(@PathVariable("id") Integer id) {
        return userService.findUserById(id)
                .map(body -> {
                    log.info("Retrieved {}", body);
                    return ResponseEntity.ok(body);
                })
                .orElse(ResponseEntity.noContent().build());
    }

    @PostMapping("/users")
    public void addUser(@RequestBody User user) {
        userService.addUser(user);
    }

    @DeleteMapping("/users/{id}")
    public void deleteUserById(@PathVariable("id") Integer id) {
        userService.removeUserById(id);
    }
}
