package kkkb1114.sampleproject.bodytemperature.timeline;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.datepicker.MaterialDatePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import kkkb1114.sampleproject.bodytemperature.R;
import kkkb1114.sampleproject.bodytemperature.connect.BLEConnect_ListAdapter;

public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.ViewHolder> {

    ArrayList<String> ad;

    public TimelineAdapter(ArrayList<String> asd){
        this.ad = asd;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.timeline_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tv_timeline.setText(ad.get(position));
        if(position+1==ad.size()){
            holder.timeline_bar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {

        return ad.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout timeline_layout;
        TextView tv_timeline;
        ImageView timeline_circle;
        ImageView timeline_bar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            initView(itemView);

        }

        public void initView(View itemView){
            timeline_layout = itemView.findViewById(R.id.ln_item);
            timeline_circle = itemView.findViewById(R.id.timeline_circle);
            timeline_bar = itemView.findViewById(R.id.timeline_bar);
            tv_timeline = itemView.findViewById(R.id.tv_timeline);
        }
    }


}
