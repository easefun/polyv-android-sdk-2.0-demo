package com.easefun.polyvsdk.server;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.easefun.polyvsdk.server.request.PolyvGetByUploader;
import com.easefun.polyvsdk.server.request.PolyvPlayTimes;
import com.easefun.polyvsdk.server.vo.PolyvGetByUploaderResult;
import com.easefun.polyvsdk.server.vo.PolyvPlayTimesResult;

import java.util.ArrayList;

/**
 * 点播业务接口，本类主要是演示POLYV服务业务接口的使用，开发者需要根据自己的业务逻辑调整代码。
 * @author Lionel 2018-11-7
 */
public class PolyvVodBusinessServer {
    private static final String TAG = "PolyvVodBusinessServer";

    private static final String USER_ID = "";
    private static final String SECRET_KEY = "";

    /**
     * 获取某分类下某子账号的视频列表，接口文档
     * http://dev.polyv.net/2018/videoproduct/v-api/v-api-vmanage/v-api-vmanage-list/get-by-uploader/
     * @param page 页码
     * @return 失败返回null，成功返回结果对象
     */
    @Nullable
    public static PolyvGetByUploaderResult getByUploader(int page) {
        ArrayList<String> exceptionList = new ArrayList<>();
        PolyvGetByUploaderResult result = null;
        //3次重试，容错网络抖动
        for (int i = 0, length = 3 ; i < length ; i++) {
            result = PolyvGetByUploader.requestGetByUploader(USER_ID, SECRET_KEY, page, PolyvGetByUploader.ORDER_TYPE_PTIME_ASC, false, exceptionList);
            if (result != null) {
                break;
            }
        }

        if (result == null) {
            for (String error : exceptionList) {
                Log.e(TAG, error);
            }
        }

        return result;
    }

    /**
     * 批量获取视频播放次数，接口文档
     * http://dev.polyv.net/2017/videoproduct/v-api/v-api-vmanage/v-api-vmanage-info/getplaytimes/
     * @param vids 视频ID列表
     * @return 失败返回null，成功返回结果对象
     */
    @Nullable
    public static PolyvPlayTimesResult playTimes(@NonNull ArrayList<String> vids) {
        if (vids.isEmpty()) {
            return null;
        }

        ArrayList<String> exceptionList = new ArrayList<>();
        PolyvPlayTimesResult result = null;
        //3次重试，容错网络抖动
        for (int i = 0, length = 3 ; i < length ; i++) {
            result = PolyvPlayTimes.requestPlayTimes(USER_ID, SECRET_KEY, vids, exceptionList);
            if (result != null) {
                break;
            }
        }

        if (result == null) {
            for (String error : exceptionList) {
                Log.e(TAG, error);
            }
        }

        return result;
    }
}
