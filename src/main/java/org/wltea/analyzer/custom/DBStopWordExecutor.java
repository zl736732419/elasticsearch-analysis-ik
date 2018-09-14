package org.wltea.analyzer.custom;

import org.wltea.analyzer.dic.DictSegment;
import org.wltea.analyzer.dic.Dictionary;

/**
 * 定时加载停用词的监控器
 * @Author zhenglian
 * @Date 2018/9/11 23:32
 */
public class DBStopWordExecutor extends DBAbstractExecutor {
    @Override
    protected String getLocation() {
        return PropertyUtil.getInstance().getStopWordLocation();
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
