# MyWeatherApp

A Java Swing desktop weather app. Search any city — in India or anywhere
in the world — and see current conditions with a clean, modern GUI.

## What's new

- **Full Swing GUI** (`WeatherAppGUI.java`) — no more console-only output.
- **Search any city, globally** — type a plain city name ("Thane", "Tokyo",
  "Cairo") or `City,CountryCode` (e.g. `Thane,IN`, `Paris,FR`) to disambiguate
  common names.
- **Quick-access buttons** for popular Indian cities (Thane, Mumbai, Delhi,
  Bengaluru, Pune, Chennai, Kolkata, Hyderabad).
- **More weather data**: feels-like temperature, min/max temperature,
  humidity, wind speed, pressure, visibility, sunrise and sunset (shown in
  each city's own local time).
- **Custom-drawn weather icons** (sun / cloud / rain / thunderstorm / snow /
  fog) — no image files or extra downloads needed.
- **Light / Dark theme toggle.**
- **Background loading** — the network call runs off the UI thread
  (`SwingWorker`), so the window never freezes while fetching data.
- **Friendlier errors** — city not found, bad API key, and network issues
  each show a clear message instead of a stack trace.

## Files

| File | Purpose |
|---|---|
| `Main.java` | Entry point — just launches the GUI |
| `WeatherAppGUI.java` | The Swing window: search bar, quick cities, weather card, details grid, theme toggle |
| `WeatherApiClient.java` | Calls the OpenWeatherMap API and parses the response |
| `WeatherData.java` | Plain data holder for all the weather fields |
| `WeatherIconPanel.java` | Draws the weather icon based on current conditions |
| `WeatherApiException.java` | Custom exception for user-friendly error messages |

## Running it

```bash
mvn compile exec:java
```

or build a standalone runnable jar (bundles the `org.json` dependency):

```bash
mvn package
java -jar target/MyWeatherApp-1.0-SNAPSHOT.jar
```

## A note on the API key

The OpenWeatherMap API key is currently hardcoded in `WeatherApiClient.java`
(same key the original project used). That's fine for local testing, but if
you ever push this to a public GitHub repo or share it, it's worth moving it
out of source code — e.g. read it from an environment variable:

```java
private static final String API_KEY = System.getenv("OPENWEATHER_API_KEY");
```

and set it before running:

```bash
export OPENWEATHER_API_KEY=your_key_here
```

## Ideas for next steps

- 5-day forecast (OpenWeatherMap's `/forecast` endpoint)
- Remember the last searched city on next launch
- "Use my current location" via IP-based geolocation
- Unit toggle (°C / °F)
