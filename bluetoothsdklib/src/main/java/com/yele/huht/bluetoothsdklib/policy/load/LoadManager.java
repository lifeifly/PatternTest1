package com.yele.huht.bluetoothsdklib.policy.load;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.yele.baseapp.utils.StringUtils;
import com.yele.huht.bluetoothsdklib.callBcak.OnUpdateListener;
import com.yele.huht.bluetoothsdklib.policy.downloadlib.bean.DownloadFlag;
import com.yele.huht.bluetoothsdklib.policy.downloadlib.bean.FileInfo;
import com.yele.huht.bluetoothsdklib.policy.downloadlib.server.DownloadServer;
import com.yele.huht.bluetoothsdklib.policy.load.event.OnLoadEvent;


import java.util.HashMap;
import java.util.Map;

public class LoadManager {

    private static LoadManager loadManager;

    public static LoadManager getInstance(Context context) {
        if (loadManager == null) {
            synchronized (LoadManager.class) {
                if (loadManager == null) {
                    loadManager = new LoadManager(context);
                }
            }
        }
        return loadManager;
    }

    private Context mContext;

    private LoadManager(Context context) {
        this.mContext = context;
        registerLoad();
    }

    private Map<String, FileInfo> mapLoad = new HashMap<>();

    private OnUpdateListener listener;

    /**
     * 开始下载
     * @param fileInfo
     * @param result
     */
    public void startLoad(FileInfo fileInfo, OnUpdateListener result) {
        if (fileInfo == null) {
            return;
        }
        FileInfo local = mapLoad.get(fileInfo.url);
        if (local == null) {
            mapLoad.put(fileInfo.url, fileInfo);
        }
        Intent intent = new Intent(mContext, DownloadServer.class);
        intent.setAction(DownloadFlag.ACTION_START);
        intent.putExtra("file_info", fileInfo);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            //android8.0以上通过startForegroundService启动service
//            mContext.startForegroundService(intent);
//        } else {
//            mContext.startService(intent);
//        }

        mContext.startService(intent);
        this.listener = result;
    }

    /**
     * 停止下载
     * @param fileInfo
     */
    public void stopLoad(FileInfo fileInfo) {
        if (fileInfo == null) {
            return;
        }
        FileInfo local = mapLoad.get(fileInfo.url);
        if (local == null) {
            mapLoad.put(fileInfo.url, fileInfo);
        }
        Intent intent = new Intent(mContext, DownloadServer.class);
        intent.setAction(DownloadFlag.ACTION_STOP);
        intent.putExtra("file_info", fileInfo);
        mContext.startService(intent);
    }

    /**
     * 删除下载
     * @param fileInfo
     */
    public void delLoad(FileInfo fileInfo) {
        if (fileInfo == null) {
            return;
        }
        FileInfo local = mapLoad.get(fileInfo.url);
        if (local == null) {
            mapLoad.put(fileInfo.url, fileInfo);
        }
        Intent intent = new Intent(mContext, DownloadServer.class);
        intent.setAction(DownloadFlag.ACTION_END);
        intent.putExtra("file_info", fileInfo);
        mContext.startService(intent);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) {
                return;
            }
            String action = intent.getAction();
            if (StringUtils.isEmpty(action)) {
                return;
            }
            FileInfo fileInfo = null;
            OnLoadEvent event = null;
            switch (action) {
                case DownloadFlag.RESULT_INIT_SUCCESS:
                    fileInfo = intent.getParcelableExtra("file_info");
                    if (fileInfo == null) {
                        return;
                    }
                    if (mapLoad.containsKey(fileInfo.url)) {
                        FileInfo local = mapLoad.get(fileInfo.url);
                        if (local != null) {
                            mapLoad.put(local.url, fileInfo);
                            event = new OnLoadEvent(OnLoadEvent.LOAD_STATE, fileInfo);
                            listener.updateListener(OnLoadEvent.LOAD_STATE);
                        }
                    }
                    break;
                case DownloadFlag.RESULT_INIT_FAIL:
                    fileInfo = intent.getParcelableExtra("file_info");
                    if (fileInfo == null) {
                        return;
                    }
                    if (mapLoad.containsKey(fileInfo.url)) {
                        FileInfo local = mapLoad.get(fileInfo.url);
                        if (local != null) {
                            mapLoad.put(local.url, fileInfo);
                            event = new OnLoadEvent(OnLoadEvent.LOAD_INIT_FAILED, fileInfo);
                            listener.updateListener(OnLoadEvent.LOAD_INIT_FAILED);
                        }
                    }
                    break;
                case DownloadFlag.RESULT_LOAD_UPDATE:
                    fileInfo = intent.getParcelableExtra("file_info");
                    if (fileInfo == null) {
                        return;
                    }
                    if (mapLoad.containsKey(fileInfo.url)) {
                        FileInfo local = mapLoad.get(fileInfo.url);
                        if (local != null) {
                            mapLoad.put(local.url, fileInfo);
                            event = new OnLoadEvent(OnLoadEvent.LOAD_UPDATE, fileInfo);
                            listener.updateListener(OnLoadEvent.LOAD_UPDATE);
                        }
                    }
                    break;
                case DownloadFlag.RESULT_LOAD_STOP:
                    fileInfo = intent.getParcelableExtra("file_info");
                    if (fileInfo == null) {
                        return;
                    }
                    if (mapLoad.containsKey(fileInfo.url)) {
                        FileInfo local = mapLoad.get(fileInfo.url);
                        if (local != null) {
                            mapLoad.put(local.url, fileInfo);
                            event = new OnLoadEvent(OnLoadEvent.LOAD_STOP, fileInfo);
                            listener.updateListener(OnLoadEvent.LOAD_STOP);
                        }
                    }
                    break;
                case DownloadFlag.RESULT_LOAD_DELETE:
                    fileInfo = intent.getParcelableExtra("file_info");
                    if (fileInfo == null) {
                        return;
                    }
                    if (mapLoad.containsKey(fileInfo.url)) {
                        FileInfo local = mapLoad.get(fileInfo.url);
                        if (local != null) {
                            mapLoad.remove(local.url);
                            event = new OnLoadEvent(OnLoadEvent.LOAD_DELETE, fileInfo);
                            listener.updateListener(OnLoadEvent.LOAD_DELETE);
                        }
                    }else{
                        event = new OnLoadEvent(OnLoadEvent.LOAD_DELETE, fileInfo);
                        listener.updateListener(OnLoadEvent.LOAD_DELETE);
                    }
                    break;
                case DownloadFlag.RESULT_LOAD_FAIL:
                    fileInfo = intent.getParcelableExtra("file_info");
                    if (fileInfo == null) {
                        return;
                    }
                    if (mapLoad.containsKey(fileInfo.url)) {
                        FileInfo local = mapLoad.get(fileInfo.url);
                        if (local != null) {
                            mapLoad.put(local.url, fileInfo);
                            event = new OnLoadEvent(OnLoadEvent.LOAD_FAILED, fileInfo);
                            listener.updateListener(OnLoadEvent.LOAD_FAILED);
                        }
                    }
                    break;
                case DownloadFlag.RESULT_FINISH:
                    fileInfo = intent.getParcelableExtra("file_info");
                    if (fileInfo == null) {
                        return;
                    }
                    if (mapLoad.containsKey(fileInfo.url)) {
                        FileInfo local = mapLoad.get(fileInfo.url);
                        if (local != null) {
                            mapLoad.remove(local.url);
                            event = new OnLoadEvent(OnLoadEvent.LOAD_FINISH, fileInfo);
                            listener.updateListener(OnLoadEvent.LOAD_FINISH);
                        }
                    }
                    break;
            }
        }
    };

    private boolean isRegister = false;

    /**
     * 注册下载广播
     */
    private void registerLoad() {
        if (!isRegister) {
            isRegister = true;
            IntentFilter filter = new IntentFilter();
            filter.addAction(DownloadFlag.RESULT_INIT_SUCCESS);
            filter.addAction(DownloadFlag.RESULT_INIT_FAIL);
            filter.addAction(DownloadFlag.RESULT_LOAD_UPDATE);
            filter.addAction(DownloadFlag.RESULT_LOAD_STOP);
            filter.addAction(DownloadFlag.RESULT_LOAD_DELETE);
            filter.addAction(DownloadFlag.RESULT_LOAD_FAIL);
            filter.addAction(DownloadFlag.RESULT_FINISH);
            mContext.registerReceiver(receiver, filter);
        }
    }

    /**
     * 注销下载广播
     */
    private void unRegisterLoad() {
        if (isRegister) {
            isRegister = false;
            mContext.unregisterReceiver(receiver);
        }
    }

    public void destory() {
        unRegisterLoad();
    }

}
