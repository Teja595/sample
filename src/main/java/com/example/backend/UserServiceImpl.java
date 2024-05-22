package com.example.backend;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.LinkedList;
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
    public Page<User_s> findByDeviceId(String deviceId, Pageable pageable) {
        return userRepository.findByDeviceId(deviceId, pageable); // Assuming this method exists in your repository
    }
    @Override
    public Page<User_s> findByDeviceIdAndEpochDataBetween(String deviceId, Long startEpoch, Long endEpoch, Pageable pageable) {
        return userRepository.findByDeviceIdAndEpochDataBetween(deviceId, startEpoch, endEpoch, pageable);
    }
    @Transactional
    public void processAndStoreData(List<User_s> allFetchedData) {
        // Sort data by epoch_data
        allFetchedData.sort(Comparator.comparingLong(User_s::getEpochData));
    
        // Step 1: Filter the data based on distance criteria
        List<User_s> distanceFilteredData = filterDataByMinimumDistance(allFetchedData);
        
        // Step 2: Calculate only speeds on the distance-filtered data
    //   List<User_s> speed =   calculateAndFilterSpeeds(distanceFilteredData);
        // System.out.println(speed);
        // Step 3: Filter the data based on speed criteria
        // List<User_s> speedFilteredData = filterDataBySpeed(distanceFilteredData, 10.0, 65.0);
        
        // Step 4: Calculate remaining fields like delta distances, delta_t, etc., only on the speed-filtered data
        calculateFields(distanceFilteredData);
    
        // Store the fully processed and filtered data
        userRepository.saveAll(distanceFilteredData);
    }
    
    private List<User_s> calculateAndFilterSpeeds(List<User_s> users) {
       
        List<User_s> validSpeedUsers = new ArrayList<>();  // List to hold users with valid speeds
        if (users.size() < 2) return validSpeedUsers;  // Return empty list if not enough users to calculate speed
    
        User_s previousUser = users.get(0);
        validSpeedUsers.add(previousUser);  // Optionally add the first user unconditionally
    
        for (int i = 1; i < users.size(); i++) {
            User_s currentUser = users.get(i);
    
            double deltaDistance = haversineDistance(previousUser.getLatitude(), previousUser.getLongitude(), 
                                                     currentUser.getLatitude(), currentUser.getLongitude());
            double deltaTime = (currentUser.getEpochData() - previousUser.getEpochData()) / 1000.0; // Convert ms to seconds
            
            if (deltaTime > 0) {
                
                double speed = (deltaDistance / deltaTime) * 3.6; // Convert m/s to km/h
                System.out.println(speed);
                if (speed > 10.0 && speed < 65.0) {
                    currentUser.setSpeed(speed);
                    validSpeedUsers.add(currentUser);  // Add only if speed is within the desired range
                }
            }
            previousUser = currentUser;  // Update previousUser for the next calculation
        }
        return validSpeedUsers;
    }
    
    
    
 
    
    

private List<User_s> filterDataByMinimumDistance(List<User_s> users) {
    if (users.size() < 2) {
        return Collections.emptyList();
    }

    List<User_s> filteredUsers = new ArrayList<>();
    User_s previous = users.get(0);
    filteredUsers.add(previous);

    for (int i = 1; i < users.size(); i++) {
        User_s current = users.get(i);
        double deltaDistance = haversineDistance(previous.getLatitude(), previous.getLongitude(), current.getLatitude(), current.getLongitude());

        if (deltaDistance >= 0.05) {
            filteredUsers.add(current);
            previous = current;
        }
    }

    return filteredUsers;
}

private List<User_s> filterDataBySpeed(List<User_s> users, double minSpeed, double maxSpeed) {
    // System.out.println("Before filtering:");
    // users.forEach(user -> System.out.println("Speed: " + (user.getSpeed() == null ? "null" : user.getSpeed())));

    // Check if the list is empty to avoid IndexOutOfBoundsException
    if (users.isEmpty()) {
        return new ArrayList<>(); // Return an empty list if no users are present
    }

    // Extract the first user to ensure it is not filtered
    User_s firstUser = users.get(0);
    List<User_s> remainingUsers = users.subList(1, users.size()); // Exclude the first user for filtering

    List<User_s> filteredUsers = new ArrayList<>();
    filteredUsers.add(firstUser); // Add the first user back unconditionally

    try {
        // Apply the speed filter to the remaining users
        List<User_s> filteredRemainingUsers = remainingUsers.stream()
            .filter(user -> user.getSpeed() != null && user.getSpeed() >= minSpeed && user.getSpeed() <= maxSpeed)
            .collect(Collectors.toList());
        filteredUsers.addAll(filteredRemainingUsers); // Add the filtered users

        // System.out.println("After filtering:");
        // filteredUsers.forEach(user -> System.out.println("Speed: " + user.getSpeed()));
    } catch (Exception e) {
        System.out.println("Error during filtering: " + e.getMessage());
        e.printStackTrace();
    }
    System.out.println(filteredUsers);

    return filteredUsers;
}



    public List<Object[]> getDeviceIdCounts() {
        return userRepository.getDeviceIdCountsSumsAndMinMaxHumanReadableDatesAndDiff();
    }
    // Implement the new method
    @Override
public Double findTotalDistanceByDeviceIdAndEpochDataBetween(String deviceId, Long startEpoch, Long endEpoch) {
    return userRepository.findTotalDistanceByDeviceIdAndEpochDataBetween(deviceId, startEpoch, endEpoch);
}

private void calculateFields(List<User_s> users) {
    if (users.isEmpty()) {
        return;
    }

    // Preprocess to remove entries with insufficient delta distance
    // List<User_s> filteredUsers = filterByMinimumDistance(users, 0.05);
    List<User_s> filteredUsers = users;
    if (filteredUsers.isEmpty()) {
        return; // Exit if no valid entries after filtering
    }

    LinkedList<Double> speedWindow = new LinkedList<>();
    User_s previous = filteredUsers.get(0);
    previous.setDelta_distance(0.0);
    previous.setSpeed(0.0);
    previous.setDelta_t(0.0);
    previous.setMov_avg_spd(0.0);
    previous.updateHumanReadableDate();

    // Initialize with the first user as a basis for comparison
    speedWindow.add(0.0); // Starting with a speed of 0.0 km/h

    // List to hold only users with acceptable moving average speeds
    List<User_s> usersWithValidAverageSpeeds = new ArrayList<>();

    for (int i = 1; i < filteredUsers.size(); i++) {
        User_s current = filteredUsers.get(i);
        double deltaDistance = haversineDistance(previous.getLatitude(), previous.getLongitude(), current.getLatitude(), current.getLongitude());
        double deltaTime = (current.getEpochData() - previous.getEpochData()) / 1000.0; // Convert milliseconds to seconds

        if (deltaTime > 0) {
            double speed = (deltaDistance / deltaTime) * 3600; // Convert meters per second to kilometers per hour
            current.setSpeed(speed);
            current.setDelta_distance(deltaDistance);
            current.setDelta_t(deltaTime);
            current.updateHumanReadableDate();

            // Update the sliding window for moving average speed
            speedWindow.add(speed);
            if (speedWindow.size() > 5) {
                speedWindow.removeFirst();
            }
            double averageSpeed = speedWindow.stream().mapToDouble(a -> a).sum() / speedWindow.size();
            current.setMov_avg_spd(averageSpeed);

            // Add to list only if moving average speed is within the specified range
            if (averageSpeed >= 10 && averageSpeed < 65) {
                usersWithValidAverageSpeeds.add(current);
            }
            previous = current;  // Update the previous user to the current one
        }
    }

    // Optional: Replace original user list with the one containing valid average speeds
    users.clear();
    users.addAll(usersWithValidAverageSpeeds);
}


private List<User_s> filterByMinimumDistance(List<User_s> users, double minDistance) {
    List<User_s> filteredUsers = new ArrayList<>();
    User_s previous = users.get(0);
    filteredUsers.add(previous);

    for (int i = 1; i < users.size(); i++) {
        User_s current = users.get(i);
        double deltaDistance = haversineDistance(previous.getLatitude(), previous.getLongitude(), current.getLatitude(), current.getLongitude());
        if (deltaDistance >= minDistance) {
            filteredUsers.add(current);
            previous = current; // Only update previous if current is added
        }
    }
    return filteredUsers;
}    
  }


