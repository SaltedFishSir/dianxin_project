package com.iflytek.util;

import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class HbaseDao {


    private String tableName;
    private String columnFamilly;
    private int regions;
    private String namespace;
    private List<Put> list;
    private String flag = "1";
    private SimpleDateFormat sdf = null;

    public HbaseDao() {

        list = new ArrayList<>();
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        //获取属性文件中的参数值
        tableName = PropertiesUtil.getProperty("hbase.table.name");
        columnFamilly = PropertiesUtil.getProperty("hbase.table.cf");
        regions = Integer.parseInt(PropertiesUtil.getProperty("hbase.regions"));
        namespace = PropertiesUtil.getProperty("hbase.namespace");
        if (!HbaseUtil.isTableExists(tableName)) {
            //创建namespace
            HbaseUtil.createNamespace(namespace);

            //创建表
            HbaseUtil.createTable(tableName, regions, columnFamilly,"f2");
        }
    }


    public void put(String log) throws ParseException {

        try {
            //切分数据
            String[] split = log.split(",");
            String call1 = split[0];
            String call2 = split[1];
            String dateTime = split[2];
            String duration = split[3];

            long time = sdf.parse(dateTime).getTime();
            String dateTime_ts = time + "";


            //获取分区号和rowKey
            String partitionCode = HbaseUtil.genPartitionCode(call1, dateTime, regions);
            String rowKey = HbaseUtil.genRowKey(partitionCode, call1, call2, dateTime, flag, duration);


            HTable table = (HTable) ConnectionUtil.getConnectionInstance().getTable(TableName.valueOf(tableName));
            table.setAutoFlushTo(false);
            //设置缓存大小
            table.setWriteBufferSize(1024 * 1024);
            Put put = new Put(Bytes.toBytes(rowKey));


            put.addColumn(Bytes.toBytes(columnFamilly), Bytes.toBytes("call1"), Bytes.toBytes(call1));
            put.addColumn(Bytes.toBytes(columnFamilly), Bytes.toBytes("call2"), Bytes.toBytes(call2));
            put.addColumn(Bytes.toBytes(columnFamilly), Bytes.toBytes("dateTime"), Bytes.toBytes(dateTime));
            put.addColumn(Bytes.toBytes(columnFamilly), Bytes.toBytes("dateTime_ts"), Bytes.toBytes(dateTime_ts));
            put.addColumn(Bytes.toBytes(columnFamilly), Bytes.toBytes("flag"), Bytes.toBytes(flag));
            put.addColumn(Bytes.toBytes(columnFamilly), Bytes.toBytes("duration"), Bytes.toBytes(duration));

            list.add(put);


            if (list.size() > 20) {
                table.put(list);
                table.flushCommits();
                list.clear();
                table.close();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}
