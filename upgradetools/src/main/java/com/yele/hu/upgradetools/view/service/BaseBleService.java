package com.yele.hu.upgradetools.view.service;

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
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.ParcelUuid;

import androidx.annotation.Nullable;

import com.yele.baseapp.utils.ByteUtils;
import com.yele.baseapp.utils.LogUtils;
import com.yele.baseapp.utils.StringUtils;
import com.yele.baseapp.view.service.BaseService;
import com.yele.hu.upgradetools.bean.ChannelQueue;
import com.yele.hu.upgradetools.bean.info.car.OkaiBleDevice;
import com.yele.hu.upgradetools.data.BLEUUIDs;
import com.yele.hu.upgradetools.policy.event.BleDevRequestConEvent;
import com.yele.hu.upgradetools.policy.event.BleServiceStatus;
import com.yele.hu.upgradetools.receiver.BluetoothMonitorReceiver;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.yele.hu.upgradetools.data.BLEUUIDs.TEST_SERVICE_UUID;

public class BaseBleService extends BaseService {

    private static final String TAG = "BaseBleService";

    @Override
    public void onCreate() {
        super.onCreate();
        registerBleBroadcast();   // 初始化时，注册蓝牙状态监听
        createForNoti();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterBleBroadcast();
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

    // 蓝牙状态广播
    private BluetoothMonitorReceiver receiver;

    /**
     * 注册蓝牙状态监听广播
     * 监视蓝牙关闭和打开的状态
     */
    private void registerBleBroadcast() {
        // 注册广播
        receiver = new BluetoothMonitorReceiver(new BluetoothMonitorReceiver.OnDeviceConnectChangeListener() {
            @Override
            public void bleOpen() {
                EventBus.getDefault().post(new BleServiceStatus(BleServiceStatus.BLUE_SERVER_SCAN_CONNECT,null));
            }

            @Override
            public void bleClose() {
                EventBus.getDefault().post(new BleServiceStatus(BleServiceStatus.BLUE_SERVER_STOP,null));
            }

            @Override
            public void devConnected(BluetoothDevice device) {
                //deviceConnected();
            }

            @Override
            public void devDisconnected(BluetoothDevice device) {
                EventBus.getDefault().post(new BleServiceStatus(BleServiceStatus.BLUE_CANCEL_CONNECT,null));
            }
        });
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        registerReceiver(receiver, intentFilter);
    }

    /**
     * 注销蓝牙状态监听广播
     */
    private void unregisterBleBroadcast() {
        if (receiver != null){
            unregisterReceiver(receiver);
            receiver = null;
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

    private int id = 0;
    // 当前扫描出来的设备列表
    protected List<OkaiBleDevice> listScanDevice = new ArrayList<>();
    private OkaiBleDevice bleDevice;

    /**
     * 蓝牙扫描的返回接口
     */
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
            // TODO 设备名称判空处理
            if (StringUtils.isEmpty(name)) {
                name = "AAAAA";
//                return;
            }

            bleDevice = new OkaiBleDevice();
            bleDevice.device = device;
            LogUtils.i(TAG, "scan ble dev name:" + name);
            LogUtils.i(TAG, "ScanRecord：" + ByteUtils.bytesToStringByBig(scanRecord));
            bleDevice.rssi = result.getRssi();
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
                bleDevice.setType(sns[1]);
                bleDevice.sn = sns[0];
            }

            if (StringUtils.isEmpty(bleDevice.sn) || StringUtils.isEmpty(bleDevice.type)) {
                return;
            }

            if (!bleDevice.type.equals("S020T")) {
                return;
            }

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
        LogUtils.i(TAG,"开始扫描");
        if (isScanDev) {
            listScanDevice.clear();
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            LocationManager alm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (alm != null && !alm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                LogUtils.i(TAG,"没有开GPS");
                //Toast.makeText(this, "请开启GPS！", Toast.LENGTH_SHORT).show();
            }
        }

        LogUtils.i(TAG,"扫描");
        isScanDev = true;
        listScanDevice.clear();
        try {
            mScanner.startScan(buildScanFilters(), buildScanSettings(), scanCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtils.i(TAG,"扫描" + isLocationOpen(this));
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
        // 过滤
        ParcelUuid parcelUuidMask = ParcelUuid.fromString("FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF");
        ParcelUuid parcelUuid = ParcelUuid.fromString(TEST_SERVICE_UUID);
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
        listScanDevice.clear();
        if (mScanner != null) {
            mScanner.stopScan(scanCallback);
        }
        isScanDev = false;
        LogUtils.i(TAG,"停止扫描");
    }


    private final int CON_ACTION_NONE = 0;
    private final int CON_ACTION_CONNECT = 1;
    private final int CON_ACTION_DISCONNECT = 2;
    private int conAction;
    protected BluetoothDevice conDevice;
    protected BluetoothGatt conGatt;
    protected OkaiBleDevice okaiBleDevice;

    private int mConnectCount=0;


    protected String COMMON_CHANNEL;   // 普通数据发送通道

    /**
     * 连接到目标蓝牙设备
     *
     * @param bleDevice 目标设备
     * @return 蓝牙连接之后的Gatt
     */
    protected BluetoothGatt connectDevice(final OkaiBleDevice bleDevice) {
        okaiBleDevice = bleDevice;
        BluetoothDevice device = bleDevice.device;
        String address = device.getAddress();
        LogUtils.i(TAG,"连接目标蓝牙：" + device.getName() + " mac Address:" + address);
        conAction = CON_ACTION_CONNECT;
        int transport = -1;
        try {
            transport = device.getClass().getDeclaredField("TRANSPORT_LE").getInt(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        BluetoothGatt gatt;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (transport == -1) {
                gatt = device.connectGatt(this, false, gattConCallback, BluetoothDevice.TRANSPORT_LE);
            } else {
                gatt = device.connectGatt(this, false, gattConCallback, transport);
            }
        } else {
            gatt = device.connectGatt(this, false, gattConCallback);
        }
        return gatt;
    }

    /**
     * 当前仪表设备连接的返回接口
     */
    private BluetoothGattCallback gattConCallback = new BluetoothGattCallback() {

        private ChannelQueue queue ;


        @Override
        public void onPhyUpdate(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
            super.onPhyUpdate(gatt, txPhy, rxPhy, status);
            LogUtils.i(TAG,"onPhyUpdate - txPhy:" + txPhy + "\nrxPhy:" + rxPhy + "\nstatus:" + status);
        }

        @Override
        public void onPhyRead(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
            super.onPhyRead(gatt, txPhy, rxPhy, status);
            LogUtils.i(TAG,"onPhyRead - txPhy:" + txPhy + "\nrxPhy:" + rxPhy + "\nstatus:" + status);
        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            LogUtils.i(TAG,"onConnectionStateChange-status:" + status + "\nnewState:" + newState);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    LogUtils.i(TAG,"onConnectionStateChange - status: " + "连接成功");
                    conDevice = gatt.getDevice();
                    okaiBleDevice.device = conDevice;
                    conGatt = gatt;
                    conAction = CON_ACTION_NONE;
                    gatt.discoverServices();
                    //deviceConnected();
                    // todo 通知栏连接状态修改
                } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                    LogUtils.i(TAG,"onConnectionStateChange - status: " + "断开成功");
                    conGatt.close();
                    conGatt = null;
                    conDevice = null;
                    okaiBleDevice.device = null;
                    conAction = CON_ACTION_NONE;
                    deviceDisconnect();
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
                LogUtils.i(TAG,"onConnectionStateChange - status: " + "操作失败" + " newState" + newState);
            } else {
                if (conAction == CON_ACTION_CONNECT) {
                    LogUtils.i(TAG,"连接失败"+ "mBluetoothGatt closed");
                    if (conGatt != null){
                        conGatt.disconnect();
                    }
                    LogUtils.i(TAG,"清除缓存是否成功:"+refreshCache());
                    if (mConnectCount < 3){
                        mConnectCount++;
                        LogUtils.i(TAG,"重新连接第"+ mConnectCount+"次");
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                EventBus.getDefault().post(new BleDevRequestConEvent(BleDevRequestConEvent.CODE_REQUEST_CONNECT,okaiBleDevice));
                            }
                        },1000);
                    }else {
                        mConnectCount = 0;
                        deviceConnectFailed();
                    }
                } else {
                    if (conGatt==null){
                        return;
                    }
                    LogUtils.i(TAG,"清除缓存是否成功:"+refreshCache());
                    conGatt.disconnect();
                    mConnectCount = 0;
                    deviceDisConnectFailed();
                }
            }

        }


        /**
         * 使能通道
         * @param gatt1 蓝牙通讯接口
         * @param bgc 需要使能通道的特征
         */
        private void enableChannel(BluetoothGatt gatt1, BluetoothGattCharacteristic bgc) {
            gatt1.setCharacteristicNotification(bgc, true);
            final BluetoothGattDescriptor descriptor = bgc.getDescriptor(UUID.fromString(BLEUUIDs.BTLE_DESCRIPTOR_CHARACTERISTIC_CONFIG));
            if (descriptor != null) {
                LogUtils.i(TAG,"notify");
                gatt1.setCharacteristicNotification(bgc, true);
                boolean ok = descriptor.setValue(
                        BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                LogUtils.e(TAG,"is OK: " + ok);
                gatt1.writeDescriptor(descriptor);
            }
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            LogUtils.i(TAG,"character:" + bgc.getUuid().toString());
        }




        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            LogUtils.i(TAG,"onServicesDiscovered - status: " + status + "开始使能");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                    gatt.requestMtu(200);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        gatt.setPreferredPhy(BluetoothDevice.PHY_LE_2M_MASK, BluetoothDevice.PHY_LE_2M_MASK, BluetoothDevice.PHY_OPTION_NO_PREFERRED);
                    }
                }else{
                    ParcelUuid parcelUuidMask = ParcelUuid.fromString(BLEUUIDs.TEST_SERVICE_UUID);
                    BluetoothGattService gattService = gatt.getService(parcelUuidMask.getUuid());
                    List<BluetoothGattCharacteristic> lists = gattService.getCharacteristics();
                    queue=new ChannelQueue(lists.size());
                    for (int i = 0; i < lists.size(); i++) {
                        BluetoothGattCharacteristic bgc = lists.get(i);
                        final int properties = bgc.getProperties();
                        if ((properties & BluetoothGattCharacteristic.PROPERTY_NOTIFY) == 0) {
                            continue;
                        }
                        queue.addQueue(bgc);
                    }
                    enableChannel(gatt, queue.getQueue());
                }

            }
        }

        /**
         * 主动从蓝牙设备读，回调从蓝牙设备读操作的结果
         * @param gatt 蓝牙连接操作
         * @param characteristic gatt特征
         * @param status 读取状态
         */
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            //super.onCharacteristicRead(gatt, characteristic, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                LogUtils.i(TAG,"onCharacteristicRead - GATT_SUCCESS");
                UUID uuid = characteristic.getService().getUuid();
                byte[] buff = characteristic.getValue();
                if (UUID.fromString(BLEUUIDs.BTLE_SERVICE_DEVICE_INFO_UUID).equals(uuid)
                        && UUID.fromString(BLEUUIDs.BTLE_CHARAC_SYSTEM_ID_UUID).equals(uuid)) {
                    // todo 从电池服务读取数据返回
                    LogUtils.i(TAG,"收到电池临时数据：" + new String(buff));
                } else if (UUID.fromString(BLEUUIDs.BTLE_SERVICE_BATTERY_UUID).equals(uuid)
                        && UUID.fromString(BLEUUIDs.BTLE_CHARAC_BATTERY_LEVEL_UUID).equals(uuid)) {
                    // todo 从设备信息服务读取数据返回
                    LogUtils.i(TAG,"收到信息临时数据：" + new String(buff));
                } else if (UUID.fromString(BLEUUIDs.BTLE_SERVICE_RW_UUID).equals(uuid)
                        && UUID.fromString(BLEUUIDs.BTLE_CHARAC_RW_5_UUID).equals(uuid)) {
                    // todo 从读写独居服务读取数据返回
                    LogUtils.i(TAG,"收到读写临时数据：" + new String(buff));
                }
            } else {
                LogUtils.i(TAG,"onCharacteristicRead - failed");
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            //super.onCharacteristicWrite(gatt, characteristic, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                LogUtils.i(TAG,"onCharacteristicWrite - GATT_SUCCESS");
            } else {
                LogUtils.i(TAG,"onCharacteristicWrite - Failed");
            }
        }

        // 缓存上一次的数据的队列
        private Map<String, String> mapData = new HashMap<>();

        /**
         * 从蓝牙设备获取到数据
         * @param gatt 连接的蓝牙设备的通讯类
         * @param characteristic 蓝牙gatt特征，包含了一堆数据
         */
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            //super.onCharacteristicChanged(gatt, characteristic);
            BluetoothDevice dev = gatt.getDevice();

            String channel = characteristic.getUuid().toString().toLowerCase();
            String revData = mapData.get(channel);

            byte[] buff = characteristic.getValue();
            String revStr = new String(buff);
            if (!channel.equals(BLEUUIDs.REPORT_CHANNEL)) {
                LogUtils.i(TAG,"收到数据：" + ByteUtils.bytesToStringByBig(buff));
                LogUtils.i(TAG,"收到数据：" + revStr + "，通道：" + channel);
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


        class RegularCmd {
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
            String reg = "(" + reg1 + ")|(" + reg2 + ")|(" + reg3 + ")|(" + reg4 + ")";
            Pattern p = Pattern.compile(reg);
            Matcher matcher = p.matcher(cmdStr);
            while (matcher.find()) {
                readCmd.list.add(matcher.group());
                end = matcher.end();
            }
            if (end == -1) {
                readCmd.endStr = cmdStr;
            } else {
                readCmd.endStr = cmdStr.substring(end);
            }
            return readCmd;
        }


        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            //super.onDescriptorRead(gatt, descriptor, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                LogUtils.i(TAG,"onDescriptorRead - GATT_SUCCESS");
            } else {
                LogUtils.i(TAG,"onDescriptorRead - fail");
            }
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            //super.onDescriptorWrite(gatt, descriptor, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (!queue.isEmpty()){
                    enableChannel(gatt,queue.getQueue());
                }else {
                    deviceConnected();
                }
                LogUtils.i(TAG,"onDescriptorWrite - GATT_SUCCESS" + descriptor.toString()+"0"+status);
            }else {
                LogUtils.i(TAG,"onDescriptorWrite - fail");
                deviceDisconnect();
            }
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            //super.onReliableWriteCompleted(gatt, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                LogUtils.i(TAG,"onReliableWriteCompleted - GATT_SUCCESS");
            } else {
                LogUtils.i(TAG,"onReliableWriteCompleted - failed");
            }
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            //super.onReadRemoteRssi(gatt, rssi, status);
            LogUtils.i(TAG,"onReadRemoteRssi - rssi: " + rssi);
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
            if (BluetoothGatt.GATT_SUCCESS == status && 200 == mtu) {
                LogUtils.i(TAG,"切换mtu成功");
                ParcelUuid parcelUuidMask = ParcelUuid.fromString(TEST_SERVICE_UUID);
                BluetoothGattService gattService = gatt.getService(parcelUuidMask.getUuid());
                List<BluetoothGattCharacteristic> lists = gattService.getCharacteristics();
                queue=new ChannelQueue(lists.size());
                for (int i = 0; i < lists.size(); i++) {
                    BluetoothGattCharacteristic bgc = lists.get(i);
                    final int properties = bgc.getProperties();
                    if ((properties & BluetoothGattCharacteristic.PROPERTY_NOTIFY) == 0) {
                        continue;
                    }
                    queue.addQueue(bgc);
                }
                enableChannel(gatt, queue.getQueue());
            } else {
                LogUtils.i(TAG,"mtu设置失败" + mtu);
            }
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
     *  设备连接超时
     */
    protected void  deviceConnectTimeOut(){

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
     * 设备连接断开
     */
    protected void deviceDisconnect() {

    }

    /**
     * 设备已连接
     */
    protected void deviceConnected() {

    }

    /**
     * 使能完全
     */
    protected void deviceChannelSuccess() {

    }

    // 当前规定的每一个包的长度
    private final int MAX_PACKET_LEN = 200;

    private static long lastTimestamp = 0;

    private long time = System.currentTimeMillis() - lastTimestamp;

    private boolean isSendCmd = false;

    /**
     * 发送具体的数据
     *
     * @param channel 当前的通道号
     * @param data    具体的数据
     * @return 返回是否发送成功
     */
    protected boolean sendSCmd(final String channel, final byte[] data) {
        LogUtils.i(TAG,"-----------------------------------------");

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

            LogUtils.i(TAG,"packet_len=" + packet_len + ", pack=" + packet_cnt + ", i=" + i);
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
                            LogUtils.e(TAG,"需要延迟了" + space + isDeviceBusy());
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
                    isSendCmd = sendCmdByte(channel, packet);
                    LogUtils.i(TAG,"指令发送1：" + isSendCmd);
                    if (!isSendCmd) {
                        LogUtils.e(TAG,"写入数据失败" + isDeviceBusy());
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        isSendCmd = sendCmdByte(channel, packet);
                        LogUtils.i(TAG,"指令发送2：" + isSendCmd);
                        if (!isSendCmd) {
                            LogUtils.e(TAG,"写入数据失败" + isDeviceBusy());
                        }
                    }
                }
            }.start();
            lastTimestamp = System.currentTimeMillis();
        }
        LogUtils.i(TAG,"指令发送结果：" + isSendCmd);
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
        LogUtils.i(TAG,"写入数据：" + new String(data) + " len:" + data.length);
        bgc.setValue(data);
        return gatt.writeCharacteristic(bgc);
    }

    private static long HONEY_CMD_TIMEOUT = 2000;

    private boolean isDeviceBusy() {
        boolean state = false;
        try {
            state = (boolean) readField(conGatt, "mDeviceBusy");
            LogUtils.e("potter123", "isDeviceBusy:" + state);
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
