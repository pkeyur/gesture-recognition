package android.csulb.edu.gesturerecognition;


import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.csulb.edu.gesturerecognition.utility.Utility;
import android.database.Cursor;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.GestureOverlayView.OnGesturingListener;
import android.gesture.Prediction;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class ContactActivity extends AppCompatActivity implements
        OnGesturePerformedListener, OnGesturingListener, OnInitListener {
    protected static final int REQUEST_SPEECH = 1;
    protected static final int REQUEST_MESSAGE = 2;
    protected static final int REQUEST_CALL = 3;
    Utility utility =new Utility();
    int contactIndex = 0;
    ListAdapter adapter = null;
    ListView lvNameList;
    List<ContactModel> listContactModel = null;
    EditText etNameTxt;
    //TextToSpeech ttsSpeaker;
    private GestureLibrary gestureLib;
    VibratorUI vUI;

    /** Called when the activity is first created. */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Voice Search Results");

        GestureOverlayView gestureOverlayView = new GestureOverlayView(this);
        View inflate = getLayoutInflater().inflate(R.layout.activity_contact, null);
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
        // setContentView(R.layout.contact_list);
        vUI = new VibratorUI((Vibrator) getSystemService(VIBRATOR_SERVICE));
        /*ttsSpeaker = new TextToSpeech(this, this);
        ttsSpeaker.setSpeechRate((float) 1);*/

        lvNameList = (ListView) findViewById(R.id.lv_contactlist_contacts);
        etNameTxt = (EditText) findViewById(R.id.edt_contactlist_name);
        lvNameList.setEnabled(false);
        etNameTxt.setInputType(InputType.TYPE_NULL);

        // refreshList("");
        /*try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        performSpeechToText();
        etNameTxt.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable arg0) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // ((CursorAdapter) adapter).getFilter().filter(s.toString());
                if (!s.toString().equals(""))
                    refreshList(s.toString());

            }

        });

    }

    private void refreshList(String key) {
        // TODO Auto-generated method stub
       // contactIndex = -1;
        Uri uri = ContactsContract.Contacts.CONTENT_URI;

        ContentResolver cr = getContentResolver();
        String sortOrder = ContactsContract.Contacts.DISPLAY_NAME
                + " COLLATE LOCALIZED ASC";

        Cursor cur = cr.query(uri, null, "DISPLAY_NAME" + " LIKE ?",
                new String[] { key + "%" }, sortOrder);

        adapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1, cur,
                new String[] { ContactsContract.Contacts.DISPLAY_NAME },
                new int[] { android.R.id.text1 });

        lvNameList.setAdapter(adapter);
        lvNameList.setFilterText(key);

        listContactModel = new ArrayList<ContactModel>();

        while (cur.moveToNext()) {
            listContactModel.add(new ContactModel(cur.getString(cur
                    .getColumnIndex(ContactsContract.Contacts._ID)), cur
                    .getString(cur.getColumnIndex("DISPLAY_NAME"))));

        }
        //say(listContactModel.size() + " results found for " + key);

        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
        ArrayList<Prediction> predictions = gestureLib.recognize(gesture);

        if (predictions.size() > 0) {
            if (predictions.get(0).score > 2.0) {
                // Toast.makeText(this, prediction.name, Toast.LENGTH_SHORT)
                // .show();
                switch (Integer.parseInt(predictions.get(0).name.trim())) {

                    case GlobalValues.GESTURE_NAVIGATE_LEFT:
                        Toast.makeText(this, "Gesture left performed.",
                                Toast.LENGTH_SHORT).show();
                        ContentResolver cr = getContentResolver();
                        Cursor phones = cr.query(
                                Phone.CONTENT_URI,
                                null,
                                Phone.CONTACT_ID
                                        + " = "
                                        + listContactModel.get(contactIndex)
                                        .getId(), null, null);
                        if (phones.moveToNext()) {
                            String number = phones.getString(phones
                                    .getColumnIndex(Phone.NUMBER));
                            Intent smsIntent = new Intent(this,SMSActivity.class);
                            smsIntent.putExtra("SENDER", number);
                            utility.say("Please speak the message");
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            startActivityForResult(smsIntent, REQUEST_MESSAGE);
                        }
                        phones.close();

                        break;

                    case GlobalValues.GESTURE_NAVIGATE_RIGHT:
                        Toast.makeText(this, "Gesture right performed.",
                                Toast.LENGTH_SHORT).show();
                                ContentResolver cr1 = getContentResolver();
                                Cursor phones1 = cr1.query(
                                        Phone.CONTENT_URI,
                                        null,
                                        Phone.CONTACT_ID
                                                + " = "
                                                + listContactModel.get(contactIndex)
                                                .getId(), null, null);
                                if (phones1.moveToNext()) {
                                    String number = phones1.getString(phones1
                                            .getColumnIndex(Phone.NUMBER));
                                    phoneCall(number);

                                }
                                phones1.close();

                        break;
                    case GlobalValues.GESTURE_NAVIGATE_UP:
                        Toast.makeText(this, "Gesture up performed.",
                                Toast.LENGTH_SHORT).show();
                        if (listContactModel != null) {
                            if (listContactModel.size() > 0) {

                                if (contactIndex > 0) {
                                    //utility.say(listContactModel.get(--contactIndex).getName());

                                }
                            }
                        }

                        break;
                    case GlobalValues.GESTURE_NAVIGATE_DOWN:
                        Toast.makeText(this, "Gesture down performed.",
                                Toast.LENGTH_SHORT).show();
                        if (listContactModel != null) {
                            if (listContactModel.size() > 0) {
                                if (contactIndex < listContactModel.size() - 1) {
                                   //utility.say(listContactModel.get(++contactIndex).getName());

                                }

                            }
                        }
                        break;
                    case GlobalValues.GESTURE_NAVIGATE_ANTI_CLOCK_ROUND:
                        utility.say("Please speak out name of the contact");
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        performSpeechToText();
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
        // vUI.vibrate(VibratorUI.DOT);
    }

    public void onGesturingStarted(GestureOverlayView overlay) {
        vUI.vibrate(VibratorUI.DOT);
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
                    etNameTxt.setText(text.get(0));
                } else {
                    utility.say("Operation successfully canceled by you!");
                }
                break;
            case REQUEST_MESSAGE:
                break;
        }

    }

    private void performSpeechToText() {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-IN");

        try {
            startActivityForResult(intent, REQUEST_SPEECH);
            etNameTxt.setText("");
        } catch (ActivityNotFoundException a) {
            Toast t = Toast.makeText(getApplicationContext(),
                    "Opps! Your device doesn't support Speech to Text",
                    Toast.LENGTH_SHORT);
            t.show();
        }

    }

    public void onInit(int arg0) {
        // TODO Auto-generated method stub
       // say("Please speak out name of the contact");
        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub

       /* if (ttsSpeaker != null) {
            ttsSpeaker.stop();
            ttsSpeaker.shutdown();
        }*/
        super.onDestroy();
    }

   /*private void say(String text2Speak) {
        ttsSpeaker.speak(text2Speak, TextToSpeech.QUEUE_FLUSH, null);

    }*/

    private void phoneCall(String phoneNumber) {

        Intent phoneCallIntent = new Intent(Intent.ACTION_CALL);
        phoneCallIntent.setData(Uri.parse("tel:" + phoneNumber));
        try{
            startActivity(phoneCallIntent);
        }catch(Exception e){

        }

        /*if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            startActivity(phoneCallIntent);
        }*/
    }

}