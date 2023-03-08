package kkkb1114.sampleproject.bodytemperature.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import kkkb1114.sampleproject.bodytemperature.MainActivity;
import kkkb1114.sampleproject.bodytemperature.R;
import kkkb1114.sampleproject.bodytemperature.activity.PillActivity;
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

                Intent intent = new Intent(context, PillActivity.class);
                startActivity(intent);
            }

        });

        significant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final LinearLayout linear = (LinearLayout) View.inflate(getActivity(), R.layout.dialog_significant, null);
                final SeekBar seekBar = (SeekBar) linear.findViewById(R.id.seekBar_significant);
                final TextView textView = (TextView) linear.findViewById(R.id.text_seek);

                AlertDialog.Builder dialog =new AlertDialog.Builder(getActivity()).setView(linear);

                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if(progress==0)
                            textView.setText(String.valueOf(progress)+" 아무런 통증 없음");
                        switch(progress){
                            case 0:
                                textView.setText(String.valueOf(progress)+" 매우 약한 통증");
                                break;
                            case 1:
                                textView.setText(String.valueOf(progress)+" 약한 통증");
                                break;
                            case 2:
                                textView.setText(String.valueOf(progress)+" 약간의 통증");
                                break;
                            case 3:
                                textView.setText(String.valueOf(progress)+" 중간 정도의 통증");
                                break;
                            case 4:
                                textView.setText(String.valueOf(progress)+" 상당한 통증");
                                break;
                            case 5:
                                textView.setText(String.valueOf(progress)+" 강한 통증");
                                break;
                            case 6:
                                textView.setText(String.valueOf(progress)+" 매우 강한 통증");
                                break;
                            case 7:
                                textView.setText(String.valueOf(progress)+" 아무런 통증 없음");
                                break;
                            case 8:
                                textView.setText(String.valueOf(progress)+" 극심한 통증");
                                break;
                            default:
                                textView.setText(String.valueOf(progress)+" 의식을 잃을 정도의 통증");
                                break;

                        }




                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {}

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {}
                });

                dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        EditText edt_significant = (EditText) linear.findViewById(R.id.edt_significant);
                        EditText edt_significantTime = (EditText) linear.findViewById(R.id.edt_significantTime);

                        String value = edt_significant.getText().toString() + "/" +textView.getText().toString();
                        String time = edt_significantTime.getText().toString();

                        float amount=0f;
                        String source = " ";
                        sqlDB = MainActivity.bodytemp_dbHelper.getReadableDatabase();
                        sqlDB.execSQL("INSERT INTO TIMELINEDATA VALUES ('"+username+"', '"+value+"', '"+ time +"', '"+source+"', '"+amount+"');");

                        dialog.dismiss();
                    }
                });
                dialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
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