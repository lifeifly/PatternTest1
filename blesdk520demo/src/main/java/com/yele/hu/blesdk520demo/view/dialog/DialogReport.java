package com.yele.hu.blesdk520demo.view.dialog;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.yele.blesdklibrary.bean.CarRunState;
import com.yele.hu.blesdk520demo.R;
import com.yele.hu.blesdk520demo.view.activity.base.BaseDialog;

import java.util.Locale;

public class DialogReport extends BaseDialog {

    public DialogReport(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getResId() {
        return R.layout.dialog_report;
    }

    private TextView tvRunInfoResult,tvErrResult;   // 上报的结果
    private TextView tvLock,tvSpeed,tvCurMileage,tvSurplusMileage,tvTotalMileage,tvRideTime;
    private TextView tvPower,tvChargeState;
    private TextView tvErrContent;

    private Button btnConfirm;

    private TextView tvAddMode,tvCarCruise,tvCarLockMode,tvCarOpenMode,tvCarSwitch;

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
        tvPower = findViewById(R.id.tv_power);
        tvChargeState = findViewById(R.id.tv_charge_state);
        tvErrContent = findViewById(R.id.tv_err_content);
        btnConfirm = findViewById(R.id.btn_confirm);
        tvAddMode = findViewById(R.id.tv_add_mode);
        tvCarCruise = findViewById(R.id.tv_car_cruise);
        tvCarLockMode = findViewById(R.id.tv_car_lock_mode);
        tvCarOpenMode = findViewById(R.id.tv_car_open_mode);
        tvCarSwitch = findViewById(R.id.tv_car_switch);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                if (mListener != null) {
                    mListener.onConfirm();
                }
            }
        });

    }

    public void showErrInfo(String err) {
        tvErrContent.setText(String.format(Locale.US,"%s",err));
    }

    public void refreshInfo(CarRunState carRunState) {
        tvLock.setText(String.valueOf(carRunState.lock));
        tvSpeed.setText(String.format(Locale.US,"%.1f km/h",carRunState.speed));
        tvCurMileage.setText(String.format(Locale.US,"%.1f km",carRunState.curMileage));
        tvSurplusMileage.setText(String.format(Locale.US,"%.1f km",carRunState.surplusMileage));
        tvTotalMileage.setText(String.format(Locale.US,"%.1f km",carRunState.totalMileage));
        tvRideTime.setText(String.format(Locale.US,"%d S",carRunState.ledState));


        tvPower.setText(String.format(Locale.US,"%.1f %%",carRunState.power));
        tvChargeState.setText(String.format(Locale.US,"%d",carRunState.chargeFlag));
        tvAddMode.setText(String.format(Locale.US,"%d",carRunState.addMode));
        tvCarCruise.setText(String.format(Locale.US,"%d",carRunState.carCruise));
        tvCarLockMode.setText(String.format(Locale.US,"%d",carRunState.carLockMode));
        tvCarOpenMode.setText(String.format(Locale.US,"%d",carRunState.carOpenMode));
        tvCarSwitch.setText(String.format(Locale.US,"%d",carRunState.carSwitch));
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
}
