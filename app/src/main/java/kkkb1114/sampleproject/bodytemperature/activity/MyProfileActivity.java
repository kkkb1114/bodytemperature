package kkkb1114.sampleproject.bodytemperature.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import kkkb1114.sampleproject.bodytemperature.R;
import kkkb1114.sampleproject.bodytemperature.database.MyProfile.MyProfile;
import kkkb1114.sampleproject.bodytemperature.database.MyProfile.MyProfile_DBHelper;
import kkkb1114.sampleproject.bodytemperature.dialog.WeightPickerDialog;
import kkkb1114.sampleproject.bodytemperature.tools.PreferenceManager;
import kkkb1114.sampleproject.bodytemperature.tools.TimeCalculationManager;

public class MyProfileActivity extends AppCompatActivity implements View.OnClickListener {

    TimeCalculationManager timeCalculationManager;
    MyProfile_DBHelper myProfile_dbHelper;

    EditText et_myProfile_name;
    TextView tv_myProfile_man;
    TextView tv_myProfile_woman;
    TextView tv_myProfile_birthDate;
    TextView tv_myProfile_weight;
    TextView tv_myProfile_purpose;
    TextView tv_myProfile_infection;
    Button bt_myProfile_cancle;
    Button bt_myProfile_confirm;

    String selectedItem_purpose; // 이용 목적 선택 문자열
    String selectedItem_infection; // 이용 목적 선택 문자열
    
    int gender = 1; // 1: 남성, 0: 여성
    String name = "";
    String birthDate = "";
    String weight = "";
    String purpose = "";
    String infection = "";

    String intentUserName = ""; // 내 정보 보기, 수정으로 들어올 경우 getIntent를 통해 문자열을 담는다.

    Context context;

    // 달력 전용
    MaterialDatePicker materialDatePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        context = this;
        timeCalculationManager = new TimeCalculationManager();
        myProfile_dbHelper = new MyProfile_DBHelper();
        Intent intent = getIntent();
        intentUserName = intent.getStringExtra("userName");
        initView();

        getMyProfile();
    }

    public void initView(){
        et_myProfile_name = findViewById(R.id.et_myProfile_name);
        tv_myProfile_man = findViewById(R.id.tv_myProfile_man);
        tv_myProfile_woman = findViewById(R.id.tv_myProfile_woman);
        tv_myProfile_birthDate = findViewById(R.id.tv_myProfile_birthDate);
        tv_myProfile_weight = findViewById(R.id.tv_myProfile_weight);
        tv_myProfile_purpose = findViewById(R.id.tv_myProfile_purpose);
        tv_myProfile_infection = findViewById(R.id.tv_myProfile_infection);
        bt_myProfile_cancle = findViewById(R.id.bt_myProfile_cancle);
        bt_myProfile_confirm = findViewById(R.id.bt_myProfile_confirm);

        et_myProfile_name.setOnClickListener(this);
        tv_myProfile_man.setOnClickListener(this);
        tv_myProfile_woman.setOnClickListener(this);
        tv_myProfile_birthDate.setOnClickListener(this);
        tv_myProfile_weight.setOnClickListener(this);
        tv_myProfile_purpose.setOnClickListener(this);
        tv_myProfile_infection.setOnClickListener(this);
        bt_myProfile_cancle.setOnClickListener(this);
        bt_myProfile_confirm.setOnClickListener(this);
    }

    /** 달력 띄우기 **/
    public void showMaterialDatePicker(String day){
        // 아래 3개 설정시 앱 내의 시간을 'UTC' 기준으로 인식 되도록 설정
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        Locale.setDefault(Locale.KOREA);
        Long today = null;

        try {
            if (tv_myProfile_birthDate.getText().toString().equals("생년월일")){
                // 오늘 날짜
                today = MaterialDatePicker.todayInUtcMilliseconds();
            }else {
                /*
                 * MaterialDatePicker의 이슈중 하나가 날짜 계산이 무조건 UTC 기준으로 계산되어 Asia/Seoul로 지정하면 하루 전으로 표시가 된다.
                 * 그래서 일단 임시 방편으로 시간 기준을 UTC로 지정하였다.
                 * */
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date = simpleDateFormat.parse(day);
                today = date.getTime();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        materialDatePicker = MaterialDatePicker.Builder.datePicker()
                .setTheme(R.style.DatePickerTheme)
                .setTitleText("생년월일")
                .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
                .setSelection(today)
                .build();

        materialDatePicker.show(getSupportFragmentManager(), "DatePicker");
    }

    /** 달력 확인 버튼 클릭 **/
    public void MaterialDatePickerClick(){
        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override
            public void onPositiveButtonClick(Long selection) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date();
                date.setTime(selection);

                String dateStr = simpleDateFormat.format(date);
                tv_myProfile_birthDate.setText(dateStr);
            }
        });
    }

    /** 성별 세팅 **/
    public void setGender(int gender){
        if (gender == 1){
            this.gender = 1;
            tv_myProfile_man.setTextColor(Color.parseColor("#2B8FB6"));
            tv_myProfile_woman.setTextColor(Color.parseColor("#9E9E9E"));
        }else {
            this.gender = 0;
            tv_myProfile_man.setTextColor(Color.parseColor("#9E9E9E"));
            tv_myProfile_woman.setTextColor(Color.parseColor("#2B8FB6"));
        }
    }

    /** 수정모드면 DB에서 내 정보 꺼내옵니다. **/
    public void getMyProfile(){
        if (isModify()){
            MyProfile myProfile = myProfile_dbHelper.DBselect(intentUserName);
            Log.e("qwewqdasdasd", myProfile.toString());
            if (myProfile != null){
                // 수정모드면 이름은 key값이라 수정 못하게 클릭 자체를 막는다.
                et_myProfile_name.setClickable(false);
                et_myProfile_name.setFocusable(false);

                et_myProfile_name.setText(myProfile.name);
                setGender(myProfile.gender);
                tv_myProfile_birthDate.setText(myProfile.birthDate);
                tv_myProfile_weight.setText(myProfile.weight);
                tv_myProfile_purpose.setText(myProfile.purpose);
                tv_myProfile_infection.setText(myProfile.infection);
                setVisibility_View(myProfile.purpose);
            }else {
                et_myProfile_name.setText("user");
                setGender(0);
                tv_myProfile_birthDate.setText(timeCalculationManager.getToday());
                tv_myProfile_weight.setText("0");
            }
        }
    }


    /** View visible 세팅 **/
    public void setVisibility_View(String selectedItem_purpose){
        Log.e("setVisibility_View", selectedItem_purpose);
        // 선택한 항목이 '감염'이면 '감염 병 선택'란을 보여준다.
        if (selectedItem_purpose.equals("감염")){
            tv_myProfile_infection.setVisibility(View.VISIBLE);
        }else {
            tv_myProfile_infection.setVisibility(View.GONE);
        }
    }

    /** 수정모드인지 체크 **/
    public boolean isModify(){
        if (intentUserName != null && !intentUserName.isEmpty()){
            return true;
        }else {
            return false;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.et_myProfile_name:
                if (isModify()){
                    Toast.makeText(context, "이름은 변경 할 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.tv_myProfile_woman:
                gender = 0;
                setGender(gender);
                // 여성을 선택후 이용 목적을 배란으로 선택 후에 다시 남성으로 돌아가면 배란이 그대로 남아있으면 안된다.
                tv_myProfile_purpose.setText("감염");
                break;

            case R.id.tv_myProfile_man:
                gender = 1;
                setGender(gender);
                // 여성을 선택후 이용 목적을 배란으로 선택 후에 다시 남성으로 돌아가면 배란이 그대로 남아있으면 안된다.
                tv_myProfile_purpose.setText("감염");
                break;

            case R.id.tv_myProfile_birthDate:
                showMaterialDatePicker(tv_myProfile_birthDate.getText().toString());
                MaterialDatePickerClick();
                break;

            case R.id.tv_myProfile_weight:
                WeightPickerDialog weightPickerDialog = new WeightPickerDialog(tv_myProfile_weight);
                weightPickerDialog.show(getSupportFragmentManager(), "WeightPickerDialog");
                break;

            case R.id.tv_myProfile_purpose:
                // 여성일때만 배란이 목록에 뜨도록 설정
                String[] items_purpose;
                if (gender == 0){
                    items_purpose = new String[]{"감염", "염증", "배란"};
                }else {
                    items_purpose = new String[]{"감염", "염증"};
                }
                AlertDialog.Builder builder_purpose = new AlertDialog.Builder(this);
                builder_purpose.setTitle("목적 선택");
                builder_purpose.setItems(items_purpose, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int position) {
                        selectedItem_purpose = items_purpose[position];
                        tv_myProfile_purpose.setText(selectedItem_purpose);

                        setVisibility_View(selectedItem_purpose);
                    }
                });
                builder_purpose.show();
                break;

            case R.id.tv_myProfile_infection:
                String[] items_infection;
                items_infection = new String[]{"감기/독감", "폐렴", "홍역"};
                AlertDialog.Builder builder_infection = new AlertDialog.Builder(this);
                builder_infection.setTitle("감염 병 선택");
                builder_infection.setItems(items_infection, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int position) {
                        selectedItem_infection = items_infection[position];
                        tv_myProfile_infection.setText(selectedItem_infection);
                    }
                });
                builder_infection.show();
                break;

            case R.id.bt_myProfile_cancle:
                finish();
                break;

            case R.id.bt_myProfile_confirm:
                name = et_myProfile_name.getText().toString();
                birthDate = tv_myProfile_birthDate.getText().toString();
                weight = tv_myProfile_weight.getText().toString();
                purpose = tv_myProfile_purpose.getText().toString();
                infection = tv_myProfile_infection.getText().toString();

                Log.e("tv_myProfile_infection.getText().toString()", tv_myProfile_infection.getText().toString());

                if (name.trim().length() == 0 || birthDate.trim().length() == 0 || weight.trim().length() == 0
                || name.equals(getResources().getString(R.string.et_myProfile_name))||
                        birthDate.equals(getResources().getString(R.string.tv_myProfile_birthDate)) ||
                        weight.equals(getResources().getString(R.string.tv_myProfile_weight)) ||
                        purpose.equals(getResources().getString(R.string.tv_myProfile_purpose))){

                    Toast.makeText(context, "정보를 모두 입력해 주세요.", Toast.LENGTH_SHORT).show();
                }else {
                    // 이용 목적이 '감염'이고 감염병 선택을 하지 않았다면 감염 병을 선택해달라는 문구를 띄운다.
                    if (purpose.equals("감염") &&
                            infection.equals(context.getResources().getString(R.string.tv_myProfile_infection))){

                        Toast.makeText(context, "감염 병을 선택해주세요.", Toast.LENGTH_SHORT).show();

                    }else {
                        // 수정모드면 DB UPDATE만 하고 신규 정보면 INSERT한다.
                        if (!isModify()) { // 신규

                            if (myProfile_dbHelper.DBselect(name) != null){
                                Toast.makeText(context, "중복된 사용자명입니다.", Toast.LENGTH_SHORT).show();

                            }else {
                                // 알람 설정 할때 저장할 데이터 미리 생성
                                PreferenceManager.PREFERENCES_NAME = name + "Setting";
                                PreferenceManager.setBoolean(context, "alarm_high_temperature_boolean", false);
                                PreferenceManager.setString(context, "alarm_high_temperature_value", String.valueOf(37.3));
                                PreferenceManager.setBoolean(context, "alarm_low_temperature_boolean", false);
                                PreferenceManager.setString(context, "alarm_low_temperature_value", String.valueOf(37.3));

                                // 다른 화면에서 현재 선택된 사용자 구분이 되어야 하기에 현재 사용자 구분 쉐어드 파일 생성
                                PreferenceManager.PREFERENCES_NAME = "login_user";
                                PreferenceManager.setString(context, "userName", name);
                                PreferenceManager.setString(context, "userPurpose", purpose);
                                PreferenceManager.setString(context, "userInfection", infection);

                                MyProfile myProfile = new MyProfile(name, gender, birthDate, weight, purpose, infection);
                                myProfile_dbHelper.DBinsert(myProfile);
                                finish();
                            }
                        }else { // 수정
                            // 다른 화면에서 현재 선택된 사용자 구분이 되어야 하기에 현재 사용자 구분 쉐어드 파일 생성
                            PreferenceManager.PREFERENCES_NAME = "login_user";
                            PreferenceManager.setString(context, "userName", name);
                            PreferenceManager.setString(context, "userPurpose", purpose);
                            PreferenceManager.setString(context, "userInfection", infection);

                            MyProfile myProfile = new MyProfile(name, gender, birthDate, weight, purpose, infection);
                            myProfile_dbHelper.DBupdate(myProfile);
                            finish();
                        }
                    }
                }
                break;
        }
    }
}