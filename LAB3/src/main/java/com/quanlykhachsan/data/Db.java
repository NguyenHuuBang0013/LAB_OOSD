package com.quanlykhachsan.data;

import java.io.InputStream;
import java.sql.*;
import java.util.*;
import javax.swing.table.DefaultTableModel;

public final class Db {
    private static final Properties P = new Properties();

    static {
        try (InputStream in = Db.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in == null) throw new RuntimeException("Không tìm thấy db.properties");
            P.load(in);
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (Exception e) {
            throw new RuntimeException("Không khởi tạo JDBC: " + e.getMessage(), e);
        }
    }

    private Db() {}

    public static Connection open() throws SQLException {
        return DriverManager.getConnection(
                P.getProperty("db.url"),
                P.getProperty("db.user"),
                P.getProperty("db.password"));
    }

    public static DefaultTableModel query(String sql, Object... params) {
        try (Connection c = open();
             PreparedStatement st = c.prepareStatement(sql)) {
            bind(st, params);
            try (ResultSet rs = st.executeQuery()) {
                ResultSetMetaData md = rs.getMetaData();
                int n = md.getColumnCount();
                String[] cols = new String[n];
                for (int i = 0; i < n; i++) cols[i] = md.getColumnLabel(i + 1);
                DefaultTableModel m = new DefaultTableModel(cols, 0);
                while (rs.next()) {
                    Object[] row = new Object[n];
                    for (int i = 0; i < n; i++) row[i] = rs.getObject(i + 1);
                    m.addRow(row);
                }
                return m;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static int execute(String sql, Object... params) {
        try (Connection c = open(); PreparedStatement st = c.prepareStatement(sql)) {
            bind(st, params);
            return st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static Object scalar(String sql, Object... params) {
        try (Connection c = open(); PreparedStatement st = c.prepareStatement(sql)) {
            bind(st, params);
            try (ResultSet rs = st.executeQuery()) {
                return rs.next() ? rs.getObject(1) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static void bind(PreparedStatement st, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            Object p = params[i];
            if (p instanceof java.util.Date && !(p instanceof java.sql.Date) && !(p instanceof java.sql.Timestamp)) {
                p = new java.sql.Date(((java.util.Date) p).getTime());
            }
            st.setObject(i + 1, p);
        }
    }

    public static <T> T tx(SqlWork<T> work) {
        try (Connection c = open()) {
            c.setAutoCommit(false);
            try {
                T result = work.run(c);
                c.commit();
                return result;
            } catch (Exception e) {
                try { c.rollback(); } catch (SQLException ignored) {}
                if (e instanceof RuntimeException re) throw re;
                throw new RuntimeException(e.getMessage(), e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @FunctionalInterface
    public interface SqlWork<T> {
        T run(Connection c) throws Exception;
    }
}
