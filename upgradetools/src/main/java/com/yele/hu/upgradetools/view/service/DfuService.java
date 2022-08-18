package com.yele.hu.upgradetools.view.service;

import android.app.Activity;

import no.nordicsemi.android.dfu.DfuBaseService;

public class DfuService extends DfuBaseService {

    @Override
    protected Class<? extends Activity> getNotificationTarget() {
        return null;
    }
}
