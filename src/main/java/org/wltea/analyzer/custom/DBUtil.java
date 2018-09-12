package org.wltea.analyzer.custom;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.common.logging.ESLoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据库工具类
 * @Author zhenglian
 * @Date 2018/9/12 21:05
 */
public class DBUtil {
    private static final Logger logger = ESLoggerFactory.getLogger(DBUtil.class);
    
    private DBUtil() {
    }
    
    public static DBUtil getInstance() {
        return Inner.instance;
    }
    
    private static class Inner {
        private static DBUtil instance = new DBUtil();
    }
    
    public Connection getConnection(String driverClassName, String url, String username, String password) {
        try {
            Class.forName(driverClassName);
        } catch (ClassNotFoundException e) {
            logger.error("load db class {} error. info: {}", driverClassName, e);
        }
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            logger.error("get db connection error. info: {}", e);
        }
        return connection;
    }
    
    public void close(Connection conn, Statement stat, ResultSet rs) {
        if (StringUtils.isNotEmpty(rs)) {
            try {
                rs.close();
            } catch (SQLException e) {
                logger.error("close result set error. info: {}", e);
            }
        }
        if (StringUtils.isNotEmpty(stat)) {
            try {
                stat.close();
            } catch (SQLException e) {
                logger.error("close statement error. info: {}", e);
            }
        }
        if (StringUtils.isNotEmpty(conn)) {
            try {
                conn.close();
            } catch (SQLException e) {
                logger.error("close connection error. info: {}", e);
            }
        }
    }
    
    public List<Map<String, Object>> listSql(String driverClassName, String url, String username, String password, 
                                String sql, List<String> columns) {
        if (StringUtils.isEmpty(sql) || StringUtils.isEmpty(columns)) {
            logger.error("invalid params: sql: {}, columns: {}", sql, columns);
            return null;
        }
        Connection connection = getConnection(driverClassName, url, username, password);
        if (StringUtils.isEmpty(connection)) {
            logger.error("get db connection error. params: {}, {}, {}, {}", driverClassName, url, username, password);
            return null;
        }
        PreparedStatement pstat;
        try {
            pstat = connection.prepareStatement(sql);
        } catch (SQLException e) {
            logger.error("create prepare statement error. info: {}", e);
            return null;
        }
        List<Map<String, Object>> result = new ArrayList<>();
        ResultSet rs = null;
        try {
            rs = pstat.executeQuery();
            while(rs.next()) {
                Map<String, Object> map = new HashMap<>();
                Object value;
                for (String column : columns) {
                    try {
                        value = rs.getObject(column);
                    } catch (SQLException e) {
                        logger.error("get column [{}] value from db error. info: {}", column, e);
                        throw e;
                    }
                    map.put(column, value);
                }
                result.add(map);
            }
        } catch (SQLException e) {
            logger.error("execute sql query error, sql: {}, info: {}", sql, e);
            return null;
        } finally {
            close(connection, pstat, rs);
        }
        return result;
    }
    
}
