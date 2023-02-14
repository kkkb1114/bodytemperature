package kkkb1114.sampleproject.bodytemperature.connect;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import kkkb1114.sampleproject.bodytemperature.R;

public class BLEConnect_ListAdapter extends RecyclerView.Adapter<BLEConnect_ListAdapter.ViewHolder> {

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.ble_connect_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 3;
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
