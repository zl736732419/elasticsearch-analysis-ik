package org.wltea.analyzer.custom;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.common.logging.ESLoggerFactory;
import org.wltea.analyzer.dic.Dictionary;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Properties;

/**
 * @Author zhenglian
 * @Date 2018/9/12 20:55
 */
public class JdbcReloadUtil {
    private static final Logger logger = ESLoggerFactory.getLogger(JdbcReloadUtil.class);
    private static final String FILE_NAME = "jdbc-reload.properties";
    private Properties props;
    
    private String driverClassName;
    private String url;
    private String username;
    private String password;
    
    private JdbcReloadUtil() {
        loadDBReloadProps();
    }
    
    private static class Inner {
        private static JdbcReloadUtil instance = new JdbcReloadUtil();
    }
    
    public static JdbcReloadUtil getInstance() {
        return Inner.instance;
    }

    private void loadDBReloadProps() {
        this.props = new Properties();
        InputStream input = null;
        Path confDirPath = Dictionary.getSingleton().getConf_dir();
//        Path confDirPath = new File("E:\\workspace\\elasticsearch-analysis-ik\\config").toPath();
        logger.info("conf dir path: {}....", confDirPath);
        Path configFile = confDirPath.resolve(FILE_NAME);
        try {
            logger.info("try load hot word db config from {}", configFile);
            input = new FileInputStream(configFile.toFile());
        } catch (FileNotFoundException e) {
            logger.error("load hot word db config error {}", e);
        }
        if (input != null) {
            try {
                props.load(input);
            } catch (Exception e) {
                logger.error("ik-analyzer load hot word db config", e);
            }
        }
        
        driverClassName = getStringProperty("jdbc.driverClassName");
        url = getStringProperty("jdbc.url");
        username = getStringProperty("jdbc.username");
        password = getStringProperty("jdbc.password");
    }
    
    public Object getProperty(String key) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        return props.get(key);
    }

    public String getStringProperty(String key) {
        Object object = getProperty(key);
        if (StringUtils.isEmpty(object)) {
            return null;
        }
        return object.toString();
    }

    public Integer getIntProperty(String key) {
        Object object = getProperty(key);
        if (StringUtils.isEmpty(object)) {
            return null;
        }
        return NumberUtil.parseInt(object);
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
