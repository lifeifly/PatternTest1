package com.yele.bluetoothlib.policy.protrol;

import android.util.Log;

import com.yele.baseapp.utils.LogUtils;
import com.yele.bluetoothlib.bean.LogDebug;
import com.yele.bluetoothlib.bean.UpgradeFrame;
import com.yele.bluetoothlib.bean.cmd.CmdFlag;
import com.yele.bluetoothlib.bean.cmd.RevResult;
import com.yele.bluetoothlib.bean.config.upgrade.UpgradeInfo;
import com.yele.bluetoothlib.policy.event.cmd.update.UpgradeResultEvent;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 升级普通蓝牙仪表的程序
 */
public class UpgradeLengthPackage {

    private static final String TAG = "UpgradeLengthPackage";

    private boolean DEBUG = true;

    private void logi(String msg) {
        if (!DEBUG || !LogDebug.IS_LOG) {
            return;
        }
        LogUtils.i(TAG,msg);
    }

    private void logw(String msg) {
        if (!DEBUG || !LogDebug.IS_LOG) {
            return;
        }
        LogUtils.w(TAG,msg);
    }

    private void loge(String msg) {
        if (!DEBUG || !LogDebug.IS_LOG) {
            return;
        }
        LogUtils.e(TAG,msg);
    }



    public interface OnControlUpgradeListener {
        /**
         * 升级成功
         */
        void upgradeSuccess();

        /**
         * 更新升级进度
         * @param percent 进度百分比
         */
        void updateProgress(int percent);

        /**
         * 升级失败
         */
        void upgradeFailed(int err,String msg);

        /**
         * 发送数据
         * @param data 当前的数据
         * @return 数据是否发送成功
         */
        boolean sendData(byte[] data);
    }

    private String path;
    private OnControlUpgradeListener listener;

    public UpgradeLengthPackage(String path, OnControlUpgradeListener l) {
        this.path = path;
        listener = l;
    }

    // 是否升级
    private boolean isUpgrade = false;
    // 升级底层程序
    private UpgradeFrame upgradeFrame;

    /**
     * 开始更新
     */
    public void startUpdate(boolean isPlanA) {
        if (upgradeFrame != null && isUpgrade) {
            return;
        }
        if (upgradeFrame == null) {
            upgradeFrame = new UpgradeFrame(UpgradeFrame.TYPE_CONTROL,path);
        }
        upgradeFrame.setOnUpgradeListener(msg -> {
            stopUpdate();
            if (listener != null) {
                listener.upgradeFailed(0x02, msg);
            }
        });
        upgradeFrame.start();
        int code = 13;
        UpgradeInfo upgradeInfo = new UpgradeInfo(code,upgradeFrame.getPackNum());
        CmdCarPackage cmdPackage = new CmdCarPackage(CmdFlag.CMD_UPGRADE_READY, upgradeInfo);
        isUpgrade = true;
        if (listener != null) {
            boolean isSend = listener.sendData(cmdPackage.packageData());
            if (!isSend) {
                listener.upgradeFailed(0x01,"发送数据失败");
                stopTimer();
                stopUpdate();
            }
        }
       // startTimer();
    }

    /**
     * 发送开始帧数据
     */
    private void sendStartData() {
        if (upgradeFrame != null && isUpgrade) {
            byte[] data = null;
            try {
                data = upgradeFrame.getStartData();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (data != null) {
                if (listener != null) {
                    boolean isSend = listener.sendData(data);
                    if (!isSend) {
                        listener.upgradeFailed(0x01,"数据发送失败");
                    }else{
                        startTimer();
                    }
                }
            } else {
                LogUtils.w(TAG, "数据位空");
            }
        } else {
            LogUtils.w(TAG, "发送失败");
        }
    }

    private Timer timer;
    private TimerTask task = null;

    private void startTimer() {
        if (timer == null) {
            timer = new Timer();
            task = new TimerTask() {
                int ind = 0;
                @Override
                public void run() {
                    if (!upgradeFrame.isEnd()){
                        Log.i("发送定时器执行没有发完","11111"+"");
                        sendUpdateData(true);
                        int percent = getUpgradeProgress();
                        if (ind++ > 20) {
                            ind = 0;
                            if (listener != null) {
                                listener.updateProgress(percent);
                            }
                        }
                    }else {
                        Log.i("发送数据完毕","222222222222");
                        stopTimer();
                    }

                }
            };
            if (listener != null) {
                listener.updateProgress(0);
            }
            timer.schedule(task, 5,5);
        }
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            task.cancel();
            timer = null;
            task = null;
        }
    }

    /**
     * 发送数据包数据
     *
     * @param next 是否是下一包
     */
    private void sendUpdateData(boolean next) {
        if (upgradeFrame != null && isUpgrade) {
            byte[] data = null;
            if (next) {
                try {
                    data = upgradeFrame.getNextData();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    data = upgradeFrame.getNowData();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (data != null) {
                if (listener != null) {
                    boolean isSend = listener.sendData(data);
                    if (!isSend) {
                        LogUtils.w(TAG, "数据发送失败");
                        stopTimer();
                        stopUpdate();
                        listener.upgradeFailed(0x01,"数据发送失败");
                    }
                }
            } else {
                LogUtils.w(TAG, "数据位空");
                stopTimer();

            }
        } else {
            LogUtils.w(TAG, "发送失败");
        }
    }

    public boolean isEnd() {
        return false;
    }

    /**
     * 处理收到的数据
     * @param revResult 当前解析出来的数据
     */
    public void dealRevResult(RevResult revResult) {
        String cmdtype=revResult.cmdType;
        switch (cmdtype){
            case "URD":
                int result = (int) revResult.object;

                if (result != -1) {
                int code = -1;
                String msg = "";
                int percent = 0;
                switch (result) {
                    case 0: // 失败，硬件版本号异常
                        code = UpgradeResultEvent.FAILED;
                        msg = "升级失败，硬件版本号异常";
                        stopUpdate();
                        break;
                    case 1: // 更新，发送下一个包
                        code = UpgradeResultEvent.UPDATE;
//                    msg = "发送下一个包";
//                    percent = getUpgradeProgress();
//                    logi("进度" + percent);
//                    sendUpdateData(true);
                        logi("升级准备就绪，开始发送包");
                        sendStartData();
                        break;
                    case 2: // 更新，重发当前包
                        code = UpgradeResultEvent.UPDATE;
                        msg = "重发当前包";
                        sendUpdateData(false);
                        percent = getUpgradeProgress();
                        break;
                    case 3: // 更新，重新发送
                        code = UpgradeResultEvent.UPDATE;
                        msg = "固件丢失，重新发送";
                        // sendStartData();
                        percent = getUpgradeProgress();
                        break;
                    case 4: // 失败，程序升级失败
                        code = UpgradeResultEvent.FAILED;
                        msg = "程序升级失败";
                        stopUpdate();
                        break;
                    case 5: // 更新，升级准备就绪
                        code = UpgradeResultEvent.UPDATE;
                        msg = "升级准备就绪";
                        //sendStartData();
                        percent = getUpgradeProgress();
                        break;
                    case 6: // 失败，密码错误
                        code = UpgradeResultEvent.FAILED;
                        msg = "密码错误";
                        stopUpdate();
                        break;
                    case 7: // 成功
                        code = UpgradeResultEvent.SUCCESS;
                        msg = "机器正常开机";
                        stopUpdate();
                        break;
                    case 8: // 成功
                        code = UpgradeResultEvent.SUCCESS;
                        msg = "程序升级成功";
                        stopUpdate();
                        break;
                }
                    if (listener == null) {
                        return;
                    }
                    if (code == UpgradeResultEvent.FAILED) {
                        listener.upgradeFailed(0x03,msg);
                    } else if (code == UpgradeResultEvent.SUCCESS) {
                        listener.upgradeSuccess();
                    } else if (code == UpgradeResultEvent.UPDATE) {
                        listener.updateProgress(percent);
                    }else{
                        logi("收到脏数据");
                    }
            }
                break;


            case "UDA":
                int result1 = (int) revResult.object;
                switch (result1){
                    case 1:
                        int back= (int) revResult.object1;
                        stopTimer();
                        logi("收到回滚包");
                        upgradeFrame.packIndex=back;
                        sendUpdateData(false);
                        startTimer();
                    case 5:
                        stopTimer();
                        listener.upgradeSuccess();


                }


                break;

        }
    }

    /**
     * 获取升级进度
     *
     * @return 当前升级进度
     */
    private int getUpgradeProgress() {
        int percent = 0;
        if (upgradeFrame != null && isUpgrade) {
            percent = upgradeFrame.getPercent();
        }
        return percent;
    }

    /**
     * 停止更新
     */
    public void stopUpdate() {
        if (upgradeFrame != null && isUpgrade) {
            upgradeFrame.stop();
            upgradeFrame = null;
        }
        isUpgrade = false;
    }
}
