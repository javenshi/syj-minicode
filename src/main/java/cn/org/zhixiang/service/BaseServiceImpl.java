package cn.org.zhixiang.service;


import cn.org.zhixiang.config.SpringContextUtil;
import cn.org.zhixiang.entity.GridPageRequest;
import cn.org.zhixiang.mapper.BaseMapper;
import cn.org.zhixiang.utils.BeanMapUtil;
import cn.org.zhixiang.utils.FieldUtil;
import cn.org.zhixiang.utils.SelectPagePackUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.util.StringUtils;


import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;



/**
 * describe:
 *
 * @创建人 syj
 * @创建时间 2018/09/30
 * @描述
 */

public class BaseServiceImpl implements BaseService {
    private Class clazz;
    private Field[] fields;
    private String baseResult;
    private String tableName;
    Map<String,Object> entity=new HashMap<>();


    private static BaseMapper baseMapper= SpringContextUtil.getBean("baseMapper");

    public BaseServiceImpl(Class clazz){
        this.clazz=clazz;
        this.init();
    }

    private void init(){
        fields = clazz.getDeclaredFields();
        StringBuffer classNameBuffer=new StringBuffer(clazz.getName());
        String className=classNameBuffer.substring(classNameBuffer.lastIndexOf(".")+1,classNameBuffer.length());
        tableName=FieldUtil.toUnderLineString(className,1);
        StringBuffer baseResultBuffer=new StringBuffer();
        for (Field field : fields){
            String underLineString=FieldUtil.toUnderLineString(field.getName(),0);
            baseResultBuffer.append(underLineString+" as " + field.getName()+",");
            entity.put(field.getName(),underLineString);
        }
        baseResult=baseResultBuffer.substring(0,baseResultBuffer.length()-1).toString();
    }
    @Override
    public Object selectOneById(String id) {
        Map<String,Object> resultMap=baseMapper.selectOneById(baseResult,tableName,id);
        return BeanMapUtil.mapToBean(resultMap,clazz);
    }

    @Override
    public PageInfo<Object> selectByPage(GridPageRequest gridPageRequest) {
        PageHelper.startPage(gridPageRequest.getPageNum(), gridPageRequest.getPageSize());
        StringBuffer sql=new StringBuffer("where 1=1 ");
        String search= SelectPagePackUtil.packSerach(gridPageRequest.getSearchMap());
        String likeSearch=SelectPagePackUtil.packLikeSerach(gridPageRequest.getLikeSearchMap());
        String order=SelectPagePackUtil.packOrder(gridPageRequest.getOrderMap());
        String group=SelectPagePackUtil.packGroup(gridPageRequest.getGroupArray());
        if(search!=null){
            sql.append(search);
        }
        if(likeSearch!=null){
            sql.append(likeSearch);
        }
        if(order!=null){
            sql.append(order);
        }
        if(group!=null){
            sql.append(group);
        }
        List<Map<String, Object>> resultList=baseMapper.selectByPage(baseResult,tableName,sql.toString());
        PageInfo pageInfo = new PageInfo(BeanMapUtil.mapsToObjects(resultList,clazz));
        return pageInfo;
    }

    @Override
    public void deleteById(String id) {
        baseMapper.deleteById(tableName,id);
    }

    @Override
    public void deleteByIds(List<String> idList) {
        String ids= String.join(",",idList);
        baseMapper.deleteByIds(tableName,ids);
    }



    @Override
    public long insertSelective(Object object) {
        StringBuffer keyBuffer=new StringBuffer();
        StringBuffer valueBuffer=new StringBuffer();
        for (Field field: fields){
            try {
                Field objectField = object.getClass().getDeclaredField(field.getName());
                objectField.setAccessible(true);
                Object objectValue= objectField.get(object);
                if(objectValue!=null){
                    keyBuffer.append("`");
                    keyBuffer.append(field.getName());
                    keyBuffer.append("`,");
                    valueBuffer.append("'");
                    valueBuffer.append(objectValue.toString());
                    valueBuffer.append("',");
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        String insertKey=keyBuffer.substring(0,keyBuffer.length()-1).toString();
        String valueKey=valueBuffer.substring(0,valueBuffer.length()-1).toString();
        long id=baseMapper.insert(tableName,insertKey,valueKey);
        return id;
    }

    @Override
    public void updateByIdSelective(Object object) {
        StringBuffer keyBuffer=new StringBuffer();
        for (Field field: fields){
            try {
                Field objectField = object.getClass().getDeclaredField(field.getName());
                objectField.setAccessible(true);
                Object objectValue= objectField.get(object);
                if(objectValue!=null&& !Objects.equals("id",objectField.getName())){
                    keyBuffer.append("set `");
                    keyBuffer.append(field.getName());
                    keyBuffer.append("` ='");
                    keyBuffer.append(objectValue);
                    keyBuffer.append("', ");
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        String param=keyBuffer.substring(0,keyBuffer.length()-1).toString();
        baseMapper.update(tableName,param,"id");

    }


}
