package com.example.backend;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


  @RestController
  @RequestMapping("/users")
  public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
      this.userService = userService;
    }
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
      User savedUser = userService.save(user);
      return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }
    @GetMapping
    public List<User> getAllUsers() {
      return userService.findAll();
    }
  }



