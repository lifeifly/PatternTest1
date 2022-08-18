package com.yele.hu.blesdk520demo.view.activity.base;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;

import androidx.annotation.NonNull;

public abstract class BaseDialog extends Dialog {

    public BaseDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (getWindow() != null) {
            getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        setCanceledOnTouchOutside(isOutCancel());
        super.onCreate(savedInstanceState);

        setContentView(getResId());

        findView();

        initData();

        initView();
    }

    protected boolean isOutCancel(){
        return false;
    }

    protected abstract int getResId();

    protected abstract void findView();

    protected abstract void initData();

    protected abstract void initView();

    protected OnBaseDialogListener mListener;

    public void setOnBaseDialogListener(OnBaseDialogListener listener) {
        this.mListener = listener;
    }

}
