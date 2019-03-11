package com.easefun.polyvsdk.server.request;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.easefun.polyvsdk.PolyvSDKUtil;
import com.easefun.polyvsdk.server.vo.PolyvPlayTimesResult;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

/**
 * 批量获取视频播放次数，接口文档
 * http://dev.polyv.net/2017/videoproduct/v-api/v-api-vmanage/v-api-vmanage-info/getplaytimes/
 * @author Lionel 2018-11-12
 */
public class PolyvPlayTimes {
    private static final String TAG = "PolyvPlayTimes";

    /**
     * 批量获取视频播放次数，详细请看
     * http://dev.polyv.net/2017/videoproduct/v-api/v-api-vmanage/v-api-vmanage-info/getplaytimes/
     */
    private static final String PLAY_TIMES_URL = "http://api.polyv.net/v2/data/%s/play-times";

    public static PolyvPlayTimesResult requestPlayTimes(@NonNull String userId, @NonNull String secretKey, @NonNull ArrayList<String> vids, @NonNull ArrayList<String> exceptionList) {
        String vidsStr = TextUtils.join(",", vids);
        long ptime = System.currentTimeMillis();
        String signParam = "ptime=" + ptime + "&" +
                "realTime=" + "1" + "&" +
                "vids=" + vidsStr +
                secretKey;

        String sign = PolyvSDKUtil.sha1(signParam).toUpperCase();
        String url = String.format(Locale.getDefault(), PLAY_TIMES_URL, userId) + "?" +
                "&ptime=" + ptime +
                "&realTime=" + "1" +
                "&sign=" + sign +
                "&vids=" + vidsStr;

        String body = PolyvSDKUtil.getUrl2String(url, 6000, 6000, exceptionList);
        if (TextUtils.isEmpty(body)) {
            return null;
        }

        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(body);
        } catch (JSONException e) {
            exceptionList.add(PolyvSDKUtil.getExceptionFullMessage(e, -1));
            return null;
        }

        int code = jsonObject.optInt("code", 0);
        //返回码看接口文档中的响应说明
        if (code == PolyvPlayTimesResult.OK) {
            Gson gson = new Gson();
            return gson.fromJson(body, PolyvPlayTimesResult.class);
        }

        Log.e(TAG, body);
        exceptionList.add(body);
        return null;
    }
}
