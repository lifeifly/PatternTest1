package com.yele.blesdklibrary.data;

public class BLEUUIDs {

    public static final String BTLE_SERVICE_BATTERY_UUID				= "0000180F-0000-1000-8000-00805f9b34fb";			// 蓝牙设备电池的UUID
    public static final String BTLE_CHARAC_BATTERY_LEVEL_UUID			= "00002A19-0000-1000-8000-00805f9b34fb";			// 蓝牙设备电池等级，用于图片显示电量

    public static final String BTLE_DESCRIPTOR_CHARACTERISTIC_CONFIG	= "00002902-0000-1000-8000-00805f9b34fb";

    public static final String BTLE_SERVICE_DEVICE_INFO_UUID			= "0000180A-0000-1000-8000-00805f9b34fb";
    public static final String BTLE_CHARAC_SYSTEM_ID_UUID				= "00002A23-0000-1000-8000-00805f9b34fb";

    public static final String BTLE_SERVICE_RW_UUID						= "0000FFF0-0000-1000-8000-00805f9b34fb";
    public static final String BTLE_CHARAC_RW_5_UUID					= "0000FFF5-0000-1000-8000-00805f9b34fb";

    // kame_li, for Queclink
    public static final String TEST_SERVICE_UUID = "00002c00-0000-1000-8000-00805f9b34fb";		// 测试服务的UUID，用于发送数据
    public static final String COMMON_CHANNEL = "00002c01-0000-1000-8000-00805f9b34fb";			// 普通发送的数据通道
    public static final String QR_CODE_CHANNEL = "00002c02-0000-1000-8000-00805f9b34fb";			// 发送二维码的数据通道
    public static final String REPORT_CHANNEL = "00002c03-0000-1000-8000-00805f9b34fb";			// 报告的信道
    public static final String UPGRADE_CHANNEL = "00002c04-0000-1000-8000-00805f9b34fb";			// 更新固件的数据通道
    public static final String CONFIG_CHANNEL = "00002C10-0000-1000-8000-00805f9b34fb";			// 更新固件的数据通道
}
