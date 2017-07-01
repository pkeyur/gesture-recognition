package android.csulb.edu.gesturerecognition;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.csulb.edu.gesturerecognition.utility.BatteryOperations;
import android.csulb.edu.gesturerecognition.utility.Utility;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;


public class FloatingBubbleService extends Service implements GestureOverlayView.OnGesturePerformedListener, TextToSpeech.OnInitListener {
    public final static int REQUEST_CODE = 1010101;
    //private WindowManager windowManager;
    //private ImageView floatingFaceBubble;
    //The root element of the collapsed view layout
    View collapsedView;
    //The root element of the expanded view layout
    View expandedView ;
    private View mFloatingView;
    private WindowManager mWindowManager;
    private GestureLibrary gestureLib;
    Utility utility= new Utility();
    VibratorUI vUI;
    TextToSpeech ttsSpeaker;

    @Override
    public void onCreate() {
        super.onCreate();
        //floatingFaceBubble = new ImageView(this)
        try {
            mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget,null);
        }catch (Exception e){
            e.printStackTrace();
        }
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        //Specify the view position
        params.gravity = Gravity.TOP | Gravity.LEFT;        //Initially view will be added to top-left corner
        params.x = 0;
        params.y = 100;
        vUI = new VibratorUI((Vibrator) getSystemService(VIBRATOR_SERVICE));
        //Add the view to the window
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingView, params);


        //The root element of the collapsed view layout
        collapsedView = mFloatingView.findViewById(R.id.collapse_view);
        //The root element of the expanded view layout
        expandedView = mFloatingView.findViewById(R.id.expanded_container);

        mFloatingView.findViewById(R.id.root_container).setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;



            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        //remember the initial position.
                        initialX = params.x;
                        initialY = params.y;

                        //get the touch location
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        int Xdiff = (int) (event.getRawX() - initialTouchX);
                        int Ydiff = (int) (event.getRawY() - initialTouchY);

                        //The check for Xdiff <10 && YDiff< 10 because sometime elements moves a little while clicking.
                        //So that is click event.
                        if (Xdiff < 10 && Ydiff < 10) {
                            if (idExpandableVisible()) {
                                expandedView.setVisibility(View.GONE);
                                expandedView.setZ(0);
                                collapsedView.setZ(1);
                            }
                            else {
                                collapsedView.setX(0);
                                collapsedView.setY(40);
                                expandedView.setX(0);
                                expandedView.setY(collapsedView.getHeight()+51);
                                expandedView.setZ(0);
                                collapsedView.setZ(1);
                                expandedView.setVisibility(View.VISIBLE);
                            }
                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        //Calculate the X and Y coordinates of the view.
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);

                        //Update the layout with new X & Y coordinate
                        mWindowManager.updateViewLayout(mFloatingView, params);
                        return true;
                }
                return false;
            }
        });



try {
    ((GestureOverlayView)mFloatingView.findViewById(R.id.gestureInput)).addOnGesturePerformedListener(this);

    gestureLib = GestureLibraries.fromRawResource(this,
            R.raw.gestures);
    if (!gestureLib.load()) {
        //TODO: Exit the service
    }

    /*vUI = new VibratorUI((Vibrator) getSystemService(VIBRATOR_SERVICE));
    ttsSpeaker = new TextToSpeech(this, this);
    ttsSpeaker.setSpeechRate((float) 1.0);*/
}
catch (Exception e){
    e.printStackTrace();
}
}

    private boolean idExpandableVisible() {
        return mFloatingView == null || mFloatingView.findViewById(R.id.expanded_container).getVisibility() == View.VISIBLE;
    }

    //Remove bubble on onDestroy event
    @Override
    public void onDestroy() {
        Toast.makeText(this, "Floating Bubble removed", Toast.LENGTH_LONG).show();
        super.onDestroy();
        if (mFloatingView != null) mWindowManager.removeView(mFloatingView);
        //floatingFaceBubble.setVisibility(View.GONE);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
        ArrayList<Prediction> predictions = gestureLib.recognize(gesture);
        // for (Prediction prediction : predictions) {
        if (predictions.size() > 0) {
            if (predictions.get(0).score > 2) {
                //if(expandedView!=null)
                expandedView.setVisibility(View.GONE);
                // Toast.makeText(this, prediction.name, Toast.LENGTH_SHORT)
                // .show();
                String locationData;
                String number;
                AudioManager am;
                switch (predictions.get(0).name) {

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
                        contactIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
                        Intent mainActivity = new Intent(
                                this,
                                MainActivity.class);
                        mainActivity.putExtra("Purpose","Google_Search");
                        startActivity(mainActivity);
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



    private static final int SPEECH_REQUEST_CODE = 0;
    static Fragment f = new Fragment() {
        // Create an intent that can start the Speech Recognizer activity
        public void displaySpeechRecognizer() {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
// Start the activity, the intent will be populated with the speech text
            startActivityForResult(intent, SPEECH_REQUEST_CODE);
        }

        // This callback is invoked when the Speech Recognizer returns.
// This is where you process the intent and extract the speech text from the intent.
        @Override
        public void onActivityResult(int requestCode, int resultCode,
                                        Intent data) {
            if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
                List<String> results = data.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS);
                String spokenText = results.get(0);
                // Do something with spokenText
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    };
    @Override
    public void onInit(int status) {

    }
//    private void say(String text2Speak) {
//        ttsSpeaker.speak(text2Speak, TextToSpeech.QUEUE_FLUSH, null);
//
//    }
}