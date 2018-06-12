package com.iflytek;

import com.iflytek.util.ConnectionUtil;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;

public class ScanTest {



    @Test
    public void scanData() throws ParseException, IOException {

        ScanUtil scanUtil = new ScanUtil();

        scanUtil.init("19335715448", "2018-08", "2018-11");

        Connection connection = ConnectionUtil.getConnectionInstance();
        Table table = connection.getTable(TableName.valueOf("ct:calllog"));

        while (scanUtil.hasNext()) {
            String[] rowkeys = scanUtil.next();
            Scan scan = new Scan();
            scan.setStartRow(Bytes.toBytes(rowkeys[0]));
            scan.setStopRow(Bytes.toBytes(rowkeys[1]));

            //regionHash + "_" + phone + "_" + startTime;
            System.out.println("时间范围："
                    + rowkeys[0].split("_")[2]
                    + "----"
                    + rowkeys[1].split("_")[2]);
            ResultScanner scanner = table.getScanner(scan);

            for (Result result : scanner) {
                System.out.println(Bytes.toString(result.getRow()));
            }
        }


    }


}
