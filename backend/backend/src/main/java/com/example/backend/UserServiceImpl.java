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
    public User save(User user) {
      return userRepository.save(user);
    }

    @Override
    public List<User> findAll() {
      return userRepository.findAll();
    }
  }


