package kkkb1114.sampleproject.bodytemperature.pill;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import kkkb1114.sampleproject.bodytemperature.R;

public class PillAdapter extends RecyclerView.Adapter<PillAdapter.ViewHolder> {
    ArrayList<String> ad;
    ArrayList<String> af;

    public PillAdapter(ArrayList<String> asd,ArrayList<String> af){
        this.ad = asd;
        this.af=af;
    }

    @NonNull
    @Override
    public PillAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.item_pill_search, parent, false);
        return new PillAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d("ad", String.valueOf(ad));
        holder.pill_name.setText(ad.get(position));
        holder.pill_source.setText(af.get(position));
    }

    @Override
    public int getItemCount() {

        return ad.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout pill_layout;
        TextView pill_name;
        TextView pill_source;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            initView(itemView);

        }

        public void initView(View itemView){
            pill_layout = itemView.findViewById(R.id.pill_layout);
            pill_name = itemView.findViewById(R.id.pill_name);
            pill_source = itemView.findViewById(R.id.pill_source);
        }
    }
}
