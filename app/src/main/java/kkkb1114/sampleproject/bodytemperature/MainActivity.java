package kkkb1114.sampleproject.bodytemperature;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Stack;

import kkkb1114.sampleproject.bodytemperature.thermometer.Generator;
import kkkb1114.sampleproject.bodytemperature.thermometer.Thermometer;

public class MainActivity extends AppCompatActivity {
    TextView tv_temp;
    Thermometer thermometer;
    private SharedPreferences preferences;
    Handler handler = new Handler();
    SharedPreferences.Editor editor;
    Stack<Double> tempStack = new Stack<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences("tempData", MODE_PRIVATE);
        tv_temp = (TextView) findViewById(R.id.tv_temperature);
        editor = preferences.edit();
        thermometer = (Thermometer) findViewById(R.id.dummyThermometer);


        // 체온측정 스레드 시작.
        new Thread(new Runnable() {
            @Override
            public void run() {

                // 1분이 지나면 최대값 파일에 작성
                if (tempStack.size() >= 20){
                    Double max=tempStack.peek();

                    while(!tempStack.isEmpty()) {

                        Double cmp = tempStack.pop();
                        if(cmp>max)
                            max=cmp;

                    }
                    tempStack.clear();

                    long now =System.currentTimeMillis();
                    Date date = new Date(now);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

                    editor.putString(dateFormat.format(date),String.valueOf(max));
                    editor.commit();

                    Log.d("max", String.valueOf(max));
                }

                // 3초마다 난수 받아옴
                String s = Generator.generate();
                tv_temp.setText(s);
                handler.postDelayed(this, 3000);
                tempStack.add(Double.valueOf(s));
                thermometer.
                Log.d("------------", String.valueOf(tempStack.size()));

            }
        }).start();








    }
    public void WriteTextFile(String foldername, String filename, String contents){
        try{
            File dir = new File (foldername);
            //디렉토리 폴더가 없으면 생성함
            if(!dir.exists()){
                dir.mkdir();
            }
            //파일 output stream 생성
            FileOutputStream fos = new FileOutputStream(foldername+"/"+filename, true);
            //파일쓰기
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
            writer.write(contents);
            writer.flush();

            writer.close();
            fos.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }


    protected void onDestroy(Bundle savedInstanceState) throws IOException {
        super.onDestroy();
    }

}