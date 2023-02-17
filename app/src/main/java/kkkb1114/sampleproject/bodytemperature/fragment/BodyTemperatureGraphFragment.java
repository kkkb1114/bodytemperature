package kkkb1114.sampleproject.bodytemperature.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import kkkb1114.sampleproject.bodytemperature.R;

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

        XAxis xAxis = lineChart.getXAxis();

        LineData chartData = new LineData();
        Map<String,?> keys = preferences.getAll();

        ArrayList<Entry> entry_chart = new ArrayList<>();
        Comparator<String> comparator = (s1, s2) -> s1.compareTo(s2);
        Map<String, String> map = new TreeMap<>(comparator);

        for(Map.Entry<String,?> entry : keys.entrySet()){
            String key = String.valueOf(entry.getKey());
            String value = String.valueOf(entry.getValue());

            map.put(key,value);
        }


        for (Map.Entry<String, String> entry : map.entrySet()) {
            float key=Float.valueOf(entry.getKey());
            float value=Float.valueOf(entry.getValue());
            entry_chart.add(new Entry(key,value));
        }


        Log.d("array",Arrays.deepToString(entry_chart.toArray()));
        LineDataSet lineDataSet1 = new LineDataSet(entry_chart, "LineGraph1"); // 데이터가 담긴 Arraylist 를 LineDataSet 으로 변환한다.

        lineDataSet1.setColor(Color.RED); // 해당 LineDataSet의 색 설정 :: 각 Line 과 관련된 세팅은 여기서 설정한다.
        XAxis.XAxisPosition position = XAxis.XAxisPosition.BOTTOM;
        xAxis.setPosition(position);
        chartData.addDataSet(lineDataSet1); // 해당 LineDataSet 을 적용될 차트에 들어갈 DataSet 에 넣는다.
        lineChart.setData(chartData); // 차트에 위의 DataSet을 넣는다.
        lineChart.invalidate(); // 차트 업데이트
        lineChart.setTouchEnabled(false);
 //https://github.com/PhilJay/MPAndroidChart/issues/789
    }

}