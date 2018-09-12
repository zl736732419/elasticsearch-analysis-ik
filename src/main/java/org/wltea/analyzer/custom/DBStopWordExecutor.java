package org.wltea.analyzer.custom;

import org.wltea.analyzer.dic.DictSegment;
import org.wltea.analyzer.dic.Dictionary;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 定时加载停用词的监控器
 * @Author zhenglian
 * @Date 2018/9/11 23:32
 */
public class DBStopWordExecutor extends DBAbstractExecutor {
    private static final String STOP_WORD = "stopWord";

    @Override
    protected String getSql() {
        String sql = JdbcReloadUtil.getInstance().getStringProperty("jdbc.reload.stopword.sql");
        return sql;
    }

    @Override
    protected List<String> getColumns() {
        List<String> columns = new ArrayList<>();
        columns.add(STOP_WORD);
        return columns;
    }

    @Override
    protected String getWord(Map<String, Object> map) {
        Object value = map.get(STOP_WORD);
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        String word = value.toString();
        return word;
    }

    @Override
    protected DictSegment getDictSegment() {
        return Dictionary.getSingleton().get_StopWords();
    }

    @Override
    protected String getDictName() {
        return "stopWordDict";
    }
}
