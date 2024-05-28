package com.example.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
// src/main/java/com/example/backend/DataFetchService .java

@Service
public class DataFetchService {

    private final DatabaseInserter databaseInserter;
    private final ObjectMapper objectMapper;

    public DataFetchService(DatabaseInserter databaseInserter, ObjectMapper objectMapper) {
        this.databaseInserter = databaseInserter;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedRate = 450000) // Run every hour
    public void fetchData() {
        try {
            // databaseInserter.clearDatabase();
            List<String> deviceIds = fetchDeviceIds();
            // System.out.println("Fetched Device IDs: " + deviceIds.size());

            ExecutorService executor = Executors.newFixedThreadPool(10);

            for (String deviceId : deviceIds) {
                executor.submit(() -> processDevice(deviceId, databaseInserter, objectMapper));
            }

            executor.shutdown();
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

            System.out.println("Finished fetching data.");
        } catch (Exception e) {
            System.out.println("Error occurred while fetching data: " + e.getMessage());
        }
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
                continueFetching = handleIOException(deviceId, page, e);
            }
        }
    }

    private boolean handleIOException(String deviceId, int page, IOException e) {
        System.out.println("Retrying for device ID: " + deviceId + " at page " + page);
        try {
            Thread.sleep(2000); // Wait for 2 seconds before retrying
            String data = fetchGeoLocationData(page, 10000, deviceId);
            GeoLocationDataResponse response = new ObjectMapper().readValue(data, GeoLocationDataResponse.class);
            return response.hasRecords();
        } catch (IOException | InterruptedException ex) {
            System.out.println("Retry failed for device ID: " + deviceId + " at page " + page + ": " + ex.getMessage());
            return false;
        }
    }

    private static OkHttpClient createHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    public static String fetchGeoLocationData(int page, int rows, String deviceId) throws IOException {
        String url = "https://gpslog.srifincredit.com/shrms/get_device_location";

        OkHttpClient client = createHttpClient();
        ObjectMapper objectMapper = new ObjectMapper();
        long currentTimeInSeconds = Instant.now().getEpochSecond();
        long previousDayTimeInSeconds = Instant.now().minus(1, ChronoUnit.DAYS).getEpochSecond();

        Map<String, Object> requestBodyMap = new HashMap<>();
        requestBodyMap.put("deviceID", deviceId);
        requestBodyMap.put("page", page);
        requestBodyMap.put("rows", rows);
        requestBodyMap.put("ts_from", previousDayTimeInSeconds);
        requestBodyMap.put("ts_to", currentTimeInSeconds);

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

            List<String> deviceIds = new ArrayList<>();
            for (DeviceRecord record : response.getRecords()) {
                deviceIds.add(record.getDeviceId());
            }
            return deviceIds;
        }
    }
}
