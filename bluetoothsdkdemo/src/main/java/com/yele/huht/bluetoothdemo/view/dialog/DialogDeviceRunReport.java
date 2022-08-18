package com.yele.huht.bluetoothdemo.view.dialog;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.yele.baseapp.utils.ViewUtils;
import com.yele.baseapp.view.dialog.BaseDialog;
import com.yele.huht.bluetoothdemo.R;
import com.yele.huht.bluetoothsdklib.bean.CarRunReport;

import java.util.Locale;

public class DialogDeviceRunReport extends BaseDialog {

    public DialogDeviceRunReport(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getResId() {
        return R.layout.dialog_device_run_report;
    }

    private TextView tvRunInfoResult,tvErrResult;   // 上报的结果
    private TextView tvLock,tvSpeed,tvCurMileage,tvSurplusMileage,tvTotalMileage,tvRideTime;
    private TextView tvChargeMos,tvDischargeMos,tvPower,tvSoh,tvHighTemp,tvLowTemp,tvMosTemp,tvOtherTemp,tvCurrent,tvVoltage,tvChargeState;
    private TextView tvAccAd,tvBrakeAd;
    private TextView tvErrContent;
    private TextView tvLedState,tvAccMode;


    private TextView tvLock1,tvSpeed1,tvCurMileage1,tvSurplusMileage1,tvTotalMileage1,tvRideTime1;
    private TextView tvLedState1,tvAccMode1;
    private TextView tvPower1,tvChargeState1;
    private TextView tvDlccMode,tvLockMode,tvDriveMode,tvGearS;
    private TextView tvErrContent1;


    private TextView tvSpeed2,tvCurMileage2,tvSurplusMileage2,tvTotalMileage2,tvRideTime2;
    private TextView tvControlTemp,tvMotorTemp;
    private TextView tvLimitValue,tvDriveValue,tvBrakeValue;
    private TextView tvControlOpen,tvDlccMode1,tvBatteryFail;
    private TextView tvCurrent1,tvVoltage1;
    private TextView tvAccAd1,tvBrakeAd1;
    private TextView tvErrContent2;

    private Button btnInf,btnCcf,btnTes;
    private LinearLayout llInf,llCcf,llTes;

    private Button btnConfirm;

    @Override
    protected void findView() {
        tvRunInfoResult = findViewById(R.id.tv_run_result);
        tvErrResult = findViewById(R.id.tv_err_result);
        tvLock = findViewById(R.id.tv_lock_state);
        tvSpeed = findViewById(R.id.tv_speed);
        tvCurMileage = findViewById(R.id.tv_current_mileage);
        tvSurplusMileage = findViewById(R.id.tv_surplus_mileage);
        tvTotalMileage = findViewById(R.id.tv_total_mileage);
        tvRideTime = findViewById(R.id.tv_ride_time);
        tvChargeMos = findViewById(R.id.tv_charge_mos);
        tvDischargeMos = findViewById(R.id.tv_discharge_mos);
        tvPower = findViewById(R.id.tv_power);
        tvSoh = findViewById(R.id.tv_soh);
        tvHighTemp = findViewById(R.id.tv_high_temp);
        tvLowTemp = findViewById(R.id.tv_low_temp);
        tvMosTemp = findViewById(R.id.tv_mos_temp);
        tvOtherTemp = findViewById(R.id.tv_other_temp);
        tvCurrent = findViewById(R.id.tv_current);
        tvVoltage = findViewById(R.id.tv_voltage);
        tvChargeState = findViewById(R.id.tv_charge_state);
        tvAccAd = findViewById(R.id.tv_accelerate_ad);
        tvBrakeAd = findViewById(R.id.tv_brake_ad);
        tvErrContent = findViewById(R.id.tv_err_content);
        btnConfirm = findViewById(R.id.btn_confirm);
        btnInf = findViewById(R.id.btn_inf);
        btnCcf = findViewById(R.id.btn_ccf);
        btnTes = findViewById(R.id.btn_tes);
        llInf = findViewById(R.id.ll_inf);
        llCcf = findViewById(R.id.ll_ccf);
        llTes = findViewById(R.id.ll_tes);
        tvLedState = findViewById(R.id.tv_led_status);
        tvAccMode = findViewById(R.id.tv_accelerate_mode);

        tvLock1 = findViewById(R.id.tv_lock_state1);
        tvSpeed1 = findViewById(R.id.tv_speed1);
        tvCurMileage1 = findViewById(R.id.tv_current_mileage1);
        tvSurplusMileage1 = findViewById(R.id.tv_surplus_mileage1);
        tvTotalMileage1 = findViewById(R.id.tv_total_mileage1);
        tvRideTime1 = findViewById(R.id.tv_ride_time1);
        tvPower1 = findViewById(R.id.tv_power1);
        tvCurrent1 = findViewById(R.id.tv_current1);
        tvVoltage1 = findViewById(R.id.tv_voltage1);
        tvChargeState1 = findViewById(R.id.tv_charge_state1);
        tvAccAd1 = findViewById(R.id.tv_accelerate_ad1);
        tvBrakeAd1 = findViewById(R.id.tv_brake_ad1);
        tvLedState1 = findViewById(R.id.tv_led_status1);
        tvAccMode1 = findViewById(R.id.tv_accelerate_mode1);
        tvDlccMode = findViewById(R.id.tv_dlcc_mode);
        tvLockMode = findViewById(R.id.tv_lock_mode);
        tvDriveMode = findViewById(R.id.tv_drive_mode);
        tvGearS = findViewById(R.id.tv_gear_s);
        tvErrContent1 = findViewById(R.id.tv_err_content1);

        tvSpeed2 = findViewById(R.id.tv_speed2);
        tvCurMileage2 = findViewById(R.id.tv_current_mileage2);
        tvSurplusMileage2 = findViewById(R.id.tv_surplus_mileage2);
        tvTotalMileage2 = findViewById(R.id.tv_total_mileage2);
        tvRideTime2 = findViewById(R.id.tv_ride_time2);
        tvDlccMode1 = findViewById(R.id.tv_dlcc_mode1);
        tvControlTemp = findViewById(R.id.tv_control_temp);
        tvMotorTemp = findViewById(R.id.tv_motor_temp);
        tvLimitValue = findViewById(R.id.tv_limit_value);
        tvDriveValue = findViewById(R.id.tv_drive_value);
        tvBrakeValue = findViewById(R.id.tv_brake_value);
        tvControlOpen = findViewById(R.id.tv_control_open);
        tvBatteryFail = findViewById(R.id.tv_battery_fail);
        tvErrContent2 = findViewById(R.id.tv_err_content2);
    }

    @Override
    protected void initData() {

    }

    private final int MODE_INF = 0;
    private final int MODE_CCF = 1;
    private final int MODE_TES = 2;
    private int mode = MODE_CCF;

    @Override
    protected void initView() {
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onConfirm();
                }
                dismiss();
            }
        });

        btnCcf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mode = MODE_CCF;
                ViewUtils.showView(llCcf);
                ViewUtils.hideView(llInf);
                ViewUtils.hideView(llTes);
                btnCcf.setSelected(true);
                btnInf.setSelected(false);
                btnTes.setSelected(false);
            }
        });

        btnInf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mode = MODE_INF;
                ViewUtils.showView(llInf);
                ViewUtils.hideView(llCcf);
                ViewUtils.hideView(llTes);
                btnCcf.setSelected(false);
                btnInf.setSelected(true);
                btnTes.setSelected(false);
            }
        });

        btnTes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mode = MODE_TES;
                ViewUtils.hideView(llInf);
                ViewUtils.hideView(llCcf);
                ViewUtils.showView(llTes);
                btnCcf.setSelected(false);
                btnInf.setSelected(false);
                btnTes.setSelected(true);
            }
        });

        btnInf.performClick();
    }

    public void refreshInfo(CarRunReport CarRunState) {
        tvLock.setText(String.valueOf(CarRunState.lock));
        tvSpeed.setText(String.format(Locale.US,"%.1f km/h", CarRunState.speed));
        tvCurMileage.setText(String.format(Locale.US,"%.1f km", CarRunState.curMileage));
        tvSurplusMileage.setText(String.format(Locale.US,"%.1f km", CarRunState.surplusMileage));
        tvTotalMileage.setText(String.format(Locale.US,"%.1f km", CarRunState.totalMileage));
        tvRideTime.setText(String.format(Locale.US,"%d S", CarRunState.rideTime));
        tvChargeMos.setText(String.valueOf(CarRunState.chargeMos));
        tvDischargeMos.setText(String.valueOf(CarRunState.dischargeMos));
        tvPower.setText(String.format(Locale.US,"%.1f %%", CarRunState.power * 0.1f));
        tvSoh.setText(String.format(Locale.US,"%.1f %%", CarRunState.soh * 0.1f));
        tvHighTemp.setText(String.format(Locale.US,"%d ℃", CarRunState.eleCoreHigh));
        tvLowTemp.setText(String.format(Locale.US,"%d ℃", CarRunState.eleCoreLow));
        tvMosTemp.setText(String.format(Locale.US,"%d ℃", CarRunState.mosTemp));
        tvOtherTemp.setText(String.format(Locale.US,"%d ℃", CarRunState.otherTemp));
        tvCurrent.setText(String.format(Locale.US,"%d ma", CarRunState.current));
        tvVoltage.setText(String.format(Locale.US,"%d mv", CarRunState.voltage));
        tvChargeState.setText(String.format(Locale.US,"%d", CarRunState.chargeFlag));
        tvAccAd.setText(String.format(Locale.US,"%d", CarRunState.accelerateAD));
        tvBrakeAd.setText(String.format(Locale.US,"%d / %d", CarRunState.brakeADLeft, CarRunState.brakeADRight));
        tvAccMode.setText(String.format(Locale.US,"%d",CarRunState.adMode));
        tvLedState.setText(String.format(Locale.US,"%d",CarRunState.ledState));
//        tvErrContent.setText(CarConfig.errCode);

        tvLock1.setText(String.valueOf(CarRunState.lock));
        tvSpeed1.setText(String.format(Locale.US,"%.1f km/h", CarRunState.speed));
        tvCurMileage1.setText(String.format(Locale.US,"%.1f km", CarRunState.curMileage));
        tvSurplusMileage1.setText(String.format(Locale.US,"%.1f km", CarRunState.surplusMileage));
        tvTotalMileage1.setText(String.format(Locale.US,"%.1f km", CarRunState.totalMileage));
        tvRideTime1.setText(String.format(Locale.US,"%d S", CarRunState.rideTime));
        tvPower1.setText(String.format(Locale.US,"%.1f %%", CarRunState.power * 0.1f));
        tvCurrent1.setText(String.format(Locale.US,"%d ma", CarRunState.current));
        tvVoltage1.setText(String.format(Locale.US,"%d mv", CarRunState.voltage));
        tvChargeState1.setText(String.format(Locale.US,"%d", CarRunState.chargeFlag));
        tvAccAd1.setText(String.format(Locale.US,"%d", CarRunState.accelerateAD));
        tvBrakeAd1.setText(String.format(Locale.US,"%d / %d", CarRunState.brakeADLeft, CarRunState.brakeADRight));
        tvAccMode1.setText(String.format(Locale.US,"%d",CarRunState.adMode));
        tvLedState1.setText(String.format(Locale.US,"%d",CarRunState.ledState));
//        tvErrContent1.setText(CarConfig.errCode);
        tvDlccMode.setText(String.format(Locale.US,"%d",CarRunState.carCruise));
        tvLockMode.setText(String.format(Locale.US,"%d",CarRunState.carLockMode));
        tvDriveMode.setText(String.format(Locale.US,"%d",CarRunState.carOpenMode));
        tvGearS.setText(String.format(Locale.US,"%d",CarRunState.sGearMode));

        tvSpeed2.setText(String.format(Locale.US,"%.1f km/h", CarRunState.speed));
        tvCurMileage2.setText(String.format(Locale.US,"%.1f km", CarRunState.curMileage));
        tvSurplusMileage2.setText(String.format(Locale.US,"%.1f km", CarRunState.surplusMileage));
        tvTotalMileage2.setText(String.format(Locale.US,"%.1f km", CarRunState.totalMileage));
        tvRideTime2.setText(String.format(Locale.US,"%d S", CarRunState.rideTime));
//        tvErrContent2.setText(CarConfig.errCode);
        tvDlccMode1.setText(String.format(Locale.US,"%d",CarRunState.carCruise));
        tvControlTemp.setText(String.format(Locale.US,"%d",CarRunState.controlTemp));
        tvMotorTemp.setText(String.format(Locale.US,"%d",CarRunState.motorTemp));
        tvLimitValue.setText(String.format(Locale.US,"%d",CarRunState.curLimitValue));
        tvDriveValue.setText(String.format(Locale.US,"%d",CarRunState.curDriveValue));
        tvBrakeValue.setText(String.format(Locale.US,"%d",CarRunState.curBrakeValue));
        tvControlOpen.setText(String.format(Locale.US,"%d",CarRunState.controlOpen));
        tvBatteryFail.setText(String.format(Locale.US,"%d",CarRunState.batteryFail));
    }


    public void showErrInfo(String err) {
        tvErrContent.setText(String.format(Locale.US,"%s",err));
        tvErrContent1.setText(String.format(Locale.US,"%s",err));
        tvErrContent2.setText(String.format(Locale.US,"%s",err));
    }




    /*@Subscribe(threadMode = ThreadMode.MAIN)
    public void onReportEvent(CmdReportEvent event) {
        if (event == null) {
            return ;
        }
        LogUtils.i("onReportEvent","上报数据====：" + event.cmdStr);
        if(event.cmdStr.startsWith("+RESP:OKCCF")){
            mode = MODE_CCF;
            ViewUtils.showView(llCcf);
            ViewUtils.hideView(llInf);
            ViewUtils.hideView(llTes);
            ViewUtils.hideView(btnInf);
            ViewUtils.hideView(btnTes);
        }else if(event.cmdStr.startsWith("+RESP:OKTES")){
            mode = MODE_TES;
            ViewUtils.hideView(llInf);
            ViewUtils.hideView(llCcf);
            ViewUtils.showView(llTes);
            ViewUtils.hideView(btnCcf);
            ViewUtils.hideView(btnInf);
        }else if(event.cmdStr.startsWith("+RESP:OKINF")) {
            mode = MODE_INF;
            ViewUtils.showView(llInf);
            ViewUtils.hideView(llCcf);
            ViewUtils.hideView(llTes);
            ViewUtils.hideView(btnCcf);
            ViewUtils.hideView(btnTes);
        }
        refreshInfo();
    }*/
}
