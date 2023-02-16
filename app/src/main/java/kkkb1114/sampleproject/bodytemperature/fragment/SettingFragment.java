package kkkb1114.sampleproject.bodytemperature.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import kkkb1114.sampleproject.bodytemperature.R;
import kkkb1114.sampleproject.bodytemperature.activity.MyProfileActivity;
import kkkb1114.sampleproject.bodytemperature.connect.ConnectActivity;

public class SettingFragment extends Fragment implements View.OnClickListener {

    TextView tv_setting_connect;
    ImageView iv_setting_profile;

    public SettingFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        initView(view);

        return view;
    }

    public void initView(View view){
        tv_setting_connect = view.findViewById(R.id.tv_setting_connect);
        tv_setting_connect.setOnClickListener(this);
        iv_setting_profile = view.findViewById(R.id.iv_setting_profile);
        iv_setting_profile.setOnClickListener(this);
    }

    /** 클릭 이벤트 모음 **/
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_setting_connect:
                // 블루투스 연결 화면 이동
                Intent connectIntent = new Intent(view.getContext(), ConnectActivity.class);
                startActivity(connectIntent);
                break;
            case R.id.iv_setting_profile:
                // 내 프로필로 이동
                Intent profileIntent = new Intent(view.getContext(), MyProfileActivity.class);
                startActivity(profileIntent);
                break;
        }
    }
}