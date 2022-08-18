package com.yele.hu.blesdk520demo.util;

import android.view.View;

public class ViewUtils {
    /**
     * 隐藏当前的视图
     * @param view 当前的视图
     */
    public static void hideView(View view) {
        if (view.getVisibility() == View.VISIBLE) {
            view.setVisibility(View.GONE);
        }
    }

    /**
     * 显示当前的视图
     * @param view 需要显示的视图
     */
    public static void showView(View view) {
        if (view.getVisibility() == View.GONE
                || view.getVisibility() == View.INVISIBLE) {
            view.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 判断视图是否是显示状态
     * @param view 需要判断的视图
     * @return 是否可视
     */
    public static boolean isShow(View view) {
        return view.getVisibility() == View.VISIBLE;
    }
}
