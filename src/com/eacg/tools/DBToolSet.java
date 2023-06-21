package com.eacg.tools;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBToolSet {

    private static DataSource dataSource = DataSourceConfig.getDataSource();

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * 查询
     *
     * @param sql    sql
     * @param params 参数个数
     * @return {@link List}<{@link Map}<{@link String}, {@link Object}>>
     */
    public static List<Map<String, Object>> selectSQL(String sql, Object... params) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        try (Connection con = getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        row.put(metaData.getColumnLabel(i), rs.getObject(i));
                    }
                    resultList.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exception, possibly rethrow as a specific checked exception
        }
        return resultList;
    }

    /**
     * 插入sql
     *
     * @param sql    sql
     * @param params 参数个数
     */
    public static void insertSQL(String sql, Object... params) {
        try (Connection con = getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新sql
     *
     * @param sql    sql
     * @param params 参数个数
     */
    public static void updateSQL(String sql, Object... params) {
        try (Connection con = getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exception, possibly rethrow as a specific checked exception
        }
    }

    /**
     * 删除sql
     *
     * @param sql    sql
     * @param params 参数个数
     */
    public static void deleteSQL(String sql, Object... params) {
        try (Connection con = getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exception, possibly rethrow as a specific checked exception
        }
    }
}
