package com.park.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据初始化配置
 * 在应用启动时执行SQL初始化脚本
 */
@Slf4j
@Component
public class DataInitConfig implements CommandLineRunner {

    @Autowired
    private DataSource dataSource;

    @Value("${spring.sql.init.mode:always}")
    private String sqlInitMode;

    @Override
    public void run(String... args) throws Exception {
        // 如果配置为 NEVER，跳过数据初始化
        if ("NEVER".equalsIgnoreCase(sqlInitMode)) {
            log.info("========== spring.sql.init.mode=NEVER，跳过数据初始化 ==========");
            return;
        }
        
        log.info("========== 开始执行数据初始化脚本 ==========");
        
        // 检测数据库类型
        boolean isMysql = isMysqlDatabase();
        
        if (isMysql) {
            log.info("检测到 MySQL 数据库，执行 MySQL 初始化脚本");
            // 先执行建库脚本
            String initSql = readSqlFile("/sql/init_database.sql");
            executeSql(initSql);
            // 再执行表结构与测试数据
            String schemaSql = readSqlFile("/sql/init.sql");
            executeSql(schemaSql);
            // 执行文件管理表结构
            String documentSql = readSqlFile("/schema/park_document.sql");
            executeSql(documentSql);
        } else {
            log.info("检测到非 MySQL 数据库，执行默认 schema.sql");
            String schemaSql = readSqlFile("/schema.sql");
            executeSql(schemaSql);
            // 执行文件管理表结构
            String documentSql = readSqlFile("/schema/park_document.sql");
            executeSql(documentSql);
        }
        
        log.info("========== 数据初始化脚本执行完成 ==========");
    }

    /**
     * 检测当前数据源是否为 MySQL
     */
    private boolean isMysqlDatabase() {
        try (Connection conn = dataSource.getConnection()) {
            String productName = conn.getMetaData().getDatabaseProductName().toLowerCase();
            return productName.contains("mysql");
        } catch (Exception e) {
            log.warn("检测数据库类型失败，默认按非 MySQL 处理", e);
            return false;
        }
    }

    /**
     * 读取SQL文件内容
     */
    private String readSqlFile(String filePath) {
        StringBuilder sb = new StringBuilder();
        try (InputStream is = getClass().getResourceAsStream(filePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // 跳过注释行
                if (!line.trim().startsWith("--")) {
                    sb.append(line).append("\n");
                }
            }
        } catch (Exception e) {
            log.error("读取SQL文件失败: {}", filePath, e);
        }
        return sb.toString();
    }

    /**
     * 执行SQL语句
     */
    private void executeSql(String sql) {
        // 按分号分割SQL语句
        String[] sqlStatements = sql.split(";");
        
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            
            for (String sqlStatement : sqlStatements) {
                sqlStatement = sqlStatement.trim();
                if (!sqlStatement.isEmpty()) {
                    try {
                        stmt.execute(sqlStatement);
                        log.debug("执行SQL成功: {}", sqlStatement.length() > 100 ? sqlStatement.substring(0, 100) + "..." : sqlStatement);
                    } catch (Exception e) {
                        log.warn("执行SQL失败: {}", sqlStatement.length() > 100 ? sqlStatement.substring(0, 100) + "..." : sqlStatement);
                        log.warn("错误信息: {}", e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            log.error("执行SQL脚本失败", e);
        }
    }
}
