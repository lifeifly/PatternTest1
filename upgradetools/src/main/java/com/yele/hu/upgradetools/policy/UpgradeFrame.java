package com.yele.hu.upgradetools.policy;


import com.yele.baseapp.utils.ByteUtils;
import com.yele.baseapp.utils.LogUtils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class UpgradeFrame {

    private static final String TAG = "UpgradeFrame";

    private boolean DEBUG_LOG = true;

    private void logi(String msg) {
        if (!DEBUG_LOG) {
            return;
        }
        LogUtils.i(TAG, msg);
    }

    public static final int TYPE_CONTROL = 13;

    public static final int MAX_DATA_LEN = 128;

    private int type;

    private String path;

    public UpgradeFrame(int type, String path) {
        this.type = type;
        this.path = path;
    }

    private final int STEP_NONE = 0;    // 当前未开始升级
    private final int STEP_INIT = 1;    // 发送初始包
    private final int STEP_DATA = 2;    // 发送包数据中
    private final int STEP_WAIT = 3;    // 升级待完成中
    /**
     * 当前升级的步骤
     */
    private int step;


    private int packNum;            // 当前包的数量
    public int packIndex = 0;      // 当前发送的包得坐标（第几个包）
    private long length = 0;        // 升级包的数据大小

    /**
     * 当前包的数量
     * @return
     */
    public int getPackNum(){
        int packTotal = 0;
        try {
            randomAccessFile.seek(0);
            byte[] head = new byte[128];
            int num = randomAccessFile.read(head);
            packTotal = ByteUtils.changeUnit16Int(head[5]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return packTotal;
    }

    // 是否正在升级
    private boolean startUpgrade = false;

    /**
     * 是否正在升级
     * @return 是否正在升级
     */
    public boolean isStartUpgrade() {
        return startUpgrade;
    }

    private RandomAccessFile randomAccessFile;
    /**
     * 开始升级
     */
    public void start() {
        if (listener == null) {
            return;
        }
        if (path == null) {
            listener.failed("The file path is empty");
            return;
        }
        File file = new File(path);
        File parent = file.getParentFile();
        if (parent == null || !file.getParentFile().exists()) {
            listener.failed("The folder path does not exist");
            return;
        }
        if (!file.exists()) {
            listener.failed("The upgrade file does not exist");
            return;
        }

        boolean fileInit = false;
        try {
            fileInit = initFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!fileInit) {
            listener.failed("Failed to initialize the file");
        }else{
            startUpgrade = true;
        }
    }

    private boolean initFile() throws IOException {
        randomAccessFile = new RandomAccessFile(new File(path), "rw");
        randomAccessFile.seek(0);
        length = randomAccessFile.length();
        byte[] head = new byte[6];
        int num = randomAccessFile.read(head);
        if (num != 6) {
            randomAccessFile.close();
            randomAccessFile = null;
            return false;
        }
        packIndex = 0;
        packNum = ByteUtils.changeUnit16Int(head[5]) + 1;
        return true;
    }

    private long getContentIndex() {
        if (packIndex <= 0) {
            return 0;
        }
        return (packIndex - 1) * MAX_DATA_LEN + MAX_DATA_LEN;
    }

    public byte[] getStartData() throws IOException {
        packIndex = 0;
        return getNowData();
    }

    public byte[] getNowData() throws IOException {
        long index = getContentIndex();
        if (index >= length) {
            return packIndex(new byte[MAX_DATA_LEN]);
        }
        int len = MAX_DATA_LEN;
        if (index + len >= length) {
            len = (int) (length - index);
        }
        if (len <= 0) {
            len = 0;
        }
        byte[] buff = new byte[len];
        if (randomAccessFile == null) {
            return null;
        }
        int num = -1;
        randomAccessFile.seek(index);//将文件流的位置移动到pos字节处
        num = randomAccessFile.read(buff);
        if (num == -1) {
            return null;
        }
        logi("当前发送包" + packIndex);
        if (buff.length < MAX_DATA_LEN) {
            byte[] data = new byte[MAX_DATA_LEN];
            System.arraycopy(buff, 0, data, 0, buff.length);
            return packIndex(data);
        } else {
            return packIndex(buff);
        }
    }

    public int getPercent() {
        if (packIndex == 0 || packNum == 0) {
            return 0;
        }
        int all = packNum * 1024 / MAX_DATA_LEN;
        return packIndex * 100 / all;
    }

    /**
     * 将指定的数据打包
     *
     * @param buff 当前指定的数据
     * @return 返回打包好的数据
     */
    private byte[] packIndex(byte[] buff) {
        byte[] data = new byte[MAX_DATA_LEN + 13];
        String head = "AT+OKUDA";
        byte[] headBuff = head.getBytes();
        System.arraycopy(headBuff, 0, data, 0, 8);
        data[8] = ByteUtils.intToUnitByte(packIndex);
        System.arraycopy(buff, 0, data, 9, MAX_DATA_LEN);
        int crc = 0;
        for (int i = 0; i < MAX_DATA_LEN + 9; i++) {
            crc = crc8Check(crc, data[i]);
        }
        data[MAX_DATA_LEN + 9] = ByteUtils.intToUnitByte(crc);
        data[MAX_DATA_LEN + 10] = 0x24;
        data[MAX_DATA_LEN + 11] = 0x0d;
        data[MAX_DATA_LEN + 12] = 0x0a;
        LogUtils.i("ssss", ByteUtils.bytesToStringByBig(data));
        return data;
    }

    /**
     * CRC8校验
     *
     * @param src 数据源
     * @param aim 目标数据
     * @return 结果
     */
    private int crc8Check(int src, byte aim) {
        int crc = src;
        crc ^= aim;
        crc &= 0xff;
        for (int i = 0; i < 8; i++) {
            if ((crc & 0x01) != 0) {
                crc = (crc >> 1) ^ 0x8c;
                crc &= 0xff;
            } else {
                crc >>= 1;
            }
        }
        crc &= 0xff;
        return crc;
    }

    public byte[] getNextData() throws IOException {
        packIndex ++;
        return getNowData();
    }

    /**
     * 停止升级
     */
    public void stop() {

    }

    public boolean isEnd() {
        return false;
    }

    private OnUpgradeListener listener;

    public void setOnUpgradeListener(OnUpgradeListener l){
        this.listener = l;
    }

    public interface OnUpgradeListener{

        void failed(String msg);
    }
}
