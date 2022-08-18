package com.yele.hu.upgradetools.policy.ble;

import android.content.Context;

import com.yele.baseapp.utils.LogUtils;
import com.yele.hu.upgradetools.R;
import com.yele.hu.upgradetools.bean.DebugFlag;
import com.yele.hu.upgradetools.bean.DeviceBase;
import com.yele.hu.upgradetools.bean.RevResult;
import com.yele.hu.upgradetools.policy.UpgradeFrame;
import com.yele.hu.upgradetools.policy.event.UpgradeActionEvent;
import com.yele.hu.upgradetools.policy.event.UpgradeResultEvent;

import java.io.IOException;

/**
 * 升级普通蓝牙仪表的程序
 */
public class UpgradeControl {

    private static final String TAG = "UpgradeControl";

    private boolean DEBUG = true;

    private void logi(String msg) {
        if (!DEBUG) {
            return;
        }
        LogUtils.i(TAG,msg);
    }

    private void logw(String msg) {
        if (!DEBUG) {
            return;
        }
        LogUtils.w(TAG,msg);
    }

    private void loge(String msg) {
        if (!DEBUG) {
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
    private Context context;

    public UpgradeControl(Context context,String path, OnControlUpgradeListener l) {
        this.context = context;
        this.path = path;
        listener = l;
    }

    // 是否升级
    private boolean isUpgrade = false;
    // 升级底层程序
    private UpgradeFrame upgradeFrame;

    /**
     * 开始更新
     * @param type
     */
    public void startUpdate(int type) {
        if (upgradeFrame != null && isUpgrade) {
            return;
        }
        if (upgradeFrame == null) {
            upgradeFrame = new UpgradeFrame(type,path);
        }
        upgradeFrame.setOnUpgradeListener(msg -> {
            stopUpdate();
            if (listener != null) {
                listener.upgradeFailed(0x02, msg);
            }
        });
        upgradeFrame.start();
        int code = 0;
        if (type == UpgradeActionEvent.TYPE_CONTROL) {
            code = 13;
        } else if (type == UpgradeActionEvent.TYPE_BLE) {
            code = 14;
        } else if (type == UpgradeActionEvent.TYPE_MCU) {
            code = 15;
        } else if (type == UpgradeActionEvent.TYPE_BMS) {
            code = 16;
        }

        /*UpgradeConfig upgradeInfo = new UpgradeConfig(code,upgradeFrame.getPackNum());
        CmdPackage cmdPackage = new CmdPackage(CmdFlag.CMD_UPGRADE_CONTROL, upgradeInfo);
        String cmdStr = cmdPackage.packCmdStr();*/
        String cmdStr = DebugFlag.SEND_PROTOCOL + "URD=" + DeviceBase.PWD + "," + code + "$\r\n";
        isUpgrade = true;
        if (listener != null) {
            boolean isSend = listener.sendData(cmdStr.getBytes());
            if (!isSend) {
                listener.upgradeFailed(0x01,context.getString(R.string.send_fail));
            }
        }

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
                        listener.upgradeFailed(0x01,context.getString(R.string.send_fail));
                    }
                }
            } else {
                LogUtils.w(TAG, "数据位空");
            }
        } else {
            LogUtils.w(TAG, "发送失败");
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
                        listener.upgradeFailed(0x01,context.getString(R.string.send_fail));
                    }
                }
            } else {
                LogUtils.w(TAG, "数据位空");
            }
        } else {
            LogUtils.w(TAG, "发送失败");
        }
    }

    /**
     * 处理收到的数据
     * @param revResult 当前解析出来的数据
     */
    public void dealRevResult(RevResult revResult) {
        int result = (int) revResult.object;
        if (result != -1) {
            int code = -1;
            String msg = "";
            int percent = 0;
            switch (result) {
                case 0: // 失败，硬件版本号异常
                    code = UpgradeResultEvent.FAILED;
                    msg = context.getString(R.string.ware_version_err);
                    stopUpdate();
                    break;
                case 1: // 更新，发送下一个包
                    code = UpgradeResultEvent.UPDATE;
                    msg = "发送下一个包";
                    percent = getUpgradeProgress();
                    logi("进度" + percent);
                    sendUpdateData(true);
                    break;
                case 2: // 更新，重发当前包
                    code = UpgradeResultEvent.UPDATE;
                    msg = "重发当前包";
                    logi("重发当前包");
                    sendUpdateData(false);
                    percent = getUpgradeProgress();
                    break;
                case 3: // 更新，重新发送
                    code = UpgradeResultEvent.UPDATE;
                    msg = "固件丢失，重新发送";
                    sendStartData();
                    percent = getUpgradeProgress();
                    break;
                case 4: // 失败，程序升级失败
                    code = UpgradeResultEvent.FAILED;
                    msg = context.getString(R.string.cmd_upgrade_fail);
                    stopUpdate();
                    break;
                case 5: // 更新，升级准备就绪
                    code = UpgradeResultEvent.UPDATE;
                    msg = "升级准备就绪";
                    sendStartData();
                    percent = getUpgradeProgress();
                    break;
                case 6: // 失败，密码错误
                    code = UpgradeResultEvent.FAILED;
                    msg = context.getString(R.string.pwd_err);
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
