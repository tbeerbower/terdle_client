package com.techelevator.utils;

/**
 * The class BasicLoggerException defines a custom exception the application uses to indicate
 * problems with writing to the log files.
 *
 * Note that by extending RuntimeException, there is no need to use try/catch blocks throughout
 * the code everywhere that we write to the application log file.
 */
public class BasicLoggerException extends RuntimeException {

	public BasicLoggerException(String message) {
		super(message);
	}
}
