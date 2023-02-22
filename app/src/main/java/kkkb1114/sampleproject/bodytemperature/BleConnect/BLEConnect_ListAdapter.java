package kkkb1114.sampleproject.bodytemperature.BleConnect;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import kkkb1114.sampleproject.bodytemperature.R;
import kkkb1114.sampleproject.bodytemperature.tools.PermissionManager;
import kkkb1114.sampleproject.bodytemperature.tools.PreferenceManager;

public class BLEConnect_ListAdapter extends RecyclerView.Adapter<BLEConnect_ListAdapter.ViewHolder> {

    ArrayList<BluetoothDevice> scanBleDeviceList;
    Context context;
    // 권한 체크
    private PermissionManager permissionManager;

    public BLEConnect_ListAdapter(Context context, ArrayList<BluetoothDevice> scanBleDeviceList){
        this.context = context;
        this.scanBleDeviceList = scanBleDeviceList;
        permissionManager = new PermissionManager();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.item_ble_connect_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        checkBlePermission();
        holder.tv_name.setText(scanBleDeviceList.get(position).getName());
        holder.tv_address.setText(scanBleDeviceList.get(position).getAddress());
        holder.tv_connectState.setText("미연결");
        setLnItemClick(holder, scanBleDeviceList.get(position).getName(), scanBleDeviceList.get(position).getAddress());
    }

    /** 아이템 클릭 이벤트 **/
    public void setLnItemClick(ViewHolder holder, String deviceName, String deviceAddress){
        holder.ln_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreferenceManager.PREFERENCES_NAME = "login_user";
                PreferenceManager.setString(context, "deviceName", deviceName);
                PreferenceManager.setString(context, "deviceAddress", deviceAddress);
            }
        });
    }
    
    /** 블루투스 주변 기기 검색 권한 체크 **/
    public void checkBlePermission(){
        // 권한 체크 (만약 권한이 허용되어있지 않으면 권한 요청)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (!(permissionManager.permissionCheck(context, Manifest.permission.ACCESS_FINE_LOCATION) ||
                    permissionManager.permissionCheck(context, Manifest.permission.ACCESS_COARSE_LOCATION) ||
                    permissionManager.permissionCheck(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION))){

                permissionManager.requestPermission(context, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                });
            }else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
                    if (!(permissionManager.permissionCheck(context, Manifest.permission.BLUETOOTH_CONNECT) ||
                            permissionManager.permissionCheck(context, Manifest.permission.BLUETOOTH_SCAN) ||
                            permissionManager.permissionCheck(context, Manifest.permission.BLUETOOTH))){

                        permissionManager.requestPermission(context, new String[]{
                                Manifest.permission.BLUETOOTH_CONNECT,
                                Manifest.permission.BLUETOOTH_SCAN,
                                Manifest.permission.BLUETOOTH
                        });
                    }
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return scanBleDeviceList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout ln_item;
        TextView tv_name;
        TextView tv_address;
        TextView tv_connectState;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            initView(itemView);

        }

        public void initView(View itemView){
            ln_item = itemView.findViewById(R.id.ln_item);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_address = itemView.findViewById(R.id.tv_address);
            tv_connectState = itemView.findViewById(R.id.tv_connectState);
        }
    }
}
