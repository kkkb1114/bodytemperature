package kkkb1114.sampleproject.bodytemperature.userList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import kkkb1114.sampleproject.bodytemperature.R;
import kkkb1114.sampleproject.bodytemperature.tools.PreferenceManager;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {

    ArrayList<String> userList;

    public UserListAdapter(ArrayList<String> userList){
        this.userList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.item_user_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // user 데이터는 각 선택 유무가 '/'를 기준으로 나뉘어 있어 '/'를 기준으로 한번 나눠서 setText() 한다.
        String name = userList.get(position).split("/")[0];
        holder.tv_user_name.setText(name);
        holder.tv_user_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 다른 화면에서 현재 선택된 사용자 구분이 되어야 하기에 현재 사용자 구분 쉐어드 파일 생성
                PreferenceManager.PREFERENCES_NAME = "user_list";
                PreferenceManager.setString(holder.itemView.getContext(), "select_user_name", name);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout ln_user;
        TextView tv_user_name;
        TextView tv_user_modify;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            initView();
        }

        public void initView(){
            ln_user = itemView.findViewById(R.id.ln_user);
            tv_user_name = itemView.findViewById(R.id.tv_user_name);
            tv_user_modify = itemView.findViewById(R.id.tv_user_modify);
        }
    }
}
