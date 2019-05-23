package com.enhinck.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MysqlDbRead {

    public static void main(String[] args) {
        String tableSchema = "nbgrid";
        final Database jdbcNb = new Database("com.mysql.jdbc.Driver",
                "jdbc:mysql://192.168.71.62/" + tableSchema, "root", "grid");
        Connection con = jdbcNb.getConnection();


    }


    public static List<Map<String, String>> getDatasByTableName(String tablename, String tableSchema, Connection con) {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        String sql = "select column_name,data_type,column_type,character_maximum_length,is_nullable,column_comment,column_default from information_schema.columns where table_name = ? AND table_schema = ?";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, tablename);
            pstmt.setString(2, tableSchema);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Map<String, String> map = new LinkedHashMap<String, String>();
                ResultSetMetaData m = null;
                m = rs.getMetaData();
                int columns = m.getColumnCount();
                for (int i = 1; i <= columns; i++) {
                    String columnName = m.getColumnName(i);
                    //int columnType = m.getColumnType(i);
                    Object value = rs.getObject(i);
                    map.put(columnName, value + "");
                }
                list.add(map);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.releaseConnection(null, pstmt, rs);
        }
        return list;
    }


    public static List<Map<String, String>> insertTest(Connection con) {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        String sql = "insert into test(ID,NAME) VALUES (?,?)";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = con.prepareStatement(sql);
            for (int i = 0; i < 22; i++) {
                for (int j = 0; j < 2 * 10000; j++) {
                    String uuid = UUID.randomUUID().toString();
                    uuid = uuid.replace("-", "");
                    pstmt.setString(1, uuid);
                    pstmt.setString(2, "张三");
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
                pstmt.clearBatch();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.releaseConnection(null, pstmt, rs);
        }
        return list;
    }


    public static List<String> showTables(Connection con) {
        List<String> list = new ArrayList<String>();
        String sql = "show tables";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                String tableName = rs.getString(1);
                list.add(tableName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.releaseConnection(null, pstmt, rs);
        }
        return list;
    }


}
