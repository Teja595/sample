package com.example.backend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


  @RestController
  @RequestMapping("/x")
  @CrossOrigin
  public class UserController {
    
    private final UserService userService;
    @Autowired
    public UserController(UserService userService) {
      this.userService = userService;
    }


    @GetMapping
    public ResponseEntity<Page<User_s>> getAllGeolocations(Pageable pageable) {
        Page<User_s> users = userService.findAll(pageable);
        // Add logging here to inspect the 'users' content
        System.out.println(users.getContent());
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
    
  }



