package com.iflytek.convertor;

import com.iflytek.kv.base.BaseDimension;
import com.iflytek.kv.key.ContactDimension;
import com.iflytek.kv.key.DateDimension;
import com.iflytek.util.JDBCInstance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class DimensionConvertor implements IConvertor {

    @Override
    public int getDimensionId(BaseDimension baseDimension) {


        /**
         * 获取sql
         */
        String[] sqls = getSql(baseDimension);

        Connection connection = null;
        try {
            connection = JDBCInstance.getInstance();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        /**
         * 执行sql
         */
        int id = excuteSql(connection, baseDimension, sqls);
        if (id == -1) {
            throw new RuntimeException("未匹配到相应维度！");
        }


        return id;

    }

    private int excuteSql(Connection connection, BaseDimension baseDimension, String[] sqls) {

        int id = -1;
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(sqls[0]);


            //第一次查询,查到就返回
            setArguments(preparedStatement, baseDimension);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }

            //没查到就插入
            preparedStatement = connection.prepareStatement(sqls[1]);
            setArguments(preparedStatement, baseDimension);
            preparedStatement.executeUpdate();

            //第二次查询
            preparedStatement = connection.prepareStatement(sqls[0]);
            setArguments(preparedStatement, baseDimension);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }


        return id;
    }

    /**
     * 预编译设置参数
     *
     * @param preparedStatement
     * @param baseDimension
     * @throws SQLException
     */
    private void setArguments(PreparedStatement preparedStatement, BaseDimension baseDimension) throws SQLException {

        int i = 0;
        if (baseDimension instanceof ContactDimension) {
            preparedStatement.setString(++i, ((ContactDimension) baseDimension).getPhone());
            preparedStatement.setString(++i, ((ContactDimension) baseDimension).getName());

        } else if (baseDimension instanceof DateDimension) {
            preparedStatement.setString(++i, ((DateDimension) baseDimension).getYear());
            preparedStatement.setString(++i, ((DateDimension) baseDimension).getMonth());
            preparedStatement.setString(++i, ((DateDimension) baseDimension).getDay());
        }


    }

    private String[] getSql(BaseDimension baseDimension) {

        String[] sqls = new String[2];

        if (baseDimension instanceof ContactDimension) {
            sqls[0] = "SELECT `id` FROM `tb_contacts` WHERE `telephone` = ? AND `name` = ?";
            sqls[1] = "insert into `tb_contacts` values(null,?,?)";

        } else if (baseDimension instanceof DateDimension) {
            sqls[0] = "SELECT `id` FROM `tb_dimension_date` WHERE `year` = ? and `month` = ? and `day` = ?";
            sqls[1] = "insert into `tb_dimension_date` values (null,?,?,?)";
        }

        return sqls;
    }


}
