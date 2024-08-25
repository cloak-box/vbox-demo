# Vsystem
Vsystem是一款为开发者提供的SDK，旨在帮助他们快速实现Android应用程序的多开功能。使用Vsystem，开发者可以轻松地将其应用程序转换为支持多个用户账户或多个实例的版本，从而满足不同用户的需求。该SDK具有易于集成、高度可定制和稳定性强等特点，可以大大简化应用程序开发过程，并提高用户体验。

# 使用说明
Vsystem SDK是一款免费的产品，虽然不开源，但您可以自由使用而无需通知本作者。我们严格禁止将该SDK用于外挂助手等严重侵犯商业化和互联网安全的软件。技术是一把双刃剑，在合法合规的前提下使用才能发挥其应有的作用。我们希望开发者们能够遵守相关法律法规和道德准则，以确保技术的正当、合理和安全使用。

# 兼容性

|      |    兼容性 |
| -------- | --------|
| Abi  | `armeabi-v7a / arm64-v8a`| 
| Android version     |   `9.0 ~ 14.0 及后续版本持续兼容更新` |  
| google play     |   `仅支持google三件套` |  
| AccountManager     |   `支持` |  

# 集成SDK
1. 使用aar方式将Vsystem SDK引入到您的项目中。其中，vsystem.aar是核心SDK，而fake_api.jar则是系统API的编译依赖库，只需要在编译时使用即可。
   具体操作步骤如下
    - 将vsystem.aar和fake_api.jar文件复制到您的项目目录下的libs文件夹中。
    - 在您的项目根目录下的build.gradle文件中添加以下代码
    ```
    implementation(files("./libs/vsystem.aar"))
    compileOnly(files("./libs/fake_api.jar"))
    ```
1. 初始化，在Application#attachBaseContext中加入以下代码初始化
    ```
    Vsystem.doAttachBaseContext(this)
    ```

1. 安装应用
    ```
    VPackageManager.installPackageAsUser("apk路径","包名",InstallFrags.INSTALL_FLAG_STORAGE,0)
    ```
1. 启动应用
    ```
    val launchIntent = VPackageManager.getLaunchIntentForPackage(packageName, 0)
    VActivityManager.preloadAppByLaunchIntent(launchIntent, 0)
    VActivityManager.startActivity(launchIntent, 0)
    ``` 
# api 文档

|类|成员变量|方法 | 描述                                         |
|--------|--------|--------|--------------------------------------------|
|Vsystem||doAttachBaseContext| 初始化Vsystem sdk                             |
|||sdkVersion| 获取sdk版本号                                   |
|||setConfig| 设置额外的配置                                    |
|||isSystemHasInstallGms| 系统是否安装gms服务                                |
|||installGms| 安装gms服务                                    |
|VsystemConfig||build| 生成额外配置                                     |
|||setAppHomeComponentName| 设置后，可修改Intent.ACTION_MAIN的行为，去到指定的activity |
|||setEnableOaidHook| 开启oaidhook                                 |
|||setEnableDaemonService| 开启守护进程                                     |
|||setNotification| 设置后台服务的通知栏                                 |
|Vlog||setEnableLog| log开关设置                                    |
|InstallResult|packageName|| 所安装app的包名                                  |
||msg|| 错误信息，success为`false`才会有值                   |
||success|| 安装是否成功                                     |
|VActivityManager||startActivity| 启动应用                                       |
|||preloadAppByLaunchIntent| 预加载应用                                      |
|VPackageManager||getInstalledApplications| 获取沙盒中已按照的所有app                             |
|||isInstalled| 判断app是否在沙盒中按照                              |
|||getLaunchIntentForPackage| 获取沙盒中指定app的`launch intent`                 |
|||forceStopApp| 强制停止应用                                     |
|||clearApk| 清除app数据                                    |
|||unInstallApk| 卸载app                                      |


# 多开支持情况
微信 QQ 小红书 抖音 快手 美团 拼多多 淘宝 陌陌 探探 哔哩哔哩 九秀直播  腾讯视频  爱聊

# 快速体验app

# ![Markdown Logo](https://raw.githubusercontent.com/cloak-box/Vbox/master/apk/ic_launcher_round.webp)
## [隐藏工具箱](https://raw.githubusercontent.com/cloak-box/Vbox/master/apk/demo_1.0.13.apk)

# todo list
- [x] 支持google 套件
- [x] 支持AccountManager api
- [ ] 沙盒中支持微信、qq 等第三方登录

# 更新记录
### 2024
###### 8月04日
- 修复Android 9 启动app奔溃的问题
###### 8月03日
- 增加对android 9的支持
- 增加导入sdcard上未安装的app
- 兼容 PendingIntentReceiver
- android 14 适配 VibratorService的vibrate方法
- 适配android 14 后台启动activity
- 修复momo无法发送聊天消息的bug
###### 7月21日
- 适配悬浮窗权限的申请
- 兼容1加手机oaid的获取
- 优化第三方app的ContentProvider安装流程
- 增加守护进程方式防止部分手机系统冻结server进程，造成binder通信出现异常
- 兼容android 13 google play 的登录
###### 7月14日
- 修复Android 14 GMS 启动ANR的问
- 修复google location 服务获取不到地理位置的问题
- 修改sdk内存的数据存储方式，增强数据类的扩展性，保证数据的存储的完整
- 修复Android 14 系统api hook失败的问题
###### 7月07日
- 修复Android 12 微信视频通话的奔溃
###### 7月06日
- 支持google play 三件套
###### 6月05日
- 适配Intent.ACTION_MAIN的行为，可以去到知道的activity
- 修复运行第三方app时出现的jit崩溃
###### 5月15日
- 支持微信登录



