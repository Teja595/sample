package com.example.backend;
import java.util.List;
import java.util.Map;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
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
    private ObjectMapper objectMapper; // Jackson's ObjectMapper, automatically configured by Spring Boot
    @Autowired
            private JdbcTemplate jdbcTemplate;
     public void insertData(String data) throws JsonProcessingException {
            JsonNode root = objectMapper.readTree(data);
            JsonNode records = root.path("records"); // Assuming the data structure
    
            if (records.isArray()) {
                for (JsonNode record : records) {
                    User_s location = new User_s();
                    location.setDeviceId(record.get("deviceid").asText());
                    location.setEpochData(record.get("epoch_data").asLong());
                    location.setEpochStored(record.get("epoch_stored").asLong());
                    location.setLatitude(record.get("latitude").asDouble());
                    location.setLongitude(record.get("longitude").asDouble());
                   
                    userRepository.save(location);
                }
            }
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
