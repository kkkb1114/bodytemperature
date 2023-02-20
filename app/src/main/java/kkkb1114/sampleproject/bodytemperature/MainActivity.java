package kkkb1114.sampleproject.bodytemperature;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.navigation.NavigationBarView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Stack;

import kkkb1114.sampleproject.bodytemperature.BleConnect.ConnectActivity;
import kkkb1114.sampleproject.bodytemperature.fragment.BodyTemperatureGraphFragment;
import kkkb1114.sampleproject.bodytemperature.fragment.HomeFragment;
import kkkb1114.sampleproject.bodytemperature.fragment.SettingFragment;
import kkkb1114.sampleproject.bodytemperature.thermometer.Generator;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    Context context;

    // 프래그먼트
    HomeFragment homeFragment;
    BodyTemperatureGraphFragment bodyTemperatureGraphFragment;
    SettingFragment settingFragment;

    // 바텀 네비게이션
    NavigationBarView navigationBarView;

    private SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Handler handler = new Handler();
    Stack<Double> tempStack = new Stack<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        //날짜 측정
        long now =System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String str = dateFormat.format(date)+"tempData";
        preferences = context.getSharedPreferences(str, MODE_PRIVATE);
        editor = preferences.edit();

        initView();
        setToolbar();
        setFragment();
        MeasurBodyTempreture();
    }

    public void initView(){
        homeFragment = new HomeFragment();
        bodyTemperatureGraphFragment = new BodyTemperatureGraphFragment();
        settingFragment = new SettingFragment();
        toolbar = findViewById(R.id.toolbar_main);
        navigationBarView = findViewById(R.id.bottomNavigation);
    }

    /** 툴바 세팅 **/
    public void setToolbar(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        switch (menuItem.getItemId()){
            case R.id.bluetoothConnect:
                Intent intent = new Intent(this, ConnectActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    /** 프래그먼트 세팅 **/
    public void setFragment(){
        // 초기 화면 세팅
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_main, homeFragment).commit();
        setBottomNavationClick();
    }

    /** 바텀 네비게이션 클릭 이벤트 **/
    public void setBottomNavationClick(){
        navigationBarView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fl_main, homeFragment).commit();
                        return true;
                    case R.id.graph:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fl_main, bodyTemperatureGraphFragment).commit();
                        return true;
                    case R.id.setting:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fl_main, settingFragment).commit();
                        return true;
                }
                return false;
            }
        });
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

    /** 체온 측정 스레드 **/
    public void MeasurBodyTempreture(){

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
                    SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm");

                    editor.putString(dateFormat.format(date),String.valueOf(max));
                    editor.commit();

                    Log.d("max", String.valueOf(max));
                }

                // 3초마다 난수 받아옴
                String s = Generator.generate();
                homeFragment.setTextThermometerView(Float.valueOf(s));
                //thermometer.setValueAndStartAnim(Float.valueOf(s));
                homeFragment.setTextTvTemperature(s);
                handler.postDelayed(this, 3000);
                tempStack.add(Double.valueOf(s));

                Log.d("------------", String.valueOf(tempStack.size()));

            }
        }).start();
    }


}