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
public class PropertyUtil {
    private static final Logger logger = ESLoggerFactory.getLogger(PropertyUtil.class);
    private static final String FILE_NAME = "word-reload.properties";
    private Properties props;
    
    private String hotWordLocation;
    private String stopWordLocation;
    
    private static class Inner {
        private static PropertyUtil instance = new PropertyUtil();
    }
    
    public static PropertyUtil getInstance() {
        return Inner.instance;
    }

    private void loadReloadPropFile() {
        this.props = new Properties();
        InputStream input = null;
        Path confDirPath = Dictionary.getSingleton().getConf_dir();
        // windows environment test used path
//        Path confDirPath = new File("E:\\workspace\\elasticsearch-analysis-ik\\config").toPath();
        logger.info("conf dir path: {}....", confDirPath);
        Path configFile = confDirPath.resolve(FILE_NAME);
        try {
            logger.info("try load hot word config from {}", configFile);
            input = new FileInputStream(configFile.toFile());
        } catch (FileNotFoundException e) {
            logger.error("load hot word config error {}", e);
        }
        if (input != null) {
            try {
                props.load(input);
            } catch (Exception e) {
                logger.error("ik-analyzer load hot word db config", e);
            }
        }
    }

    public Object getProperty(String key) {
        loadReloadPropFile();
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        return props.get(key);
    }

    public String getStringProperty(String key) {
        loadReloadPropFile();
        Object object = getProperty(key);
        if (StringUtils.isEmpty(object)) {
            return null;
        }
        return object.toString();
    }

    public Integer getIntProperty(String key) {
        loadReloadPropFile();
        Object object = getProperty(key);
        if (StringUtils.isEmpty(object)) {
            return null;
        }
        return NumberUtil.parseInt(object);
    }

    public String getHotWordLocation() {
        loadReloadPropFile();
        hotWordLocation = getStringProperty("es.hot_word.reload.location");
        return hotWordLocation;
    }

    public String getStopWordLocation() {
        loadReloadPropFile();
        stopWordLocation = getStringProperty("es.stop_word.reload.location");
        return stopWordLocation;
    }
}
