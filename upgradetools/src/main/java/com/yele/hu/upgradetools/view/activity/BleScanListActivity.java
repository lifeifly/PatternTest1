package com.yele.hu.upgradetools.view.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.yele.baseapp.utils.DateUtils;
import com.yele.baseapp.utils.LogUtils;
import com.yele.baseapp.utils.ToastUtil;
import com.yele.baseapp.utils.ViewUtils;
import com.yele.baseapp.view.activity.BasePerActivity;
import com.yele.hu.upgradetools.MyApplication;
import com.yele.hu.upgradetools.R;
import com.yele.hu.upgradetools.bean.LoopTestData;
import com.yele.hu.upgradetools.bean.info.car.OkaiBleDevice;
import com.yele.hu.upgradetools.data.DataManager;
import com.yele.hu.upgradetools.policy.event.BleDevRequestConEvent;
import com.yele.hu.upgradetools.policy.event.BleDevScanEvent;
import com.yele.hu.upgradetools.view.adapter.CmdScanListAdapter;
import com.yele.hu.upgradetools.view.dialog.LoadDialog;
import com.yele.hu.upgradetools.view.service.BleService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索附近蓝牙
 */
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
    private CmdScanListAdapter adapter;

    private long startTime;  // 开始连接的时间
    private String viewMode;  // activity跳转传递过来的值


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
        Intent intent = getIntent();
        if(intent != null){
            viewMode = intent.getStringExtra("view_mode");
        }

        if(deviceList == null){
            deviceList = new ArrayList<>();
        }

        if(adapter == null) {
            adapter = new CmdScanListAdapter(this,deviceList);
            adapter.setHasStableIds(true);   // 设置item唯一标识
            adapter.setOnDevListener((id, device) -> {
                stopScanDevice();     // 取消扫描
                connectDev(device);   // 蓝牙连接
            });
            rvDeviceList.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
            ((DefaultItemAnimator)rvDeviceList.getItemAnimator()).setSupportsChangeAnimations(false);    // 取消item的动画，防止item刷新产生闪烁
            rvDeviceList.setAdapter(adapter);
        }
    }

    /**
     * 连接设备
     * @param device 设备
     */
    private void connectDev(OkaiBleDevice device) {
        showProgressDialog(getString(R.string.connecting));
        DataManager.getInstance().device = device;
        startTime = DateUtils.getTimeStamp();
        EventBus.getDefault().post(new BleDevRequestConEvent(BleDevRequestConEvent.CODE_REQUEST_CONNECT,device));
    }

    @Override
    protected void initView() {
        tvConfirm.setOnClickListener(this);
        ivBack.setOnClickListener(this);

        ViewUtils.hideView(ivBack);

        srlList.setOnRefreshListener(() -> {
            if (srlList.isRefreshing()) {
                startScan();   // 开始扫描蓝牙
                // 5秒后结束刷新，刷新动效消失
                new Handler().postDelayed(() -> cancelRefresh(), 5*1000);
                // 25秒后停止扫描蓝牙
                new Handler().postDelayed(() -> stopScanDevice(), 25*1000);
            }
        });

        refreshTitle();

        Intent intent = new Intent(BleScanListActivity.this, BleService.class);
        startService(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            stopScanDevice();
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopScanDevice();
        // 停止服务
        stopService(new Intent(BleScanListActivity.this, BleService.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshTitle();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_confirm:
                if(MyApplication.isScan){
                    stopScanDevice();
                }else {
                    startScan();
                }
                break;
            case R.id.iv_back:
                stopScanDevice();
                finish();
                break;
        }
    }


    /**
     * 开始扫描设备
     */
    private void startScan() {
        deviceList.clear();
        adapter.notifyDataSetChanged();
        EventBus.getDefault().post(new BleDevScanEvent(BleDevScanEvent.REQUEST_SCAN_DEV,null));
        MyApplication.isScan = true;
        refreshTitle();
    }

    /**
     * 取消界面的刷新
     */
    private void cancelRefresh() {
        if (srlList.isRefreshing()) {
            srlList.setRefreshing(false);
        }
    }

    /**
     * 停止扫描
     */
    private void stopScanDevice() {
        cancelRefresh();
        EventBus.getDefault().post(new BleDevScanEvent(BleDevScanEvent.REQUEST_STOP_SCAN_DEV,null));
        MyApplication.isScan = false;
        refreshTitle();
    }

    /**
     * 刷新列表
     */
    private void refreshTitle(){
        tvTitle.setText(R.string.scan_list);
        if(MyApplication.isScan){
            tvConfirm.setText(R.string.stop_scan);
        }else {
            tvConfirm.setText(R.string.start_scan);
        }
    }


    private Dialog loadDialog;

    /**
     * 显示加载中弹窗
     * @param msg  加载提示语
     */
    public void showProgressDialog(String msg) {
        loadDialog = LoadDialog.createLoadingDialog(this, msg);
        loadDialog.setOnKeyListener((dialog, keyCode, event) -> {
            if(keyCode == KeyEvent.KEYCODE_BACK){
                EventBus.getDefault().post(new BleDevRequestConEvent(BleDevRequestConEvent.CODE_REQUEST_DISCONNECT,null));
                DataManager.getInstance().device = null;
            }
            return false;
        });
    }

    /**
     * 隐藏弹窗
     */
    public void hideProgressDialog() {
        LoadDialog.closeDialog(loadDialog);
    }

    /**
     * 扫描结果
     * @param event  扫描事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onScanEvent(BleDevScanEvent event){
        switch (event.code){
            case BleDevScanEvent.RESULT_DISCOVER_LIST_DEV:
                deviceList.add((OkaiBleDevice) event.obj);
                adapter.notifyDataSetChanged();
                break;
        }
    }


    /**
     * 连接结果
     * @param event 连接事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConEvent(BleDevRequestConEvent event){
        switch (event.code){
            case BleDevRequestConEvent.CODE_RESULT_CONNECT:          // 连接成功
                hideProgressDialog();
                MyApplication.isCon = true;
                LoopTestData.time = doConnTime();
                startActivity(new Intent(BleScanListActivity.this, UpgradeLengthActivity.class));
                break;
            case BleDevRequestConEvent.CODE_RESULT_CONNECT_FAIL:          // 连接失败
                hideProgressDialog();
                MyApplication.isCon = false;
                ToastUtil.showShort(BleScanListActivity.this,getString(R.string.connect_fail));
                break;
            case BleDevRequestConEvent.CODE_RESULT_DISCONNECT:          // 断开连接
                hideProgressDialog();
                MyApplication.isCon = false;
                ToastUtil.showShort(BleScanListActivity.this,getString(R.string.disconnect_success));
                break;
            case BleDevRequestConEvent.CODE_RESULT_DISCONNECT_FAIL:     // 断开连接失败
                hideProgressDialog();
                MyApplication.isCon = false;
                ToastUtil.showShort(BleScanListActivity.this,getString(R.string.disconnect_fail));
                break;
            case BleDevRequestConEvent.CODE_RESULT_TIME_OUT:
                LogUtils.i(TAG,"连接超时");
                hideProgressDialog();
                MyApplication.isCon = false;
                ToastUtil.showShort(BleScanListActivity.this,getString(R.string.connect_time_out));
                break;
        }
    }

    /**
     * 连接时间处理
     */
    private String doConnTime() {
        long curTime = DateUtils.getTimeStamp();
        LogUtils.w(TAG,"连接成功：" + curTime);
        // 这样得到的差值是微秒级别
        long diff = curTime - startTime;
        LogUtils.i(TAG, "时间间隔: " + diff);
        //以天数为单位取整
        long day = diff / (1000 * 60 * 60 * 24);
        //以小时为单位取整
        long hour=(diff/(60*60*1000)-day*24);
        //以分钟为单位取整
        long min=((diff/(60*1000))-day*24*60-hour*60);
        //以秒为单位
        long second= ((diff / 1000) - (day * 24 * 60 * 60) - (hour * 60 * 60) - (min * 60));

        if(day == 0){
            return min + "分" + second + "秒" + (diff%1000) + "毫秒";
        } else {
            return day + "天" + hour + "小时" + min + "分" + second + "秒" + (diff%1000) + "毫秒";
        }
    }

}
