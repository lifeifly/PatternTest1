package com.yele.downloadlib.server;

import android.os.Handler;
import android.os.Message;

import com.yele.baseapp.utils.FileUtils;
import com.yele.downloadlib.bean.DownloadFlag;
import com.yele.downloadlib.bean.FileInfo;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadInit implements Runnable {

    private Handler mHandler;
    private FileInfo mFileInfo;

    public DownloadInit(Handler mHandler, FileInfo fileInfo) {
        this.mHandler = mHandler;
        this.mFileInfo = fileInfo;
    }

    @Override
    public void run() {
        HttpURLConnection connect = null;
        RandomAccessFile raf = null;

        try {
            URL url = new URL(mFileInfo.url);
            connect = (HttpURLConnection) url.openConnection();
            connect.setConnectTimeout(5000);
            connect.setRequestMethod("HEAD");
            int length = -1;
            if(connect.getResponseCode() == HttpURLConnection.HTTP_OK
                    || connect.getResponseCode() == HttpURLConnection.HTTP_PARTIAL){
                //获得文件长度
                length = connect.getContentLength();
            }
            if (length <= 0) {
                Message msg = new Message();
                msg.what = DownloadFlag.FAIL_LOAD_SIZE;
                msg.obj = mFileInfo;
                mHandler.sendMessage(msg);
                return ;
            }
            if (mFileInfo.size <= 0) {
                mFileInfo.size = length;
            }

            String fileName = connect.getHeaderField("Content-Disposition");
            dealConnectFileName(fileName);

            // todo 处理下载文件夹以及路径问题
            FileUtils.createFile(mFileInfo.localPath);

            raf = new RandomAccessFile(new File(mFileInfo.localPath), "rwd");
            raf.setLength(length);

            mFileInfo.size = length;
            Message msg = new Message();
            msg.what = DownloadFlag.GET_LOAD_SIZE;
            msg.obj = mFileInfo;
            mHandler.sendMessage(msg);
        }catch (IOException e) {
            e.printStackTrace();
            Message msg = new Message();
            msg.what = DownloadFlag.FAIL_LOAD_SIZE;
            msg.obj = mFileInfo;
            mHandler.sendMessage(msg);
        } finally {
            if (raf != null) {
                try {
                    raf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (connect != null) {
                connect.disconnect();
            }
        }
    }

    /**
     * 处理连接文件名称
     * @param fileName 文件名称
     */
    private void dealConnectFileName(String fileName) {
        if (mFileInfo.fileName != null
                && !"".equals(mFileInfo.fileName)) {
            return;
        }
        if (fileName != null && !"".equals(fileName)) {
            String[] buff = fileName.split("[;]");
            if (buff.length > 1) {
                String[] buff2 = buff[1].split("[=]");
                if (buff2.length > 1) {
                    mFileInfo.fileName = buff2[1];
                }
            }
        } else {
            if (mFileInfo.url != null && !"".equals(mFileInfo.url)) {
                String[] buff = mFileInfo.url.split("[/]");
                if (buff.length > 1) {
                    mFileInfo.fileName = buff[buff.length - 1];
                }
            }
        }
    }
}
