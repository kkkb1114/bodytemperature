package kkkb1114.sampleproject.bodytemperature.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import kkkb1114.sampleproject.bodytemperature.R;
import kkkb1114.sampleproject.bodytemperature.ThermometerView;

public class HomeFragment extends Fragment {

    TextView tv_temperature;
    ThermometerView thermometer;

    public HomeFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initView(view);
        return view;
    }

    public void initView(View view){
        tv_temperature = view.findViewById(R.id.tv_temperature);
        thermometer = view.findViewById(R.id.thermometer);
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
}