package com.yele.hu.upgradetools.view.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yele.baseapp.utils.LogUtils;
import com.yele.baseapp.utils.ToastUtil;
import com.yele.baseapp.utils.ViewUtils;
import com.yele.baseapp.view.activity.BaseActivity;
import com.yele.hu.upgradetools.MyApplication;
import com.yele.hu.upgradetools.R;
import com.yele.hu.upgradetools.bean.CmdFlag;
import com.yele.hu.upgradetools.bean.RevResult;
import com.yele.hu.upgradetools.bean.info.car.ReportInfo;
import com.yele.hu.upgradetools.data.DataManager;
import com.yele.hu.upgradetools.policy.event.BleDevRequestConEvent;
import com.yele.hu.upgradetools.policy.event.BleServiceStatus;
import com.yele.hu.upgradetools.policy.event.CmdResultEvent;
import com.yele.hu.upgradetools.policy.event.CmdRevEvent;
import com.yele.hu.upgradetools.policy.event.CmdSendEvent;
import com.yele.hu.upgradetools.view.service.DfuService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import no.nordicsemi.android.dfu.DfuProgressListener;
import no.nordicsemi.android.dfu.DfuServiceInitiator;
import no.nordicsemi.android.dfu.DfuServiceListenerHelper;


public class DfuUpgradeActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "DfuUpgradeActivity";

    @Override
    protected int getResId() {
        return R.layout.activity_dfu_upgrade;
    }

    private ImageView ivBack;
    private TextView tvTitle,tvConfirm,tvDevice,tvConnState;
    private TextView tvControlUpdate;
    private Button btnReadConfig;
    private Button btnRotateUpgrade,btnVolumeUpgrade;
    private RelativeLayout rlVersion;
    private TextView tvMeterSoftVersion,tvMeterHardwareVersion,tvControlSoftVersion,tvControlHardwareVersion;

    private DataManager dataManager;

    @Override
    protected void findView() {
        ivBack = findViewById(R.id.iv_back);
        tvTitle = findViewById(R.id.tv_title);
        tvConfirm = findViewById(R.id.tv_confirm);
        tvControlUpdate = findViewById(R.id.tv_control_update);
        tvDevice = findViewById(R.id.tv_device);
        tvConnState = findViewById(R.id.tv_conn_state);
        btnReadConfig = findViewById(R.id.btn_read_config);
        btnRotateUpgrade = findViewById(R.id.btn_rotate_upgrade);
        btnVolumeUpgrade = findViewById(R.id.btn_volume_upgrade);
        rlVersion = findViewById(R.id.rl_version);
        tvMeterSoftVersion = findViewById(R.id.tv_meter_soft_version);
        tvMeterHardwareVersion = findViewById(R.id.tv_meter_hardware_version);
        tvControlSoftVersion = findViewById(R.id.tv_control_soft_version);
        tvControlHardwareVersion = findViewById(R.id.tv_control_hardware_version);
    }

    @Override
    protected void initData() {
        dataManager = DataManager.getInstance();
        if (dataManager.device == null) {
            disConnectDevice();
            finish();
            return;
        }

    }



    @Override
    protected void initView() {
        ivBack.setOnClickListener(this);
        btnRotateUpgrade.setOnClickListener(this);
        btnVolumeUpgrade.setOnClickListener(this);
        btnReadConfig.setOnClickListener(this);

        tvTitle.setText(R.string.upgrade_page);
        tvConfirm.setText("");

        ViewUtils.hideView(btnRotateUpgrade);
        ViewUtils.hideView(btnVolumeUpgrade);
        ViewUtils.hideView(rlVersion);

        freshConnStatus();

        DfuServiceListenerHelper.registerProgressListener(DfuUpgradeActivity.this, mDfuProgressListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    /**
     * 更新连接状态
     */
    private void freshConnStatus() {
        if (dataManager.device == null) {
            tvConnState.setText(R.string.not_connect);
            tvDevice.setText("--");
            tvConnState.setBackgroundResource(R.drawable.bg_dialog_cancel);
            MyApplication.isCon = false;
        } else {
            tvDevice.setText(dataManager.device.device.getName());
            tvConnState.setText(R.string.connected);
            tvConnState.setBackgroundResource(R.drawable.bg_dialog_confirm);
            MyApplication.isCon = true;
        }
    }

    private boolean isUpgrade = false;

    @Override
    public void onClick(View view) {
        freshConnStatus();
        switch (view.getId()){
            case R.id.iv_back:
                // 断开连接
                disConnectDevice();
                finish();
                break;
            case R.id.btn_rotate_upgrade:
                if(isUpgrade) {
                    startUpgrade(R.raw.ea10b_yb_03h_rotate);
                    isUpgrade = false;
                } else {
                    ToastUtil.showShort(DfuUpgradeActivity.this, getString(R.string.upgrading));
                }
                break;
            case R.id.btn_volume_upgrade:
                if(isUpgrade) {
                    startUpgrade(R.raw.ea10b_yb_03h_volume);
                    isUpgrade = false;
                } else {
                    ToastUtil.showShort(DfuUpgradeActivity.this, getString(R.string.upgrading));
                }
                break;
            case R.id.btn_read_config:
                if(dataManager.device == null || dataManager.device.isConnected){
                    ToastUtil.showShort(DfuUpgradeActivity.this,"设备未连接");
                }
                EventBus.getDefault().post(new CmdSendEvent(CmdFlag.CMD_READ_CONFIG,null));
                break;
        }
    }



    /**
     * 将asset文件写入缓存
     */
    private boolean copyAssetAndWrite(String fileName){
        try {
            String pathDir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Upgrade" + File.separator + "Control";
            File fileDir = new File(pathDir);
            if (!fileDir.exists()){
                fileDir.mkdirs();
            }
            File outFile = new File(fileDir,fileName);
            if (!outFile.exists()){
                boolean res = outFile.createNewFile();
                if (!res){
                    return false;
                }
            }else {
                if (outFile.length()>10){      // 表示已经写入一次
                    return true;
                }
            }
            InputStream is=getAssets().open(fileName);
            FileOutputStream fos = new FileOutputStream(outFile);
            byte[] buffer = new byte[1024];
            int byteCount;
            while ((byteCount = is.read(buffer)) != -1) {
                fos.write(buffer, 0, byteCount);
            }
            fos.flush();
            is.close();
            fos.close();
            return true;
        } catch (IOException e) {
            LogUtils.i(TAG,e.getMessage());
            e.printStackTrace();
        }
        return false;
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            disConnectDevice();
            finish();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 断开连接操作
     */
    private void disConnectDevice(){
        MyApplication.isCon = false;
        EventBus.getDefault().post(new BleDevRequestConEvent(BleDevRequestConEvent.CODE_REQUEST_DISCONNECT,null));
        DataManager.getInstance().device = null;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        disConnectDevice();

        DfuServiceListenerHelper.unregisterProgressListener(DfuUpgradeActivity.this, mDfuProgressListener);
    }

    /**
     * 连接结果
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConEvent(BleDevRequestConEvent event){
        switch (event.code){
            case BleDevRequestConEvent.CODE_RESULT_CONNECT:
                // 连接成功
                tvDevice.setText(dataManager.device.device.getName());
                tvConnState.setText(R.string.connected);
                tvConnState.setBackgroundResource(R.drawable.bg_dialog_confirm);
                MyApplication.isCon = true;
                break;
            case BleDevRequestConEvent.CODE_RESULT_CONNECT_FAIL:   // 连接失败
            case BleDevRequestConEvent.CODE_RESULT_DISCONNECT:    // 断开连接
                tvConnState.setText(R.string.not_connect);
                tvDevice.setText("--");
                tvConnState.setBackgroundResource(R.drawable.bg_dialog_cancel);
                dataManager.device = null;
                MyApplication.isCon = false;
                break;
            case BleDevRequestConEvent.CODE_RESULT_DISCONNECT_FAIL:
                // 断开连接失败
                tvConnState.setText(R.string.connected);
                tvDevice.setText(dataManager.device.device.getName());
                tvConnState.setBackgroundResource(R.drawable.bg_dialog_confirm);
                MyApplication.isCon = true;
                break;
            case BleDevRequestConEvent.CODE_RESULT_TIME_OUT:
                LogUtils.i(TAG,"连接超时");
                tvConnState.setText(R.string.not_connect);
                tvDevice.setText("--");
                tvConnState.setBackgroundResource(R.drawable.bg_dialog_cancel);
                dataManager.device = null;
                MyApplication.isCon = false;
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReConnEvent(BleServiceStatus event) {
        switch (event.code) {
            case BleServiceStatus.BLUE_SERVER_STOP:
                tvConnState.setText(R.string.not_connect);
                tvDevice.setText("--");
                tvConnState.setBackgroundResource(R.drawable.bg_dialog_cancel);
                dataManager.device = null;
                MyApplication.isCon = false;
                break;
            case BleServiceStatus.BLUE_CANCEL_CONNECT:
                tvConnState.setText(R.string.not_connect);
                tvDevice.setText("--");
                tvConnState.setBackgroundResource(R.drawable.bg_dialog_cancel);
                dataManager.device = null;
                MyApplication.isCon = false;
                break;
        }
    }


    /**
     * 发送结果
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResultData(CmdResultEvent event) {
        if (event == null) {
            return;
        }
        if (!event.isSuccess) {
            LogUtils.i(TAG,"发送失败");
            tvControlUpdate.setText(R.string.send_fail);
        }
    }

    /**
     * 接收到指令
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRevCmdEvent(CmdRevEvent event){
        if(event == null){
            return;
        }
        RevResult revResult = event.object;
        if(revResult.result == RevResult.SUCCESS){
            dealResultData(revResult);
        }else {
            tvControlUpdate.setText(revResult.errMsg);
            LogUtils.i(TAG,"应答数据错误: " + revResult.errMsg);
        }
    }

    /**
     * 处理数据
     * @param revResult
     */
    private void dealResultData(RevResult revResult) {
        switch (revResult.cmd){
            case CmdFlag.CMD_READ_CONFIG:
                dataManager.setConfigInfo((ReportInfo) revResult.object);
                ReportInfo reportInfo = (ReportInfo) revResult.object;
                ViewUtils.showView(rlVersion);
                tvMeterSoftVersion.setText(String.format(Locale.US,getString(R.string.yb_soft_version), reportInfo.versionInfo.bleSoftVersion));
                tvMeterHardwareVersion.setText(String.format(Locale.US,getString(R.string.yb_ware_version), reportInfo.versionInfo.bleWareVersion));
                tvControlSoftVersion.setText(String.format(Locale.US,getString(R.string.control_soft_version), reportInfo.versionInfo.controlSoftVersion));
                tvControlHardwareVersion.setText(String.format(Locale.US,getString(R.string.control_ware_version), reportInfo.versionInfo.controlWareVersion));

                /*if(reportInfo.versionInfo.controlSoftVersion.equals("43")){
                    ViewUtils.hideView(btnUpgrade);
                    tvControlUpdate.setText(String.format(Locale.US,getString(R.string.control_newest), reportInfo.versionInfo.controlSoftVersion));
                    isUpgrade = false;
                }else {*/
                    btnReadConfig.setClickable(false);
                    btnReadConfig.setAlpha(0.5f);
                    tvControlUpdate.setText(String.format(Locale.US,"当前仪表版本：%s", reportInfo.versionInfo.bleSoftVersion));
                    ViewUtils.showView(btnRotateUpgrade);
                    ViewUtils.showView(btnVolumeUpgrade);
                    isUpgrade = true;
                //}
                break;
        }
    }


    private DfuServiceInitiator dfuServiceInitiator;

    /**
     * 开始升级
     */
    protected void startUpgrade(int rawId) {
        if (dataManager.device == null) {
            ToastUtil.showShort(this,"升级失败，当前没有设备连接");
            return;
        }
        // 创建dfu通知
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DfuServiceInitiator.createDfuNotificationChannel(this);
        }
        dfuServiceInitiator = new DfuServiceInitiator(dataManager.device.device.getAddress())
                .setDisableNotification(true)
                .setZip(rawId);
        dfuServiceInitiator.start(this, DfuService.class);
    }

    /**
     * 停止蓝牙固件的升级
     */
    private void stopDfu() {
        if (dfuServiceInitiator == null) {
            return;
        }
        stopService(new Intent(this, DfuService.class));
        dfuServiceInitiator = null;
    }

    private final DfuProgressListener mDfuProgressListener = new DfuProgressListener() {
        @Override
        public void onDeviceConnecting(String deviceAddress) {
            LogUtils.i(TAG,"onDeviceConnecting");
            tvControlUpdate.setText("连接中\n");
        }

        @Override
        public void onDeviceConnected(String deviceAddress) {
            LogUtils.i(TAG,"onDeviceConnected");
            tvControlUpdate.setText("已连接\n");
        }

        @Override
        public void onDfuProcessStarting(String deviceAddress) {
            LogUtils.i(TAG,"onDfuProcessStarting");
            tvControlUpdate.setText("开始升级1\n");
        }

        @Override
        public void onDfuProcessStarted(String deviceAddress) {
            LogUtils.i(TAG,"onDfuProcessStarted");
            tvControlUpdate.setText("开始升级2\n");
        }

        @Override
        public void onEnablingDfuMode(String deviceAddress) {
            LogUtils.i(TAG, "onEnablingDfuMode");
            tvControlUpdate.setText("使能dfu\n");
        }

        @Override
        public void onProgressChanged(String deviceAddress, int percent, float speed, float avgSpeed, int currentPart, int partsTotal) {
            LogUtils.i(TAG,"onProgressChanged" + percent);
            tvControlUpdate.setText(String.format(Locale.US,"升级进度：%d", percent));
        }

        @Override
        public void onFirmwareValidating(String deviceAddress) {
            LogUtils.i(TAG, "onFirmwareValidating");
            tvControlUpdate.setText("警告\n");

        }

        @Override
        public void onDeviceDisconnecting(String deviceAddress) {
            LogUtils.i(TAG, "onDeviceDisconnecting");
            tvControlUpdate.setText("连接断开\n");
        }

        @Override
        public void onDeviceDisconnected(String deviceAddress) {
            LogUtils.i(TAG,"onDeviceDisconnected");
            tvControlUpdate.setText("连接已断开\n");
        }

        @Override
        public void onDfuCompleted(String deviceAddress) {
            LogUtils.i(TAG, "onDfuCompleted");
            tvControlUpdate.setText("升级完成\n");
            stopDfu();
            ViewUtils.hideView(btnRotateUpgrade);
            ViewUtils.hideView(btnVolumeUpgrade);
            btnReadConfig.setClickable(true);
            btnReadConfig.setAlpha(1.0f);
        }

        @Override
        public void onDfuAborted(String deviceAddress) {
            LogUtils.i(TAG, "onDfuAborted");
            //升级流产，失败
            tvControlUpdate.setText("升级流产，请重新升级。\n");
        }

        @Override
        public void onError(String deviceAddress, int error, int errorType, String message) {
            LogUtils.i(TAG,"onError");
            stopDfu();
            tvControlUpdate.setText(String.format(Locale.US,"升级失败，请重新升级。%s", message));
        }
    };



}