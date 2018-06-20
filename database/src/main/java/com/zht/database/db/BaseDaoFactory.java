package com.zht.database.db;

import android.database.sqlite.SQLiteDatabase;

//整体采用单例的构造模式
//同时作为工厂生产相应的basedao对象
public class BaseDaoFactory {
    //私有的构造方法
    private SQLiteDatabase sqLiteDatabase;
    private String sqLiteDatabasePath;

    public BaseDaoFactory() {
        sqLiteDatabasePath = "data/data/com.zht.database/jett.db";
        sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(sqLiteDatabasePath, null);
    }

    private static final BaseDaoFactory INSTANCE = new BaseDaoFactory();

    public static BaseDaoFactory getInstance() {
        return INSTANCE;
    }

    public <T> BaseDao<T> getBaseDao(Class<T> entityClass) {
        BaseDao baseDao = null;
        try {
            baseDao = BaseDao.class.newInstance();
            baseDao.init(sqLiteDatabase, entityClass);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return baseDao;
    }
}
