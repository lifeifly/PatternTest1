package com.yele.downloadlib.bean;

public class DownloadFlag {

    public static final String ACTION_START = "com.yele.download.start";        // 下载某文件
    public static final String ACTION_STOP = "com.yele.download.stop";          // 暂停下载某文件
    public static final String ACTION_END = "com.yele.download.end";            // 结束当前的下载内容
    public static final String RESULT_INIT_SUCCESS = "com.yele.download.result.init"; // 返回当前的下载情况-某文件开始下载
    public static final String RESULT_INIT_FAIL = "com.yele.download.result.init.fail"; // 返回当前的下载情况-某文件初始加载失败
    public static final String RESULT_LOAD_UPDATE = "com.yele.download.result.load.part"; // 返回当前的下载情况-某文件下载更新
    public static final String RESULT_LOAD_STOP = "com.yele.download.result.load.stop"; // 返回当前的下载情况-某文件暂停下载
    public static final String RESULT_LOAD_DELETE = "com.yele.download.result.load.delete"; // 返回当前的下载情况-某文件移除下载信息
    public static final String RESULT_LOAD_FAIL = "com.yele.download.result.load.fail"; // 返回当前的下载情况-某文件下载失败
    public static final String RESULT_FINISH = "com.yele.download.result.finish"; // 返回当前的下载情况-某文件下载完成

    public static final int GET_LOAD_SIZE = 0X01;       // 加载需要加载内容的大小成功
    public static final int FAIL_LOAD_SIZE = 0x81;      // 加载失败-获取目标内容大小
    public static final int GET_LOAD_FILE_PART = 0X02;  // 加载成功-加载部分文件，进度更新
    public static final int FAIL_LOAD_FILE_PART = 0X82; // 加载失败-加载部分文件
    public static final int STOP_LOAD_FILE = 0X03;      // 暂停加载文件
    public static final int GET_LOAD_FINISH = 0X04;     // 加载成功

}
