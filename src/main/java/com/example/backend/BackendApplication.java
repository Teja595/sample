package com.example.backend;

import org.springframework.scheduling.annotation.EnableScheduling;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.system.SystemProperties;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
@EnableScheduling
public class BackendApplication {
    // mvn spring-boot:run
    @Autowired
    private DatabaseInserter databaseInserter;

    @Autowired
    private Environment env;

    public static void main(String[] args) throws Exception {
        //   BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        //         String rawPassword = "password1"; // Your raw password
        //         String encodedPassword = passwordEncoder.encode(rawPassword);
        // System.out.println(encodedPassword);
        SpringApplication.run(BackendApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            // Initial data fetch when application starts
            // dataFetchService().fetchData();
			System.out.println("Application started...");
        };
    }

    @Bean
    public DataFetchService dataFetchService() {
        return new DataFetchService(databaseInserter, new ObjectMapper());
    }
}
