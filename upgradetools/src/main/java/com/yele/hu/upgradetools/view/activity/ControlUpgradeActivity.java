package com.yele.hu.upgradetools.view.activity;

import android.os.Environment;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yele.baseapp.utils.LogUtils;
import com.yele.baseapp.utils.StringUtils;
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
import com.yele.hu.upgradetools.policy.event.CmdResultEvent;
import com.yele.hu.upgradetools.policy.event.CmdRevEvent;
import com.yele.hu.upgradetools.policy.event.CmdSendEvent;
import com.yele.hu.upgradetools.policy.event.UpgradeActionEvent;
import com.yele.hu.upgradetools.policy.event.UpgradeResultEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;


public class ControlUpgradeActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "ControlActivity";

    @Override
    protected int getResId() {
        return R.layout.activity_control_upgrade;
    }

    private ImageView ivBack;
    private TextView tvTitle,tvConfirm,tvDevice,tvConnState;
    private TextView tvControlUpdate;
    private Button btnReadConfig;
    private Button btnUpgrade;
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
        btnUpgrade = findViewById(R.id.btn_upgrade);
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
        btnUpgrade.setOnClickListener(this);
        btnReadConfig.setOnClickListener(this);

        tvTitle.setText(R.string.upgrade_page);
        tvConfirm.setText("");

        ViewUtils.hideView(btnUpgrade);
        ViewUtils.hideView(rlVersion);

        freshConnStatus();
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
            case R.id.btn_upgrade:
                /*String upLoadFilePath = FileManager.UPGRADE_DIR + "Control_ES50C_ECU_UP_20210826_56_19.bin";
                EventBus.getDefault().post(new UpgradeActionEvent(UpgradeActionEvent.TYPE_CONTROL, UpgradeActionEvent.ACTION_START, upLoadFilePath));*/
                if(isUpgrade) {
                    String filename = "Control.bin";
                    boolean isSuccess = copyAssetAndWrite(filename);
                    LogUtils.i(TAG, "isSuccess==>" + isSuccess);
                    if (isSuccess) {
                        String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Upgrade" + File.separator + "Control";
                        File upgradeFile = new File(rootPath, filename);
                        String path = upgradeFile.getAbsolutePath();
                        if(StringUtils.isEmpty(path)){
                            ToastUtil.showShort(ControlUpgradeActivity.this, getString(R.string.file_not_exist));
                            return;
                        }
                        EventBus.getDefault().post(new UpgradeActionEvent(UpgradeActionEvent.TYPE_CONTROL, UpgradeActionEvent.ACTION_START, path));
                    } else {
                        ToastUtil.showShort(ControlUpgradeActivity.this, getString(R.string.file_not_exist));
                    }
                    isUpgrade = false;
                } else {
                    ToastUtil.showShort(ControlUpgradeActivity.this, getString(R.string.upgrading));
                }
                break;
            case R.id.btn_read_config:
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

                if(reportInfo.versionInfo.controlSoftVersion.equals("43")){
                    ViewUtils.hideView(btnUpgrade);
                    tvControlUpdate.setText(String.format(Locale.US,getString(R.string.control_newest), reportInfo.versionInfo.controlSoftVersion));
                    isUpgrade = false;
                }else {
                    btnReadConfig.setClickable(false);
                    btnReadConfig.setAlpha(0.5f);
                    tvControlUpdate.setText(String.format(Locale.US,getString(R.string.current_control_version), reportInfo.versionInfo.controlSoftVersion));
                    ViewUtils.showView(btnUpgrade);
                    isUpgrade = true;
                }
                break;
        }
    }


    /**
     * 固件升级结果
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpgradeResultEvent(UpgradeResultEvent event) {
        if (event == null) {
            return;
        }
        if (event.result == UpgradeResultEvent.SUCCESS) {
            tvControlUpdate.setText(R.string.upgrade_success);
            ViewUtils.hideView(btnUpgrade);
            btnReadConfig.setClickable(true);
            btnReadConfig.setAlpha(1.0f);
        } else if (event.result == UpgradeResultEvent.FAILED) {
            tvControlUpdate.setText(String.format(Locale.US,getString(R.string.upgrade_fail), event.msg));
        } else if (event.result == UpgradeResultEvent.UPDATE) {
            if (event.msg != null) {
                tvControlUpdate.setText(String.format(Locale.US,getString(R.string.upgrade_percent), event.percent));
            }
        }
    }


}