package com.example.backend;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface UserRepository extends JpaRepository<User_s, Long> {
    List<User_s> findAllByOrderByEpochDataAsc();
    
}
