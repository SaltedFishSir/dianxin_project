package com.iflytek.mr;

import com.iflytek.convertor.DimensionConvertor;
import com.iflytek.kv.base.BaseDimension;
import com.iflytek.kv.key.CommDimension;
import com.iflytek.kv.value.CountDurationValue;
import com.iflytek.util.JDBCInstance;
import com.iflytek.util.JDBCUtil;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.output.FileOutputCommitter;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MySQLOutPutFormat extends OutputFormat<BaseDimension, CountDurationValue> {


    @Override
    public RecordWriter<BaseDimension, CountDurationValue> getRecordWriter(TaskAttemptContext context) throws IOException, InterruptedException {

        Connection connection = null;
        try {
            connection = JDBCInstance.getInstance();
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new MyRecordWriter(connection);
    }

    @Override
    public void checkOutputSpecs(JobContext context) throws IOException, InterruptedException {

    }

    @Override
    public OutputCommitter getOutputCommitter(TaskAttemptContext context) throws IOException, InterruptedException {
        return new FileOutputCommitter(FileOutputFormat.getOutputPath(context), context);
    }


    public static class MyRecordWriter extends RecordWriter<BaseDimension, CountDurationValue> {

        private Connection connection = null;
        private PreparedStatement preparedStatement = null;
        private int batchBound = 500;//缓存sql条数边界
        private int batchSize = 0;//客户端已经缓存的条数

        public MyRecordWriter(Connection connection) {
            this.connection = connection;
        }


        @Override
        public void write(BaseDimension key, CountDurationValue value) throws IOException, InterruptedException {

            CommDimension commDimension = (CommDimension) key;
            //插入数据到mysql
            String sql = "INSERT INTO tb_call VALUES (?,?,?,?,?) ON DUPLICATE KEY UPDATE `call_sum`=?, `call_duration_sum`=?;";

            //维度转换
            DimensionConvertor convertor = new DimensionConvertor();

            int dateId = convertor.getDimensionId(commDimension.getDateDimension());
            int contactId = convertor.getDimensionId(commDimension.getContactDimension());

            String date_contact = dateId + "_" + contactId;

            int countSum = Integer.valueOf(value.getCountSum());
            int durationSum = Integer.valueOf(value.getDurationSum());

            try {
                if(preparedStatement == null)
                {
                    preparedStatement = connection.prepareStatement(sql);
                }
                int i = 0;
                preparedStatement.setString(++i, date_contact);
                preparedStatement.setInt(++i, dateId);
                preparedStatement.setInt(++i, contactId);
                preparedStatement.setInt(++i, countSum);
                preparedStatement.setInt(++i, durationSum);
                preparedStatement.setInt(++i, countSum);
                preparedStatement.setInt(++i, durationSum);

                //将sql缓存到客户端
                preparedStatement.addBatch();

                batchSize++;
                if (batchSize >= batchBound) {
                    //批量执行sql
                    preparedStatement.executeBatch();
                    connection.commit();
                    batchSize = 0;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }


        }

        @Override
        public void close(TaskAttemptContext context) throws IOException, InterruptedException {
            try {
                if (preparedStatement != null) {
                    preparedStatement.executeBatch();
                    connection.commit();
                }
                JDBCUtil.close(connection, preparedStatement, null);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
