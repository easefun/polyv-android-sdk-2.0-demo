package com.easefun.polyvsdk.bean;

import androidx.annotation.Keep;

/**
 * date: 2019/8/9 0009
 *
 * @author hwj
 * description 描述一下方法的作用
 */
@Keep
public class PolyvAddDanmakuResult {

    /**
     * code : 200
     * status : success
     * message : success
     * data : {"id":444963,"vid":"c538856dde2600e0096215c16592d4d3_c","userid":"c538856dde","msg":"我在上班路上小心点","time":"00:00:11","fontsize":"18","fontmode":"roll","fontcolor":"0xFFFFFF","timestamp":1565343964000,"sessionid":null,"param2":null,"msgtype":"speak","user":"","origin":"vod"}
     */

    private int code;
    private String status;
    private String message;
    private DataBean data;

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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 444963
         * vid : c538856dde2600e0096215c16592d4d3_c
         * userid : c538856dde
         * msg : 我在上班路上小心点
         * time : 00:00:11
         * fontsize : 18
         * fontmode : roll
         * fontcolor : 0xFFFFFF
         * timestamp : 1565343964000
         * sessionid : null
         * param2 : null
         * msgtype : speak
         * user :
         * origin : vod
         */

        private int id;
        private String vid;
        private String userid;
        private String msg;
        private String time;
        private String fontsize;
        private String fontmode;
        private String fontcolor;
        private long timestamp;
        private Object sessionid;
        private Object param2;
        private String msgtype;
        private String user;
        private String origin;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getVid() {
            return vid;
        }

        public void setVid(String vid) {
            this.vid = vid;
        }

        public String getUserid() {
            return userid;
        }

        public void setUserid(String userid) {
            this.userid = userid;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getFontsize() {
            return fontsize;
        }

        public void setFontsize(String fontsize) {
            this.fontsize = fontsize;
        }

        public String getFontmode() {
            return fontmode;
        }

        public void setFontmode(String fontmode) {
            this.fontmode = fontmode;
        }

        public String getFontcolor() {
            return fontcolor;
        }

        public void setFontcolor(String fontcolor) {
            this.fontcolor = fontcolor;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public Object getSessionid() {
            return sessionid;
        }

        public void setSessionid(Object sessionid) {
            this.sessionid = sessionid;
        }

        public Object getParam2() {
            return param2;
        }

        public void setParam2(Object param2) {
            this.param2 = param2;
        }

        public String getMsgtype() {
            return msgtype;
        }

        public void setMsgtype(String msgtype) {
            this.msgtype = msgtype;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getOrigin() {
            return origin;
        }

        public void setOrigin(String origin) {
            this.origin = origin;
        }
    }
}
