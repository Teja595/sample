package com.example.backend;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.json.JSONArray;
import org.json.JSONObject;

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
//     private static final String SSH_HOST = "learn2code.redgrape.tech";
//     private static final int SSH_PORT = 22; // Default SSH port
//     private static final String SSH_USER = "mvenkata";
//     private static final String SSH_PASSWORD = "IU4YkDD0kdLq";

//     private static final String REMOTE_HOST = "192.168.1.15"; // Assuming the database is on the same host as the SSH server
//     private static final int LOCAL_PORT = 5434; // Local port to forward the DB connection
//     private static final int REMOTE_PORT = 5432; // Default PostgreSQL port
//     private static final String DB_URL = "jdbc:postgresql://localhost:" + LOCAL_PORT + "/db_teja";
// //    private static final String DB_URL = "jdbc:postgresql://192.168.1.15:5433/db_teja";
//     private static final String DB_USER = "user_teja";
//     private static final String DB_PASSWORD = "VNgLRyn2jezQGBWaxH7Kuh";
 @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper; // Jackson's ObjectMapper, automatically configured by Spring Boot

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
                    // System.out.println(record.get("deviceid").asText());
                    // System.out.println(record.get("epoch_data").asLong());
                    // System.out.println(record.get("epoch_stored").asLong());
                    // System.out.println(record.get("latitude").asDouble());
                    // System.out.println(record.get("longitude").asDouble());
                    // location.setTimestamp(record.get("timestamp").asLong());
                    userRepository.save(location);
                }
            }
        }
    

    // private static Session setupSSHTunnel(String sshHost, int sshPort, String sshUser, String sshPassword,
    //                                       String remoteHost, int localPort, int remotePort) throws Exception {
    //     JSch jsch = new JSch();
    //     Session session = jsch.getSession(sshUser, sshHost, sshPort);
    //     session.setPassword(sshPassword);

    //     java.util.Properties config = new java.util.Properties();
    //     config.put("StrictHostKeyChecking", "no");
    //     session.setConfig(config);

    //     session.connect();
    //     session.setPortForwardingL(localPort, remoteHost, remotePort);
    //     System.out.println("SSH Tunnel established");
    //     return session;
    // }


    // private static void insertRecordsIntoDatabase(JSONArray records) throws SQLException {
    //     // Establish a database connection
    //     try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
    //         String sql = "INSERT INTO geolocation (deviceid, epoch_data, epoch_stored, latitude, longitude) VALUES (?, ?, ?, ?, ?)";

    //         // Prepare the SQL statement
    //         try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
    //             for (int i = 0; i < records.length(); i++) {
    //                 JSONObject record = records.getJSONObject(i);

    //                 pstmt.setString(1, record.getString("deviceid"));
    //                 pstmt.setLong(2, record.getLong("epoch_data"));
    //                 pstmt.setLong(3, record.getLong("epoch_stored"));
    //                 pstmt.setString(4, record.getString("latitude"));
    //                 pstmt.setString(5, record.getString("longitude"));

    //                 // Execute the insert statement
    //                 pstmt.executeUpdate();
    //             }
    //         }
    //     }
    //     catch (Exception e) {
    //         e.printStackTrace();
    //     }
    // }
}
