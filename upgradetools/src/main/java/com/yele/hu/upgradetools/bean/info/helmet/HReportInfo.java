package com.yele.hu.upgradetools.bean.info.helmet;

/**
 * Copyright (C),2020-2021,杭州野乐科技有限公司
 * FileName: HReportInfo
 *
 * @Author: Chenxc
 * @Date: 2021/7/21 19:12
 * @Description: 头盔状态上报信息类集合
 * History:
 * <author> <time><version><desc>
 */
public class HReportInfo {
    /**
     * 开关机状态：
     *          0：关机
     *          1：开机
     */
    public int powerState = 0;
    /**
     * 灯状态：0~99
     *      0：熄灭；
     *      1：LOG显示；
     *      2：左转向
     *      3：右转向
     *      4：骑行状态
     *      5：刹车状态
     */
    public int ledState = 0;
    /**
     * 前灯转向时状态
     *      0：流动
     *      1：闪烁
     */
    public int frontTurnState = 0;
    /**
     * 前灯骑行时状态
     *      0：呼吸
     *      1：闪烁
     */
    public int frontDriveState = 0;
    /**
     * 前灯骑行时的颜色
     */
    public String frontDriveColor;
    /**
     * 后灯转向时
     *      0：流动效果
     *      1：闪烁效果
     *      2：呼吸效果
     */
    public int rearTurnState = 0;
    /**
     * 后灯骑行时状态
     *      0：骑行闪电效果
     *      1：骑行螺旋效果
     *      2：骑行菱形效果
     *      3：骑行闪电加动画效果
     *      4：骑行扇形开合
     *      5：骑行动画
     */
    public int rearDriveState = 0;
    /**
     * 后灯骑行时颜色
     */
    public String rearDriveColor;
    /**
     * 警示灯状态
     *      1：警示灯状态1
     *      2：警示灯状态2
     */
    public int warningState;
    /**
     * 灯效模式
     *      0：白天模式
     *      1：夜间模式
     *      2：雨雾模式
     *      3：DIY模式
     */
    public int showMode;
    /**
     * 头盔锁状态
     *      0：锁定
     *      1：打开
     */
    public int lockState;
    /**
     * 体感转向状态
     *      0：关闭体感转向功能
     *      1：打开体感转向功能
     *      2：左转摆头动作学习
     *      3：右转摆头动作学习
     *      4：未配置
     */
    public int bodyTurnState;
    /**
     * 前灯流水速度。1/2/3 3速度最快
     */
    public int frontFlowSpeed;
    /**
     * 前灯亮度，1~9  9表示最亮
     */
    public int frontLight;
    /**
     * 尾灯流水速度。1/2/3 3速度最快
     */
    public int rearFlowSpeed;
    /**
     * 尾灯亮度，1~9  9表示最亮
     */
    public int rearLight;
    /**
     * 剩余电量 0~100 单位 %
     */
    public int battery;
    /**
     * 剩余续航电量 0~9999 单位小时
     */
    public float surplusTime;
    /**
     * 头盔翻到状态
     *      0：正常
     *      1：翻倒
     */
    public int fallState;
    /**
     * 错误码信息
     */
    public int errCode;
}
