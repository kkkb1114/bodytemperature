package kkkb1114.sampleproject.bodytemperature.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import kkkb1114.sampleproject.bodytemperature.R;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

public class BodyTemperatureGraphFragment extends Fragment {

    public LineChart lineChart;
    private SharedPreferences preferences;
    Context context;
    long now;
    Date date;
    SimpleDateFormat dateFormat;
    String str;

    public BodyTemperatureGraphFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ArrayList<Entry> entry_chart = new ArrayList<>();

        context = getActivity();
        now =System.currentTimeMillis();
        date = new Date(now);
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        str=dateFormat.format(date)+"tempData";
        preferences = context.getSharedPreferences(str, MODE_PRIVATE);


        View view = inflater.inflate(R.layout.fragment_body_temperature_graph, container, false);
        initView(view);


        showChart();

        return view;
    }

    public void initView(View view){
        lineChart = view.findViewById(R.id.chart);
    }

    public void showChart() {

        Map<String,?> keys = preferences.getAll();

        if(keys.size()==0)
            return;

        XAxis xAxis = lineChart.getXAxis();
        LineData lineData = new LineData();


        ArrayList<Entry> entry_chart_Y = new ArrayList<>();
        ArrayList<String> entry_chart_X = new ArrayList<>();
        Comparator<String> comparator = (s1, s2) -> s1.compareTo(s2);
        Map<String, String> map = new TreeMap<>(comparator);

        for(Map.Entry<String,?> entry : keys.entrySet()){
            String key = String.valueOf(entry.getKey());
            String value = String.valueOf(entry.getValue());

            map.put(key,value);
        }


        int i=0;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key=entry.getKey();
            float value=Float.valueOf(entry.getValue());
            entry_chart_Y.add(new Entry(i,value));
            entry_chart_X.add(key);
            i++;
        }



        LineDataSet lineDataSet = new LineDataSet(entry_chart_Y, "체온"); // 데이터가 담긴 Arraylist 를 LineDataSet 으로 변환한다.
        lineDataSet.setColor(Color.RED); // 해당 LineDataSet의 색 설정 :: 각 Line 과 관련된 세팅은 여기서 설정한다.
        lineDataSet.setLineWidth(3);


        XAxis.XAxisPosition position = XAxis.XAxisPosition.BOTTOM;
        xAxis.setPosition(position);// x 축 설정
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(entry_chart_X));
        lineData.addDataSet(lineDataSet); // 해당 LineDataSet 을 적용될 차트에 들어갈 DataSet 에 넣는다.
        lineChart.setData(lineData); // 차트에 위의 DataSet을 넣는다.
        lineChart.getDescription().setEnabled(false);
        lineChart.invalidate(); // 차트 업데이트
        lineChart.setTouchEnabled(false);


    }

}