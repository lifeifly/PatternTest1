package com.yele.huht.bluetoothsdklib.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.ParcelUuid;
import android.util.Log;

import androidx.annotation.Nullable;

import com.yele.baseapp.utils.ByteUtils;
import com.yele.baseapp.utils.LogUtils;
import com.yele.baseapp.utils.StringUtils;
import com.yele.baseapp.view.service.BaseService;
import com.yele.huht.bluetoothsdklib.bean.DebugFlag;
import com.yele.huht.bluetoothsdklib.bean.OkaiBleDevice;
import com.yele.huht.bluetoothsdklib.callBcak.OnConnectDevState;
import com.yele.huht.bluetoothsdklib.callBcak.OnDisConnectDevState;
import com.yele.huht.bluetoothsdklib.callBcak.OnScanDevState;
import com.yele.huht.bluetoothsdklib.data.BLEUUIDs;
import com.yele.huht.bluetoothsdklib.data.BindData;
import com.yele.huht.bluetoothsdklib.receiver.BluetoothMonitorReceiver;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BaseBleService extends BaseService {

    private static final String TAG = "BaseBleService";

    protected void logi(String msg) {
        if (!DebugFlag.IS_DEBUG) {
            return;
        }
        LogUtils.i(TAG, msg);
    }

    protected void logw(String msg) {
        if (!DebugFlag.IS_DEBUG) {
            return;
        }
        LogUtils.w(TAG, msg);
    }

    protected void loge(String msg) {
        if (!DebugFlag.IS_DEBUG) {
            return;
        }
        LogUtils.e(TAG, msg);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerBleBroadcast();
        createForNoti();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unRegisterBleBroadcast();
    }

    /**
     * 兼容8.0以上的要求，需要创建通知栏
     */
    private void createForNoti() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel("CHANNEL_ONE_ID", "蓝牙工具后台服务",
                    NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(channel);
            Notification notification = new Notification.Builder(this).setChannelId("CHANNEL_ONE_ID")
                    .setTicker("Nature")
                    .setContentTitle("蓝牙工具后台服务")
                    .setContentText("蓝牙工具后台服务")
                    .build();
            startForeground(1349, notification);
        }
    }

    private BluetoothMonitorReceiver bleMonitorReceiver;

    /**
     * 注册蓝牙广播
     */
    private void registerBleBroadcast() {
        bleMonitorReceiver = new BluetoothMonitorReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);

        // 监视蓝牙设备与APP连接的状态
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        registerReceiver(bleMonitorReceiver, intentFilter);
    }

    private void unRegisterBleBroadcast() {
        unregisterReceiver(bleMonitorReceiver);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // 连接状态接口
    protected OnConnectDevState connState;
    // 断开连接接口
    protected OnDisConnectDevState disConnState;
    // 扫描接口
    protected OnScanDevState scanState;

    // 当前的蓝牙适配器
    private BluetoothAdapter adapter = null;

    // 目标蓝牙的名称
    private String aimScanDevName;

    /**
     * 设置扫描的目标设备名称
     *
     * @param aimScanDevName 目标设备名称
     */
    protected void setAimScanDevName(String aimScanDevName) {
        this.aimScanDevName = aimScanDevName;
    }

    private int id = 0;
    // 当前扫描出来的设备列表
    protected List<OkaiBleDevice> listScanDevice = new ArrayList<>();
    private OkaiBleDevice bleDevice;

    private ScanCallback scanCallback = new ScanCallback() {


        private String[] dealScan(byte[] scanRecord) {
            String[] datas = null;
            try {
                byte[] buffer = new byte[5];
                System.arraycopy(scanRecord, 7, buffer, 0, 5);
                String carType = new String(buffer);
                int length = scanRecord[12];
                byte[] buffferSn = new byte[2];
                System.arraycopy(scanRecord, 21 + length, buffferSn, 0, 2);
                String nameCar = new String(buffferSn);
                byte[] buffferSnName = new byte[6];
                System.arraycopy(scanRecord, 23 + length, buffferSnName, 0, 6);
                String snProduction = ByteUtils.bytesToStringByBig(buffferSnName);
                StringBuffer stringBuffer = new StringBuffer(nameCar);
                stringBuffer.append(snProduction);
                datas = new String[2];
                datas[0] = stringBuffer.toString();
                datas[1] = carType;
            } catch (IndexOutOfBoundsException e) {
                return datas;
            }
            return datas;
        }

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            BluetoothDevice device = result.getDevice();
            ScanRecord record = result.getScanRecord();
            byte[] scanRecord = record.getBytes();
            if (device == null) {
                return;
            }
            String name = device.getName();
            // todo 蓝牙名称判空处理
            if (StringUtils.isEmpty(name)) {
                name = "AAAA";
//                return;
            }
            bleDevice = new OkaiBleDevice();
            bleDevice.device = device;
            LogUtils.i(TAG, "scan ble dev name:" + name);
            LogUtils.i(TAG, "ScanRecord：" + ByteUtils.bytesToStringByBig(scanRecord));
            if (scanRecord == null || scanRecord.length <= 2) {
                LogUtils.i(TAG, "scan dev record : null or len not enough!");
                return;
            }
            int len = scanRecord[0] - 1;
            if (len <= 0 || len > scanRecord.length - 2) {
                LogUtils.i(TAG, "scan dev record is not dev data");
                return;
            }
            byte[] buff = new byte[len];
            System.arraycopy(scanRecord, 2, buff, 0, len);
            String devName = new String(buff);
            LogUtils.i(TAG, "scan our dev name:" + devName);

            String[] sns = dealScan(scanRecord);
            if (sns != null && sns.length == 2) {
                bleDevice.type = sns[1];
                bleDevice.sn = sns[0];
            }

            /*if (StringUtils.isEmpty(bleDevice.sn) || StringUtils.isEmpty(bleDevice.type)) {
                return;
            }*/

            /*if (!bleDevice.type.equals("S200T") && !bleDevice.type.equals("S500T")) {
                return;
            }*/

            if (name.equals(aimScanDevName)) {
                discoverAimDevice(bleDevice);
                stopScanDev();
            } else if (aimScanDevName == null) {
                boolean has = false;
                for (int i = 0; i < listScanDevice.size(); i++) {
                    if (listScanDevice.get(i).device.getAddress().equals(device.getAddress())) {
                        has = true;
                        break;
                    }
                }
                if (!has) {
                    bleDevice.id = id;
                    id++;
                    discoverNewDevice(bleDevice);
                    listScanDevice.add(bleDevice);
                }
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };

    /**
     * 当前次扫描蓝牙列表的时候发现了新设备
     *
     * @param device 蓝牙扫描到的新设备
     */
    protected void discoverNewDevice(OkaiBleDevice device) {

    }

    /**
     * 发现目标设备
     *
     * @param device 具体的设备信息
     */
    protected void discoverAimDevice(OkaiBleDevice device) {

    }

    private BluetoothLeScanner mScanner;
    // 当前是否正在扫描蓝牙
    private boolean isScanDev = false;

    /**
     * 开始扫描设备
     */
    protected void startScanDev() {
        if (adapter == null) {
            adapter = BluetoothAdapter.getDefaultAdapter();
            adapter.startDiscovery();
        }
        if (!adapter.isEnabled()) {
            adapter.enable();
        }
        mScanner = adapter.getBluetoothLeScanner();
        logi("开始扫描");
        if (isScanDev) {
            listScanDevice.clear();
            return;
        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//            LocationManager alm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//            if (alm != null && !alm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//                logi("没有开GPS");
////                Toast.makeText(this, "请开启GPS！", Toast.LENGTH_SHORT).show();
//            }
//        }

        logi("扫描");
        isScanDev = true;
        listScanDevice.clear();
        try {
            mScanner.startScan(buildScanFilters(), buildScanSettings(), scanCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        adapter.startLeScan(mLeScanCallBack);
//        logi("扫描" + isLocationOpen(this));
    }

    public static boolean isLocationOpen(final Context context) {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        //gps定位
        boolean isGpsProvider = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        //网络定位
        boolean isNetWorkProvider = manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return isGpsProvider || isNetWorkProvider;
    }

    //设置蓝牙扫描过滤器
    private ScanFilter.Builder scanFilterBuilder;
    //设置蓝牙扫描设置
    private ScanSettings.Builder scanSettingBuilder;

    private List<ScanFilter> scanFilterList;

    private List<ScanFilter> buildScanFilters() {
        scanFilterList = new ArrayList<>();
        // 通过服务 uuid 过滤自己要连接的设备   过滤器搜索GATT服务UUID
        scanFilterBuilder = new ScanFilter.Builder();
        ParcelUuid parcelUuidMask = ParcelUuid.fromString("FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF");
        ParcelUuid parcelUuid = ParcelUuid.fromString(BLEUUIDs.TEST_SERVICE_UUID);
        scanFilterBuilder.setServiceUuid(parcelUuid, parcelUuidMask);
        scanFilterList.add(scanFilterBuilder.build());
        return scanFilterList;
    }

    private ScanSettings buildScanSettings() {
        scanSettingBuilder = new ScanSettings.Builder();
        //设置蓝牙LE扫描的扫描模式。
        //使用最高占空比进行扫描。建议只在应用程序处于此模式时使用此模式在前台运行
        scanSettingBuilder.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            //设置蓝牙LE扫描滤波器硬件匹配的匹配模式
            //在主动模式下，即使信号强度较弱，hw也会更快地确定匹配.在一段时间内很少有目击/匹配。
            scanSettingBuilder.setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE);
            //设置蓝牙LE扫描的回调类型
            //为每一个匹配过滤条件的蓝牙广告触发一个回调。如果没有过滤器是活动的，所有的广告包被报告
            scanSettingBuilder.setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES);
        }
        return scanSettingBuilder.build();
    }

    /**
     * 停止扫描设备
     */
    protected void stopScanDev() {
        if (adapter == null) {
            return;
        }
        if (!isScanDev) {
            return;
        }
//        listScanDevice.clear();
        if (mScanner != null) {
            mScanner.stopScan(scanCallback);
        }
//        adapter.stopLeScan(mLeScanCallBack);
        isScanDev = false;
    }


    private final int CON_ACTION_NONE = 0;
    private final int CON_ACTION_CONNECT = 1;
    private final int CON_ACTION_DISCONNECT = 2;
    private int conAction;
    protected BluetoothDevice conDevice;
    protected BluetoothGatt conGatt;

    private int mConnectCount = 0;

    /**
     * 连接到目标蓝牙设备
     *
     * @param macAddress 目标地址
     * @return 蓝牙连接之后的Gatt
     */
    protected BluetoothGatt connectDevice(final String macAddress) {
        if (adapter == null) {
            adapter = BluetoothAdapter.getDefaultAdapter();
            adapter.startDiscovery();
        }
        if (adapter.isEnabled()) {
            adapter.enable();
        }
        BluetoothDevice device = adapter.getRemoteDevice(macAddress);
        logi("连接目标蓝牙：" + device.getName() + " mac Address:" + macAddress);
        conAction = CON_ACTION_CONNECT;
        conGatt = device.connectGatt(BaseBleService.this, false, new BluetoothGattCallback() {

            @Override
            public void onConnectionStateChange(BluetoothGatt gatt1, int status, int newState) {
                super.onConnectionStateChange(gatt1, status, newState);
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        logi("onConnectionStateChange - status: " + "连接成功");
//                        logw("连接成功：" + DateUtils.getTimeStrByStyle("yyyy-MM-dd HH:mm:ss"));
                        conDevice = gatt1.getDevice();
                        BindData.bleDevice = gatt1.getDevice();
                        conGatt = gatt1;
                        BindData.bleDevGatt = gatt1;
                        conAction = CON_ACTION_NONE;
                        gatt1.discoverServices();
//                        deviceConnected();
                        // todo 通知栏连接状态修改
                    } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                        logi("onConnectionStateChange - status: " + "断开成功");
                        logi("清除缓存是否成功"+refreshCache()+"");
//                        logw("连接断开成功：" + DateUtils.getTimeStrByStyle("yyyy-MM-dd HH:mm:ss"));
                        conGatt.close();
                        conGatt = null;
                        BindData.bleDevGatt = null;
                        conDevice = null;
                        conAction = CON_ACTION_NONE;
                        BindData.bleDevice = null;
                        deviceDisconnect();
                        // todo 通知栏断开连接状态修改
                    }
                } else {
                    if (conAction == CON_ACTION_CONNECT) {
                        logi("连接失败"+ "mBluetoothGatt closed");
                        if (conGatt == null){
                            return;
                        }
                        logi("清除缓存是否成功"+refreshCache()+"");
                        conGatt.disconnect();
                        conGatt.close();
                        conGatt = null;
                        BindData.bleDevGatt = null;
                        if (mConnectCount < BindData.connectRetry){
                            mConnectCount++;
                            logi("重新连接第"+ mConnectCount+"次");
                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    connectDevice(macAddress);
                                }
                            },1000);
                        }else {
                            mConnectCount++;
                            deviceConnectFailed();
                        }

                    } else {
                        if (conGatt==null){
                            return;
                        }
                        logi("清除缓存是否成功"+refreshCache()+"");
                        conGatt.disconnect();
                        conGatt.close();
                        conGatt = null;
                        BindData.bleDevGatt = null;
                        mConnectCount=0;
                        deviceDisConnectFailed();
                    }
                    logi("onConnectionStateChange - status: " + "操作失败" + " newState" + newState);
//                    logw("连接操作失败：" + DateUtils.getTimeStrByStyle("yyyy-MM-dd HH:mm:ss"));
                }
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt1, int status) {
//                        super.onServicesDiscovered(gatt, status);
                logi("onServicesDiscovered - status: " + status + "开始使能");
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    isStartEnable = true;
                    enableChannel(gatt1, BLEUUIDs.COMMON_CHANNEL);
                }
            }

            boolean isStartEnable = false, isEnableChannel = false, isEnableCom = false, isEnableQR = false, isEnableResport = false, isEnableUpgrade = false, isEnableConfig = false;

            /**
             * 使能通道
             * @param gatt1 蓝牙通讯接口
             * @param channel 需要使能的通道
             */
            private void enableChannel(BluetoothGatt gatt1, String channel) {
//                gatt1.requestMtu(40);
                ParcelUuid parcelUuidMask = ParcelUuid.fromString(BLEUUIDs.TEST_SERVICE_UUID);
                BluetoothGattService gattService = gatt1.getService(parcelUuidMask.getUuid());
                if (null == gattService) {
                    logi("服务获取失败");
                    return;
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                List<BluetoothGattCharacteristic> lists = gattService.getCharacteristics();
                for (int i = 0; i < lists.size(); i++) {
                    BluetoothGattCharacteristic bgc = lists.get(i);
                    final int properties = bgc.getProperties();
                    if ((properties & BluetoothGattCharacteristic.PROPERTY_NOTIFY) == 0) {
                        continue;
                    }
                    boolean has = false;
                    if (bgc.getUuid().toString().equals(channel)) {
                        has = true;
                    } else if (bgc.getUuid().toString().equals(BLEUUIDs.QR_CODE_CHANNEL)) {
                        has = false;
                    } else if (bgc.getUuid().toString().equals(BLEUUIDs.REPORT_CHANNEL)) {
                        has = false;
                    } else if (bgc.getUuid().toString().equals(BLEUUIDs.UPGRADE_CHANNEL)) {
                        has = false;
                    }
                    if (has) {
                        gatt1.setCharacteristicNotification(bgc, true);
                        final BluetoothGattDescriptor descriptor = bgc.getDescriptor(UUID.fromString(BLEUUIDs.BTLE_DESCRIPTOR_CHARACTERISTIC_CONFIG));
                        if (descriptor != null) {
                            logi("notify");
                            gatt1.setCharacteristicNotification(bgc, true);
                            descriptor.setValue(
                                    BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            gatt1.writeDescriptor(descriptor);
                            logi("character: " + bgc.getUuid().toString()+" notify打开");
                        }
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

            private Map<String, String> mapData = new HashMap<>();

            /**
             * 从蓝牙设备获取到数据
             * @param gatt1 连接的蓝牙设备的通讯类
             * @param characteristic 蓝牙gatt特征，包含了一堆数据
             */
            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt1, BluetoothGattCharacteristic characteristic) {
//                        super.onCharacteristicChanged(gatt, characteristic);
//                logi("----> onCharacteristicChanged()");
                BluetoothDevice dev = gatt1.getDevice();
                if (dev == null) {
                    logi("rev null dev data");
                    return;
                }
                String name = dev.getName();
                // todo 蓝牙名称判空处理
                /*if (StringUtils.isEmpty(name)) {
                    logi("rev name null dev data");
                    return;
                }*/

                String channel = characteristic.getUuid().toString().toLowerCase();
                String revData = mapData.get(channel);

                byte[] buff = characteristic.getValue();
                String revStr = new String(buff);
                if (!channel.equals(BLEUUIDs.REPORT_CHANNEL)) {
                    logi("收到数据：" + ByteUtils.bytesToStringByBig(buff));
                    logi("收到数据：" + revStr);
                }

                if (revStr.startsWith("+ACK") || revStr.startsWith("+RESP")) {
                    if (revStr.endsWith("$\r\n")) {
                        revData += revStr;
                        List<String> list = getListCmdStr(revData);
                        for (int i = 0; i < list.size(); i++) {
                            dealRevData(characteristic.getUuid().toString().toLowerCase(), list.get(i));
                        }
                        revData = "";
                    } else {
                        if (revStr.contains("$\r\n")) {
                            // 如果中间有尾巴
                            String[] buffer = revStr.split("[$\r\n]");
                            for (int i = 0; i < buffer.length - 1; i++) {
                                revData += buffer[i];
                            }
                            List<String> list = getListCmdStr(revData);
                            for (int i = 0; i < list.size(); i++) {
                                dealRevData(characteristic.getUuid().toString().toLowerCase(), list.get(i));
                            }
                            revData = buffer[buffer.length - 1];
                        } else {
                            revData += revStr;
                        }
                    }
                } else if (revStr.endsWith("$\r\n")) {
                    // 收到剩余数据的可能性
                    revData += revStr;
                    List<String> list = getListCmdStr(revData);
                    for (int i = 0; i < list.size(); i++) {
                        dealRevData(characteristic.getUuid().toString().toLowerCase(), list.get(i));
                    }
                    revData = "";
                } else if (revStr.contains("$\r\n")) {
                    // 如果中间有剩余数据，那么就把判定符之前的数据给组合到上一次数据中，剩余的数据用于下次。
                    String[] buffer = revStr.split("[$\r\n]");
                    for (int i = 0; i < buffer.length - 1; i++) {
                        revData += buffer[i];
                    }
                    List<String> list = getListCmdStr(revData);
                    for (int i = 0; i < list.size(); i++) {
                        dealRevData(characteristic.getUuid().toString().toLowerCase(), list.get(i));
                    }
                    revData = buffer[buffer.length - 1];
                } else {
                    revData += revStr;
                }
                mapData.put(channel, revData);
            }

            /**
             * 从当前的指令堆栈中获取可能存在的多条指令
             * @param cmdStr 指令数据
             * @return
             */
            private List<String> getListCmdStr(String cmdStr) {
                List<String> list = new ArrayList<>();
                String reg1 = "\\+ACK(((?!ACK)(?!RESP).)*?)\\$\r\n";
                String reg2 = "\\+RESP(((?!ACK)(?!RESP).)*?)\\$\r\n";
                String reg = "(" + reg1 + ")|(" + reg2 + ")";
                Pattern p = Pattern.compile(reg);
                Matcher matcher = p.matcher(cmdStr);
                while (matcher.find()) {
                    list.add(matcher.group());
                }
                return list;
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
            public void onDescriptorWrite(BluetoothGatt gatt1, BluetoothGattDescriptor descriptor, int status) {
//                        super.onDescriptorWrite(gatt, descriptor, status);
                String channel = descriptor.getUuid().toString().toLowerCase();
                logi("使能通道：" + channel);
                mapData.put(channel, "");
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    if (isStartEnable) {
//                        if (!isEnableCom) {
//                            isEnableCom = true;
//                            enableChannel(gatt1, BLEUUIDs.QR_CODE_CHANNEL);
//                        } else
                        if (!isEnableCom) {
                            isEnableCom = true;
                            enableChannel(gatt1, BLEUUIDs.REPORT_CHANNEL);
                        } else if (!isEnableResport) {
                            isEnableResport = true;
                            enableChannel(gatt1, BLEUUIDs.UPGRADE_CHANNEL);
                        } else if (!isEnableUpgrade) {
                            isEnableUpgrade = true;
                            enableChannel(gatt1, BLEUUIDs.CONFIG_CHANNEL);
                        } else if (!isEnableConfig) {
                            isEnableConfig = true;
                            isEnableChannel = true;
                            isStartEnable = false;
                        }
                    }
                    logi("onDescriptorWrite - GATT_SUCCESS" + descriptor.toString());
                }
                if (isStartEnable&&descriptor.getCharacteristic().getUuid().toString().equals(BLEUUIDs.REPORT_CHANNEL)){
                    logi("onDescriptorWrite - GATT_SUCCESS" + descriptor.toString());
                    connectTimer.cancel();
                    if (status == BluetoothGatt.GATT_SUCCESS){
                        deviceConnected();
                    }else {
                        deviceConnectFailed();
                    }
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


        });
        return conGatt;
    }

    protected CountDownTimer connectTimer = new CountDownTimer(1000 * 15, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {

            logi("超时检测倒计时"+millisUntilFinished/1000+"s");
        }

        @Override
        public void onFinish() {
            if (conGatt!=null){
                conGatt.close();
                conGatt = null;
                BindData.bleDevGatt = null;
            }
            if (conDevice!=null){
                conDevice = null;
            }
            BindData.bleDevice=null;
            deviceConnectTimeOut();
        }
    };

    protected synchronized boolean refreshCache() {
        try {
            final Method refresh = BluetoothGatt.class.getMethod("refresh");
            if (refresh != null) {
                refresh.setAccessible(true);
                boolean success = (Boolean) refresh.invoke(conGatt);
                return  success;
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
        return false;
    }

    /**
     * 断开设备失败
     */
    protected void deviceDisConnectFailed() {

    }

    /**
     * 连接设备失败
     */
    protected void deviceConnectFailed() {

    }

    /**
     * 设备连接呗断开
     */
    protected void deviceDisconnect() {

    }

    /**
     * 设备已连接
     */
    protected void deviceConnected() {

    }

    /**
     *  设备连接超时
     */
    protected void  deviceConnectTimeOut(){

    }

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
    protected boolean sendSCmd(final String channel, final byte[] data) {

        final int timeout = 200;

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

//            if (!sendCmdByte(channel, packet)) {
//                loge("写入数据失败");
//                return false;
//            }
//
//            if (timeout > 0) {
//                try {
//                    Thread.sleep(timeout);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
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
                    if (!sendCmdByte(channel, packet)) {
                        loge("写入数据失败" + isDeviceBusy());
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
        BluetoothGattService gattService = gatt.getService(UUID.fromString(BLEUUIDs.TEST_SERVICE_UUID));
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

    private static long HONEY_CMD_TIMEOUT = 2000;

    private boolean isDeviceBusy() {
        boolean state = false;
        try {
            state = (boolean) readField(conGatt, "mDeviceBusy");
            Log.e("potter123", "isDeviceBusy:" + state);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return state;
    }

    public Object readField(Object object, String name) throws IllegalAccessException, NoSuchFieldException {
        Field field = object.getClass().getDeclaredField(name);
        field.setAccessible(true);
        return field.get(object);
    }

    /**
     * 处理收到的指令数据
     *
     * @param channel 具体的通道号
     * @param data    具体的数据
     */
    protected void dealRevData(String channel, String data) {

    }
}
