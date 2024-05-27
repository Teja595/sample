package com.example.backend;
import java.util.ArrayList;
import java.util.*;
import java.util.Map;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import jakarta.transaction.Transactional;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.jdbc.core.JdbcTemplate;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import org.springframework.stereotype.Component;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInserter {

 @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserServiceImpl userServiceImpl;
//     @Autowired
// private DeviceRecordRepository deviceRecordRepository;

// public void insertRecordsData(List<DeviceRecord> records) {
//     deviceRecordRepository.saveAll(records);
// }

    @Autowired
    private ObjectMapper objectMapper; // Jackson's ObjectMapper, automatically configured by Spring Boot
    @Autowired
            private JdbcTemplate jdbcTemplate;
            @Transactional
    public void clearDatabase() {
        userRepository.deleteAllInBatch(); // This method assumes you're using Spring Data JPA repository
    }
            private User_s lastLocation = null; // Field to store the last location processed

            public void insertData(List<String> allData) throws JsonProcessingException {
                List<User_s> allUsers = new ArrayList<>();
                Set<Long> uniqueEpochData = new HashSet<>();
            
                // Iterate over each JSON string representing a page of data
                for (String dataPage : allData) {
                    JsonNode root = objectMapper.readTree(dataPage);
                    JsonNode records = root.path("records");
            
                    if (records.isArray()) {
                        for (JsonNode record : records) {
                            long epochData = record.get("epoch_data").asLong();
                            // Check if epochData is unique
                            if (uniqueEpochData.add(epochData)) {
                                User_s user = parseUser(record);
                        // if (!isDuplicate(user)) {
                            allUsers.add(user);
                        // }
                            }
                        }
                    }
                }
            
                // Process the entire list of User_s objects at once
                userServiceImpl.processAndStoreData(allUsers);
            }
            
              // Helper method to check if epoch_data is already present in the database
    private boolean isDuplicate(User_s user) {
          String sql = "SELECT COUNT(*) FROM user_s WHERE epoch_data = ? AND latitude = ? AND longitude = ?";
          Integer count = jdbcTemplate.queryForObject(sql, new Object[]{user.getEpochData(), user.getLatitude(), user.getLongitude()}, Integer.class);
          return count != null && count > 0;
      }
        
            // Helper method to parse a JsonNode to a User_s object, assuming you have setters for all fields in User_s
    private User_s parseUser(JsonNode record) {
        User_s user = new User_s();
        user.setDeviceId(record.get("deviceid").asText());
        user.setEpochData(record.get("epoch_data").asLong());
        user.setEpochStored(record.get("epoch_stored").asLong());
        user.setLatitude(record.get("latitude").asDouble());
        user.setLongitude(record.get("longitude").asDouble());
        // Include parsing for other fields as necessary
        return user;
    }

    


         // Autowire JdbcTemplate
        
            // Existing insertData method
        
            public void printCarsDataUsingJdbcTemplate() {
                String sql = "SELECT * FROM cars";
        
                List<Map<String, Object>> cars = jdbcTemplate.queryForList(sql);
        
                for (Map<String, Object> car : cars) {
                    System.out.println(car);
                }
            }

            public void runAtStartup() {
                    
                        System.out.println("Fetching users from the database...");
                        List<User_s> users = userRepository.findAll();
                        for (User_s user : users) {
                            System.out.println(user.toString());
                        }
                    
                }



            }