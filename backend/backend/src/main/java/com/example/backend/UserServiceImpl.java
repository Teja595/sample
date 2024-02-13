package com.example.backend;
import org.springframework.stereotype.Service;
import java.util.List;


  @Service
  public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
      this.userRepository = userRepository;
    }
    // In UserServiceImpl class

    @Override
    public User_s save(User_s user) {
      return userRepository.save(user);
    }

    @Override
    public List<User_s> findAll() {
      return userRepository.findAll();
    }
  }


