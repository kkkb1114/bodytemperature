package kkkb1114.sampleproject.bodytemperature.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
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
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import kkkb1114.sampleproject.bodytemperature.MainActivity;
import kkkb1114.sampleproject.bodytemperature.R;
import kkkb1114.sampleproject.bodytemperature.graph.MyMarkerView;
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

    int Dday=0;

    String Sdate;

    int Stime;

    String InflaType;






    public BodyTemperatureGraphFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        ArrayList<Entry> entry_chart = new ArrayList<>();

        setUser();
        View view = inflater.inflate(R.layout.fragment_body_temperature_graph, container, false);
        try {
            initView(view);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
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

    public void initView(View view) throws ParseException {
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
        String Sdate = select_user.getString("surgeryDate"," ");
        InflaType = select_user.getString("userInfection"," ");

            tv_graphdate.setVisibility(View.INVISIBLE);
            rv_timeline.setVisibility(View.INVISIBLE);
            lineChart.setVisibility(View.INVISIBLE);
            lineChart2.setVisibility(View.VISIBLE);
            tv_period.setVisibility(View.VISIBLE);
            period_layout.setVisibility(View.VISIBLE);
            showPchart(view);



    }



    public void setListner(){


        bt_periodCal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    select_user = context.getSharedPreferences("login_user",MODE_PRIVATE);
                    username = select_user.getString("userName","선택된 사용자 없음");

                    sqlDB = MainActivity.bodytemp_dbHelper.getWritableDatabase();

                    Calendar calendar = Calendar.getInstance();

                //---sets the time for the alarm to trigger---
                    calendar.set( Calendar.YEAR, np_periodDate.getYear());
                    calendar.set( Calendar.MONTH, np_periodDate.getMonth() );
                    calendar.set( Calendar.DAY_OF_MONTH, np_periodDate.getDayOfMonth());
                    int year = calendar.get( Calendar.YEAR )-1900;
                    int month = calendar.get( Calendar.MONTH );
                    int day = calendar.get( Calendar.DAY_OF_MONTH );
                    Date d = new Date(year,month,day);


                    SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
                    String strDate = dateFormatter.format(d);

                    cursor = sqlDB.rawQuery("SELECT * FROM OVULDATA WHERE name = '"+username+"'; ", null);
                    String s=null;
                    while(cursor.moveToNext()) {
                        s = cursor.getString(0);
                    }
                    if(s==null)
                        sqlDB.execSQL("INSERT INTO OVULDATA VALUES ('"+username+"', '"+np_period_integer.getValue()+"', '"+ strDate +"');");
                    else
                        sqlDB.execSQL(
                                "UPDATE OVULDATA SET " +
                                        "period = '" + np_period_integer.getValue() + "'," +
                                        "ovulDateTime = '" + strDate + "'" +
                                        " WHERE name = '" + username + "'"
                        );

                try {
                    showPchart(getView());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
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
        np_period_integer.setMinValue(20);
        np_period_integer.setMaxValue(40);

    }

    public void showPchart(View view) throws ParseException {

        sqlDB = MainActivity.bodytemp_dbHelper.getReadableDatabase();
        lineChart2 = new LineChart(getActivity());
        lineChart2 = view.findViewById(R.id.Pchart);


        XAxis xAxis = lineChart2.getXAxis();
        String username = select_user.getString("userName","선택된 사용자 없음");

        String day = null;
        String month = null;
        String year=null;
        int period = 0;

        cursor = sqlDB.rawQuery("SELECT * FROM OVULDATA WHERE name = '"+username+"'; ", null);
        while(cursor.moveToNext()) {
           day = cursor.getString(2).substring(8);
           month=cursor.getString(2).substring(5);
           year=cursor.getString(2).substring(0,10);
           period = cursor.getInt(1);
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
        ArrayList<Entry> entry_chart_Y2 = new ArrayList<>();


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
        float[] temperatures20 = {36.6f, 36.65f, 36.6f, 36.55f, 36.6f, 36.3f, 36.7f, 36.75f, 36.85f, 36.95f, 36.95f, 36.85f, 36.8f, 36.85f, 36.95f, 36.95f, 36.9f, 36.95f, 37.0f, 37.0f, 36.95f, 37.0f, 37.05f, 37.1f, 37.0f, 37.0f, 37.05f, 36.95f, 36.9f, 36.95f, 37.0f, 37.05f, 36.95f, 37.0f, 37.05f, 37.05f, 37.1f, 37.1f ,37.15f, 37.15f, 37.1f, 37.15f, 37.2f, 37.2f,37.25f};
        float[] temperatures21 = {36.65f, 36.6f, 36.65f, 36.6f, 36.55f, 36.6f, 36.3f, 36.7f, 36.75f, 36.85f, 36.95f, 36.95f, 36.85f, 36.8f, 36.85f, 36.95f, 36.95f, 36.9f, 36.95f, 37.0f, 37.0f, 36.95f, 37.0f, 37.05f, 37.1f, 37.0f, 37.0f, 37.05f, 36.95f, 36.9f, 36.95f, 37.0f, 37.05f, 36.95f, 37.0f, 37.05f, 37.05f, 37.1f, 37.1f , 37.15f, 37.15f, 37.1f, 37.15f, 37.2f, 37.2f};
        float[] temperatures22 = {36.55f, 36.65f, 36.6f, 36.65f, 36.6f, 36.55f, 36.6f, 36.3f, 36.7f, 36.75f, 36.85f, 36.95f, 36.95f, 36.85f, 36.8f, 36.85f, 36.95f, 36.95f, 36.9f, 36.95f, 37.0f, 37.0f, 36.95f, 37.0f, 37.05f, 37.1f, 37.0f, 37.0f, 37.05f, 36.95f, 36.9f, 36.95f, 37.0f, 37.05f, 36.95f, 37.0f, 37.05f, 37.05f, 37.1f, 37.1f, 37.15f, 37.15f, 37.1f, 37.15f, 37.2f};
        float[] temperatures23 = {36.55f, 36.55f, 36.65f, 36.6f, 36.65f, 36.6f, 36.55f, 36.6f, 36.3f, 36.7f, 36.75f, 36.85f, 36.95f, 36.95f, 36.85f, 36.8f, 36.85f, 36.95f, 36.95f, 36.9f, 36.95f, 37.0f, 37.0f, 36.95f, 37.0f, 37.05f, 37.1f, 37.0f, 37.0f, 37.05f, 36.95f, 36.9f, 36.95f, 37.0f, 37.05f, 36.95f, 37.0f, 37.05f, 37.05f, 37.1f, 37.1f, 37.15f, 37.15f, 37.1f, 37.15f};
        float[] temperatures24 = {36.6f, 36.55f, 36.55f, 36.65f, 36.6f, 36.65f, 36.6f, 36.55f, 36.6f, 36.3f, 36.7f, 36.75f, 36.85f, 36.95f, 36.95f, 36.85f, 36.8f, 36.85f, 36.95f, 36.95f, 36.9f, 36.95f, 37.0f, 37.0f, 36.95f, 37.0f, 37.05f, 37.1f, 37.0f, 37.0f, 37.05f, 36.95f, 36.9f, 36.95f, 37.0f, 37.05f, 36.95f, 37.0f, 37.05f, 37.05f, 37.1f, 37.1f, 37.15f, 37.15f, 37.1f};
        float[] temperatures25 = {36.55f, 36.6f, 36.55f, 36.55f, 36.65f, 36.6f, 36.65f, 36.6f, 36.55f, 36.6f, 36.3f, 36.7f, 36.75f, 36.85f, 36.95f, 36.95f, 36.85f, 36.8f, 36.85f, 36.95f, 36.95f, 36.9f, 36.95f, 37.0f, 37.0f, 36.95f, 37.0f, 37.05f, 37.1f, 37.0f, 37.0f, 37.05f, 36.95f, 36.9f, 36.95f, 37.0f, 37.05f, 36.95f, 37.0f, 37.05f, 37.05f, 37.1f, 37.15f, 37.15f, 37.1f};
        float[] temperatures26 = {36.6f, 36.55f, 36.6f, 36.55f, 36.55f, 36.65f, 36.6f, 36.65f, 36.6f, 36.55f, 36.6f, 36.3f, 36.7f, 36.75f, 36.85f, 36.95f, 36.95f, 36.85f, 36.8f, 36.85f, 36.95f, 36.95f, 36.9f, 36.95f, 37.0f, 37.0f, 36.95f, 37.0f, 37.05f, 37.1f, 37.0f, 37.0f, 37.05f, 36.95f, 36.9f, 36.95f, 37.0f, 37.05f, 36.95f, 37.0f, 37.05f, 37.05f, 37.1f, 37.15f, 37.15f};
        float[] temperatures27 = {36.66f, 36.6f, 36.55f, 36.6f, 36.55f, 36.55f, 36.65f, 36.6f, 36.65f, 36.6f, 36.55f, 36.6f, 36.3f, 36.7f, 36.75f, 36.85f, 36.95f, 36.95f, 36.85f, 36.8f, 36.85f, 36.95f, 36.95f, 36.9f, 36.95f, 37.0f, 37.0f, 36.95f, 37.0f, 37.05f, 37.1f, 37.0f, 37.0f, 37.05f, 36.95f, 36.9f, 36.95f, 37.0f, 37.05f, 36.95f, 37.0f, 37.05f, 37.05f, 37.1f, 37.15f};
        float[] temperatures28 = {36.7f, 36.66f, 36.6f, 36.55f, 36.6f, 36.55f, 36.55f, 36.65f, 36.6f, 36.65f, 36.6f, 36.55f, 36.6f, 36.3f, 36.7f, 36.75f, 36.85f, 36.95f, 36.95f, 36.85f, 36.8f, 36.85f, 36.95f, 36.95f, 36.9f, 36.95f, 37.0f, 37.0f, 36.95f, 37.0f, 37.05f, 37.1f, 37.0f, 37.0f, 37.05f, 36.95f, 36.9f, 36.95f, 37.0f, 37.05f, 36.95f, 37.0f, 37.05f, 37.05f, 37.1f};
        float[] temperatures29 = {36.7f, 36.66f, 36.6f, 36.55f, 36.6f, 36.55f, 36.55f, 36.65f, 36.6f, 36.65f, 36.6f, 36.55f, 36.6f, 36.55f, 36.3f, 36.7f, 36.75f, 36.85f, 36.95f, 36.95f, 36.85f, 36.8f, 36.85f, 36.95f, 36.95f, 36.9f, 36.95f, 37.0f, 37.0f, 36.95f, 37.0f, 37.05f, 37.1f, 37.0f, 37.0f, 37.05f, 36.95f, 36.9f, 36.95f, 37.0f, 37.05f, 36.95f, 37.0f, 37.05f, 37.05f};
        float[] temperatures30 = {36.7f, 36.66f, 36.6f, 36.55f, 36.6f, 36.55f, 36.55f, 36.65f, 36.6f, 36.65f, 36.6f, 36.55f, 36.6f, 36.6f, 36.55f, 36.3f, 36.7f, 36.75f, 36.85f, 36.95f, 36.95f, 36.85f, 36.8f, 36.85f, 36.95f, 36.95f, 36.9f, 36.95f, 37.0f, 37.0f, 36.95f, 37.0f, 37.05f, 37.1f, 37.0f, 37.0f, 37.05f, 36.95f, 36.9f, 36.95f, 37.0f, 37.05f, 36.95f, 37.0f, 37.05f};
        float[] temperatures31 = {36.7f, 36.66f, 36.6f, 36.55f, 36.6f, 36.55f, 36.55f, 36.65f, 36.6f, 36.65f, 36.6f, 36.55f, 36.6f, 36.7f, 36.5f, 36.55f, 36.3f, 36.7f, 36.75f, 36.85f, 36.95f, 36.95f, 36.85f, 36.8f, 36.85f, 36.95f, 36.95f, 36.9f, 36.95f, 37.0f, 37.0f, 36.95f, 37.0f, 37.05f, 37.1f, 37.0f, 37.0f, 37.05f, 36.95f, 36.9f, 36.95f, 37.0f, 37.05f, 36.95f, 37.0f};
        float[] temperatures32 = {36.7f, 36.66f, 36.6f, 36.55f, 36.6f, 36.55f, 36.55f, 36.65f, 36.6f, 36.65f, 36.6f, 36.55f, 36.6f, 36.5f, 36.6f, 36.55f, 36.6f, 36.3f, 36.7f, 36.75f, 36.85f, 36.95f, 36.95f, 36.85f, 36.8f, 36.85f, 36.95f, 36.95f, 36.9f, 36.95f, 37.0f, 37.0f, 36.95f, 37.0f, 37.05f, 37.1f, 37.0f, 37.0f, 37.05f, 36.95f, 36.9f, 36.95f, 37.0f, 37.05f, 36.95f};
        float[] temperatures33 = {36.7f, 36.66f, 36.6f, 36.55f, 36.6f, 36.55f, 36.55f, 36.65f, 36.6f, 36.65f, 36.6f, 36.55f, 36.6f, 36.5f, 36.6f, 36.55f, 36.5f, 36.5f, 36.3f, 36.7f, 36.75f, 36.85f, 36.95f, 36.95f, 36.85f, 36.8f, 36.85f, 36.95f, 36.95f, 36.9f, 36.95f, 37.0f, 37.0f, 36.95f, 37.0f, 37.05f, 37.1f, 37.0f, 37.0f, 37.05f, 36.95f, 36.9f, 36.95f, 37.0f, 37.05f};
        float[] temperatures34 = {36.7f, 36.66f, 36.6f, 36.55f, 36.6f, 36.55f, 36.55f, 36.65f, 36.6f, 36.65f, 36.6f, 36.55f, 36.6f, 36.5f, 36.5f, 36.55f, 36.6f, 36.5f, 36.55f, 36.3f, 36.7f, 36.75f, 36.85f, 36.95f, 36.95f, 36.85f, 36.8f, 36.85f, 36.95f, 36.95f, 36.9f, 36.95f, 37.0f, 37.0f, 36.95f, 37.0f, 37.05f, 37.1f, 37.0f, 37.0f, 37.05f, 36.95f, 36.9f, 36.95f, 37.0f};
        float[] temperatures35 = {36.7f, 36.66f, 36.6f, 36.55f, 36.6f, 36.55f, 36.55f, 36.65f, 36.6f, 36.65f, 36.6f, 36.55f, 36.6f, 36.5f, 36.5f, 36.66f, 36.6f, 36.5f, 36.55f, 36.55f, 36.3f, 36.7f, 36.75f, 36.85f, 36.95f, 36.95f, 36.85f, 36.8f, 36.85f, 36.95f, 36.95f, 36.9f, 36.95f, 37.0f, 37.0f, 36.95f, 37.0f, 37.05f, 37.1f, 37.0f, 37.0f, 37.05f, 36.95f, 36.9f, 36.95f};
        float[] temperatures36 = {36.7f, 36.66f, 36.6f, 36.55f, 36.6f, 36.55f, 36.55f, 36.65f, 36.6f, 36.65f, 36.6f, 36.55f, 36.6f, 36.5f, 36.5f, 36.66f, 36.6f, 36.5f, 36.55f, 36.56f, 36.55f, 36.3f, 36.7f, 36.75f, 36.85f, 36.95f, 36.95f, 36.85f, 36.8f, 36.85f, 36.95f, 36.95f, 36.9f, 36.95f, 37.0f, 37.0f, 36.95f, 37.0f, 37.05f, 37.1f, 37.0f, 37.0f, 37.05f, 36.95f, 36.9f};
        float[] temperatures37 = {36.7f, 36.66f, 36.6f, 36.55f, 36.6f, 36.55f, 36.55f, 36.65f, 36.6f, 36.65f, 36.6f, 36.55f, 36.6f, 36.5f, 36.5f, 36.55f, 36.6f, 36.5f, 36.55f, 36.56f, 36.55f, 36.53f, 36.3f, 36.7f, 36.75f, 36.85f, 36.95f, 36.95f, 36.85f, 36.8f, 36.85f, 36.95f, 36.95f, 36.9f, 36.95f, 37.0f, 37.0f, 36.95f, 37.0f, 37.05f, 37.1f, 37.0f, 37.0f, 37.05f, 36.95f};
        float[] temperatures38 = {36.7f, 36.66f, 36.6f, 36.55f, 36.6f, 36.55f, 36.55f, 36.65f, 36.6f, 36.65f, 36.6f, 36.55f, 36.6f, 36.5f, 36.5f, 36.5f, 36.55f, 36.6f, 36.5f, 36.55f, 36.55f, 36.5f , 36.53f, 36.3f, 36.7f, 36.75f, 36.85f, 36.95f, 36.95f, 36.85f, 36.8f, 36.85f, 36.95f, 36.95f, 36.9f, 36.95f, 37.0f, 37.0f, 36.95f, 37.0f, 37.05f, 37.1f, 37.0f, 37.0f, 37.05f};
        float[] temperatures39 = {36.7f, 36.66f, 36.6f, 36.55f, 36.6f, 36.55f, 36.55f, 36.65f, 36.6f, 36.65f, 36.6f, 36.55f, 36.6f, 36.5f, 36.5f, 36.5f, 36.55f, 36.6f, 36.5f, 36.55f, 36.55f, 36.5f , 36.53f, 36.5f, 36.3f, 36.7f, 36.75f, 36.85f, 36.95f, 36.95f, 36.85f, 36.8f, 36.85f, 36.95f, 36.95f, 36.9f, 36.95f, 37.0f, 37.0f, 36.95f, 37.0f, 37.05f, 37.1f, 37.0f, 37.0f};
        float[] temperatures40 = {36.7f, 36.66f, 36.6f, 36.55f, 36.6f, 36.55f, 36.55f, 36.65f, 36.6f, 36.65f, 36.6f, 36.55f, 36.6f, 36.5f, 36.5f, 36.5f, 36.55f, 36.6f, 36.5f, 36.55f, 36.55f, 36.5f , 36.53f, 36.5f, 36.5f, 36.3f, 36.7f, 36.75f, 36.85f, 36.95f, 36.95f, 36.85f, 36.8f, 36.85f, 36.95f, 36.95f, 36.9f, 36.95f, 37.0f, 37.0f, 36.95f, 37.0f, 37.05f, 37.1f, 37.0f};

        for(int j=0; j<45; j++) {
            if(period==20)
                entry_chart_Y.add(new Entry(j, temperatures20[j]));
            else if(period==21)
                entry_chart_Y.add(new Entry(j, temperatures21[j]));
            else if(period==22)
                entry_chart_Y.add(new Entry(j, temperatures22[j]));
            else if(period==23)
                entry_chart_Y.add(new Entry(j, temperatures23[j]));
            else if(period==24)
                entry_chart_Y.add(new Entry(j, temperatures24[j]));
            else if(period==25)
                entry_chart_Y.add(new Entry(j, temperatures25[j]));
            else if(period==26)
                entry_chart_Y.add(new Entry(j, temperatures26[j]));
            else if(period==27)
                entry_chart_Y.add(new Entry(j, temperatures27[j]));
            else if(period==28)
                entry_chart_Y.add(new Entry(j, temperatures28[j]));
            else if(period==29)
                entry_chart_Y.add(new Entry(j, temperatures29[j]));
            else if(period==30)
                entry_chart_Y.add(new Entry(j, temperatures30[j]));
            else if(period==31)
                entry_chart_Y.add(new Entry(j, temperatures31[j]));
            else if(period==32)
                entry_chart_Y.add(new Entry(j, temperatures32[j]));
            else if(period==33)
                entry_chart_Y.add(new Entry(j, temperatures33[j]));
            else if(period==34)
                entry_chart_Y.add(new Entry(j, temperatures34[j]));
            else if(period==35)
                entry_chart_Y.add(new Entry(j, temperatures35[j]));
            else if(period==36)
                entry_chart_Y.add(new Entry(j, temperatures36[j]));
            else if(period==37)
                entry_chart_Y.add(new Entry(j, temperatures37[j]));
            else if(period==38)
                entry_chart_Y.add(new Entry(j, temperatures38[j]));
            else if(period==39)
                entry_chart_Y.add(new Entry(j, temperatures39[j]));
            else if(period==40)
                entry_chart_Y.add(new Entry(j, temperatures40[j]));
        }

        for(int j=0; j<45; j++)
        {
            if(entry_chart_Y.get(j).getY()-36.3f==0)
                Dday=j;
        }


        for(int j=0; j<45; j++)
        {
            double val=0;
            int count=0;
            cursor = sqlDB.rawQuery("SELECT * FROM TEMPDATA WHERE name = '"+username+"' AND tempDateTime LIKE '"+year+"%'; ", null);
            while(cursor.moveToNext()) {
                val+=cursor.getDouble(1);
                count++;
            }

            if(val!=0)
                entry_chart_Y2.add(new Entry(j,(float)val/count));

            LocalDate date = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                date = LocalDate.parse(year);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                year= String.valueOf(date.plusDays(1));
            }
        }


        LineDataSet lineDataSet = new LineDataSet(entry_chart_Y, "표준"); // 데이터가 담긴 Arraylist 를 LineDataSet 으로 변환한다.
        lineDataSet.setColor(Color.BLUE); // 해당 LineDataSet의 색 설정 :: 각 Line 과 관련된 세팅은 여기서 설정한다.
        lineDataSet.setLineWidth(3);


        if(!entry_chart_Y2.isEmpty()) {
            LineDataSet lineDataSet2 = new LineDataSet(entry_chart_Y2, "측정");
            lineDataSet2.setColor(Color.RED);
            lineDataSet2.setLineWidth(3);
            lineData.addDataSet(lineDataSet2);
        }
        else{
            Toast.makeText(context.getApplicationContext(), "아직 측정된 체온이 없습니다.", Toast.LENGTH_SHORT).show();
        }



        XAxis.XAxisPosition position = XAxis.XAxisPosition.BOTTOM;
        xAxis.setPosition(position);// x 축 설정
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(entry_chart_X));


        lineData.addDataSet(lineDataSet); // 해당 LineDataSet 을 적용될 차트에 들어갈 DataSet 에 넣는다.

        lineChart2.setData(lineData); // 차트에 위의 DataSet을 넣는다.
        lineChart2.getDescription().setEnabled(false);
        lineChart2.invalidate(); // 차트 업데이트
        lineChart2.setTouchEnabled(true);
        lineChart2.setScaleEnabled(true);
        lineChart2.setPinchZoom(true);
        MyMarkerView marker = new MyMarkerView(context,R.layout.markerview,Dday);
        marker.setChartView(lineChart2);
        lineChart2.setMarker(marker);
    }




}