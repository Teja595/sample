package com.example.backend;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
public interface UserService {

  Page<User_s> findAll(Pageable pageable);
  void updateDeltaDistances();
  // List<User_s> findAllByOrderByEpochDataAsc();

  }





