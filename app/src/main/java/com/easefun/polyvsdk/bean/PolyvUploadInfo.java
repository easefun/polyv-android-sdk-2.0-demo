package com.easefun.polyvsdk.bean;

public class PolyvUploadInfo {
    // 标题
    private String title;
    // 文件路径
    private String filepath;
    // 文件大小
    private long filesize;
    // 描述
    private String desc;
    // 分类id
    private String cataid;
    // 已上传的文件大小
    private long percent;
    // 需上传的总文件大小
    private long total;

    public PolyvUploadInfo(){}

    public PolyvUploadInfo(String title, String desc, long filesize, String filepath) {
        // cataid默认为0
        this(title, desc, filesize, filepath, "0");
    }

    public PolyvUploadInfo(String title, String desc, long filesize, String filepath, String cataid) {
        this.title = title;
        this.desc = desc;
        this.filesize = filesize;
        this.filepath = filepath;
        this.cataid = cataid;
    }

    public String getCataid() {
        return cataid;
    }

    public void setCataid(String cataid) {
        this.cataid = cataid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public long getFilesize() {
        return filesize;
    }

    public void setFilesize(long filesize) {
        this.filesize = filesize;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
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
        return "PolyvUploadInfo{" +
                "title='" + title + '\'' +
                ", filepath='" + filepath + '\'' +
                ", filesize=" + filesize +
                ", desc='" + desc + '\'' +
                ", cataid='" + cataid + '\'' +
                ", percent=" + percent +
                ", total=" + total +
                '}';
    }
}
