package com.easefun.polyvsdk.server.vo;

import java.util.ArrayList;

/**
 * 批量获取视频播放次数结果，接口文档
 * http://dev.polyv.net/2017/videoproduct/v-api/v-api-vmanage/v-api-vmanage-info/getplaytimes/
 * @author Lionel 2018-11-12
 */
public class PolyvPlayTimesResult {
    /** success */
    public static final int OK = 200;

    private int code;
    private String status;
    private String message;
    private ArrayList<Data> data;

    public static int getOK() {
        return OK;
    }

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

    public ArrayList<Data> getData() {
        return data;
    }

    public void setData(ArrayList<Data> data) {
        this.data = data;
    }

    public class Data {
        private String vid;
        private int times;

        public String getVid() {
            return vid;
        }

        public void setVid(String vid) {
            this.vid = vid;
        }

        public int getTimes() {
            return times;
        }

        public void setTimes(int times) {
            this.times = times;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "vid='" + vid + '\'' +
                    ", times=" + times +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "PolyvPlayTimesResult{" +
                "code=" + code +
                ", status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
