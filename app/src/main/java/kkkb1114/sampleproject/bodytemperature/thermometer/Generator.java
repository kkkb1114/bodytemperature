package kkkb1114.sampleproject.bodytemperature.thermometer;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class Generator extends AppCompatActivity {

    public static String generate()
    {
        double max=40.0;
        double min=37.3;


        double temp =  Math.random() * (max-min+1)+min-1;
        Log.d("temp",String.valueOf(temp));
        String s = String.format("%.2f",temp);
        return s;
    }
}
