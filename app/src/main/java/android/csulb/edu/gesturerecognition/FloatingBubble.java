package android.csulb.edu.gesturerecognition;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;


public class FloatingBubble extends AppCompatActivity {

    Button btnGenerateBubble;
    Button btnRemoveBubble;

    public final static int REQUEST_CODE = -1010101;

   /* public void checkDrawOverlayPermission() {
        *//** check if we already  have permission to draw over other apps *//*
        if (!Settings.canDrawOverlays()) {
            *//** if not construct intent to request permission *//*
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            */

    /**
     * request permission via start activity for result
     *//*
            startActivityForResult(intent, REQUEST_CODE);
        }
    }
*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_floating_bubble);
        setTitle(R.string.bubble_setting);


        btnGenerateBubble = (Button) findViewById(R.id.btnPutFloatingBubble);
        btnRemoveBubble = (Button) findViewById(R.id.btnRemoveFloatingBubble);

        btnGenerateBubble.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(FloatingBubble.this, FloatingBubbleService.class);
                if (!Settings.canDrawOverlays(getApplicationContext())) {
                    Intent mainIntent = new Intent(FloatingBubble.this, BubblePermissionActivity.class);
                    /** request permission via start activity for result */
                    startActivity(mainIntent);
                } else {
                    startService(intent);
                }
            }
        });

        btnRemoveBubble.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent stopIntent = new Intent(FloatingBubble.this, FloatingBubbleService.class);
                stopService(stopIntent);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}