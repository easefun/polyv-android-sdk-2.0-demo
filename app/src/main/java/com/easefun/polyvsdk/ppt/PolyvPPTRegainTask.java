package com.easefun.polyvsdk.ppt;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.easefun.polyvsdk.PolyvDownloader;
import com.easefun.polyvsdk.PolyvDownloaderManager;
import com.easefun.polyvsdk.download.ppt.IPolyvDownloaderPptListener;
import com.easefun.polyvsdk.po.ppt.PolyvPptInfo;
import com.easefun.polyvsdk.util.PolyvAPIHelper;
import com.easefun.polyvsdk.util.PolyvNetworkUtils;
import com.easefun.polyvsdk.util.PolyvReqUtils;
import com.easefun.polyvsdk.video.PolyvVideoUtil;
import com.easefun.polyvsdk.vo.PolyvValidatePptVO;

import java.util.concurrent.Future;

public class PolyvPPTRegainTask {
    private Future pptJsonFuture;
    private PolyvDownloader downloader;
    private String downloadVid;

    private Handler handler = new Handler(Looper.getMainLooper());

    private PolyvPPTRegainTask() {
    }

    private static class Singleton {
        private static final PolyvPPTRegainTask PPTREGAINTASK = new PolyvPPTRegainTask();
    }

    public static PolyvPPTRegainTask getInstance() {
        return Singleton.PPTREGAINTASK;
    }

    public void regainPPT(String vid, Context context, OnPPTRegainLisnter lisnter, boolean isOnline) {
        if (isOnline) {
            getOnlinePPT(vid, context, lisnter);
        } else {
            downloadPPT(vid, context, lisnter);
        }
    }

    public void getOnlinePPT(final String vid, final Context context, final OnPPTRegainLisnter listener) {
        shutdown();
        if (listener != null) {
            listener.onProgress(0);
        }
        if (!PolyvNetworkUtils.isConnected(context)) {
            if (listener != null) {
                listener.onFail(vid, "网络异常", 199);
            }
            return;
        }
        pptJsonFuture = PolyvAPIHelper.submitTask(new Runnable() {
            @Override
            public void run() {
                final PolyvReqUtils.Data data = PolyvAPIHelper.getPPTJson(vid);
                if (data.isSuccess()) {
                    final PolyvPptInfo pptInfo = PolyvPptInfo.format2PptInfo(vid, data.data);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (listener != null) {
                                if (pptInfo.getPages().size() > 0) {
                                    listener.onProgress(100);
                                    listener.onSuccess(vid, pptInfo);
                                } else {
                                    listener.onFail(vid, "空数据", 139);
                                }
                            }
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (listener != null) {
                                listener.onFail(vid, data.data, data.responseCode);
                            }
                        }
                    });
                }
            }
        });
    }

    public void downloadPPT(final String vid, Context context, final OnPPTRegainLisnter listener) {
        shutdown();
        if (listener != null) {
            listener.onProgress(0);
        }
        if (!PolyvNetworkUtils.isConnected(context)) {
            if (listener != null) {
                listener.onFail(vid, "网络异常", 199);
            }
            return;
        }
        downloadVid = vid;
        downloader = PolyvDownloaderManager.getPolyvPptDownloader(vid);
        downloader.setPolyvDownloadPptListener(new IPolyvDownloaderPptListener() {
            @Override
            public void onProgress(int current, int total) {
                if (total == 0)
                    return;
                if (listener != null) {
                    listener.onProgress((int) ((current * 1f / total) * 100));
                }
            }

            @Override
            public void onSuccess() {
                PolyvValidatePptVO validatePptVO = PolyvVideoUtil.validatePpt(vid);
                if (validatePptVO != null && validatePptVO.getResult() == PolyvValidatePptVO.RESULT_HAVE_LOCAL_PPT
                        && validatePptVO.getPptInfo() != null && validatePptVO.getPptInfo().getPages().size() > 0) {
                    if (listener != null) {
                        listener.onProgress(100);
                        listener.onSuccess(vid, validatePptVO.getPptInfo());
                    }
                } else {
                    if (listener != null) {
                        listener.onFail(vid, "下载失败", 129);
                    }
                }
            }

            @Override
            public void onFailure(int errorReason) {
                if (listener != null) {
                    listener.onFail(vid, "下载失败", errorReason);
                }
            }
        });
        downloader.start(context);
    }

    public interface OnPPTRegainLisnter {
        void onProgress(int progress);

        void onSuccess(String vid, PolyvPptInfo pptvo);

        void onFail(String vid, String tips, int errorReason);
    }

    public void shutdown() {
        handler.removeCallbacksAndMessages(null);
        if (pptJsonFuture != null && !pptJsonFuture.isCancelled() && !pptJsonFuture.isDone()) {
            pptJsonFuture.cancel(true);
            pptJsonFuture = null;
        }
        if (downloader != null) {
            PolyvDownloaderManager.clearPolyvPptDownload(downloadVid);
            downloader = null;
        }
    }
}
