package com.yele.hu.blesdk520demo.view.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.yele.blesdklibrary.BleManage;
import com.yele.blesdklibrary.bean.OkaiBleDevice;
import com.yele.blesdklibrary.port.OnConnectDevStateBack;
import com.yele.blesdklibrary.port.OnDevicePermissionBack;
import com.yele.blesdklibrary.port.OnScanDevStateBack;
import com.yele.hu.blesdk520demo.MyApplication;
import com.yele.hu.blesdk520demo.R;
import com.yele.hu.blesdk520demo.util.ToastUtil;
import com.yele.hu.blesdk520demo.util.ViewUtils;
import com.yele.hu.blesdk520demo.view.activity.base.BasePerActivity;
import com.yele.hu.blesdk520demo.view.adapter.AdapterDeviceList;
import com.yele.hu.blesdk520demo.view.dialog.LoadDialog;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends BasePerActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    @Override
    protected int getResId() {
        return R.layout.activity_main;
    }

    private TextView tvTitle;
    private TextView tvConfirm;
    private ImageView ivBack;

    private RecyclerView rvDeviceList;
    private List<OkaiBleDevice> deviceList;
    private AdapterDeviceList adapter;

    private SwipeRefreshLayout srlList;

    private EditText etSn;
    private Button btnConn;

    @Override
    protected void findView() {
        tvConfirm = findViewById(R.id.tv_confirm);
        tvTitle = findViewById(R.id.tv_title);
        ivBack = findViewById(R.id.iv_back);
        srlList = findViewById(R.id.srl_list);
        rvDeviceList = findViewById(R.id.rv_device_list);
        etSn = findViewById(R.id.et_sn);
        btnConn = findViewById(R.id.btn_conn);
    }


    @Override
    protected void initData() {
        if (adapter == null) {
            deviceList = new ArrayList<>();
            adapter = new AdapterDeviceList(this, deviceList);
            rvDeviceList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            rvDeviceList.setAdapter(adapter);
            adapter.setOnDevListener(new AdapterDeviceList.OnDevListener() {
                @Override
                public void clickDev(final OkaiBleDevice bleDevice) {
                    showProgressDialog("正在连接");
                    /* 停止扫描 */
                    stopScan();
                    /* 车辆权限判断 */
                    BleManage.getInstance().setQueryDevicePermission(bleDevice.type, bleDevice.sn, new OnDevicePermissionBack() {
                        @Override
                        public void resultQuery(boolean hasPermission) {
                            if(hasPermission){
                                /* 蓝牙连接 */
                                BleManage.getInstance().setDeviceConnect(bleDevice.id, new OnConnectDevStateBack() {
                                    @Override
                                    public void connectState(boolean state, String msg) {
                                        if (state) {
                                            hideProgressDialog();
                                            MyApplication.curDevice = bleDevice.device;
                                            MyApplication.bleDevice = bleDevice;
                                            startActivity(new Intent(MainActivity.this, ControlActivity.class));
                                        } else {
                                            ToastUtil.showShort(MainActivity.this, msg);
                                        }
                                    }
                                });
                            } else {
                                hideProgressDialog();
                                ToastUtil.showShort(MainActivity.this,"当前车辆没有权限！");
                            }
                        }
                    });


                }
            });
        }
    }

    @Override
    protected void initView() {
        tvTitle.setText("扫描页");
        ViewUtils.hideView(tvConfirm);
        ivBack.setOnClickListener(this);
        btnConn.setOnClickListener(this);
        srlList.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (srlList.isRefreshing()) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            srlList.setRefreshing(false);

                        }
                    }, 3000);

                    if (MyApplication.isScan) {
                        stopScan();
                    }else{
                        startScan();
                    }

                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 停止扫描
            stopScan();
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopScan();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                stopScan();
                finish();
                break;
            case R.id.btn_conn:
                showProgressDialog("正在扫描连接");
                BleManage.getInstance().setStartScan(etSn.getText().toString(), new OnScanDevStateBack() {
                    @Override
                    public void onScanSuccess(final OkaiBleDevice device) {
                        /* 车辆权限判断 */
                        BleManage.getInstance().setQueryDevicePermission(device.type, device.sn, new OnDevicePermissionBack() {
                            @Override
                            public void resultQuery(boolean hasPermission) {
                                if(hasPermission){
                                    /* 蓝牙连接 */
                                    BleManage.getInstance().setDeviceConnect(device.id, new OnConnectDevStateBack() {
                                        @Override
                                        public void connectState(boolean state, String msg) {
                                            if (state) {
                                                hideProgressDialog();
                                                MyApplication.curDevice = device.device;
                                                startActivity(new Intent(MainActivity.this, ControlActivity.class));
                                            } else {
                                                ToastUtil.showShort(MainActivity.this, msg);
                                            }
                                        }
                                    });
                                } else {
                                    hideProgressDialog();
                                    ToastUtil.showShort(MainActivity.this,"当前车辆没有权限！");
                                }
                            }
                        });
                    }
                });
                break;
        }
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

    private void startScan() {
        MyApplication.isScan = true;
        deviceList.clear();
        // 开始扫描
        BleManage.getInstance().setStartScan(null, new OnScanDevStateBack() {
            @Override
            public void onScanSuccess(final OkaiBleDevice device) {
                deviceList.add(device);
                adapter.notifyDataSetChanged();
            }
        });

        mHandler.postDelayed(runnable, 30 * 1000);
    }

    /**
     *
     */
    private Handler mHandler = new Handler();
    /**
     * 停止搜索
     */
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            BleManage.getInstance().setStopScan();
        }
    };

    /**
     * 停止扫描
     */
    private void stopScan() {
        if (MyApplication.isScan) {
            BleManage.getInstance().setStopScan();
            mHandler.removeCallbacks(runnable);
            MyApplication.isScan = false;
        }
    }
}