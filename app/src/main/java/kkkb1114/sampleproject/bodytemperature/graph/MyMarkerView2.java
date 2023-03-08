
package kkkb1114.sampleproject.bodytemperature.graph;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import kkkb1114.sampleproject.bodytemperature.R;

public class MyMarkerView2 extends MarkerView {

    private TextView tvContent;
    private AlertDialog alertDialog;
    private Entry lastEntry; // 마지막으로 선택된 마커
    String Stime;
    ArrayList compare_X;
    String InflayType;

    AlertDialog.Builder dlgBuilder;
    public MyMarkerView2(Context context, int layoutResource, String Stime, ArrayList compare_X, String InflaType) {
        super(context, layoutResource);

        this.Stime = Stime;
        this.compare_X = compare_X;
        this.InflayType=InflaType;


        tvContent = (TextView)findViewById(R.id.tvContent);

    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        if (e != null) {

            Date format1=null;
            try {
                format1 = new SimpleDateFormat("yyyy-MM-dd hh:mm").parse((String) compare_X.get((int) e.getX()));
            } catch (ParseException ex) {
                throw new RuntimeException(ex);
            }
            Date format2 = null;
            try {
                format2 = new SimpleDateFormat("yyyy-MM-dd hh:mm").parse(Stime);
            } catch (ParseException ex) {
                throw new RuntimeException(ex);
            }
            long diffHor = (format1.getTime() - format2.getTime()) / 3600000;

            if (alertDialog != null) {
                alertDialog.dismiss(); // 기존 다이얼로그가 열려있는 경우 닫음
            }

            if (lastEntry == null || lastEntry != e) { // 마지막으로 선택된 마커와 현재 마커가 다른 경우에만 다이얼로그를 생성함


                if (highlight != null) {

                    if (diffHor>=0 && diffHor<=3) {
                        final LinearLayout linear = (LinearLayout) View.inflate(getContext(), R.layout.markerview, null);
                        AlertDialog.Builder dlgBuilder = new AlertDialog.Builder(getContext()).setView(linear);
                        dlgBuilder.setTitle("수술 후 "+ diffHor + "시간"); //제목

                        if(InflayType.equals("수술부위 감염"))
                            dlgBuilder.setMessage("수술 직후 체온이 일시적으로 상승할 수 있으므로, 특별한 대응은 필요하지 않습니다."); // 메시지
                        else if(InflayType.equals("혈전"))
                            dlgBuilder.setMessage("수술 직후 체온이 일시적으로 상승할 수 있으므로, 특별한 대응은 필요하지 않습니다."); // 메시지
                        else
                            dlgBuilder.setMessage("수술 직후 체온이 일시적으로 상승할 수 있으므로, 특별한 대응은 필요하지 않습니다."); // 메시지

                        dlgBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        alertDialog = dlgBuilder.create();

                    } else if (diffHor>=4 && diffHor<=7) {
                        final LinearLayout linear = (LinearLayout) View.inflate(getContext(), R.layout.markerview, null);
                        AlertDialog.Builder dlgBuilder = new AlertDialog.Builder(getContext()).setView(linear);
                        dlgBuilder.setTitle("수술 후 "+ diffHor + "시간"); //제목
                        if(InflayType.equals("수술부위 감염"))
                            dlgBuilder.setMessage("체온이 38도 이상이라면, 항열제를 복용합니다. 의사와 상의하여 적절한 항생제를 복용하며, 감염의 가능성을 판단하여 적절한 검사를 시행합니다."); // 메시지
                        else if(InflayType.equals("혈전"))
                            dlgBuilder.setMessage("체온이 38도 이상이라면, 항열제를 복용합니다. 항응고제를 복용하며, 특히 다리나 팔에 통증, 부종, 빨간색 등이 나타나는 경우 의사와 상의하여 추가적인 검사를 시행합니다."); // 메시지
                        else
                            dlgBuilder.setMessage("체온이 38도 이상이라면, 항열제를 복용합니다. 의사와 상의하여 적절한 항생제를 복용하며, 농양의 위치와 크기에 따라 추가적인 검사를 시행합니다."); // 메시지

                        dlgBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        alertDialog = dlgBuilder.create();

                    } else {
                        final LinearLayout linear = (LinearLayout) View.inflate(getContext(), R.layout.markerview, null);
                        AlertDialog.Builder dlgBuilder = new AlertDialog.Builder(getContext()).setView(linear);
                        dlgBuilder.setTitle("수술 후 "+ diffHor + "시간"); //제목
                        if(InflayType.equals("수술부위 감염"))
                            dlgBuilder.setMessage("체온이 계속해서 상승한다면, 의사와 상의하여 추가적인 검사를 시행하고 항생제 치료를 시행합니다."); // 메시지
                        else if(InflayType.equals("혈전"))
                            dlgBuilder.setMessage("체온이 계속해서 상승하거나, 혈전의 증상이 나타나는 경우 의사와 상의하여 즉시 병원으로 이동하여 치료를 받아야 합니다."); // 메시지
                        else
                            dlgBuilder.setMessage("체온이 계속해서 상승하거나, 농양의 증상이 나타나는 경우 의사와 상의하여 적절한 조치를 취해야 합니다. 대개 농양은 치료를 위해 외과적 수술이 필요할 수 있습니다."); // 메시지

                        dlgBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        alertDialog = dlgBuilder.create();
                    }

                    alertDialog.show(); // 마커를 클릭한 경우 다이얼로그를 보여줌
                }

                lastEntry = e; // 마지막으로 선택된 마커 갱신
            }
        }

        super.refreshContent(e, highlight);
    }

    public MPPointF getOffset() {
        return new MPPointF(-getWidth() / 2, -getHeight() - 10); // 좌표를 (-width/2, -height-10)으로 지정
    }
}
