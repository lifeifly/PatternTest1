package com.yele.huht.bluetoothsdklib.policy.downloadlib.server;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.yele.huht.bluetoothsdklib.policy.downloadlib.bean.DownloadFlag;
import com.yele.huht.bluetoothsdklib.policy.downloadlib.bean.FileInfo;
import com.yele.huht.bluetoothsdklib.policy.downloadlib.bean.ThreadInfo;
import com.yele.huht.bluetoothsdklib.policy.downloadlib.db.DownloadDB;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DownloadThread {

    private static final String TAG = "DownloadThread";

//    private final int LOAD_START = 0x1201;
    private final int LOAD_UPDATE = 0x1202;
    private final int LOAD_STOP = 0x1203;
    private final int LOAD_FINISH = 0x1204;
    private final int LOAD_FAIL = 0x1205;

    // 下面则为当前的线程的进度
    public static final int STATE_INIT = 0;         // 下载状态-加载中
    public static final int STATE_LOAD = 1;         // 下载状态-加载中
    public static final int STATE_STOP = 2;         // 下载状态-停止
    public static final int STATE_FAILED = 3;       // 下载状态-失败
    public static final int STATE_FINISH = 4;       // 下载状态-完成

    // 当前文件的下载进度
    public int state = STATE_INIT;

    private Handler loadHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            ThreadInfo sendInfo = (ThreadInfo) msg.obj;
            switch (msg.what) {
                case LOAD_UPDATE:
                    // 处理下载线程进度反馈
                    dealUpdate(sendInfo);
                    break;
                case LOAD_STOP:
                    // 处理下载线程停止下载
                    dealStop(sendInfo);
                    break;
                case LOAD_FINISH:
                    // 处理下载线程完成
                    dealFinish(sendInfo);
                    break;
                case LOAD_FAIL:
                    // 处理下载线程失败
                    dealFailed(sendInfo);
                    break;
            }
        }
    };

    /**
     * 处理下载失败的功能
     *  先更新当前线程中的数据，然后保存到对应的数据库中
     *  然后检测是否所有的线程都已经下载完成或者下载失败了，如果是，则通知前台下载失败，否则就不管
     * @param now 当前线程信息
     */
    private void dealFailed(ThreadInfo now) {
        for (int i = 0; i < listThreadInfo.size(); i++) {
            ThreadInfo info = listThreadInfo.get(i);
            if (info.id == now.id) {
                info.finished = now.finished;
                break;
            }
        }
        downloadDB.updateLoadThread(now);
        boolean allEnd = true;
        for (int i = 0; i < listTask.size(); i++) {
            LoadTask task = listTask.get(i);
            if (task.isRun) {
                allEnd = false;
            }
        }
        if (allEnd) {
//            LogUtils.i(TAG,"线程：" + now.id + "下载失败，其他线程也下载完成了");
            state = STATE_FAILED;
            Message msg = new Message();
            msg.what = DownloadFlag.FAIL_LOAD_FILE_PART;
            msg.obj = mFileInfo;
            mHanlder.sendMessage(msg);
        }else{
//            LogUtils.i(TAG,"线程：" + now.id + "下载失败，但是还有线程在下载");
        }
    }

    /**
     * 处理下载完成
     *  先更新当前线程中的数据，然后保存到对应的数据库中
     *  检测所有线程是否都已经完成了
     * @param now 返回的现在线程的信息
     */
    private void dealFinish(ThreadInfo now) {
        for (int i = 0; i < listThreadInfo.size(); i++) {
            ThreadInfo info = listThreadInfo.get(i);
            if (info.id == now.id) {
                info.finished = now.finished;
                break;
            }
        }
        downloadDB.updateLoadThread(now);
        boolean allEnd = true;
        boolean allFinish = true;
        for (int i = 0; i < listTask.size(); i++) {
            LoadTask task = listTask.get(i);
            if (task.isRun) {
                allEnd = false;
                break;
            }
        }
        for (int i = 0; i < listThreadInfo.size(); i++) {
            ThreadInfo info = listThreadInfo.get(i);
            if (info.finished != info.size) {
                allFinish = false;
                break;
            }

        }
        if (allEnd && allFinish) {
//            LogUtils.i(TAG,"线程：" + now.id + "下载完成，其他线程也下载完成了");
            downloadDB.delAppointLoadThread(mFileInfo.url);
            state = STATE_FINISH;
            Message msg = new Message();
            msg.what = DownloadFlag.GET_LOAD_FINISH;
            msg.obj = mFileInfo;
            mHanlder.sendMessage(msg);
        } else if (allEnd) {
//            LogUtils.i(TAG,"线程：" + now.id + "下载完成，其他线程也下载完成了，但是有线程下载失败了");
            state = STATE_FAILED;
            Message msg = new Message();
            msg.what = DownloadFlag.FAIL_LOAD_FILE_PART;
            msg.obj = mFileInfo;
            mHanlder.sendMessage(msg);
        }else{
//            LogUtils.i(TAG,"线程：" + now.id + "下载完成，其他线程还没有下载，就更新下进度");
        }
    }

    /**
     * 处理下载暂停
     * @param now 当前返回的下载信息
     */
    private void dealStop(ThreadInfo now) {
        for (int i = 0; i < listThreadInfo.size(); i++) {
            ThreadInfo info = listThreadInfo.get(i);
            if (info.id == now.id) {
                info.finished = now.finished;
                break;
            }
        }
        downloadDB.updateLoadThread(now );
        boolean allStop = true;
        for (int i = 0; i < listTask.size(); i++) {
            LoadTask task = listTask.get(i);
            if (task.isRun) {
                allStop = false;
                break;
            }
        }
        if (allStop) {
//            LogUtils.i(TAG,"线程：" + now.id + "下载暂停，其他线程也下载暂停了");
            state = STATE_STOP;
            Message msg = new Message();
            msg.what = DownloadFlag.STOP_LOAD_FILE;
            msg.obj = mFileInfo;
            mHanlder.sendMessage(msg);
        }else{
            int finished = 0;
            for (int i = 0; i < listThreadInfo.size(); i++) {
                finished += listThreadInfo.get(i).finished;
            }
            mFileInfo.finished = finished;
            downloadDB.updateLoadFileInfo(mFileInfo);
            int percent = mFileInfo.getPercent();
//            LogUtils.i(TAG,"线程：" + now.id + ",更新下载进度:" + percent);
            state = STATE_LOAD;
            Message msg = new Message();
            msg.what = DownloadFlag.GET_LOAD_FILE_PART;
            msg.obj = mFileInfo;
            msg.arg1 = percent;
            mHanlder.sendMessage(msg);
//            LogUtils.i(TAG,"线程：" + now.id + "下载暂停，其他线程还没有暂停");
        }
    }

    /**
     * 处理更新信息
     * @param now 当前返回的加载信息
     */
    private void dealUpdate(ThreadInfo now) {
        for (int i = 0; i < listThreadInfo.size(); i++) {
            ThreadInfo info = listThreadInfo.get(i);
            if (info.id == now.id) {
                info.finished = now.finished;
                break;
            }
        }
        downloadDB.updateLoadThread(now);
        int finished = 0;
        for (int i = 0; i < listThreadInfo.size(); i++) {
            finished += listThreadInfo.get(i).finished;
        }
//        LogUtils.i(TAG,"线程：" + now.id + "，更新下载大小" + finished + "/" + mFileInfo.size);
        mFileInfo.finished = finished;
        downloadDB.updateLoadFileInfo(mFileInfo);
        float complete = (float) ((double)finished * 1.0 / (double)mFileInfo.size) ;
        int percent = (int) (complete * 100);
        if (percent == 100 && mFileInfo.finished != mFileInfo.size) {
            percent = 99;
        }
        state = STATE_LOAD;
//        LogUtils.i(TAG,"线程：" + now.id + "，更新下载进度" + complete);
        Message msg = new Message();
        msg.what = DownloadFlag.GET_LOAD_FILE_PART;
        msg.obj = mFileInfo;
        msg.arg1 = percent;
        mHanlder.sendMessage(msg);

    }

    private Handler mHanlder;
    private FileInfo mFileInfo;
    private DownloadDB downloadDB;

    public DownloadThread(Handler handler, FileInfo fileInfo, DownloadDB db) {
        this.mHanlder = handler;
        this.mFileInfo = fileInfo;
        this.downloadDB = db;
    }

    /* 当前最大的下载队列 */
    private final int MAX_NUM = 1;
    /* 当前的下载信息 */
    private List<ThreadInfo> listThreadInfo = new ArrayList<>();
    /* 当前正在下载的线程 */
    private List<LoadTask> listTask = new ArrayList<>();

    /**
     * 开始进行下载目标文件
     * 数据库中已经有下载线程信息，那么就直接拿过来用
     */
    public void startLoad() {
        List<ThreadInfo> list = downloadDB.getThreads(mFileInfo.url);
        if (list == null || list.size() == 0) {
            startInitLoad();
            return;
        }
        state = STATE_LOAD;
        listThreadInfo.clear();
        listThreadInfo.addAll(list);
        startContinueLoad(list);
    }

    /**
     * 暂停当前下载的目标文件
     */
    public void stopLoad() {
        if (listTask == null || listTask.size() == 0) {
            return;
        }
        for (int i = 0; i < listTask.size(); i++) {
            LoadTask task = listTask.get(i);
            task.stopLoad();
        }
    }


    /**
     * 开始下载（如果没有下载过的话，根据设置的同时下载数量，进行）
     */
    private void startInitLoad() {
        long space = mFileInfo.size / MAX_NUM;
        for (int i = 0; i < MAX_NUM; i++) {
            ThreadInfo threadInfo = new ThreadInfo();
            threadInfo.id = i;
            threadInfo.url = mFileInfo.url;
            threadInfo.path = mFileInfo.localPath;

            if (i == MAX_NUM - 1) {
                threadInfo.size = mFileInfo.size - i * space;

                threadInfo.startIndex = i * space;
                threadInfo.endIndex = mFileInfo.size - 1;
            } else {
                threadInfo.size = space;

                threadInfo.startIndex = i * space;
                threadInfo.endIndex = (i + 1) * space - 1;
            }
            listThreadInfo.add(threadInfo);
            downloadDB.updateLoadThread(threadInfo);
        }
        startContinueLoad(listThreadInfo);
    }

    /**
     * 开始根据分配的线程信息，进行下载
     *
     * @param list 分配的线程信息
     */
    private void startContinueLoad(List<ThreadInfo> list) {
        for (int i = 0; i < list.size(); i++) {
            LoadTask task = new LoadTask(list.get(i));
            new Thread(task).start();
            listTask.add(task);
        }
    }

    class LoadTask implements Runnable {

        /* 当前下载任务的线程ID */
        public int id;
        /* 当前线程的下载信息 */
        private ThreadInfo threadInfo;
        /* 当前程序是否暂停 */
        private boolean isPause = false;
        /* 判断当前线程是否正在运行 */
        public boolean isRun = false;

        public LoadTask(ThreadInfo threadInfo) {
            this.id = threadInfo.id;
            this.threadInfo = threadInfo;
        }

        public void stopLoad() {
            this.isPause = true;
        }

        @Override
        public void run() {
            HttpURLConnection connect = null;
            RandomAccessFile raf = null;
            InputStream is = null;
            isRun = true;
            try {
                URL url = new URL(threadInfo.url);
                connect = (HttpURLConnection) url.openConnection();
                connect.setConnectTimeout(5000);
                connect.setRequestMethod("GET");


                long start = threadInfo.startIndex + threadInfo.finished;

                connect.setRequestProperty("Range", "bytes=" + start + "-" + threadInfo.endIndex);

                File file = new File(threadInfo.path);
                raf = new RandomAccessFile(file, "rwd");
                raf.seek(start);

                int code = connect.getResponseCode();

                if (code == HttpURLConnection.HTTP_PARTIAL
                        || code == HttpURLConnection.HTTP_OK) {
                    is = connect.getInputStream();

                    byte[] buffer = new byte[1024 << 2];
                    int len;
                    long time = System.currentTimeMillis();
                    while ((len = is.read(buffer)) != -1) {
                        threadInfo.finished += len;
                        raf.write(buffer, 0, len);
                        if (System.currentTimeMillis() - time > 500
                                || threadInfo.finished == mFileInfo.size) {
                            time = System.currentTimeMillis();
                            Message msg = new Message();
                            msg.what = LOAD_UPDATE;
                            msg.obj = threadInfo;
                            loadHandler.sendMessage(msg);
                        }
                        if (isPause) {
                            break;
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connect != null) {
                    connect.disconnect();
                }
                try {
                    if (raf != null) {
                        raf.close();
                    }
                    if (is != null) {
                        is.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                isRun = false;
                if (threadInfo.finished == threadInfo.size) {
                    Message msg = new Message();
                    msg.what = LOAD_FINISH;
                    msg.obj = threadInfo;
                    loadHandler.sendMessage(msg);
                } else if (isPause) {
                    Message msg = new Message();
                    msg.what = LOAD_STOP;
                    msg.obj = threadInfo;
                    loadHandler.sendMessage(msg);
                } else {
//                    LogUtils.i(TAG,"下载文件大小：" + threadInfo.finished + "/" + threadInfo.size);
                    Message msg = new Message();
                    msg.what = LOAD_FAIL;
                    msg.obj = threadInfo;
                    loadHandler.sendMessage(msg);
                }
            }
        }
    }
}
