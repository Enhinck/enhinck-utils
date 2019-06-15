package com.enhinck.db;

import com.enhinck.db.entity.InformationSchemaColumns;
import com.enhinck.db.util.SqlUtil;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import org.apache.commons.collections.map.HashedMap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;


public class MysqlDbRead {

    static String[] list = {"tb_fair_center_activity", "tb_fair_center_banner", "tb_fair_center_icon", "tb_house_tree_device", "tb_merchants_activity", "tb_merchants_coupons", "tb_merchants_img", "tb_merchants_manage_user", "tb_merchants_shop", "tb_message_ruxin", "tb_mobile_location_info", "tb_modian_call_log", "tb_reserve_place"};


    public static void main2(String[] args) {
        final Database newDb = new Database("com.mysql.jdbc.Driver",
                "jdbc:mysql://127.0.0.1:3306/ioc_dev", "root", "mysql");
        Connection newDbConnection = newDb.getConnection();
        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < list.length; i++) {
            String tableName = list[i];
            String tableDDL = getTableDDL(tableName, newDbConnection);
            stringBuilder.append("DROP TABLE IF EXISTS ").append(tableName).append(END_SQL).append(NEW_LINE);
            stringBuilder.append(tableDDL).append(END_SQL).append(NEW_LINE);
        }
        System.out.println(stringBuilder.toString());

    }


    public static void main(String[] args) {
        Map<String, InformationSchemaColumns> newMap = new HashedMap();

        InformationSchemaColumns informationSchemaColumns1 = new InformationSchemaColumns();
        informationSchemaColumns1.setColumnComment("沙河");
        newMap.put("1", informationSchemaColumns1);
        InformationSchemaColumns informationSchemaColumns2 = new InformationSchemaColumns();
        informationSchemaColumns2.setIsNullable("YES");
        newMap.put("2", informationSchemaColumns2);
        newMap.put("3", null);

        Map<String, InformationSchemaColumns> oldMap = new HashedMap();
        InformationSchemaColumns informationSchemaColumns11 = new InformationSchemaColumns();
        informationSchemaColumns11.setColumnComment("沙河");
        oldMap.put("1", informationSchemaColumns11);
        InformationSchemaColumns informationSchemaColumns22 = new InformationSchemaColumns();
        informationSchemaColumns22.setIsNullable("NO");
        oldMap.put("2", informationSchemaColumns22);
        MapDifference<String, InformationSchemaColumns> difference = Maps.difference(newMap, oldMap);

        difference.entriesOnlyOnLeft();


        difference.entriesDiffering().forEach((key, diff) -> {
            diff.leftValue();

            diff.rightValue();
        });
        System.out.println(difference);


    }

    public static void main3(String[] args) {
        final Database oldDB = new Database("com.mysql.jdbc.Driver",
                "jdbc:mysql://127.0.0.1:3306/ioc_uat", "root", "mysql");
        Connection oldDBConnection = oldDB.getConnection();
        final Database newDb = new Database("com.mysql.jdbc.Driver",
                "jdbc:mysql://127.0.0.1:3306/ioc_dev", "root", "mysql");
        Connection newDbConnection = newDb.getConnection();
        //  String test = compareTableNames(oldDBConnection, newDbConnection);
        //System.out.println(test);

        Map<String, InformationSchemaColumns> informationSchemaColumnsMap = getColumnByTableName("sys_menu", "ioc_dev", newDbConnection);

        Map<String, InformationSchemaColumns> newMap = new HashedMap();

        Map<String, InformationSchemaColumns> oldMap = new HashedMap();

        informationSchemaColumnsMap.forEach((key, value) -> {
            System.out.println(key + "----" + value);
        });
    }


    /**
     * 获取建表语句
     *
     * @param tablename
     * @param con
     * @return
     */
    public static String getTableDDL(String tablename, Connection con) {
        String sql = "show create table " + tablename;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                String create = rs.getString(2);
                return create;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.releaseConnection(null, pstmt, rs);
        }
        return "";
    }


    // 1.比对 表列表  少表 直接生成建表语句列表

    public static final String NEW_LINE = "\n";
    public static final String END_SQL = ";";

    /**
     * @param oldDbConnection 需要升级的库
     * @param newDBConnection 已升级的库
     */
    public static String compareTableNames(final Connection oldDbConnection, final Connection newDBConnection) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("-- 新增的表开始").append(NEW_LINE);
        Set<String> oldTableNameSets = getTablesSet(oldDbConnection);
        Set<String> newTableNameSets = getTablesSet(newDBConnection);
        newTableNameSets.removeAll(oldTableNameSets);
        // 增量表
        Set<String> addTableNameSets = newTableNameSets;
        //
        addTableNameSets.forEach(tableName -> {
            String tableDDL = getTableDDL(tableName, newDBConnection);
            stringBuilder.append(tableDDL).append(END_SQL).append(NEW_LINE);
        });
        stringBuilder.append("-- 新增的表开始结束").append(NEW_LINE);

        return stringBuilder.toString();
    }

    // 2.比对表字段
    /*
    字段列表比对

    // add表字段

    相同字段名比对
    // update 表字段

     */
    public String compareTableColumns(final Connection oldDbConnection, final Connection newDBConnection) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("-- 新增的字段开始");
        Set<String> oldTableNameSets = getTablesSet(oldDbConnection);
        oldTableNameSets.forEach(tableName -> {


        });
        //
        stringBuilder.append("-- 新增的字段结束");
        return stringBuilder.toString();
    }


    public String compareColumns(String tableName, final Connection oldDbConnection, final Connection newDBConnection) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("--  table ").append(tableName).append(NEW_LINE);

        stringBuilder.append("-- table end -----").append(NEW_LINE);
        return stringBuilder.toString();
    }

    public static Map<String, InformationSchemaColumns> getColumnByTableName(String tablename, String tableSchema, Connection con) {
        Map<String, InformationSchemaColumns> map = new LinkedHashMap<>();
        SqlUtil.Sqls sqls = SqlUtil.getWhere(InformationSchemaColumns.class).andEqualTo("tableName", tablename).andEqualTo("tableSchema", tableSchema).orderByAsc("ordinalPosition");
        String sql = SqlUtil.getSelectSql(InformationSchemaColumns.class, sqls.build());
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = con.prepareStatement(sql);
            sqls.setParams(pstmt);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                InformationSchemaColumns informationSchemaColumns = SqlUtil.getDbData(InformationSchemaColumns.class, rs);
                map.put(informationSchemaColumns.getColumnName(), informationSchemaColumns);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.releaseConnection(null, pstmt, rs);
        }
        return map;
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


    public static Set<String> getTablesSet(Connection con) {
        Set<String> sets = new HashSet<>();
        String sql = "show tables";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                String tableName = rs.getString(1);
                sets.add(tableName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.releaseConnection(null, pstmt, rs);
        }
        return sets;
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
