package org.wltea.analyzer.custom;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.common.logging.ESLoggerFactory;
import org.wltea.analyzer.dic.DictSegment;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 抽象热词更新执行器
 * @Author zhenglian
 * @Date 2018/9/12
 */
public abstract class DBAbstractExecutor {
    protected static final Logger logger = ESLoggerFactory.getLogger(DBHotWordExecutor.class);
    private ScheduledExecutorService pool = Executors.newScheduledThreadPool(1);

    public void loadExtWord() {
        logger.info("load custom {} dict.....", getDictName());
        // 每个60秒重新更新一次热词
        Integer interval = PropertyUtil.getInstance().getIntProperty("jdbc.reload.interval");
        if (StringUtils.isEmpty(interval)) {
            interval = 60000; 
        }
        pool.scheduleAtFixedRate(new LoadExtWordRunnable(), 1000,
                interval, TimeUnit.MILLISECONDS);
    }
    
    public static void main(String[] args) {
        new DBHotWordExecutor().loadExtWord();
    }

    /**
     * 热词更新runnable
     */
    private class LoadExtWordRunnable implements Runnable {
        @Override
        public void run() {
            try {
                String location = getLocation();
                logger.info("load dict {} words from remote, location: {}", getDictName(), location);
                List<String> words = HttpUtil.getRemoteWords(location);
                if (StringUtils.isEmpty(words)) {
                    logger.debug("no result for dict {} from db.");
                    return;
                }
                DictSegment dict = getDictSegment();
                words.stream().forEach(word -> {
                    if (StringUtils.isEmpty(word)) {
                        return;
                    }
                    dict.fillSegment(word.trim().toLowerCase().toCharArray());
                    logger.info("load word {} into elasticsearch {} dict success.", word, getDictName());
                });
            } catch (Exception e) {
                logger.error("execute load word error. info: {}", e);
            }
        }
    }
    
    protected abstract String getLocation();
    protected abstract DictSegment getDictSegment();
    protected abstract String getDictName();
}
