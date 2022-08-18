package com.yele.huht.bluetoothsdklib.policy.downloadlib.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class FileInfo implements Parcelable {
    /* 目标下载链接 */
    public String url;
    /* 目标文件的名称 */
    public String fileName;
    /* 目标文件的保存地址 */
    public String localPath;
    /* 目标文件的大小 */
    public long size;
    /* 目标文件已经完成的大小 */
    public long finished;
    /* 目标的MD5 */
    public String md5;
    /* 忽略是否已经下载了，true直接重新下载 */
    public boolean ignoreLoad = false;

    public int getPercent() {
        int percent = 0;
        float complete = (float) ((double)finished * 1.0 / (double)size) ;
        percent = (int) (complete * 1000);
        if (percent == 1000 && finished != size) {
            percent = 999;
        }
        return percent;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.url);
        dest.writeString(this.fileName);
        dest.writeString(this.localPath);
        dest.writeLong(this.size);
        dest.writeLong(this.finished);
        dest.writeString(this.md5);
        dest.writeByte(this.ignoreLoad ? (byte) 1 : (byte) 0);
    }

    public FileInfo() {
    }

    protected FileInfo(Parcel in) {
        this.url = in.readString();
        this.fileName = in.readString();
        this.localPath = in.readString();
        this.size = in.readLong();
        this.finished = in.readLong();
        this.md5 = in.readString();
        this.ignoreLoad = in.readByte() != 0;
    }

    public static final Creator<FileInfo> CREATOR = new Creator<FileInfo>() {
        @Override
        public FileInfo createFromParcel(Parcel source) {
            return new FileInfo(source);
        }

        @Override
        public FileInfo[] newArray(int size) {
            return new FileInfo[size];
        }
    };
}
