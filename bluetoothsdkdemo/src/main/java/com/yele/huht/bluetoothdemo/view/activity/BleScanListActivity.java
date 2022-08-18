package com.yele.huht.bluetoothdemo.view.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import com.yele.baseapp.utils.DateUtils;
import com.yele.baseapp.utils.ToastUtil;
import com.yele.baseapp.view.activity.BasePerActivity;
import com.yele.huht.bluetoothdemo.MyApplication;
import com.yele.huht.bluetoothdemo.R;
import com.yele.huht.bluetoothdemo.view.adapter.AdapterCmdScanList;
import com.yele.huht.bluetoothdemo.view.dialog.LoadDialog;
import com.yele.huht.bluetoothsdklib.BleManage;
import com.yele.huht.bluetoothsdklib.bean.OkaiBleDevice;
import com.yele.huht.bluetoothsdklib.callBcak.OnConnectDevState;
import com.yele.huht.bluetoothsdklib.callBcak.OnScanDevState;


import java.util.ArrayList;
import java.util.List;

public class BleScanListActivity extends BasePerActivity implements View.OnClickListener {

    private static final String TAG = "BleScanListActivity";

    @Override
    protected int getResId() {
        return R.layout.activity_ble_scan_list;
    }

    private TextView tvTitle,tvConfirm;
    private ImageView ivBack;

    private SwipeRefreshLayout srlList;
    private RecyclerView rvDeviceList;
    private List<OkaiBleDevice> deviceList;
    private AdapterCmdScanList adapter;

    private long startTime;  // 开始连接的时间

    @Override
    protected void findView() {
        tvConfirm = findViewById(R.id.tv_confirm);
        tvTitle = findViewById(R.id.tv_title);
        ivBack = findViewById(R.id.iv_back);
        rvDeviceList = findViewById(R.id.rv_device_list);
        srlList = findViewById(R.id.srl_list);
    }

    @Override
    protected void initData() {
        if(adapter == null) {
            deviceList = new ArrayList<>();
            adapter = new AdapterCmdScanList(this,deviceList);
        }
        rvDeviceList.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        rvDeviceList.setAdapter(adapter);
        adapter.setOnDevListener((id, device) -> {
            stopScan();
            // 蓝牙连接
            showProgressDialog("正在连接");
            String address = device.device.getAddress();
            MyApplication.curDevice = device;
            startTime = DateUtils.getTimeStamp();
            BleManage.getInstance().deviceConnect(device.id, new OnConnectDevState() {
                @Override
                public void connectState(boolean state, String msg) {
                    if(state){
                        String connTime = doConnTime();
                        hideProgressDialog();
                        MyApplication.isCon = true;
                        startActivity(new Intent(BleScanListActivity.this, MainActivity.class));
                    }else {
                        // 连接失败
                        hideProgressDialog();
                        MyApplication.isCon = false;
                        ToastUtil.showShort(BleScanListActivity.this,"连接失败");
                    }
                }
            });
        });
    }

    /**
     * 连接时间处理
     */
    private String doConnTime() {
        long curTime = DateUtils.getTimeStamp();
        // 这样得到的差值是微秒级别
        long diff = curTime - startTime;
        //以天数为单位取整
        long day = diff / (1000 * 60 * 60 * 24);
        //以小时为单位取整
        long hour=(diff/(60*60*1000)-day*24);
        //以分钟为单位取整
        long min=((diff/(60*1000))-day*24*60-hour*60);
        //以秒为单位
        long second= ((diff / 1000) - (day * 24 * 60 * 60) - (hour * 60 * 60) - (min * 60));

        return min + "分" + second + "秒" + (diff%1000) + "毫秒";
//        return day + "天" + hour + "小时" + min + "分" + second + "秒" + (diff%1000) + "毫秒";
    }

    @Override
    protected void initView() {
        tvConfirm.setOnClickListener(this);
        ivBack.setOnClickListener(this);

        srlList.setOnRefreshListener(() -> {
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
                refreshTitle();
            }
        });

        refreshTitle();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_confirm:
                if(MyApplication.isScan){
                    stopScan();
                }else {
                    startScan();
                }
                refreshTitle();
                break;
            case R.id.iv_back:
                BleManage.getInstance().deviceStopScan();
                MyApplication.isScan = false;
                finish();
                break;
        }
    }

    /**
     * 开始扫描
     * 扫描30秒后停止扫描
     */
    private void startScan() {
        MyApplication.isScan = true;
        deviceList.clear();
        BleManage.getInstance().deviceStartScan(null, new OnScanDevState() {
            @Override
            public void onScanSuccess(OkaiBleDevice device) {
                deviceList.add(device);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onScanFail(String msg) {

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
            stopScan();
            refreshTitle();
        }
    };

    /**
     * 停止扫描
     */
    private void stopScan() {
        if (MyApplication.isScan) {
            BleManage.getInstance().deviceStopScan();
            mHandler.removeCallbacks(runnable);
            MyApplication.isScan = false;
        }
    }

    /**
     * 刷新列表
     */
    private void refreshTitle(){
        tvTitle.setText("扫描列表");
        if(MyApplication.isScan){
            tvConfirm.setText("停止");
        }else {
            tvConfirm.setText("扫描");
        }
    }


    private Dialog loadDialog;

    /**
     * 显示加载中弹窗
     * @param msg  加载提示语
     */
    public void showProgressDialog(String msg) {
        loadDialog = LoadDialog.createLoadingDialog(this, msg);
    }

    /**
     * 隐藏弹窗
     */
    public void hideProgressDialog() {
        LoadDialog.closeDialog(loadDialog);
    }

}
