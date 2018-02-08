package com.onlyknow.app.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.onlyknow.app.database.bean.OKCardBean;

import java.sql.SQLException;

/**
 * Created by Administrator on 2017/12/11.
 */

public class OKDatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String TABLE_NAME = "card.db";
    private Dao<OKCardBean, Integer> userDao;

    private OKDatabaseHelper(Context context) {
        super(context, TABLE_NAME, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, OKCardBean.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, OKCardBean.class, true);
            onCreate(sqLiteDatabase, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static OKDatabaseHelper instance;

    /**
     * 单例获取该Helper
     *
     * @param context
     * @return
     */
    public static synchronized OKDatabaseHelper getHelper(Context context) {
        if (instance == null) {
            synchronized (OKDatabaseHelper.class) {
                if (instance == null)
                    instance = new OKDatabaseHelper(context);
            }
        }
        return instance;
    }

    /**
     * 获得CardDao
     *
     * @return
     * @throws SQLException
     */
    public Dao<OKCardBean, Integer> getCardDao() throws SQLException {
        if (userDao == null) {
            userDao = getDao(OKCardBean.class);
        }
        return userDao;
    }

    /**
     * 释放资源
     */
    @Override
    public void close() {
        super.close();
        userDao = null;
    }
}
