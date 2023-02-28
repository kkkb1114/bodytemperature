package kkkb1114.sampleproject.bodytemperature.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

import kkkb1114.sampleproject.bodytemperature.MainActivity;
import kkkb1114.sampleproject.bodytemperature.R;
import kkkb1114.sampleproject.bodytemperature.timeline.TimelineAdapter;


public class BodyTemperatureGraphFragment extends Fragment {

    public LineChart lineChart;
    public LineChart lineChart2;
    Context context;
    RecyclerView rv_timeline;
    TimelineAdapter timelineAdapter;
    TextView tv_graphdate;
    TextView tv_period;

    NumberPicker np_period_integer;
    DatePicker np_periodDate;
    Button bt_periodCal;


    LinearLayout period_layout;
    MaterialDatePicker materialDatePicker;
    String username;

    View view;

    SharedPreferences select_user;

    SQLiteDatabase sqlDB;

    Cursor cursor;





    public BodyTemperatureGraphFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        ArrayList<Entry> entry_chart = new ArrayList<>();

        setUser();
        View view = inflater.inflate(R.layout.fragment_body_temperature_graph, container, false);
        initView(view);
        this.setListner();

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        tv_graphdate.setText(dateFormat.format(date));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setUser();
    }

    public void initView(View view){
        tv_graphdate=view.findViewById(R.id.tv_graphdate);
        tv_period=view.findViewById(R.id.tv_period);
        lineChart = view.findViewById(R.id.chart);
        lineChart2 = view.findViewById(R.id.Pchart);
        rv_timeline=view.findViewById(R.id.rv_timeline_list);
        period_layout=view.findViewById(R.id.period_layout);
        np_period_integer=view.findViewById(R.id.np_period_integer);
        bt_periodCal=view.findViewById(R.id.bt_periodCal);
        np_periodDate=view.findViewById(R.id.np_periodDate);
        setPicker();

        long now =System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String str = dateFormat.format(date);


        String userpurpose = select_user.getString("userPurpose"," ");
        if(userpurpose.equals("감염"))
        {
            tv_graphdate.setVisibility(View.VISIBLE);
            rv_timeline.setVisibility(View.VISIBLE);
            lineChart.setVisibility(View.VISIBLE);
            lineChart2.setVisibility(View.INVISIBLE);
            tv_period.setVisibility(View.INVISIBLE);
            period_layout.setVisibility(View.INVISIBLE);
            showChart(view,str);
            setRecyclerView(view,str);

        }
        else if(userpurpose.equals("배란"))
        {
            tv_graphdate.setVisibility(View.INVISIBLE);
            rv_timeline.setVisibility(View.INVISIBLE);
            lineChart.setVisibility(View.INVISIBLE);
            lineChart2.setVisibility(View.VISIBLE);
            tv_period.setVisibility(View.VISIBLE);
            period_layout.setVisibility(View.VISIBLE);
            showPchart(view);

        }

    }

    public void showChart(View view , String date) {

        sqlDB = MainActivity.bodytemp_dbHelper.getReadableDatabase();
        HashMap<String,Double> keys = new HashMap<String, Double>();
        lineChart = new LineChart(getActivity());
        lineChart = view.findViewById(R.id.chart);
        XAxis xAxis = lineChart.getXAxis();
        String username = select_user.getString("userName","선택된 사용자 없음");

        cursor = sqlDB.rawQuery("SELECT * FROM TEMPDATA WHERE tempDateTime LIKE '"+date+"%' AND name = '"+username+"'; ", null);

        while(cursor.moveToNext()) {
            keys.put(cursor.getString(2).substring(11), cursor.getDouble(1));
        }

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

    public void setRecyclerView(View view,String date){
        sqlDB = MainActivity.bodytemp_dbHelper.getReadableDatabase();
        ArrayList<String> ad = new ArrayList<>();
        HashMap<String,String> keys = new HashMap<String, String>();
        ArrayList<Entry> entry_arr = new ArrayList<>();
        rv_timeline=view.findViewById(R.id.rv_timeline_list);
        rv_timeline.setVisibility(View.VISIBLE);

        String username = select_user.getString("userName","선택된 사용자 없음");
        cursor = sqlDB.rawQuery("SELECT * FROM TIMELINEDATA WHERE TimelineDateTime LIKE '"+date+"%' AND name = '"+username+"'; ", null);

        while(cursor.moveToNext()) {
            keys.put(cursor.getString(2).substring(11), cursor.getString(1));
        }

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

        bt_periodCal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    select_user = context.getSharedPreferences("login_user",MODE_PRIVATE);
                    username = select_user.getString("userName","선택된 사용자 없음");

                    sqlDB = MainActivity.bodytemp_dbHelper.getWritableDatabase();
                    String str = np_periodDate.getYear() + "-" +np_periodDate.getMonth() + "-"+np_periodDate.getDayOfMonth();
                    cursor = sqlDB.rawQuery("SELECT * FROM OVULDATA WHERE name = '"+username+"'; ", null);
                    String s=null;
                    while(cursor.moveToNext()) {
                        s = cursor.getString(0);
                    }
                    if(s==null)
                        sqlDB.execSQL("INSERT INTO OVULDATA VALUES ('"+username+"', '"+np_period_integer.getValue()+"', '"+ str +"');");
                    else
                        sqlDB.execSQL(
                                "UPDATE OVULDATA SET " +
                                        "period = '" + np_period_integer.getValue() + "'," +
                                        "ovulDateTime = '" + str + "'" +
                                        " WHERE name = '" + username + "'"
                        );

                    showPchart(getView());
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

                select_user = context.getSharedPreferences("login_user",MODE_PRIVATE);
                String username = select_user.getString("userName","선택된 사용자 없음");


                showChart(getView(),dateStr);
                setRecyclerView(getView(),dateStr);
            }
        });
    }

    public void setUser()
    {
        context = getContext();


        select_user = context.getSharedPreferences("login_user",MODE_PRIVATE);
        String username = select_user.getString("userName","선택된 사용자 없음");


        if(username.equals("선택된 사용자 없음"))
        {
            Toast.makeText(context, "사용자 등록을 완료해주세요.", Toast.LENGTH_SHORT).show();
        }

    }

    public void setPicker() {
        np_period_integer.setMinValue(14);
        np_period_integer.setMaxValue(40);

    }

    public void showPchart(View view)
    {

        sqlDB = MainActivity.bodytemp_dbHelper.getReadableDatabase();
        lineChart2 = new LineChart(getActivity());
        lineChart2 = view.findViewById(R.id.Pchart);
        XAxis xAxis = lineChart2.getXAxis();
        String username = select_user.getString("userName","선택된 사용자 없음");

        String day = null;
        String month = null;
        
        cursor = sqlDB.rawQuery("SELECT * FROM OVULDATA WHERE name = '"+username+"'; ", null);
        while(cursor.moveToNext()) {
           day = cursor.getString(2).substring(7);
           month=cursor.getString(2).substring(7);
        }

        if(day==null)
        {
            lineChart2.setData(null);
            lineChart2.invalidate();
            return;
        }

        LineData lineData = new LineData();

        ArrayList<Entry> entry_chart_Y = new ArrayList<>();
        ArrayList<String> entry_chart_X = new ArrayList<>();


        int i=0;
        int num= Integer.parseInt(day);


        while(i<45)
        {
            if(month.startsWith("1")||month.startsWith("3")||month.startsWith("5")||month.startsWith("7")||month.startsWith("8")||month.startsWith("10")||month.startsWith("12"))
            {
                if(num==31)
                {
                    entry_chart_X.add(String.valueOf(num));
                    num=1;
                }
                entry_chart_X.add(String.valueOf(num));
                num++;
                i++;
            }
            else if(month.startsWith("2"))
            {
                if(num==28)
                {
                    entry_chart_X.add(String.valueOf(num));
                    num=1;
                }
                entry_chart_X.add(String.valueOf(num));
                num++;
                i++;
            }
            else{
                if(num==30)
                {
                    entry_chart_X.add(String.valueOf(num));
                    num=1;
                }
                entry_chart_X.add(String.valueOf(num));
                num++;
                i++;
            }

        }


        float [] temp = {36.7f,36.66f,36.6f,36.55f,36.6f,36.55f,36.55f,36.65f,36.6f,36.65f,36.6f,36.55f,36.6f,36.5f,36.7f,36.75f,36.85f,36.95f,36.95f,36.85f,36.8f,36.85f,36.95f,36.95f,36.9f,36.95f,37.0f,37.0f,36.95f,37.0f,37.05f,37.1f,37.0f,37.0f,37.05f,36.95f,36.9f,36.95f,37.0f,37.05f,36.95f,37.0f,37.05f,37.05f,37.1f};

        for(int j=0; j<temp.length; j++) {
            entry_chart_Y.add(new Entry(j, temp[j]));
        }


        LineDataSet lineDataSet = new LineDataSet(entry_chart_Y, "표준"); // 데이터가 담긴 Arraylist 를 LineDataSet 으로 변환한다.
        lineDataSet.setColor(Color.BLUE); // 해당 LineDataSet의 색 설정 :: 각 Line 과 관련된 세팅은 여기서 설정한다.
        lineDataSet.setLineWidth(3);


        XAxis.XAxisPosition position = XAxis.XAxisPosition.BOTTOM;
        xAxis.setPosition(position);// x 축 설정
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(entry_chart_X));


        lineData.addDataSet(lineDataSet); // 해당 LineDataSet 을 적용될 차트에 들어갈 DataSet 에 넣는다.
        lineChart2.setData(lineData); // 차트에 위의 DataSet을 넣는다.
        lineChart2.getDescription().setEnabled(false);
        lineChart2.invalidate(); // 차트 업데이트
        lineChart2.setTouchEnabled(false);


    }


}