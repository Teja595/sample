package com.example.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import java.io.IOException;
import java.util.*;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.MediaType;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.backend.DatabaseInserter;
import java.net.*;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

// /home/mvenkata/get_users_data/backend/backend/src/main/java/com/example/backend/DatabaseInserter


// mvn spring-boot:run

@SpringBootApplication
public class BackendApplication {
	@Autowired
	private DatabaseInserter databaseInserter;
	
		@Autowired
		private Environment env;
	
	
	public static void main(String[] args) throws Exception {
		
		SpringApplication.run(BackendApplication.class, args);
	}
		// 9740161211   
// 6394934198
// 7905324311
// 8171058530
// 9473538088
// 9076787184
// 7272850869
// 8953603550
// 7052387845
// 9170514758  128 pandey
// 9591045797
// 6203220212  377 vivek
// 9917249136  49 yogesh
@Bean
public CommandLineRunner commandLineRunner(DatabaseInserter databaseInserter, ObjectMapper objectMapper) {
    return args -> {
        databaseInserter.clearDatabase();
        List<String> deviceIds = fetchDeviceIds();
		// List<String> deviceIds = Arrays.asList("9740161211", "6394934198", "7905324311", "8171058530", "9473538088", "9076787184", "7272850869", "8953603550");
		// System.out.println(deviceIds);
        ExecutorService executor = Executors.newFixedThreadPool(10); // Use a thread pool to handle concurrent requests

        for (String deviceId : deviceIds) {
            executor.submit(() -> processDevice(deviceId, databaseInserter, objectMapper));
        }

        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("Finished fetching data.");
    };
}

private void processDevice(String deviceId, DatabaseInserter databaseInserter, ObjectMapper objectMapper) {
    int page = 1;
    final int rowsPerPage = 10000;
    boolean continueFetching = true;

    while (continueFetching) {
        try {
            String data = fetchGeoLocationData(page, rowsPerPage, deviceId);
            GeoLocationDataResponse response = objectMapper.readValue(data, GeoLocationDataResponse.class);

            if (response.hasRecords()) {
                databaseInserter.insertData(Collections.singletonList(data));
                System.out.println("Data for page " + page + " of device ID " + deviceId + " has been processed and inserted into the database.");
                page++;
            } else {
                continueFetching = false;
            }
        } catch (IOException e) {
            System.out.println("IOException occurred for device ID: " + deviceId + " at page " + page + ": " + e.getMessage());
            // Handle the exception, maybe retry or break
        } 
		// catch (SocketTimeoutException e) {
        //     System.out.println("Timeout occurred for device ID: " + deviceId + " at page " + page);
        // } catch (JsonProcessingException e) {
        //     System.out.println("Error processing JSON data: " + e.getMessage());
        // }
    }
}

	public static String fetchGeoLocationData(int page,int rows,String deviceId) throws IOException {
		String url = "https://gpslog.srifincredit.com/shrms/get_device_location";

		OkHttpClient client = new OkHttpClient();
		ObjectMapper objectMapper = new ObjectMapper(); // Jackson's ObjectMapper
		long currentTimeInSeconds = Instant.now().getEpochSecond();

		long previousDayTimeInSeconds = Instant.now().minus(4, ChronoUnit.DAYS).getEpochSecond();
		Map<String, Object> requestBodyMap = new HashMap<>();
		requestBodyMap.put("deviceID", deviceId);
		requestBodyMap.put("page", page);
		requestBodyMap.put("rows", rows);
		requestBodyMap.put("ts_from", previousDayTimeInSeconds);
		requestBodyMap.put("ts_to", currentTimeInSeconds);

		// Convert Map to JSON String
		String requestBodyJson = objectMapper.writeValueAsString(requestBodyMap);

		RequestBody requestBody = RequestBody.create(requestBodyJson, MediaType.get("application/json; charset=utf-8"));

		Request request = new Request.Builder()
				.url(url)
				.post(requestBody)
				.addHeader("X-API-TOKEN", "TLQ6lMV3RxujZBhOyKbkcWftANEvPawJgrm0nSXG1UdIeoq5p2")
				.addHeader("Accept", "application/json")
				.build();

		try (Response response = client.newCall(request).execute()) {
			if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

			return response.body().string();
		}
	}
	public static List<String> fetchDeviceIds() throws IOException {
		String apiUrl = "https://gpslog.srifincredit.com/shrms/get_device_ids";
		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder()
				.url(apiUrl)
				.get()
				.addHeader("X-API-TOKEN", "TLQ6lMV3RxujZBhOyKbkcWftANEvPawJgrm0nSXG1UdIeoq5p2")
				.addHeader("Accept", "application/json")
				.build();
	
		try (Response okhttpResponse = client.newCall(request).execute()) {
			if (!okhttpResponse.isSuccessful()) throw new IOException("Unexpected code " + okhttpResponse);
	
			ObjectMapper objectMapper = new ObjectMapper();
			DeviceIdApiResponse response = objectMapper.readValue(okhttpResponse.body().string(), DeviceIdApiResponse.class);
	
			// Extract device IDs from the records
			List<String> deviceIds = new ArrayList<>();
			for (DeviceRecord record : response.getRecords()) {
				deviceIds.add(record.getDeviceid());
			}
			return deviceIds;
		}
	}
	


}
