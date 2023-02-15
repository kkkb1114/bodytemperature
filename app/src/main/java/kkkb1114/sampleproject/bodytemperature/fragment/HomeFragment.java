package kkkb1114.sampleproject.bodytemperature.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import kkkb1114.sampleproject.bodytemperature.R;
import kkkb1114.sampleproject.bodytemperature.thermometer.Thermometer;

public class HomeFragment extends Fragment {

    TextView tv_temperature;
    Thermometer thermometer;

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


}