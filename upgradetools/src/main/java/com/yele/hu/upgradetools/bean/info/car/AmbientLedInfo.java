package com.yele.hu.upgradetools.bean.info.car;

public class AmbientLedInfo {
    /**
     * <相应模式>:
     * 0x0 模式统一(各模式属于成套情况下)
     * 0x1 骑行模式
     * 0x2 待机模式
     * 0x3 充电模式41
     * 0x4 解锁模式
     * 0X5 报错模式
     */
    public int carMode = 0x0;

    /**
     * <样式>:
     * 0x0: 关闭
     * 0x1: 开
     * 0x2: 单向循环
     * 0x3: 双向循环
     * 0x4: 下坠循环
     * 0x5: 呼吸循环
     * 0x6: 上升循环
     * 0x7: 瀑布循环
     */
    public int atmosphereLightStyle = 0x0;

    /**
     * <RGB 颜色值>:
     * FC0003:红色
     * FFFC03:黄色
     * 03FC00:绿色
     * 03FFFC:青色
     * 0003FC:蓝色
     * FC03FF:紫色
     */
    public String rgbColor;

    /**
     * <流速>: 0-100
     */
    public int flowSpeed = 0;
}