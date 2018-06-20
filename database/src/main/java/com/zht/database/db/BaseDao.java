package com.zht.database.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.zht.database.annotation.DbField;
import com.zht.database.annotation.DbTable;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class BaseDao<T> implements IBaseDao<T> {
    //持有数据库的引用
    private SQLiteDatabase sqLiteDatabase;
    //表名
    private String tableName;
    //持有操作数据库所对应的的java类型
    private Class<T> entityClass;
    //用来表示是否做过初始化操作
    private boolean isInit = false;
    //定义一个缓存空间
    private HashMap<String,Field>  cacheMap;

    protected boolean init(SQLiteDatabase sqLiteDatabase,Class<T> entityClass){
        this.sqLiteDatabase = sqLiteDatabase;
        this.entityClass = entityClass;
        if(!isInit){
            if(entityClass.getAnnotation(DbTable.class)==null){
                //反射到类名
                tableName = entityClass.getSimpleName();
            }else{
                //取注解上的名字
                tableName = entityClass.getAnnotation(DbTable.class).value();
            }

            if(!sqLiteDatabase.isOpen()){
                return false;
            }
            //执行建表操作
            init(sqLiteDatabase,entityClass);
            //初始化缓存机制
            initCacheMap();
        }
        return isInit;
    }

    //获取建表所需要的sql
    private String getCreatTableSQL(){
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("creat table if not exists");
        stringBuffer.append(tableName+"(");
        //反射得到所有的成员变量
        Field[] fields = entityClass.getDeclaredFields();
        for (Field field : fields) {
            Class type = field.getType();
            if(field.getAnnotation(DbField.class)==null){
                if(type==String.class){
                    stringBuffer.append(field.getAnnotation(DbField.class).value()+" TEXT,");
                }else if(type == Integer.class){
                    stringBuffer.append(field.getAnnotation(DbField.class).value()+" INTEGER,");
                }else if(type == Long.class){
                    stringBuffer.append(field.getAnnotation(DbField.class).value()+" BIGINT,");
                }else if(type == Double.class){
                    stringBuffer.append(field.getAnnotation(DbField.class).value()+" DOUBLE,");
                }else if(type == byte[].class){
                    stringBuffer.append(field.getAnnotation(DbField.class).value()+" BLOB,");
                }else{
                    continue;
                }
            }else{
                if(type==String.class){
                    stringBuffer.append(field.getName()+" TEXT,");
                }else if(type == Integer.class){
                    stringBuffer.append(field.getName()+" INTEGER,");
                }else if(type == Long.class){
                    stringBuffer.append(field.getName()+" BIGINT,");
                }else if(type == Double.class){
                    stringBuffer.append(field.getName()+" DOUBLE,");
                }else if(type == byte[].class){
                    stringBuffer.append(field.getName()+" BLOB,");
                }else{
                    continue;
                }
            }
        }
        if(stringBuffer.charAt(stringBuffer.length()-1)==','){
           stringBuffer.deleteCharAt(stringBuffer.length()-1);
        }
        stringBuffer.append(")");
        return stringBuffer.toString();
    }

    private void initCacheMap(){
        //获取到所有的列名
        String sql = "select * from "+tableName+" limit 1,0";
        Cursor cursor = sqLiteDatabase.rawQuery(sql,null);
        String[] columnNames = cursor.getColumnNames();
        //获取所有的成员变量
        Field[] columnFields = entityClass.getDeclaredFields();
        //打开所有字段的访问权限
        for (Field field : columnFields) {
            field.setAccessible(true);
        }
        //对1和2进行映射
        for (String name : columnNames) {
            //采用一种匹配的关系进行缓存
            Field columnField = null;
            for (Field field : columnFields) {
                String fieldName = null;
                //获取相对应的表名
                if(field.getAnnotation(DbField.class)!=null){
                    fieldName = field.getAnnotation(DbField.class).value();
                }else{
                    fieldName = field.getName();
                }
                if(fieldName.equals(name)){
                    columnField = field;
                    break;
                }
            }
            if(columnField!=null){
                cacheMap.put(name,columnField);
            }
        }
    }

    @Override
    public long insert(T entity) {
        //准备数据
        Map<String,String> map = getValues(entity);
        ContentValues values = getContentValues(map);
        long result = sqLiteDatabase.insert(tableName,null,values);
        return result;
    }

    private Map<String,String> getValues(T entity){
        HashMap<String,String> map = null;
        Iterator<Field> fieldIterator = cacheMap.values().iterator();
        while (fieldIterator.hasNext()){
            Field field = fieldIterator.next();
            field.setAccessible(true);
            //获取成员变量的值
            try {
                Object object = field.get(entity);
                if(object==null){
                    continue;
                }
                String value = object.toString();
                //获取列名
                String key = null;
                if(field.getAnnotation(DbField.class)!=null){
                    key = field.getAnnotation(DbField.class).value();
                }else{
                    key = field.getName();
                }
                if(!TextUtils.isEmpty(key)&&!TextUtils.isEmpty(value)){
                    map.put(key,value);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }
        return map;
    }

    private ContentValues getContentValues(Map<String,String> map){
        ContentValues contentValues = new ContentValues();
        Set keys = map.keySet();
        Iterator<String> iterator = keys.iterator();
        while(iterator.hasNext()){
            String key = iterator.next();
            String value = map.get(key);
            if(value!= null){
                contentValues.put(key,value);
            }
        }
        return contentValues;
    }

    //创建一个内部的条件类，用来储存条件信息
    private class  Condition{
        private String whereCause;
        private String[] whereArgs;

        public Condition(Map<String,String> whereCause) {

        }
    }
}
