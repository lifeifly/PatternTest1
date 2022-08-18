package com.yele.huht.bluetoothdemo.view.dialog;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.yele.baseapp.view.dialog.BaseDialog;
import com.yele.huht.bluetoothdemo.R;

public class DialogCarOutConfig extends BaseDialog {

    public DialogCarOutConfig(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getResId() {
        return R.layout.dialog_car_out_config;
    }

    private EditText etBrakeSelect,etUnit,etGear1,etGear2,etGear3,etGear4;
    private EditText etBatteryType,etElcGear,etLedMode,etTaillight,etSalesCode,etCustomerCode;
    private EditText etCarModel,etBleModel,etBleSwitch;
    private Button btnCancel,btnConfirm;

    @Override
    protected void findView() {
        etBrakeSelect = findViewById(R.id.et_brake_select);
        etUnit = findViewById(R.id.et_unit);
        etGear1 = findViewById(R.id.et_gear1);
        etGear2 = findViewById(R.id.et_gear2);
        etGear3 = findViewById(R.id.et_gear3);
        etGear4 = findViewById(R.id.et_gear4);
        etBatteryType = findViewById(R.id.et_battery_type);
        etElcGear = findViewById(R.id.et_elc_gear);
        etLedMode = findViewById(R.id.et_led_mode);
        etTaillight = findViewById(R.id.et_taillight);
        etSalesCode = findViewById(R.id.et_sales_code);
        etCustomerCode = findViewById(R.id.et_customer_code);
        btnCancel = findViewById(R.id.btn_cancel);
        btnConfirm = findViewById(R.id.btn_confirm);
        etCarModel = findViewById(R.id.et_car_model);
        etBleModel = findViewById(R.id.et_ble_model);
        etBleSwitch = findViewById(R.id.et_ble_switch);
    }

    @Override
    protected void initData() {
        /*etBrakeSelect.setText(CarConfig.brakeSelect + "");
        etUnit.setText(YBConfig.YB_SHOW_MODE + "");
        etGear1.setText(CarConfig.gear1 + "");
        etGear2.setText(CarConfig.gear2 + "");
        etGear3.setText(CarConfig.gear3 + "");
        etGear4.setText(CarConfig.gear4 + "");
        etBatteryType.setText(CarConfig.batteryType + "");
        etElcGear.setText(CarConfig.electronicBrakeSelect + "");
        etLedMode.setText(CarConfig.openCarLedMode + "");
        etTaillight.setText(CarConfig.taillightMode + "");
        etSalesCode.setText(CarConfig.salesLocationCode);
        etCustomerCode.setText(CarConfig.customerCode);
        etCarModel.setText(CarConfig.carModel);
        etBleModel.setText(YBConfig.bleModel + "");
        etBleSwitch.setText(YBConfig.bleSwitch+"");*/

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*CarConfig.brakeSelect = Integer.parseInt(etBrakeSelect.getText().toString());
                YBConfig.YB_SHOW_MODE = Integer.parseInt(etUnit.getText().toString());
                CarConfig.gear1 = Integer.parseInt(etGear1.getText().toString());
                CarConfig.gear2 = Integer.parseInt(etGear2.getText().toString());
                CarConfig.gear3 = Integer.parseInt(etGear3.getText().toString());
                CarConfig.gear4 = Integer.parseInt(etGear4.getText().toString());
                CarConfig.batteryType = Integer.parseInt(etBatteryType.getText().toString());
                CarConfig.electronicBrakeSelect = Integer.parseInt(etElcGear.getText().toString());
                CarConfig.openCarLedMode = Integer.parseInt(etLedMode.getText().toString());
                CarConfig.taillightMode = Integer.parseInt(etTaillight.getText().toString());
                CarConfig.salesLocationCode = etSalesCode.getText().toString();
                CarConfig.customerCode = etCustomerCode.getText().toString();
                CarConfig.carModel = etCarModel.getText().toString();
                YBConfig.bleModel = Integer.parseInt(etBleModel.getText().toString());
                YBConfig.bleSwitch = Integer.parseInt(etBleSwitch.getText().toString());*/
                if(listener != null){
                    listener.onClickListener();
                }
                dismiss();
            }
        });
    }

    @Override
    protected void initView() {
        /*if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }*/
    }

    @Override
    public void dismiss() {
        super.dismiss();
        /*if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }*/
    }


    public interface onClickListener{
        void onClickListener();
    }
    private onClickListener listener;
    public void setOnClickListener(onClickListener l){
        this.listener = l;
    }

}
