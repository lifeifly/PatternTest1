package com.yele.hu.upgradetools.policy;

import android.util.Log;

import com.yele.baseapp.utils.ByteUtils;
import com.yele.baseapp.utils.LogUtils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class UpgradeLengthFrame {

    private static final String TAG = "UpgradeLengthFrame";

    private boolean DEBUG_LOG = true;

    private void logi(String msg) {
        if (!DEBUG_LOG) {
            return;
        }
        LogUtils.i(TAG, msg);
    }


    public static final int MAX_DATA_LEN = 128;

    private int type = 0;
    public static final int TYPE_CONTROL = 13;


    private boolean isPlanUDA;

    private String path;

    public UpgradeLengthFrame(int type, String path) {
        this.type = type;
        this.isPlanUDA = type == TYPE_CONTROL;
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
    public long packIndex = 0;      // 当前发送的包得坐标（第几个包）
    private long length = 0;        // 升级包的数据大小

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
            listener.failed("文件路径为空");
            return;
        }
        File file = new File(path);
        File parent = file.getParentFile();
        if (parent == null || !file.getParentFile().exists()) {
            listener.failed("文件夹路径不存在");
            return;
        }
        if (!file.exists()) {
            listener.failed("升级文件不存在");
            return;
        }

        boolean fileInit = false;
        try {
            fileInit = initFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!fileInit) {
            listener.failed("初始化文件失败");
        }else{
            startUpgrade = true;
        }
    }

    private boolean initFile() throws IOException {
        randomAccessFile = new RandomAccessFile(new File(path), "rw");
        randomAccessFile.seek(0);
        length = randomAccessFile.length();
        byte[] head;
        /*if (isPlanUDA) {
            head = new byte[6];
        }else{*/
            head = new byte[13];
       // }
        int num = randomAccessFile.read(head);
        if (num != 6 && num != 13) {
            randomAccessFile.close();
            randomAccessFile = null;
            return false;
        }
        packIndex = 0;
     /*   if (isPlanUDA) {
            packNum = ByteUtils.changeUnit16Int(head[5]) + 1;
            logi("包的总数"+packNum);
        }else{*/
            byte[] lenBuf = new byte[2];
            System.arraycopy(head, 11, lenBuf, 0, 2);
         //   packNum = ByteUtils.bytesToIntByBig(lenBuf, 2);


           int  mode1 = (int) (length  % 128);
           if (mode1 == 0) {
               packNum = (int) (length / 128);
           } else {
               packNum = (int) length / 128 + 1;
        }

/*
        logi("包的总数"+packNum);

           logi("包的大小"+length+"");
*/

       // }
        return true;
    }

    private long getContentIndex() {
        if (packIndex <= 0) {
            return 0;
        }
        return (packIndex - 1) * MAX_DATA_LEN + MAX_DATA_LEN;
    }

    /**
     * 获取开始帧数据
     * @return 返回开始帧的数据
     * @throws IOException 读取数据失败
     */
    public byte[] getStartData() throws IOException {
        packIndex = 0;
        return getNowData();
    }

    /**
     * 获取当前帧数据
     * @return 返回当前帧数据
     * @throws IOException 读取数据失败
     */
    public byte[] getNowData() throws IOException {
        long index = getContentIndex();
        if (index == length) {
            return packIndexControl(new byte[MAX_DATA_LEN]);

        } else if (index > length)
        {
          //  logi("当前位置index:"+index+"  "+"当前长度length:"+length);
            return null;
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
           // logi("当前发送包为null" + packIndex);
            return null;
        }
        logi("当前发送包" + packIndex);
        if (buff.length < MAX_DATA_LEN) {
            byte[] data = new byte[MAX_DATA_LEN];
            System.arraycopy(buff, 0, data, 0, buff.length);
            return packIndexControl(data);
        } else {
           return packIndexControl(buff);

        }
    }

    public boolean isEnd(){
        if (packIndex<=0){
            return  false;
        }
        return  packIndex==packNum;
    }

    public int getPackNum() {
        return packNum;
    }

    public int getPercent() {
       // Log.e(TAG, "getPercent111: " + packIndex + " num " + packNum);
        if (packIndex == 0 || packNum == 0) {
            return 0;
        }
        //Log.e(TAG, "getPercent: " + packIndex + " num " + packNum);

        Log.i("packindex", packIndex / packNum + "");
        return (int) ((float) packIndex / packNum * 100);
    }

    /**
     * 将指定的数据打包
     *
     * @param buff 当前指定的数据
     * @return 返回打包好的数据
     */
    private byte[] packIndexControl(byte[] buff) {
        byte[] data = new byte[MAX_DATA_LEN + 16];
        String head = "AT+OKUDA";
        byte[] headBuff = head.getBytes();
        System.arraycopy(headBuff, 0, data, 0, 8);

        byte[] lenBuff = ByteUtils.longToBytesByBig(packIndex, 4);
        System.arraycopy(lenBuff, 0, data, 8, 4);

        System.arraycopy(buff, 0, data, 12, MAX_DATA_LEN);
        int crc = 0;
        for (int i = 0; i < MAX_DATA_LEN + 12; i++) {
            crc = crc8Check(crc, data[i]);
        }

        data[MAX_DATA_LEN + 12] = ByteUtils.intToUnitByte(crc);
        data[MAX_DATA_LEN + 13] = 0x24;
        data[MAX_DATA_LEN + 14] = 0x0d;
        data[MAX_DATA_LEN + 15] = 0x0a;
        LogUtils.i("ssss", ByteUtils.bytesToStringByBig(data));
        return data;
    }

    private byte[] packIndexMCU(byte[] buff) {
        byte[] data = new byte[MAX_DATA_LEN + 16];
        String head = "AT+OKUDB";
        byte[] headBuff = head.getBytes();
        System.arraycopy(headBuff, 0, data, 0, 8);

        byte[] lenBuff = ByteUtils.longToBytesBySmall(packIndex, 4);
        System.arraycopy(lenBuff, 0, data, 8, 4);

//        byte[] buf = new byte[buff.length];
//        for (int i = 0; i < buff.length; i++) {
//            buf[i] = buff[buff.length - 1 - i];
//        }
        System.arraycopy(buff, 0, data, 12, MAX_DATA_LEN);
        int crc = 0;
        for (int i = 0; i < MAX_DATA_LEN + 12; i++) {
            crc = crc8Check(crc, data[i]);
        }
        data[MAX_DATA_LEN + 12] = ByteUtils.intToUnitByte(crc);
        data[MAX_DATA_LEN + 13] = 0x24;
        data[MAX_DATA_LEN + 14] = 0x0d;
        data[MAX_DATA_LEN + 15] = 0x0a;
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




    /**
     * 升级监听
     */
    private OnUpgradeListener listener;

    public void setOnUpgradeListener(OnUpgradeListener l){
        this.listener = l;
    }

    public interface OnUpgradeListener{

        void failed(String msg);
    }
}
