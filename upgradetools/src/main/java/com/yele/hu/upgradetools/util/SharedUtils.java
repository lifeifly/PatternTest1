package com.yele.hu.upgradetools.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.yele.baseapp.utils.StringUtils;
import com.yele.hu.upgradetools.bean.LoginInfo;

public class SharedUtils {

    private static final String FILE_NAME = "userInfo";

    /**
     * 保存登录信息到缓存信息中
     *
     * @param context 上下文
     * @param name    客户码
     * @param pwd   PDAID
     */
    public static void saveLoginInfo(Context context, String name, String pwd) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("name", name);
        editor.putString("pwd", pwd);
        editor.apply();
    }

    /**
     * 读取缓存信息中的登录信息
     * @param context 上下文
     * @return LoginInfo类  登录信息
     */
    public static LoginInfo readLoginInfo(Context context) {
        LoginInfo loginInfo = null;
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        String name = sp.getString("name", "");
        String pwd = sp.getString("pwd", "");
        if (!StringUtils.isEmpty(name)) {
            loginInfo = new LoginInfo();
            loginInfo.name = name;
            loginInfo.pwd = pwd;
        }
        return loginInfo;
    }



}
