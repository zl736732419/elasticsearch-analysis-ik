package org.wltea.analyzer.custom;

import org.apache.commons.lang3.math.NumberUtils;

/**
 * @Author zhenglian
 * @Date 2018/9/12
 */
public class NumberUtil {
    public static Integer parseInt(Object obj) {
        if (StringUtils.isEmpty(obj) 
                || !NumberUtils.isDigits(obj.toString())) {
            return null;
        }
        if (obj.toString().contains(".")) {
            return null;
        }
        return Integer.parseInt(obj.toString());
    }
}
