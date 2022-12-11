package com.mywebsiteproject.service;

import com.mywebsiteproject.model.CityParameters;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class PollutionService {

    private static final Logger LOGGER = Logger.getLogger(PollutionService.class.getName());
    @Value("${apikey.pollution}")
    private String apiToken;

    public JSONObject getCurrentPollutionDataByCityStateCountry(String cityName, String stateCode, String countryCode) {
        CityParameters cityParameters = getCityLatitudeAndLongitude(cityName, stateCode, countryCode);
        return getCurrentPollutionDataByLatAndLon(cityParameters);
    }

    public JSONObject getCurrentPollutionDataByLatAndLon(CityParameters cityParameters) {
        JSONObject resultJson = null;
        double latitude = cityParameters.getLatitude();
        double longitude = cityParameters.getLongitude();
        try {
            String urlInitial = "https://api.openweathermap.org/data/2.5/air_pollution?lat=" +
                    latitude +
                    "&lon=" +
                    longitude +
                    "&appid=" +
                    apiToken;

            LOGGER.log(Level.INFO, "getCurrentPollutionDataByLatAndLon Initial URL {0}", urlInitial);

            URL url = new URL(urlInitial);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            //Getting the response code
            int responseCode = conn.getResponseCode();

            if (responseCode != 200) {
                throw new RuntimeException("HttpResponseCode: " + responseCode);
            } else {

                Scanner scanner = new Scanner(url.openStream());

                //Write all the JSON data into a string using a scanner
                StringBuilder inLine = new StringBuilder();
                while (scanner.hasNext()) {
                    inLine.append(scanner.nextLine());
                }

                //Close the scanner
                scanner.close();

                //Using the JSON simple library parse the string into a json object
                JSONParser parse = new JSONParser();
                JSONObject dataObj = (JSONObject) parse.parse(String.valueOf(inLine));

                JSONArray array = (JSONArray) dataObj.get("list");
                for (Object o : array) {
                    JSONObject listObj = (JSONObject) o;

                    JSONObject component = (JSONObject) listObj.get("components");
                    JSONObject main = (JSONObject) listObj.get("main");

                    long timestamp = Long.parseLong(listObj.get("dt").toString());
                    Date date = new Date(timestamp * 1000); //in ms

                    Map<String, String> resultString = new HashMap<>();
                    resultString.put("date", date.toString());
                    resultString.put("aqi", main.get("aqi").toString());
                    resultString.put("no2", component.get("no2").toString());
                    resultString.put("pm10", component.get("pm10").toString());
                    resultString.put("o3", component.get("o3").toString());
                    resultString.put("pm2_5", component.get("pm2_5").toString());
                    resultString.put("resultCity", cityParameters.getCity());
                    resultString.put("resultState", cityParameters.getState());
                    resultString.put("resultCountry", cityParameters.getCountry());
                    LOGGER.log(Level.INFO, "resultString: {0}", resultString);

                    resultJson = new JSONObject(resultString);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultJson;
    }

    public CityParameters getCityLatitudeAndLongitude(String cityName, String stateCode, String countryCode) {
        String resultLimit = "1";
        CityParameters cityParameters = new CityParameters();
        try {
            StringBuilder urlInitial = new StringBuilder();
            urlInitial.append("https://api.openweathermap.org/geo/1.0/direct?q=");
            urlInitial.append(cityName);
            urlInitial.append(",");
            urlInitial.append(stateCode);
            urlInitial.append(",");
            urlInitial.append(countryCode);
            urlInitial.append("&limit=");
            urlInitial.append(resultLimit);
            urlInitial.append("&appid=");
            urlInitial.append(apiToken);

            LOGGER.log(Level.INFO, "getCityLatitudeAndLongitude Initial URL {0}", urlInitial);
            URL url = new URL(urlInitial.toString());

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            //Getting the response code
            int responseCode = conn.getResponseCode();

            if (responseCode != 200) {
                throw new RuntimeException("HttpResponseCode: " + responseCode);
            } else {

                Scanner scanner = new Scanner(url.openStream());

                //Write all the JSON data into a string using a scanner
                StringBuilder inLine = new StringBuilder();
                while (scanner.hasNext()) {
                    inLine.append(scanner.nextLine());
                }

                //Close the scanner
                scanner.close();

                //Using the JSON simple library parse the string into a json object
                LOGGER.log(Level.INFO, "Array to parse {0}", inLine);
                JSONParser parser = new JSONParser();
                JSONArray dataObjArray = (JSONArray) parser.parse(String.valueOf(inLine));

                JSONObject dataObj = (JSONObject) dataObjArray.get(0); // 0 as we have resultLimit = 1
                LOGGER.log(Level.INFO, "dataObj to parse {0}", dataObj);

                cityParameters.setCity((String) dataObj.get("name"));
                cityParameters.setState((String) dataObj.get("state"));
                cityParameters.setCountry((String) dataObj.get("country"));
                cityParameters.setLatitude((Double) dataObj.get("lat"));
                cityParameters.setLongitude((Double) dataObj.get("lon"));
                LOGGER.log(Level.INFO, "city parameters: {0}", cityParameters.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cityParameters;
    }
}