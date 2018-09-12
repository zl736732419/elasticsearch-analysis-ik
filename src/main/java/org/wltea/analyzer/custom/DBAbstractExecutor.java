package org.wltea.analyzer.custom;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.common.logging.ESLoggerFactory;
import org.wltea.analyzer.dic.DictSegment;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 抽象热词更新执行器
 * @Author zhenglian
 * @Date 2018/9/12
 */
public abstract class DBAbstractExecutor {
    private static final Logger logger = ESLoggerFactory.getLogger(DBHotWordExecutor.class);
    private ScheduledExecutorService pool = Executors.newScheduledThreadPool(1);

    public void loadExtWord() {
        // 每个60秒重新更新一次热词
        Integer interval = JdbcReloadUtil.getInstance().getIntProperty("jdbc.reload.interval");
        if (StringUtils.isEmpty(interval)) {
            interval = 60000; 
        }
        pool.scheduleAtFixedRate(new LoadExtWordRunnable(), 10,
                interval, TimeUnit.MILLISECONDS);
    }

    /**
     * 热词更新runnable
     */
    private class LoadExtWordRunnable implements Runnable {
        @Override
        public void run() {
            JdbcReloadUtil instance = JdbcReloadUtil.getInstance();
            String driverClassName = instance.getDriverClassName();
            String url = instance.getUrl();
            String username = instance.getUsername();
            String password = instance.getPassword();
            // 获取热词更新sql
            String sql = getSql();
            List<String> columns = getColumns();
            List<Map<String, Object>> result = DBUtil.getInstance().listSql(driverClassName, url, username, password,
                    sql, columns);
            if (StringUtils.isEmpty(result)) {
                return;
            }
            DictSegment dict = getDictSegment();
            result.stream().forEach(map -> {
                String word = getWord(map);
                if (StringUtils.isEmpty(word)) {
                    return;
                }
                dict.fillSegment(word.trim().toLowerCase().toCharArray());
                logger.debug("load word {} into elasticsearch {} dict success.", word, getDictName());
            });
        }
    }
    
    protected abstract String getSql();
    protected abstract List<String> getColumns();
    protected abstract String getWord(Map<String, Object> map);
    protected abstract DictSegment getDictSegment();
    protected abstract String getDictName();
}
