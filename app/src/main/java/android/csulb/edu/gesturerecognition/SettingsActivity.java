package android.csulb.edu.gesturerecognition;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {

    ListView settingsList;
    ArrayList<String> settingsOptions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle("Settings");
        settingsList = (ListView) findViewById(R.id.settings_list);
        settingsOptions = new ArrayList<>();
        settingsOptions.add("Speed Dial");
        settingsOptions.add("Home Address");
        settingsOptions.add("Work Address");
        settingsOptions.add("Floating Bubble Accessibility");
        settingsList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, settingsOptions));
        settingsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String menuOption = (String) parent.getItemAtPosition(position);
                Intent intent;
                switch (menuOption)
                {

                    case "Speed Dial":
                        intent = new Intent(SettingsActivity.this,SpeedDialActivity.class);
                        startActivity(intent);
                        break;
                    case "Home Address":
                        intent = new Intent(SettingsActivity.this, AddressActivity.class);
                        intent.putExtra("Type","Home");
                        startActivity(intent);
                        break;
                    case "Work Address":
                        intent = new Intent(SettingsActivity.this, AddressActivity.class);
                        intent.putExtra("Type","Work");
                        startActivity(intent);
                        break;
                    case "Floating Bubble Accessibility":
                        intent = new Intent(SettingsActivity.this,FloatingBubble.class);
                        startActivity(intent);
                        break;

                }
            }
        });
    }
}
