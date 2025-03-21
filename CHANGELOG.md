### 2025-03-21 polyv-android-sdk-2.0.0-demo v2.22.4.1

### 功能完善&bug修复
* 增加播放线路配置面板(demo)
* 优化播放错误弹窗面板(demo)
* 增加网络状态监控恢复播放机制(demo、sdk)
* 优化异常重试时可按当前进度播放(sdk)
* 优化接口请求超时配置(sdk)
* 优化并启用httpdns优选(sdk)
* 完善播放异常追溯能力(sdk)
* 优化复杂网络的播放稳定性(sdk)

### 修改依赖
```groovy
// 修改对应的sdk依赖
implementation 'net.polyv.android:polyvPlayer:2.22.4.1'
implementation 'net.polyv.android:polyvDownload:2.22.4.1'
implementation 'net.polyv.android:polyvUpload:2.22.4.1'
implementation 'net.polyv.android:polyvSub:2.22.4.1'
```

### 迁移说明

**由 2.18.x 及以下版本升级到 2.19.1 及以上版本时，需要注意视频下载的迁移**

**自 2.19.1 版本开始，本地播放视频鉴权方式进行了调整，为了在覆盖升级时兼容已下载的旧版本视频，初始换sdk时会自动迁移，必须严格测试本地缓存视频是否迁移成功。**

API文档请看 [v2.22.4.1 API](http://repo.polyv.net/android/sdk/2.22.4.1/api/index.html)

### 2025-01-16 polyv-android-sdk-2.0.0-demo v2.22.4

### 功能完善&bug修复
* 支持显示热力图
* 支持自定义标签打点
* 底层httpdns优化


### 修改依赖
```groovy
// 修改对应的sdk依赖
implementation 'net.polyv.android:polyvPlayer:2.22.4'
implementation 'net.polyv.android:polyvDownload:2.22.4'
implementation 'net.polyv.android:polyvUpload:2.22.4'
implementation 'net.polyv.android:polyvSub:2.22.4'
```

### 迁移说明

**由 2.18.x 及以下版本升级到 2.19.1 及以上版本时，需要注意视频下载的迁移**

**自 2.19.1 版本开始，本地播放视频鉴权方式进行了调整，为了在覆盖升级时兼容已下载的旧版本视频，初始换sdk时会自动迁移，必须严格测试本地缓存视频是否迁移成功。**

API文档请看 [v2.22.4 API](http://repo.polyv.net/android/sdk/2.22.4/api/index.html)

### 2024-12-13 polyv-android-sdk-2.0.0-demo v2.22.3

### 功能完善&bug修复
* 【SDK】兼容先初始化下载目录调用顺序导致的下载视频迁移问题
* 【SDK、Demo】增加修复迁移失败视频的方法和UI交互
* 【SDK、Demo】兼容多用户下载的视频迁移问题
* 【Demo】修复某些情况下跑马灯字体过小问题
* 【SDK】修复已知偶现下载崩溃问题
* 【SDK】修复没有画面的视频使用音频模式播放问题
* 【Demo】修复设备锁定屏幕旋转后，播放器还会响应重力感应旋转问题


### 修改依赖
```groovy
// 修改对应的sdk依赖
implementation 'net.polyv.android:polyvPlayer:2.22.3'
implementation 'net.polyv.android:polyvDownload:2.22.3'
implementation 'net.polyv.android:polyvUpload:2.22.3'
implementation 'net.polyv.android:polyvSub:2.22.3'
```

### 迁移说明

**由 2.18.x 及以下版本升级到 2.19.1 及以上版本时，需要注意视频下载的迁移**

**自 2.19.1 版本开始，本地播放视频鉴权方式进行了调整，为了在覆盖升级时兼容已下载的旧版本视频，初始换sdk时会自动迁移，必须严格测试本地缓存视频是否迁移成功。**

API文档请看 [v2.22.3 API](http://repo.polyv.net/android/sdk/2.22.3/api/index.html)

### 2024-09-11 polyv-android-sdk-2.0.0-demo v2.22.2

### 功能完善&bug修复
* 【SDK、Demo】优化视频宽高动态变化问题
* 【SDK】修复ipv6下载加密视频失败问题
* 【SDK】zip4j解压库版本升级
* 【SDK、Demo】下载视频token失效添加失败回调


### 修改依赖
```groovy
// 修改对应的sdk依赖
implementation 'net.polyv.android:polyvPlayer:2.22.2'
implementation 'net.polyv.android:polyvDownload:2.22.2'
implementation 'net.polyv.android:polyvUpload:2.22.2'
implementation 'net.polyv.android:polyvSub:2.22.2'
```

### 迁移说明

**由 2.18.x 及以下版本升级到 2.19.1 及以上版本时，需要注意视频下载的迁移**

**自 2.19.1 版本开始，本地播放视频鉴权方式进行了调整，为了在覆盖升级时兼容已下载的旧版本视频，初始换sdk时会自动迁移，必须严格测试本地缓存视频是否迁移成功。必须确保`client.settingsWithConfigStrin`g在`initDownloadDir`之前调用，否则迁移失败视频将无法播放。**

API文档请看 [v2.22.2 API](http://repo.polyv.net/android/sdk/2.22.2/api/index.html)

### 2024-07-22 polyv-android-sdk-2.0.0-demo v2.22.1

### 功能完善&bug修复
* 【SDK】新增清除播放时长api
* 【SDK】修复播放器抛出空指针异常
* 【SDK】修复切换线路崩溃异常
* 【SDK】修复上传日志崩溃异常


### 修改依赖
```groovy
// 修改对应的sdk依赖
implementation 'net.polyv.android:polyvPlayer:2.22.1'
implementation 'net.polyv.android:polyvDownload:2.22.1'
implementation 'net.polyv.android:polyvUpload:2.22.1'
implementation 'net.polyv.android:polyvSub:2.22.1'
```

### 迁移说明

**由 2.18.x 及以下版本升级到 2.19.1 及以上版本时，需要注意视频下载的迁移**

**自 2.19.1 版本开始，本地播放视频鉴权方式进行了调整，为了在覆盖升级时兼容已下载的旧版本视频，初始换sdk时会自动迁移，必须严格测试本地缓存视频是否迁移成功。必须确保`client.settingsWithConfigStrin`g在`initDownloadDir`之前调用，否则迁移失败视频将无法播放。**

API文档请看 [v2.22.1 API](http://repo.polyv.net/android/sdk/2.22.1/api/index.html)

### 2024-06-07 polyv-android-sdk-2.0.0-demo v2.22.0

### 功能完善&bug修复
* 【Demo】新增广告跳过
* 【SDK、Demo】新增双字幕
* 【SDK、Demo】新增广告文案响应后台配置
* 【SDK】修复部分视频下载失败问题
* 【SDK】修复续播功能失败问题
* 【SDK】修复硬编码安全隐患问题
* 【Demo】修复题目不显示问题

### 修改依赖
```groovy
// 修改对应的sdk依赖
implementation 'net.polyv.android:polyvPlayer:2.22.0'
implementation 'net.polyv.android:polyvDownload:2.22.0'
implementation 'net.polyv.android:polyvUpload:2.22.0'
implementation 'net.polyv.android:polyvSub:2.22.0'
```

### 迁移说明

**由 2.18.x 及以下版本升级到 2.19.1 及以上版本时，需要注意视频下载的迁移**

**自 2.19.1 版本开始，本地播放视频鉴权方式进行了调整，为了在覆盖升级时兼容已下载的旧版本视频，初始换sdk时会自动迁移，必须严格测试本地缓存视频是否迁移成功。必须确保`client.settingsWithConfigStrin`g在`initDownloadDir`之前调用，否则迁移失败视频将无法播放。**

API文档请看 [v2.22.0 API](http://repo.polyv.net/android/sdk/2.22.0/api/index.html)

### 2024-03-19 polyv-android-sdk-2.0.0-demo v2.21.2

### 功能完善&bug修复
* 【SDK】优化升级httpdns
* 【SDK】完善错误日志收集
* 【SDK】兼容旧版投屏功能使用
* 【SDK】修复已知内存泄露问题

### 修改依赖
```groovy
// 修改对应的sdk依赖
implementation 'net.polyv.android:polyvPlayer:2.21.2'
implementation 'net.polyv.android:polyvDownload:2.21.2'
implementation 'net.polyv.android:polyvUpload:2.21.2'
implementation 'net.polyv.android:polyvSub:2.21.2'
```

### 迁移说明

**由 2.18.x 及以下版本升级到 2.19.1 及以上版本时，需要注意视频下载的迁移**

**自 2.19.1 版本开始，本地播放视频鉴权方式进行了调整，为了在覆盖升级时兼容已下载的旧版本视频，初始换sdk时会自动迁移，必须严格测试本地缓存视频是否迁移成功。必须确保`client.settingsWithConfigStrin`g在`initDownloadDir`之前调用，否则迁移失败视频将无法播放。**

API文档请看 [v2.21.2 API](http://repo.polyv.net/android/sdk/2.21.2/api/index.html)

### 2024-02-22 polyv-android-sdk-2.0.0-demo v2.21.1

### 功能完善
* 【SDK】优化升级httpdns

### 修改依赖
```groovy
// 修改对应的sdk依赖
implementation 'net.polyv.android:polyvPlayer:2.21.1'
implementation 'net.polyv.android:polyvDownload:2.21.1'
implementation 'net.polyv.android:polyvUpload:2.21.1'
implementation 'net.polyv.android:polyvSub:2.21.1'
```

### 迁移说明

**由 2.18.x 及以下版本升级到 2.19.1 及以上版本时，需要注意视频下载的迁移**

**自 2.19.1 版本开始，本地播放视频鉴权方式进行了调整，为了在覆盖升级时兼容已下载的旧版本视频，初始换sdk时会自动迁移，必须严格测试本地缓存视频是否迁移成功。必须确保`client.settingsWithConfigStrin`g在`initDownloadDir`之前调用，否则迁移失败视频将无法播放。**

API文档请看 [v2.21.1 API](http://repo.polyv.net/android/sdk/2.21.1/api/index.html)

### 2024-01-05 polyv-android-sdk-2.0.0-demo v2.21.0

### 功能完善
* 【SDK】优化部分视频的seek速度
* 【SDK】优化在弱网情况下的视频播放体验
* 【SDK】修复自动续播失效的问题
* 【SDK】修复已知会引起崩溃的问题

### 修改依赖
```groovy
// 修改对应的sdk依赖
implementation 'net.polyv.android:polyvPlayer:2.21.0'
implementation 'net.polyv.android:polyvDownload:2.21.0'
implementation 'net.polyv.android:polyvUpload:2.21.0'
implementation 'net.polyv.android:polyvSub:2.21.0'
```

### 迁移说明

**由 2.18.x 及以下版本升级到 2.19.1 及以上版本时，需要注意视频下载的迁移**

**自 2.19.1 版本开始，本地播放视频鉴权方式进行了调整，为了在覆盖升级时兼容已下载的旧版本视频，初始换sdk时会自动迁移，必须严格测试本地缓存视频是否迁移成功。必须确保`client.settingsWithConfigStrin`g在`initDownloadDir`之前调用，否则迁移失败视频将无法播放。**

API文档请看 [v2.21.0 API](http://repo.polyv.net/android/sdk/2.21.0/api/index.html)

### 2023-09-01 polyv-android-sdk-2.0.0-demo v2.20.0

### 功能完善
* 【SDK】支持跨端续播
* 【SDK】支持溯源水印
* 【demo】优化跑马灯显示样式
* 【SDK、demo】移除先前版本中未使用的imei, deviceId字段

### 修改依赖
``` groovy
// 修改对应的sdk依赖
implementation 'net.polyv.android:polyvPlayer:2.20.0'
implementation 'net.polyv.android:polyvDownload:2.20.0'
implementation 'net.polyv.android:polyvUpload:2.20.0'
implementation 'net.polyv.android:polyvSub:2.20.0'
```

### 迁移说明

**由 2.18.x 及以下版本升级到 2.19.1 及以上版本时，需要注意视频下载的迁移**

**自 2.19.1 版本开始，本地播放视频鉴权方式进行了调整，为了在覆盖升级时兼容已下载的旧版本视频，初始换sdk时会自动迁移，必须严格测试本地缓存视频是否迁移成功。必须确保`client.settingsWithConfigStrin`g在`initDownloadDir`之前调用，否则迁移失败视频将无法播放。**

API文档请看 [v2.20.0 API](http://repo.polyv.net/android/sdk/2.20.0/api/index.html)

### 2023-07-20 polyv-android-sdk-2.0.0-demo v2.19.1

### 功能完善&bug修复
* 【SDK】离线视频鉴权方式修改
* 【SDK】播放器支持设置起播位置
* 【SDK】优化在弱网情况下的视频播放体验
* 【SDK】修复部分视频音画不同步问题
* 【SDK】修复已知会引起崩溃的问题

### 修改依赖
``` groovy
// 修改对应的sdk依赖
implementation 'net.polyv.android:polyvPlayer:2.19.1'
implementation 'net.polyv.android:polyvDownload:2.19.1'
implementation 'net.polyv.android:polyvUpload:2.19.1'
implementation 'net.polyv.android:polyvSub:2.19.1'
```

### 迁移说明

**由 2.18.x 及以下版本升级到 2.19.1 及以上版本时，需要注意视频下载的迁移**

**自 2.19.1 版本开始，本地播放视频鉴权方式进行了调整，为了在覆盖升级时兼容已下载的旧版本视频，初始换sdk时会自动迁移，必须严格测试本地缓存视频是否迁移成功。必须确保`client.settingsWithConfigStrin`g在`initDownloadDir`之前调用，否则迁移失败视频将无法播放。**

API文档请看 [v2.19.1 API](http://repo.polyv.net/android/sdk/2.19.1/api/index.html)

### 2023-03-14 polyv-android-sdk-2.0.0-demo v2.18.4

### 功能完善&bug修复
* 【SDK】优化播放器超时重连
* 【SDK】修复错误vid引起崩溃异常
* 【SDK】修复gif库存在漏洞问题
* 【SDK】修复错误token引发崩溃问题

### 修改依赖
``` groovy
// 修改对应的sdk依赖
implementation 'net.polyv.android:polyvPlayer:2.18.4'//SDK核心包
implementation 'net.polyv.android:polyvDownload:2.18.4'
implementation 'net.polyv.android:polyvGif:2.2.3'
implementation 'net.polyv.android:polyvSub:2.18.4'
```

API文档请看 [v2.18.4 API](http://repo.polyv.net/android/sdk/2.18.4/api/index.html)


### 2022-11-18 polyv-android-sdk-2.0.0-demo v2.18.3

### 功能完善&bug修复
* 【SDK】新增vrm13开关,修复vrm13播放异常

### 修改依赖
``` groovy
// 修改对应的sdk依赖
implementation 'net.polyv.android:polyvPlayer:2.18.3'//SDK核心包
```

API文档请看 [v2.18.3 API](http://repo.polyv.net/android/sdk/2.18.3/api/index.html)

### 2022-10-31 polyv-android-sdk-2.0.0-demo v2.18.2

### 功能完善&bug修复
* 【Demo、SDK】幕享投屏升级到1.1.5新版
* 【SDK】支持后台配置HttpDNS开关
* 【Demo、SDK】播放器支持软硬解手动切换功能
* 【SDK】修复VRM13使用软解播放

### 修改依赖
``` groovy
// 修改对应的sdk依赖
implementation 'net.polyv.android:polyvPlayer:2.18.2'//SDK核心包
implementation 'net.polyv.android:polyvModuleABI:1.7.6'//SDK核心包
implementation 'net.polyv.android:polyvDownload:2.18.2'//SDK下载功能
```

API文档请看 [v2.18.2 API](http://repo.polyv.net/android/sdk/2.18.2/api/index.html)


### 2022-09-23 polyv-android-sdk-2.0.0-demo v2.18.0

### 功能完善&bug修复
* 【SDK】新增支持播放vrm13加密视频
* 【Demo】修复部分机型小窗播放关闭后播放器未销毁的问题
* 【SDK】修复部分视频播放未触发 onPlay 回调的问题

### 修改依赖
``` groovy
// 修改对应的sdk依赖
implementation 'net.polyv.android:polyvPlayer:2.18.0'//SDK核心包
implementation 'net.polyv.android:polyvDownload:2.18.0'//SDK下载功能
```

API文档请看 [v2.18.0 API](http://repo.polyv.net/android/sdk/2.18.0/api/index.html)

### 2022-06-21 polyv-android-sdk-2.0.0-demo v2.16.6

由于[ bintray 停服 ](https://jfrog.com/blog/into-the-sunset-bintray-jcenter-gocenter-and-chartcenter/) 导致历史版本依赖将不可使用。旧版本用户请升级至v2.15.2+。如集成Demo层建议同步更新。

### 功能完善&bug修复

+ 【SDK】提升账号安全性
+ 【SDK】初始化支持多个加密串同时使用
+ 【SDK】播放器支持https+ip访问开启了SNI验证的服务器
+ 【SDK & Demo】修复elog错误信息缺失问题
+ 【SDK】修复已知问题

### 修改依赖

  ``` groovy
  // 打开项目级别根目录的 build.gradle，补充MavenCentral源地址
  allprojects {
      repositories {
          //...省略
          //mavenCentral 源
          mavenCentral()
          //阿里云效关于central的镜像
          maven{
              url 'https://maven.aliyun.com/repository/central'
          }
          //阿里云效仓库，必须添加
          maven {
              credentials {
                  username '609cc5623a10edbf36da9615'
                  password 'EbkbzTNHRJ=P'
              }
              url 'https://packages.aliyun.com/maven/repository/2102846-release-8EVsoM/'
          }
      }
  }
  
  // 修改对应的sdk依赖，注意groupId已经变更，请整个依赖复制修改，请勿仅改动版本号
  implementation 'net.polyv.android:polyvPlayer:2.16.6'//SDK核心包
  implementation 'net.polyv.android:polyvDownload:2.16.6'//SDK下载功能
  implementation 'net.polyv.android:polyvUpload:2.3.4'//SDK上传功能
  implementation 'net.polyv.android:polyvSub:2.16.6'//弹幕、截图功能中使用
  ```


  API文档请看[v2.16.6 API](http://repo.polyv.net/android/sdk/2.16.6/api/index.html)

### 2021-10-28 polyv-android-sdk-2.0.0-demo v2.16.5

由于[ bintray 停服 ](https://jfrog.com/blog/into-the-sunset-bintray-jcenter-gocenter-and-chartcenter/) 导致历史版本依赖将不可使用。旧版本用户请升级至v2.15.2+。如集成Demo层建议同步更新。

### 功能完善&bug修复

+ 【SDK、Demo】发布跑马灯2.0，支持跑马灯局部闪烁、局部滚动以及显示时间、是否描边等属性设置


### 修改依赖

  ``` groovy
  // 打开项目级别根目录的 build.gradle，补充MavenCentral源地址
  allprojects {
      repositories {
          //...省略
          //mavenCentral 源
          mavenCentral()
          //阿里云效关于central的镜像
          maven{
              url 'https://maven.aliyun.com/repository/central'
          }
          //阿里云效仓库，必须添加
          maven {
              credentials {
                  username '609cc5623a10edbf36da9615'
                  password 'EbkbzTNHRJ=P'
              }
              url 'https://packages.aliyun.com/maven/repository/2102846-release-8EVsoM/'
          }
      }
  }
  
  // 修改对应的sdk依赖，注意groupId已经变更，请整个依赖复制修改，请勿仅改动版本号
  implementation 'net.polyv.android:polyvPlayer:2.16.5'//SDK核心包
  implementation 'net.polyv.android:polyvDownload:2.16.3'//SDK下载功能
  implementation 'net.polyv.android:polyvUpload:2.3.4'//SDK上传功能
  implementation 'net.polyv.android:polyvSub:2.16.5'//弹幕、截图功能中使用
  ```


  API文档请看[v2.16.5 API](http://repo.polyv.net/android/sdk/2.16.5/api/index.html)

---
### 2021-08-02 polyv-android-sdk-2.0.0-demo v2.16.3
由于[ bintray 停服 ](https://jfrog.com/blog/into-the-sunset-bintray-jcenter-gocenter-and-chartcenter/) 导致历史版本依赖将不可使用。旧版本用户请升级至v2.15.2+。如集成Demo层建议同步更新。

### 功能完善&bug修复
* 【SDK、Demo】优化续播规则
* 【Demo】新增暂停广告大小参数
* 【SDK】修复m3u8开始时间不连续导致无法seek到0的问题。
* 【SDK、Demo】修复高刷新率屏幕下弹幕重复出现的问题。

### 修改依赖
``` groovy
// 打开项目级别根目录的 build.gradle，补充MavenCentral源地址
allprojects {
    repositories {
        //...省略
        //mavenCentral 源
        mavenCentral()
        //阿里云效关于central的镜像
        maven{
            url 'https://maven.aliyun.com/repository/central'
        }
        //阿里云效仓库，必须添加
        maven {
            credentials {
                username '609cc5623a10edbf36da9615'
                password 'EbkbzTNHRJ=P'
            }
            url 'https://packages.aliyun.com/maven/repository/2102846-release-8EVsoM/'
        }
    }
}

// 修改对应的sdk依赖，注意groupId已经变更，请整个依赖复制修改，请勿仅改动版本号
implementation 'net.polyv.android:polyvPlayer:2.16.3'//SDK核心包
implementation 'net.polyv.android:polyvDownload:2.16.3'//SDK下载功能
implementation 'net.polyv.android:polyvUpload:2.3.4'//SDK上传功能
implementation 'net.polyv.android:polyvSub:2.16.3'//弹幕、截图功能中使用
```


API文档请看[v2.16.0 API](http://repo.polyv.net/android/sdk/2.16.0/api/index.html)

### 设置学员唯一标识
__请调用`PolyvSDKClient`的[setViewerId](
http://repo.polyv.net/android/sdk/2.12.1/api/com/easefun/polyvsdk/PolyvSDKClient.html#setViewerId-java.lang.String-)方法设置学员唯一标识。可以获得Polyv更好的技术支持服务，设置学员唯一标识的[意义点这里](https://github.com/easefun/polyv-android-sdk-2.0-demo/wiki/10.设置学员唯一标识的意义)__。

### 必要修改
* 旧版本的下载存在队列错误的问题，无法向前兼容，请使用最新版本的下载队列，参考[diff](https://github.com/easefun/polyv-android-sdk-2.0-demo/commit/742eab45026fbe9c9e19c9d61ef2203949a5acce#diff-3ef6bab6a4d852e377d11f8d75b79fda)。
* 从[2.8.0](https://github.com/easefun/polyv-android-sdk-2.0-demo/releases/tag/v2.8.0)版本开始播放器的渲染控件由SurfaceView更改为TextureView，由于渲染控件间的层级问题，如果是使用demo中的弹幕功能，那么弹幕的view需要更改为DanmakuView才能正常使用。如果是点播sdk和直播sdk同时集成，那么直播sdk的弹幕渲染控件也需要改为DanmakuView。
* 从[2.13.0](https://github.com/easefun/polyv-android-sdk-2.0-demo/releases/tag/v2.13.0)开始，设置下载目录在Android Q版本后仅支持私有目录，强烈建议开发者提前做好数据迁移工作。若保存在非私有目录，则在Android Q后存储相关功能将受限，Android Q以下不受影响。同时也可以设置`requestLegacyExternalStorage`开启Android Q的兼容模式来临时过渡。
* 从[2.15.0](https://github.com/easefun/polyv-android-sdk-2.0-demo/releases/tag/v2.15.0)开始，投屏SDK正式转移到Demo层，旧版的点播投屏仍旧可以使用，但投屏sdk不再维护更新。旧版投屏SDK和`com.easefun.polyv:polyvPlayer:2.15.0+`存在冲突。详情查看Wiki[《7.视频投屏》](https://github.com/easefun/polyv-android-sdk-2.0-demo/wiki/7.%E8%A7%86%E9%A2%91%E6%8A%95%E5%B1%8F)介绍
* 从[2.15.2](https://github.com/easefun/polyv-android-sdk-2.0-demo/tree/v2.15.2)开始，旧版SDK由于Bintray停服将废弃使用，请使用新版本v2.15.2+集成。投屏SDK自2.15.0起移动到了Demo层，故不迁移更新，请开发者集成新版。

---

### 2021-07-07 polyv-android-sdk-2.0.0-demo v2.16.2
### 功能完善&bug修复
* 【SDK】修复`setVideoPath()`播放外部链接失败问题，修复续播问题

### 修改依赖
``` groovy
//打开项目级别根目录的 build.gradle，补充MavenCentral源地址
allprojects {
    repositories {
        //...省略
        //mavenCentral 源
        mavenCentral()
        //阿里云效关于central的镜像
        maven{
            url 'https://maven.aliyun.com/repository/central'
        }
        //阿里云效仓库，必须添加
        maven {
            credentials {
                username '609cc5623a10edbf36da9615'
                password 'EbkbzTNHRJ=P'
            }
            url 'https://packages.aliyun.com/maven/repository/2102846-release-8EVsoM/'
        }
    }
}

//修改对应的sdk依赖，注意groupId已经变更，请整个依赖复制修改，请勿仅改动版本号
    implementation 'net.polyv.android:polyvPlayer:2.16.2'//SDK核心包

```


API文档请看[v2.16.0 API](http://repo.polyv.net/android/sdk/2.16.0/api/index.html)
### 必要修改
* 旧版本的下载存在队列错误的问题，无法向前兼容，请使用最新版本的下载队列，参考[diff](https://github.com/easefun/polyv-android-sdk-2.0-demo/commit/742eab45026fbe9c9e19c9d61ef2203949a5acce#diff-3ef6bab6a4d852e377d11f8d75b79fda)。
* 从[2.8.0](https://github.com/easefun/polyv-android-sdk-2.0-demo/releases/tag/v2.8.0)版本开始播放器的渲染控件由SurfaceView更改为TextureView，由于渲染控件间的层级问题，如果是使用demo中的弹幕功能，那么弹幕的view需要更改为DanmakuView才能正常使用。如果是点播sdk和直播sdk同时集成，那么直播sdk的弹幕渲染控件也需要改为DanmakuView。
* 从[2.13.0](https://github.com/easefun/polyv-android-sdk-2.0-demo/releases/tag/v2.13.0)开始，设置下载目录在Android Q版本后仅支持私有目录，强烈建议开发者提前做好数据迁移工作。若保存在非私有目录，则在Android Q后存储相关功能将受限，Android Q以下不受影响。同时也可以设置`requestLegacyExternalStorage`开启Android Q的兼容模式来临时过渡。
* 从[2.15.0](https://github.com/easefun/polyv-android-sdk-2.0-demo/releases/tag/v2.15.0)开始，投屏SDK正式转移到Demo层，旧版的点播投屏仍旧可以使用，但投屏sdk不再维护更新。旧版投屏SDK和`com.easefun.polyv:polyvPlayer:2.15.0+`存在冲突。详情查看Wiki[《7.视频投屏》](https://github.com/easefun/polyv-android-sdk-2.0-demo/wiki/7.%E8%A7%86%E9%A2%91%E6%8A%95%E5%B1%8F)介绍
* 从[2.15.2](https://github.com/easefun/polyv-android-sdk-2.0-demo/tree/v2.15.2)开始，旧版SDK由于Bintray停服将废弃使用，请使用新版本v2.15.2+集成。投屏SDK自2.15.0起移动到了Demo层，故不迁移更新，请开发者集成新版。
---

### 2021-07-01 polyv-android-sdk-2.0.0-demo v2.16.1
### 功能完善&bug修复
* 【SDK】适配DRM12方案下的视频播放与下载
* 【SDK】播放器私有化
* 【SDK】播放器适配android 11机型的tag pointer问题

### 修改依赖
``` groovy
//打开项目级别根目录的 build.gradle，补充MavenCentral源地址
allprojects {
    repositories {
        //...省略
        //mavenCentral 源
        mavenCentral()
        //阿里云效关于central的镜像
        maven{
            url 'https://maven.aliyun.com/repository/central'
        }
        //阿里云效仓库，必须添加
        maven {
            credentials {
                username '609cc5623a10edbf36da9615'
                password 'EbkbzTNHRJ=P'
            }
            url 'https://packages.aliyun.com/maven/repository/2102846-release-8EVsoM/'
        }
    }
}

//修改对应的sdk依赖，注意groupId已经变更，请整个依赖复制修改，请勿仅改动版本号
    implementation 'net.polyv.android:polyvPlayer:2.16.1'//SDK核心包
    implementation 'net.polyv.android:polyvDownload:2.16.0'//SDK下载功能
    implementation 'net.polyv.android:polyvUpload:2.3.3'//SDK上传功能
    implementation 'net.polyv.android:polyvGif:2.2.2'//demo中课程讨论区显示的内容里用到的包
    implementation 'net.polyv.android:polyvSub:2.15.2'//弹幕、截图功能中使用
 
    implementation files("libs/source-lecast-release.aar")//乐播投屏sdk，2.15.0起改为demo层集成，详细说明见wiki文档

```


API文档请看[v2.16.0 API](http://repo.polyv.net/android/sdk/2.16.0/api/index.html)

### 设置学员唯一标识
__请调用`PolyvSDKClient`的[setViewerId](
http://repo.polyv.net/android/sdk/2.12.1/api/com/easefun/polyvsdk/PolyvSDKClient.html#setViewerId-java.lang.String-)方法设置学员唯一标识。可以获得Polyv更好的技术支持服务，设置学员唯一标识的[意义点这里](https://github.com/easefun/polyv-android-sdk-2.0-demo/wiki/10.设置学员唯一标识的意义)__。

### 必要修改
* 旧版本的下载存在队列错误的问题，无法向前兼容，请使用最新版本的下载队列，参考[diff](https://github.com/easefun/polyv-android-sdk-2.0-demo/commit/742eab45026fbe9c9e19c9d61ef2203949a5acce#diff-3ef6bab6a4d852e377d11f8d75b79fda)。
* 从[2.8.0](https://github.com/easefun/polyv-android-sdk-2.0-demo/releases/tag/v2.8.0)版本开始播放器的渲染控件由SurfaceView更改为TextureView，由于渲染控件间的层级问题，如果是使用demo中的弹幕功能，那么弹幕的view需要更改为DanmakuView才能正常使用。如果是点播sdk和直播sdk同时集成，那么直播sdk的弹幕渲染控件也需要改为DanmakuView。
* 从[2.13.0](https://github.com/easefun/polyv-android-sdk-2.0-demo/releases/tag/v2.13.0)开始，设置下载目录在Android Q版本后仅支持私有目录，强烈建议开发者提前做好数据迁移工作。若保存在非私有目录，则在Android Q后存储相关功能将受限，Android Q以下不受影响。同时也可以设置`requestLegacyExternalStorage`开启Android Q的兼容模式来临时过渡。
* 从[2.15.0](https://github.com/easefun/polyv-android-sdk-2.0-demo/releases/tag/v2.15.0)开始，投屏SDK正式转移到Demo层，旧版的点播投屏仍旧可以使用，但投屏sdk不再维护更新。旧版投屏SDK和`com.easefun.polyv:polyvPlayer:2.15.0+`存在冲突。详情查看Wiki[《7.视频投屏》](https://github.com/easefun/polyv-android-sdk-2.0-demo/wiki/7.%E8%A7%86%E9%A2%91%E6%8A%95%E5%B1%8F)介绍
* 从[2.15.2](https://github.com/easefun/polyv-android-sdk-2.0-demo/tree/v2.15.2)开始，旧版SDK由于Bintray停服将废弃使用，请使用新版本v2.15.2+集成。投屏SDK自2.15.0起移动到了Demo层，故不迁移更新，请开发者集成新版。

### 2020-12-23 polyv-android-sdk-2.0.0-demo v2.14.0
### 设置学员唯一标识
__请调用`PolyvSDKClient`的[setViewerId](
http://repo.polyv.net/android/sdk/2.12.1/api/com/easefun/polyvsdk/PolyvSDKClient.html#setViewerId-java.lang.String-)方法设置学员唯一标识。可以获得Polyv更好的技术支持服务，设置学员唯一标识的[意义点这里](https://github.com/easefun/polyv-android-sdk-2.0-demo/wiki/10.设置学员唯一标识的意义)__。

### 功能完善&bug修复
* 本地播放支持播放视频片头[SDK]
* 增加了禁止拖拽事件的通知[Demo、SDK]
* 优化投屏功能视频播放进度的同步[Demo、SDK]
* 修复部分已知的异常报错[SDK]

### 必要修改
* 旧版本的下载存在队列错误的问题，无法向前兼容，请使用最新版本的下载队列，参考[diff](https://github.com/easefun/polyv-android-sdk-2.0-demo/commit/742eab45026fbe9c9e19c9d61ef2203949a5acce#diff-3ef6bab6a4d852e377d11f8d75b79fda)。
* 从[2.8.0](https://github.com/easefun/polyv-android-sdk-2.0-demo/releases/tag/v2.8.0)版本开始播放器的渲染控件由SurfaceView更改为TextureView，由于渲染控件间的层级问题，如果是使用demo中的弹幕功能，那么弹幕的view需要更改为DanmakuView才能正常使用。如果是点播sdk和直播sdk同时集成，那么直播sdk的弹幕渲染控件也需要改为DanmakuView。
* 从[2.13.0](https://github.com/easefun/polyv-android-sdk-2.0-demo/releases/tag/v2.13.0)开始，设置下载目录在Android Q版本后仅支持私有目录，强烈建议开发者提前做好数据迁移工作。若保存在非私有目录，则在Android Q后存储相关功能将受限，Android Q以下不受影响。同时也可以设置`requestLegacyExternalStorage`开启Android Q的兼容模式来临时过渡。

### 修改依赖
更新以下各依赖版本
```Groovy
implementation 'com.easefun.polyv:polyvPlayer:2.14.0'
implementation 'com.easefun.polyv:polyvScreencast:0.3.0'
```

__jar和so都由maven仓库统一管理，通过依赖自动引入。__<br/>

API文档请看[v2.13.1 API](http://repo.polyv.net/android/sdk/2.13.1/api/index.html)

### 2020-11-19 polyv-android-sdk-2.0.0-demo v2.13.2
### 设置学员唯一标识
__请调用`PolyvSDKClient`的[setViewerId](
http://repo.polyv.net/android/sdk/2.12.1/api/com/easefun/polyvsdk/PolyvSDKClient.html#setViewerId-java.lang.String-)方法设置学员唯一标识。可以获得Polyv更好的技术支持服务，设置学员唯一标识的[意义点这里](https://github.com/easefun/polyv-android-sdk-2.0-demo/wiki/10.设置学员唯一标识的意义)__。

### 功能完善&bug修复
提供HttpDNS开关，支持IPV6[SDK]

### 必要修改
* 旧版本的下载存在队列错误的问题，无法向前兼容，请使用最新版本的下载队列，参考[diff](https://github.com/easefun/polyv-android-sdk-2.0-demo/commit/742eab45026fbe9c9e19c9d61ef2203949a5acce#diff-3ef6bab6a4d852e377d11f8d75b79fda)。
* 从[2.8.0](https://github.com/easefun/polyv-android-sdk-2.0-demo/releases/tag/v2.8.0)版本开始播放器的渲染控件由SurfaceView更改为TextureView，由于渲染控件间的层级问题，如果是使用demo中的弹幕功能，那么弹幕的view需要更改为DanmakuView才能正常使用。如果是点播sdk和直播sdk同时集成，那么直播sdk的弹幕渲染控件也需要改为DanmakuView。
* 从[2.13.0](https://github.com/easefun/polyv-android-sdk-2.0-demo/releases/tag/v2.13.0)开始，设置下载目录在Android Q版本后仅支持私有目录，强烈建议开发者提前做好数据迁移工作。若保存在非私有目录，则在Android Q后存储相关功能将受限，Android Q以下不受影响。同时也可以设置`requestLegacyExternalStorage`开启Android Q的兼容模式来临时过渡。

### 修改依赖
更新以下各依赖版本
```Groovy
implementation 'com.easefun.polyv:polyvPlayer:2.13.2'
```

__jar和so都由maven仓库统一管理，通过依赖自动引入。__<br/>

API文档请看[v2.13.1 API](http://repo.polyv.net/android/sdk/2.13.1/api/index.html)

### 2020-09-10 polyv-android-sdk-2.0.0-demo v2.13.1
### 设置学员唯一标识
__请调用`PolyvSDKClient`的[setViewerId](
http://repo.polyv.net/android/sdk/2.12.1/api/com/easefun/polyvsdk/PolyvSDKClient.html#setViewerId-java.lang.String-)方法设置学员唯一标识。可以获得Polyv更好的技术支持服务，设置学员唯一标识的[意义点这里](https://github.com/easefun/polyv-android-sdk-2.0-demo/wiki/10.设置学员唯一标识的意义)__。

### 功能完善&bug修复
* 压缩图片，减小应用体积[Demo]
* 修复 PolyvPlayerPreviewView 偶现崩溃问题[Demo]
* 修复投屏偶现退出时，接收端没有一并退出的问题[Demo]
* 修复个别 Android 4.x 机型会崩溃问题[SDK]

### 必要修改
* 旧版本的下载存在队列错误的问题，无法向前兼容，请使用最新版本的下载队列，参考[diff](https://github.com/easefun/polyv-android-sdk-2.0-demo/commit/742eab45026fbe9c9e19c9d61ef2203949a5acce#diff-3ef6bab6a4d852e377d11f8d75b79fda)。
* 从[2.8.0](https://github.com/easefun/polyv-android-sdk-2.0-demo/releases/tag/v2.8.0)版本开始播放器的渲染控件由SurfaceView更改为TextureView，由于渲染控件间的层级问题，如果是使用demo中的弹幕功能，那么弹幕的view需要更改为DanmakuView才能正常使用。如果是点播sdk和直播sdk同时集成，那么直播sdk的弹幕渲染控件也需要改为DanmakuView。
* 从[2.13.0](https://github.com/easefun/polyv-android-sdk-2.0-demo/releases/tag/v2.13.0)开始，设置下载目录在Android Q版本后仅支持私有目录，强烈建议开发者提前做好数据迁移工作。若保存在非私有目录，则在Android Q后存储相关功能将受限，Android Q以下不受影响。同时也可以设置`requestLegacyExternalStorage`开启Android Q的兼容模式来临时过渡。

### 修改依赖
更新以下各依赖版本
```Groovy
implementation 'com.easefun.polyv:polyvPlayer:2.13.1'//SDK核心包
implementation 'com.easefun.polyv:polyvPlayerABI:1.9.6'//SDK核心包
implementation 'com.easefun.polyv:polyvUpload:2.3.1'//SDK上传功能
```

__jar和so都由maven仓库统一管理，通过依赖自动引入。__<br/>

API文档请看[v2.13.1 API](http://repo.polyv.net/android/sdk/2.13.1/api/index.html)

### 2020-07-20 polyv-android-sdk-2.0.0-demo v2.13.0
### 设置学员唯一标识
__请调用`PolyvSDKClient`的[setViewerId](
http://repo.polyv.net/android/sdk/2.12.1/api/com/easefun/polyvsdk/PolyvSDKClient.html#setViewerId-java.lang.String-)方法设置学员唯一标识。可以获得Polyv更好的技术支持服务，设置学员唯一标识的[意义点这里](https://github.com/easefun/polyv-android-sdk-2.0-demo/wiki/10.设置学员唯一标识的意义)__。

### 功能完善&bug修复
* SDK适配到Android Q，注意自此版本开始，下载目录仅限私有目录。[SDK、Demo]
* 安全性升级。[SDK]
* 优化音频焦点处理。[SDK]
* 修复下载导致的崩溃异常，修复异常30027错误。[SDK]
* 修复wav格式的音频源文件切换倍速无效[SDK]

### 必要修改
* 旧版本的下载存在队列错误的问题，无法向前兼容，请使用最新版本的下载队列，参考[diff](https://github.com/easefun/polyv-android-sdk-2.0-demo/commit/742eab45026fbe9c9e19c9d61ef2203949a5acce#diff-3ef6bab6a4d852e377d11f8d75b79fda)。
* 从[2.8.0](https://github.com/easefun/polyv-android-sdk-2.0-demo/releases/tag/v2.8.0)版本开始播放器的渲染控件由SurfaceView更改为TextureView，由于渲染控件间的层级问题，如果是使用demo中的弹幕功能，那么弹幕的view需要更改为DanmakuView才能正常使用。如果是点播sdk和直播sdk同时集成，那么直播sdk的弹幕渲染控件也需要改为DanmakuView。
* 从[2.13.0](https://github.com/easefun/polyv-android-sdk-2.0-demo/releases/tag/v2.13.0)开始，设置下载目录在Android Q版本后仅支持私有目录，强烈建议开发者提前做好数据迁移工作。若保存在非私有目录，则在Android Q后存储相关功能将受限，Android Q以下不受影响。同时也可以设置`requestLegacyExternalStorage`开启Android Q的兼容模式来临时过渡。

### 修改依赖
更新以下各依赖版本
```Groovy
implementation 'com.easefun.polyv:polyvPlayer:2.13.0'//SDK核心包
implementation 'com.easefun.polyv:polyvPlayerABI:1.9.6//SDK核心包
implementation 'com.easefun.polyv:polyvDownload:2.13.0'//SDK下载功能'
implementation 'com.easefun.polyv:polyvUpload:2.3.0'//SDK上传功能
```

__jar和so都由maven仓库统一管理，通过依赖自动引入。__<br/>

API文档请看[v2.13.0 API](http://repo.polyv.net/android/sdk/2.13.0/api/index.html)

### 2020-06-02 polyv-android-sdk-2.0.0-demo v2.12.2
### 设置学员唯一标识
__请调用`PolyvSDKClient`的[setViewerId](
http://repo.polyv.net/android/sdk/2.12.1/api/com/easefun/polyvsdk/PolyvSDKClient.html#setViewerId-java.lang.String-)方法设置学员唯一标识。可以获得Polyv更好的技术支持服务，设置学员唯一标识的[意义点这里](https://github.com/easefun/polyv-android-sdk-2.0-demo/wiki/10.设置学员唯一标识的意义)__。

### 新增功能
* 新增接口PolyvVideoView.getRealPlayStatus()获取视频实时状态

### 功能完善&bug修复
* 优化统计日志逻辑
* 修复播放器与云课堂0.12.0及以下版本的冲突问题

### 必要修改
* 旧版本的下载存在队列错误的问题，无法向前兼容，请使用最新版本的下载队列，参考[diff](https://github.com/easefun/polyv-android-sdk-2.0-demo/commit/742eab45026fbe9c9e19c9d61ef2203949a5acce#diff-3ef6bab6a4d852e377d11f8d75b79fda)。
* 从[2.8.0](https://github.com/easefun/polyv-android-sdk-2.0-demo/releases/tag/v2.8.0)版本开始播放器的渲染控件由SurfaceView更改为TextureView，由于渲染控件间的层级问题，如果是使用demo中的弹幕功能，那么弹幕的view需要更改为DanmakuView才能正常使用。如果是点播sdk和直播sdk同时集成，那么直播sdk的弹幕渲染控件也需要改为DanmakuView。

### 修改依赖
__polyvPlayer包和polyvDownload包需要同时升级到最新版本。__
```Groovy
//compile 'com.easefun.polyv:polyvPlayer:2.12.1'//SDK核心包
implementation 'com.easefun.polyv:polyvPlayer:2.12.2'//SDK核心包
//compile 'com.easefun.polyv:polyvPlayerABI:1.9.2'//SDK核心包
implementation 'com.easefun.polyv:polyvPlayerABI:1.9.3'//SDK核心包
```

__jar和so都由maven仓库统一管理，通过依赖自动引入。__<br/>

API文档请看[v2.12.2 API](http://repo.polyv.net/android/sdk/2.12.2/api/index.html)

### 2020-05-15 polyv-android-sdk-2.0.0-demo v2.12.1
### 设置学员唯一标识
__请调用`PolyvSDKClient`的[setViewerId](
http://repo.polyv.net/android/sdk/2.12.1/api/com/easefun/polyvsdk/PolyvSDKClient.html#setViewerId-java.lang.String-)方法设置学员唯一标识。可以获得Polyv更好的技术支持服务，设置学员唯一标识的[意义点这里](https://github.com/easefun/polyv-android-sdk-2.0-demo/wiki/10.设置学员唯一标识的意义)__。

### 新增功能
* 新增设置预加载时长参数。[PolyvVideoView.setMaxCacheSize](http://repo.polyv.net/android/sdk/2.12.1/api/com/easefun/polyvsdk/video/PolyvVideoView.html#setMaxCacheSize-long-)，[PolyvVideoView.setMaxCacheDuration](http://repo.polyv.net/android/sdk/2.12.1/api/com/easefun/polyvsdk/video/PolyvVideoView.html#setMaxCacheDuration-int-)
* 新增设置播放器logo配置。参考demo中的`PolyvPlayerLogoView`的使用。
* 新增禁止进度条拖动配置。参考demo中的`PolyvPlayerMediaController`的`dragSeekStrategy`成员字段。

### 功能完善&bug修复
* 完善播放器播放失败的重试机制。
* 修复播放器销毁后监听器还回调的问题。

### 必要修改
* 旧版本的下载存在队列错误的问题，无法向前兼容，请使用最新版本的下载队列，参考[diff](https://github.com/easefun/polyv-android-sdk-2.0-demo/commit/742eab45026fbe9c9e19c9d61ef2203949a5acce#diff-3ef6bab6a4d852e377d11f8d75b79fda)。
* 从[2.8.0](https://github.com/easefun/polyv-android-sdk-2.0-demo/releases/tag/v2.8.0)版本开始播放器的渲染控件由SurfaceView更改为TextureView，由于渲染控件间的层级问题，如果是使用demo中的弹幕功能，那么弹幕的view需要更改为DanmakuView才能正常使用。如果是点播sdk和直播sdk同时集成，那么直播sdk的弹幕渲染控件也需要改为DanmakuView。

### 修改依赖
__polyvPlayer包和polyvDownload包需要同时升级到最新版本。__
```Groovy
//compile 'com.easefun.polyv:polyvPlayer:2.12.0'//SDK核心包
implementation 'com.easefun.polyv:polyvPlayer:2.12.1'//SDK核心包
//compile 'com.easefun.polyv:polyvPlayerABI:1.8.0'//SDK核心包
implementation 'com.easefun.polyv:polyvPlayerABI:1.9.2'//SDK核心包
```

__jar和so都由maven仓库统一管理，通过依赖自动引入。__<br/>

API文档请看[v2.12.1 API](http://repo.polyv.net/android/sdk/2.12.1/api/index.html)

### 2020-03-27 polyv-android-sdk-2.0.0-demo v2.12.0
### 设置学员唯一标识
__请调用`PolyvSDKClient`的[setViewerId](
http://repo.polyv.net/android/sdk/2.12.0/api/com/easefun/polyvsdk/PolyvSDKClient.html#setViewerId-java.lang.String-)方法设置学员唯一标识。可以获得Polyv更好的技术支持服务，设置学员唯一标识的[意义点这里](https://github.com/easefun/polyv-android-sdk-2.0-demo/wiki/10.设置学员唯一标识的意义)__。

### 新增功能
* 添加长按屏幕快进的功能。

### 功能完善&bug修复
* 优化httpdns的请求逻辑。
* 修复token过期，视频播放失败的问题。
* 修复子账号无法正常下载ppt的问题。
* 修复问答图片没有正常显示的问题。
* 添加设置全屏播放时视频方向的方法。
* 修改关闭弹幕时会隐藏发送弹幕按钮。

### 必要修改
* 旧版本的下载存在队列错误的问题，无法向前兼容，请使用最新版本的下载队列，参考[diff](https://github.com/easefun/polyv-android-sdk-2.0-demo/commit/742eab45026fbe9c9e19c9d61ef2203949a5acce#diff-3ef6bab6a4d852e377d11f8d75b79fda)。
* 从[2.8.0](https://github.com/easefun/polyv-android-sdk-2.0-demo/releases/tag/v2.8.0)版本开始播放器的渲染控件由SurfaceView更改为TextureView，由于渲染控件间的层级问题，如果是使用demo中的弹幕功能，那么弹幕的view需要更改为DanmakuView才能正常使用。如果是点播sdk和直播sdk同时集成，那么直播sdk的弹幕渲染控件也需要改为DanmakuView。

### 修改依赖
__polyvPlayer包和polyvDownload包需要同时升级。__
```Groovy
//compile 'com.easefun.polyv:polyvPlayer:2.11.1'//SDK核心包
implementation 'com.easefun.polyv:polyvPlayer:2.12.0'//SDK核心包
//compile 'com.easefun.polyv:polyvDownload:2.11.0'//SDK下载功能
implementation 'com.easefun.polyv:polyvDownload:2.12.0'//SDK下载功能
```

__jar和so都由maven仓库统一管理，通过依赖自动引入。__<br/>

API文档请看[v2.12.0 API](http://repo.polyv.net/android/sdk/2.12.0/api/index.html)

### 2020-01-19 polyv-android-sdk-2.0.0-demo v2.11.1
### 设置学员唯一标识
__请调用`PolyvSDKClient`的[setViewerId](
http://repo.polyv.net/android/sdk/2.11.1/api/com/easefun/polyvsdk/PolyvSDKClient.html#setViewerId-java.lang.String-)方法设置学员唯一标识。可以获得Polyv更好的技术支持服务，设置学员唯一标识的[意义点这里](https://github.com/easefun/polyv-android-sdk-2.0-demo/wiki/10.设置学员唯一标识的意义)__。

### bug修复
* 修复打点信息携带"-"字符引起的崩溃问题。

### 必要修改
* 旧版本的下载存在队列错误的问题，无法向前兼容，请使用最新版本的下载队列，参考[diff](https://github.com/easefun/polyv-android-sdk-2.0-demo/commit/742eab45026fbe9c9e19c9d61ef2203949a5acce#diff-3ef6bab6a4d852e377d11f8d75b79fda)。
* 从[2.8.0](https://github.com/easefun/polyv-android-sdk-2.0-demo/releases/tag/v2.8.0)版本开始播放器的渲染控件由SurfaceView更改为TextureView，由于渲染控件间的层级问题，如果是使用demo中的弹幕功能，那么弹幕的view需要更改为DanmakuView才能正常使用。如果是点播sdk和直播sdk同时集成，那么直播sdk的弹幕渲染控件也需要改为DanmakuView。

### 修改依赖
__polyvDownload包需升级到最新版本。__
```Groovy
//compile 'com.easefun.polyv:polyvPlayer:2.11.0'//SDK核心包
implementation 'com.easefun.polyv:polyvPlayer:2.11.1'//SDK核心包
```

__jar和so都由maven仓库统一管理，通过依赖自动引入。__<br/>

API文档请看[v2.11.1 API](http://repo.polyv.net/android/sdk/2.11.1/api/index.html)

### 2020-01-03 polyv-android-sdk-2.0.0-demo v2.11.0
### 设置学员唯一标识
__请调用`PolyvSDKClient`的[setViewerId](
http://repo.polyv.net/android/sdk/2.11.0/api/com/easefun/polyvsdk/PolyvSDKClient.html#setViewerId-java.lang.String-)方法设置学员唯一标识。可以获得Polyv更好的技术支持服务，设置学员唯一标识的[意义点这里](https://github.com/easefun/polyv-android-sdk-2.0-demo/wiki/10.设置学员唯一标识的意义)__。

### 新增接口
* 修复下载偶现队列错误的问题。新增接口[PolyvDownloader.setPolyvDownloadBeforeStartListener2](http://repo.polyv.net/android/sdk/2.11.0/api/com/easefun/polyvsdk/download/PolyvDownloaderListenerEvent.html#setPolyvDownloadBeforeStartListener2-com.easefun.polyvsdk.download.listener.IPolyvDownloaderBeforeStartListener2-)、[PolyvDownloader.setPolyvDownloadWaitingListener](http://repo.polyv.net/android/sdk/2.11.0/api/com/easefun/polyvsdk/download/PolyvDownloaderListenerEvent.html#setPolyvDownloadWaitingListener-com.easefun.polyvsdk.download.listener.IPolyvDownloaderWaitingListener-)。
* 新增获取观看视频内容时长的接口[PolyvVideoView.getVideoContentPlayedTime](http://repo.polyv.net/android/sdk/2.11.0/api/com/easefun/polyvsdk/video/PolyvVideoView.html#getVideoContentPlayedTime--)。

### 新增功能
* 新增Android8.0画中画功能。

### 功能完善&bug修复
* 进入后台播放开启foreground service。
* 修复setViewLogParam2无效的问题。
* 优化下载，避免包冲突导致解压卡住的问题。
* 完善日志系统，添加flow、seek日志。
* 修复部分操作会导致carsh的bug。

### 必要修改
* 旧版本的下载存在队列错误的问题，无法向前兼容，请使用最新版本的下载队列，参考[diff](https://github.com/easefun/polyv-android-sdk-2.0-demo/commit/742eab45026fbe9c9e19c9d61ef2203949a5acce#diff-3ef6bab6a4d852e377d11f8d75b79fda)。
* 从[2.8.0](https://github.com/easefun/polyv-android-sdk-2.0-demo/releases/tag/v2.8.0)版本开始播放器的渲染控件由SurfaceView更改为TextureView，由于渲染控件间的层级问题，如果是使用demo中的弹幕功能，那么弹幕的view需要更改为DanmakuView才能正常使用。如果是点播sdk和直播sdk同时集成，那么直播sdk的弹幕渲染控件也需要改为DanmakuView。

### 修改依赖
__polyvPlayer包和polyvDownload包需要同时升级。__
```Groovy
//compile 'com.easefun.polyv:polyvPlayer:2.10.0'//SDK核心包
implementation 'com.easefun.polyv:polyvPlayer:2.11.0'//SDK核心包
//compile 'com.easefun.polyv:polyvDownload:2.9.0'//SDK下载功能
implementation 'com.easefun.polyv:polyvDownload:2.11.0'//SDK下载功能
```

__jar和so都由maven仓库统一管理，通过依赖自动引入。__<br/>

API文档请看[v2.11.0 API](http://repo.polyv.net/android/sdk/2.11.0/api/index.html)

### 2019-9-19 polyv-android-sdk-2.0.0-demo v2.10.0
### 设置学员唯一标识
__请调用`PolyvSDKClient`的[setViewerId](
http://repo.polyv.net/android/sdk/2.10.0/api/com/easefun/polyvsdk/PolyvSDKClient.html#setViewerId-java.lang.String-)方法设置学员唯一标识。可以获得Polyv更好的技术支持服务，设置学员唯一标识的[意义点这里](https://github.com/easefun/polyv-android-sdk-2.0-demo/wiki/10.设置学员唯一标识的意义)__。

### 新增接口
* 新增设置自动播放接口。使用[PolyvVideoView.setAutoPlay(boolean isAutoPlay)](http://repo.polyv.net/android/sdk/2.10.0/api/com/easefun/polyvsdk/video/PolyvVideoView.html#setAutoPlay-boolean-)方法。
* 新增获取当前播放进度回调。使用`PolyvVideoView`的[setOnGetCurrentPositionListener(IPolyvOnGetCurrentPositionListener l)](http://repo.polyv.net/android/sdk/2.10.0/api/com/easefun/polyvsdk/video/PolyvVideoView.html#setOnGetCurrentPositionListener-com.easefun.polyvsdk.video.listener.IPolyvOnGetCurrentPositionListener-)方法、[setOnGetCurrentPositionListener(long intervalMs, IPolyvOnGetCurrentPositionListener l)](http://repo.polyv.net/android/sdk/2.10.0/api/com/easefun/polyvsdk/video/PolyvVideoView.html#setOnGetCurrentPositionListener-long-com.easefun.polyvsdk.video.listener.IPolyvOnGetCurrentPositionListener-)方法设置监听回调。

### 新增功能
* 新增三分屏播放课件功能。
* 新增视频加载时支持显示视频流的加载网速。

### 功能完善&bug修复
* 升级demo项目Gradle版本。
* 修复某些视频seek到duration时间点无法播放的问题。
* 修复直播转存的视频，视频前面被截取后无法续播的问题。
* 跑马灯的闪烁样式支持渐变效果。
* 修复发送的弹幕样式没有生效的问题。
* 修复自定义问答没有发送答题统计的问题。
* 修复自定义问答没有清空，导致切换视频时，会显示上一个视频设置的自定义问答的问题。

### 必要修改
* 从[2.8.0](https://github.com/easefun/polyv-android-sdk-2.0-demo/releases/tag/v2.8.0)版本开始播放器的渲染控件由SurfaceView更改为TextureView，由于渲染控件间的层级问题，如果是使用demo中的弹幕功能，那么弹幕的view需要更改为DanmakuView才能正常使用。如果是点播sdk和直播sdk同时集成，那么直播sdk的弹幕渲染控件也需要改为DanmakuView。

### 修改依赖
__polyvPlayer包和polyvDownload包需要同时升级。__
```Groovy
//compile 'com.easefun.polyv:polyvPlayer:2.9.0'//SDK核心包
implementation 'com.easefun.polyv:polyvPlayer:2.10.0'//SDK核心包
//compile 'com.easefun.polyv:polyvDownload:2.8.2'//SDK下载功能
implementation 'com.easefun.polyv:polyvDownload:2.9.0'//SDK下载功能
//compile 'com.easefun.polyv:polyvSub:2.9.0'//弹幕、截图功能中使用
implementation 'com.easefun.polyv:polyvSub:2.10.0'//弹幕、截图功能中使用
```

__jar和so都由maven仓库统一管理，通过依赖自动引入。__<br/>

API文档请看[v2.10.0 API](http://repo.polyv.net/android/sdk/2.10.0/api/index.html)

### 2019-08-08 polyv-android-sdk-2.0.0-demo v2.9.0
### 设置学员唯一标识
__请调用`PolyvSDKClient`的[setViewerId](
http://repo.polyv.net/android/sdk/2.8.2/api/com/easefun/polyvsdk/PolyvSDKClient.html#setViewerId-java.lang.String-)方法设置学员唯一标识。可以获得Polyv更好的技术支持服务，设置学员唯一标识的[意义点这里](https://github.com/easefun/polyv-android-sdk-2.0-demo/wiki/10.设置学员唯一标识的意义)__。

### 新增接口
* 新增替换问答接口。[PolyvVideoView.changeQuestion(int showTime, ArrayList<PolyvQuestionVO> questionVOList)](http://repo.polyv.net/android/sdk/2.9.0/api/com/easefun/polyvsdk/video/PolyvVideoView.html#changeQuestion-int-java.util.ArrayList-)，见demo中`PolyvPlayerAnswerView`类示例。
*  新增设置SDK加密串方法。[PolyvVideoView.settingsWithConfigString(config, aeskey, iv)](http://repo.polyv.net/android/sdk/2.9.0/api/com/easefun/polyvsdk/PolyvSDKClient.html#settingsWithConfigString-java.lang.String-java.lang.String-java.lang.String-)，跟iOS对其接口名称。

### 新增功能
* 新增支持子账号的功能。使用[PolyvSDkClient.settingsWithAppId(appId, secretKey, userId)](http://repo.polyv.net/android/sdk/2.9.0/api/com/easefun/polyvsdk/PolyvSDKClient.html#settingsWithAppId-java.lang.String-java.lang.String-java.lang.String-)方法。
* 新增支持自定义片头功能。使用[PolyvVideoView.setCustomTeaser(url, int duration) ](http://repo.polyv.net/android/sdk/2.9.0/api/com/easefun/polyvsdk/video/PolyvVideoView.html#setCustomTeaser-java.lang.String-int-)方法。

### 功能完善&bug修复
* 自定义问答增加showTime功能，见demo中`PolyvCustomQuestionBuilder`类示例。
* 问答新增[PolyvPlayErrorReason.QUESTION_CLIENT_ERROR](http://repo.polyv.net/android/sdk/2.9.0/api/com/easefun/polyvsdk/video/PolyvPlayErrorReason.html#QUESTION_CLIENT_ERROR)、[PolyvPlayErrorReason.QUESTION_SERVER_ERROR](http://repo.polyv.net/android/sdk/2.9.0/api/com/easefun/polyvsdk/video/PolyvPlayErrorReason.html#QUESTION_SERVER_ERROR)错误类型。需要更新`PolyvErrorMessageUtils`类。
* 更新httpdns混淆配置。
* 全屏播放隐藏虚拟键，[diff](https://github.com/easefun/polyv-android-sdk-2.0-demo/commit/68b85d823b06039811bf9d5bdb26c31a68f45eef#diff-e0cb4db0cd21598968c2782fb2609afe)。
* 发送弹幕接口新增参数：弹幕Id。且对发送弹幕的参数进行了校验。见demo中`PolyvPlayerDanmuFragment`类`PolyvDanmakuManager.SendDanmakuListener`回调方法示例。

### 必要修改
* 从[2.8.0](https://github.com/easefun/polyv-android-sdk-2.0-demo/releases/tag/v2.8.0)版本开始播放器的渲染控件由SurfaceView更改为TextureView，由于渲染控件间的层级问题，如果是使用demo中的弹幕功能，那么弹幕的view需要更改为DanmakuView才能正常使用。如果是点播sdk和直播sdk同时集成，那么直播sdk的弹幕渲染控件也需要改为DanmakuView。
* 自定义问答功能这个版本进行了重构，不向前兼容，升级到当前SDK版本，需要升级自定义问答功能。见demo中`PolyvPlayerAnswerView`类示例。

### 修改依赖
```Groovy
//compile 'com.easefun.polyv:polyvPlayer:2.8.3'//SDK核心包
compile 'com.easefun.polyv:polyvPlayer:2.9.0'//SDK核心包
//compile 'com.easefun.polyv:polyvSub:2.8.0'//弹幕、截图功能中使用
compile 'com.easefun.polyv:polyvSub:2.9.0'//弹幕、截图功能中使用
```

__jar和so都由maven仓库统一管理，通过依赖自动引入。__<br/>

API文档请看[v2.9.0 API](http://repo.polyv.net/android/sdk/2.9.0/api/index.html)

### 2019-06-18 polyv-android-sdk-2.0.0-demo v2.8.3
### 设置学员唯一标识
__请调用`PolyvSDKClient`的[setViewerId](
http://repo.polyv.net/android/sdk/2.8.2/api/com/easefun/polyvsdk/PolyvSDKClient.html#setViewerId-java.lang.String-)方法设置学员唯一标识。可以获得Polyv更好的技术支持服务，设置学员唯一标识的[意义点这里](https://github.com/easefun/polyv-android-sdk-2.0-demo/wiki/10.设置学员唯一标识的意义)__。

### 功能完善&bug修复
* 修复播放器destroy方法和播放控制方法并发可能会出现异常的问题。

### 必要修改
* 从[2.8.0](https://github.com/easefun/polyv-android-sdk-2.0-demo/releases/tag/v2.8.0)版本开始播放器的渲染控件由SurfaceView更改为TextureView，由于渲染控件间的层级问题，如果是使用demo中的弹幕功能，那么弹幕的view需要更改为DanmakuView才能正常使用。如果是点播sdk和直播sdk同时集成，那么直播sdk的弹幕渲染控件也需要改为DanmakuView。

### 修改依赖
```Groovy
//compile 'com.easefun.polyv:polyvPlayer:2.8.2'//SDK核心包
compile 'com.easefun.polyv:polyvPlayer:2.8.3'//SDK核心包
```

__jar和so都由maven仓库统一管理，通过依赖自动引入。__<br/>

API文档请看[v2.8.3 API](http://repo.polyv.net/android/sdk/2.8.3/api/index.html)

### 2019-06-10 polyv-android-sdk-2.0.0-demo v2.8.2
### 设置学员唯一标识
__请调用`PolyvSDKClient`的[setViewerId](
http://repo.polyv.net/android/sdk/2.8.2/api/com/easefun/polyvsdk/PolyvSDKClient.html#setViewerId-java.lang.String-)方法设置学员唯一标识。可以获得Polyv更好的技术支持服务，设置学员唯一标识的[意义点这里](https://github.com/easefun/polyv-android-sdk-2.0-demo/wiki/10.设置学员唯一标识的意义)__。

### 新增接口
* 新增离线播放发送viewlog的开关。使用[PolyvVideoView.enableLocalViewLog(boolean enable)](http://repo.polyv.net/android/sdk/2.8.2/api/com/easefun/polyvsdk/video/PolyvVideoView.html#enableLocalViewLog-boolean-)方法。

### 功能完善&bug修复
* 修复弹幕暂停后，调用resume方法可能会无法恢复滚动的问题，参考[PolyvPlayerDanmuFragment](https://github.com/easefun/polyv-android-sdk-2.0-demo/commit/21dbda0e35be875fa51aa751d4bf6513733fdec4#diff-4bf2ab060ce3c54c56f94510993f2034)的改动。
* 修复退到后台后，弹幕没有暂停的问题，参考[PolyvPlayerActivity](https://github.com/easefun/polyv-android-sdk-2.0-demo/commit/21dbda0e35be875fa51aa751d4bf6513733fdec4#diff-4bf2ab060ce3c54c56f94510993f2034)的改动。

### 必要修改
* 从[2.8.0](https://github.com/easefun/polyv-android-sdk-2.0-demo/releases/tag/v2.8.0)版本开始播放器的渲染控件由SurfaceView更改为TextureView，由于渲染控件间的层级问题，如果是使用demo中的弹幕功能，那么弹幕的view需要更改为DanmakuView才能正常使用。如果是点播sdk和直播sdk同时集成，那么直播sdk的弹幕渲染控件也需要改为DanmakuView。

### 修改依赖
需要同时升级。
```Groovy
//compile 'com.easefun.polyv:polyvPlayer:2.8.1'//SDK核心包
compile 'com.easefun.polyv:polyvPlayer:2.8.2'//SDK核心包
//compile 'com.easefun.polyv:polyvDownload:2.8.1'//SDK下载功能
compile 'com.easefun.polyv:polyvDownload:2.8.2'//SDK下载功能
```

__jar和so都由maven仓库统一管理，通过依赖自动引入。__<br/>

API文档请看[v2.8.2 API](http://repo.polyv.net/android/sdk/2.8.2/api/index.html)

### 2019-05-21 polyv-android-sdk-2.0.0-demo v2.8.1
### 设置学员唯一标识
__请调用`PolyvSDKClient`的[setViewerId](
http://repo.polyv.net/android/sdk/2.8.1/api/com/easefun/polyvsdk/PolyvSDKClient.html#setViewerId-java.lang.String-)方法设置学员唯一标识。可以获得Polyv更好的技术支持服务，设置学员唯一标识的[意义点这里](https://github.com/easefun/polyv-android-sdk-2.0-demo/wiki/10.设置学员唯一标识的意义)__。

### 新增接口
* [PolyvVideoView](http://repo.polyv.net/android/sdk/2.8.1/api/com/easefun/polyvsdk/video/PolyvVideoView.html)新增一个[setVideoPath](http://repo.polyv.net/android/sdk/2.8.1/api/com/easefun/polyvsdk/video/PolyvVideoView.html#setVideoPath-java.lang.String-java.util.Map-)重载方法。视频地址播放支持设置header。

### 新增功能
* 视频地址播放新增自动续播功能。

### 功能完善&bug修复
* 视频播放过程中出现错误，增加自动重试逻辑，提升播放体验。
* 优化日志上报机制，提升SDK排查错误能力。
* 减少投屏功能的日志输出量。
* 修复播放器屏幕不会常亮的问题。之前在demo中设置屏幕常亮的方法可以去掉。


### 必要修改
* 从2.8.0版本开始播放器的渲染控件由SurfaceView更改为TextureView，由于渲染控件间的层级问题，如果是使用demo中的弹幕功能，那么弹幕的view需要更改为DanmakuView才能正常使用。如果是点播sdk和直播sdk同时集成，那么直播sdk的弹幕渲染控件也需要改为DanmakuView。

### 修改依赖
```Groovy
//compile 'com.easefun.polyv:polyvPlayer:2.8.0'//SDK核心包
compile 'com.easefun.polyv:polyvPlayer:2.8.1'//SDK核心包
//compile 'com.easefun.polyv:polyvDownload:2.8.0'//SDK下载功能
compile 'com.easefun.polyv:polyvDownload:2.8.1'//SDK下载功能
//compile 'com.easefun.polyv:polyvScreencast:0.2.0'//SDK投屏功能
compile 'com.easefun.polyv:polyvScreencast:0.2.1'//SDK投屏功能
```

__jar和so都由maven仓库统一管理，通过依赖自动引入。__<br/>

API文档请看[v2.8.1 API](http://repo.polyv.net/android/sdk/2.8.1/api/index.html)

### 2019-05-06 polyv-android-sdk-2.0.0-demo v2.8.0
### 设置学员唯一标识
__请调用`PolyvSDKClient`的[setViewerId](
http://repo.polyv.net/android/sdk/2.8.0/api/com/easefun/polyvsdk/PolyvSDKClient.html#setViewerId-java.lang.String-)方法设置学员唯一标识。可以获得Polyv更好的技术支持服务，设置学员唯一标识的[意义点这里](https://github.com/easefun/polyv-android-sdk-2.0-demo/wiki/10.设置学员唯一标识的意义)__。

### 新增接口
* [PolyvVideoView](http://repo.polyv.net/android/sdk/2.8.0/api/com/easefun/polyvsdk/video/PolyvVideoView.html)新增多个`screenshot`方法。
* [PolyvVideoView](http://repo.polyv.net/android/sdk/2.8.0/api/com/easefun/polyvsdk/video/PolyvVideoView.html)新增[setLoadTimeoutSecond](http://repo.polyv.net/android/sdk/2.8.0/api/com/easefun/polyvsdk/video/PolyvVideoView.html#setLoadTimeoutSecond-boolean-int-)，[setBufferTimeoutSecond](http://repo.polyv.net/android/sdk/2.8.0/api/com/easefun/polyvsdk/video/PolyvVideoView.html#setBufferTimeoutSecond-boolean-int-)方法。

### 新增功能
* 新增自定义问答的功能。参考`PolyvPlayerActivity`里`showCustomQuestion()`方法中的示例代码来使用。
* 新增多账户下载功能。`PolyvSDKClient`新增方法`openMultiDownloadAccount`用于打开多账户。demo中新增`PolyvUserClient`类，提供用户登录登出功能。
* 链接地址播放视频新增精准seek支持。

### 功能完善&bug修复
* 优化HTTPDNS相关逻辑。提升播放体验。
* 使用HTTPS播放视频。解决一些HTTP请求被劫持导致视频无法播放的问题。
* 优化播放视频出错逻辑。新增错误码并同步更新了demo中错误提示`PolyvErrorMessageUtils`内容。
* 优化片头数据获取逻辑。解决某些视频片头没有更新的问题。
* 优化播放器暂停状态从后台回到前台时画面会被清除的问题。
* 优化截图功能。无需再请求网络。
* 播放器默认关闭加载超时、缓冲超时的功能。可以使用[PolyvVideoView](http://repo.polyv.net/android/sdk/2.8.0/api/com/easefun/polyvsdk/video/PolyvVideoView.html)的[setLoadTimeoutSecond](http://repo.polyv.net/android/sdk/2.8.0/api/com/easefun/polyvsdk/video/PolyvVideoView.html#setLoadTimeoutSecond-boolean-int-)、[setBufferTimeoutSecond](http://repo.polyv.net/android/sdk/2.8.0/api/com/easefun/polyvsdk/video/PolyvVideoView.html#setBufferTimeoutSecond-boolean-int-)方法开启。
* 优化demo中下载和上传功能的数据库更新方式。
* 补充答题统计功能。

### 必要修改
* 该版本播放器的渲染控件由SurfaceView更改为TextureView，由于渲染控件间的层级问题，如果是使用demo中的弹幕功能，那么弹幕的view需要更改为DanmakuView才能正常使用。如果是点播sdk和直播sdk同时集成，那么直播sdk的弹幕渲染控件也需要改为DanmakuView。
* 该版本播放器内部不会主动调用保持屏幕常亮方法，可以在demo中使用view.setKeepScreenOn(true)方法来设置屏幕常亮。

### 修改依赖
```Groovy
//compile 'com.easefun.polyv:polyvPlayer:2.7.3'//SDK核心包
compile 'com.easefun.polyv:polyvPlayer:2.8.0'//SDK核心包
//compile 'com.easefun.polyv:polyvPlayerABI:1.7.1'//SDK核心包
compile 'com.easefun.polyv:polyvPlayerABI:1.8.0'//SDK核心包
//compile 'com.easefun.polyv:polyvDownload:2.7.3'//SDK下载功能
compile 'com.easefun.polyv:polyvDownload:2.8.0'//SDK下载功能
//compile 'com.easefun.polyv:polyvUpload:2.2.2'//SDK上传功能
compile 'com.easefun.polyv:polyvUpload:2.2.3'//SDK上传功能
//compile 'com.easefun.polyv:polyvSub:2.7.3'//弹幕、截图功能中使用
compile 'com.easefun.polyv:polyvSub:2.8.0'//弹幕、截图功能中使用
```

__jar和so都由maven仓库统一管理，通过依赖自动引入。__<br/>

API文档请看[v2.8.0 API](http://repo.polyv.net/android/sdk/2.8.0/api/index.html)

### 2019-03-28 polyv-android-sdk-2.0.0-demo v2.7.3
### 设置学员唯一标识
__请调用`PolyvSDKClient`的[setViewerId](
http://repo.polyv.net/android/sdk/2.7.3/api/com/easefun/polyvsdk/PolyvSDKClient.html#setViewerId-java.lang.String-)方法设置学员唯一标识。可以获得Polyv更好的技术支持服务，设置学员唯一标识的[意义点这里](https://github.com/easefun/polyv-android-sdk-2.0-demo/wiki/10.设置学员唯一标识的意义)__。

### 新增接口
* PolyvSDKClient新增[setViewerInfo](http://repo.polyv.net/android/sdk/2.7.3/api/com/easefun/polyvsdk/PolyvSDKClient.html#setViewerInfo-com.easefun.polyvsdk.po.PolyvViewerInfo-)方法。为支持各端接口统一。

### 新增音频下载相关接口
* `PolyvDownloaderManager` 新增 `getPolyvDownloader(vid, bitrate, fileType)`, `isWaitingDownload(vid, bitrate, fileType)`, `clearPolyvDownload(vid, bitrate, fileType)`, `getKey(vid, bitrate, fileType)` 方法。
* `PolyvDownloader` 新增 `FILE_VIDEO`, `FILE_AUDIO` 字段，新增 `delete` 方法。
* `PolyvDownloaderUtils` 新增 `deleteAudio`, `delete(vid, bitrate, fileType)` 方法。
* `PolyvVideoUtil` 新增 `validateMP3Audio` 方法。
* `PolyvVideoVO` 新增 `getFileSizeMatchFileType(bitrate, fileType)` 方法。
* `PolyvDownloaderErrorReason` 新增 `AUDIO_NOT_EXIST` 字段。

### 新增功能
* 添加双击手势控制播放器暂停及播放，参考 `PolyvPlayerActivity` 里 `videoView.setOnGestureDoubleClickListener` 的使用。
* 添加播放视频前的网络类型检测及提示，参考 `PolyvPlayerActivity` 里 `PolyvNetworkDetection` 的使用。
* 添加下载音频的功能，参考 `PolyvDownloadListViewAdapter` 里 `PolyvDownloaderManager.getPolyvDownloader(vid, bitrate, fileType)` 的使用。

### 功能完善&bug修复
* 优化了视频播放失败的错误交互逻辑。为了提升用户体验，见`PolyvPlayerPlayErrorView`、`PolyvPlayerPlayRouteView`、更新错误提示`PolyvErrorMessageUtils`内容。
* 修复同时批量下载视频时，偶尔会出现几个视频无法播放问题。
* 修复播放直播转存视频过程中断网，进度条会跳到最后的问题。
* 修复多账户功能中，非加密视频无法下载的问题。
* 添加设置滚动跑马灯再次出现的间隔，参考 `PolyvPlayerActivity` 的 `videoView.setMarqueeView` 里的 `setReappearTime` 方法。
* 调整统计观看时长的计算规则。
* 问答区分单选多选及相关优化，参考 `PolyvPlayerAnswerView` 的更改。
* 优化调整声音/亮度的手势触发位置。
* 弹幕库升级。解决部分客户集成云课堂sdk弹幕库版本冲突的问题。
* video数据库升级。解决部分客户sdk的升级问题。
* `PolyvErrorMessageUtils` 增加音频下载的错误提示类型。

### 修改依赖
```Groovy
//compile 'com.easefun.polyv:polyvPlayer:2.7.2'//SDK核心包
compile 'com.easefun.polyv:polyvPlayer:2.7.3'//SDK核心包
//compile 'com.easefun.polyv:polyvDownload:2.7.1'//SDK下载功能
compile 'com.easefun.polyv:polyvDownload:2.7.3'//SDK下载功能
//compile 'com.easefun.polyv:polyvSub:2.5.2'//弹幕、截图功能中使用
compile 'com.easefun.polyv:polyvSub:2.7.3'//弹幕、截图功能中使用
```

__jar和so都由maven仓库统一管理，通过依赖自动引入。__<br/>

API文档请看[v2.7.3 API](http://repo.polyv.net/android/sdk/2.7.3/api/index.html)

### 2019-03-12 polyv-android-sdk-2.0.0-demo v2.7.2
### 设置学员唯一标识
__请调用`PolyvSDKClient`的[setViewerId](
http://repo.polyv.net/android/sdk/2.7.2/api/com/easefun/polyvsdk/PolyvSDKClient.html#setViewerId-java.lang.String-)方法设置学员唯一标识。可以获得Polyv更好的技术支持服务，设置学员唯一标识的[意义点这里](https://github.com/easefun/polyv-android-sdk-2.0-demo/wiki/10.设置学员唯一标识的意义)__。

### 新增接口
* [PolyvDownloader](http://repo.polyv.net/android/sdk/2.7.2/api/com/easefun/polyvsdk/PolyvDownloader.html)新增[setPolyvDownloadProressListener2](http://repo.polyv.net/android/sdk/2.7.2/api/com/easefun/polyvsdk/download/IPolyvDownloaderListenerEvent.html#setPolyvDownloadProressListener2-com.easefun.polyvsdk.download.listener.IPolyvDownloaderProgressListener2-)监听回调接口。用于解决下载视频无法删除的问题。
* [PolyvDownloader](http://repo.polyv.net/android/sdk/2.7.2/api/com/easefun/polyvsdk/PolyvDownloader.html)新增[setPolyvDownloadUnzipListener](http://repo.polyv.net/android/sdk/2.7.2/api/com/easefun/polyvsdk/download/IPolyvDownloaderListenerEvent.html#setPolyvDownloadUnzipListener-com.easefun.polyvsdk.download.listener.IPolyvDownloaderUnzipListener-)监听回调接口。用于回调加密视频的解压进度。
* 添加验证本地视频有效性的方法，使用 [PolyvVideoUtil](
http://repo.polyv.net/android/sdk/2.7.2/api/com/easefun/polyvsdk/video/PolyvVideoUtil.html).[validateLocalVideo](
http://repo.polyv.net/android/sdk/2.7.2/api/com/easefun/polyvsdk/video/PolyvVideoUtil.html#validateLocalVideo-java.lang.String-)。

### 新增功能
* 添加多线路切换的功能，使用 `PolyvVideoView.changeRoute`。
* 添加精准seek的功能，使用 `PolyvVideoView.setSeekType`。
* 添加防录屏功能，使用 `PolyvVideoView.disableScreenCAP`。

### 功能完善&bug修复
* 优化播放失败重试逻辑。
* 优化下载进度回调。见[IPolyvDownloaderProgressListener](http://repo.polyv.net/android/sdk/2.7.2/api/com/easefun/polyvsdk/download/listener/IPolyvDownloaderProgressListener.html#onDownload-long-long-)、[IPolyvDownloaderProgressListener2](http://repo.polyv.net/android/sdk/2.7.2/api/com/easefun/polyvsdk/download/listener/IPolyvDownloaderProgressListener2.html)。
* 调整下载速度回调间隔时间默认为1秒。
* 修复开启自动续播时，视频播放完成后保存了播放进度，导致下次进入无法播放的问题。
* 优化SDK中数据库操作偶发异常的问题。
* 小窗播放时添加设置控件的显示状态。
* 修复退出播放界面时，视频可能还在播放的问题。
* 添加点击打点进度条可以触发seek。
* 优化在onPrepared回调中使用seek方法的逻辑。
* 修复android9.0投屏功能获取网络类型不正确的问题。
* 修复上传功能在选择文件之后可能会发生闪退的问题。
* video数据库升级，解决部分客户的升级问题。

### 增删配置
* 投屏相关：添加 &lt;uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" /&gt; 权限
* 投屏相关：移除sdk-lecast-release.aar，现在由polyvScreencast:0.2.0导入

### 修改依赖
```Groovy
//compile 'com.easefun.polyv:polyvPlayer:2.7.0'//SDK核心包
compile 'com.easefun.polyv:polyvPlayer:2.7.2'//SDK核心包
//compile 'com.easefun.polyv:polyvPlayerABI:1.7.0'//SDK核心包
compile 'com.easefun.polyv:polyvPlayerABI:1.7.1'//SDK核心包
//compile 'com.easefun.polyv:polyvDownload:2.6.0'//SDK下载功能
compile 'com.easefun.polyv:polyvDownload:2.7.1'//SDK下载功能
//compile 'com.easefun.polyv:polyvScreencast:0.1.0'//SDK投屏功能
compile 'com.easefun.polyv:polyvScreencast:0.2.0'//SDK投屏功能
//compile 'com.easefun.polyv:polyvUpload:2.2.1'//SDK上传功能
compile 'com.easefun.polyv:polyvUpload:2.2.2'//SDK上传功能
```

__jar和so都由maven仓库统一管理，通过依赖自动引入。__<br/>

API文档请看[v2.7.2 API](http://repo.polyv.net/android/sdk/2.7.2/api/index.html)

### 2019-01-08 polyv-android-sdk-2.0.0-demo v2.7.0
### 设置学员唯一标识
__请调用`PolyvSDKClient`的`setViewerId`方法设置学员唯一标识。可以获得Polyv更好的技术支持服务，设置学员唯一标识的[意义点这里](https://github.com/easefun/polyv-android-sdk-2.0-demo/wiki/10.设置学员唯一标识的意义)__。

### 新增功能
* 添加视频横屏锁屏功能
* SDK增加投屏功能

### 功能完善&bug修复
* 优化SDK 切换倍速再更换清晰度后会还原一倍速度的问题
* 点播视频答题选项卡样式优化
* 优化下载视频逻辑
* 完善mp4视频播放异常时的处理逻辑
* mp3源文件播放时添加显示首图
* 修复当视频仅有片尾广告时播放异常的情况
* 添加可以控制进入后台时是否能继续播放
* 添加在播放器的暂停回调里暂停弹幕

### 新增配置
* 投屏相关：build.gradle里的投屏依赖库，libs里的投屏所需核心库，proguard-rules里的投屏混淆配置，PolyvApplication里的投屏初始化配置。

### 修改依赖
```Groovy
//compile 'com.easefun.polyv:polyvPlayer:2.6.1'//SDK核心包
compile 'com.easefun.polyv:polyvPlayer:2.7.0'//SDK核心包
//compile 'com.easefun.polyv:polyvPlayerABI:1.6.1'//SDK核心包
compile 'com.easefun.polyv:polyvPlayerABI:1.7.0'//SDK核心包
//compile 'com.easefun.polyv:polyvDownload:2.5.0'//SDK下载功能
compile 'com.easefun.polyv:polyvDownload:2.6.0'//SDK下载功能
compile 'com.easefun.polyv:polyvScreencast:0.1.0'//SDK投屏功能
```

### 新增混淆
```
###jmdns
-keep class javax.jmdns.** { *; }
-dontwarn javax.jmdns.**

###CyberGarage-upnp
-keep class org.cybergarage.** { *; }
-dontwarn org.cybergarage.**

###plist
-keep class com.dd.plist.** { *; }
-dontwarn com.dd.plist.**

###Lebo
-keep class com.hpplay.**{*;}
-keep class com.hpplay.**$*{*;}
-dontwarn com.hpplay.**
```

__jar和so都由maven仓库统一管理，通过依赖自动引入。__<br/>

API文档请看[v2.7.0 API](http://repo.polyv.net/android/sdk/2.7.0/api/index.html)

### 2018-12-11 polyv-android-sdk-2.0.0-demo v2.6.1
### 功能完善&bug修复
* 修复播放器释放后可能会引发崩溃问题
* 完善播放器加载超时的相关逻辑

### 修改依赖
```Groovy
//compile 'com.easefun.polyv:polyvPlayer:2.6.0'//SDK核心包
compile 'com.easefun.polyv:polyvPlayer:2.6.1'//SDK核心包
```

__jar和so都由maven仓库统一管理，通过依赖自动引入。__<br/>

API文档请看[v2.6.1 API](http://repo.polyv.net/android/sdk/2.6.1/api/index.html)

### 2018-11-28 polyv-android-sdk-2.0.0-demo v2.6.0
### 新增功能
* 添加支持顶部字幕的功能
* 添加设置播放超时及缓冲超时的功能

### 新增接口
* `PolyvVodPlayerUtil`中增加获取视频观看进度`lastPositionWithVid`，获取视频观看进度保存时间戳`lastPositionTimestampWithVid`方法

### 功能完善&bug修复
* 优化播放/下载视频偶尔会出现20016报错的问题
* 优化手势滑动音量调节跨度太大的问题
* 修复播放视频偶尔发生崩溃的问题
* 优化切换视频黑色背景覆盖不完全的问题
* 修复字幕的初始化选择及顺序不正确的问题
* 动态更新httpdns刷新时间
* 修改数据库降级会崩溃的问题
* 播放过程中切换相同码率，调整为不做处理，也不会回调`PolyvPlayErrorReason.CHANGE_EQUAL_BITRATE`（30007）异常
* 播放过程中因为弱网或者断网，调整为不回调`PolyvPlayErrorReason.EXCEPTION_COMPLETION`（30017）异常，而是播放器停留在当前画面
* 统一播放器错误。见`PolyvPlayerActivity`中`videoView.setOnErrorListener`设置部分，有详细注释

### 修改依赖
```Groovy
//compile 'com.easefun.polyv:polyvPlayer:2.5.2'//SDK核心包
compile 'com.easefun.polyv:polyvPlayer:2.6.0'//SDK核心包
//compile 'com.easefun.polyv:polyvPlayerABI:1.6.0'//SDK核心包
compile 'com.easefun.polyv:polyvPlayerABI:1.6.1'//SDK核心包
```

### 其他
* 由于现在使用依赖的方式可以下载并关联源码包，因此把之前的源码包移除了

__jar和so都由maven仓库统一管理，通过依赖自动引入。__
API文档请看[v2.6.0 API](http://repo.polyv.net/android/sdk/2.6.0/api/index.html)

### 2018-10-16 polyv-android-sdk-2.0.0-demo v2.5.2
### 新增功能
* 添加视频打点功能

### 功能完善&bug修复
* 修复网络截图的默认保存路径无法写入的问题
* 修复问答答错后回退到0秒不生效的问题
* 修复日志记录中偶发性文件夹为空的异常问题
* 修复播放器在不设置播放器控制拦情况下，关闭手势功能，出现的播放器控制拦空对象异常问题
* 完善`PolyvVideoView`中`setViewerId`,`setViewLogParam2`方法的注释，使其更明确的知道与后台的观众昵称，自定义ID的对应关系
* 更新获取及发送弹幕接口的请求地址

### 修改依赖
```Groovy
//compile 'com.easefun.polyv:polyvPlayer:2.5.1'//SDK核心包
compile 'com.easefun.polyv:polyvPlayer:2.5.2'//SDK核心包
//compile 'com.easefun.polyv:polyvSub:2.3.1'//弹幕、截图功能中使用
compile 'com.easefun.polyv:polyvSub:2.5.2'
```

### 升级源码包
* polyvPlayerSources2.5.1.jar -> polyvPlayerSources2.5.2.jar
* polyvSubSources2.5.1.jar -> polyvSubSources2.5.2.jar

__jar和so都由maven仓库统一管理，通过依赖自动引入。__
API文档请看[v2.5.2 API](http://repo.polyv.net/android/sdk/2.5.2/api/index.html)

### 2018-8-23 polyv-android-sdk-2.0.0-demo v2.5.1
### 功能完善&bug修复
* 修复错误日志异常输出的问题
* 内部网络请求优化，去掉跟踪日志

### 修改依赖
```Groovy
//compile 'com.easefun.polyv:polyvPlayer:2.5.0'//SDK核心包
compile 'com.easefun.polyv:polyvPlayer:2.5.1'//SDK核心包
```

### 升级源码包
* polyvPlayerSources2.5.0.jar -> polyvPlayerSources2.5.1.jar

__jar和so都由maven仓库统一管理，通过依赖自动引入。__
API文档请看[v2.5.1 API](http://repo.polyv.net/android/sdk/2.5.1/api/index.html)

### 2018-8-13 polyv-android-sdk-2.0.0-demo v2.5.0
### 新增功能
* demo中添加下载队列功能。

### 功能完善&bug修复
* 修复音频播放异常时，播放进度没有保存的问题。
* 修复demo中音频播放异常时，重新播放不显示封面图的问题。
* 修改demo中分享的文本内容。
* 修正demo中错误提示的错别字。见`PolyvErrorMessageUtils`。

### 新增接口/字段
* 下载增加回调视频信息的接口。`PolyvDownloader`中新增`setPolyvDownloadVideoInfoListener(IPolyvDownloaderVideoInfoListener l)`监听方法。

### 修改依赖
```Groovy
//compile 'com.easefun.polyv:polyvPlayer:2.4.0'//SDK核心包
compile 'com.easefun.polyv:polyvPlayer:2.5.0'//SDK核心包
//compile 'com.easefun.polyv:polyvDownload:2.4.0'//SDK下载功能 
compile 'com.easefun.polyv:polyvDownload:2.5.0'//SDK下载功能
```

### 新增依赖
```Groovy
compile "com.daimajia.swipelayout:library:1.2.0@aar"//demo中下载列表使用
```

### 升级源码包
* polyvPlayerSources2.4.0.jar -> polyvPlayerSources2.5.0.jar
* polyvDownloadSources2.4.0.jar -> polyvDownloadSources2.5.0.jar

__jar和so都由maven仓库统一管理，通过依赖自动引入。__
API文档请看[v2.5.0 API](http://repo.polyv.net/android/sdk/2.5.0/api/index.html)

### 2018-7-24 polyv-android-sdk-2.0.0-demo v2.4.0
### 新增功能
* 添加音视频切换功能

### 新增接口/字段
* 视频缓存机制重构，在`PolyvApplication`中的`setDownloadDir()`方法。
*       修复插入SD卡后，内部（不可移除）存储中的视频无法播放的问题
*       采用新接口获取存储路径。在`PolyvStorageUtils`类中。
* `PolyvSDKClient`中新增`getSubDirList()`，`setSubDirList(ArrayList<File>)`接口。
* `PolyvVideoView` 新增 `setVidWithStudentId` 接口

### 功能完善&bug修复
* 优化播放失败重试逻辑。
* 优化localDNS无法解析域名导致播放视频失败的问题。
* 优化视频下载资源解压逻辑。
* 优化播放器销毁时的清除逻辑。
* 视频缓存机制重构。
* 视频获取总时长优化。
* 优化问答界面。
* 修复转存拼接视频不能正常播放完成的问题。
* 修复播放某些视频开头会有马赛克的问题。
* 修复视频开始播放时触发横屏显示，点击屏幕后显示所有操作按钮的界面。见`polyv_controller_media_center_set.xml`。
* 优化错误提示方式。见`PolyvPlayerActivity`中的`showErrorView()`方法。

### 修改依赖
```Groovy
//compile 'com.easefun.polyv:polyvPlayer:2.3.3'//SDK核心包
compile 'com.easefun.polyv:polyvPlayer:2.4.0'//SDK核心包
//compile 'com.easefun.polyv:polyvPlayerABI:1.4.0'//SDK核心包 
compile 'com.easefun.polyv:polyvPlayerABI:1.6.0'//SDK核心包
//compile 'com.easefun.polyv:polyvDownload:2.3.3'//SDK下载功能 
compile 'com.easefun.polyv:polyvDownload:2.4.0'//SDK下载功能
```
### 升级源码包
* polyvPlayerSources2.3.3.jar -> polyvPlayerSources2.4.0.jar
* polyvDownloadSources2.3.3.jar -> polyvDownloadSources2.4.0.jar

__jar和so都由maven仓库统一管理，通过依赖自动引入。__
API文档请看[v2.4.0 API](http://repo.polyv.net/android/sdk/2.4.0/api/index.html)

### 2018-6-7 polyv-android-sdk-2.0.0-demo v2.3.3
### 新增功能
* 添加音视频切换功能

### 新增接口/字段
* `PolyvVideoView` 新增 `setPriorityMode`，`changeMode`，`getCurrentMode`，`setOnChangeModeListener` 接口
* `PolyvPlayErrorReason` 新增 `AUDIO_URL_EMPTY`，`NOT_LOCAL_AUDIO`，`CAN_NOT_CHANGE_AUDIO`，`CAN_NOT_CHANGE_VIDEO`，`LOCAL_AUDIO_ERROR` 字段

### 功能完善&bug修复
* 优化下载普通视频的进度回调
* 优化播放视频断网时的处理

### 已知未修复bug
* 转存拼接视频无法正常播放完成。

### 修改依赖
```Groovy
//compile 'com.easefun.polyv:polyvPlayer:2.3.2'//SDK核心包
compile 'com.easefun.polyv:polyvPlayer:2.3.3'//SDK核心包
//compile 'com.easefun.polyv:polyvDownload:2.3.1'//SDK下载功能 
compile 'com.easefun.polyv:polyvDownload:2.3.3'//SDK下载功能 
compile 'de.hdodenhof:circleimageview:2.2.0'//圆形imageview，音频封面图使用，新增
```

### 升级源码包
* polyvPlayerSources2.3.2.jar -> polyvPlayerSources2.3.3.jar
* polyvDownloadSources2.3.1.jar -> polyvDownloadSources2.3.3.jar

__jar和so都由maven仓库统一管理，通过依赖自动引入。__
API文档请看[v2.3.3 API](http://repo.polyv.net/android/sdk/2.3.3/api/index.html)

### 2018-5-24 polyv-android-sdk-2.0.0-demo v2.3.2
### 功能完善&bug修复
* 修复下载的视频播放出错的问题。

### 已知未修复bug
* 转存拼接视频无法正常播放完成。

### 修改依赖
```Groovy
//compile 'com.easefun.polyv:polyvPlayer:2.3.1'//SDK核心包
compile 'com.easefun.polyv:polyvPlayer:2.3.2'//SDK核心包
```

### 升级源码包
* polyvPlayerSources2.3.1.jar -> polyvPlayerSources2.3.2.jar

__jar和so都由maven仓库统一管理，通过依赖自动引入。__
API文档请看[v2.3.2 API](http://repo.polyv.net/android/sdk/2.3.2/api/index.html)

### 2018-5-18 polyv-android-sdk-2.0.0-demo v2.3.1
### 新增功能
* 下载增加一些SD卡的错误判断和错误类型。见`PolyvErrorMessageUtils`。

### 功能完善&bug修复
* 修复频繁切换视频会闪退的问题。
* 修复某些情况下视频无法播放的问题。
* 针对小米手机setKeepScreenOn报错，增加了catch异常。
* 优化无法创建文件夹和文件的问题。接口调用有改变请看`PolyvApplication`的`PolyvDevMountInfo.getInstance().init(...)`。
* 优化视频播放的逻辑。
* 优化下载逻辑。
* 优化数据库cursor关闭逻辑。
* 优化视频异常结束的报错逻辑。
* 修复demo中预览图界面可能会闪退的问题。请看`PolyvPlayerPreviewView`。
* 修复加载含有特殊字符的弹幕会崩溃的问题。

### 修改依赖
```Groovy
//compile 'com.easefun.polyv:polyvPlayer:2.2.2'//SDK核心包
compile 'com.easefun.polyv:polyvPlayer:2.3.1'//SDK核心包
//compile 'com.easefun.polyv:polyvPlayerABI:1.3.0'//SDK核心包 
compile 'com.easefun.polyv:polyvPlayerABI:1.4.0'//SDK核心包 
//compile 'com.easefun.polyv:polyvDownload:2.2.1'//SDK下载功能 
compile 'com.easefun.polyv:polyvDownload:2.3.1'//SDK下载功能 
//compile 'com.easefun.polyv:polyvSub:2.2.1'//弹幕、截图功能中使用
compile 'com.easefun.polyv:polyvSub:2.3.1'//弹幕、截图功能中使用
```

### 升级源码包
* polyvPlayerSources2.2.2.jar -> polyvPlayerSources2.3.1.jar
* polyvDownloadSources2.2.1.jar -> polyvDownloadSources2.3.1.jar
* polyvSubSources2.2.1.jar -> polyvSubSources2.3.1.jar

__jar和so都由maven仓库统一管理，通过依赖自动引入。__
API文档请看[v2.3.1 API](http://repo.polyv.net/android/sdk/2.3.1/api/index.html)

### 2018-1-24 polyv-android-sdk-2.0.0-demo v2.2.2
### 功能完善&bug修复
* 增加播放参数。

### 已知未修复bug
* 转存拼接视频无法正常播放完成。

### 修改依赖
```Groovy
//compile 'com.easefun.polyv:polyvPlayer:2.2.1'//SDK核心包
compile 'com.easefun.polyv:polyvPlayer:2.2.2'//SDK核心包
```

### 升级源码包
* polyvPlayerSources2.2.1.jar -> polyvPlayerSources2.2.2.jar

__jar和so都由maven仓库统一管理，通过依赖自动引入。__
API文档请看[v2.2.2 API](http://repo.polyv.net/android/sdk/2.2.2/api/index.html)

### 2018-1-16 polyv-android-sdk-2.0.0-demo v2.2.1
### 新增功能
* SDK包上传到maven仓库。
* 增加跑马灯功能。

### 新增接口
* `PolyvVideoView`增加多个`setVidByStrategy`多态接口，支持可以只去播放网络视频。
* `PolyvUploader`增加一个`start`多态接口，支持验证用户剩余空间是否可用。
* `PolyvUploaderManager`增加一个`getPolyvUploader`多态接口，支持上传视频到指定的分类目录下面。

### 功能完善&bug修复
* 修复文件大小显示不正确的问题。
* 修复demo中部分视频播放结束后，向右滑动屏幕快进，视频会重新播放且进度条进度显示不正确的问题。参考`PolyvPlayerActivity`播放器的手势监听及`PolyvPlayerMediaController`的进度条改变监听。
* 修复demo中账号后台内存空间满了，上传没有提示的问题。使用`PolyvUploader.start(sign,ptime)`方法。
* 修复demo中播放下一个视频时，进度条的缓存进度没有立刻刷新。
* demo中在线列表断网下载增加提示。参考`PolyvOnlineListViewAdapter`下载按钮的点击监听。
* 添加切换视频清晰度成功时再改变选择的码率控件的逻辑。参考`PolyvPlayerMediaController`的`resetBitRateView`方法。

### 已知未修复bug
* 转存拼接视频无法正常播放完成。

### 新增maven仓库
```Groovy
allprojects {
    repositories {
        jcenter()
        maven {url 'http://maven.aliyun.com/nexus/content/repositories/releases/'}
        maven {url 'https://dl.bintray.com/polyv/android'}
    }
}
```

### 新增依赖
```Groovy
compile 'com.easefun.polyv:polyvPlayer:2.2.1'//SDK核心包
compile 'com.easefun.polyv:polyvPlayerABI:1.3.0'//SDK核心包
compile 'com.easefun.polyv:polyvDownload:2.2.1'//SDK下载功能
compile 'com.easefun.polyv:polyvUpload:2.2.1'//SDK上传功能
compile 'com.nostra13.universalimageloader:universal-image-loader:1.9.5'//demo中图片使用
compile 'com.easefun.polyv:polyvGif:2.2.1'//demo中课程讨论区显示的内容里用到的包。
compile 'com.easefun.polyv:polyvSub:2.2.1'//弹幕、截图功能中使用
```

### 移除jar包
* alicloud-android-sdk-httpdns-1.1.0.jar
* alicloud-android-utils-1.0.2.jar
* bugly_crash_release.jar
* converter-gson-2.1.0.jar
* danmaku-flame-master-0.6.2.jar
* gson-2.7.jar
* ijkplayer-exo-0.8.0.1.jar
* ijkplayer-java-0.8.0.1.jar
* okhttp-3.3.0.jar
* okio-1.8.0.jar
* org.apache.http.legacy.jar
* polyvDownload2.x.x.jar
* polyvPlayer2.x.x.jar
* polyvSub2.x.x.jar
* polyvUpload2.x.x.jar
* polyvWidget0.5.1.x.jar
* retrofit-2.1.0.jar 
* universal-image-loader-1.9.5.jar
* utdid4all-1.1.5.3_proguard.jar

### 移除so库
* libijkffmpeg.so
* libijkplayer.so
* libijksdl.so
* libpolyvModule.so

### 升级源码包
* polyvDownloadSources2.0.6.jar -> polyvDownloadSources2.2.1.jar
* polyvPlayerSources2.0.6.jar -> polyvPlayerSources2.2.1.jar
* polyvUploadSources2.0.6.jar -> polyvUploadSources2.2.1.jar

__jar和so都由maven仓库统一管理，通过依赖自动引入。__
API文档请看[v2.2.1 API](http://repo.polyv.net/android/sdk/2.2.1/api/index.html)

### 2017-10-24 polyv-android-sdk-2.0.0-demo v2.0.6
### 新增功能
* 视频广告按照分类生效。

### 新增接口
* `PolyvVideoView`增加`setViewerName`设置观众名称接口。
* `PolyvVideoView`增加`setViewerParam`设置观众额外参数接口。

### 功能完善&bug修复
* 修复在视频播放界面切换视频多次后闪退的问题。
* 修复播放某些视频或者在播放视频中拖动进度会提示视频异常结束错误的问题。
* 完善播放和下载错误提示文本。增加对用户自行解决问题有帮助的文本。见`PolyvErrorMessageUtils`。

### 已知未修复bug
* 转存拼接视频无法正常播放完成。

### 升级jar包
* polyvPlayer2.0.5.jar -> polyvPlayer2.0.6.jar
* polyvSub2.0.5.jar -> polyvSub2.0.6.jar
* polyvDownload2.0.5.jar -> polyvDownload2.0.6.jar
* polyvUpload2.0.5.jar -> polyvUpload2.0.6.jar
* polyvWidget0.5.1.6.jar -> polyvWidget0.5.1.7.jar

### 移除jar包
* relinker-1.2.2.jar

### 升级so库
* libijkffmpeg.so

### 升级源码包
* polyvDownloadSources2.0.5.jar -> polyvDownloadSources2.0.6.jar
* polyvPlayerSources2.0.5.jar -> polyvPlayerSources2.0.6.jar
* polyvUploadSources2.0.5.jar -> polyvUploadSources2.0.6.jar

API文档请看[v2.0.6 API](http://demo.polyv.net/polyv/android/sdk/2.0.6/api/index.html)

### 2017-9-6 polyv-android-sdk-2.0.0-demo v2.0.5
### 新增功能
* 播放增加源文件播放支持。
* 下载增加队列功能。
* 下载增加源文件下载支持。
* 视频异常结束增加保存播放进度。
* demo中在线视频列表添加上拉可以加载更多数据。

### 修改功能
* 修改播放错误提示内容，并且代码移动到`PolyvErrorMessageUtils`。
* 修改下载错误提示内容，并且代码移动到`PolyvErrorMessageUtils`。
* demo中修改从课程列表下载视频时的操作逻辑。

### 新增接口
* 下载增加获取下载目录下载视频列表`getDownloadDirVideoList`，删除下载目录`deleteDownloaderDir`的工具方法，方法在`PolyvDownloaderUtils`中。
* `PolyvDownloaderManager`增加`setDownloadQueueCount`设置下载队列总数的接口。`PolyvApplication`类中有调用该接口。

### 功能完善&bug修复
* 修复seekTo到视频最后几秒会出现视频异常结束的问题。
* 修复在`PolyvVideoView`的`setOnPreparedListener`回调方法中调用`PolyvVideoView`的`seekTo`方法没有起作用的问题。
* 从设置系统亮度更改为设置当前窗口的亮度，并移除`android.permission.WRITE_SETTINGS`权限。
* 修复播放视频广告或者片头时切换其他视频，release并发引起崩溃的问题。
* 修复切换视频的时候，出现只有部分区域的图像被清除的问题。
* 完善权限提示功能。
* 修复上传功能在release apk中不能正常使用的问题。
* 修复demo中在线视频列表界面由于下载对话框未弹出时退出界面会发生崩溃的问题。

### 升级jar包
* polyvPlayer2.0.4.jar -> polyvPlayer2.0.5.jar
* polyvSub2.0.4.jar -> polyvSub2.0.5.jar
* polyvDownload2.0.4.jar -> polyvDownload2.0.5.jar
* polyvUpload2.0.4.jar -> polyvUpload2.0.5.jar
* polyvWidget0.5.1.5.jar -> polyvWidget0.5.1.6.jar

### 升级so库
* libijkffmpeg.so

### 升级源码包
* polyvDownloadSources2.0.4.jar -> polyvDownloadSources2.0.5.jar
* polyvPlayerSources2.0.4.jar -> polyvPlayerSources2.0.5.jar
* polyvUploadSources2.0.4.jar -> polyvUploadSources2.0.5.jar

API文档请看[v2.0.5 API](http://demo.polyv.net/polyv/android/sdk/2.0.5/api/index.html)

### 2017-8-15 polyv-android-sdk-2.0.0-demo v2.0.4
### 功能完善&bug修复
* 修复某些视频无法播放的问题。
* 修复打开视频播放，视频还在加载 loading 切换到其他界面（切换到后台），视频加载完成后在后台播放的问题。

### 升级jar包
* polyvPlayer2.0.3.jar -> polyvPlayer2.0.4.jar
* polyvSub2.0.3.jar -> polyvSub2.0.4.jar
* polyvDownload2.0.3.jar -> polyvDownload2.0.4.jar
* polyvUpload2.0.3.jar -> polyvUpload2.0.4.jar

### 升级so库
* libijkffmpeg.so

### 升级源码包
* polyvDownloadSources2.0.3.jar -> polyvDownloadSources2.0.4.jar
* polyvPlayerSources2.0.3.jar -> polyvPlayerSources2.0.4.jar
* polyvUploadSources2.0.3.jar -> polyvUploadSources2.0.4.jar

API文档请看[v2.0.4 API](http://demo.polyv.net/polyv/android/sdk/2.0.4/api/index.html)

### 2017-7-19 polyv-android-sdk-2.0.0-demo v2.0.3
### 修改功能
* 播放界面的错误提示从Toast改为AlertDialog，并且增加了默认提示。在`PolyvPlayerActivity`中。
* 下载界面的错误提示从Toast改为AlertDialog，并且增加了默认提示。在`PolyvDownloadListViewAdapter`中。

### 功能完善&bug修复
* 修复视频信息加载失败的问题。
* 修复PolyvVideoView中setVideoPath和release并发崩溃的问题。
* 修复视频下载完成后，去掉SDK的存储权限，播放本地视频时，会提示“本地文件损坏”的问题。修改为在播放视频的时候如果是必须从本地播放，就判断是否有读写存储设备权限。
* 修复只有片头广告，没有片头的情况下，无法播放片尾广告的问题。
* 修复android6.0以上当第一次安装app后登录，没有授予读写存储设备权限，设置的路径，和第二次已经授予app读写存储设备权限后登录设置的路径，不一致的问题。在`PolyvApplication`中。
* 修复demo中进入下载列表和进入上传列表没有请求动态权限的问题。在`PolyvMainActivity`中。
* 修复demo中下载界面点击下载全部没有判断权限的问题。在`PolyvDownloadListViewAdapter`中。
* 修改demo中下载没有权限提示：检测到拒绝写入SD卡，请先为应用程序分配权限，再重新下载 -> 检测到拒绝写入存储设备，请先为应用程序分配权限，再重新下载。在`PolyvDownloadListViewAdapter`中。
* 修改demo中从下载列表进行播放，必须从本地播放视频。在`PolyvDownloadListViewAdapter`中。
* 视频播放增加两个错误类型。在`PolyvPlayerActivity`中。
* 视频下载增加两个错误类型。在`PolyvDownloadListViewAdapter`中。

### 升级jar包
* polyvPlayer2.0.2.jar -> polyvPlayer2.0.3.jar
* polyvSub2.0.2.jar -> polyvSub2.0.3.jar
* polyvDownload2.0.2.jar -> polyvDownload2.0.3.jar
* polyvUpload2.0.2.jar -> polyvUpload2.0.3.jar

### 升级源码包
* polyvDownloadSources2.0.2.jar -> polyvDownloadSources2.0.3.jar
* polyvPlayerSources2.0.2.jar -> polyvPlayerSources2.0.3.jar
* polyvUploadSources2.0.2.jar -> polyvUploadSources2.0.3.jar

API文档请看[v2.0.3 API](http://demo.polyv.net/polyv/android/sdk/2.0.3/api/index.html)

### 2017-6-22 polyv-android-sdk-2.0.0-demo v2.0.2
### 新增功能
* 下载增加vid判断，并增加一个错误类型。

### 功能完善&bug修复
* 增加视频播放兼容性。
* 修复android6.0以下倍速播放声音变调的问题。
* 修复PolyvVideoView的release方法并发引发崩溃的问题。
* 修复连续切换视频会导致崩溃的问题。
* 修复1.0版本的视频下载文件，无法兼容播放的问题。
* 修复了一个字幕功能会产生内存溢出的问题。
* 下载增加文件是否已经下载完成判断。如果已经存在了视频文件，则马上进行进度回调（进度回调中的进度为1，总进度也是1）。然后马上进行下载完成回调。

### 升级jar包
* polyvPlayer2.0.1.jar -> polyvPlayer2.0.2.jar
* polyvSub2.0.1.jar -> polyvSub2.0.2.jar
* polyvDownload2.0.1.jar -> polyvDownload2.0.2.jar
* polyvUpload2.0.1.jar -> polyvUpload2.0.2.jar
* polyvWidget0.5.1.4.jar -> polyvWidget0.5.1.5.jar
* ijkplayer-exo-0.7.7.1.jar -> ijkplayer-exo-0.8.0.1.jar
* ijkplayer-java-0.7.7.1.jar -> ijkplayer-java-0.8.0.1.jar

### 升级源码包
* polyvDownloadSources2.0.1.jar -> polyvDownloadSources2.0.2.jar
* polyvPlayerSources2.0.1.jar -> polyvPlayerSources2.0.2.jar
* polyvUploadSources2.0.1.jar -> polyvUploadSources2.0.2.jar

API文档请看[v2.0.2 API](http://demo.polyv.net/polyv/android/sdk/2.0.2/api/index.html)
***
##### 欢迎来到Polyv-android-sdk-2.0.0-demo的Wiki。
##### 文档内容中的代码都可以从项目中找到。
##### 如果本内容与代码不符，以代码为准。
##### 如果发现内容不符，请好心的提醒我们，我们将马上修改，感谢。