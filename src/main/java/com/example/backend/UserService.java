package com.example.backend;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
public interface UserService {

  Page<User_s> findAll(Pageable pageable);
  // void updateDeltaDistances();
  
  Page<User_s> findByDeviceId(String deviceId, Pageable pageable);
  Page<User_s> findByDeviceIdAndEpochDataBetween(String deviceId, Long startEpoch, Long endEpoch, Pageable pageable);
  
  public List<Object[]> getDeviceIdCounts();
  // Add this new method
  Double findTotalDistanceByDeviceIdAndEpochDataBetween(String deviceId, Long startEpoch, Long endEpoch);
}





