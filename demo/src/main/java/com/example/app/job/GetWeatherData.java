package com.example.app.job;

import com.example.app.model.Weather;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Component
@EnableScheduling
public class GetWeatherData {

    //URI used to create a POST request to send weather data to database
    private final String POST_REQUEST_URI = "http://localhost:8080/api/weather";
    // URL from where weather data is taken from
    private final String WEATHER_DATA_URL = "https://www.ilmateenistus.ee/ilma_andmed/xml/observations.php";
    // Stations from where we want the data
    private final List<String> IMPORTANT_STATIONS = new ArrayList<>(Arrays.asList("Tallinn-Harku", "Tartu-Tõravere", "Pärnu"));

    private Document openFile() throws RuntimeException, IOException, ParserConfigurationException, SAXException {
        // Opens the XML file from URL. Returns Document object of said file.
        URL url = new URL(WEATHER_DATA_URL);
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(url.openStream());
        document.getDocumentElement().normalize();
        return document;
    }

    private ArrayList<Weather> getWeatherObjects (Document document) {
        // From given XML document, finds stations that interests us.
        // Creates a Weather object for every said station and returns them.
        ArrayList<Weather> weathers = new ArrayList<>();

        long timestamp = Long.parseLong(document.getDocumentElement().getAttribute("timestamp"));
        NodeList nodeList = document.getElementsByTagName("station");
        // Goes through stations
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element element = (Element) nodeList.item(i);
            // Creates weather object if we have found a needed station
            String name = element.getElementsByTagName("name").item(0).getTextContent();
            if (IMPORTANT_STATIONS.contains(name)) {
                Weather weather = new Weather();
                // Add already found attributes to weather object
                weather.setStationName(name);
                weather.setTimestamp(timestamp);
                // Add other attributes to weather object
                weather.setWmoCode(element.getElementsByTagName("wmocode").item(0).getTextContent());
                weather.setAirTemperature(Double.parseDouble(element.getElementsByTagName("airtemperature").item(0).getTextContent()));
                weather.setWindSpeed(Double.parseDouble(element.getElementsByTagName("windspeed").item(0).getTextContent()));
                weather.setWeatherPhenomenon(element.getElementsByTagName("phenomenon").item(0).getTextContent());

                weathers.add(weather);
            }
        }
        return weathers;
    }

    private void addWeatherToDatabase (Weather weather) throws IOException, InterruptedException {
        // Sends Weather object to database.
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(weather);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .uri(URI.create(POST_REQUEST_URI))
                .header("Content-Type", "application/json")
                .build();
        HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Gets weather data from needed stations and sends it to database every hour at HH:15.
     */
    @Scheduled(cron = "0 15 * * * *")
    public void getWeatherData() {
        try {
            Document document = openFile();
            ArrayList<Weather> weathers = getWeatherObjects(document);
            for (Weather weather : weathers) {
                addWeatherToDatabase(weather);
            }
        } catch (IOException | SAXException | ParserConfigurationException e) {
            System.out.println("Something went wrong with opening the URL!");
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            System.out.println("Something went wrong with sending the data to database!");
            throw new RuntimeException(e);
        }
    }
}
