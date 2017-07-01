package android.csulb.edu.gesturerecognition;

import java.util.ArrayList;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.GestureOverlayView.OnGesturingListener;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class SMSActivity extends AppCompatActivity implements
        OnGesturePerformedListener, OnGesturingListener, OnInitListener {
    protected static final int REQUEST_SPEECH = 1;
    protected static final int REQUEST_MESSAGE = 2;
    TextView tvSenderNumber, tvMessage;
    Intent callerIntent;
    String senderNumber;
    TextToSpeech ttsSpeaker;
    private GestureLibrary gestureLib;
    VibratorUI vUI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callerIntent = getIntent();
        senderNumber = callerIntent.getStringExtra("SENDER");
        setTitle("SMS");
        GestureOverlayView gestureOverlayView = new GestureOverlayView(this);
        View inflate = getLayoutInflater().inflate(R.layout.activity_sms, null);
        gestureOverlayView.addView(inflate);
        gestureOverlayView.addOnGesturePerformedListener(this);
        gestureOverlayView.setGestureColor(Color.CYAN);
        gestureOverlayView.setUncertainGestureColor(Color.GRAY);
        gestureOverlayView.addOnGesturingListener(this);
        gestureLib = GestureLibraries.fromRawResource(this,
                R.raw.gestures_navigation);
        if (!gestureLib.load()) {
            finish();
        }
        setContentView(gestureOverlayView);

        ttsSpeaker = new TextToSpeech(this, this);
        ttsSpeaker.setSpeechRate((float) 1);
        vUI = new VibratorUI((Vibrator) getSystemService(VIBRATOR_SERVICE));
        tvSenderNumber = (TextView) findViewById(R.id.tv_sms_screen_phone_number);
        tvMessage = (TextView) findViewById(R.id.tv_sms_screen_message);
        tvSenderNumber.setText("Sending to:" + senderNumber);
        tvMessage.setText("Message:");
        performSpeachToText();
    }

    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
        ArrayList<Prediction> predictions = gestureLib.recognize(gesture);

        if (predictions.size() > 0) {
            if (predictions.get(0).score > 2.0) {
                // Toast.makeText(this, prediction.name, Toast.LENGTH_SHORT)
                // .show();
                switch (Integer.parseInt(predictions.get(0).name.trim())) {

                    case GlobalValues.GESTURE_NAVIGATE_LEFT:
                        break;

                    case GlobalValues.GESTURE_NAVIGATE_RIGHT:
                        sendSMS(senderNumber, tvMessage.getText().toString()
                                .substring(8));
                        break;
                    case GlobalValues.GESTURE_NAVIGATE_UP:
                        say("Your " + tvMessage.getText().toString());
                        break;
                    case GlobalValues.GESTURE_NAVIGATE_DOWN:
                        break;
                    case GlobalValues.GESTURE_NAVIGATE_ANTI_CLOCK_ROUND:
                        performSpeachToText();
                        break;
                }

                // Toast.makeText(this, predictions.get(0).name,
                // Toast.LENGTH_SHORT).show();
            } else {
                vUI.vibrate(VibratorUI.MEDIUM_GAP);
            }

        }

    }

    public void onGesturingEnded(GestureOverlayView overlay) {
        // TODO Auto-generated method stub

    }

    public void onGesturingStarted(GestureOverlayView overlay) {
        // TODO Auto-generated method stub

    }

    private void sendSMS(String phoneNumber, String message) {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(
                SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                new Intent(DELIVERED), 0);

        // --- When the SMS has been sent ---
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        say("Your SMS is sent");
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getApplicationContext(), "Generic failure",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        say("Service not available");
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getApplicationContext(), "Null PDU",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Radio off",
                                Toast.LENGTH_SHORT).show();
                        break;
                }


            }

        }, new IntentFilter(SENT));

        // --- When the SMS has been delivered. ---
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        // say("SMS delivered");
                        break;
                    case Activity.RESULT_CANCELED:
                        // say("SMS not delivered");

                        break;
                }
            }
        }, new IntentFilter(DELIVERED));

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
    }

    public void onInit(int arg0) {
      /*  try {
            //say("Please speak the message");
           // Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub

        if (ttsSpeaker != null) {
            ttsSpeaker.stop();
            ttsSpeaker.shutdown();
        }
        super.onDestroy();
    }

    private void say(String text2Speak) {
        ttsSpeaker.speak(text2Speak, TextToSpeech.QUEUE_FLUSH, null);

    }

    private void performSpeachToText() {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-IN");

        try {
            startActivityForResult(intent, REQUEST_SPEECH);
            tvMessage.setText("Message:");
        } catch (ActivityNotFoundException a) {
            Toast t = Toast.makeText(getApplicationContext(),
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
            case REQUEST_SPEECH: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> text = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    tvMessage.setText("Message: " + text.get(0));

                } else {
                    say("Operation successfully canceled by you!");

                }
                break;
            }

        }
    }

}