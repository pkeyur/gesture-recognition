package android.csulb.edu.gesturerecognition.utility;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;

public class BatteryOperations {

    Utility utility = new Utility();

    private BroadcastReceiver battery_receiver = new BroadcastReceiver() {
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

            //Log.d(LOG_TAG, "Battery level:" + bundle.toString());

            if (isPresent) {
                if (rawlevel >= 0 && scale > 0) {
                    level = (rawlevel * 100) / scale;
                }

                String info = "Battery Level is" + level + "%\n";

                info += ("Battery is " + getPlugTypeString(plugged) + "\n");

                info += ("and Status is " + getStatusString(status) + "\n");

                setBatteryLevelText(context, info);// );
            } else {
                setBatteryLevelText(context, "Battery not present!!!");
            }
        }
    };
    private String getPlugTypeString(int plugged) {
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

    private void setBatteryLevelText(Context context, String text) {
        utility.initTTS(context);
        utility.say( text);
        if(!text.equals(""))
        context.unregisterReceiver(battery_receiver);
    }

    public void registerBatteryLevelReceiver(Context context) {
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        context.registerReceiver(battery_receiver, filter);
    }
}
