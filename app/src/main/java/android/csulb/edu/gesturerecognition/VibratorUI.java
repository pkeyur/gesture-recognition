package android.csulb.edu.gesturerecognition;

import android.os.Vibrator;

public class VibratorUI {


    //constants for vibrator time.
    public static final int DOT = 200; // Length of a Morse Code "dot" in
    // milliseconds
    public static final int DASH = 500; // Length of a Morse Code "dash" in
    // milliseconds
    public static final int SHORT_GAP = 200; // Length of Gap Between
    // dots/dashes
    public static final int MEDIUM_GAP = 500; // Length of Gap Between Letters
    public static final int LONG_GAP = 1000; // Length of Gap Between Words
    public static final long[] ERROR_PATTERN = { 0, 200, 500 }; // To indicate
    // error

    private Vibrator v;

    public VibratorUI(Vibrator v) {
        super();
        this.v = v;

    }

    public void vibrate(int duration) {
        v.vibrate(duration);
    }
    public void vibrate(long[] pattern, int repeat)
    {

        v.vibrate(pattern, repeat);
    }

}