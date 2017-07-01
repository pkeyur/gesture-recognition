package android.csulb.edu.gesturerecognition;

import android.content.SharedPreferences;
import android.csulb.edu.gesturerecognition.utility.Utility;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class  SpeedDialActivity extends AppCompatActivity {

    private static final int SPEED_DIAL_QUANTITY = 4;
    TextView speed_dial1;
    TextView speed_dial2;
    TextView speed_dial3;
    TextView speed_dial4;
    Button save;
    ArrayList<String> contactNumberList;

    //private static String FILE_NAME = "SpeedDialSettings";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed_dial);

        setTitle(R.string.speed_dial);
        contactNumberList = new ArrayList<>();

        speed_dial1 = (TextView) findViewById(R.id.editText1);
        speed_dial2 = (TextView) findViewById(R.id.editText2);
        speed_dial3 = (TextView) findViewById(R.id.editText3);
        speed_dial4 = (TextView) findViewById(R.id.editText4);

        fillTextBoxes();

        save = (Button) findViewById(R.id.button);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactNumberList.add(0, speed_dial1.getText().toString());
                contactNumberList.add(1, speed_dial2.getText().toString());
                contactNumberList.add(2, speed_dial3.getText().toString());
                contactNumberList.add(3, speed_dial4.getText().toString());
                saveContactNumbers();
                Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fillTextBoxes() {
        ArrayList<String> savedNumbers = getContactnumbers();
            speed_dial1.setText(savedNumbers.get(0));
            speed_dial2.setText(savedNumbers.get(1));
            speed_dial3.setText(savedNumbers.get(2));
            speed_dial4.setText(savedNumbers.get(3));

    }

    private void saveContactNumbers() {
        SharedPreferences speedDial = getSharedPreferences(Utility.SPEED_DIAL_FILE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor edit = speedDial.edit();
        edit.clear();
        for (int i = 0; i < contactNumberList.size(); i++) {
            edit.putString(String.valueOf(i), contactNumberList.get(i));
        }
        edit.apply();

    }


    protected ArrayList<String> getContactnumbers() {
        ArrayList<String> result = new ArrayList<>();
        SharedPreferences speedDial = getSharedPreferences(Utility.SPEED_DIAL_FILE_NAME , MODE_PRIVATE);
        for(int i=0; i <SPEED_DIAL_QUANTITY; i++) {
            result.add(i,speedDial.getString(String.valueOf(i), ""));
        }
        return result;
    }
}
