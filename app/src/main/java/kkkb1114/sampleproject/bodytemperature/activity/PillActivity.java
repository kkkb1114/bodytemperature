package kkkb1114.sampleproject.bodytemperature.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import kkkb1114.sampleproject.bodytemperature.API.OpenApi;
import kkkb1114.sampleproject.bodytemperature.R;
import kkkb1114.sampleproject.bodytemperature.pill.PillAdapter;

public class PillActivity extends AppCompatActivity{

    Context context;
    Button bt_pill_cancel, bt_pill_confirm, bt_pillSearch;
    EditText edt_pillSearch;

    View view;
    RecyclerView rv_pill;

    PillAdapter pillAdapter;

    String url;
    String searchText;
    String data, text;
    int pageNum=1;

    ArrayList<String> ad = new ArrayList<>();
    ArrayList<String> af = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pill);
        context = this;

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
                       OpenApi pill = new OpenApi(targetName,ad,af,pillAdapter,context,rv_pill);
                       pill.execute();


                }
                else{

                }

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
        pillAdapter = new PillAdapter(ad,af);
        rv_pill.setLayoutManager(new LinearLayoutManager(context));
        rv_pill.setAdapter(pillAdapter);



    }

}
