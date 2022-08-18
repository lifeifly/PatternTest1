package com.yele.bluetoothlib.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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
import android.os.IBinder;
import android.os.ParcelUuid;

import androidx.annotation.Nullable;

import com.yele.baseapp.utils.ByteUtils;
import com.yele.baseapp.utils.LogUtils;
import com.yele.baseapp.utils.StringUtils;
import com.yele.baseapp.view.service.BaseService;
import com.yele.bluetoothlib.bean.BLEUUIDs;
import com.yele.bluetoothlib.bean.LogDebug;
import com.yele.bluetoothlib.bean.device.OkDevice;
import com.yele.bluetoothlib.policy.broadcast.BluetoothMonitorReceiver;

import java.util.ArrayList;
import java.util.List;

/**
 * 蓝牙的基础功能
 */
public class BaseBleService extends BaseService {

    private static final String TAG = "BaseBleService";
    /**
     * 单独打印开关
     */
    private boolean DEBUG_LOG = true;

    /**
     * 单独打印 log.i的信息
     *
     * @param msg 打印内容
     */
    private void logi(String msg) {
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
    private void logw(String msg) {
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
    private void loge(String msg) {
        if (!DEBUG_LOG || !LogDebug.IS_LOG) {
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

    private BluetoothMonitorReceiver bleMonitorReceiver;

    /**
     * 注册蓝牙广播
     */
    private void registerBleBroadcast() {
        bleMonitorReceiver = new BluetoothMonitorReceiver(new BluetoothMonitorReceiver.OnDeviceConnectChangeListener() {
            @Override
            public void bleOpen() {

            }

            @Override
            public void bleClose() {

            }

            @Override
            public void devConnected(BluetoothDevice device) {
                deviceConnected();
            }

            @Override
            public void devDisconnected(BluetoothDevice device) {
                deviceDisconnect();
            }
        });
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

    /**
     * 兼容8.0以上的要求，需要创建通知栏
     */
    private void createForNoti() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel("CHANNEL_ONE_ID", "蓝牙调试工具后台服务",
                    NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(channel);
            Notification notification = new Notification.Builder(this).setChannelId("CHANNEL_ONE_ID")
                    .setTicker("Nature")
                    .setContentTitle("蓝牙调试工具后台服务")
                    .setContentText("蓝牙调试工具后台服务")
                    .build();
            startForeground(1349, notification);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

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

    // 当前扫描出来的设备列表
    private List<OkDevice> listScanDevice = new ArrayList<>();

    // 扫描回调接口
    private ScanCallback scanCallback = new ScanCallback() {
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
            if (StringUtils.isEmpty(name)) {
                return;
            }
            logi("scan ble dev name:" + name);
            if (scanRecord == null || scanRecord.length <= 2) {
                logi("scan dev record : null or len not enough!");
                return;
            }
            int len = scanRecord[0] - 1;
            if (len <= 0 || len > scanRecord.length - 2) {
                logi("scan dev record is not dev data");
                return;
            }

            OkDevice okDevice = new OkDevice();
            okDevice.rssi = result.getRssi();
            okDevice.device = device;
            okDevice.rawBytes = scanRecord;

            byte[] buff = new byte[len];
            System.arraycopy(scanRecord, 2, buff, 0, len);
            String devName = new String(buff);
            logi("scan our dev name:" + devName);

            if (StringUtils.isEmpty(devName)) {
                return;
            }
//            record.getServiceUuids();

            String[] sns = dealScan(scanRecord);

            if (sns != null && sns.length == 2) {
                okDevice.setType(sns[1]);
                okDevice.sn = sns[0];
            }
            logi("scan our dev sn:" + okDevice.sn);
            if (!dealOurService(record)) {
                return;
            }

            if (devName.equals(aimScanDevName)) {
                discoverAimDevice(device);
                stopScanDev();
            } else if (aimScanDevName == null) {
                int index = -1;
                for (int i = 0; i < listScanDevice.size(); i++) {
                    if (listScanDevice.get(i).device.getAddress().equals(device.getAddress())) {
                        index = i;
                        break;
                    }
                }
                if (index == -1) {
                    discoverNewDevice(okDevice);
                    listScanDevice.add(okDevice);
                }else{
                    OkDevice de = listScanDevice.get(index);
                    de.rssi = result.getRssi();
                }
            }
        }

        private boolean dealOurService(ScanRecord scanRecord) {
            List<ParcelUuid> list = scanRecord.getServiceUuids();
            if (list == null) {
                return false;
            }
            int index = -1;
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).toString().equals(BLEUUIDs.TEST_SERVICE_UUID)) {
                    index = i;
                    break;
                }
            }
            return index != -1;
        }

        /**
         * 处理当前的扫描的的广播码
         * @param scanRecord
         * @return
         */
        private String[] dealScan(byte[] scanRecord) {
            String[] datas = null;
            try {
                byte[] buffer = new byte[5];
                System.arraycopy(scanRecord, 7, buffer, 0, 5);
                String carType = new String(buffer);
                int length = scanRecord[12];
                byte[] bufferSn = new byte[2];
                System.arraycopy(scanRecord, 21 + length, bufferSn, 0, 2);
                String nameCar = new String(bufferSn);
                byte[] bufferSnName = new byte[6];
                System.arraycopy(scanRecord, 23 + length, bufferSnName, 0, 6);
                String snProduction = ByteUtils.bytesToStringByBig(bufferSnName);
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
    protected void discoverNewDevice(OkDevice device) {

    }

    /**
     * 发现目标设备
     *
     * @param device 具体的设备信息
     */
    protected void discoverAimDevice(BluetoothDevice device) {

    }

    // 蓝牙扫描器
    private BluetoothLeScanner mScanner;

    // 当前是否正在扫描蓝牙
    private boolean isScanDev = false;

    /**
     * 开始扫描设备
     */
    protected void startScanDev() {
        new Thread() {

            //设置蓝牙扫描过滤器
            private ScanFilter.Builder scanFilterBuilder;
            //设置蓝牙扫描设置
            private ScanSettings.Builder scanSettingBuilder;

            private List<ScanFilter> scanFilterList;

            /**
             * 创建扫描时的过滤属性
             * @return 反馈扫描属性
             */
            private List<ScanFilter> buildScanFilters() {
                scanFilterList = new ArrayList<>();
                // 通过服务 uuid 过滤自己要连接的设备   过滤器搜索GATT服务UUID
                scanFilterBuilder = new ScanFilter.Builder();
                scanFilterList.add(scanFilterBuilder.build());
                return scanFilterList;
            }

            /**
             * 创建扫描配置
             * @return 当前扫描配置
             */
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

            @Override
            public void run() {
                super.run();
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    LocationManager alm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    if (alm != null && !alm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        logi("没有开GPS");
                    }
                }

                logi("扫描");
                isScanDev = true;
                listScanDevice.clear();
                try {
                    mScanner.startScan(buildScanFilters(), buildScanSettings(), scanCallback);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
        logi("扫描" + isLocationOpen(this));

    }

    /**
     * 判断当前定位功能是否打开
     *
     * @param context 上下文
     * @return 是否打开定位权限
     */
    public static boolean isLocationOpen(final Context context) {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        //gps定位
        boolean isGpsProvider = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        //网络定位
        boolean isNetWorkProvider = manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return isGpsProvider || isNetWorkProvider;
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
        listScanDevice.clear();
        if (mScanner != null) {
            mScanner.stopScan(scanCallback);
        }
//        adapter.stopLeScan(mLeScanCallBack);
        isScanDev = false;
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
}
