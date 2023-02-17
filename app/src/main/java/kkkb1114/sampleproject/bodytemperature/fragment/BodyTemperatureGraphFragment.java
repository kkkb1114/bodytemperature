package kkkb1114.sampleproject.bodytemperature.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import kkkb1114.sampleproject.bodytemperature.R;
import kkkb1114.sampleproject.bodytemperature.timeline.TimelineAdapter;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;


public class BodyTemperatureGraphFragment extends Fragment {

    public LineChart lineChart;
    private SharedPreferences preferences;
    private SharedPreferences preferences2;
    Context context;
    long now;
    Date date;
    SimpleDateFormat dateFormat;
    String str;
    RecyclerView rv_timeline;
    TimelineAdapter timelineAdapter;
    TextView tv_graphdate;
    MaterialDatePicker materialDatePicker;




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
        preferences2 = context.getSharedPreferences(dateFormat.format(date)+"timelineData",MODE_PRIVATE);



        View view = inflater.inflate(R.layout.fragment_body_temperature_graph, container, false);
        initView(view);
        this.setListner();
        tv_graphdate.setText(dateFormat.format(date));


        return view;
    }

    public void initView(View view){
        tv_graphdate=view.findViewById(R.id.tv_graphdate);
        lineChart = view.findViewById(R.id.chart);
        rv_timeline=view.findViewById(R.id.rv_timeline_list);
        showChart(preferences,view);
        setRecyclerView(preferences2,view);
    }

    public void showChart(SharedPreferences preference,View view) {

        lineChart = new LineChart(getActivity());
        lineChart = view.findViewById(R.id.chart);
        XAxis xAxis = lineChart.getXAxis();

        LineData chartData = new LineData();
        Map<String,?> keys = preference.getAll();

        if(keys.size()==0) {
            lineChart.setData(null);
            lineChart.invalidate();
            return;
        }

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

    public void setRecyclerView(SharedPreferences preference, View view){

        ArrayList<String> ad = new ArrayList<>();
        Map<String,?> keys = preference.getAll();
        ArrayList<Entry> entry_arr = new ArrayList<>();
        rv_timeline=view.findViewById(R.id.rv_timeline_list);
        rv_timeline.setVisibility(View.VISIBLE);

        if(keys.size()==0) {
            rv_timeline.setVisibility(View.INVISIBLE);
            return;
        }



        timelineAdapter = new TimelineAdapter(ad);
        rv_timeline.setLayoutManager(new LinearLayoutManager(context));
        rv_timeline.setAdapter(new TimelineAdapter(ad));
        timelineAdapter.notifyDataSetChanged();

        Comparator<String> comparator = (s1, s2) -> s1.compareTo(s2);
        Map<String, String> map = new TreeMap<>(comparator);

        for(Map.Entry<String,?> entry : keys.entrySet()){
            String key = String.valueOf(entry.getKey());
            String value = String.valueOf(entry.getValue());

            map.put(key,value);
        }


        int i=0;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value=entry.getValue();
            ad.add(key+" "+value);
            i++;
        }

    }

    public void setListner(){
        tv_graphdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMaterialDatePicker();
                MaterialDatePickerClick();
            }
        });

    }

    public void showMaterialDatePicker(){
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        // 오늘 날짜
        Long today = MaterialDatePicker.todayInUtcMilliseconds();

        materialDatePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Date Picker")
                .setSelection(today).build();

        materialDatePicker.show(getActivity().getSupportFragmentManager(), "DatePicker");
    }

    public void MaterialDatePickerClick(){
        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override
            public void onPositiveButtonClick(Long selection) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date();
                date.setTime(selection);

                String dateStr = simpleDateFormat.format(date);
                tv_graphdate.setText(dateStr);

                SharedPreferences pref = context.getSharedPreferences(dateStr+"tempData",MODE_PRIVATE);
                SharedPreferences pref2 = context.getSharedPreferences(dateStr+"timelineData",MODE_PRIVATE);



                showChart(pref,getView());
                setRecyclerView(pref2,getView());
                Log.d("pref",dateStr+"timelineData" );
            }
        });
    }

}