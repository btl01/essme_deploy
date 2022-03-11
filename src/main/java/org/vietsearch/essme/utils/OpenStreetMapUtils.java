package org.vietsearch.essme.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.vietsearch.essme.model.expert.FeaturesItem;
import org.vietsearch.essme.model.expert.Geometry;
import org.vietsearch.essme.model.expert.Location;

public class OpenStreetMapUtils {

    private static OpenStreetMapUtils instance = null;
    private JSONParser jsonParser;

    public OpenStreetMapUtils() {
        jsonParser = new JSONParser();
    }

    public static OpenStreetMapUtils getInstance() {
        if (instance == null) {
            instance = new OpenStreetMapUtils();
        }
        return instance;
    }

    private String getRequest(String url) throws Exception {

        final URL obj = new URL(url);
        final HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("GET");

        if (con.getResponseCode() != 200) {
            return null;
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }

    public Map<String, Double> getCoordinates(String address) {
        Map<String, Double> res;
        StringBuffer query;
        String[] split = address.split(" ");
        String queryResult = null;

        query = new StringBuffer();
        res = new HashMap<String, Double>();

        query.append("https://nominatim.openstreetmap.org/search?q=");

        if (split.length == 0) {
            return null;
        }

        for (int i = 0; i < split.length; i++) {
            query.append(split[i]);
            if (i < (split.length - 1)) {
                query.append("+");
            }
        }
        query.append("&format=json&addressdetails=1");


        try {
            queryResult = getRequest(query.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (queryResult == null) {
            return null;
        }

        Object obj = JSONValue.parse(queryResult);


        if (obj instanceof JSONArray) {
            JSONArray array = (JSONArray) obj;
            if (array.size() > 0) {
                JSONObject jsonObject = (JSONObject) array.get(0);

                String lon = (String) jsonObject.get("lon");
                String lat = (String) jsonObject.get("lat");

                res.put("lon", Double.parseDouble(lon));
                res.put("lat", Double.parseDouble(lat));

            }
        }

        return res;
    }

    public  JSONArray addressToJsonArray(String address){
        Map<String, Double> res;
        StringBuffer query;
        String[] split = address.split(" ");
        String queryResult = null;

        query = new StringBuffer();
        res = new HashMap<String, Double>();

        query.append("https://nominatim.openstreetmap.org/search?q=");

        if (split.length == 0) {
            return null;
        }

        for (int i = 0; i < split.length; i++) {
            query.append(split[i]);
            if (i < (split.length - 1)) {
                query.append("+");
            }
        }
        query.append("&format=json&addressdetails=1");


        try {
            queryResult = getRequest(query.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (queryResult == null) {
            return null;
        }

        Object obj = JSONValue.parse(queryResult);


        if (obj instanceof JSONArray) {
            return (JSONArray) obj;
        }

        return new JSONArray();
    }

    public Location addressToLocation(String address){
        JSONArray listLocation = addressToJsonArray(address);
        Location location = new Location();

        if (listLocation.isEmpty())
            return location;

        JSONObject parseLocation = (JSONObject) listLocation.get(0);

        // geometry
        Geometry geometry = new Geometry();
        geometry.setType("Point");
        List<Double> cordidate = new ArrayList<>();
        cordidate.add(Double.parseDouble((String) parseLocation.get("lon")));
        cordidate.add(Double.parseDouble((String) parseLocation.get("lat")));
        geometry.setCoordinates(cordidate);

        // feature
        FeaturesItem featuresItem = new FeaturesItem();
        featuresItem.setGeometry(geometry);
        featuresItem.setType("Feature");

        // feature list
        List<FeaturesItem> featuresItemList = new ArrayList<>();
        featuresItemList.add(featuresItem);

        // location
        location.setFeatures(featuresItemList);
        location.setType("FeatureCollection");


        return  location;
    }


}
