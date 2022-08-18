package com.yele.downloadlib.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class ThreadInfo implements Parcelable {

    /* 目标地址 */
    public String url;

    /* 线程ID */
    public int id;

    /* 目标起始加载地址 */
    public long startIndex;

    /* 目标结束加载地址 */
    public long endIndex;

    /* 已经加载完成的进度 */
    public long finished;

    /* 已经加载完成的进度 */
    public long size;

    /* 已经加载完成的进度 */
    public String path;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.url);
        dest.writeInt(this.id);
        dest.writeLong(this.startIndex);
        dest.writeLong(this.endIndex);
        dest.writeLong(this.finished);
        dest.writeLong(this.size);
        dest.writeString(this.path);
    }

    public ThreadInfo() {
    }

    protected ThreadInfo(Parcel in) {
        this.url = in.readString();
        this.id = in.readInt();
        this.startIndex = in.readLong();
        this.endIndex = in.readLong();
        this.finished = in.readLong();
        this.size = in.readLong();
        this.path = in.readString();
    }

    public static final Creator<ThreadInfo> CREATOR = new Creator<ThreadInfo>() {
        @Override
        public ThreadInfo createFromParcel(Parcel source) {
            return new ThreadInfo(source);
        }

        @Override
        public ThreadInfo[] newArray(int size) {
            return new ThreadInfo[size];
        }
    };
}
