package com.easefun.polyvsdk;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.chinanetcenter.wcs.android.utils.FileUtil;
import com.easefun.polyvsdk.bean.PolyvDownloadInfo;
import com.easefun.polyvsdk.database.PolyvDownloadSQLiteHelper;
import com.easefun.polyvsdk.log.PolyvCommonLog;
import com.easefun.polyvsdk.util.PolyvStorageUtils;
import com.easefun.polyvsdk.util.PolyvTaskExecutorUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author df
 * @create 2019/4/4
 * @Describe 用户管理类
 */
public class PolyvUserClient {
    private static final PolyvUserClient ourInstance = new PolyvUserClient();
    private static final String TAG = "PolyvUserClient";
    private static final String OLD_DOWNLOAD_DIR = "polyvdownload";
    private static final String DEFAULT_DOWNLOAD_DIR = "easefundownload/defalut";
    private static final String MUITL_DOWNLOAD_DIR = "easefundownload";
    private ArrayList<File> externalFilesDirs = new ArrayList<>();
    private boolean isMovingFile;//是否正在移动文件

    public static PolyvUserClient getInstance() {
        return ourInstance;
    }

    private PolyvUserClient() {
    }

    //登录接口 调用登录前 先调用logout 登出
    public void login(@NonNull String accountId, Context context) {
        if (TextUtils.isEmpty(accountId)) {
            PolyvCommonLog.e(TAG, "viewerid is null");
            return;
        }
        PolyvSDKClient.getInstance().setViewerId(accountId);
        PolyvSDKClient.getInstance().setDownloadDir(null);

        /**
         *  如果单账户体系切换到多账户 体系 并且需要保存以前的数据到新的账户下
         *   迁移完数据后 再初始化下载目录
         */
        if(PolyvSDKClient.getInstance().isMultiDownloadAccount() && PolyvSDKClient.getInstance().isSaveOldData()){
            processOldDownloadData(context);
        }else{//初始化下载目录
            initDownloadDir(context);
        }

    }

    //登出
    public void logout(Context context) {
        if(!PolyvSDKClient.getInstance().isMultiDownloadAccount()){
            return;
        }
        PolyvDownloaderManager.releaseDownload();
        PolyvDownloadSQLiteHelper.getInstance(context).release();
        PolyvSDKClient.getInstance().setViewerId("");
        setDefaultDownloadPath(context);
    }

    private void setDefaultDownloadPath(Context context) {
        String rootDownloadDirName = DEFAULT_DOWNLOAD_DIR;//以前的目录 用户根据自己的目录结构来进行修改

        if (externalFilesDirs.isEmpty()) {
            externalFilesDirs = PolyvStorageUtils.getExternalFilesDirs(context);
        }
        if (externalFilesDirs.size() == 0) {
            // TODO 没有可用的存储设备,后续不能使用视频缓存功能
            Log.e(TAG, "没有可用的存储设备,后续不能使用视频缓存功能");
            return;
        }

        //SD卡会有SD卡接触不良，SD卡坏了，SD卡的状态错误的问题。
        //我们在开发中也遇到了SD卡没有权限写入的问题，但是我们确定APP是有赋予android.permission.WRITE_EXTERNAL_STORAGE权限的。
        //有些是系统问题，有些是SD卡本身的问题，这些问题需要通过重新拔插SD卡或者更新SD卡来解决。所以如果想要保存下载视频至SD卡请了解这些情况。
        final File downloadDir = new File(externalFilesDirs.get(0), rootDownloadDirName);
        PolyvSDKClient.getInstance().setDownloadDir(downloadDir);
    }


    public void initDownloadDir(Context context) {
        //如果是多账户体系  增加下载路径与用户强相关性
        String rootDownloadDirName = PolyvSDKClient.getInstance().isMultiDownloadAccount() ?
                MUITL_DOWNLOAD_DIR +File.separator+ PolyvSDKClient.getInstance().getViewerId() : OLD_DOWNLOAD_DIR;

        if (externalFilesDirs.isEmpty()) {
            externalFilesDirs = PolyvStorageUtils.getExternalFilesDirs(context);
        }
        if (externalFilesDirs.size() == 0) {
            // TODO 没有可用的存储设备,后续不能使用视频缓存功能
            Log.e(TAG, "没有可用的存储设备,后续不能使用视频缓存功能");
            return;
        }

        //SD卡会有SD卡接触不良，SD卡坏了，SD卡的状态错误的问题。
        //我们在开发中也遇到了SD卡没有权限写入的问题，但是我们确定APP是有赋予android.permission.WRITE_EXTERNAL_STORAGE权限的。
        //有些是系统问题，有些是SD卡本身的问题，这些问题需要通过重新拔插SD卡或者更新SD卡来解决。所以如果想要保存下载视频至SD卡请了解这些情况。
        File downloadDir = new File(externalFilesDirs.get(0), rootDownloadDirName);
        PolyvSDKClient.getInstance().setDownloadDir(downloadDir);

        //兼容旧下载视频目录，如果新接入SDK，无需使用以下代码
        //获取SD卡信息
        PolyvDevMountInfo.getInstance().init(context, new PolyvDevMountInfo.OnLoadCallback() {

            @Override
            public void callback() {
                //是否有可移除的存储介质（例如 SD 卡）或内部（不可移除）存储可供使用。
                if (!PolyvDevMountInfo.getInstance().isSDCardAvaiable()) {
                    return;
                }

                //可移除的存储介质（例如 SD 卡），需要写入特定目录/storage/sdcard1/Android/data/包名/。
                //现在PolyvDevMountInfo.getInstance().getExternalSDCardPath()默认返回的目录路径就是/storage/sdcard1/Android/data/包名/。
                //跟PolyvDevMountInfo.getInstance().init(Context context, final OnLoadCallback callback)接口有区别，请保持同步修改。
                ArrayList<File> subDirList = new ArrayList<>();
                String externalSDCardPath = PolyvDevMountInfo.getInstance().getExternalSDCardPath();
                if (!TextUtils.isEmpty(externalSDCardPath)) {
                    StringBuilder dirPath = new StringBuilder();
                    dirPath.append(externalSDCardPath).append(File.separator).append(MUITL_DOWNLOAD_DIR);
                    File saveDir = new File(dirPath.toString());
                    if (!saveDir.exists()) {
                        saveDir.mkdirs();//创建下载目录
                    }

                    subDirList.add(saveDir);
                }

                //如果没有可移除的存储介质（例如 SD 卡），那么一定有内部（不可移除）存储介质可用，都不可用的情况在前面判断过了。
                File saveDir = new File(PolyvDevMountInfo.getInstance().getInternalSDCardPath() + File.separator + MUITL_DOWNLOAD_DIR);
                if (!saveDir.exists()) {
                    saveDir.mkdirs();//创建下载目录
                }

                //设置下载存储目录
//				PolyvSDKClient.getInstance().setDownloadDir(saveDir);
                subDirList.add(saveDir);
                PolyvSDKClient.getInstance().setSubDirList(subDirList);
            }
        }, true);
    }

    //拷贝旧有的数据到新的账户目录结构
    public void processOldDownloadData(final Context context) {
        String rootDownloadDirName = OLD_DOWNLOAD_DIR;//以前的目录 用户根据自己的目录结构来进行修改

        if (externalFilesDirs.isEmpty()) {
            externalFilesDirs = PolyvStorageUtils.getExternalFilesDirs(context);
        }
        if (externalFilesDirs.size() == 0) {
            // TODO 没有可用的存储设备,后续不能使用视频缓存功能
            Log.e(TAG, "没有可用的存储设备,后续不能使用视频缓存功能");
            return;
        }

        //SD卡会有SD卡接触不良，SD卡坏了，SD卡的状态错误的问题。
        //我们在开发中也遇到了SD卡没有权限写入的问题，但是我们确定APP是有赋予android.permission.WRITE_EXTERNAL_STORAGE权限的。
        //有些是系统问题，有些是SD卡本身的问题，这些问题需要通过重新拔插SD卡或者更新SD卡来解决。所以如果想要保存下载视频至SD卡请了解这些情况。
        final File downloadDir = new File(externalFilesDirs.get(0), rootDownloadDirName);
        final String newDir =  externalFilesDirs.get(0)+File.separator+MUITL_DOWNLOAD_DIR+
                File.separator+PolyvSDKClient.getInstance().getViewerId();
        if (downloadDir.exists() && !isMovingFile) {
            isMovingFile = true;
            PolyvCommonLog.d(TAG,"move old download file ");
            PolyvTaskExecutorUtils.getInstance().executeInSinglePool(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "run: transferOldDBToNewDB");
                    transferOldDBToNewDB();
                    moveDownloadToNewPath(downloadDir);
                }

                private void transferOldDBToNewDB() {
                    //获取原有数据库中数据
                    List<PolyvDownloadInfo> downloadInfos = PolyvDownloadSQLiteHelper.
                            getInstance(context).getAll();
                    //释放原有的数据库
                    PolyvDownloadSQLiteHelper.
                            getInstance(context).release();

                    //插入完成后再初始化下载目录
                    initDownloadDir(context);

                    //创建新的数据库并插入原有数据
                    PolyvDownloadSQLiteHelper.
                            getInstance(context).insert(downloadInfos);
                }

                private void moveDownloadToNewPath(File downloadDir) {
                    try{

                        PolyvCommonLog.d(TAG,"moveDownloadToNewPath:"+newDir);
                        if (downloadDir.exists() && downloadDir.isDirectory()) {
                            copyFile(downloadDir, newDir);
                            com.hpplay.common.utils.FileUtil.deleteFile(downloadDir);
                        }
                    }catch (Exception e){
                        PolyvCommonLog.exception(e);
                    }
                }

                private void copyFile(File downloadDir, String newDir) throws IOException {
                    File[] files = downloadDir.listFiles();
                    int length = files.length;

                    for (int i = 0; i < length; i++) {
                        if(files[i].isFile()){
                            File copyFile = new File(newDir,files[i].getName());
                            FileUtil.copyFile(files[i].getAbsolutePath(),copyFile.getAbsolutePath());
                            files[i].delete();
                        }else {
                            File newFile = new File(newDir,files[i].getName());
                            if(!newFile.exists()){
                                newFile.mkdir();
                            }
                            copyFile(files[i],newFile.getAbsolutePath());
                        }
                    }
                }
            });
        }else {
            PolyvCommonLog.d(TAG,"initDownloadDir");

            initDownloadDir(context);
        }
    }
}
