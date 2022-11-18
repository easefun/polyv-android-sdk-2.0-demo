polyv-android-sdk-2.0.0-demo
===
[![build passing](https://img.shields.io/badge/build-passing-brightgreen.svg)](#)
[![GitHub release](https://img.shields.io/badge/release-v2.18.3-blue.svg)](https://github.com/easefun/polyv-android-sdk-2.0-demo/releases/tag/v2.18.3)

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
- [polyv-android-sdk-2.0.0-demo](#polyv-android-sdk-200-demo)
    - [简介](#简介)
    - [试用](#试用)
    - [文档](#文档)
    - [运行环境](#运行环境)
    - [支持功能](#支持功能)
    - [与SDK 1.0对比](#与sdk-10对比)
      - [关系？](#关系)
      - [如果你接触过SDK1.0，那么你可能想了解SDK2.0.0里面优化了什么。](#如果你接触过sdk10那么你可能想了解sdk200里面优化了什么)
      - [为什么要升级到SDK2.0.0](#为什么要升级到sdk200)
      - [集成SDK2.0.0较SDK1.0最大的改变是什么？](#集成sdk200较sdk10最大的改变是什么)
      - [如何从SDK1.0升级到SDK2.0.6和更早版本](#如何从sdk10升级到sdk206和更早版本)
      - [如何从SDK1.0升级到SDK2.2.1和更新版本](#如何从sdk10升级到sdk221和更新版本)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

***
### 简介
SDK 2.0.0 是Polyv为开发者用户提供的点播SDK ，是jar文件和so文件。易于集成，内部包含`视频播放` `弹幕` `视频下载` `视频上传`等功能。首先需要在[链接到官网](http://www.polyv.net)注册账户并开通点播功能，然后集成SDK2.0.0到你的项目中。

SDK 2.0.0 demo是SDK2.0.0的demo示例Android studio项目工程，其中包含了最新SDK2.0.0并且演示了如何在项目中集成SDK2.0.0。

### 试用
可以[点击下载](https://www.pgyer.com/B7aw)，安装试用。
安装密码：polyv

### 文档
1. [wiki 文档](https://help.polyv.net/#/vod/android/)
2. [全版本更新记录](./CHANGELOG.md)
3. [API 文档](https://github.com/easefun/polyv-android-sdk-2.0-demo/blob/master/API%E6%96%87%E6%A1%A3%E7%9B%AE%E5%BD%95.md)
***
### 运行环境
* JDK 1.7 或以上
* Android SDK 14 或以上
* Android Studio 2.2.0 或以上
* **abiFilters 建议配置为[arm64-v8a、armeabi-v7a、x86]。不要使用armeabi，仅使用armeabi可能会在部分机型上出现音画不同步、跳秒播放等异常情况**
***
### 支持功能
* 普通功能
  * 播放
  * 暂停
  * 获取视频总时长
  * 获取当前播放位置
  * 获取视频缓冲百分比
  * 播放进度跳跃
* 设置播放速度
* 设置屏幕比例，例：比例缩放，充满父窗，匹配内容，16:9比例缩放，4:3比例缩放
* 设置播放缓存视图，在视频加载中显示的loading视图
* 跑马灯
* 全屏
* 广告（只在线播放支持）
* 片头（只在线播放支持）
* 秒播（只在线播放支持，图片广告，视频广告，片头播放过程中提前加载视频）
* 问答
* 字幕
* 自动续播
* 截图
* 切换码率（清晰度）
* 手势滑动
  * 左向上
  * 左向下
  * 右向上
  * 右向下
  * 往左滑
  * 往右滑
* 获取播放时长
* 获取停留时长
* 开启关闭声音
* 声音调节
* 亮度调节
* 后台播放
* 锁屏播放
* 下载（包含下载队列，全部暂停，全部开始）
* 上传
* 弹幕
***
### 与SDK 1.0对比
如果未接触过[polyv-android-sdk-demo（1.0）](https://github.com/easefun/polyv-android-sdk-demo)（以下简称SDK1.0demo）和polyv-android-sdk(1.0)（以下称SDK1.0）可以跳过SDK1.0相关部分。

#### 关系？
SDK2.0.0是SDK1.0的升级版，在SDK1.0的基础上，进行升级，重构，优化。
#### 如果你接触过SDK1.0，那么你可能想了解SDK2.0.0里面优化了什么。
* 点播逻辑和直播逻辑完全分离开来
  * 使SDK包体积更小
  * 使接口更加精简
  * 有独立的API文档
* 播放/下载/上传功能分开jar包
  * 需要哪个功能就导入相关功能包
  * 使SDK包体积更小
  * 灵活升级功能包
* 抽象接口方法
  * 提供接口源码，能在IDE中直接浏览接口描述
* 全新优美的播放器界面。
* Android Studio项目。
#### 为什么要升级到SDK2.0.0
集成门槛和开发难度大大降低。
SDK1.0demo和SDK1.0不再进行功能更新。
#### 集成SDK2.0.0较SDK1.0最大的改变是什么？
* 第一点是SDK2.0的类名统一前缀Polyv，SDK1.0中的类可以继续使用。
* 第二点是所有回调方法都在com.easefun.polyvsdk.video.listener包中定义，并且所有回调方法都在主线程中回调。
* 第三点是广告视频和点播视频使用不同的播放器进行播放，从而为实现视频预加载提供可能，因此增加了PolyvAuxiliaryVideoView，用于播放广告视频，片头视频。逻辑已全部由播放器控制。

更多细节请在SDK2.0.0demo中查看。
#### 如何从SDK1.0升级到SDK2.0.6和更早版本
* 删除SDK1.0中的so库，使用SDK2.0.0中的so库。
* 删除SDK1.0中的jar包，使用SDK2.0.0中的jar包。
#### 如何从SDK1.0升级到SDK2.2.1和更新版本
* 删除SDK1.0中的so库。
* 删除SDK1.0中的jar包。
* 增加2.2.1和更高版本中的依赖。