package com.easefun.polyvsdk.server.request;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.easefun.polyvsdk.PolyvSDKUtil;
import com.easefun.polyvsdk.server.vo.PolyvGetByUploaderResult;
import com.google.gson.Gson;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Locale;

/**
 * 获取某分类下某子账号的视频列表，接口文档
 * http://dev.polyv.net/2018/videoproduct/v-api/v-api-vmanage/v-api-vmanage-list/get-by-uploader/
 * @author Lionel 2018-11-8
 */
public class PolyvGetByUploader {
    private static final String TAG = "PolyvGetByUploader";

    /** ptime升序 */
    public static final int ORDER_TYPE_PTIME_ASC = 1;
    /** ptime降序 */
    public static final int ORDER_TYPE_PTIME_DESC = 2;
    /** times升序 */
    public static final int ORDER_TYPE_TIMES_ASC = 3;
    /** times降序 */
    public static final int ORDER_TYPE_TIMES_DESC = 4;

    @IntDef({
            ORDER_TYPE_PTIME_ASC,
            ORDER_TYPE_PTIME_DESC,
            ORDER_TYPE_TIMES_ASC,
            ORDER_TYPE_TIMES_DESC
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface OrderType {}

    /**
     * 获取某分类下某子账号的视频列表，详细请看
     * http://dev.polyv.net/2018/videoproduct/v-api/v-api-vmanage/v-api-vmanage-list/get-by-uploader/
     */
    private static final String GET_BY_UPLOADER_URL = "http://api.polyv.net/v2/video/%s/get-by-uploader";

    /**
     * 获取某分类下某子账号的视频列表，接口文档
     * http://dev.polyv.net/2018/videoproduct/v-api/v-api-vmanage/v-api-vmanage-list/get-by-uploader/
     * @param userId 用户ID
     * @param secretKey key
     * @param page 页码
     * @param orderType 排序类型
     * @param containSubCata 是否包含子分类
     * @return 失败返回null，成功返回结果对象
     */
    @Nullable
    public static PolyvGetByUploaderResult requestGetByUploader(@NonNull String userId, @NonNull String secretKey, int page, @OrderType int orderType, boolean containSubCata, @NonNull ArrayList<String> exceptionList) {
        int containSubCataType = containSubCata ? 1 : 0;
        long ptime = System.currentTimeMillis();
        String signParam = "containSubCata=" + containSubCataType + "&" +
                "orderType=" + orderType + "&" +
                "page=" + page + "&" +
                "ptime=" + ptime +
                secretKey;

        String sign = PolyvSDKUtil.sha1(signParam).toUpperCase();
        String url = String.format(Locale.getDefault(), GET_BY_UPLOADER_URL, userId) + "?" +
                "&containSubCata=" + containSubCataType +
                "&orderType=" + orderType +
                "&page=" + page +
                "&ptime=" + ptime +
                "&sign=" + sign;

        String body = PolyvSDKUtil.getUrl2String(url, 6000, 6000, exceptionList);
        if (TextUtils.isEmpty(body)) {
            return null;
        }

        Gson gson = new Gson();
        PolyvGetByUploaderResult result = gson.fromJson(body, PolyvGetByUploaderResult.class);
        if (result == null) {
            return null;
        }

        //返回码看接口文档中的响应说明
        if (result.getCode() != PolyvGetByUploaderResult.OK) {
            Log.e(TAG, result.toString());
            exceptionList.add(result.toString());
            return null;
        }

        return result;
    }
}
