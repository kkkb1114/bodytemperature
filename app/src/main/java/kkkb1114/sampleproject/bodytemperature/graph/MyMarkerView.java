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

    import kkkb1114.sampleproject.bodytemperature.R;

    public class MyMarkerView extends MarkerView {

        private TextView tvContent;
        private AlertDialog alertDialog;
        private Entry lastEntry; // 마지막으로 선택된 마커
        int Dday;

        AlertDialog.Builder dlgBuilder;
        public MyMarkerView(Context context, int layoutResource, int Dday) {
            super(context, layoutResource);

            this.Dday = Dday;

            tvContent = (TextView)findViewById(R.id.tvContent);

        }

        // callbacks everytime the MarkerView is redrawn, can be used to update the
        // content (user-interface)
        @Override
        public void refreshContent(Entry e, Highlight highlight) {
            if (e != null) {
                if (alertDialog != null) {
                    alertDialog.dismiss(); // 기존 다이얼로그가 열려있는 경우 닫음
                }

                if (lastEntry == null || lastEntry != e) { // 마지막으로 선택된 마커와 현재 마커가 다른 경우에만 다이얼로그를 생성함


                    if (highlight != null) {

                        if(e.getX()<Dday)
                        { final LinearLayout linear = (LinearLayout) View.inflate(getContext(), R.layout.markerview, null);
                            AlertDialog.Builder dlgBuilder = new AlertDialog.Builder(getContext()).setView(linear);
                            dlgBuilder.setTitle("배란일 전 증상"); //제목
                            dlgBuilder.setMessage("생리 전 증후군 (PMS)이 발생할 수 있습니다. 이는 생리가 시작되기 전 몇 일에서 2주 정도 동안 나타나는 감정적, 신체적 증상을 의미합니다.\u200B\n" +
                                    "\n" +
                                    "가슴이 민감해지거나 부어오르는 것 같은 증상이 나타날 수 있습니다.\u200B\n" +
                                    "\n" +
                                    "복부가 팽창하거나 불편해질 수 있습니다.\u200B\n" +
                                    "\n" +
                                    "두통이나 어지러움이 나타날 수 있습니다.\u200B"); // 메시지
                            dlgBuilder.setPositiveButton("확인",new DialogInterface.OnClickListener(){
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            alertDialog = dlgBuilder.create();

                        }
                        else if(e.getX()==Dday)
                        {
                            final LinearLayout linear = (LinearLayout) View.inflate(getContext(), R.layout.markerview, null);
                            AlertDialog.Builder dlgBuilder = new AlertDialog.Builder(getContext()).setView(linear);
                            dlgBuilder.setTitle("배란일 증상"); //제목
                            dlgBuilder.setMessage("배란 증상은 불규칙할 수 있으며, 일반적으로 약 24~48시간 동안 나타납니다.\u200B\n" +
                                    "\n" +
                                    "배란을 일으키는 호르몬인 에스트로겐이 최고치에 도달하면서 체온이 조금 오르고, 배란을 일으키는 호르몬인 LH (황체형성호르몬)의 분비량이 증가합니다. 이런 증상을 이용해 배란 일정을 파악할 수도 있습니다.\u200B"); // 메시지
                            dlgBuilder.setPositiveButton("확인",new DialogInterface.OnClickListener(){
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            alertDialog = dlgBuilder.create();

                        }
                        else if(e.getX()<=Dday+7)
                        {
                            final LinearLayout linear = (LinearLayout) View.inflate(getContext(), R.layout.markerview, null);
                            AlertDialog.Builder dlgBuilder = new AlertDialog.Builder(getContext()).setView(linear);
                            dlgBuilder.setTitle("수정 증상"); //제목
                            dlgBuilder.setMessage("수정 증상은 배란 후 수일 동안 나타나며, 이 기간은 여성마다 다를 수 있습니다.\u200B\n" +
                                    "\n" +
                                    "수정 증상 중 하나는 배란이 일어난 날부터 1주일 이내에 가슴이 민감하고 부어오르는 것입니다.\u200B\n" +
                                    "\n" +
                                    "배란 후 7~10일 정도 경과하면 자궁 내막이 두터워지기 시작합니다. 이로 인해 가벼운 출혈이나 살짝의 착란이 발생할 수 있습니다.\u200B\n" +
                                    "\n" +
                                    "소화불량, 체중 변화, 피로, 기분 변화 등의 증상도 나타날 수 있습니다.\u200B"); // 메시지
                            dlgBuilder.setPositiveButton("확인",new DialogInterface.OnClickListener(){
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            alertDialog = dlgBuilder.create();
                        }
                        else {
                            final LinearLayout linear = (LinearLayout) View.inflate(getContext(), R.layout.markerview, null);
                            AlertDialog.Builder dlgBuilder = new AlertDialog.Builder(getContext()).setView(linear);
                            dlgBuilder.setTitle("착상 증상"); //제목
                            dlgBuilder.setMessage("착상 증상은 배란 후 1주일 이내에 나타날 수 있습니다.\u200B\n" +
                                    "\n" +
                                    "착상이 성공적으로 일어난 경우, 자궁에서 발생하는 호르몬 변화로 인해 체온이 오르고, 자궁 내막이 더 두터워집니다.\u200B\n" +
                                    "\n" +
                                    "이후에는 임신 초기 증상들이 나타나기 시작합니다. 이 증상에는 구토, 두통, 가슴이 민감해지는 등의 증상이 포함될 수 있습니다.\u200B"); // 메시지
                            dlgBuilder.setPositiveButton("확인",new DialogInterface.OnClickListener(){
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
