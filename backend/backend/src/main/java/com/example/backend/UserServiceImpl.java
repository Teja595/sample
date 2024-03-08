package com.example.backend;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

  @Service
  public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
      this.userRepository = userRepository;
    }

    public static final double EARTH_RADIUS_KM = 6371.0;

    public static double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
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
   
    @Override
    @Transactional
    public void updateDeltaDistances() {
      List<User_s> users = userRepository.findAllByOrderByEpochDataAsc();  // Assuming this method is implemented to fetch users in order.
        if (users.size() < 2) return; // Not enough data to calculate delta distances.
    
        User_s previous = users.get(0); // Starting from the first user.
    
        // Loop starts from the second user.
        for (int i = 1; i < users.size(); i++) {
            User_s current = users.get(i);
    
            // Calculate the distance between the previous and current user.
            double deltaDistance = haversineDistance(
                    previous.getLatitude(), previous.getLongitude(),
                    current.getLatitude(), current.getLongitude());
    
            // Update the current user's additionalField1 with the delta distance.
            current.setDelta_distance(deltaDistance);//km
            // Calculate delta time between the previous and current epoch_data.
        //     double deltaTime = (current.getEpochData() - previous.getEpochData()) * 1.0; // Convert to double
        double deltaTime = (current.getEpochData() - previous.getEpochData()) / 1000.0; // Assuming epochData is in milliseconds(seconds)
        current.setDelta_t(deltaTime);
        double sped = (current.getDelta_distance() * 3600) / current.getDelta_t(); // Speed in km/h
        current.setSpeed(sped);
        
        // // Update the current user with the delta time.
        // current.setDelta_t(deltaTime); // Assuming you have this setter method
            // System.out.println(deltaDistance);
            // Save the updated current user back to the database.
            userRepository.save(current); // This saves the current user with the updated distance.
    
            // The current user becomes the previous user for the next iteration.
            previous = current;
        }
    }

    // @Transactional
    // public void recalculateDeltaValues() {
    //     List<User_s> users = userRepository.findAllByOrderByIdAsc();
    //     if (users.size() < 2) return; // Not enough data

    //     User_s previous = users.get(0);

    //     for (int i = 1; i < users.size(); i++) {
    //         User_s current = users.get(i);

    //         double deltaDistance = haversineDistance(
    //                 previous.getLatitude(), previous.getLongitude(),
    //                 current.getLatitude(), current.getLongitude());
    //         current.setDeltaDistance(deltaDistance);

    //         double deltaTime = (current.getEpochData() - previous.getEpochData()) / 1000.0; // Assuming epochData is in milliseconds
    //         current.setDeltaTime(deltaTime);

    //         userRepository.save(current); // This saves the current user with updated values
    //         previous = current;
    //     }
    // }

    
  }


