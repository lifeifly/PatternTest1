package com.yele.huht.bluetoothdemo.view.dialog;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.yele.baseapp.view.dialog.BaseDialog;
import com.yele.huht.bluetoothdemo.R;

public class DialogOutMode extends BaseDialog {

    public DialogOutMode(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getResId() {
        return R.layout.dialog_out_mode;
    }

    private EditText etClear,etMode,etReadConfig;
    private Button btnCancel,btnConfirm;

    @Override
    protected void findView() {
        etClear = findViewById(R.id.et_clear);
        etMode = findViewById(R.id.et_mode);
        btnCancel = findViewById(R.id.btn_cancel);
        btnConfirm = findViewById(R.id.btn_confirm);
        etReadConfig = findViewById(R.id.et_read_config);
    }

    @Override
    protected void initData() {
        /*etClear.setText(CarConfig.clearData + "");
        etMode.setText(CarConfig.shippingMode + "");
        etReadConfig.setText(CarConfig.hasReadConfig + "");*/

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* CarConfig.clearData = Integer.parseInt(etClear.getText().toString());
                CarConfig.shippingMode = Integer.parseInt(etMode.getText().toString());
                CarConfig.hasReadConfig = Integer.parseInt(etReadConfig.getText().toString());
                EventBus.getDefault().post(new CmdSendEvent(CmdFlag.CMD_OUT_MODE));*/
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
}
