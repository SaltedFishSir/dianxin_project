package com.iflytek;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Producer {


    /**
     * 数据格式：call1,call1_name,call2,call_name,date_time,date_time_ts,duration,flag
     */

    private ArrayList<String> phoneList = new ArrayList<String>();

    private Map<String, String> map = new HashMap<>();

    public void init() {
        phoneList.add("15369468720");
        phoneList.add("19920860202");
        phoneList.add("18411925860");
        phoneList.add("14473548449");
        phoneList.add("18749966182");
        phoneList.add("19379884788");
        phoneList.add("19335715448");
        phoneList.add("18503558939");
        phoneList.add("13407209608");
        phoneList.add("15596505995");
        phoneList.add("17519874292");
        phoneList.add("15178485516");
        phoneList.add("19877232369");
        phoneList.add("18706287692");
        phoneList.add("18944239644");
        phoneList.add("17325302007");
        phoneList.add("18839074540");
        phoneList.add("19879419704");
        phoneList.add("16480981069");
        phoneList.add("18674257265");
        phoneList.add("18302820904");
        phoneList.add("15133295266");
        phoneList.add("17868457605");
        phoneList.add("15490732767");
        phoneList.add("15064972307");


        map.put("15369468720", "李雁");
        map.put("19920860202", "卫艺");
        map.put("18411925860", "仰莉");
        map.put("14473548449", "陶欣悦");
        map.put("18749966182", "施梅梅");
        map.put("19379884788", "金虹霖");
        map.put("19335715448", "魏明艳");
        map.put("18503558939", "华贞");
        map.put("13407209608", "华啟倩");
        map.put("15596505995", "仲采绿");
        map.put("17519874292", "卫丹");
        map.put("15178485516", "戚丽红");
        map.put("19877232369", "何翠柔");
        map.put("18706287692", "钱溶艳");
        map.put("18944239644", "钱琳");
        map.put("17325302007", "缪静欣");
        map.put("18839074540", "焦秋菊");
        map.put("19879419704", "吕访琴");
        map.put("16480981069", "沈丹");
        map.put("18674257265", "褚美丽");
        map.put("18302820904", "孙怡");
        map.put("15133295266", "许婵");
        map.put("17868457605", "曹红恋");
        map.put("15490732767", "吕柔");
        map.put("15064972307", "冯怜云");
    }


    /**
     * 产生日志
     *
     * @return
     */
    public String generateLog() throws ParseException {

        init();

        int call1Index = 0;
        int call2Index = 0;

        while (true) {
            call1Index = (int) (Math.random() * phoneList.size());
            call2Index = (int) (Math.random() * phoneList.size());
            if (call1Index != call2Index) {
                break;
            }
        }

        String call1 = phoneList.get(call1Index);
        String call2 = phoneList.get(call2Index);

        //获取随机date_time
        String dateTime = getDateTime("2018-01-01", "2019-01-01");

        //获取随机时长
        int duration = (int) (Math.random() * 60 * 20) + 1;
        DecimalFormat df = new DecimalFormat("0000");
        String dura = df.format(duration);

        StringBuilder sb = new StringBuilder();
        sb.append(call1)
                .append(",")
                .append(call2)
                .append(",")
                .append(dateTime)
                .append(",")
                .append(dura);


        return sb.toString();
    }


    /**
     * 获取随机date_time
     */
    private String getDateTime(String start, String end) throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long s = sdf.parse(start).getTime();
        long e = sdf.parse(end).getTime();

        long result = (long) (Math.random() * (e - s)) + s;

        String dateTime = sdf2.format(result);

        return dateTime;
    }


    /**
     * 循环写日志
     * @param path
     * @throws IOException
     * @throws ParseException
     * @throws InterruptedException
     */
    public void writeLog(String path) throws IOException, ParseException, InterruptedException {

        FileOutputStream fos = null;
        OutputStreamWriter writer = null;
        try {
            fos = new FileOutputStream(path);
            writer = new OutputStreamWriter(fos, "UTF-8");
            while (true) {

                String log = generateLog();
                System.out.println(log);

                writer.write(log+"\n");
                writer.flush();

                Thread.sleep(250);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            writer.close();
            fos.close();
        }


    }


    public static void main(String[] args) throws ParseException, InterruptedException, IOException {
//        System.out.println(getDateTime("2018-01-01", "2019-01-01"));
        Producer producer = new Producer();
        producer.writeLog(args[0]);

    }


}
