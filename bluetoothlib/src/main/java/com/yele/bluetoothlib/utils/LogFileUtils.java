package com.yele.bluetoothlib.utils;

import android.os.Environment;

import com.yele.baseapp.utils.LogUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class LogFileUtils {
    private static final String tag = "LogFileUtils";

    public static String getDeviceInfoPath(){
        String filePath = "";
        boolean hasSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (hasSDCard) { // 判断当前是否有SD卡
            // 如果有就缓存在SD卡下
            filePath = Environment.getExternalStorageDirectory().toString() + File.separator + "BleTest" + File.separator;
        } else // 系统下载缓存根目录的
            filePath = Environment.getDownloadCacheDirectory().toString() + File.separator + "BleTest" + File.separator;


        return filePath;
    }

    /**
     * 检查当前路径是否存在，如果不存在就创建路径
     * @param path
     */
    public static void checkDirectoryPath(String path) {
        File dir = new File(path);	//
        if(!dir.exists()){		//判断当前路径是否存在
            dir.mkdirs();
        }
    }

    /**
     * 判断当前文件是否存在
     *
     * @param files
     * @return
     */
    public static boolean isFileExist(String files) {
        boolean isExist;
        File file = new File(files); // 获取当前的文件
        isExist = file.exists(); // 获取当前文件是否存在
        return isExist;
    }

    /**
     * 获取根目录的路径
     */
    public static String getRootDirectoryPath() {
        String filePath = "";
        boolean hasSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (hasSDCard) { // 判断当前是否有SD卡
            // 如果有就缓存在SD卡下
//            filePath = Environment.getExternalStorageDirectory().toString() + File.separator + "okai"
//                    + File.separator;
            filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString() + File.separator + "okai"
                    + File.separator;
        } else // 系统下载缓存根目录的
            filePath = Environment.getDownloadCacheDirectory().toString() + File.separator + "okai"
                    + File.separator;
        return filePath;
    }

    /**
     * 获取项目路径
     * @return 当前的项目路径
     */
    public static String getProDirectoryPath() {
        return getRootDirectoryPath() + "BleTest" + File.separator;
    }
    /**
     * 获取记录保存的文件夹
     * @return
     */
    public static String getLogFiles(){
        return getProDirectoryPath() + "log" + File.separator;
    }
    /**
     * 判断当前本地是否有该记录文件
     * @param serverName 当前服务的名字
     * @return
     */
    public static boolean isLocalHasLog(String serverName){
        boolean state = false;
        String path = getLogFiles() + serverName;
        File file = new File(path);
        if(!file.exists()){
            state = true;
        }
        return state;
    }

    /**
     * 创建文件夹
     */
    public static void createFiles(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                File parent = file.getParentFile();
                if (parent != null) {
                    if (!parent.exists()) {
                        boolean create = parent.mkdirs();
                        LogUtils.i(tag,"创建文件夹 " + create);
                    }
                }else{
                    File dir = new File(file.getParent());
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                }
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存当前的内容成文件
     * 首先先判断路径是否存在，如果路径不存在，则创建路径、文件
     *
     * @param filePath
     * @param content
     */
    public static void saveData(String filePath, String content) {
        try {
            createFiles(filePath); // 判断并创建当前文件
            RandomAccessFile files = new RandomAccessFile(filePath, "rw");
            files.seek(files.length());
            String str = content + "\n";
            files.write(str.getBytes());
            files.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            LogUtils.e(tag, "当前文件打开失败" );
        } catch (IOException e) {
            LogUtils.e(tag, "当前文件写入失败");
        } catch (Exception e) {
            LogUtils.e(tag, "当前文件写入失败2");
        }
    }

    public static List<String> readData(String filePath) {
        List<String> list = null;
        try{
            FileInputStream is = new FileInputStream(filePath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"));
            String line = null;
            list = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                list.add(line);
            }
        }catch (FileNotFoundException e) {
            LogUtils.e(tag, "当前文件打开失败");
        } catch (IOException e) {
            LogUtils.e(tag, "当前文件写入失败");
        } catch (Exception e) {
            LogUtils.e(tag, "当前文件写入失败2");
        }
        return list;
    }

    /**
     *
     * @param datas
     * @param fileName
     */
    public static boolean saveFiles(byte[] datas, String fileName) {
        boolean state = false;
        String path = getLogFiles();
        path += fileName;
        File file = new File(path);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            saveData(datas, file);
            state = true;
        } catch (IOException e) {
            state = false;
        }
        return state;

    }
    /**
     * 保存当前的数据
     * @param data
     * @param file
     */
    public static void saveData(byte[] data,File file){
        InputStream is = new ByteArrayInputStream(data);
        if(is != null){
            OutputStream os = null;
            try {
                os = new FileOutputStream(file);
                byte[] buff = new byte[4*1024];
                int temp = 0;
                while((temp = is.read(buff)) != -1){
                    os.write(buff,0,temp);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    os.close();
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

//    /**
//     * 保存当前点的坐标系
//     *
//     * @param context
//     * @param fileName
//     * @param listInfo
//     */
//    public static String savePointsToFile(Context context, String fileName, List<CarLocationInfo> listInfo) {
//        String path = getLogFiles();
//        path += fileName;
//        saveData(context, path, "{\"list\":[");
//        for (int i = 0; i < listInfo.size(); i++) {
//            String buff = listInfo.get(i).toString();
//            if(i != listInfo.size() - 1){
//                buff += ",";
//            }
//            saveData(context, path, buff);
//        }
//        saveData(context, path, "]}");
//        return path;
//    }
//    /**
//     * 保存当前
//     * @param context
//     * @param filePath
//     * @param listInfo
//     * @return
//     */
//    public static void savePointsCoverFile(Context context, String filePath, List<CarLocationInfo> listInfo){
//        LogUtils.w(tag, "文件删除：" + filePath);
//        deleteFiles(filePath);
//        saveData(context, filePath, "{\"list\":[");
//        for (int i = 0; i < listInfo.size(); i++) {
//            String buff = listInfo.get(i).toString();
//            if(i != listInfo.size() - 1){
//                buff += ",";
//            }
//            saveData(context, filePath, buff);
//        }
//        saveData(context, filePath, "]}");
//    }

    /**
     * 删除对应文件路径
     * @param filePath 当前的文件路径
     */
    public static void deleteFiles(String filePath){
        File file = new File(filePath); // 获取当前的文件
        if(file.exists()){ // 获取当前文件是否存在
            file.delete();
        }
    }

    public static String getFilePath(File file) {
        String path = file.getAbsolutePath();
        String filePath = path.substring(0, path.lastIndexOf("/") + 1);
        return filePath;
    }

    public static String getFilePath(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            return getFilePath(file);
        }
        return null;
    }
}
