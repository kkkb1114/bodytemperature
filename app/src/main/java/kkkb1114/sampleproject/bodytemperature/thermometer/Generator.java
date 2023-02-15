package kkkb1114.sampleproject.bodytemperature.thermometer;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class Generator extends AppCompatActivity {

    public static String generate()
    {
        double max=40.0;
        double min=37.3;


        double temp =  Math.random() * (max-min+1)+min;
        Log.d("temp",String.valueOf(temp));
        String s = String.format("%.2f",temp);
        return s;
    }



}
