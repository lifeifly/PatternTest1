package com.yele.hu.blesdk520demo.view.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yele.blesdklibrary.BleManage;
import com.yele.blesdklibrary.bean.CarRunState;
import com.yele.blesdklibrary.bean.ErrorInfo;
import com.yele.blesdklibrary.bean.InitInfo;
import com.yele.blesdklibrary.data.LockStateEnum;
import com.yele.blesdklibrary.data.SwitchStatusEnum;
import com.yele.blesdklibrary.policy.receiver.BluetoothMonitorReceiver;
import com.yele.blesdklibrary.port.OnBleConnectBack;
import com.yele.blesdklibrary.port.OnCmdErrorCodeBack;
import com.yele.blesdklibrary.port.OnCmdInitInfoResultBack;
import com.yele.blesdklibrary.port.OnCmdReportBack;
import com.yele.blesdklibrary.port.OnCmdResultBack;
import com.yele.blesdklibrary.port.OnConnectDevStateBack;
import com.yele.blesdklibrary.port.OnUpdateResultBack;
import com.yele.hu.blesdk520demo.MyApplication;
import com.yele.hu.blesdk520demo.R;
import com.yele.hu.blesdk520demo.bean.AutoCmdState;
import com.yele.hu.blesdk520demo.bean.CmdFlag;
import com.yele.hu.blesdk520demo.bean.FileManager;
import com.yele.hu.blesdk520demo.util.FileUtils;
import com.yele.hu.blesdk520demo.util.LogUtils;
import com.yele.hu.blesdk520demo.view.activity.base.BaseActivity;
import com.yele.hu.blesdk520demo.view.adapter.AdapterCmdBtn;
import com.yele.hu.blesdk520demo.view.dialog.DialogReport;
import com.yele.hu.blesdk520demo.view.dialog.LoadDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ControlActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "ControlActivity";

    @Override
    protected int getResId() {
        return R.layout.activity_control;
    }

    // bar
    private ImageView ivBack;
    private TextView tvTitle,tvConfirm;

    private TextView tvRev,tvRes,tvReport,tvBleUpdate,tvControlUpdate;

    private BluetoothMonitorReceiver receiver;

    private TextView tvDevice,tvConnState;

    private RecyclerView rvBtn;
    private AdapterCmdBtn adapter;
    private List<AutoCmdState> list;

    private InitInfo info;

    @Override
    protected void findView() {
        ivBack = findViewById(R.id.iv_back);
        tvTitle = findViewById(R.id.tv_title);
        tvConfirm = findViewById(R.id.tv_confirm);
        tvRev = findViewById(R.id.tv_rev);
        tvRes = findViewById(R.id.tv_res);
        tvReport = findViewById(R.id.tv_report);
        tvBleUpdate = findViewById(R.id.tv_ble_update);
        tvControlUpdate = findViewById(R.id.tv_control_update);
        tvDevice = findViewById(R.id.tv_device);
        tvConnState = findViewById(R.id.tv_conn_state);
        rvBtn = findViewById(R.id.rv_btn);
    }

    @Override
    protected void initData() {
        BleManage.getInstance().setPassword("OKAIYLBT");
        if (list == null) {
            list = new ArrayList<>();
            for (int i = 0; i < CmdFlag.CMD_STR.length; i++) {
                list.add(new AutoCmdState(CmdFlag.CMD_STR[i]));
            }
        }
        if(adapter == null){
            adapter = new AdapterCmdBtn(ControlActivity.this,list);
            rvBtn.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
            rvBtn.setAdapter(adapter);
            adapter.setOnCmdListener(new AdapterCmdBtn.OnCmdListener() {
                @Override
                public void clickCmd(int position) {
                    initCmd(position);
                }
            });
        }

        BleManage.getInstance().setBleConnectListener(new OnBleConnectBack() {
            @Override
            public void connected() {

            }

            @Override
            public void disconnected() {
                tvConnState.setText("未连接");
                tvDevice.setText("--");
            }
        });

        /**
         * 获取车辆上报所有状态信息
         */
        BleManage.getInstance().getReportConfigRev(new OnCmdReportBack() {
            @Override
            public void CmdReportEvent(ErrorInfo error, CarRunState carRunState) {
                if (error == null){
                    tvReport.setText(carRunState.toString());

                    if (dialogShowRun != null && dialogShowRun.isShowing()) {
                        dialogShowRun.refreshInfo(carRunState);
                    }
                }else {
                    tvRes.setText(error.toString());
                }
            }
        });


        /**
         * 获取滑板车错误码信息
         */
        BleManage.getInstance().getErrorCodeInfo(new OnCmdErrorCodeBack() {
            @Override
            public void CmdErrorCode(boolean hasSuccess, String code, ErrorInfo error) {
                if(!hasSuccess){
                    tvRes.setText(error.toString());
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
        tvConfirm.setText("连接");
        if(MyApplication.curDevice == null){
            tvConnState.setText("未连接");
            tvDevice.setText("--");
        }else {
            tvDevice.setText(MyApplication.curDevice.getName());
            tvConnState.setText("已连接");
        }

        receiver = new BluetoothMonitorReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        registerReceiver(receiver, intentFilter);

    }

    //定义 请求返回码
    public static final  int IMPORT_REQUEST_CODE=10005;

    public static final  int IMPORT_REQUEST_CODE_BLE=10006;

    /**
     * 指令选择
     * @param position
     */
    private void initCmd(int position) {
        switch (position){
            case 0:
                // 读取配置
                BleManage.getInstance().setReadConfig(new OnCmdInitInfoResultBack() {
                    @Override
                    public void CmdInitInfo(ErrorInfo error, InitInfo initInfo) {
                        if(initInfo == null && error != null){
                            tvRes.setText(error.toString());
                        }else {
                            tvRes.setText("读取配置成功");
                            tvRev.setText(initInfo.toString());
                            info = initInfo;
                            LogUtils.i(TAG,initInfo.toString());
                        }
                    }
                });
                break;
            case 1:
                // 寻车指令
                BleManage.getInstance().setSearchCar(new OnCmdResultBack() {
                    @Override
                    public void CmdResultEvent(boolean hasSuccess, ErrorInfo error) {
                        if(!hasSuccess){
                            tvRes.setText(error.toString());
                        }else {
                            tvRes.setText("寻车成功");
                        }
                        tvRev.setText("");
                    }
                });
                break;
            case 2:
                // 解锁车辆
                BleManage.getInstance().setAllLockCar(LockStateEnum.UN_LOCK, new OnCmdResultBack() {
                    @Override
                    public void CmdResultEvent(boolean hasSuccess, ErrorInfo error) {
                        if(!hasSuccess){
                            tvRes.setText(error.toString());
                        }else {
                            tvRes.setText("开机成功");
                        }
                        tvRev.setText("");
                    }
                });
                break;
            case 3:
                // 锁车
                BleManage.getInstance().setAllLockCar(LockStateEnum.LOCK, new OnCmdResultBack() {
                    @Override
                    public void CmdResultEvent(boolean hasSuccess, ErrorInfo error) {
                        if(!hasSuccess){
                            tvRes.setText(error.toString());
                        }else {
                            tvRes.setText("关机成功");
                        }
                        tvRev.setText("");
                    }
                });
                break;
            case 4:
                showRideBikeDialog();    // 骑行参数
                break;
            case 5:
                showRideBikeGearDialog();
                break;
            case 6:
                /*正常模式-LED控制---0：大灯关*/
                BleManage.getInstance().setLedControl(0, new OnCmdResultBack() {
                    @Override
                    public void CmdResultEvent(boolean hasSuccess, ErrorInfo error) {
                        if(!hasSuccess){
                            tvRes.setText(error.toString());
                        }else {
                            tvRes.setText("大灯关");
                        }
                        tvRev.setText("");
                    }
                });
                break;
            case 7:
                // 关大灯
                BleManage.getInstance().setLedControl(1, new OnCmdResultBack() {
                    @Override
                    public void CmdResultEvent(boolean hasSuccess, ErrorInfo error) {
                        if(!hasSuccess){
                            tvRes.setText(error.toString());
                        }else {
                            tvRes.setText("大灯开");
                        }
                        tvRev.setText("");
                    }
                });
                break;
            case 8:
                // 修改蓝牙名称
                showDialogBleNameChange();
                break;
            case 9:
                /*休眠/唤醒模式*/
                BleManage.getInstance().setOpenCar(0, new OnCmdResultBack() {
                    @Override
                    public void CmdResultEvent(boolean b, ErrorInfo errorInfo) {
                        if(!b){
                            tvRes.setText(errorInfo.toString());
                        }else {
                            tvRes.setText("休眠/唤醒模式");
                        }
                        tvRev.setText("");
                    }
                });
                break;
            case 10:
                /*掉电/上电模式*/
                BleManage.getInstance().setOpenCar(1, new OnCmdResultBack() {
                    @Override
                    public void CmdResultEvent(boolean b, ErrorInfo errorInfo) {
                        if(!b){
                            tvRes.setText(errorInfo.toString());
                        }else {
                            tvRes.setText("掉电/上电模式");
                        }
                        tvRev.setText("");
                    }
                });
                break;
            case 11:
                // 定速巡航-开
                BleManage.getInstance().setCarCruise(1, new OnCmdResultBack() {
                    @Override
                    public void CmdResultEvent(boolean b, ErrorInfo errorInfo) {
                        if(!b){
                            tvRes.setText(errorInfo.toString());
                        }else {
                            tvRes.setText("定速-开");
                        }
                        tvRev.setText("");
                    }
                });
                break;
            case 12:
                // 定速巡航-关
                BleManage.getInstance().setCarCruise(0, new OnCmdResultBack() {
                    @Override
                    public void CmdResultEvent(boolean b, ErrorInfo errorInfo) {
                        if(!b){
                            tvRes.setText(errorInfo.toString());
                        }else {
                            tvRes.setText("定速-关");
                        }
                        tvRev.setText("");
                    }
                });
                break;
            case 13:
                // 锁车模式-是否锁定车辆-开（锁住）
                BleManage.getInstance().setCarLockMode(1, new OnCmdResultBack() {
                    @Override
                    public void CmdResultEvent(boolean b, ErrorInfo errorInfo) {
                        if(!b){
                            tvRes.setText(errorInfo.toString());
                        }else {
                            tvRes.setText("锁车-开");
                        }
                        tvRev.setText("");
                    }
                });
                break;
            case 14:
                // 锁车模式-是否锁定车辆-关（无锁模式）
                BleManage.getInstance().setCarLockMode(0, new OnCmdResultBack() {
                    @Override
                    public void CmdResultEvent(boolean b, ErrorInfo errorInfo) {
                        if(!b){
                            tvRes.setText(errorInfo.toString());
                        }else {
                            tvRes.setText("锁车-关");
                        }
                        tvRev.setText("");
                    }
                });
                break;
            case 15:
                // 无助力模式-开
                BleManage.getInstance().setCarOpenMode(0, new OnCmdResultBack() {
                    @Override
                    public void CmdResultEvent(boolean b, ErrorInfo errorInfo) {
                        if(!b){
                            tvRes.setText(errorInfo.toString());
                        }else {
                            tvRes.setText("助力启动");
                        }
                        tvRev.setText("");
                    }
                });
                break;
            case 16:
                // 无助力模式-关
                BleManage.getInstance().setCarOpenMode(1, new OnCmdResultBack() {
                    @Override
                    public void CmdResultEvent(boolean b, ErrorInfo errorInfo) {
                        if(!b){
                            tvRes.setText(errorInfo.toString());
                        }else {
                            tvRes.setText("无助力启动");
                        }
                        tvRev.setText("");
                    }
                });
                break;
            case 17:
                // 自检模式
                BleManage.getInstance().setDeviceCheckMode(1, 0, new OnCmdResultBack() {
                    @Override
                    public void CmdResultEvent(boolean hasSuccess, ErrorInfo error) {
                        if(hasSuccess){
                            tvRes.setText("成功");
                            BleManage.getInstance().setDeviceCheckMode(0, 0, new OnCmdResultBack() {
                                @Override
                                public void CmdResultEvent(boolean hasSuccess, ErrorInfo error) {
                                    if(!hasSuccess){
                                        tvRes.setText(error.toString());
                                    }else {
                                        tvRes.setText("自检关闭");
                                    }
                                }
                            });
                        }else {
                            tvRes.setText(error.toString());
                        }
                    }
                });
                break;
            case 18:
                // 本地蓝牙升级
//                Intent intent1 = new Intent(Intent.ACTION_GET_CONTENT);
//                intent1.setType("*/*");//设置类型，我这里是任意类型，可以过滤文件类型
//                intent1.addCategory(Intent.CATEGORY_OPENABLE);
//                startActivityForResult(intent1,IMPORT_REQUEST_CODE_BLE);

                String bluetoothPath;
                if(info.softVersion.equals("24")){
                    bluetoothPath = FileManager.UPGRADE_DIR + "ES520_YB_UP_20210514_23_181.zip";   // 23
                }else if(info.softVersion.equals("23")){
                    bluetoothPath = FileManager.UPGRADE_DIR + "ES520_20210617_1A_B5_Test2.zip";    // 26
                }else if(info.softVersion.equals("26")){
                    bluetoothPath = FileManager.UPGRADE_DIR + "ES520B_YB_UP_20210506_22_181.zip";    // 22
                }else {
                    bluetoothPath = FileManager.UPGRADE_DIR + "ES520_20210610_18_B5_test.zip";    // 24
                }
                BleManage.getInstance().setUpgrade(0, bluetoothPath, new OnUpdateResultBack() {
                    @Override
                    public void updating(int state, String msg, int progress) {
                        LogUtils.i(TAG,msg + ":" + progress);
                        if(state == 0){
                            tvBleUpdate.setText("初始化");
                        }else if(state == 1){
                            tvBleUpdate.setText("进度:" + progress);
                        }
                    }

                    @Override
                    public void updateSuccess() {
                        tvBleUpdate.setText("升级成功");
                    }

                    @Override
                    public void updateFailed(ErrorInfo errorInfo) {
                        tvBleUpdate.setText(errorInfo.toString());
                    }
                });
                break;
            case 19:
                // 本地控制器升级
//                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                intent.setType("*/*");//设置类型，我这里是任意类型，可以过滤文件类型
//                intent.addCategory(Intent.CATEGORY_OPENABLE);
//                startActivityForResult(intent,IMPORT_REQUEST_CODE);

                String controlPath;
                if(info.controlSV.equals("37")){
                    controlPath = FileManager.UPGRADE_DIR + "Control_ES520_ECU_UP_20210425_23D_18D.bin";   // 23
                }else {
                    controlPath = FileManager.UPGRADE_DIR + "Control_ES520_ECU_UP_20210611_37_18.bin";    // 37
                }
                BleManage.getInstance().setUpgrade(1, controlPath, new OnUpdateResultBack() {
                    @Override
                    public void updating(int state, String msg, int progress) {
                        LogUtils.i(TAG,state + ":" + progress);
                        if(state == 0){
                            tvControlUpdate.setText("初始化");
                        }else if(state == 1){
                            tvControlUpdate.setText("进度:" + progress);
                        }
                    }


                    @Override
                    public void updateSuccess() {
                        tvControlUpdate.setText("升级成功");
                    }

                    @Override
                    public void updateFailed(ErrorInfo errorInfo) {
                        tvControlUpdate.setText(errorInfo.toString());
                    }
                });
                break;
        }
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

                            LogUtils.i(TAG,"ffff" + upLoadFilePath);

                            BleManage.getInstance().setUpgrade(1, upLoadFilePath, new OnUpdateResultBack() {
                                @Override
                                public void updating(int state, String msg, int progress) {
                                    LogUtils.i(TAG,state + ":" + progress);
                                    if(state == 0){
                                        tvControlUpdate.setText("初始化");
                                    }else if(state == 1){
                                        tvControlUpdate.setText("进度:" + progress);
                                    }
                                }

                                @Override
                                public void updateSuccess() {
                                    tvControlUpdate.setText("升级成功");
                                }

                                @Override
                                public void updateFailed(ErrorInfo errorInfo) {
                                    tvControlUpdate.setText(errorInfo.toString());
                                }
                            });

                        }
                    }
                }
            }
        }else if(requestCode == IMPORT_REQUEST_CODE_BLE){
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                if (uri == null) {
                    return;
                }

                String path = FileUtils.getPath(this, uri);
                if (path == null) {
                    return;
                }

                File file = new File(path);
                if (!file.exists()) {
                    return;
                }

                String upLoadFilePath = file.toString();
                String upLoadFileName = file.getName();

                LogUtils.i(TAG,"ffff" + upLoadFilePath);

                BleManage.getInstance().setUpgrade(0, upLoadFilePath, new OnUpdateResultBack() {
                    @Override
                    public void updating(int state, String msg, int progress) {
                        LogUtils.i(TAG,state + ":" + progress);
                        if(state == 0){
                            tvBleUpdate.setText("初始化");
                        }else if(state == 1){
                            tvBleUpdate.setText("进度:" + progress);
                        }
                    }

                    @Override
                    public void updateSuccess() {
                        tvBleUpdate.setText("升级成功");
                    }

                    @Override
                    public void updateFailed(ErrorInfo errorInfo) {
                        tvBleUpdate.setText(errorInfo.toString());
                    }
                });

            }
        }
    }

    @Override
    public void onClick(View view) {
        tvRev.setText("");
        tvRes.setText("");
        tvReport.setText("");
        switch (view.getId()){
            case R.id.iv_back:
                // 断开连接
                disConnectDevice();
                finish();
                break;
            case R.id.tv_confirm:
//                clickReport();
                showProgressDialog("正在连接");
                BleManage.getInstance().setDeviceConnect(MyApplication.bleDevice.id, new OnConnectDevStateBack() {
                    @Override
                    public void connectState(boolean state, String msg) {
                        if(state) {
                            LogUtils.i(TAG,"onClick,连接");
                            readConfig();
                        }
                    }
                });

                break;
        }
    }

    // 读取配置
    private void readConfig() {
        BleManage.getInstance().setReadConfig(new OnCmdInitInfoResultBack() {
            @Override
            public void CmdInitInfo(ErrorInfo error, InitInfo initInfo) {
                LogUtils.i(TAG,"发送读取配置指令");
                if(initInfo == null && error != null){
                    tvRes.setText(error.toString());
                }else {
                    hideProgressDialog();
                    tvDevice.setText(MyApplication.curDevice.getName());
                    tvConnState.setText("已连接");
                    tvRes.setText("读取配置成功");
                    tvRev.setText(initInfo.toString());
                    info = initInfo;
                    LogUtils.i(TAG,initInfo.toString());
                }
            }
        });
    }



    private Dialog loadDialog;

    /**
     * 显示加载窗
     * @param msg
     */
    public void showProgressDialog(String msg) {
        loadDialog = LoadDialog.createLoadingDialog(this, msg);
    }

    /**
     * 隐藏加载窗
     */
    public void hideProgressDialog() {
        LoadDialog.closeDialog(loadDialog);
    }

    private DialogReport dialogShowRun;

    public void clickReport() {
        if (dialogShowRun != null && dialogShowRun.isShowing()) {
            dialogShowRun.dismiss();
        }
        dialogShowRun = new DialogReport(this);
        dialogShowRun.setCanceledOnTouchOutside(false);
        dialogShowRun.show();
    }

    /**
     * 骑行参数（最大速度）
     * speedMode 0：柔和模式，1：运动模式
     * showModel 0：YD,1：KM
     */
    private void showRideBikeDialog() {
        // todo 需要重新修改
        AlertDialog.Builder builder = new AlertDialog.Builder(ControlActivity.this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_ride_config,null);

        final EditText etMaxSpeed = view.findViewById(R.id.et_max_speed);
        final EditText etAddSpeedMode = view.findViewById(R.id.et_add_speed_mode);
        final EditText etShow = view.findViewById(R.id.et_show);
        final EditText etReportTime = view.findViewById(R.id.et_report_time);
        final EditText etCloseTime= view.findViewById(R.id.et_close_time);

        etMaxSpeed.setText(String.format(Locale.US,"%d",info.MaxSpeed));
        etAddSpeedMode.setText(String.format(Locale.US,"%d",info.addMode));
        etShow.setText(String.format(Locale.US,"%d",info.showMode));
        etReportTime.setText(String.format(Locale.US,"%d",info.reportSpace));
        etCloseTime.setText(String.format(Locale.US,"%d",info.standbyTime));

        builder.setView(view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        int maxSpeed = Integer.parseInt(etMaxSpeed.getText().toString());
                        int addSpeedMode = Integer.parseInt(etAddSpeedMode.getText().toString());
                        int showModel = Integer.parseInt(etShow.getText().toString());
                        int reportTime = Integer.parseInt(etReportTime.getText().toString());
                        int closeTime = Integer.parseInt(etCloseTime.getText().toString());

                        BleManage.getInstance().setRideConfig(maxSpeed, addSpeedMode, showModel,
                                reportTime, closeTime, new OnCmdResultBack() {
                                    @Override
                                    public void CmdResultEvent(boolean hasSuccess, ErrorInfo error) {
                                        if(!hasSuccess){
                                            tvRes.setText(error.toString());
                                        }else {
                                            tvRes.setText("配置成功");
                                        }
                                    }
                                });
                    }
                }).create().show();
    }


    /**
     * 骑行参数(S档开关)
     * speedMode 0：柔和模式，1：运动模式
     * showModel 0：YD,1：KM
     */
    private void showRideBikeGearDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ControlActivity.this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_ride_config_gears,null);

        final Switch swSwitch = view.findViewById(R.id.sw_switch);
        final EditText etAddSpeedMode = view.findViewById(R.id.et_add_speed_mode);
        final EditText etShow = view.findViewById(R.id.et_show);
        final EditText etReportTime = view.findViewById(R.id.et_report_time);
        final EditText etCloseTime= view.findViewById(R.id.et_close_time);

        etAddSpeedMode.setText(String.format(Locale.US,"%d",info.addMode));
        etShow.setText(String.format(Locale.US,"%d",info.showMode));
        etReportTime.setText(String.format(Locale.US,"%d",info.reportSpace));
        etCloseTime.setText(String.format(Locale.US,"%d",info.standbyTime));
        builder.setView(view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SwitchStatusEnum statusEnum;
                        if(swSwitch.isChecked()){
                            statusEnum = SwitchStatusEnum.START;
                        }else {
                            statusEnum = SwitchStatusEnum.END;
                        }

                        int addSpeedMode = Integer.parseInt(etAddSpeedMode.getText().toString());
                        int showModel = Integer.parseInt(etShow.getText().toString());
                        int reportTime = Integer.parseInt(etReportTime.getText().toString());
                        int closeTime = Integer.parseInt(etCloseTime.getText().toString());

                        BleManage.getInstance().setRideConfig(statusEnum, addSpeedMode, showModel,
                                reportTime, closeTime, new OnCmdResultBack() {
                                    @Override
                                    public void CmdResultEvent(boolean hasSuccess, ErrorInfo error) {
                                        if(!hasSuccess){
                                            tvRes.setText(error.toString());
                                        }else {
                                            tvRes.setText("配置成功");
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
        AlertDialog.Builder builder = new AlertDialog.Builder(ControlActivity.this);
        final EditText editText = new EditText(this);
        builder.setTitle("输入名称")
                .setView(editText).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String name = editText.getText().toString();
                /**
                 * 正常模式-修改蓝牙名称
                 */
                BleManage.getInstance().setChangeBleName(name, new OnCmdResultBack() {
                    @Override
                    public void CmdResultEvent(boolean hasSuccess, ErrorInfo error) {
                        if(!hasSuccess){
                            tvRes.setText(error.toString());
                        }else {
                            tvRes.setText("成功");
                        }
                    }
                });
            }
        }).create().show();
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
        BleManage.getInstance().setDeviceDisconnect();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (receiver!=null){
            unregisterReceiver(receiver);
        }
    }

}