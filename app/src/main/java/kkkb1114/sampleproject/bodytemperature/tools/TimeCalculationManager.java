package kkkb1114.sampleproject.bodytemperature.tools;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeCalculationManager {

    public long five_MinutesMillis = 5 * 60 * 1000; // 5분에 해당하는 밀리초 계산
    public long ten_MinutesMillis = 10 * 60 * 1000; // 10분에 해당하는 밀리초 계산
    public long thirty_MinutesMillis = 30 * 60 * 1000; // 30분에 해당하는 밀리초 계산
    public long sixty_MinutesMillis = 60 * 60 * 1000; // 60분에 해당하는 밀리초 계산


    /** 현재 시간에서 +10분 구하기 **/
    public long getFormatTimeNow(long ten_MinutesMillis){
        long currentTimeMillis = System.currentTimeMillis(); // 현재 시간을 밀리초 단위로 가져옴
        long time = currentTimeMillis + ten_MinutesMillis; // 10분 후의 시간 계산

        Date mReDate = new Date(time);
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyyMMddHHmm");
        String formatDate = mFormat.format(mReDate);
        return Long.parseLong(formatDate);
    }
}
