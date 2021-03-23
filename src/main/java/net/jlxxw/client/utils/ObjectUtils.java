package net.jlxxw.client.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * @author chunyang.leng
 * @date 2021-03-23 2:21 下午
 */
public class ObjectUtils {

    /**
     * 如果对象为空，返回默认值
     * @param obj 需要检查的对象
     * @param defaultValue 预定的默认值
     * @param <T>
     * @return 默认值
     */
    public static <T> T defaultValue(T obj, T defaultValue) {
        return Objects.isNull(obj) ? defaultValue : obj;
    }

    public static Integer parseInteger(String value,Integer defaultValue){
        return StringUtils.isNotBlank(value)?Integer.parseInt(value):defaultValue;
    }
}
