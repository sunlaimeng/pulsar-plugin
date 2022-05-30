package auth.util;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author lamen 2022/5/30
 */
public class DruidUtil {
    private static final Logger logger = LoggerFactory.getLogger(DruidUtil.class);
    private static DataSource dataSource;
    static {
        try {
            Properties properties = new Properties();
            InputStream inputStream = DruidUtil.class.getClassLoader().getResourceAsStream("druid.properties");
            properties.load(inputStream);
            dataSource = DruidDataSourceFactory.createDataSource(properties);
        } catch (Exception e) {
            logger.error("Druid dataSource init failed.", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
