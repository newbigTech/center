/*
 * Copyright (C), 2002-2014, 汇通达网络股份有限公司
 * FileName: StringUtil.java
 * Author:   12061769
 * Date:     2014-7-29 上午09:42:23
 * Description: //模块目的、功能描述      
 * History: //修改记录
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间            版本号                  描述
 */
package cn.htd.zeus.tc.common.util;

import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 * 〈一句话功能简述〉<br>
 * String工具类
 * 
 * @author 12061769
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class StringUtilHelper {

    /**
     * 构造方法
     */
    private StringUtilHelper() {

    }

    /**
     * 不为空验证 为空返回false 不为空返回true
     * 
     * @param obj
     * @return 为空返回false，不为空返回true。
     */
    public static boolean isNotNull(Object obj) {
        return (null == obj || "".equals(String.valueOf(obj).trim())) ? false : true;
    }

    /**
     * 不为空验证 只要有一个 为空就返回false 都不为空 则返回true
     * 
     * @param objs
     * @return 只要有一个为空就返回false，都不为空 则返回true。
     */
    public static boolean isNotNull(Object... objs) {
        if (null == objs) {
            return false;
        }
        for (Object obj : objs) {
            if (null == obj || "".equals(String.valueOf(obj).trim())) {
                return false;
            }
        }
        return true;
    }

    /**
     * 为空验证 为空返回true 不为空返回false
     * 
     * @param obj
     * @return 为空返回true，不为空返回false。
     */
    public static boolean isNull(Object obj) {
        return (null == obj || "".equals(String.valueOf(obj).trim())) ? true : false;
    }

    /**
     * 为空验证 只要有一个为空就返回true 都不为空则返回false
     * 
     * @param objs
     * @return 只要有一个为空就返回true，都不为空则返回false。
     */
    public static boolean isNull(Object... objs) {
        if (null == objs) {
            return true;
        }
        for (Object obj : objs) {
            if (null == obj || "".equals(String.valueOf(obj).trim())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 全部为空验证，全部为空返回true,只要有一个不为空返回false
     * 
     * @param objs
     * @return 全部为空返回true，只要有一个不为空返回false。
     */
    public static boolean allIsNull(Object... objs) {
        if (null == objs) {
            return true;
        }
        for (Object obj : objs) {
            if (null != obj && !"".equals(String.valueOf(obj).trim())) {
                return false;
            }
        }
        return true;
    }

    /**
     * 全部不为空验证，全部不为空返回true,只要有一个为空返回false
     * 
     * @param objs
     * @return 全部不为空返回true，只要有一个为空返回false。
     */
    public static boolean allIsNotNull(Object... objs) {
        if (null == objs) {
            return true;
        }
        for (Object obj : objs) {
            if (null == obj || "".equals(String.valueOf(obj).trim())) {
                return false;
            }
        }
        return true;
    }

    /**
     * 数字格式验证 正确返回true 错误返回false
     * 
     * @param obj
     * @return 正确返回true，错误返回false。
     */
    public static boolean parseNumFormat(String obj) {
        if (StringUtils.isEmpty(obj)) {
            return false;
        }
        boolean flag = true;
        try {
            Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");  
            flag = pattern.matcher(obj).matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }
 

}
