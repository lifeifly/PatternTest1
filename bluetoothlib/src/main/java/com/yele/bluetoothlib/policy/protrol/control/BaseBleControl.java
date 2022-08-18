package com.yele.bluetoothlib.policy.protrol.control;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Build;
import android.os.ParcelUuid;

import com.yele.baseapp.utils.ByteUtils;
import com.yele.baseapp.utils.LogUtils;
import com.yele.baseapp.utils.StringUtils;
import com.yele.bluetoothlib.bean.BLEUUIDs;
import com.yele.bluetoothlib.bean.LogDebug;
import com.yele.bluetoothlib.bean.device.OkDevice;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Copyright (C),2020-2021,杭州野乐科技有限公司
 * FileName: BaseBleControl
 *
 * @Author: Chenxc
 * @Date: 2021/7/21 13:04
 * @Description: 蓝牙通讯控制的基础类
 * History:
 * <author> <time><version><desc>
 */
public class BaseBleControl {

    protected static final String TAG = "BaseBleControl";
    /**
     * 单独打印开关
     */
    protected boolean DEBUG_LOG = true;

    /**
     * 单独打印 log.i的信息
     *
     * @param msg 打印内容
     */
    protected void logi(String msg) {
        if (!DEBUG_LOG || !LogDebug.IS_LOG) {
            return;
        }
        LogUtils.i(TAG, msg);
    }

    /**
     * 单独打印 log.w的信息
     *
     * @param msg 打印内容
     */
    protected void logw(String msg) {
        if (!DEBUG_LOG || !LogDebug.IS_LOG) {
            return;
        }
        LogUtils.w(TAG, msg);
    }

    /**
     * 单独打印 log.e的信息
     *
     * @param msg 打印内容
     */
    protected void loge(String msg) {
        if (!DEBUG_LOG || !LogDebug.IS_LOG) {
            return;
        }
        LogUtils.e(TAG, msg);
    }

    protected OkDevice okDevice;

    protected Context mContext;

    protected BluetoothDevice conDevice;

    protected BluetoothGatt conGatt;

    protected OnBleConnectListener connectListener;

    protected OnBleDataChangeListener dataChangeListener;

    protected String TEST_SERVICE_UUID = BLEUUIDs.TEST_SERVICE_UUID;
    protected String COMMON_CHANNEL,REPORT_CHANNEL,UPGRADE_CHANNEL,
            COMMON_HEAD_CHANNEL,REPORT_HEAD_CHANNEL,UPGRADE_HEAD_CHANNEL,
            COMMON_KNAP_CHANNEL,REPORT_KNAP_CHANNEL,UPGRADE_KNAP_CHANNEL,
            CONFIG_CHANNE;

    protected final int CON_ACTION_NONE = 0,CON_ACTION_CONNECT=1,CON_ACTION_DISCONNECT=2;
    protected int conAction = 0;
    /**
     * 当前仪表设备连接的返回接口
     */
    protected BluetoothGattCallback gattConCallback = new BluetoothGattCallback() {

        private void deviceConnected() {
            okDevice.isConnected = true;
            if (connectListener != null) {
                connectListener.connected(0);
            }
        }

        private void deviceDisconnected() {
            okDevice.isConnected = false;
            if (connectListener != null) {
                connectListener.disConnected(0);
            }
        }

        private void deviceConnectFailed() {
            okDevice.isConnected = false;
            if (connectListener != null) {
                connectListener.connected(1);
            }
        }

        private void deviceDisConnectFailed() {
            okDevice.isConnected = false;
            if (connectListener != null) {
                connectListener.disConnected(1);
            }
        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt1, int status, int newState) {
            super.onConnectionStateChange(gatt1, status, newState);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    logi("onConnectionStateChange - status: " + "连接成功");
                    conDevice = gatt1.getDevice();
                    conGatt = gatt1;
                    conAction = CON_ACTION_NONE;
                    gatt1.discoverServices();
                    deviceConnected();
                    // todo 通知栏连接状态修改
                } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                    logi("onConnectionStateChange - status: " + "断开成功");
                    conGatt.close();
                    conGatt = null;
                    conDevice = null;
                    conAction = CON_ACTION_NONE;
                    logi("onConnectionStateChange - status: " + "断开成功--反馈前端");
                    deviceDisconnected();
                    // todo 通知栏断开连接状态修改
                }
            } else if (status == BluetoothGatt.GATT_FAILURE) {
                if (conAction == CON_ACTION_CONNECT) {
                    deviceConnectFailed();
                } else if (conAction == CON_ACTION_DISCONNECT) {
                    deviceDisConnectFailed();
                }else if(conAction == CON_ACTION_NONE){
                    deviceConnectFailed();
                }
                logi("onConnectionStateChange - status: " + "操作失败" + " newState" + newState);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt1, int status) {
//                        super.onServicesDiscovered(gatt, status);
            logi("onServicesDiscovered - status: " + status + "开始使能");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                isStartEnable = true;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                    gatt1.requestMtu(200);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        gatt1.setPreferredPhy(BluetoothDevice.PHY_LE_2M_MASK, BluetoothDevice.PHY_LE_2M_MASK, BluetoothDevice.PHY_OPTION_NO_PREFERRED);
                    }
                }else{
                    enableChannel(gatt1, COMMON_CHANNEL);
                }
            }
        }




        /**
         * 主动从蓝牙设备读，回调从蓝牙设备读操作的结果
         * @param gatt1 蓝牙连接操作
         * @param characteristic gatt特征
         * @param status 读取状态
         */
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt1, BluetoothGattCharacteristic characteristic, int status) {
//                        super.onCharacteristicRead(gatt, characteristic, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                logi("onCharacteristicRead - GATT_SUCCESS");
                UUID uuid = characteristic.getService().getUuid();
                byte[] buff = characteristic.getValue();
                if (UUID.fromString(BLEUUIDs.BTLE_SERVICE_DEVICE_INFO_UUID).equals(uuid)
                        && UUID.fromString(BLEUUIDs.BTLE_CHARAC_SYSTEM_ID_UUID).equals(uuid)) {
                    // todo 从电池服务读取数据返回
                    logi("收到电池临时数据：" + new String(buff));
                } else if (UUID.fromString(BLEUUIDs.BTLE_SERVICE_BATTERY_UUID).equals(uuid)
                        && UUID.fromString(BLEUUIDs.BTLE_CHARAC_BATTERY_LEVEL_UUID).equals(uuid)) {
                    // todo 从设备信息服务读取数据返回
                    logi("收到信息临时数据：" + new String(buff));
                } else if (UUID.fromString(BLEUUIDs.BTLE_SERVICE_RW_UUID).equals(uuid)
                        && UUID.fromString(BLEUUIDs.BTLE_CHARAC_RW_5_UUID).equals(uuid)) {
                    // todo 从读写独居服务读取数据返回
                    logi("收到读写临时数据：" + new String(buff));
                }
            } else {
                logi("onCharacteristicRead - failed");
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt1, BluetoothGattCharacteristic characteristic, int status) {
//                        super.onCharacteristicWrite(gatt, characteristic, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                logi("onCharacteristicWrite - GATT_SUCCESS");
            } else {
                logi("onCharacteristicWrite - Failed");
            }
        }

        // 缓存上一次的数据的队列
        private Map<String, String> mapData = new HashMap<>();

        /**
         * 从蓝牙设备获取到数据
         * @param gatt1 连接的蓝牙设备的通讯类
         * @param characteristic 蓝牙gatt特征，包含了一堆数据
         */
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt1, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt1, characteristic);
            BluetoothDevice dev = gatt1.getDevice();
            if (dev == null) {
                logi("rev null dev data");
                return;
            }
            String name = dev.getName();
            if (StringUtils.isEmpty(name)) {
                logi("rev name null dev data");
                return;
            }
            String channel = characteristic.getUuid().toString().toLowerCase();
            String revData = mapData.get(channel);

            byte[] buff = characteristic.getValue();
            String revStr = new String(buff);
            LogUtils.i(TAG,"总收到的指令：" + revStr);
            if (!channel.equals(REPORT_CHANNEL)) {
                logi("收到数据：" + ByteUtils.bytesToStringByBig(buff));
                logi("收到数据：" + revStr);
            }
            String data = revData + revStr;

            RegularCmd regularCmd = getListCmdStr(data);
            List<String> list = regularCmd.list;
            for (int i = 0; i < list.size(); i++) {
                dealRevData(channel, list.get(i));
            }
            revData = regularCmd.endStr;

            mapData.put(channel, revData);
        }

        class RegularCmd{
            String endStr;
            List<String> list;
        }

        /**
         * 从当前的指令堆栈中获取可能存在的多条指令
         * 通过正则表达式来进行指令数据的截取
         * @param cmdStr 需要进行判断的收取数据
         * @return 返回解析出来的指令队列
         */
        private RegularCmd getListCmdStr(String cmdStr) {
            RegularCmd readCmd = new RegularCmd();
            int end = -1;
            readCmd.list = new ArrayList<>();
            String reg1 = "\\+ACK(((?!ACK)(?!RESP).)*?)\\$\r\n";
            String reg2 = "\\+RESP(((?!ACK)(?!RESP).)*?)\\$\r\n";
            String reg3 = "\\+ACK(((?!ACK)(?!RESP).)*?)\\?\r\n";
            String reg4 = "\\+RESP(((?!ACK)(?!RESP).)*?)\\?\r\n";
            String reg = "(" + reg1 + ")|(" + reg2 + ")|(" + reg3 + ")|(" +reg4 +")";
            Pattern p = Pattern.compile(reg);
            Matcher matcher = p.matcher(cmdStr);
            while (matcher.find()) {
                readCmd.list.add(matcher.group());
                end = matcher.end();
            }

            if (end == -1) {
                readCmd.endStr = cmdStr;
            }else{
                readCmd.endStr = cmdStr.substring(end);
            }
            return readCmd;
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
            if (BluetoothGatt.GATT_SUCCESS == status && 200 == mtu) {
                logi("切换mtu成功");
                enableChannel(gatt,COMMON_CHANNEL);
            } else {
                logi("mtu设置失败" + mtu);

            }
        }

        // 使能通道的标志  //参数1：开始使能的标志  参数2 是否已经使能完全通道了
        boolean isStartEnable = false,isEnableChannel = false;
        // 参数1 普通通道使能标志  参数2 上报使能通道标志 参数3 更新使能通道  参数4 头盔配置使能通道  参数5 头盔上报使能通道  参数6 头盔更新使能通道  参数7 背包配置使能通道  参数8 背包上报使能通道  参数9 背包更新使能通道   参数10 配置使能通道
        boolean isEnableCom = false, isEnableReport = false, isEnableUpgrade = false,
                isEnableHeadConfig = false,isEnableHeadReport = false, isEnableHeadUpgrade = false,
                isEnableKnapConfig = false,isEnableKnapReport = false, isEnableKnapUpgrade = false,
                isEnableConfig = false;

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt1, BluetoothGattDescriptor descriptor, int status) {
//                        super.onDescriptorWrite(gatt, descriptor, status);
            String channel = descriptor.getUuid().toString().toLowerCase();
            logi("使能通道：" + channel);
            mapData.put(channel, "");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (isStartEnable) {
                    if (!isEnableCom) {
                        isEnableCom = true;
                        enableChannel(gatt1, REPORT_CHANNEL);
                    } else if (!isEnableReport) {
                        isEnableReport = true;
                        enableChannel(gatt1, UPGRADE_CHANNEL);
                    } else if (!isEnableUpgrade) {
                        isEnableUpgrade = true;
                        enableChannel(gatt1, COMMON_HEAD_CHANNEL);
                    } else if (!isEnableHeadConfig) {
                        isEnableHeadConfig = true;
                        enableChannel(gatt1, REPORT_HEAD_CHANNEL);
                    } else if (!isEnableHeadReport) {
                        isEnableHeadReport = true;
                        enableChannel(gatt1, UPGRADE_HEAD_CHANNEL);
                    }else if (!isEnableHeadUpgrade) {
                        isEnableHeadUpgrade = true;
                        enableChannel(gatt1, COMMON_KNAP_CHANNEL);
                    } else if (!isEnableKnapConfig) {
                        isEnableKnapConfig = true;
                        enableChannel(gatt1, REPORT_KNAP_CHANNEL);
                    } else if (!isEnableKnapReport) {
                        isEnableKnapReport = true;
                        enableChannel(gatt1, UPGRADE_KNAP_CHANNEL);
                    }else if (!isEnableKnapUpgrade) {
                        isEnableKnapUpgrade = true;
                        enableChannel(gatt1, CONFIG_CHANNE);
                    } else if (!isEnableConfig) {
                        isEnableConfig = true;
                        isEnableChannel = true;
                        isStartEnable = false;
                    }
                }
                logi("onDescriptorWrite - GATT_SUCCESS" + descriptor.toString());
            } else {
                logi("onDescriptorWrite - fail");
            }
        }

        /**
         * 使能通道
         * @param gatt1 蓝牙通讯接口
         * @param channel 需要使能的通道
         */
        private void enableChannel(BluetoothGatt gatt1, String channel) {
//                gatt1.requestMtu(40);
            ParcelUuid parcelUuidMask = ParcelUuid.fromString(TEST_SERVICE_UUID);
            BluetoothGattService gattService = gatt1.getService(parcelUuidMask.getUuid());
            if (null == gattService) {
                logi("服务获取失败");
                return;
            }
            List<BluetoothGattCharacteristic> lists = gattService.getCharacteristics();
            for (int i = 0; i < lists.size(); i++) {
                BluetoothGattCharacteristic bgc = lists.get(i);
                final int properties = bgc.getProperties();
                if ((properties & BluetoothGattCharacteristic.PROPERTY_NOTIFY) == 0) {
                    continue;
                }
                boolean has = false;
                String now = bgc.getUuid().toString();
                loge("获取character:" + now);
                if (now.equals(channel)) {
                    has = true;
                }
                if (has) {
                    gatt1.setCharacteristicNotification(bgc, true);
                    final BluetoothGattDescriptor descriptor = bgc.getDescriptor(UUID.fromString(BLEUUIDs.BTLE_DESCRIPTOR_CHARACTERISTIC_CONFIG));
                    if (descriptor != null) {
                        logi("notify");
                        gatt1.setCharacteristicNotification(bgc, true);
                        boolean ok = descriptor.setValue(
                                BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                        loge("is OK: " + ok);
                        gatt1.writeDescriptor(descriptor);
                    }
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    logi("character:" + bgc.getUuid().toString());
                }
            }
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt1, BluetoothGattDescriptor descriptor, int status) {
//                        super.onDescriptorRead(gatt, descriptor, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                logi("onDescriptorRead - GATT_SUCCESS");
            } else {
                logi("onDescriptorRead - fail");
            }
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt1, int status) {
//                        super.onReliableWriteCompleted(gatt, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                logi("onReliableWriteCompleted - GATT_SUCCESS");
            } else {
                logi("onReliableWriteCompleted - failed");
            }
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt1, int rssi, int status) {
//                        super.onReadRemoteRssi(gatt, rssi, status);
            logi("onReadRemoteRssi - rssi: " + rssi);
        }

    };

    // 当前规定的每一个包的长度
    private final int MAX_PACKET_LEN = 200;

    private static long lastTimestamp = 0;

    private long time = System.currentTimeMillis() - lastTimestamp;
    /**
     * 发送具体的数据
     *
     * @param channel 当前的通道号
     * @param data    具体的数据
     * @return 返回是否发送成功
     */
    protected boolean sendSCmd(final String channel, byte[] data) {

//        byte[] buff = new byte[data.length];
//        System.arraycopy(data,0,buff,0,data.length);
//        buff[data.length]  = 0x0d;
//        buff[data.length + 1] = 0x0a;

        final int timeout = 30;

        int packet_cnt = (data.length + MAX_PACKET_LEN - 1) / MAX_PACKET_LEN;
        int packet_len = 0;
        for (int i = 0; i < packet_cnt; i++) {

            if (i < (packet_cnt - 1)) {
                packet_len = MAX_PACKET_LEN;
            } else {
                packet_len = data.length % MAX_PACKET_LEN;
                if (0 == packet_len) {
                    packet_len = MAX_PACKET_LEN;
                }
            }

            logi("packet_len=" + packet_len + "pack" + packet_cnt + ", i=" + i);
            final byte[] packet = new byte[packet_len];
            System.arraycopy(data, (i * MAX_PACKET_LEN), packet, 0, packet_len);

            new Thread() {
                @Override
                public void run() {
                    super.run();
                    if (time < timeout || isDeviceBusy()) {
                        int rest = 0;
                        do {
                            long space = timeout - time;
                            loge("需要延迟了" + space + isDeviceBusy());
                            if (space <= 0) {
                                space = timeout;
                            }
                            rest++;
                            if (rest > 15) {
                                lastTimestamp = System.currentTimeMillis();
                                break;
                            }
                            try {
                                Thread.sleep(space);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } while (isDeviceBusy());
                    }
                    LogUtils.e(TAG, new String(packet));
                    if (!sendCmdByte(channel, packet)) {
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (!sendCmdByte(channel, packet)) {
                            loge("写入数据失败" + isDeviceBusy());
                        }
                    }
                }
            }.start();
            lastTimestamp = System.currentTimeMillis();
        }
        return true;
    }

    /**
     * 发送数据（当前发送的数据必须小于20个字节）
     *
     * @param channel 指定的通道号
     * @param data    数据内容
     * @return 发送数据是否成功
     */
    private boolean sendCmdByte(String channel, byte[] data) {
        BluetoothGatt gatt = conGatt;
        if (null == gatt || null == data || data.length <= 0 || null == channel || channel.isEmpty()) {
            return false;
        }
        BluetoothGattService gattService = gatt.getService(UUID.fromString(TEST_SERVICE_UUID));
        if (gattService == null) {
            return false;
        }
        BluetoothGattCharacteristic bgc = gattService.getCharacteristic(UUID.fromString(channel));
        if (bgc == null) {
            return false;
        }
        logi("写入数据：" + new String(data) + " len:" + data.length);
        bgc.setValue(data);
        return gatt.writeCharacteristic(bgc);
    }

    /**
     * 判断当前蓝牙设备是否在繁忙
     *
     * @return 返回是否设备写入繁忙
     */
    private boolean isDeviceBusy() {
        boolean state = false;
        try {
            state = (boolean) readField(conGatt, "mDeviceBusy");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return state;
    }

    /**
     * 读取文件
     *
     * @param object
     * @param name
     * @return
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     */
    public Object readField(Object object, String name) throws IllegalAccessException, NoSuchFieldException {
        Field field = object.getClass().getDeclaredField(name);
        field.setAccessible(true);
        return field.get(object);
    }

    protected void dealRevData(String channel, String s) {

    }

}
