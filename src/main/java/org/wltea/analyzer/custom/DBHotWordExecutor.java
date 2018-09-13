package org.wltea.analyzer.custom;

import org.wltea.analyzer.dic.DictSegment;
import org.wltea.analyzer.dic.Dictionary;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 定时加载热词的监控器
 * @Author zhenglian
 * @Date 2018/9/11 23:32
 */
public class DBHotWordExecutor extends DBAbstractExecutor {
    private static final String HOT_WORD = "hotWord";
    
    @Override
    protected String getSql() {
        String sql = JdbcReloadUtil.getInstance().getStringProperty("jdbc.reload.sql");
        return sql;
    }

    @Override
    protected List<String> getColumns() {
        List<String> columns = new ArrayList<>();
        columns.add(HOT_WORD);
        return columns;
    }

    @Override
    protected DictSegment getDictSegment() {
        return Dictionary.getSingleton().get_MainDict();
    }

    @Override
    protected String getWord(Map<String, Object> map) {
        Object value = map.get(HOT_WORD);
        if (StringUtils.isEmpty(value)) {
            logger.warn("this record from db is invalid, don't have {} column", HOT_WORD);
            return null;
        }
        String word = value.toString();
        return word;
    }

    @Override
    protected String getDictName() {
        return "mainDict";
    }
    
}
