package com.example.backend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.util.Map;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import java.time.format.DateTimeParseException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


  @RestController
  @RequestMapping("/x")
  @CrossOrigin
  public class UserController {
    
    private final UserService userService;
    @Autowired
    public UserController(UserService userService) {
      this.userService = userService;
    }
    @GetMapping("/unique-device-ids")
    public List<Object[]> getUniqueDeviceIds() {
        return userService.getDeviceIdCounts();
    }
    @GetMapping("/totalDistance")
    public ResponseEntity<?> getTotalDistance(
            @RequestParam("deviceId") String deviceId,
            @RequestParam("startTime") String startTimeStr,
            @RequestParam("endTime") String endTimeStr) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
            LocalDateTime startTime = LocalDateTime.parse(startTimeStr, formatter);
            LocalDateTime endTime = LocalDateTime.parse(endTimeStr, formatter);
    
            long startEpoch = startTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            long endEpoch = endTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    
            if (startEpoch >= endEpoch) {
                return ResponseEntity.badRequest().body("The start time must be before the end time.");
            }
    
            double totalDistance = userService.findTotalDistanceByDeviceIdAndEpochDataBetween(deviceId, startEpoch, endEpoch);
            return ResponseEntity.ok(Map.of("totalDistance", totalDistance));
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("Failed to parse date-time: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred while calculating total distance: " + e.getMessage());
        }
    }
    
    @GetMapping
    public ResponseEntity<?> getAllGeolocations(
        @RequestParam("startTime") String startTimeStr,
        @RequestParam("endTime") String endTimeStr,
        @RequestParam(value = "deviceId", required = false) String deviceId,
        Pageable pageable) {
    
        // Parse startTimeStr and endTimeStr into LocalDateTime
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        LocalDateTime startTime = LocalDateTime.parse(startTimeStr, formatter);
        LocalDateTime endTime = LocalDateTime.parse(endTimeStr, formatter);
    
        // Convert LocalDateTime to epoch milliseconds
        long startEpoch = startTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long endEpoch = endTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    
        try {
            Page<User_s> users;
            if (deviceId != null && !deviceId.trim().isEmpty()) {
                // Update the method call to include date filtering
                users = userService.findByDeviceIdAndEpochDataBetween(deviceId, startEpoch, endEpoch, pageable);
                if (users.isEmpty()) {
                    // Handle case where no users are found for the given deviceId and date range
                    return ResponseEntity.notFound().build();
                }
            } else {
                // If deviceId is not provided, consider how you want to handle this case.
                return ResponseEntity.badRequest().body("Device ID is required");
            }
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            // Handle exceptions
            return new ResponseEntity<>("An error occurred while fetching geolocation data", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
  }


