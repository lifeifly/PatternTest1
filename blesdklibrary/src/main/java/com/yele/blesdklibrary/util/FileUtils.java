package com.yele.blesdklibrary.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class FileUtils {
    /**
     * 判断文件是否存在
     * @param path 文件路径
     * @return 文件是否存在
     */
    public static boolean isFileExists(String path) {
        if (path == null) {
            return false;
        }
        File file = new File(path);
        return file.exists() && file.isFile();
    }

    /**
     * 创建文件
     * @param path 目标路径
     */
    public static void createFile(String path) {
        if (path == null) {
            return;
        }
        File file = new File(path);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 删除文件
     * @param path 文件路径
     * @return 移除成功
     */
    public static boolean delFile(String path) {
        File file = new File(path);
        if (file.exists() && file.isFile()) {
            return file.delete();
        }
        return false;
    }

    /**
     * 判断路径是否是文件路径
     * @param path 路径信息
     * @return 是否是文件路径
     */
    public static boolean isFilePath(String path) {
        File file = new File(path);
        return file.isFile();
    }

    /**
     * 写数据到指定的文件中
     * @param path 文件路径
     * @param data 数据
     */
    public static void writeDataToFile(String path, byte[] data) {
        if (!isFileExists(path)) {
            createFile(path);
        }
        OutputStream os = null;
        try {
            os = new FileOutputStream(new File(path));
            os.write(data);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
