package com.yele.hu.upgradetools.view.activity;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.yele.baseapp.utils.StringUtils;
import com.yele.baseapp.utils.ToastUtil;
import com.yele.baseapp.view.activity.BasePerActivity;
import com.yele.hu.upgradetools.R;
import com.yele.hu.upgradetools.bean.ActiveInfo;
import com.yele.hu.upgradetools.bean.LoginInfo;
import com.yele.hu.upgradetools.policy.http.HttpManager;
import com.yele.hu.upgradetools.policy.http.back.BackLoginState;
import com.yele.hu.upgradetools.util.PhoneUtils;
import com.yele.hu.upgradetools.util.SharedUtils;


/**
 * 登录
 */
public class LoginActivity extends BasePerActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";

    @Override
    protected int getResId() {
        return R.layout.activity_login;
    }

    private Button btnLogin;
    private EditText etAccount,etPassword;

    @Override
    protected void findView() {
        btnLogin = findViewById(R.id.btn_login);
        etAccount = findViewById(R.id.et_account);
        etPassword = findViewById(R.id.et_password);
    }

    @Override
    protected void initData() {
        LoginInfo loginInfo = SharedUtils.readLoginInfo(LoginActivity.this);
        if (loginInfo != null) {
            etAccount.setText(loginInfo.name);
            etPassword.setText(loginInfo.pwd);
        }

        etPassword.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                // 监听到回车键，会执行2次该方法。按下与松开
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    //按下事件
                    loginUser();
                }
            }
            return false;
        });
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
        if (view.getId() == R.id.btn_login) {
            loginUser();
        }
    }



    private void loginUser() {
        String pwd = etPassword.getText().toString();
        String userName = etAccount.getText().toString();
        if (StringUtils.isEmpty(pwd) || StringUtils.isEmpty(userName)) {
            ToastUtil.showShort(LoginActivity.this, R.string.input_pda_empty);
            return;
        }

        String imei = PhoneUtils.getIMEI(LoginActivity.this);

        if (StringUtils.isEmpty(imei)) {
            imei = "YL000000000003";
        }

        SharedUtils.saveLoginInfo(LoginActivity.this,userName,pwd);

        HttpManager.requestLoginState(userName, pwd,imei, new BackLoginState() {
            @Override
            public void loginSuccess(ActiveInfo info) {
                Intent intent = new Intent(LoginActivity.this, BleScanListActivity.class);
                startActivity(intent);
            }

            @Override
            public void loginFailed(int state, String err) {
                ToastUtil.showShort(LoginActivity.this,"Err Request:" + err)  ;
            }
        });

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
        //super.refuseReadPhoneStatePermission();
        ToastUtil.showShort(LoginActivity.this,getString(R.string.refuse_read_phone_state_permission));
    }

    @Override
    protected void ensureBleOpenPermission() {
        //super.ensureBleOpenPermission();
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
        //super.refuseBleOpenPermission();
        ToastUtil.showShort(LoginActivity.this,getString(R.string.refluse_ble_open_permission));
    }

    @Override
    protected void ensureROWPermission() {
        //super.ensureROWPermission();
        if (!hasLocationPermission()) {
            requestLocationPermission();
        }
    }

    @Override
    protected void refuseROWPermission() {
        //super.refuseROWPermission();
        ToastUtil.showShort(LoginActivity.this,getString(R.string.refuse_row_permission));
    }

    @Override
    protected void ensureLocationPermission() {
        //super.ensureLocationPermission();
    }

    @Override
    protected void refuseLocationPermission() {
        //super.refuseLocationPermission();
        ToastUtil.showShort(LoginActivity.this,getString(R.string.refuse_location_permission));
    }
}