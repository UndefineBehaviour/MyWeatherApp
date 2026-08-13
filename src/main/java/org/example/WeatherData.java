package org.example;

/**
 * Simple data holder for all the weather fields we display in the GUI.
 */
public class WeatherData {

    private String cityName;
    private String country;

    private double temperature;
    private double feelsLike;
    private double tempMin;
    private double tempMax;

    private int humidity;
    private int pressure;
    private double windSpeedMs;   // meters/second, as returned by the API
    private int visibilityMeters;

    private long sunriseEpoch;    // seconds, UTC
    private long sunsetEpoch;     // seconds, UTC
    private int timezoneOffsetSeconds; // shift from UTC for this city

    private String weatherMain;        // e.g. "Clear", "Rain", "Clouds"
    private String weatherDescription; // e.g. "light rain"
    private String weatherIconCode;    // e.g. "10d"

    public String getCityName() { return cityName; }
    public void setCityName(String cityName) { this.cityName = cityName; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }

    public double getFeelsLike() { return feelsLike; }
    public void setFeelsLike(double feelsLike) { this.feelsLike = feelsLike; }

    public double getTempMin() { return tempMin; }
    public void setTempMin(double tempMin) { this.tempMin = tempMin; }

    public double getTempMax() { return tempMax; }
    public void setTempMax(double tempMax) { this.tempMax = tempMax; }

    public int getHumidity() { return humidity; }
    public void setHumidity(int humidity) { this.humidity = humidity; }

    public int getPressure() { return pressure; }
    public void setPressure(int pressure) { this.pressure = pressure; }

    public double getWindSpeedMs() { return windSpeedMs; }
    public void setWindSpeedMs(double windSpeedMs) { this.windSpeedMs = windSpeedMs; }

    public double getWindSpeedKmh() { return windSpeedMs * 3.6; }

    public int getVisibilityMeters() { return visibilityMeters; }
    public void setVisibilityMeters(int visibilityMeters) { this.visibilityMeters = visibilityMeters; }

    public double getVisibilityKm() { return visibilityMeters / 1000.0; }

    public long getSunriseEpoch() { return sunriseEpoch; }
    public void setSunriseEpoch(long sunriseEpoch) { this.sunriseEpoch = sunriseEpoch; }

    public long getSunsetEpoch() { return sunsetEpoch; }
    public void setSunsetEpoch(long sunsetEpoch) { this.sunsetEpoch = sunsetEpoch; }

    public int getTimezoneOffsetSeconds() { return timezoneOffsetSeconds; }
    public void setTimezoneOffsetSeconds(int timezoneOffsetSeconds) { this.timezoneOffsetSeconds = timezoneOffsetSeconds; }

    public String getWeatherMain() { return weatherMain; }
    public void setWeatherMain(String weatherMain) { this.weatherMain = weatherMain; }

    public String getWeatherDescription() { return weatherDescription; }
    public void setWeatherDescription(String weatherDescription) { this.weatherDescription = weatherDescription; }

    public String getWeatherIconCode() { return weatherIconCode; }
    public void setWeatherIconCode(String weatherIconCode) { this.weatherIconCode = weatherIconCode; }
}
