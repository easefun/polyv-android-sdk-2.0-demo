package com.easefun.polyvsdk.server.vo;


import java.util.ArrayList;

/**
 * 获取某分类下某子账号的视频列表结果，接口文档
 * http://dev.polyv.net/2018/videoproduct/v-api/v-api-vmanage/v-api-vmanage-list/get-by-uploader/
 * @author Lionel 2018-11-7
 */
public class PolyvGetByUploaderResult {
    /** success */
    public static final int OK = 200;

    private int code;
    private String status;
    private String message;
    private Data data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "PolyvGetByUploaderResult{" +
                "code=" + code +
                ", status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }

    public class Data {
        private int pageSize;
        private int pageNumber;
        private int totalItems;
        private ArrayList<Content> contents;

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public int getPageNumber() {
            return pageNumber;
        }

        public void setPageNumber(int pageNumber) {
            this.pageNumber = pageNumber;
        }

        public int getTotalItems() {
            return totalItems;
        }

        public void setTotalItems(int totalItems) {
            this.totalItems = totalItems;
        }

        public ArrayList<Content> getContents() {
            return contents;
        }

        public void setContents(ArrayList<Content> contents) {
            this.contents = contents;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "pageSize=" + pageSize +
                    ", pageNumber=" + pageNumber +
                    ", totalItems=" + totalItems +
                    ", contents=" + contents +
                    '}';
        }

        public class Content {
            private String vid;
            private long cataid;
            private String title;
            private String context;
            private int times;
            private String firstImage;
            private String tag;
            private String aacLink;
            private String status;
            private String uploaderEmail;
            private long ptime;

            public String getVid() {
                return vid;
            }

            public void setVid(String vid) {
                this.vid = vid;
            }

            public long getCataid() {
                return cataid;
            }

            public void setCataid(long cataid) {
                this.cataid = cataid;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getContext() {
                return context;
            }

            public void setContext(String context) {
                this.context = context;
            }

            public int getTimes() {
                return times;
            }

            public void setTimes(int times) {
                this.times = times;
            }

            public String getFirstImage() {
                return firstImage;
            }

            public void setFirstImage(String firstImage) {
                this.firstImage = firstImage;
            }

            public String getTag() {
                return tag;
            }

            public void setTag(String tag) {
                this.tag = tag;
            }

            public String getAacLink() {
                return aacLink;
            }

            public void setAacLink(String aacLink) {
                this.aacLink = aacLink;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getUploaderEmail() {
                return uploaderEmail;
            }

            public void setUploaderEmail(String uploaderEmail) {
                this.uploaderEmail = uploaderEmail;
            }

            public long getPtime() {
                return ptime;
            }

            public void setPtime(long ptime) {
                this.ptime = ptime;
            }

            @Override
            public String toString() {
                return "Content{" +
                        "vid='" + vid + '\'' +
                        ", cataid=" + cataid +
                        ", title='" + title + '\'' +
                        ", context='" + context + '\'' +
                        ", times=" + times +
                        ", firstImage='" + firstImage + '\'' +
                        ", tag='" + tag + '\'' +
                        ", aacLink='" + aacLink + '\'' +
                        ", status='" + status + '\'' +
                        ", uploaderEmail='" + uploaderEmail + '\'' +
                        ", ptime=" + ptime +
                        '}';
            }
        }
    }
}
