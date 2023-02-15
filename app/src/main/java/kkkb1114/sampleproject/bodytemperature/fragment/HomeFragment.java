package kkkb1114.sampleproject.bodytemperature.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Stack;

import kkkb1114.sampleproject.bodytemperature.R;
import kkkb1114.sampleproject.bodytemperature.ThermometerView;
import kkkb1114.sampleproject.bodytemperature.thermometer.Generator;

public class HomeFragment extends Fragment {

    TextView tv_temperature;
    ThermometerView thermometer;
    private SharedPreferences preferences;
    Handler handler = new Handler();
    SharedPreferences.Editor editor;
    Stack<Double> tempStack = new Stack<>();

    public HomeFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        preferences = getContext().getSharedPreferences("tempData", MODE_PRIVATE);
        editor = preferences.edit();

        initView(view);
        MeasurBodyTempreture();
        return view;
    }

    public void initView(View view){
        tv_temperature = view.findViewById(R.id.tv_temperature);
        thermometer = view.findViewById(R.id.thermometer);
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
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

                    editor.putString(dateFormat.format(date),String.valueOf(max));
                    editor.commit();

                    Log.d("max", String.valueOf(max));
                }

                // 3초마다 난수 받아옴
                String s = Generator.generate();
                thermometer.setCurValue(Float.valueOf(s));
                //thermometer.setValueAndStartAnim(Float.valueOf(s));
                tv_temperature.setText(s);
                handler.postDelayed(this, 3000);
                tempStack.add(Double.valueOf(s));

                Log.d("------------", String.valueOf(tempStack.size()));

            }
        }).start();
    }
}