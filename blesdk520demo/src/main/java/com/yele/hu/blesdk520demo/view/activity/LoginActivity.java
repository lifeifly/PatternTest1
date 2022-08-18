package com.yele.hu.blesdk520demo.view.activity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.yele.hu.blesdk520demo.R;
import com.yele.hu.blesdk520demo.util.ToastUtil;
import com.yele.hu.blesdk520demo.view.activity.base.BasePerActivity;


public class LoginActivity extends BasePerActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";

    @Override
    protected int getResId() {
        return R.layout.activity_login;
    }

    private Button btnLogin;

    @Override
    protected void findView() {
        btnLogin = findViewById(R.id.btn_login);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        btnLogin.setOnClickListener(this);

        if (!hasReadPhoneStatePermission()) {
            requestReadPhoneStatePermission();
        }else{
            if (!hasBleOpenPermission()) {
                requestBleOpenPermission();
            } else {
                if (!hasWriteOrReadPermission()) {
                    requestSysWORPermission();
                } else {
                    if (!hasLocationPermission()) {
                        requestLocationPermission();
                    }
                }
            }
        }

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_login:
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
//                startActivity(new Intent(LoginActivity.this, SelectModeActivity.class));
                break;
        }
    }

    @Override
    protected void ensureReadPhoneStatePermission() {
        super.ensureReadPhoneStatePermission();
        if (!hasBleOpenPermission()) {
            requestBleOpenPermission();
        } else {
            if (!hasWriteOrReadPermission()) {
                requestSysWORPermission();
            } else {
                if (!hasLocationPermission()) {
                    requestLocationPermission();
                }
            }
        }
    }

    @Override
    protected void refuseReadPhoneStatePermission() {
        super.refuseReadPhoneStatePermission();
        ToastUtil.showShort(LoginActivity.this,"I need read phone permission to read debug log!");
    }

    @Override
    protected void ensureBleOpenPermission() {
        super.ensureBleOpenPermission();
        if (!hasWriteOrReadPermission()) {
            requestSysWORPermission();
        } else {
            if (!hasLocationPermission()) {
                requestLocationPermission();
            }
        }
    }

    @Override
    protected void refuseBleOpenPermission() {
        super.refuseBleOpenPermission();
        ToastUtil.showShort(LoginActivity.this,"I need control bluetooth to communicate with device!");
    }

    @Override
    protected void ensureROWPermission() {
        super.ensureROWPermission();
        if (!hasLocationPermission()) {
            requestLocationPermission();
        }
    }

    @Override
    protected void refuseROWPermission() {
        super.refuseROWPermission();
        ToastUtil.showShort(LoginActivity.this,"I need read or write sdcard to upgrade device !");
    }

    @Override
    protected void ensureLocationPermission() {
        super.ensureLocationPermission();
    }
}