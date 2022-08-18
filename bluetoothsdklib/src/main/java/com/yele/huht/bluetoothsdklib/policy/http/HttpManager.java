package com.yele.huht.bluetoothsdklib.policy.http;

import com.yele.baseapp.policy.http.MyHttp;
import com.yele.baseapp.policy.http.listener.MyNormalHttpHandler;
import com.yele.baseapp.policy.http.request.RequestParams;
import com.yele.huht.bluetoothsdklib.policy.http.back.OnApkUpdateBack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HttpManager {

    private static final String TAG = "HttpManager";

    private static final String HTTP_IP = "47.103.29.209";        // IP地址

    private static final String HTTP_PORT = "16580";                    // 端口号

    private static final String ROOT = "http://" + HTTP_IP + ":"+ HTTP_PORT + "/";

    /*版本更新接口*/
    private static String getUpdateUrl() {
        return ROOT + "ble/update";
    }

    /**
     * 请求版本更新
     * @param bleVersion 蓝牙版本号字段
     * @param controlVersion 控制器版本号字段
     * @param back 返回
     */
    public static void requestUpdateInfo(final String bleVersion, String controlVersion, final OnApkUpdateBack back) {

        RequestParams params = new RequestParams();
        params.put("ble_version_code", bleVersion);
        params.put("control_version_code", controlVersion);

        MyHttp.get(getUpdateUrl(), params, new MyNormalHttpHandler() {
            @Override
            public void onSuccess(Object responseObj) {
                if (back == null) {
                    return;
                }
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(responseObj.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (jsonObject == null) {
                    back.backFailed(0x01, "参数解析异常");
                    return;
                }
                int code = -1;
                String msg = null;
                try {
                    code = jsonObject.getInt("code");
                    msg = jsonObject.getString("message");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (code == -1) {
                    back.backFailed(0x02,"参数解析异常2");
                    return;
                } else if (code != 0) {
                    back.backFailed(code, msg);
                    return;
                }
                JSONArray array = null;
                try {
                    array = jsonObject.getJSONArray("data");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (array == null) {
                    back.backSuccess();
                    return;
                }

                try {
                    for (int i=0;i<array.length();i++) {
                        JSONObject object = array.getJSONObject(i);
                        int sign = object.getInt("sign");
                        int vCode = object.getInt("newVersion");
                        String vName = object.getString("versionName");
                        String content = object.getString("updateContent");
                        String url = object.getString("url");
                        long size = object.getLong("size");
                        back.backSuccess(sign, vCode, vName, url, content, size);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    back.backFailed(0x04,"参数解析异常4");
                }
            }

            @Override
            public void onFiled(Object responseObj) {
                if (back != null) {
                    back.backFailed(0x00,"网络连接异常");
                }
            }
        });
    }

}
