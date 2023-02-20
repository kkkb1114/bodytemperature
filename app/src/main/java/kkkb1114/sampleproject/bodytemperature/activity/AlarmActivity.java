package kkkb1114.sampleproject.bodytemperature.activity;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;

import kkkb1114.sampleproject.bodytemperature.R;
import kkkb1114.sampleproject.bodytemperature.tools.PreferenceManager;

public class AlarmActivity extends AppCompatActivity implements View.OnClickListener {

    Context context;

    TextView tv_high_temperature;
    TextView tv_low_temperature;
    SeekBar sb_high_temperature;
    SeekBar sb_low_temperature;
    Switch sw_high_temperature;
    Switch sw_low_temperature;
    Button bt_alarm_confirm;
    Button bt_alarm_cancle;

    DecimalFormat decimalFormat = new DecimalFormat(".#");

    /*
     * 1. 최초 화면 띄울때 뷰 세팅 구분 값
     * 2. 저장소에 알람 데이터가 있으면 프로그래스바가 움직여서 프로그래스바 움직임 감지 메소드가 돌기때문에 처음에는 막기위해 만들었다.
     */
    boolean firstSetView = false;

    // seekBar 최소, 최고 온도
    double max = 40.0;
    double min = 37.3;
    double alarm_high_temperature_value = 0.0;
    double alarm_low_temperature_value = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        context = this;
        PreferenceManager.PREFERENCES_NAME = "user_list";
        String select_user_name = PreferenceManager.getString(context, "select_user_name");
        // 해당 화면에서는 알람값만 컨트롤하기에 "PREFERENCES_NAME" 설정
        PreferenceManager.PREFERENCES_NAME = select_user_name+"Profile";

        initView();
        setSeekBar();
        setBodyTemperature();
    }

    public void initView(){
        tv_high_temperature = findViewById(R.id.tv_high_temperature);
        tv_low_temperature = findViewById(R.id.tv_low_temperature);
        sb_high_temperature = findViewById(R.id.sb_high_temperature);
        sb_low_temperature = findViewById(R.id.sb_low_temperature);
        sw_high_temperature = findViewById(R.id.sw_high_temperature);
        sw_low_temperature = findViewById(R.id.sw_low_temperature);
        bt_alarm_confirm = findViewById(R.id.bt_alarm_confirm);
        bt_alarm_confirm.setOnClickListener(this);
        bt_alarm_cancle = findViewById(R.id.bt_alarm_cancle);
        bt_alarm_cancle.setOnClickListener(this);
    }

    /** 체온 처음 값 세팅 **/
    public void setBodyTemperature(){
        // 고체온
        boolean alarm_high_temperature_boolean = PreferenceManager.getBoolean(context, "alarm_high_temperature_boolean");
        String alarm_high_temperature_str = PreferenceManager.getString(context, "alarm_high_temperature_value");
        int high_temperature_progress = 0;

        if (alarm_high_temperature_str.trim().isEmpty()){
            alarm_high_temperature_value = min;
            high_temperature_progress = (int) min;
        }else {
            alarm_high_temperature_value = Double.parseDouble(decimalFormat.format(Double.parseDouble(alarm_high_temperature_str)));
            high_temperature_progress = (int) ((alarm_high_temperature_value - min) * 100);
        }

        if (alarm_high_temperature_boolean){
            sw_high_temperature.setChecked(true);
            tv_high_temperature.setText(String.valueOf(alarm_high_temperature_value));
        }else {
            sw_high_temperature.setChecked(false);
            tv_high_temperature.setText(String.valueOf(min));
        }
        setProgressThumb(sb_high_temperature, high_temperature_progress);

        // 저체온
        boolean alarm_low_temperature_boolean = PreferenceManager.getBoolean(context, "alarm_low_temperature_boolean");
        String alarm_low_temperature_str = PreferenceManager.getString(context, "alarm_low_temperature_value");
        int low_temperature_progress = 0;

        if (alarm_low_temperature_str.trim().isEmpty()){
            alarm_low_temperature_value = min;
            low_temperature_progress = (int) min;
        }else {
            alarm_low_temperature_value = Double.parseDouble(decimalFormat.format(Double.parseDouble(alarm_low_temperature_str)));
            low_temperature_progress = (int) ((alarm_low_temperature_value - min) * 100);
        }

        if (alarm_low_temperature_boolean){
            sw_low_temperature.setChecked(true);
            tv_low_temperature.setText(String.valueOf(alarm_low_temperature_value));
        }else {
            sw_low_temperature.setChecked(false);
            tv_low_temperature.setText(String.valueOf(min));
        }
        setProgressThumb(sb_low_temperature, low_temperature_progress);

        firstSetView = true;
    }

    /** seekBar 세팅 **/
    public void setSeekBar(){
        double step = 0.01;
        // seekbar 최대 값 설정
        sb_high_temperature.setMax((int) ((max-min) / step));
        sb_low_temperature.setMax((int) ((max-min) / step));

        setSeekBarAnimation(sb_high_temperature, "high_temperature_progress",max, min);
        setSeekBarAnimation(sb_low_temperature, "low_temperature_progress",max, min);
        setSeekBarChange_high(sb_high_temperature, min, step);
        setSeekBarChange_low(sb_low_temperature, min, step);
    }

    /** progress thumb 임의 위치로 배치 **/
    public void setProgressThumb(SeekBar seekBar, int progress){
        seekBar.post(new Runnable() {
            @Override
            public void run() {
                seekBar.setProgress(progress);
            }
        });
    }

    /** 고온 seekBar 동작시 이벤트 **/
    public void setSeekBarChange_high(SeekBar seekBar, double min, double step){
        sb_high_temperature.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                if (firstSetView){
                    double value = min + (progress * step);
                    String result = decimalFormat.format(value);
                    tv_high_temperature.setText(result);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    /** 저온 seekBar 동작시 이벤트 **/
    public void setSeekBarChange_low(SeekBar seekBar, double min, double step){
        sb_low_temperature.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                if (firstSetView){
                    double value = min + (progress * step);
                    String result = decimalFormat.format(value);
                    tv_low_temperature.setText(result);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    /** seekBar 최초 위치 설정 **/
    public void setSeekBarAnimation(SeekBar seekBar, String propertyName,double max, double min){
        int progress_half = (int) (((max / min) / 2 ) -1);
        ObjectAnimator objectAnimator = ObjectAnimator.ofInt(seekBar, propertyName, progress_half);
        objectAnimator.setDuration(100); // 0.5초
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.start();
    }

    /** 알람 설정 데이터 저장 **/
    public void saveAlarmData(){
        /*
         * 1. (고,저)체온 알람 스위치 on 되어 있는지 확인
         * 2.  on: (고,저)체온 알람 on 이라는 boolean 값 저장
         * 3.      (고,저)체온 기준 String 값 저장
         * 4. off: (고,저)체온 알람 off 이라는 boolean 값 저장
         *         이때는 값을 초기 값으로 저장한다.
         */
        if (sw_high_temperature.isChecked()){
            PreferenceManager.setBoolean(context, "alarm_high_temperature_boolean",true);
            PreferenceManager.setString(context, "alarm_high_temperature_value", tv_high_temperature.getText().toString());
        }else {
            PreferenceManager.setBoolean(context, "alarm_high_temperature_boolean",false);
            PreferenceManager.setString(context, "alarm_high_temperature_value", String.valueOf(min));
        }
        if (sw_low_temperature.isChecked()){
            PreferenceManager.setBoolean(context, "alarm_low_temperature_boolean",true);
            PreferenceManager.setString(context, "alarm_low_temperature_value", tv_low_temperature.getText().toString());
        }else {
            PreferenceManager.setBoolean(context, "alarm_low_temperature_boolean",false);
            PreferenceManager.setString(context, "alarm_low_temperature_value", String.valueOf(min));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bt_alarm_cancle:
                finish();
                break;
            case R.id.bt_alarm_confirm:
                saveAlarmData();
                finish();
                break;
        }
    }
}