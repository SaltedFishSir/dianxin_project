package com.iflytek.coprocessor;

import com.iflytek.util.ConnectionUtil;
import com.iflytek.util.HbaseUtil;
import com.iflytek.util.PropertiesUtil;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Durability;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.coprocessor.BaseRegionObserver;
import org.apache.hadoop.hbase.coprocessor.ObserverContext;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessorEnvironment;
import org.apache.hadoop.hbase.regionserver.wal.WALEdit;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class CalleeWriteObServer extends BaseRegionObserver {

    private int regions = Integer.valueOf(PropertiesUtil.getProperty("hbase.regions"));

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void postPut(ObserverContext<RegionCoprocessorEnvironment> e, Put put, WALEdit edit, Durability durability) throws IOException {

        //1.将要操作的表
        String tableName = PropertiesUtil.getProperty("hbase.table.name");

        //2.获取当前操作的表
        String curTableName = e.getEnvironment().getRegionInfo().getTable().getNameAsString();

        //3.判断需要操作的表是否就是当前表
        if (!tableName.equals(curTableName)) {
            return;
        }

        //4.封装数据
        String row = Bytes.toString(put.getRow());

        //regionHash + "_" + call1 + "_" + dateTime + "_" + call2 + "_" + flag + "_" + duration
        String[] split = row.split("_");
        String call1 = split[1];
        String call2 = split[3];
        String dateTime = split[2];
        String dateTime_ts = null;
        try {
            System.out.println("==============================");
            System.out.println(dateTime);
            dateTime_ts = sdf.parse(dateTime).getTime() + "";


        } catch (ParseException e1) {
            e1.printStackTrace();
        }

        String flag = split[4];
        String duration = split[5];

        if ("0".equals(flag)) {
            return;
        }

        //获取分区号
        String regionHash = HbaseUtil.genPartitionCode(call2, dateTime, regions);
        //获取rowkey
        String rowKey = HbaseUtil.genRowKey(regionHash, call2, dateTime, call1, "0", duration);

        Put newPut = new Put(Bytes.toBytes(rowKey));

        newPut.addColumn(Bytes.toBytes("f2"), Bytes.toBytes("call1"), Bytes.toBytes(call2));
        newPut.addColumn(Bytes.toBytes("f2"), Bytes.toBytes("buildtime"), Bytes.toBytes(dateTime));
        newPut.addColumn(Bytes.toBytes("f2"), Bytes.toBytes("buildtime_ts"), Bytes.toBytes(dateTime_ts));
        newPut.addColumn(Bytes.toBytes("f2"), Bytes.toBytes("call2"), Bytes.toBytes(call1));
        newPut.addColumn(Bytes.toBytes("f2"), Bytes.toBytes("flag"), Bytes.toBytes("0"));
        newPut.addColumn(Bytes.toBytes("f2"), Bytes.toBytes("duration"), Bytes.toBytes(duration));

        //获取连接
        Connection connection = ConnectionUtil.getConnectionInstance();
        Table table = connection.getTable(TableName.valueOf(tableName));

        table.put(newPut);
        table.close();


    }
}
