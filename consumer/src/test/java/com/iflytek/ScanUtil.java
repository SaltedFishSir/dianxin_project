package com.iflytek;

import com.iflytek.util.HbaseUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ScanUtil {

    private List<String[]> list;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
    private int i = 0;


    public void init(String phone, String start, String stop) throws ParseException {

        list = new ArrayList<>();
        Date startDate = sdf.parse(start);
        Date stopDate = sdf.parse(stop);


        //当前时间点
        Calendar startPoint = Calendar.getInstance();
        startPoint.setTime(startDate);

        //当前结束时间点
        Calendar stopPoint = Calendar.getInstance();
        stopPoint.setTime(stopDate);
        stopPoint.add(Calendar.MONTH, 1);

        while (startPoint.getTimeInMillis() <= stopDate.getTime()) {

            String startTime = sdf.format(startPoint.getTime());
            String stopTime = sdf.format(stopPoint.getTime());

            String partitionCode = HbaseUtil.genPartitionCode(phone, startTime, 6);

            String startRow = partitionCode + "_" + phone + "_" + startTime;
            String stopRow = partitionCode + "_" + phone + "_" + stopTime;

            String[] rowKeys = {startRow, stopRow};

            list.add(rowKeys);

            startPoint.add(Calendar.MONTH, 1);
            stopPoint.add(Calendar.MONTH, 1);
        }
    }


    public boolean hasNext() {
        return i < list.size();
    }


    public String[] next() {
        return list.get(i++);
    }


}
