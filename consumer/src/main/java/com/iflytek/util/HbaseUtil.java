package com.iflytek.util;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HbaseUtil {

    public static Connection connection;
    public static Admin admin;
    private static Configuration configuration = HBaseConfiguration.create();


    /**
     * 关闭资源
     */
    public static void closeResource() {
        if (connection != null) {
            try {
                connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (admin != null) {
            try {
                admin.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 判断表是否存在
     *
     * @param tableName
     * @return
     * @throws IOException
     */
    public static boolean isTableExists(String tableName) {

        boolean result = false;
        try {
            connection = ConnectionFactory.createConnection(configuration);
            admin = connection.getAdmin();
            result = admin.tableExists(TableName.valueOf(tableName));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeResource();
        }

        return result;
    }


    /**
     * 创建namespace
     *
     * @param namespace
     */
    public static void createNamespace(String namespace) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");

        NamespaceDescriptor descriptor = NamespaceDescriptor.create(namespace)
                .addConfiguration("creator", "saltedfish")
                .addConfiguration("createtime", sdf.format(new Date()))
                .build();

        try {
            connection = ConnectionFactory.createConnection(configuration);
            admin = connection.getAdmin();
            admin.createNamespace(descriptor);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeResource();
        }


    }


    /**
     * 创建table
     *
     * @param tableName
     * @param columnFamily
     */
    public static void createTable(String tableName, int regions,String... columnFamily) {

        if (isTableExists(tableName)) {
            System.out.println(tableName + "表已存在！");
            return;
        }

        HTableDescriptor hTableDescriptor = new HTableDescriptor(TableName.valueOf(tableName));

        for (String cf : columnFamily) {
            hTableDescriptor.addFamily(new HColumnDescriptor(cf));
        }

//        try {
//            hTableDescriptor.addCoprocessor("com.iflytek.coprocessor.CalleeWriteObServer");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        try {
            /**
             * 创建表预分区
             */
            connection = ConnectionFactory.createConnection(configuration);
            admin = connection.getAdmin();
            admin.createTable(hTableDescriptor, getSplitKeys(regions));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeResource();
        }

    }


    /**
     * 预分区，获取分区键
     *
     * @param regions 在公司中，一般一个regionserver维护2到3个region（针对同一个表），我是3个regionserver，所以6个regions
     *                <p>
     *                "|"的ask码值较大，用来离散数据，分区之后添加数据，会将rowkey与分区键进行比较，按位比较
     * @return
     */
    public static byte[][] getSplitKeys(int regions) {

        byte[][] splitkeys = new byte[regions][];
        DecimalFormat df = new DecimalFormat("00");

        for (int i = 0; i < regions; i++) {
            splitkeys[i] = Bytes.toBytes(df.format(i) + "|");
        }
        return splitkeys;
    }


    /**
     * 生成rowKey
     *
     * @param regionHash
     * @param call1
     * @param dateTime
     * @param duration
     * @return
     */
    public static String genRowKey(String regionHash, String call1, String call2, String dateTime, String flag, String duration) {

        // 05_19920860202_19379884788_2018-11-23 06:02:50_1_0432
        return regionHash + "_" + call1 + "_" + dateTime + "_" + call2 + "_" + flag + "_" + duration;
    }


    /**
     * 生成分区号
     *
     * @param call1
     * @param dateTime
     * @param regions
     * @return
     */
    public static String genPartitionCode(String call1, String dateTime, int regions) {

        //去电话号码后四位
        int callLast4 = Integer.valueOf(call1.substring(call1.length() - 4));
        //取时间的年月，不要日，按年太集中，按日太分散
        int yearMonth = Integer.valueOf(dateTime.replace("-", "").substring(0, 6));

        int result = (callLast4 ^ yearMonth) % regions;
        DecimalFormat df = new DecimalFormat("00");

        return df.format(result);
    }


    public static void main(String[] args) throws Exception {

  //      createTable("ct:calllog", "f1", 6);

    }

}
