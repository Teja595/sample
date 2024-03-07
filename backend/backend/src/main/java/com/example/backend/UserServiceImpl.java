package com.example.backend;
import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

  @Service
  public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
      this.userRepository = userRepository;
    }
    // In UserServiceImpl class

    // @Override
    // public User_s save(User_s user) {
    //   return userRepository.save(user);
    // }

    @Override
    public Page<User_s> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
  }


