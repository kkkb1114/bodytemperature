package kkkb1114.sampleproject.bodytemperature.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import kkkb1114.sampleproject.bodytemperature.R;
import kkkb1114.sampleproject.bodytemperature.dialog.WeightPickerDialog;
import kkkb1114.sampleproject.bodytemperature.tools.PreferenceManager;

public class MyProfileActivity extends AppCompatActivity implements View.OnClickListener {

    EditText et_myProfile_name;
    TextView tv_myProfile_man;
    TextView tv_myProfile_woman;
    TextView tv_myProfile_birthDate;
    TextView tv_myProfile_weight;
    Button bt_myProfile_cancle;
    Button bt_myProfile_confirm;

    int gender = 1; // 1: 남성, 0: 여성
    String name;
    String birthDate;
    String weight;

    Context context;

    // 달력 전용
    MaterialDatePicker materialDatePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        context = this;
        initView();
        getMyProfile();
    }

    public void initView(){
        et_myProfile_name = findViewById(R.id.et_myProfile_name);
        tv_myProfile_man = findViewById(R.id.tv_myProfile_man);
        tv_myProfile_woman = findViewById(R.id.tv_myProfile_woman);
        tv_myProfile_birthDate = findViewById(R.id.tv_myProfile_birthDate);
        tv_myProfile_weight = findViewById(R.id.tv_myProfile_weight);
        bt_myProfile_cancle = findViewById(R.id.bt_myProfile_cancle);
        bt_myProfile_confirm = findViewById(R.id.bt_myProfile_confirm);

        et_myProfile_name.setOnClickListener(this);
        tv_myProfile_man.setOnClickListener(this);
        tv_myProfile_woman.setOnClickListener(this);
        tv_myProfile_birthDate.setOnClickListener(this);
        tv_myProfile_weight.setOnClickListener(this);
        bt_myProfile_cancle.setOnClickListener(this);
        bt_myProfile_confirm.setOnClickListener(this);
    }

    /** 달력 띄우기 **/
    public void showMaterialDatePicker(){
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        // 오늘 날짜
        Long today = MaterialDatePicker.todayInUtcMilliseconds();

        materialDatePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Date Picker")
                .setSelection(today).build();

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
            tv_myProfile_man.setTextColor(Color.parseColor("#2B8FB6"));
            tv_myProfile_woman.setTextColor(Color.parseColor("#9E9E9E"));
        }else {
            tv_myProfile_man.setTextColor(Color.parseColor("#9E9E9E"));
            tv_myProfile_woman.setTextColor(Color.parseColor("#2B8FB6"));
        }
    }

    /** 쉐어드에서 내 정보 꺼내옵니다. **/
    public void getMyProfile(){
        gender = PreferenceManager.getInt(context, "gender");
        name = PreferenceManager.getString(context, "name");
        birthDate = PreferenceManager.getString(context, "birthDate");
        weight = PreferenceManager.getString(context, "weight");

        setGender(gender);
        if (name.length() >= 2){
            et_myProfile_name.setText(name);
        }
        if (birthDate.length() >= 2){
            tv_myProfile_birthDate.setText(birthDate);
        }
        if (weight.length() >= 2){
            tv_myProfile_weight.setText(weight);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_myProfile_woman:
                gender = 0;
                setGender(gender);
                break;

            case R.id.tv_myProfile_man:
                gender = 1;
                setGender(gender);
                break;

            case R.id.tv_myProfile_birthDate:
                showMaterialDatePicker();
                MaterialDatePickerClick();
                break;

            case R.id.tv_myProfile_weight:
                WeightPickerDialog weightPickerDialog = new WeightPickerDialog(tv_myProfile_weight);
                weightPickerDialog.show(getSupportFragmentManager(), "WeightPickerDialog");
                break;

            case R.id.bt_myProfile_cancle:
                finish();
                break;

            case R.id.bt_myProfile_confirm:
                name = et_myProfile_name.getText().toString();
                birthDate = tv_myProfile_birthDate.getText().toString();
                weight = tv_myProfile_weight.getText().toString();
                PreferenceManager.PREFERENCES_NAME = "myProfile";

                if (name.trim().length() == 0 || birthDate.trim().length() == 0 || weight.trim().length() == 0
                || name.equals("이름을 입력하세요")|| birthDate.equals("생년월일") || weight.equals("몸무게")){
                    Toast.makeText(context, "정보를 모두 기입해 주세요.", Toast.LENGTH_SHORT).show();
                }else {
                    PreferenceManager.setString(context, "name", name);
                    PreferenceManager.setInt(context, "gender", gender);
                    PreferenceManager.setString(context, "birthDate", birthDate);
                    PreferenceManager.setString(context, "weight", weight);

                    finish();
                }
                break;
        }
    }
}