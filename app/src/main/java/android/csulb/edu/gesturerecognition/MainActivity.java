package android.csulb.edu.gesturerecognition;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.csulb.edu.gesturerecognition.utility.BatteryOperations;
import android.csulb.edu.gesturerecognition.utility.Utility;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity  implements
        GestureOverlayView.OnGesturePerformedListener, GestureOverlayView.OnGesturingListener,
        TextToSpeech.OnInitListener {

    private static final String LOG_TAG = MainActivity.class
            .getName();
    private GestureLibrary gestureLib;
    private Location currentLocation;
    private LocationManager mLocationManager;
    private String locationProvider;
    private Utility utility = new Utility(this, this);
    VibratorUI vUI;
    TextToSpeech ttsSpeaker;

    /** Called when the activity is first created. */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        utility.initTTS(getApplicationContext());

        GestureOverlayView gestureOverlayView = new GestureOverlayView(this);
        View inflate = getLayoutInflater().inflate(R.layout.activity_main,
                null);
        gestureOverlayView.addView(inflate);
        gestureOverlayView.addOnGesturePerformedListener(this);
        gestureOverlayView.setGestureColor(Color.CYAN);
        gestureOverlayView.setUncertainGestureColor(Color.GRAY);
        gestureOverlayView.addOnGesturingListener(this);
        gestureLib = GestureLibraries.fromRawResource(this,
                R.raw.gestures);
        if (!gestureLib.load()) {
            finish();
        }
        setContentView(gestureOverlayView);
        if(getIntent().hasExtra("Purpose")){
            if(getIntent().getStringExtra("Purpose").equals("Google_Search")) {
                performSpeechToText();

            }
        }
        vUI = new VibratorUI((Vibrator) getSystemService(VIBRATOR_SERVICE));
        ttsSpeaker = new TextToSpeech(this, this);
        ttsSpeaker.setSpeechRate((float) 1.0);

    }

    @Override
    protected void onResume() {
//		initLocationService();
//		mLocationManager.requestLocationUpdates(locationProvider, 4000, 100,
//				this);
        super.onResume();
    }

    @Override
    protected void onDestroy() {

        if (ttsSpeaker != null) {
            ttsSpeaker.stop();
            ttsSpeaker.shutdown();
        }
        super.onDestroy();
    }

    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
        ArrayList<Prediction> predictions = gestureLib.recognize(gesture);
        // for (Prediction prediction : predictions) {
        if (predictions.size() > 0) {
            if (predictions.get(0).score > 2) {
                // Toast.makeText(this, prediction.name, Toast.LENGTH_SHORT)
                // .show();
                String locationData;
                String number;
                AudioManager am;
                switch (predictions.get(0).name) {

                    /*case "Home":
                        Toast.makeText(this, "Gesture Home performed.",
                                Toast.LENGTH_SHORT).show();
                        locationData = utility.getAddressString(this,"Home", Utility.LOCATION_FILE_NAME);
                        utility.startmapIntent(getApplicationContext(), locationData);
                        break;

                    case "Work":
                        Toast.makeText(this, "Gesture Work Performed.",
                                Toast.LENGTH_SHORT).show();
                        locationData = utility.getAddressString(this,"Work", Utility.LOCATION_FILE_NAME);
                        startmapIntent(locationData);
                        break;
                    case "one":
                       number = utility.getSpeedDialNumber(this,0);
                        Toast.makeText(getApplicationContext(),"Calling to "+number,Toast.LENGTH_SHORT).show();
                        utility.phoneCall(MainActivity.this,number);
                        break;

                    case "two":
                        number = utility.getSpeedDialNumber(this,1);
                        utility.phoneCall(MainActivity.this,number);
                        break;

                    case "three":
                        number = utility.getSpeedDialNumber(this,2);
                        utility.phoneCall(MainActivity.this,number);
                        break;

                    case "four":
                        number = utility.getSpeedDialNumber(this,3);
                        utility.phoneCall(MainActivity.this,number);
                        break;

                    case "battery":
                        Toast.makeText(this, "Gesture Battery Performed.",
                                Toast.LENGTH_SHORT).show();
                        new BatteryOperations().registerBatteryLevelReceiver(this);
                        //battery_receiver.;
                        break;

                    case "circle":
                        Toast.makeText(this, "Gesture Circle Performed.",
                                Toast.LENGTH_SHORT).show();

                        Intent contactIntent = new Intent(
                                MainActivity.this,
                                ContactActivity.class);
                        startActivity(contactIntent);

                        break;

                    case "map":
                        Toast.makeText(this, "Gesture Map Performed.",
                                Toast.LENGTH_SHORT).show();
                        String uri = String.format(Locale.ENGLISH, "geo:%f,%f",34.068921,-118.4451811);
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        startActivity(intent);
                        break;*/
                    case "Home":
                        Toast.makeText(this, "Gesture Home performed.",
                                Toast.LENGTH_SHORT).show();
                        locationData = utility.getAddressString(this,"Home", Utility.LOCATION_FILE_NAME);
                        utility.startmapIntent(this, locationData);
                        break;

                    case "Work":
                        Toast.makeText(this, "Gesture Work Performed.",Toast.LENGTH_SHORT).show();
                        locationData = utility.getAddressString(this,"Work", Utility.LOCATION_FILE_NAME);
                        utility.startmapIntent(this, locationData);
                        break;
                    case "one":
                        number = utility.getSpeedDialNumber(this,0);
                        Toast.makeText(getApplicationContext(),"Calling to "+number,Toast.LENGTH_SHORT).show();
                        utility.phoneCall(this,number);
                        break;

                    case "two":
                        number = utility.getSpeedDialNumber(this,1);
                        utility.phoneCall(this,number);
                        break;

                    case "three":
                        number = utility.getSpeedDialNumber(this,2);
                        utility.phoneCall(this,number);
                        break;

                    case "four":
                        number = utility.getSpeedDialNumber(this,3);
                        utility.phoneCall(this,number);
                        break;

                    case "battery":
                        Toast.makeText(this, "Gesture Battery Performed.",
                                Toast.LENGTH_SHORT).show();
                        new BatteryOperations().registerBatteryLevelReceiver(getApplicationContext());
                        //battery_receiver.;
                        break;

                    case "circle":
                        Toast.makeText(this, "Gesture Circle Performed.",
                                Toast.LENGTH_SHORT).show();
                        utility.say("Please speak out name of the contact");
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Intent contactIntent = new Intent(
                                this,
                                ContactActivity.class);
                        startActivity(contactIntent);

                        break;

                    case "map":
                        Toast.makeText(this, "Gesture Performed for Music Player.",
                                Toast.LENGTH_SHORT).show();
                        //TODO - put dynamic address
                       /* String uri = String.format(Locale.ENGLISH, "geo:%f,%f",34.068921,-118.4451811);
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        startActivity(intent);*/
                        Intent intent = new Intent(MediaStore.INTENT_ACTION_MUSIC_PLAYER);
                        startActivity(intent);
                        break;
                    case "sms":
                        Toast.makeText(this, "Gesture Search Performed.",
                                Toast.LENGTH_SHORT).show();
                        utility.say("Please speak out google search string");
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        performSpeechToText();
                        break;

                    case "time":
                        Toast.makeText(this, "Gesture Time Performed.",
                                Toast.LENGTH_SHORT).show();
                        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                        utility.initTTS(getApplicationContext());
                        utility.say("Current Date and Time is "+ currentDateTimeString);
                        break;

                    case "vibrationmode":
                        am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
                        if(am.getRingerMode()==am.RINGER_MODE_NORMAL) {
                            am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                            Toast.makeText(getApplicationContext(), "Ringer Mode is set to Vibration",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                            Toast.makeText(getApplicationContext(), "Ringer Mode is set to Normal",Toast.LENGTH_SHORT).show();
                        }
                        break;
                    /*case GlobalValues.GESTURE_NAVIGATE_RIGHT:
                        Intent contactIntent = new Intent(
                                MainActivity.this,
                                ContactActivity.class);
                        startActivity(contactIntent);
                        break;
                    case GlobalValues.GESTURE_NAVIGATE_UP:
                        Toast.makeText(this, "Gesture up performed.",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case GlobalValues.GESTURE_NAVIGATE_DOWN:
                        Toast.makeText(this, "Gesture down performed.",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case GlobalValues.GESTURE_NAVIGATE_ANTI_CLOCK_ROUND:
                        Toast.makeText(this, "Gesture search performed.",
                                Toast.LENGTH_SHORT).show();
                        break;*/
                }

                // Toast.makeText(this, predictions.get(0).name,
                // Toast.LENGTH_SHORT).show();
            }
            else {
                vUI.vibrate(VibratorUI.MEDIUM_GAP);
            }

        }
    }
    void startmapIntent(String addressString) {
        Uri gmmIntentUri = Uri.parse("google.navigation:q="+addressString);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null)
            startActivity(mapIntent);
    }

    public void onGesturingEnded(GestureOverlayView overlay) {
        // vUI.vibrate(VibratorUI.DOT);
    }

    public void onGesturingStarted(GestureOverlayView overlay) {
        vUI.vibrate(VibratorUI.DOT);
    }



    public void onInit(int arg0) {
       // say("Welcome to project third eye!");

    }

    private void say(String text2Speak) {
        ttsSpeaker.speak(text2Speak, TextToSpeech.QUEUE_FLUSH, null);

    }

    /*private BroadcastReceiver battery_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isPresent = intent.getBooleanExtra("present", false);
            // String technology = intent.getStringExtra("technology");
            int plugged = intent.getIntExtra("plugged", -1);
            int scale = intent.getIntExtra("scale", -1);
            // int health = intent.getIntExtra("health", 0);
            int status = intent.getIntExtra("status", 0);
            int rawlevel = intent.getIntExtra("level", -1);
            int level = 0;

            Bundle bundle = intent.getExtras();

            Log.d(LOG_TAG, "Battery level:" + bundle.toString());

            if (isPresent) {
                if (rawlevel >= 0 && scale > 0) {
                    level = (rawlevel * 100) / scale;
                }

                String info = "Battery Level is" + level + "%\n";

                info += ("Battery is " + getPlugTypeString(plugged) + "\n");

                info += ("and Status is " + getStatusString(status) + "\n");

                setBatteryLevelText(info);// );
            } else {
                setBatteryLevelText("Battery not present!!!");
            }
        }
    };*/

    /*private String getPlugTypeString(int plugged) {
        String plugType = "Unplugged";

        switch (plugged) {
            case BatteryManager.BATTERY_PLUGGED_AC:
                plugType = "Plugged with AC adapter.";
                break;
            case BatteryManager.BATTERY_PLUGGED_USB:
                plugType = "Plugged with USB.";
                break;
        }

        return plugType;
    }

    private String getStatusString(int status) {
        String statusString = "Unknown";

        switch (status) {
            case BatteryManager.BATTERY_STATUS_CHARGING:
                statusString = "Charging";
                break;
            case BatteryManager.BATTERY_STATUS_DISCHARGING:
                statusString = "Discharging";
                break;
            case BatteryManager.BATTERY_STATUS_FULL:
                statusString = "Full";
                break;
            case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                statusString = "Not Charging";
                break;
        }

        return statusString;
    }

    private void setBatteryLevelText(String text) {
        say(text);
    }

    private void registerBatteryLevelReceiver() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(battery_receiver, filter);
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                return true;

        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }


    protected static final int REQUEST_SPEECH = 1;
    protected static final int REQUEST_MESSAGE = 2;
    public static String speechToText="";
    public void performSpeechToText() {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-IN");

        try {
            startActivityForResult(intent, REQUEST_SPEECH);

        } catch (ActivityNotFoundException a) {
            Toast t = Toast.makeText(this,
                    "Opps! Your device doesn't support Speech to Text",
                    Toast.LENGTH_SHORT);
            t.show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_SPEECH:
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> text = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                        speechToText = text.get(0);

                        Uri uri1 = Uri.parse("http://www.google.com/#q="+speechToText);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri1);
                        startActivity(intent);

                } else {
                    say("Operation successfully canceled by you!");
                }
                break;
            case REQUEST_MESSAGE:
                break;
        }

    }

}