package com.yele.hu.blesdk520demo.view.activity;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.yele.blesdklibrary.BleManage;
import com.yele.blesdklibrary.bean.ErrorInfo;
import com.yele.blesdklibrary.port.OnCmdResultBack;
import com.yele.hu.blesdk520demo.MyApplication;
import com.yele.hu.blesdk520demo.R;
import com.yele.hu.blesdk520demo.util.LogUtils;
import com.yele.hu.blesdk520demo.util.ToastUtil;
import com.yele.hu.blesdk520demo.view.activity.base.BasePerActivity;

import java.util.Timer;
import java.util.TimerTask;

public class LedControlActivity extends BasePerActivity {

    private static final String TAG = "ControlDeviceActivity";

    @Override
    protected int getResId() {
        return R.layout.activity_led_control;
    }


    private Button btnStart;
    private TextView tvResult;

    private int curPosition = 0;
    private int curResult = 0;   // 成功应答
    private int curFailed = 0;   // 失败应答
    private int notResult = 0;  // 没有应答

    private int ledState = 0;

    @Override
    protected void findView() {
        btnStart = findViewById(R.id.btn_start);
        tvResult = findViewById(R.id.tv_result);
    }

    @Override
    protected void initView() {
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyApplication.curDevice != null) {
                    startLedControl();
                } else {
                    endTimerRead();
                    ToastUtil.showShort(LedControlActivity.this, "蓝牙未连接");
                }
            }
        });
    }

    @Override
    protected void initData() {
        BleManage.getInstance().setPassword("OKAIYLBT");
    }


    private void startLedControl(){
        curPosition ++;
        startTimerRead();
        LogUtils.i(TAG,"写入大灯：" + curPosition);
        if (ledState == 0) {
            ledState = 1;
        } else {
            ledState = 0;
        }
        tvResult.setText(curPosition + "/" + curResult + "/" + curFailed + "/" + notResult);
        BleManage.getInstance().setLedControl(ledState, new OnCmdResultBack() {
            @Override
            public void CmdResultEvent(boolean b, ErrorInfo errorInfo) {
                if(b){
                    endTimerRead();
                    curResult++;
                    startLedControl();
                }else {
                    endTimerRead();
                    curFailed ++;
                    startTimerRead();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        endTimerRead();
    }


    private boolean isTimerRead = false;        // 定时器是否开启
    private Timer timerToRead;                  // 定时器-用于扫描当前的蓝牙设备
    private TimerTask taskRead;                 // 定时任务，用于扫描当前的蓝牙设备是否符合目标设备
    private int pregress = 0;

    /**
     * 定时开始扫描，500ms检索一次
     */
    private void startTimerRead() {
        if (isTimerRead) {
            return;
        }
        pregress = 10;
        timerToRead = new Timer();
        taskRead = new TimerTask() {
            @Override
            public void run() {
                pregress --;
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        LogUtils.i(TAG,"大灯失败： " + pregress);
                        if(pregress < 0){
                            endTimerRead();
                            notResult ++;
                            startLedControl();
                        }
                    }
                });
            }
        };
        isTimerRead = true;
        timerToRead.schedule(taskRead, 0, 1000);
    }

    /**
     * 结束定时扫描
     */
    private void endTimerRead() {
        if (!isTimerRead) {
            return;
        }
        timerToRead.cancel();
        taskRead.cancel();
        isTimerRead = false;
    }

}
