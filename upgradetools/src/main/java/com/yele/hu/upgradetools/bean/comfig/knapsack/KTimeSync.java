package com.yele.hu.upgradetools.bean.comfig.knapsack;

import java.util.Calendar;
import java.util.Date;

/**
 * Copyright (C),2020-2021,杭州野乐科技有限公司
 * FileName: KTimeSync
 *
 * @Author: Chenxc
 * @Date: 2021/8/4 14:31
 * @Description: 当地时间校准
 * History:
 * <author> <time><version><desc>
 */
public class KTimeSync {

    public int week;

    public int hour;

    public int min;

    public int sec;

    public int milliSec;

    public KTimeSync() {

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        int we = cal.get(Calendar.DAY_OF_WEEK);
        week = we > 0 ? we - 1 : 7;
        hour = cal.get(Calendar.HOUR_OF_DAY);
        min = cal.get(Calendar.MINUTE);
        sec = cal.get(Calendar.SECOND);
        milliSec = cal.get(Calendar.MILLISECOND);
    }
}
