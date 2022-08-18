package com.yele.hu.upgradetools.policy.http;


import android.util.Log;

import com.yele.baseapp.policy.http.MyHttp;
import com.yele.baseapp.policy.http.listener.MyNormalHttpHandler;
import com.yele.baseapp.policy.http.request.RequestParams;
import com.yele.hu.upgradetools.policy.http.back.BackLoginState;
import com.yele.hu.upgradetools.policy.http.back.BackPwd;

import org.json.JSONException;
import org.json.JSONObject;

public class HttpManager {

    private static final String TAG = "HttpManager";

//    public static final String HTTP_IP = "192.168.1.19";
    public static final String HTTP_IP = "47.99.49.215";
//    public static final String HTTP_IP = "367z9u9396.wicp.vip:20960";
//    public static final String HTTP_IP = "367z9u9396.wicp.vip:29588";

    private static final String ROOT = "http://" + HTTP_IP + ":10081/";
    private static final String GET_LOGIN_STATE = ROOT + "login";

    /**
     * 请求登陆状态
     * @param back 对应的返回接口
     */
    public static void requestLoginState(String numberId, String pwd, String imei,final BackLoginState back) {
        RequestParams params = new RequestParams();
        params.put("username",numberId);
        params.put("password",pwd);
        params.put("imei",imei);
        MyHttp.post(GET_LOGIN_STATE, params, new MyNormalHttpHandler() {
            @Override
            public void onSuccess(Object responseObj) {
                if (back == null) {
                    return;
                }
                if (responseObj == null) {
                    back.loginFailed(0,"Get filed");
                    return;
                }
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject((String) responseObj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (jsonObject == null) {
                    back.loginFailed(1,"request filed");
                    return;
                }
                int code = 0;
                String msg = null;
                try {
                    code = jsonObject.getInt("code");
                    msg = jsonObject.getString("message");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (code != 2) {
                    back.loginFailed(code, msg);
                    return;
                }else{
                    back.loginSuccess(null);
                }

            }

            @Override
            public void onFiled(Object responseObj) {
                if (back == null) {
                    return;
                }
                back.loginFailed(4,responseObj.toString());
            }
        });
    }


    public static void requestLoginPwd(String imei, final BackPwd backPwd) {
        RequestParams params = new RequestParams();
        params.put("imeiId","PONY202003260000-" + imei);
        params.put("pwd", "F5neRLomc419rWUz");
        MyHttp.get("http://47.99.49.215:9091/getModeTest",params,new MyNormalHttpHandler(){

            @Override
            public void onSuccess(Object responseObj) {
                if (backPwd == null) {
                    return;
                }
                if (responseObj == null) {
                    backPwd.backFailed("Request Err");
                    return;
                }
                JSONObject jsonObject = null;
                String str = responseObj.toString();
                try {
                    str = str.replace("'pwd':","'pwd':\"").replace("}","\"}");
                    Log.i(TAG, "onSuccess: " + str);
                    jsonObject = new JSONObject(str);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (jsonObject == null) {
                    backPwd.backFailed(responseObj.toString());
                    return;
                }
                int code = -1;
                String pwd = null;
                try {
                    pwd = jsonObject.getString("pwd");
                    code = jsonObject.getInt("code");
                    pwd = str.substring(str.length() - 34,str.length() - 2);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (code == 0) {
                    backPwd.backSuccess(pwd);
                }else{
                    backPwd.backFailed("请求失败");
                }
            }

            @Override
            public void onFiled(Object responseObj) {
                if (backPwd != null) {
                    backPwd.backFailed("Request Failed");
                }
            }
        });
        
    }

    public static void request11() {
        RequestParams params = new RequestParams();
        params.put("imei", "123531515");
        MyHttp.post("http://192.168.2.245:12101/api/init_device", params, new MyNormalHttpHandler() {
            @Override
            public void onSuccess(Object responseObj) {
                if (responseObj == null) {
                    return;
                }
                Log.i(TAG, "onSuccess: " + responseObj.toString());
            }

            @Override
            public void onFiled(Object responseObj) {
                Log.i(TAG, "onFiled: "  + responseObj.toString());
            }
        });
    }
}
