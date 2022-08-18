package com.yele.downloadlib.server;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.Nullable;

import com.yele.baseapp.utils.FileUtils;
import com.yele.baseapp.utils.LogUtils;
import com.yele.baseapp.utils.MD5Utils;
import com.yele.baseapp.utils.StringUtils;
import com.yele.downloadlib.bean.DownloadFlag;
import com.yele.downloadlib.bean.FileInfo;
import com.yele.downloadlib.policy.db.DownloadDB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DownloadServer extends Service {

    private static final String TAG = "DownloadServer";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (action == null || action.equals("")) {
                return START_STICKY;
            }
            if (action.equals(DownloadFlag.ACTION_START)) {
                // 开始下载任务
                FileInfo mFileInfo = intent.getParcelableExtra("file_info");
                dealStartFileInfo(mFileInfo);
            } else if (action.equals(DownloadFlag.ACTION_STOP)) {
                // 暂停下载
                FileInfo mFileInfo = intent.getParcelableExtra("file_info");
                dealStopFileInfo(mFileInfo);
            } else if (action.equals(DownloadFlag.ACTION_END)) {
                // 取消删除下载
                FileInfo mFileInfo = intent.getParcelableExtra("file_info");
                dealEndFileInfo(mFileInfo);
            }
        }
        return START_STICKY;
    }

    /**
     * 处理取消并删除下载文件的线程
     * @param mFileInfo 当前文件信息
     */
    private void dealEndFileInfo(FileInfo mFileInfo) {
        if (mFileInfo == null) {
            LogUtils.i(TAG,"取消文件失败，取消对象为空");
            return;
        }
        if (mapLoad.containsKey(mFileInfo.url)) {
            DownloadThread thread = mapLoad.get(mFileInfo.url);
            if (thread != null) {
                if (thread.state == DownloadThread.STATE_LOAD) {
                    LogUtils.i(TAG, "先暂停文件下载再删除对应信息：" + mFileInfo.url);
                    listDel.add(mFileInfo.url);
                    thread.stopLoad();
                    return;
                }else{
                    mapLoad.remove(mFileInfo.url);
                }
            }
        }
        downloadDB.delAppointLoadThread(mFileInfo.url);
        downloadDB.delFileInfo(mFileInfo);

        LogUtils.i(TAG, "取消文件成功：" + mFileInfo.url);
        FileUtils.delFile(mFileInfo.localPath);
        Intent intent = new Intent();
        intent.setAction(DownloadFlag.RESULT_LOAD_DELETE);
        intent.putExtra("file_info", mFileInfo);
        sendBroadcast(intent);
    }

    /**
     * 暂停下载
     * @param mFileInfo 目标文件
     */
    private void dealStopFileInfo(FileInfo mFileInfo) {
        if (mFileInfo == null) {
            LogUtils.i(TAG,"取消文件失败，取消对象为空");
            return;
        }
        if (mapLoad.containsKey(mFileInfo.url)) {
            DownloadThread thread = mapLoad.get(mFileInfo.url);
            if (thread != null && thread.state == DownloadThread.STATE_LOAD) {
                LogUtils.i(TAG, "暂停文件下载：" + mFileInfo.url);
                listStop.add(mFileInfo.url);
                thread.stopLoad();
                return;
            }
        }
        Intent intent = new Intent();
        intent.setAction(DownloadFlag.RESULT_LOAD_STOP);
        intent.putExtra("file_info", mFileInfo);
        sendBroadcast(intent);
    }

    /**
     * 处理需要下载的文件
     *
     * @param fileInfo 文件内容
     */
    private void dealStartFileInfo(FileInfo fileInfo) {
        if (fileInfo == null) {
            return;
        }
        FileInfo localInfo = downloadDB.getLoadFileInfo(fileInfo.url);
        if (localInfo == null) {
            // 本地没有下载数据，那么就开始下载
            DownloadInit downloadInit = new DownloadInit(mHandler, fileInfo);
            new Thread(downloadInit).start();
            return;
        }
        if (localInfo.finished == localInfo.size) {
            LogUtils.i(TAG,"文件已经下载过了");
            if (fileInfo.ignoreLoad) {
                delFileRestLoad(fileInfo, localInfo);
                return;
            }
            // 已经下载完成了
            if (FileUtils.isFileExists(localInfo.localPath)) {
                if (!StringUtils.isEmpty(localInfo.md5)) {
                    LogUtils.i(TAG,"开始校验文件");
                    String localMd5 = MD5Utils.md5File(localInfo.localPath);
                    if (localMd5.equals(localInfo.md5)) {
                        LogUtils.i(TAG,"开始校验文件成功");
                        // 如果两个MD5一致，说明文件没有修改过，那么就告知前端下载过了
                        Intent intent = new Intent();
                        intent.setAction(DownloadFlag.RESULT_FINISH);
                        intent.putExtra("file_info", localInfo);
                        sendBroadcast(intent);
                    } else {
                        LogUtils.i(TAG,"校验文件失败：" + localMd5);
                        // 删除之前下载的内容，然后重新下载
                        delFileRestLoad(fileInfo, localInfo);
                    }
                } else {
                    LogUtils.i(TAG,"文件没有MD5，无需校验");
                    // 当前没有MD5，所以就通知前台下载完成了
                    Intent intent = new Intent();
                    intent.setAction(DownloadFlag.RESULT_FINISH);
                    intent.putExtra("file_info", localInfo);
                    sendBroadcast(intent);
                }
            } else {
                // 本地文件不存在，则重新进行下载，移除对应的内容
                downloadDB.delAppointLoadThread(localInfo.url);
                downloadDB.delFileInfo(localInfo);

                DownloadInit downloadInit = new DownloadInit(mHandler, fileInfo);
                new Thread(downloadInit).start();
            }
        } else {

            fileInfo = localInfo;
            Intent intent = new Intent();
            intent.setAction(DownloadFlag.RESULT_LOAD_UPDATE);
            intent.putExtra("file_info", fileInfo);
            sendBroadcast(intent);

            startFileLoad(fileInfo);
        }
    }

    /**
     * 删除之前的文件并重新下载
     *
     * @param fileInfo  需要下载的文件信息
     * @param localInfo 本地的文件信息
     */
    private void  delFileRestLoad(FileInfo fileInfo, FileInfo localInfo) {
        // 删除之前下载的内容，然后重新下载
        FileUtils.delFile(localInfo.localPath);
        downloadDB.delAppointLoadThread(localInfo.url);
        downloadDB.delFileInfo(localInfo);

        // 避免当前已经有了内容
        mapLoad.remove(fileInfo.url);

        DownloadInit downloadInit = new DownloadInit(mHandler, fileInfo);
        new Thread(downloadInit).start();
    }

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Intent intent = new Intent();
            FileInfo fileInfo = (FileInfo) msg.obj;
            switch (msg.what) {
                case DownloadFlag.GET_LOAD_SIZE:
                    LogUtils.i(TAG, "加载文件大小成功");
                    // 通知前端开始加载界面
                    intent.setAction(DownloadFlag.RESULT_INIT_SUCCESS);
                    intent.putExtra("file_info", fileInfo);
                    intent.putExtra("progress", 0);
                    sendBroadcast(intent);
                    // 开始进行加载
                    startFileLoad(fileInfo);
                    break;
                case DownloadFlag.FAIL_LOAD_SIZE:
                    // 通知前端加载-初始化失败
                    intent.setAction(DownloadFlag.RESULT_INIT_FAIL);
                    intent.putExtra("file_info", fileInfo);
                    sendBroadcast(intent);
                    break;
                case DownloadFlag.GET_LOAD_FILE_PART:
                    // 通知前端加载-进度更新
                    intent.setAction(DownloadFlag.RESULT_LOAD_UPDATE);
                    intent.putExtra("file_info", fileInfo);
                    int progress = msg.arg1;
                    intent.putExtra("progress", progress);
                    sendBroadcast(intent);
                    downloadDB.updateLoadFileInfo(fileInfo);
                    break;
                case DownloadFlag.FAIL_LOAD_FILE_PART:
                    // 通知前端加载文件-失败
                    intent.setAction(DownloadFlag.RESULT_LOAD_FAIL);
                    intent.putExtra("file_info", fileInfo);
                    sendBroadcast(intent);
                    downloadDB.updateLoadFileInfo(fileInfo);
                    break;
                case DownloadFlag.STOP_LOAD_FILE:
                    // 通知前端文件加载-停止了
                    downloadDB.updateLoadFileInfo(fileInfo);
                    upStopFile(fileInfo);
                    break;
                case DownloadFlag.GET_LOAD_FINISH:
                    // 通知前端文件加载-完成
                    downloadDB.delAppointLoadThread(fileInfo.url);
                    mapLoad.remove(fileInfo.url);
                    intent.setAction(DownloadFlag.RESULT_FINISH);
                    intent.putExtra("file_info", fileInfo);
                    sendBroadcast(intent);
                    break;
            }

        }
    };

    /**
     * 处理具体的停止下载文件的
     */
    private void upStopFile(FileInfo fileInfo) {
        int index = -1;
        for (int i = 0; i < listStop.size(); i++) {
            String url = listStop.get(i);
            if (!StringUtils.isEmpty(url) && fileInfo.url.equals(url)) {
                // 停止下载文件的处理
                index = i;
                mapLoad.remove(url);
                break;
            }
        }
        if(index != -1){
            Intent intent = new Intent();
            intent.setAction(DownloadFlag.RESULT_LOAD_STOP);
            intent.putExtra("file_info", fileInfo);
            sendBroadcast(intent);
            return;
        }
        for (int i = 0; i < listDel.size(); i++) {
            String url = listDel.get(i);
            if (!StringUtils.isEmpty(url) && fileInfo.url.equals(url)) {
                // 停止下载文件的处理
                index = i;
                mapLoad.remove(url);
                break;
            }
        }
        if (index == -1) {
            return;
        }
        Intent intent = new Intent();
        intent.setAction(DownloadFlag.RESULT_LOAD_DELETE);
        intent.putExtra("file_info", fileInfo);
        sendBroadcast(intent);
        FileUtils.delFile(fileInfo.localPath);
        downloadDB.delAppointLoadThread(fileInfo.url);
        downloadDB.delFileInfo(fileInfo);
    }

    /* 正在下载的下载进程 */
    private Map<String, DownloadThread> mapLoad = new HashMap<>();
    /* 准备暂停下载的进程 */
    private List<String> listStop = new ArrayList<>();
    /* 准备取消下载的进程 */
    private List<String> listDel = new ArrayList<>();

    /**
     * 开始下载文件
     * @param fileInfo 文件信息
     */
    private void startFileLoad(FileInfo fileInfo) {
        if (fileInfo == null) {
            return;
        }
        if (!mapLoad.containsKey(fileInfo.url)) {
            DownloadThread thread = new DownloadThread(mHandler, fileInfo, downloadDB);
            thread.startLoad();
            mapLoad.put(fileInfo.url, thread);
        }else{
            DownloadThread thread = mapLoad.get(fileInfo.url);
            if (thread != null && thread.state != DownloadThread.STATE_LOAD) {
                thread.startLoad();
            }
        }
    }

    private DownloadDB downloadDB;

    @Override
    public void onCreate() {
        super.onCreate();
        downloadDB = DownloadDB.getInstance(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
