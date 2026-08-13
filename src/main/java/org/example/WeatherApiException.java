package org.example;

/**
 * Thrown when the weather API call fails for a reason we want to show
 * the user a friendly message for (city not found, bad API key, network
 * issue, etc.) instead of a raw stack trace.
 */
public class WeatherApiException extends Exception {
    public WeatherApiException(String message) {
        super(message);
    }

    public WeatherApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
