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
    public double calculateTotalDurationInHours(String deviceId, long startDate, long endDate) {
        List<User_s> users = userRepository.findAllByDeviceIdAndEpochDataBetween(deviceId, startDate, endDate);

        double totalDurationInSeconds = users.stream()
            .mapToDouble(User_s::getDelta_t)
            .sum();

        return totalDurationInSeconds / 3600.0; // Convert seconds to hours
    }
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
    // System.out.println(allFetchedData);
        // Step 1: Filter the data based on distance criteria
        List<User_s> distanceFilteredData = filterDataByMinimumDistance(allFetchedData);
        
        // Step 2: Calculate only speeds on the distance-filtered data
    //   List<User_s> speed =   calculateAndFilterSpeeds(distanceFilteredData);
        // System.out.println(speed);
        // Step 3: Filter the data based on speed criteria
        // List<User_s> speedFilteredData = filterDataBySpeed(distanceFilteredData, 10.0, 65.0);
        
        // Step 4: Calculate remaining fields like delta distances, delta_t, etc., only on the speed-filtered data
        calculateFieldss(distanceFilteredData);
    // Fetch existing data from the database to check for duplicates
        List<User_s> existingData = userRepository.findAll();
    
        // Use a Set to track unique records based on some unique combination of fields (e.g., epochData, userId)
        Set<String> uniqueKeys = existingData.stream()
                .map(user -> user.getEpochData() + "_" + user.getDeviceId())
                .collect(Collectors.toSet());
    
        // Filter out duplicates from the distanceFilteredData
        List<User_s> uniqueDataToStore = distanceFilteredData.stream()
                .filter(user -> uniqueKeys.add(user.getEpochData() + "_" + user.getDeviceId()))
                .collect(Collectors.toList());
    
        // Store the fully processed and filtered data
        userRepository.saveAll(uniqueDataToStore);
    }
    
    
    
    
    public double[] calculateTotalDurationAndDistance(String deviceId, long startDate, long endDate) {
        List<User_s> users = userRepository.findAllByDeviceIdAndEpochDataBetween(deviceId, startDate, endDate);
    
        Map<String, Double[]> totalDurationsAndDistances = calculateTotalDurationsAndDistances(users);
    
        Double[] durationAndDistance = totalDurationsAndDistances.getOrDefault(deviceId, new Double[]{0.0, 0.0});
        double totalDurationInHours = durationAndDistance[0]; // Already in hours
        double totalDistance = durationAndDistance[1]; // Distance in kilometers
    
        return new double[]{totalDurationInHours, totalDistance};
    }
    
    public Map<String, Double[]> calculateTotalDurationsAndDistances(List<User_s> users) {
        Map<String, Double[]> totalDurationsAndDistances = new HashMap<>();
    
        for (User_s user : users) {
            String deviceId = user.getDeviceId();
            double deltaTime = user.getDelta_t();
            double deltaDistance = user.getDelta_distance(); // Distance in kilometers
    
            Double[] durationAndDistance = totalDurationsAndDistances.getOrDefault(deviceId, new Double[]{0.0, 0.0});
            durationAndDistance[0] += deltaTime; // Accumulate time in seconds
            durationAndDistance[1] += deltaDistance; // Accumulate distance in kilometers
    
            totalDurationsAndDistances.put(deviceId, durationAndDistance);
        }
    
        // Convert total durations from seconds to hours
        for (Map.Entry<String, Double[]> entry : totalDurationsAndDistances.entrySet()) {
            Double[] durationAndDistance = entry.getValue();
            durationAndDistance[0] = durationAndDistance[0] / 3600.0; // Convert seconds to hours
        }
    
        return totalDurationsAndDistances;
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
Boolean flag = true;

private void calculateFieldss(List<User_s> users) {
    if (users.isEmpty()) {
        return;
    }

    List<User_s> filteredUsers = users; // No filtering is being applied here
    if (filteredUsers.isEmpty()) {
        return; // Exit if no valid entries after filtering
    }

    // Flag check and debug print
    // if (flag) {
    //     System.out.println(users);
    //     flag = false;
    // }

    LinkedList<Double> speedWindow = new LinkedList<>();
    User_s previous = null; // Initialize previous as null
    List<User_s> usersWithValidAverageSpeeds = new ArrayList<>();

    for (User_s current : filteredUsers) {
        if (previous == null || !isSameDay(previous.getEpochData(), current.getEpochData())) {
            current.setDelta_distance(0.0);
            current.setSpeed(0.0);
            current.setDelta_t(0.0);
            current.setMov_avg_spd(0.0);
            current.updateHumanReadableDate();

            // Initialize the sliding window for the new day
            speedWindow.clear();
            speedWindow.add(0.0); // Starting with a speed of 0.0 km/h
        } else {
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
            }
        }
        previous = current; // Update the previous user to the current one
    }
  // Calculate total durations and distances for each device ID
  Map<String, Double[]> totalDurationsAndDistances = calculateTotalDurationsAndDistances(usersWithValidAverageSpeeds);

  // Store or log total durations and distances as needed
  for (Map.Entry<String, Double[]> entry : totalDurationsAndDistances.entrySet()) {
      String deviceId = entry.getKey();
      Double[] durationAndDistance = entry.getValue();
      Double totalDurationInHours = durationAndDistance[0];
      Double totalDistance = durationAndDistance[1];
      System.out.println("Device ID: " + deviceId + ", Total Duration: " + totalDurationInHours + " hours, Total Distance: " + totalDistance + " km");
  }
    
    users.clear();
    users.addAll(usersWithValidAverageSpeeds);
}
// private Map<String, Double[]> calculateTotalDurationsAndDistances(List<User_s> users) {
//     Map<String, Double[]> totalDurationsAndDistances = new HashMap<>();

//     for (User_s user : users) {
//         String deviceId = user.getDeviceId();
//         double deltaTime = user.getDelta_t();
//         double deltaDistance = user.getDelta_distance(); // Distance in kilometers

//         Double[] durationAndDistance = totalDurationsAndDistances.getOrDefault(deviceId, new Double[]{0.0, 0.0});
//         durationAndDistance[0] += deltaTime; // Accumulate time in seconds
//         durationAndDistance[1] += deltaDistance; // Accumulate distance in kilometers

//         totalDurationsAndDistances.put(deviceId, durationAndDistance);
//     }

//     // Convert total durations from seconds to hours
//     for (Map.Entry<String, Double[]> entry : totalDurationsAndDistances.entrySet()) {
//         Double[] durationAndDistance = entry.getValue();
//         durationAndDistance[0] = durationAndDistance[0] / 3600.0; // Convert seconds to hours
//     }

//     return totalDurationsAndDistances;
// }



// Helper method to check if two timestamps are on the same day
private boolean isSameDay(long epoch1, long epoch2) {
    Calendar cal1 = Calendar.getInstance();
    cal1.setTimeInMillis(epoch1);
    Calendar cal2 = Calendar.getInstance();
    cal2.setTimeInMillis(epoch2);
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
           cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
}
   
  }


