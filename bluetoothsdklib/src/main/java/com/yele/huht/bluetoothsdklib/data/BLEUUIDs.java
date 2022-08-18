package com.yele.huht.bluetoothsdklib.data;


public class BLEUUIDs {
	
	
	public static final String BTLE_DISCOVER_FILTER_UUID				= "000055aa-0000-1000-8000-00805f9b34fb";
	
	
	public static final String BTLE_SERVICE_LINK_LOSS_ALERT_UUID		= "00001803-0000-1000-8000-00805f9b34fb";
	public static final String BTLE_SERVICE_ALERT_UUID					= "00001802-0000-1000-8000-00805f9b34fb";
	public static final String BTLE_CHARAC_ALERT_LEVEL_UUID				= "00002A06-0000-1000-8000-00805f9b34fb";
	public static final String BTLE_CHARAC_LINK_LOSS_ALERT_UUID_HW2		= "00002AFF-0000-1000-8000-00805f9b34fb";
	public static final int BTLE_CHARAC_ALERT_LEVEL_OFF_VAL				= 0x00;
	public static final int BTLE_CHARAC_ALERT_LEVEL_MILD_VAL            = 0x01;
	public static final int BTLE_CHARAC_ALERT_LEVEL_HIGH_VAL            = 0x02;
	public static final int BTLE_CHARAC_ALERT_LEVEL_WRITE_LEN           = 1;

	public static final String BTLE_SERVICE_BATTERY_UUID				= "0000180F-0000-1000-8000-00805f9b34fb";			// 蓝牙设备电池的UUID
	public static final String BTLE_CHARAC_BATTERY_LEVEL_UUID			= "00002A19-0000-1000-8000-00805f9b34fb";			// 蓝牙设备电池等级，用于图片显示电量
	public static final int BTLE_CHARAC_BATTERY_LEVEL_READ_LEN          = 1;


	public static final String BTLE_SERVICE_KEYS_UUID					= "0000FFE0-0000-1000-8000-00805f9b34fb";
	public static final String BTLE_CHARAC_KEYS_NOTIFICATION_UUID		= "0000FFE1-0000-1000-8000-00805f9b34fb";
	public static final String BTLE_DESCRIPTOR_CHARACTERISTIC_CONFIG	= "00002902-0000-1000-8000-00805f9b34fb";
	public static final int BTLE_CHARAC_KEYS_NOTIFICATION_READ_LEN      = 1;


	public static final String BTLE_SERVICE_DEVICE_INFO_UUID			= "0000180A-0000-1000-8000-00805f9b34fb";
	public static final String BTLE_CHARAC_SYSTEM_ID_UUID				= "00002A23-0000-1000-8000-00805f9b34fb";
	public static final int BTLE_CHARAC_SYSTEM_ID_READ_LEN              = 8;


	public static final String BTLE_SERVICE_RW_UUID						= "0000FFF0-0000-1000-8000-00805f9b34fb";
	public static final String BTLE_CHARAC_RW_1_UUID					= "0000FFF1-0000-1000-8000-00805f9b34fb";
	public static final String BTLE_CHARAC_RW_2_UUID					= "0000FFF2-0000-1000-8000-00805f9b34fb";
	public static final String BTLE_CHARAC_RW_3_UUID					= "0000FFF3-0000-1000-8000-00805f9b34fb";
	public static final String BTLE_CHARAC_RW_4_UUID					= "0000FFF4-0000-1000-8000-00805f9b34fb";
	public static final String BTLE_CHARAC_RW_5_UUID					= "0000FFF5-0000-1000-8000-00805f9b34fb";
	public static final String BTLE_CHARAC_RW_7_UUID					= "0000FFF7-0000-1000-8000-00805f9b34fb";
	public static final byte BTLE_CHARAC_RW_1_CLEANUP_VAL                = (byte) 0xAA;
	public static final int BTLE_CHARAC_RW_1_CLEANUP_LEN                = 1;
	public static final int BTLE_CHARAC_RW_5_DATA_LEN                   = 4;

	// PROXI - hejun 修改为灯具的编码
	public static final String BTLE_PROX_SERVICE_UUID					= "00001000-b5a3-f393-e0a9-e50e24dcca9e";
	public static final String BTLE_PROX_CHARAC_RD_UUID					= "00001001-b5a3-f393-e0a9-e50e24dcca9e";
	public static final String BTLE_PROX_CHARAC_WD_UUID          		= "00001002-b5a3-f393-e0a9-e50e24dcca9e";

	
	// kame
    public static String Service_uuid = "00001000-0000-1000-8000-00805f9b34fb";
    public static String Characteristic_uuid_TX = "00001002-0000-1000-8000-00805f9b34fb";
    public static String Characteristic_uuid_FUNCTION = "00001001-0000-1000-8000-00805f9b34fb";


	// kame_li, for Queclink
	public static final String TEST_SERVICE_UUID = "00002c00-0000-1000-8000-00805f9b34fb";		// 测试服务的UUID，用于发送数据
	public static final String COMMON_CHANNEL = "00002c01-0000-1000-8000-00805f9b34fb";			// 普通发送的数据通道
	public static final String QR_CODE_CHANNEL = "00002c02-0000-1000-8000-00805f9b34fb";			// 发送二维码的数据通道
	public static final String REPORT_CHANNEL = "00002c03-0000-1000-8000-00805f9b34fb";			// 报告的信道
	public static final String UPGRADE_CHANNEL = "00002c04-0000-1000-8000-00805f9b34fb";			// 更新固件的数据通道
	public static final String CONFIG_CHANNEL = "00002C10-0000-1000-8000-00805f9b34fb";			// 更新固件的数据通道
    public static final String BACKPACK_CHANNEL = "00002c07-0000-1000-8000-00805f9b34fb";       // 背包协议发送数据

	public static String CUR_CHANNEL = "";		// 当前的通道
}
