package com.easefun.polyvsdk.bean;

public class PolyvDownloadInfo {
    // vid
    private String vid;
    // 时长
    private String duration;
    // 文件大小
    private long filesize;
    // 码率
    private int bitrate;
    // 标题
    private String title;
    // 已下载的文件大小(mp4)/已下载的文件个数(ts)
    private long percent;
    // 总文件大小(mp4)/总个数(ts)
    private long total;

    public PolyvDownloadInfo(){}

    public PolyvDownloadInfo(String vid, String duration, long filesize, int bitrate, String title) {
        this.vid = vid;
        this.duration = duration;
        this.filesize = filesize;
        this.bitrate = bitrate;
        this.title = title;
    }

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public long getFilesize() {
        return filesize;
    }

    public void setFilesize(long filesize) {
        this.filesize = filesize;
    }

    public int getBitrate() {
        return bitrate;
    }

    public void setBitrate(int bitrate) {
        this.bitrate = bitrate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getPercent() {
        return percent;
    }

    public void setPercent(long percent) {
        this.percent = percent;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "PolyvDownloadInfo{" +
                "vid='" + vid + '\'' +
                ", duration='" + duration + '\'' +
                ", filesize=" + filesize +
                ", bitrate=" + bitrate +
                ", title='" + title + '\'' +
                ", percent=" + percent +
                ", total=" + total +
                '}';
    }
}
