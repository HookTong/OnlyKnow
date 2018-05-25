package com.onlyknow.app.utils;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.onlyknow.app.api.OKServiceResult;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public class OKGsonUtil {


    public static <T> OKServiceResult<T> fromServiceResultJsonByObject(String json, Class<T> clazz) {

        if (TextUtils.isEmpty(json)) return null;

        Type type = new AppType(OKServiceResult.class, new Class[]{clazz});

        try {

            return new Gson().fromJson(json, type);

        } catch (Exception e) {

            e.printStackTrace();

            return null;

        }

    }

    public static <T> OKServiceResult<List<T>> fromServiceResultJsonByList(String json, Class<T> clazz) {

        if (TextUtils.isEmpty(json)) return null;

        Type listType = new AppType(List.class, new Class[]{clazz});

        Type type = new AppType(OKServiceResult.class, new Type[]{listType});

        OKServiceResult<List<T>> serviceResult = null;

        try {

            serviceResult = new Gson().fromJson(json, type);

            return serviceResult;

        } catch (Exception e) {

            return null;

        }
    }

    public static <T> T fromJson(String json, Class<T> clazz) {

        if (TextUtils.isEmpty(json)) return null;

        try {

            return new Gson().fromJson(json, clazz);

        } catch (Exception e) {

            return null;

        }
    }

    private static class AppType implements ParameterizedType {
        private final Class raw;
        private final Type[] args;

        AppType(Class raw, Type[] args) {
            this.raw = raw;
            this.args = args != null ? args : new Type[0];
        }

        @Override
        public Type[] getActualTypeArguments() {
            return args;
        }

        @Override
        public Type getRawType() {
            return raw;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }
    }
}
