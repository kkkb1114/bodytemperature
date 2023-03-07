package kkkb1114.sampleproject.bodytemperature.activity;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import kkkb1114.sampleproject.bodytemperature.API.OpenApi;
import kkkb1114.sampleproject.bodytemperature.MainActivity;
import kkkb1114.sampleproject.bodytemperature.Notification.AlarmReceiver;
import kkkb1114.sampleproject.bodytemperature.R;
import kkkb1114.sampleproject.bodytemperature.pill.PillAdapter;

public class PillActivity extends AppCompatActivity{

    Context context;
    Button bt_pill_cancel, bt_pill_confirm, bt_pillSearch;
    EditText edt_pillSearch;
    SQLiteDatabase sqlDB;
    View view;
    RecyclerView rv_pill;

    PillAdapter pillAdapter;
    String username;

    String searchText;

    SharedPreferences daymax;
    float imax;
    float amax;

    Cursor cursor;


    ArrayList<String> ad = new ArrayList<>();
    ArrayList<String> af = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pill);
        context = this;

        SharedPreferences select_user;
        select_user = context.getSharedPreferences("login_user",MODE_PRIVATE);
        username = select_user.getString("userName","선택된 사용자 없음");

        SharedPreferences daymax;
        daymax=context.getSharedPreferences("DayMax",MODE_PRIVATE);
        imax=daymax.getFloat("ibuprofen",1);
        amax=daymax.getFloat("acetaminophen",1);


        initView();
        setRecyclerView(view);
        setListner();

    }

    private void initView() {
        bt_pill_cancel= (Button)findViewById(R.id.bt_pill_cancle);
        bt_pill_confirm= (Button)findViewById(R.id.bt_pill_confirm);
        bt_pillSearch= (Button)findViewById(R.id.bt_pillSearch);
        edt_pillSearch=(EditText) findViewById(R.id.edt_pillSearch);

    }

    public void setListner() {

        bt_pillSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edt_pillSearch.getText().toString().length()!=0) {
                       searchText = edt_pillSearch.getText().toString();
                       String targetName= edt_pillSearch.getText().toString();
                       ad.clear();
                       af.clear();
                       OpenApi pill = new OpenApi(targetName,ad,af,pillAdapter,context,rv_pill);
                       pill.execute();


                }
                else{

                }

            }

        });


        bt_pill_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final LinearLayout linear = (LinearLayout) View.inflate(context, R.layout.dialog_pill, null);
                AlertDialog dialog = new AlertDialog.Builder(context).setView(linear)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                EditText edt_pill = (EditText) linear.findViewById(R.id.edt_pill);
                                TextView  pill_warning = (TextView) linear.findViewById(R.id.pill_warning);
                                sqlDB = MainActivity.bodytemp_dbHelper.getReadableDatabase();
                                Float amount = 0f;

                                try{
                                    amount = Float.valueOf(edt_pill.getText().toString());
                                }
                                catch (NumberFormatException e){
                                    Toast toast = Toast. makeText(context, "숫자로만 표기하여 주십시오.", Toast.LENGTH_LONG);
                                    toast.show();
                                }



                                if(amount != 0) {
                                    String value = ad.get(PillAdapter.getSelected());
                                    String source = af.get(PillAdapter.getSelected());


                                    long now = System.currentTimeMillis();
                                    Date date = new Date(now);
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");


                                    SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
                                    Float total = 0f;
                                    Float total2 = 0f;

                                    if (source.contains("buprofen")) {
                                        String ibu = "ibuprofen";
                                        cursor = sqlDB.rawQuery("SELECT * FROM TIMELINEDATA WHERE name = '" + username + "' AND TimelineDateTime LIKE '" + dateFormat2.format(date) + "%' AND Source LIKE '%" + ibu + "%'; ", null);
                                        while (cursor.moveToNext()) {
                                            Log.d("name ", cursor.getString(1));
                                            total += cursor.getFloat(4);
                                        }


                                        if (total + amount > imax) {
                                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                            imm.hideSoftInputFromWindow(edt_pill.getWindowToken(), 0);
                                            Toast toast = Toast.makeText(context, "이부프로펜 1일 최대량을 넘었습니다. 현재(" + total + ")", Toast.LENGTH_LONG);
                                            toast.show();

                                        } else {
                                            sqlDB.execSQL("INSERT INTO TIMELINEDATA VALUES ('" + username + "', '" + value + "', '" + dateFormat.format(date) + "', '" + source + "', '" + amount + "');");
                                            Log.d("pass ", "pass");
                                            setAlarm_30minutes_after_administration();
                                            pill_warning.setVisibility(View.INVISIBLE);
                                            dialog.dismiss();
                                            finish();
                                        }

                                    } else if (source.contains("Acetaminophen")) {
                                        String ibu = "acetaminophen";
                                        cursor = sqlDB.rawQuery("SELECT * FROM TIMELINEDATA WHERE name = '" + username + "' AND TimelineDateTime LIKE '" + dateFormat2.format(date) + "%' AND Source LIKE '%" + ibu + "%'; ", null);
                                        while (cursor.moveToNext()) {
                                            Log.d("name ", cursor.getString(1));
                                            total2 += cursor.getFloat(4);
                                        }

                                        if (total2 + amount > amax) {
                                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                            imm.hideSoftInputFromWindow(edt_pill.getWindowToken(), 0);
                                            Toast toast = Toast.makeText(context, "아세트아미노펜 1일 최대량을 넘었습니다. 현재(" + total2 + ")", Toast.LENGTH_LONG);
                                            toast.show();

                                        } else {
                                            sqlDB.execSQL("INSERT INTO TIMELINEDATA VALUES ('" + username + "', '" + value + "', '" + dateFormat.format(date) + "', '" + source + "', '" + amount + "');");
                                            Log.d("pass ", "pass");
                                            setAlarm_30minutes_after_administration();
                                            pill_warning.setVisibility(View.INVISIBLE);
                                            dialog.dismiss();
                                            finish();
                                        }
                                    }

                                }
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }).create();

                        dialog.show();

            }
        });

        bt_pill_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

    public void setRecyclerView(View view){

        rv_pill=(RecyclerView) findViewById(R.id.rv_pill_list);
        rv_pill.setVisibility(View.VISIBLE);
        pillAdapter = new PillAdapter(ad,af,context);
        rv_pill.setLayoutManager(new LinearLayoutManager(context));
        rv_pill.setAdapter(pillAdapter);



    }

    /** 투약 30분후 알람 추가 **/
    public void setAlarm_30minutes_after_administration(){
        // 투약은 30분 후 알람이기에 1800000 더함.
        int requestID = (int) System.currentTimeMillis()+1800000;

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("alarm_mode", 2); // 0: 고온, 1: 저온

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestID, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager_administratione = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager_administratione.set(AlarmManager.RTC_WAKEUP, 0, pendingIntent);
    }
}
