package cn.org.zhixiang.utils;

import java.util.Map;
import java.util.Set;

/**
 * describe:
 *
 * @创建人 syj
 * @创建时间 2018/09/30
 * @描述
 */
public class SelectPagePackUtil {

    public static String packSerach(Map<String, String> searchMap){
        if(searchMap==null){
            return null;
        }
        StringBuffer stringBuffer=new StringBuffer();
        Set<Map.Entry<String, String>> entrySet= searchMap.entrySet();
        for (Map.Entry<String, String> entry:entrySet){
            stringBuffer.append(" and ");
            stringBuffer.append(FieldUtil.toUnderLineString(entry.getKey(),0));
            stringBuffer.append(" = '");
            stringBuffer.append(entry.getValue());
            stringBuffer.append("'");
        }
        return stringBuffer.toString();
    }
    public static String packLikeSerach(Map<String, String> likeSearchMap){
        if(likeSearchMap==null){
            return null;
        }
        StringBuffer stringBuffer=new StringBuffer();
        Set<Map.Entry<String, String>> entrySet= likeSearchMap.entrySet();
        for (Map.Entry<String, String> entry:entrySet){
            stringBuffer.append(" and ");
            stringBuffer.append(FieldUtil.toUnderLineString(entry.getKey(),0));
            stringBuffer.append(" like %'");
            stringBuffer.append(entry.getValue());
            stringBuffer.append("'");
        }
        return stringBuffer.toString();
    }
    public static String packOrder(Map<String, String> orderMap){
        if(orderMap==null){
            return null;
        }
        StringBuffer stringBuffer=new StringBuffer();
        Set<Map.Entry<String, String>> entrySet= orderMap.entrySet();
        stringBuffer.append(" order by  ");
        for (Map.Entry<String, String> entry:entrySet){
            stringBuffer.append(FieldUtil.toUnderLineString(entry.getKey(),0));
            stringBuffer.append(" ");
            stringBuffer.append(entry.getValue());
            stringBuffer.append(",");
        }
        return stringBuffer.substring(0,stringBuffer.length()-1).toString();
    }
    public static String packGroup(String[] groupArray){
        if(groupArray==null){
            return null;
        }
        StringBuffer stringBuffer=new StringBuffer();

        stringBuffer.append(" group by  ");
        for ( String entry:groupArray){
            stringBuffer.append(FieldUtil.toUnderLineString(entry,0));
            stringBuffer.append(",");
        }
        return stringBuffer.substring(0,stringBuffer.length()-1).toString();
    }
}