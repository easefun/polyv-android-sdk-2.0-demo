package com.easefun.polyvsdk.util;

import androidx.annotation.NonNull;

import com.easefun.polyvsdk.PolyvDownloader;
import com.easefun.polyvsdk.PolyvDownloaderErrorReason;
import com.easefun.polyvsdk.video.PolyvPlayErrorReason;

/**
 * 错误类型转成错误信息工具类
 * @author Lion 2017-8-3
 */
public class PolyvErrorMessageUtils {
    /**
     * 获取下载错误信息
     * @param type 下载错误类型
     * @return 错误信息字符串
     */
    @NonNull
    public static String getDownloaderErrorMessage(PolyvDownloaderErrorReason.ErrorType type) {
        return getDownloaderErrorMessage(type, PolyvDownloader.FILE_VIDEO);
    }

    /**
     * 获取下载错误信息
     *
     * @param type 下载错误类型
     * @return 错误信息字符串
     */
    @NonNull
    public static String getDownloaderErrorMessage(PolyvDownloaderErrorReason.ErrorType type, int fileType) {
        String tipsTypeMsg = PolyvDownloader.FILE_VIDEO == fileType ? "视频" : "音频";
        switch (type) {
            case VID_IS_NULL:
                return tipsTypeMsg + "id不正确，请设置正确的视频id进行下载";

            case NOT_PERMISSION:
                return "非法下载，请向管理员反馈";

            case RUNTIME_EXCEPTION:
                return "当前" + tipsTypeMsg + "无法下载，请向管理员反馈";

            case VIDEO_STATUS_ERROR:
                return tipsTypeMsg + "状态异常，无法下载";

            case M3U8_NOT_DATA:
                return tipsTypeMsg + "数据加载失败，请重新下载";

            case QUESTION_NOT_DATA:
                return tipsTypeMsg + "问答数据加载失败，请重新下载";

            case MULTIMEDIA_LIST_EMPTY:
                return "当前" + tipsTypeMsg + "无法下载，请向管理员反馈";

            case CAN_NOT_MKDIR:
                return "当前" + tipsTypeMsg + "无法下载，请重启手机再次下载或者向管理员反馈";

            case DOWNLOAD_TS_ERROR:
                return tipsTypeMsg + "文件下载失败，请重新下载";

            case MULTIMEDIA_EMPTY:
                return "当前" + tipsTypeMsg + "无法下载，请向管理员反馈";

            case NOT_CREATE_DIR:
                return "当前" + tipsTypeMsg + "无法下载，请重启手机再次下载或者向管理员反馈";

            case VIDEO_LOAD_FAILURE:
                return "当前" + tipsTypeMsg + "无法下载，请向管理员反馈";

            case VIDEO_NULL:
                return tipsTypeMsg + "信息加载失败，请重新下载";

            case DIR_SPACE_LACK:
                return "检测到移动设备存储空间不足，请清除存储空间再重新下载";

            case DOWNLOAD_DIR_IS_NUll:
                return "检测到存储目录未设置，请先设置存储目录再重新下载";

            case HLS_15X_URL_ERROR:
                return "当前" + tipsTypeMsg + "无法下载，请向管理员反馈";

            case HLS_SPEED_TYPE_IS_NULL:
                return tipsTypeMsg + "速度类型错误，请设置了速度类型后重新下载";

            case HLS_15X_ERROR:
                return "当前" + tipsTypeMsg + "无法下载，请向管理员反馈";

            case GET_VIDEO_INFO_ERROR:
                return tipsTypeMsg + "信息加载失败，请重新下载";

            case WRITE_EXTERNAL_STORAGE_DENIED:
                return "检测到拒绝写入存储设备，请先为应用程序分配权限，再重新下载";

            case VID_ERROR:
                return tipsTypeMsg + "id不正确，请设置正确的" + tipsTypeMsg + "id进行下载";

            case EXTRA_DIR_IS_NUll:
                return "检测到资源目录未设置，请先设置存储目录再重新下载";

            case NOT_CREATE_EXTRA_DIR:
                return "当前视频无法下载，请重启手机再次下载或者向管理员反馈";

            case CREATE_NOMEDIA_ERROR:
                return "当前" + tipsTypeMsg + "无法下载，请重启手机再次下载或者向管理员反馈";

            case URL_IS_EMPTY:
                return "当前" + tipsTypeMsg + "无法下载，请向管理员反馈";

            case CREATE_M3U8_FILE_ERROR:
                return "当前" + tipsTypeMsg + "无法下载，请重启手机再次下载或者向管理员反馈";

            case WRITE_M3U8_FILE_ERROR:
                return "当前" + tipsTypeMsg + "无法下载，请重启手机再次下载或者向管理员反馈";

            case CREATE_VIDEO_JSON_ERROR:
                return "当前" + tipsTypeMsg + "无法下载，请重启手机再次下载或者向管理员反馈";

            case WRITE_VIDEO_JSON_ERROR:
                return "当前视频无法下载，请重启手机再次下载或者向管理员反馈";

            case QUESTION_FORMAT_JSON_ERROR:
                return "当前" + tipsTypeMsg + "无法下载，请重新下载或者切换网络重新下载或者向管理员反馈";

            case DELETE_ZIP_FILE_ERROR:
                return "当前" + tipsTypeMsg + "无法下载，请重启手机再次下载或者向管理员反馈";

            case CREATE_VIDEO_TMP_FILE_ERROR:
                return "当前" + tipsTypeMsg + "无法下载，请重启手机再次下载或者向管理员反馈";

            case DOWNLOAD_VIDEO_FILE_LENGTH_ERROR:
                return "当前" + tipsTypeMsg + "无法下载，请重新下载或者切换网络重新下载或者向管理员反馈";

            case VIDEO_HTTP_CODE_ERROR:
                return "当前" + tipsTypeMsg + "无法下载，请重新下载或者切换网络重新下载或者向管理员反馈";

            case VIDEO_DOWNLOAD_ERROR:
                return "当前" + tipsTypeMsg + "无法下载，请删除后重新下载或者切换网络重新下载或者向管理员反馈";

            case VIDEO_RENAME_ERROR:
                return "当前" + tipsTypeMsg + "无法下载，请重启手机再次下载或者向管理员反馈";

            case UNZIP_FILE_ERROR:
                return "当前" + tipsTypeMsg + "解压失败，请尝试删除视频再次下载或者切换网络再次下载或者向管理员反馈";

            case CREATE_ZIP_TMP_FILE_ERROR:
                return "当前" + tipsTypeMsg + "无法下载，请重启手机再次下载或者向管理员反馈";

            case CREATE_UNZIP_DIR_ERROR:
                return "当前" + tipsTypeMsg + "无法下载，请重启手机再次下载或者向管理员反馈";

            case DOWNLOAD_ZIP_FILE_LENGTH_ERROR:
                return "当前" + tipsTypeMsg + "无法下载，请重新下载或者切换网络重新下载或者向管理员反馈";

            case ZIP_HTTP_CODE_ERROR:
                return "当前" + tipsTypeMsg + "无法下载，请重新下载或者切换网络重新下载或者向管理员反馈";

            case ZIP_DOWNLOAD_ERROR:
                return "当前" + tipsTypeMsg + "下载出错，请删除后重新下载或者切换网络重新下载或者向管理员反馈";

            case ZIP_RENAME_ERROR:
                return "当前" + tipsTypeMsg + "无法下载，请重启手机再次下载或者向管理员反馈";

            case NETWORK_DENIED:
                return "无法连接网络，请连接网络后下载";

            case MEDIA_UNKNOWN:
                return "SD卡不可用，请重启手机或者更换SD卡或者向管理员反馈";

            case MEDIA_REMOVED:
                return "SD卡被移除，请重启手机或者向管理员反馈";

            case MEDIA_UNMOUNTED:
                return "SD卡存在但未安装，请重新安装SD卡或者向管理员反馈";

            case MEDIA_CHECKING:
                return "SD卡正在进行磁盘检查中，请稍后下载或者重启手机或者向管理员反馈";

            case MEDIA_NOFS:
                return "SD卡空白或正在使用不受支持的文件系统，请重启手机或者更换SD卡或者向管理员反馈";

            case MEDIA_MOUNTED_READ_ONLY:
                return "SD卡不能写入，请重启手机或者向管理员反馈";

            case MEDIA_SHARED:
                return "SD卡不可用，请重启手机或者更换SD卡或者向管理员反馈";

            case MEDIA_BAD_REMOVAL:
                return "SD卡不可用，请重启手机或者更换SD卡或者向管理员反馈";

            case MEDIA_UNMOUNTABLE:
                return "SD卡不可用，请重启手机或者更换SD卡或者向管理员反馈";

            case MEDIA_EJECTING:
                return "SD卡不可用，请重启手机或者更换SD卡或者向管理员反馈";

            case VIDEO_BITRATE_NOT_EXIST:
                return "当前" + tipsTypeMsg + "下载出错，请尝试切换码率进行下载或者向管理员反馈";

            case AUDIO_NOT_EXIST:
                return tipsTypeMsg + "文件不存在，无法下载，请尝试切换网络重新下载或者向管理员反馈";

            case VIDEO_JSON_CLIENT_ERROR:
                return "视频加载失败，请检查网络设置";

            case VIDEO_JSON_SERVER_ERROR:
                return "视频加载失败，请联系管理员";

            default:
                return "当前" + tipsTypeMsg + "无法下载，请向管理员反馈";
        }
    }

    /**
     * 获取播放错误信息
     * @param playErrorReason 播放错误类型
     * @return 错误信息字符串
     */
    public static String getPlayErrorMessage(@PolyvPlayErrorReason.PlayErrorReason int playErrorReason) {
        switch (playErrorReason) {
            case PolyvPlayErrorReason.VIDEO_ERROR:
                return "视频加载失败，请检查网络设置";

            case PolyvPlayErrorReason.NETWORK_DENIED:
                return "无法连接网络，请连接网络后播放";

            case PolyvPlayErrorReason.OUT_FLOW:
                return "流量超标，请向管理员反馈";

            case PolyvPlayErrorReason.TIMEOUT_FLOW:
                return "账号过期，请向管理员反馈";

            case PolyvPlayErrorReason.LOCAL_VIDEO_ERROR:
                return "本地视频文件损坏，请重新下载";

            case PolyvPlayErrorReason.START_ERROR:
                return "播放异常，请重新播放";

            case PolyvPlayErrorReason.NOT_PERMISSION:
                return "非法播放，请向管理员反馈";

            case PolyvPlayErrorReason.USER_TOKEN_ERROR:
                return "请先设置播放凭证，再进行播放";

            case PolyvPlayErrorReason.VIDEO_STATUS_ERROR:
                return "视频状态异常，无法播放，请向管理员反馈";

            case PolyvPlayErrorReason.VID_ERROR:
                return "视频id不正确，请设置正确的视频id进行播放";

            case PolyvPlayErrorReason.BITRATE_ERROR:
                return "清晰度不正确，请设置正确的清晰度进行播放";

            case PolyvPlayErrorReason.VIDEO_NULL:
                return "视频信息加载失败，请尝试切换网络重新播放";

            case PolyvPlayErrorReason.MP4_LINK_NUM_ERROR:
                return "当前视频无法播放，请向管理员反馈";

            case PolyvPlayErrorReason.M3U8_LINK_NUM_ERROR:
                return "当前视频无法播放，请向管理员反馈";

            case PolyvPlayErrorReason.HLS_SPEED_TYPE_NULL:
                return "播放速度不正确，请设置正确的播放速度进行播放";

            case PolyvPlayErrorReason.NOT_LOCAL_VIDEO:
                return "找不到缓存的视频文件，请连网后重新下载";

            case PolyvPlayErrorReason.HLS_15X_INDEX_EMPTY:
                return "当前视频无法播放，请向管理员反馈";

            case PolyvPlayErrorReason.HLS_15X_ERROR:
                return "当前视频无法播放，请向管理员反馈";

            case PolyvPlayErrorReason.HLS_15X_URL_ERROR:
                return "当前视频无法播放，请向管理员反馈";

            case PolyvPlayErrorReason.M3U8_15X_LINK_NUM_ERROR:
                return "当前视频无法播放，请向管理员反馈";

            case PolyvPlayErrorReason.CHANGE_EQUAL_HLS_SPEED:
                return "切换播放速度相同，请选择其它播放速度";

            case PolyvPlayErrorReason.CAN_NOT_CHANGE_BITRATE:
                return "未开始播放视频不能切换清晰度，请先播放视频";

            case PolyvPlayErrorReason.CAN_NOT_CHANGE_HLS_SPEED:
                return "未开始播放视频不能切换播放速度，请先播放视频";

            case PolyvPlayErrorReason.QUESTION_ERROR:
                return "视频问答数据加载失败，请重新播放或者切换网络重新播放";

            case PolyvPlayErrorReason.CHANGE_BITRATE_NOT_EXIST:
                return "视频没有这个清晰度，请切换其它清晰度";

            case PolyvPlayErrorReason.HLS_URL_ERROR:
                return "当前视频无法播放，请向管理员反馈";

            case PolyvPlayErrorReason.LOADING_VIDEO_ERROR:
                return "当前视频无法播放，请向管理员反馈";

            case PolyvPlayErrorReason.HLS2_URL_ERROR:
                return "当前视频无法播放，请向管理员反馈";

            case PolyvPlayErrorReason.TOKEN_NULL:
                return "播放授权获取失败，请重新播放或者切换网络重新播放或者向管理员反馈";

            case PolyvPlayErrorReason.WRITE_EXTERNAL_STORAGE_DENIED:
                return "检测到拒绝读取存储设备，请先为应用程序分配权限，再重新播放";

            case PolyvPlayErrorReason.SOURCE_URL_EMPTY:
                return "当前视频无法播放，请向管理员反馈";

            case PolyvPlayErrorReason.AUDIO_URL_EMPTY:
                return "当前音频无法播放，请向管理员反馈";

            case PolyvPlayErrorReason.NOT_LOCAL_AUDIO:
                return "找不到缓存的音频文件，请连网后重新下载";

            case PolyvPlayErrorReason.CAN_NOT_CHANGE_AUDIO:
                return "未开始播放不能切换到音频，请先开始播放";

            case PolyvPlayErrorReason.CAN_NOT_CHANGE_VIDEO:
                return "未开始播放不能切换到视频，请先开始播放";

            case PolyvPlayErrorReason.LOCAL_AUDIO_ERROR:
                return "本地音频文件损坏，请重新下载";

            case PolyvPlayErrorReason.LOAD_TIMEOUT:
                return "加载超时，请重新播放";

            case PolyvPlayErrorReason.CAN_NOT_CHANGE_ROUTE:
                return "当前无法切换播放器的线路，请重新进入播放器";

            case PolyvPlayErrorReason.TOKEN_CLIENT_ERROR:
                return "视频加载失败，请检查网络设置";

            case PolyvPlayErrorReason.TOKEN_SERVER_ERROR:
                return "视频加载失败，请联系管理员";

            case PolyvPlayErrorReason.VIDEO_JSON_CLIENT_ERROR:
                return "视频加载失败，请检查网络设置";

            case PolyvPlayErrorReason.VIDEO_JSON_SERVER_ERROR:
                return "视频加载失败，请联系管理员";

            case PolyvPlayErrorReason.QUESTION_CLIENT_ERROR:
                return "视频加载失败，请检查网络设置";

            case PolyvPlayErrorReason.QUESTION_SERVER_ERROR:
                return "视频加载失败，请联系管理员";
            case PolyvPlayErrorReason.HLS_PRIVATE_VERSION_ERROR:
                return "播放器不支持播放该视频，请升级播放器版本";
            case PolyvPlayErrorReason.HLS_KEY_VERSION_ERROR:
                return "请升级播放器版本";
            default:
                return "当前视频无法播放，请向管理员反馈";
        }
    }
}
