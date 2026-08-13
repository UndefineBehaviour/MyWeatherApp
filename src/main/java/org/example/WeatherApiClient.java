package org.example;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Talks to the OpenWeatherMap "current weather" endpoint.
 *
 * Works for ANY city worldwide. To disambiguate common city names
 * (e.g. there are multiple "Springfield"s, multiple "Cordoba"s, etc.)
 * you can pass "City,CC" where CC is the ISO 3166 country code,
 * for example "Thane,IN", "Paris,FR", "Springfield,US".
 * A plain city name like "Mumbai" or "Tokyo" also works fine.
 */
public class WeatherApiClient {

    // API key is read from the OPENWEATHER_API_KEY environment variable —
    // never hardcode a real key in source that goes on a public repo.
    private static final String API_KEY = System.getenv("OPENWEATHER_API_KEY");
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";
    private static final int TIMEOUT_MS = 8000;

    public static WeatherData fetchWeatherData(String cityQuery) throws WeatherApiException {
        if (cityQuery == null || cityQuery.isBlank()) {
            throw new WeatherApiException("Please enter a city name.");
        }
        if (API_KEY == null || API_KEY.isBlank()) {
            throw new WeatherApiException(
                "Missing API key. Set the OPENWEATHER_API_KEY environment variable " +
                "before running the app (see README).");
        }

        HttpURLConnection conn = null;
        try {
            String encodedCity = URLEncoder.encode(cityQuery.trim(), StandardCharsets.UTF_8);
            String requestUrl = BASE_URL + "?q=" + encodedCity + "&appid=" + API_KEY + "&units=metric";

            URL url = URI.create(requestUrl).toURL();
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(TIMEOUT_MS);
            conn.setReadTimeout(TIMEOUT_MS);

            int responseCode = conn.getResponseCode();

            if (responseCode == 200) {
                String body = readStream(conn.getInputStream());
                return parseWeather(body);
            } else if (responseCode == 404) {
                throw new WeatherApiException(
                        "Couldn't find \"" + cityQuery + "\". Check the spelling, or try \"City,CountryCode\" "
                                + "(e.g. \"Thane,IN\") if the name is common.");
            } else if (responseCode == 401) {
                throw new WeatherApiException("Invalid or expired API key. Please check your OpenWeatherMap API key.");
            } else if (responseCode == 429) {
                throw new WeatherApiException("Too many requests right now. Please wait a moment and try again.");
            } else {
                String errBody = conn.getErrorStream() != null ? readStream(conn.getErrorStream()) : "";
                throw new WeatherApiException("Weather service error (HTTP " + responseCode + "). " + errBody);
            }

        } catch (IOException e) {
            throw new WeatherApiException("Network error: couldn't reach the weather service. "
                    + "Check your internet connection and try again.", e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private static String readStream(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }

    private static WeatherData parseWeather(String jsonResponse) {
        JSONObject obj = new JSONObject(jsonResponse);
        WeatherData data = new WeatherData();

        data.setCityName(obj.optString("name", "Unknown"));

        JSONObject sys = obj.optJSONObject("sys");
        if (sys != null) {
            data.setCountry(sys.optString("country", ""));
            data.setSunriseEpoch(sys.optLong("sunrise", 0));
            data.setSunsetEpoch(sys.optLong("sunset", 0));
        }

        JSONObject main = obj.getJSONObject("main");
        data.setTemperature(main.getDouble("temp"));
        data.setFeelsLike(main.optDouble("feels_like", main.getDouble("temp")));
        data.setTempMin(main.optDouble("temp_min", main.getDouble("temp")));
        data.setTempMax(main.optDouble("temp_max", main.getDouble("temp")));
        data.setHumidity(main.optInt("humidity", 0));
        data.setPressure(main.optInt("pressure", 0));

        JSONObject wind = obj.optJSONObject("wind");
        if (wind != null) {
            data.setWindSpeedMs(wind.optDouble("speed", 0));
        }

        data.setVisibilityMeters(obj.optInt("visibility", 0));
        data.setTimezoneOffsetSeconds(obj.optInt("timezone", 0));

        JSONArray weatherArr = obj.getJSONArray("weather");
        if (weatherArr.length() > 0) {
            JSONObject weather = weatherArr.getJSONObject(0);
            data.setWeatherMain(weather.optString("main", ""));
            data.setWeatherDescription(weather.optString("description", ""));
            data.setWeatherIconCode(weather.optString("icon", ""));
        }

        return data;
    }
}
