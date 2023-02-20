package kkkb1114.sampleproject.bodytemperature.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import kkkb1114.sampleproject.bodytemperature.R;
import kkkb1114.sampleproject.bodytemperature.tools.PreferenceManager;
import kkkb1114.sampleproject.bodytemperature.userList.UserListAdapter;

public class UserSettingActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbar_user_setting;
    Context context;
    ArrayList<String> userList;
    TextView tv_close;

    RecyclerView rv_user_list;
    UserListAdapter userListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setting);
        context = this;
        initView();
        setToolbar();
    }

    @Override
    public void onResume() {
        super.onResume();
        // 설정화면에서 사용자 변경후 다시 바뀌어야하기에 onResume()에 추가
        getUserData();
        setRecyclerView();
    }

    public void initView(){
        toolbar_user_setting = findViewById(R.id.toolbar_user_setting);
        rv_user_list = findViewById(R.id.rv_user_list);
        tv_close = findViewById(R.id.tv_close);
        tv_close.setOnClickListener(this);
    }

    /** 쉐어드에서 유저 데이터 가져와 ArrayList로 변환 **/
    public void getUserData(){
        PreferenceManager.PREFERENCES_NAME = "user_list";
        Map<String, ?> dataMap = PreferenceManager.getAllDataList(context);
        // 현재 선택한 유저 데이터는 뺴기
        dataMap.remove("select_user_name");
        // Map -> ArrayList 변환
        Collection<String> values = (Collection<String>) dataMap.values();
        userList = new ArrayList<>(values);
    }

    /** 리사이클러뷰 세팅 **/
    public void setRecyclerView(){
        userListAdapter = new UserListAdapter(context, userList);
        rv_user_list.setLayoutManager(new LinearLayoutManager(context));
        rv_user_list.setAdapter(userListAdapter);
        userListAdapter.notifyDataSetChanged();
    }

    /** 툴바 세팅 **/
    public void setToolbar(){
        setSupportActionBar(toolbar_user_setting);
        getSupportActionBar().setTitle("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.user_setting_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        switch (menuItem.getItemId()){
            case R.id.user_add:
                // 유저 추가 화면 이동
                Intent addUserIntent = new Intent(context, MyProfileActivity.class);
                startActivity(addUserIntent);
                break;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_close:
                finish();
                break;
        }
    }
}