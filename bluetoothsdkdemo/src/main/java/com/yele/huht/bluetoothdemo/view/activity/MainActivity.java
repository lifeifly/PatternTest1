package com.yele.huht.bluetoothdemo.view.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yele.huht.bluetoothdemo.MyApplication;
import com.yele.huht.bluetoothdemo.R;
import com.yele.baseapp.view.activity.BasePerActivity;
import com.yele.huht.bluetoothdemo.util.FileUtils;
import com.yele.huht.bluetoothdemo.view.adapter.AdapterConfigList;
import com.yele.huht.bluetoothdemo.view.dialog.DialogCarOutConfig;
import com.yele.huht.bluetoothdemo.view.dialog.DialogDeviceRunReport;
import com.yele.huht.bluetoothdemo.view.dialog.DialogOutMode;
import com.yele.huht.bluetoothsdklib.BleManage;
import com.yele.huht.bluetoothsdklib.bean.CarRunReport;
import com.yele.huht.bluetoothsdklib.bean.ErrorInfo;
import com.yele.huht.bluetoothsdklib.bean.InitInfo;
import com.yele.huht.bluetoothsdklib.bean.LockStateInfo;
import com.yele.huht.bluetoothsdklib.callBcak.OnCmdErrorCode;
import com.yele.huht.bluetoothsdklib.callBcak.OnCmdInitInfoResult;
import com.yele.huht.bluetoothsdklib.callBcak.OnCmdLockState;
import com.yele.huht.bluetoothsdklib.callBcak.OnCmdReport;
import com.yele.huht.bluetoothsdklib.callBcak.OnCmdResult;
import com.yele.huht.bluetoothsdklib.callBcak.OnDisConnectDevState;
import com.yele.huht.bluetoothsdklib.callBcak.OnUpdateResult;
import com.yele.huht.bluetoothsdklib.data.LockStateEnum;
import com.yele.huht.bluetoothsdklib.data.StateEnum;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MainActivity extends BasePerActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    @Override
    protected int getResId() {
        return R.layout.activity_main;
    }

    // bar
    private ImageView ivBack;
    private TextView tvTitle,tvConfirm;

    private TextView tvRev,tvResult,tvReport,tvBleUpdate,tvControlUpdate;

    private CheckBox cbPwd,cbBleSwitch;

    private TextView tvDevice,tvConnState;
    private RecyclerView rvCmd;
    private AdapterConfigList adapter;
    private List<String> mList;

    // 是否开始成品测试
    private boolean isStartTest = false;

    //定义 请求返回码
    public static final int IMPORT_REQUEST_CODE = 10005;
    public static final int IMPORT_REQUEST_CODE_BLE = 10006;

    private InitInfo info;

    @Override
    protected void findView() {
        ivBack = findViewById(R.id.iv_back);
        tvTitle = findViewById(R.id.tv_title);
        tvConfirm = findViewById(R.id.tv_confirm);
        tvRev = findViewById(R.id.tv_rev);
        tvResult = findViewById(R.id.tv_result);
        tvBleUpdate = findViewById(R.id.tv_ble_update);
        tvControlUpdate = findViewById(R.id.tv_control_update);
        cbPwd = findViewById(R.id.cb_pwd);
        tvDevice = findViewById(R.id.tv_device);
        tvConnState = findViewById(R.id.tv_conn_state);
        rvCmd = findViewById(R.id.rv_cmd);
        cbBleSwitch = findViewById(R.id.cb_ble_switch);
        tvReport = findViewById(R.id.tv_report);
    }

    public static final String[] CMD_STR = new String[]{
            "正常-读取所有配置信息","测试-蓝牙参数配置","测试-车辆参数配置","测试-开始成品测试",
            "正常-模式切换-正常模式","正常-模式切换-测试模式","正常-模式切换-恢复出厂",
            "正常-寻车指令",
            "正常-车辆锁控制-全开","正常-车辆锁控制-全锁",/*"正常模式-车辆控制详细各个锁"*/"",
            "正常-骑行参数配置",
            "正常-LED控制-关闭","正常-LED控制-打开",
            "正常-车辆指令密码修改","正常-修改蓝牙名称",
            "修改开机模式-掉电/上电","修改开机模式-休眠/唤醒",
            "定速巡航-开","定速巡航-关",
            "锁车模式-开","锁车模式-关",
            "助力启动","无助力启动",
            "蓝牙本地升级","控制器本地升级",
            "测试-出厂车辆参数配置", "测试-开启学习模式","测试-取消/停止学习模式","测试-写码",
            "正常-出厂模式","正常-报警器控制-开","正常-报警器控制-关","正常-开始自检","正常-退出自检",
            "状态信息查询","普通数据上报","测试数据上报"
    };

    private DialogDeviceRunReport dialogShowRun;

    /**
     * 上报弹窗
     */
    public void dialogReport() {
        if (dialogShowRun != null && dialogShowRun.isShowing()) {
            dialogShowRun.dismiss();
        }
        dialogShowRun = new DialogDeviceRunReport(this);
        dialogShowRun.setCanceledOnTouchOutside(false);
        dialogShowRun.show();
    }

    @Override
    protected void initData() {
        if(mList == null){
            mList = new ArrayList<>();
        }

        mList.addAll(Arrays.asList(CMD_STR));

        if(adapter == null){
            adapter = new AdapterConfigList(MainActivity.this,mList);
            rvCmd.setLayoutManager(new LinearLayoutManager(MainActivity.this,LinearLayoutManager.VERTICAL,false));
            rvCmd.setAdapter(adapter);
            adapter.setOnDevListener(new AdapterConfigList.OnDevListener() {
                @Override
                public void clickDev(int id) {
                    sendCmdStr(id);
                }
            });
        }

        // 上报数据
        BleManage.getInstance().getReportConfigRev(new OnCmdReport() {
            @Override
            public void CmdReportEvent(ErrorInfo error, CarRunReport carRunState) {
                if (error == null){
                    tvReport.setText(carRunState.toString());
                    if (dialogShowRun != null && dialogShowRun.isShowing()) {
                        dialogShowRun.refreshInfo(carRunState);
                    }
                }else {
                    tvResult.setText(error.toString());
                }
            }
        });

        // 错误码信息
        BleManage.getInstance().getErrorCodeInfo(new OnCmdErrorCode() {
            @Override
            public void CmdErrorCode(boolean hasSuccess, String code, ErrorInfo error) {
                if(!hasSuccess){
                    tvResult.setText(error.toString());
                }else {
                    tvRev.setText(code);
                    if (dialogShowRun != null && dialogShowRun.isShowing()) {
                        dialogShowRun.showErrInfo(code);
                    }
                }
            }
        });
    }

    @Override
    protected void initView() {
        ivBack.setOnClickListener(this);
        tvConfirm.setOnClickListener(this);

        tvTitle.setText("指令控制列表");
        tvConfirm.setText("上报");

        freshConnStatus();
    }

    /**
     * 更新连接状态
     */
    private void freshConnStatus() {
        if (MyApplication.curDevice == null) {
            tvConnState.setText("未连接");
            tvDevice.setText("--");
            tvConnState.setBackgroundResource(R.drawable.bg_dialog_cancel);
            MyApplication.isCon = false;
        } else {
            tvDevice.setText(MyApplication.curDevice.device.getName());
            tvConnState.setText("已连接");
            tvConnState.setBackgroundResource(R.drawable.bg_dialog_confirm);
            MyApplication.isCon = true;
        }
    }

    @Override
    public void onClick(View view) {
        freshConnStatus();
        switch (view.getId()){
            case R.id.iv_back:
                // 断开连接
                disConnect();
                finish();
                break;
            case R.id.tv_confirm:
                dialogReport();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            disConnect();
            finish();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 断开连接
     */
    private void disConnect() {
        BleManage.getInstance().deviceDisConnect(new OnDisConnectDevState() {
            @Override
            public void disConnectState(boolean state) {
                if(state){
                    MyApplication.isCon = false;
                    MyApplication.curDevice = null;
                }
                freshConnStatus();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        disConnect();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMPORT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                if (uri != null) {
                    String path = FileUtils.getPath(this, uri);
                    if (path != null) {
                        File file = new File(path);
                        if (file.exists()) {
                            String upLoadFilePath = file.toString();
                            String upLoadFileName = file.getName();
                            BleManage.getInstance().setUpgrade(1, upLoadFilePath, new OnUpdateResult() {
                                @Override
                                public void updateResult(boolean hasSuccess, Object object) {
                                    if(hasSuccess){
                                        tvControlUpdate.setText(object.toString());
                                    }else {
                                        tvControlUpdate.setText(object.toString());
                                    }
                                }
                            });
                        }
                    }
                }
            }
        }else if(requestCode == IMPORT_REQUEST_CODE_BLE){
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                if (uri != null) {
                    String path = FileUtils.getPath(this, uri);
                    if (path != null) {
                        File file = new File(path);
                        if (file.exists()) {
                            String upLoadFilePath = file.toString();
                            String upLoadFileName = file.getName();

                            BleManage.getInstance().setUpgrade(0, upLoadFilePath, new OnUpdateResult() {
                                @Override
                                public void updateResult(boolean hasSuccess, Object object) {
                                    if(hasSuccess){
                                        tvBleUpdate.setText(object.toString());
                                    }else {
                                        tvBleUpdate.setText(object.toString());
                                    }
                                }
                            });
                        }
                    }
                }
            }
        }
    }

    private DialogOutMode dialogOutMode;
    /**
     * 出厂模式
     */
    private void showDialogOutMode() {
        if (dialogOutMode != null && dialogOutMode.isShowing()) {
            dialogOutMode.dismiss();
        }

        dialogOutMode = new DialogOutMode(MainActivity.this);
        dialogOutMode.setCanceledOnTouchOutside(false);
        dialogOutMode.show();
    }

    /**
     * 测试-写码
     */
    private void showDialogWriteCode() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        final EditText editText = new EditText(this);
        builder.setTitle("写入电池码")
                .setView(editText).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String name = editText.getText().toString();
                /*CarConfig.batteryCode = name;
                EventBus.getDefault().post(new CmdSendEvent(CmdFlag.CMD_INPUT_CODE));*/
            }
        }).create().show();
    }

    private DialogCarOutConfig dialogCarOutConfig;
    /**
     * 测试-出厂车辆参数配置
     */
    private void showDialogCarOutConfig() {
        if (dialogCarOutConfig != null && dialogCarOutConfig.isShowing()) {
            dialogCarOutConfig.dismiss();
        }
        dialogCarOutConfig = new DialogCarOutConfig(MainActivity.this);
        dialogCarOutConfig.setCanceledOnTouchOutside(false);
        dialogCarOutConfig.setOnClickListener(new DialogCarOutConfig.onClickListener() {
            @Override
            public void onClickListener() {
                /*if(!cbBleSwitch.isChecked()){
                    EventBus.getDefault().post(new CmdSendEvent(CmdFlag.CMD_OUT_CAR_CONFIG));
                }else {
                    EventBus.getDefault().post(new CmdSendEvent(CmdFlag.CMD_OUT_CAR_CONFIG_BLE_SWITCH));
                }*/
            }
        });
        dialogCarOutConfig.show();
    }

    /**
     * 蓝牙参数
     */
    private void showBleConfigDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_ble_config,null);

        final EditText etMaxSpeed = view.findViewById(R.id.et_max_speed);
        final EditText etAddSpeedMode = view.findViewById(R.id.et_add_speed_mode);
        final EditText etShow = view.findViewById(R.id.et_show);
        final EditText etReportTime = view.findViewById(R.id.et_report_time);
        final EditText etCloseTime= view.findViewById(R.id.et_close_time);
        final EditText etMaxDeplay = view.findViewById(R.id.et_max_delay);

        etMaxSpeed.setText(info.pwd);
        etAddSpeedMode.setText(String.format(Locale.US,"%d", info.broadcastSpace));
        etShow.setText(info.SN);
        etReportTime.setText(String.format(Locale.US,"%d", info.broadcastTime));
        etCloseTime.setText(String.format(Locale.US,"%d", info.minConnectSpace));
        etMaxDeplay.setText(String.format(Locale.US,"%d", info.maxConnectSpace));

        builder.setView(view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int addSpeedMode = Integer.parseInt(etAddSpeedMode.getText().toString());
                        int showModel = Integer.parseInt(etMaxDeplay.getText().toString());
                        int reportTime = Integer.parseInt(etReportTime.getText().toString());
                        int closeTime = Integer.parseInt(etCloseTime.getText().toString());

                        // 测试模式-蓝牙参数配置
                        BleManage.getInstance().setConfigBle(etMaxSpeed.getText().toString(),
                                etShow.getText().toString(), addSpeedMode, reportTime, closeTime, showModel, new OnCmdResult() {
                            @Override
                            public void CmdResultEvent(boolean hasSuccess, ErrorInfo error) {
                                if(hasSuccess){
                                    tvResult.setText("蓝牙参数配置成功");
                                }else {
                                    tvResult.setText(error.toString());
                                }
                            }
                        });
                    }
                }).create().show();
    }

    /**
     * 车辆参数
     */
    private void showCarConfigDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_car_config,null);

        final EditText etShow = view.findViewById(R.id.et_show);
        final EditText etReportTime = view.findViewById(R.id.et_report_time);

        etShow.setText(info.carSn);
        etReportTime.setText(info.typeName);

        builder.setView(view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // 测试模式-车辆参数配置
                        BleManage.getInstance().setConfigCar(etShow.getText().toString(), etReportTime.getText().toString(), new OnCmdResult() {
                            @Override
                            public void CmdResultEvent(boolean hasSuccess, ErrorInfo error) {
                                if(hasSuccess){
                                    tvResult.setText("蓝牙参数配置成功");
                                }else {
                                    tvResult.setText(error.toString());
                                }
                            }
                        });
                    }
                }).create().show();
    }


    /**
     * 骑行参数
     */
    private void showRideBikeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_ride_config,null);

        final EditText etMaxSpeed = view.findViewById(R.id.et_max_speed);
        final EditText etAddSpeedMode = view.findViewById(R.id.et_add_speed_mode);
        final EditText etShow = view.findViewById(R.id.et_show);
        final EditText etReportTime = view.findViewById(R.id.et_report_time);
        final EditText etCloseTime= view.findViewById(R.id.et_close_time);

        etMaxSpeed.setText(info.MaxSpeed + "");
        etAddSpeedMode.setText(info.addMode + "");
        etShow.setText(info.showMode + "");
        etReportTime.setText(info.reportSpace + "");
        etCloseTime.setText(info.standbyTime + "");

        builder.setView(view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        int maxSpeed = Integer.parseInt(etMaxSpeed.getText().toString());
                        int addSpeedMode = Integer.parseInt(etAddSpeedMode.getText().toString());
                        int showModel = Integer.parseInt(etShow.getText().toString());
                        int reportTime = Integer.parseInt(etReportTime.getText().toString());
                        int closeTime = Integer.parseInt(etCloseTime.getText().toString());
                        BleManage.getInstance().setNormalBikeConfig(maxSpeed, addSpeedMode, showModel, reportTime, closeTime, new OnCmdResult() {
                            @Override
                            public void CmdResultEvent(boolean hasSuccess, ErrorInfo error) {
                                if(hasSuccess){
                                    tvResult.setText("蓝牙参数配置成功");
                                }else {
                                    tvResult.setText(error.toString());
                                }
                            }
                        });
                    }
                }).create().show();
    }

    /**
     * 修改蓝牙名称
     */
    private void showDialogBleNameChange() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        final EditText editText = new EditText(this);
        builder.setTitle("输入名称")
                .setView(editText).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String name = editText.getText().toString();

                // 正常模式-修改蓝牙名称
                BleManage.getInstance().setChangeBleName(name, new OnCmdResult() {
                    @Override
                    public void CmdResultEvent(boolean hasSuccess, ErrorInfo error) {
                        if(hasSuccess){
                            tvResult.setText("修改蓝牙名称成功");
                        }else {
                            tvResult.setText(error.toString());
                        }
                    }
                });
            }
        }).create().show();
    }

    /**
     * 修改密码
     */
    private void showDialogChangePwd() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_change_password,null);

        final EditText etOldPwd = view.findViewById(R.id.et_old_pwd);
        final EditText etNewPwd = view.findViewById(R.id.et_new_pwd);
        etOldPwd.setText(info.pwd);
        etOldPwd.setEnabled(false);

        builder.setTitle("输入密码")
                .setView(view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String oldPwd = etOldPwd.getText().toString();
                        String newPwd = etNewPwd.getText().toString();

                        // 正常模式-车辆指令密码修改
                        BleManage.getInstance().setChangePassword(oldPwd, newPwd, info.carSn, new OnCmdResult() {
                            @Override
                            public void CmdResultEvent(boolean hasSuccess, ErrorInfo error) {
                                if(hasSuccess){
                                    tvResult.setText("修改密码成功");
                                }else {
                                    tvResult.setText(error.toString());
                                }
                            }
                        });
                    }
                }).create().show();
    }

    /**
     * 读取配置信息
     */
    private void showDialogReadConfig() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        final EditText editText = new EditText(this);
        editText.setText("OKAIYLBT");
        builder.setTitle("输入密码")
                .setView(editText)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final String pwd = editText.getText().toString();
                        // 初始化设备 / 读取车辆配置信息接口
                        BleManage.getInstance().setReadConfig(pwd, new OnCmdInitInfoResult() {
                            @Override
                            public void CmdInitInfo(ErrorInfo error, InitInfo initInfo) {
                                if(initInfo == null && error != null){
                                    tvResult.setText(error.toString());
                                }else {
                                    tvResult.setText("读取配置成功");
                                    tvRev.setText(initInfo.toString());
                                    info = initInfo;
                                }
                            }
                        });
                    }
                }).create().show();
    }


//    /**
//     * 处理数据
//     * @param revResult
//     */
//    private void dealResultData(BleService.RevResult revResult) {
//        switch (revResult.cmd){
//            case CmdFlag.CMD_OUT_CAR_CONFIG:
//                tvResult.setText("出厂车辆参数配置成功");
//                break;
//            case CmdFlag.CMD_STUDY:
//                tvResult.setText("学习模式配置成功");
//                break;
//            case CmdFlag.CMD_INPUT_CODE:
//                tvResult.setText("写码成功");
//                break;
//            case CmdFlag.CMD_OUT_MODE:
//                tvResult.setText("出厂模式配置成功");
//                break;
//            case CmdFlag.CMD_WARN_CONTROL:
//                tvResult.setText("报警器控制成功");
//                break;
//            case CmdFlag.CMD_CHECK_MODE:
//                if(YBConfig.hasStartCheck == 0) {
//                    if (YBConfig.checkParts == 0) {
//                        tvResult.append("开始检测电量区-速度区结果：" + YBConfig.checkResult + "\n");
//                        YBConfig.hasStartCheck = 0;  // 开始自检
//                        YBConfig.checkParts = 1;   // 速度区
//                        EventBus.getDefault().post(new CmdSendEvent(CmdFlag.CMD_CHECK_MODE));
//                    } else if (YBConfig.checkParts == 1) {
//                        tvResult.append("开始检测功能区-电量区结果：" + YBConfig.checkResult + "\n");
//                        YBConfig.hasStartCheck = 0;  // 开始自检
//                        YBConfig.checkParts = 2;   // 速度区
//                        EventBus.getDefault().post(new CmdSendEvent(CmdFlag.CMD_CHECK_MODE));
//                    } else if (YBConfig.checkParts == 2) {
//                        tvResult.append("开始全关-功能区结果：" + YBConfig.checkResult + "\n");
//                        YBConfig.hasStartCheck = 0;  // 开始自检
//                        YBConfig.checkParts = 3;   // 速度区
//                        EventBus.getDefault().post(new CmdSendEvent(CmdFlag.CMD_CHECK_MODE));
//                    } else if (YBConfig.checkParts == 3) {
//                        tvResult.append("全关结果：" + YBConfig.checkResult + "\n");
//                    }
//                }else {
//                    tvResult.setText("退出自检");
//                }
//                break;
//        }
//    }


    /**
     * 下发指令
     * @param id
     */
    private void sendCmdStr(int id) {
        if(!MyApplication.isCon){
            tvResult.setText("蓝牙未连接，无法操作指令");
            return;
        }
        tvRev.setText("");
        tvResult.setText("");
        switch (id){
            case 0:
                showDialogReadConfig();
                break;
            case 1:
                showBleConfigDialog();
                break;
            case 2:
                showCarConfigDialog();
                break;
            case 3:    // 测试模式-成品测试,默认点击后是取消/停止成品测试
                if(isStartTest) {
                    // 测试模式- 取消/停止成品测试
                    BleManage.getInstance().setTestProduct(StateEnum.OFF, new OnCmdResult() {
                        @Override
                        public void CmdResultEvent(boolean hasSuccess, ErrorInfo error) {
                            if(hasSuccess){
                                tvResult.setText("取消/停止成品测试成功");
                            }else {
                                tvResult.setText(error.toString());
                            }
                        }
                    });
                    isStartTest = false;
                }else {
                    // 测试模式- 开始成品测试
                    BleManage.getInstance().setTestProduct(StateEnum.ON, new OnCmdResult() {
                        @Override
                        public void CmdResultEvent(boolean hasSuccess, ErrorInfo error) {
                            if(hasSuccess){
                                tvResult.setText("开始成品测试成功");
                            }else {
                                tvResult.setText(error.toString());
                            }
                        }
                    });
                    isStartTest = true;
                }
                break;
            case 4:
                // 切换模式  0：正常模式，1：测试模式，2：恢复出厂设置
//                DebugFlag.debugMode = cbPwd.isChecked();
                BleManage.getInstance().setNormalChangeMode(0, new OnCmdResult() {
                    @Override
                    public void CmdResultEvent(boolean hasSuccess, ErrorInfo error) {
                        if(hasSuccess){
                            tvResult.setText("切换正常模式成功");
                        }else {
                            tvResult.setText(error.toString());
                        }
                    }
                });
                break;
            case 5:
                // 切换模式  0：正常模式，1：测试模式，2：恢复出厂设置
//                DebugFlag.debugMode = cbPwd.isChecked();
                BleManage.getInstance().setNormalChangeMode(1, new OnCmdResult() {
                    @Override
                    public void CmdResultEvent(boolean hasSuccess, ErrorInfo error) {
                        if(hasSuccess){
                            tvResult.setText("切换测试模式成功");
                        }else {
                            tvResult.setText(error.toString());
                        }
                    }
                });
                break;
            case 6:
                // 切换模式  0：正常模式，1：测试模式，2：恢复出厂设置
//                DebugFlag.debugMode = cbPwd.isChecked();
                BleManage.getInstance().setNormalChangeMode(2, new OnCmdResult() {
                    @Override
                    public void CmdResultEvent(boolean hasSuccess, ErrorInfo error) {
                        if(hasSuccess){
                            tvResult.setText("恢复出厂设置成功");
                        }else {
                            tvResult.setText(error.toString());
                        }
                    }
                });
                break;
            case 7:
                // 正常模式-寻车
                BleManage.getInstance().setSearchCar(new OnCmdResult() {
                    @Override
                    public void CmdResultEvent(boolean hasSuccess, ErrorInfo error) {
                        if(hasSuccess){
                            tvResult.setText("寻车成功");
                        }else {
                            tvResult.setText(error.toString());
                        }
                    }
                });
                break;
            case 8:
                // 正常模式-车辆控制总开
                BleManage.getInstance().setAllLockCar(LockStateEnum.UN_LOCK, new OnCmdLockState() {
                    @Override
                    public void CmdLockState(ErrorInfo error, LockStateInfo stateInfo) {
                        if(stateInfo == null && error != null){
                            tvResult.setText(error.toString());
                        }else {
                            tvResult.setText("车辆控制总开成功");
                            tvRev.setText(stateInfo.toString());
                        }
                    }
                });
                break;
            case 9:
                // 正常模式-车辆控制总关
                BleManage.getInstance().setAllLockCar(LockStateEnum.LOCK, new OnCmdLockState() {
                    @Override
                    public void CmdLockState(ErrorInfo error, LockStateInfo stateInfo) {
                        if(stateInfo == null && error != null){
                            tvResult.setText(error.toString());
                        }else {
                            tvResult.setText("车辆控制总关成功");
                            tvRev.setText(stateInfo.toString());
                        }
                    }
                });
                break;
            /*case 10:
                // 正常模式-车辆控制详细各个锁
                showDialogLockTypeSelect();
                break;*/
            case 11:
                // 骑行参数 speedMode 0：柔和模式，1：运动模式  showModel 0：YD,1：KM
                showRideBikeDialog();
                break;
            case 12:
                // 正常模式-LED控制 1：大灯开，0：大灯关
                BleManage.getInstance().setLedControl(0, new OnCmdResult() {
                    @Override
                    public void CmdResultEvent(boolean hasSuccess, ErrorInfo error) {
                        if(hasSuccess){
                            tvResult.setText("大灯关成功");
                        }else {
                            tvResult.setText(error.toString());
                        }
                    }
                });
                break;
            case 13:
                // 正常模式-LED控制 1：大灯开，0：大灯关
                BleManage.getInstance().setLedControl(1, new OnCmdResult() {
                    @Override
                    public void CmdResultEvent(boolean hasSuccess, ErrorInfo error) {
                        if(hasSuccess){
                            tvResult.setText("大灯开成功");
                        }else {
                            tvResult.setText(error.toString());
                        }
                    }
                });
                break;
            case 14:
                // 修改密码
                showDialogChangePwd();
                break;
            case 15:
                // 修改蓝牙名称
                showDialogBleNameChange();
                break;
            case 16:
                // 修改开关机模式 1：开关机模式修改为芯片掉电/上电模式，蓝牙广播停止
                BleManage.getInstance().setOpenCar(1, new OnCmdResult() {
                    @Override
                    public void CmdResultEvent(boolean hasSuccess, ErrorInfo error) {
                        if(hasSuccess){
                            tvResult.setText("芯片掉电/上电模式成功");
                        }else {
                            tvResult.setText(error.toString());
                        }
                    }
                });
                break;
            case 17:
                BleManage.getInstance().setOpenCar(0, new OnCmdResult() {
                    @Override
                    public void CmdResultEvent(boolean hasSuccess, ErrorInfo error) {
                        if(hasSuccess){
                            tvResult.setText("芯片休眠/唤醒模式成功");
                        }else {
                            tvResult.setText(error.toString());
                        }
                    }
                });
                break;
            case 18:
                // 开关定速巡航
                BleManage.getInstance().setCarCruise(1, new OnCmdResult() {
                    @Override
                    public void CmdResultEvent(boolean hasSuccess, ErrorInfo error) {
                        if(hasSuccess){
                            tvResult.setText("开定速巡航成功");
                        }else {
                            tvResult.setText(error.toString());
                        }
                    }
                });
                break;
            case 19:
                BleManage.getInstance().setCarCruise(0, new OnCmdResult() {
                    @Override
                    public void CmdResultEvent(boolean hasSuccess, ErrorInfo error) {
                        if(hasSuccess){
                            tvResult.setText("关定速巡航成功");
                        }else {
                            tvResult.setText(error.toString());
                        }
                    }
                });
                break;
            case 20:
                // 开关车辆锁车模式
                BleManage.getInstance().setCarLockMode(1, new OnCmdResult() {
                    @Override
                    public void CmdResultEvent(boolean hasSuccess, ErrorInfo error) {
                        if(hasSuccess){
                            tvResult.setText("开车辆锁车模式成功");
                        }else {
                            tvResult.setText(error.toString());
                        }
                    }
                });
                break;
            case 21:
                BleManage.getInstance().setCarLockMode(0, new OnCmdResult() {
                    @Override
                    public void CmdResultEvent(boolean hasSuccess, ErrorInfo error) {
                        if(hasSuccess){
                            tvResult.setText("关车辆锁车模式成功");
                        }else {
                            tvResult.setText(error.toString());
                        }
                    }
                });
                break;
            case 22:
                // 切换车辆启动模式
                BleManage.getInstance().setCarOpenMode(0, new OnCmdResult() {
                    @Override
                    public void CmdResultEvent(boolean hasSuccess, ErrorInfo error) {
                        if(hasSuccess){
                            tvResult.setText("车辆助力启动模式成功");
                        }else {
                            tvResult.setText(error.toString());
                        }
                    }
                });
                break;
            case 23:
                BleManage.getInstance().setCarOpenMode(1, new OnCmdResult() {
                    @Override
                    public void CmdResultEvent(boolean hasSuccess, ErrorInfo error) {
                        if(hasSuccess){
                            tvResult.setText("车辆无助力启动模式成功");
                        }else {
                            tvResult.setText(error.toString());
                        }
                    }
                });
                break;
            case 24:
                // 仪表本地升级
                tvBleUpdate.setText("");
                Intent intent1 = new Intent(Intent.ACTION_GET_CONTENT);
                intent1.setType("*/*");//设置类型，我这里是任意类型，可以过滤文件类型
                intent1.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent1,IMPORT_REQUEST_CODE_BLE);

//                String path = FileManager.UPGRADE_DIR + "ES200BT_12.zip";
                break;
            case 25:
                // 控制器本地升级
                tvControlUpdate.setText("");
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");//设置类型，我这里是任意类型，可以过滤文件类型
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent,IMPORT_REQUEST_CODE);
                break;
            case 26:
                // 测试-出厂车辆参数配置
                showDialogCarOutConfig();
                break;
            /*case 27:
                // "测试-开启学习模式
                CarConfig.studyMode = 1;
                EventBus.getDefault().post(new CmdSendEvent(CmdFlag.CMD_STUDY));
                break;
            case 28:
                // 测试-取消/停止学习模式
                CarConfig.studyMode = 0;
                EventBus.getDefault().post(new CmdSendEvent(CmdFlag.CMD_STUDY));
                break;
            case 29:
                // 测试-写码
                showDialogWriteCode();
                break;
            case 30:
                // 正常-出厂模式
                showDialogOutMode();
                break;
            case 31:
                // 正常-报警器控制-开
                CarConfig.warnControl = 1;
                EventBus.getDefault().post(new CmdSendEvent(CmdFlag.CMD_WARN_CONTROL));
                break;
            case 32:
                // 正常-报警器控制-关
                CarConfig.warnControl = 0;
                EventBus.getDefault().post(new CmdSendEvent(CmdFlag.CMD_WARN_CONTROL));
                break;
            case 33:
                // 正常-开始自检模式控制
                tvResult.setText("开始自检-速度区" + "\n");
                YBConfig.hasStartCheck = 0;  // 开始自检
                YBConfig.checkParts = 0;   // 速度区
                EventBus.getDefault().post(new CmdSendEvent(CmdFlag.CMD_CHECK_MODE));
                break;
            case 34:
                // 正常-停止自检
                YBConfig.hasStartCheck = 1;  // 开始自检
                YBConfig.checkParts = 3;   // 速度区
                EventBus.getDefault().post(new CmdSendEvent(CmdFlag.CMD_CHECK_MODE));
                break;
            case 35:
                // 滑板车状态信息查询
                EventBus.getDefault().post(new CmdSendEvent(CmdFlag.CMD_STATE_QUERY));
                break;
            case 36:
                // 普通数据上报
                YBConfig.reportMode = 0;
                EventBus.getDefault().post(new CmdSendEvent(CmdFlag.SMD_MODIFY_REPORT));
                break;
            case 37:
                // 定时数据上报
                YBConfig.reportMode = 1;
                EventBus.getDefault().post(new CmdSendEvent(CmdFlag.SMD_MODIFY_REPORT));
                break;*/
            default:
                break;

        }
    }
}