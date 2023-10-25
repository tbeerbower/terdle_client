package com.techelevator;

import com.techelevator.utils.BasicConsole;
import com.techelevator.utils.SystemInOutConsole;

import java.util.Optional;

/**
 * Application is the class that launches the TErdle application.
 * 
 * It creates the objects required by the controller object and passes them into the constructor. 
 * (This is an example of dependency injection.)
 */
public class Application {

    // For communication with the server REST API
    private static final String API_BASE_URL =
            Optional.ofNullable(System.getenv("API_BASE_URL")).orElse("http://localhost:9000/");

    public static void main(String[] args) {

        // Create the basic i/o mechanism (the console)
        BasicConsole systemInOutConsole = new SystemInOutConsole();

        // The controller manages the program flow. Create a controller and call its run() method to start the menu loop.
        ApplicationController controller = new ApplicationController(systemInOutConsole, API_BASE_URL);
        controller.run();
    }
}
