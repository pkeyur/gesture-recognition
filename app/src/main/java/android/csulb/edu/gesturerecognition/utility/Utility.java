package android.csulb.edu.gesturerecognition.utility;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Utility  extends Activity implements TextToSpeech.OnInitListener{

    public static final String LOCATION_FILE_NAME = "LocationStorage";
    public static final String SPEED_DIAL_FILE_NAME = "SpeedDialSettings";
    static Activity activity;
    static Context context;
    public Utility(){

    }
    public Utility(Activity activity, Context context){
        this.activity=activity;
        this.context=context;
    }

    public Map<String, Double> getAddress(Context context, String key, String fileName) {
        SharedPreferences locationDetails = context.getSharedPreferences(fileName , context.MODE_PRIVATE);
        Map<String, Double> address = new HashMap<>();
        double longitude = 0.0;
        double latitude = 0.0;
        if(key == "HOME") {
            longitude = Double.valueOf(locationDetails.getString("homeLongitude", ""));
            latitude = Double.valueOf(locationDetails.getString("homeLatitude", ""));
        }
        else if(key=="WORK") {
            longitude = Double.valueOf(locationDetails.getString("workLongitude", ""));
            latitude = Double.valueOf(locationDetails.getString("workLatitude", ""));
        }
        address.put("longitude", longitude);
        address.put("latitude", latitude);

        return address;
    }

    public String getAddressString(Context context, String key, String fileName) {
        String addressString="";

        SharedPreferences locationDetails = context.getSharedPreferences(Utility.LOCATION_FILE_NAME, context.MODE_PRIVATE);


        if(key.equals("Home")) {
            addressString = locationDetails.getString("homeAddress", "");

        }
        else if(key.equals("Work")) {
            addressString = locationDetails.getString("workAddress", "");
        }
        return addressString;
    }



    public void setAddress(Context context, String key, String address) {
        SharedPreferences locationDetails = context.getSharedPreferences(Utility.LOCATION_FILE_NAME, context.MODE_PRIVATE);
        SharedPreferences.Editor edit = locationDetails.edit();
        //edit.clear();
        address = address.replace('+',' ');
        if(key.equals("Home")) {
            edit.putString("homeAddress", String.valueOf(address));
            Toast.makeText(context,address,Toast.LENGTH_SHORT).show();

        }
        else if(key.equals("Work")) {
            edit.putString("workAddress", String.valueOf(address));
            Toast.makeText(context,address,Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(context,"key is null",Toast.LENGTH_SHORT).show();
        edit.commit();

    }

    public String getSpeedDialNumber(Context context, int key) {
        String addressString="";

        SharedPreferences speedDialDetails = context.getSharedPreferences(Utility.SPEED_DIAL_FILE_NAME, context.MODE_PRIVATE);
        addressString = speedDialDetails.getString(String.valueOf(key), "");
        return addressString;
    }
    public void phoneCall(Context context, String phoneNumber) {

        Intent phoneCallIntent = new Intent(Intent.ACTION_CALL);
        phoneCallIntent.setData(Uri.parse("tel:" + phoneNumber));
        //TODO: Add permission check
phoneCallIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try{
            context.startActivity(phoneCallIntent);
        }catch(Exception e){
            e.printStackTrace();

        }

        /*if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            startActivity(phoneCallIntent);
        }*/
    }
    public void startmapIntent(Context context,String addressString) {
        Uri gmmIntentUri = Uri.parse("google.navigation:q="+addressString);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        mapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (mapIntent.resolveActivity(context.getPackageManager()) != null)
            context.startActivity(mapIntent);
    }
    public void  initTTS(Context context){
        if(ttsSpeaker==null)
        ttsSpeaker =  new TextToSpeech(context, this);
    }
    static TextToSpeech ttsSpeaker;
    public void say(String text2Speak) {
       //TextToSpeech ttsSpeaker =  new TextToSpeech(this);
        ttsSpeaker.setSpeechRate((float) 1.0);
        ttsSpeaker.speak(text2Speak , TextToSpeech.QUEUE_FLUSH, null);
        //context.unregisterReceiver(battery_receiver);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            Locale.setDefault(Locale.US);
        }
    }

}
