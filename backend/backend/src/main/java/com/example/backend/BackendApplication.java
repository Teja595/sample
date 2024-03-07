package com.example.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import java.io.IOException;
import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.backend.DatabaseInserter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
// /home/mvenkata/get_users_data/backend/backend/src/main/java/com/example/backend/DatabaseInserter
@SpringBootApplication
public class BackendApplication {
	@Autowired
	private DatabaseInserter databaseInserter;
	
		@Autowired
		private Environment env;
	
	
	public static void main(String[] args) throws Exception {
		
		SpringApplication.run(BackendApplication.class, args);
	}
@Bean
	public CommandLineRunner commandLineRunner(DatabaseInserter databaseInserter) {
		checkPort();
		return args -> {
			Scanner scanner = new Scanner(System.in);
			int page = 1; // Starting page
			final int rowsPerPage = 10;
			boolean continueFetching = true;
//			GeoLocationService geoLocationService = new GeoLocationService();
			while (continueFetching) {
				System.out.println("Fetching data for page: " + page);
				String data = fetchGeoLocationData(page, rowsPerPage);
			databaseInserter.insertData(data);
//				geoLocationService.insertData(data);
				System.out.println(data); // Display the fetched data

				System.out.println("Fetch next page? (yes/no): ");
				String userInput = scanner.nextLine();

				if (!"yes".equalsIgnoreCase(userInput)) {
					continueFetching = false;
				} else {
					page++; // Increment to fetch the next page
				}
			}

			System.out.println("Finished fetching data.");
			
		};
		}
	public static String fetchGeoLocationData(int page,int rows) throws IOException {
		String url = "https://gpslog.srifincredit.com/shrms/get_device_location";

		OkHttpClient client = new OkHttpClient();
		ObjectMapper objectMapper = new ObjectMapper(); // Jackson's ObjectMapper
		long currentTimeInSeconds = Instant.now().getEpochSecond();

		long previousDayTimeInSeconds = Instant.now().minus(1, ChronoUnit.DAYS).getEpochSecond();
		Map<String, Object> requestBodyMap = new HashMap<>();
		requestBodyMap.put("deviceID", "7052387845");
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
	public void checkPort() {
		// Get the local server port
		String port = env.getProperty("local.server.port");
		System.out.println("Application is running on port: " + port);
	}

}
