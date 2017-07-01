package android.csulb.edu.gesturerecognition;

import android.app.Activity;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

public class SettingAccess extends Activity{

    public static final String FILE_NAME = "AddressSettings";

    private static final String GOOGLE_GEOCODE_URL = "https://maps.googleapis.com/maps/api/geocode/json?address=";



//    ⁠⁠⁠void setAddress(String key, double longitude, double latitude) {
//
//        SharedPreferences locationDetails = getSharedPreferences(FILE_NAME, MODE_PRIVATE);
//        SharedPreferences.Editor edit = locationDetails.edit();
//        edit.clear();
//        switch(key)
//        {
//            case "HOME":
//                edit.putString("homeLongitude", String.valueOf(longitude));
//                edit.putString("homeLatitude", String.valueOf(latitude));
//                break;
//            case "WORK":
//                edit.putString("workLongitude", String.valueOf(longitude));
//                edit.putString("workLatitude", String.valueOf(latitude));
//                break;
//        }
//        edit.apply();
//    }
//
//    Map<String, Double> getAddress(String key) {
//        SharedPreferences locationDetails = getSharedPreferences(FILE_NAME , MODE_PRIVATE);
//        Map<String, Double> address = new HashMap<>();
//        double longitude = 0.0;
//        double latitude = 0.0;
//        switch (key)
//        {
//            case "HOME":
//                longitude = Double.valueOf(locationDetails.getString("homeLongitude", ""));
//                latitude = Double.valueOf(locationDetails.getString("homeLatitude", ""));
//                break;
//            case "WORK":
//                longitude = Double.valueOf(locationDetails.getString("workLongitude", ""));
//                latitude = Double.valueOf(locationDetails.getString("workLatitude", ""));
//                break;
//        }
//        address.put("longitude", longitude);
//        address.put("latitude", latitude);
//        return address;
//    }
//
//    boolean validateAddress(String address) {
//        boolean isValidated = false;
//        String newAddress = GOOGLE_GEOCODE_URL + converAddressFormat(address);
//    return isValidated;
//    }
//
//    String converAddressFormat(String address) {
//        return address.replace(' ','+');
//    }
}
