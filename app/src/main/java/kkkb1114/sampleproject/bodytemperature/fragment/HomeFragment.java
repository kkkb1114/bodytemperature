package kkkb1114.sampleproject.bodytemperature.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Date;

import kkkb1114.sampleproject.bodytemperature.MainActivity;
import kkkb1114.sampleproject.bodytemperature.R;
import kkkb1114.sampleproject.bodytemperature.thermometer.ThermometerView;

public class HomeFragment extends Fragment {

    TextView tv_temperature;
    ThermometerView thermometer;
    ImageView pill;
    ImageView significant;
    private SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Context context;
    SharedPreferences select_user;

    SQLiteDatabase sqlDB;

    String username;
    public HomeFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_home, container, false);
        context = getActivity();

        setUser();

        initView(view);
        this.setListner();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setUser();
    }

    public void initView(View view){
        tv_temperature = view.findViewById(R.id.tv_temperature);
        thermometer = view.findViewById(R.id.thermometer);
        pill = view.findViewById(R.id.iv_pill);
        significant = view.findViewById(R.id.iv_significant);
    }

    public void setTextTvTemperature(String data){
        if (tv_temperature != null){
            tv_temperature.setText(data);
        }
    }

    public void setTextThermometerView(Float data){
        if (thermometer != null){
            thermometer.setCurValue(data);
        }
    }

    public void setListner()
    {
        pill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final LinearLayout linear = (LinearLayout) View.inflate(getActivity(), R.layout.dialog_pill, null);
                new AlertDialog.Builder(getActivity()).setView(linear)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                EditText edt_pill = (EditText) linear.findViewById(R.id.edt_pill);
                                String value = edt_pill.getText().toString();
                                long now =System.currentTimeMillis();
                                Date date = new Date(now);
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

                                sqlDB = MainActivity.bodytemp_dbHelper.getReadableDatabase();
                                sqlDB.execSQL("INSERT INTO TIMELINEDATA VALUES ('"+username+"', '"+value+"', '"+ dateFormat.format(date) +"');");

                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                }).show();
            }
        });

        significant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final LinearLayout linear = (LinearLayout) View.inflate(getActivity(), R.layout.dialog_significant, null);
                new AlertDialog.Builder(getActivity()).setView(linear)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                EditText edt_significant = (EditText) linear.findViewById(R.id.edt_significant);
                                String value = edt_significant.getText().toString();
                                long now =System.currentTimeMillis();
                                Date date = new Date(now);
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

                                sqlDB = MainActivity.bodytemp_dbHelper.getReadableDatabase();
                                sqlDB.execSQL("INSERT INTO TIMELINEDATA VALUES ('"+username+"', '"+value+"', '"+ dateFormat.format(date) +"');");

                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });
    }

    public void setUser()
    {
        select_user = context.getSharedPreferences("login_user",MODE_PRIVATE);
        username = select_user.getString("userName","선택된 사용자 없음");

        /*
        long now =System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String str = username+dateFormat.format(date)+"timelineData";


        preferences = context.getSharedPreferences(str, MODE_PRIVATE);
        editor = preferences.edit();
        */


    }



}