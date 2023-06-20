package com.eacg.tools;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.yaml.snakeyaml.Yaml;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class DataSourceConfig {

    private static DataSource dataSource;

    static {
        Yaml yaml = new Yaml();
        try (InputStream in = DataSourceConfig.class.getResourceAsStream("/config.yml")) {
            Map<String, Object> yamlMap = yaml.load(in);
            Map<String, String> dbConfig = (Map<String, String>) yamlMap.get("database");
            String url = dbConfig.get("url");
            String username = dbConfig.get("username");
            String password = dbConfig.get("password");
            System.out.println(url);
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(url);
            config.setUsername(username);
            config.setPassword(password);
            config.setMaximumPoolSize(10);
            config.setConnectionTimeout(30000);
            config.setIdleTimeout(600000);
            config.setMaxLifetime(1800000);

            dataSource = new HikariDataSource(config);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load database configuration", e);
        }
    }

    public static DataSource getDataSource() {
        return dataSource;
    }
}
