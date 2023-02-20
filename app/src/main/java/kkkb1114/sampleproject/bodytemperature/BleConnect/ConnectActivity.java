package kkkb1114.sampleproject.bodytemperature.BleConnect;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import kkkb1114.sampleproject.bodytemperature.R;

public class ConnectActivity extends AppCompatActivity {

    RecyclerView rv_bluetooth_list;
    BLEConnect_ListAdapter bleConnect_listAdapter;
    Button bt_search;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        context = this;
        initView();
        setRecyclerView();
    }

    public void initView(){
        rv_bluetooth_list = findViewById(R.id.rv_bluetooth_list);
        bt_search = findViewById(R.id.bt_search);
    }

    /** RecyclerView 세팅 **/
    public void setRecyclerView(){
        bleConnect_listAdapter = new BLEConnect_ListAdapter();
        rv_bluetooth_list.setLayoutManager(new LinearLayoutManager(context));
        rv_bluetooth_list.setAdapter(new BLEConnect_ListAdapter());
        bleConnect_listAdapter.notifyDataSetChanged();
    }

    /** 주변 기기 검색 버튼 클릭 이벤트 **/
    public void searchButtonClickEvent(){

    }
}