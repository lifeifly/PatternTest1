package com.yele.hu.blesdk520demo.view.activity.base;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;

import androidx.annotation.Nullable;

/**
 * 基础界面
 */
public abstract class BaseActivity extends Activity {

    private static final String TAG = "BaseActivity";
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        beforeInit();

        setContentView(getResId());

        findView();

        initData();

        initView();
    }

    /**
     * 在设置界面之前的初始化操作
     * (放在这里面，有时候可以用于必须横屏的界面的统一处理等，如果需要不同可以覆盖处理)
     */
    protected void beforeInit(){

    }

    /**
     * 获取界面的现实布局ID
     * @return 返回现实布局ID
     */
    protected abstract int getResId() ;

    /**
     * 绑定控件
     */
    protected abstract void findView();

    /**
     * 初始化数
     */
    protected abstract void initData();

    /**
     * 初始化界面
     */
    protected abstract void initView();

    /**
     * 3秒钟内第一次点击退出
     */
    protected boolean clickOnceBack(){
        return true;
    }

    /**
     * 3秒钟内第二次点击退出
     * @return 是否退出界面，默认选择是
     */
    protected boolean clickBackTwice(){
        return true;
    }

    private long curClickTime = 0;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            long time = System.currentTimeMillis();
            if (time - curClickTime > 3000) {
                if (!clickOnceBack()) {
                    return false;
                }
                curClickTime = time;
            }else{
                if(clickBackTwice()){
                    return super.dispatchKeyEvent(event);
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }
}
