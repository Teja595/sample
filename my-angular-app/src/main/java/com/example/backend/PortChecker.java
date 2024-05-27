
package com.example.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class PortChecker {

    @Autowired
    private Environment env;

    public void checkPort() {
        // Get the local server port
        String port = env.getProperty("local.server.port");
        System.out.println("Application is running on port: " + port);
    }
}
