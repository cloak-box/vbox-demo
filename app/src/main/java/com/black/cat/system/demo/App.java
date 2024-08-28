package com.black.cat.system.demo;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import androidx.annotation.NonNull;
import com.black.cat.system.demo.bean.AppInfo;
import com.black.cat.system.demo.hook.PinedHookKt;
import com.black.cat.system.demo.hook.PluginManager;
import com.black.cat.system.demo.ui.act.MainActivity;
import com.black.cat.system.demo.utils.FileUtilKt;
import com.black.cat.vsystem.api.NotificationBuilder;
import com.black.cat.vsystem.api.VPackageManager;
import com.black.cat.vsystem.api.Vlog;
import com.black.cat.vsystem.api.Vsystem;
import com.black.cat.vsystem.api.VsystemConfig;
import com.black.cat.vsystem.dexopt.ShareLog;
import com.huawei.agconnect.AGConnectInstance;
import com.huawei.agconnect.crash.AGConnectCrash;
import com.tencent.mmkv.MMKV;
import java.io.File;
import java.util.function.BiConsumer;
import top.canyie.pine.xposed.PineXposed;

public class App extends Application {

  public static App instance;

  @Override
  public void attachBaseContext(Context base) {
    super.attachBaseContext(base);
    instance = this;
    Vlog.setEnableLog(BuildConfig.DEBUG);
    ShareLog.logEnable = BuildConfig.DEBUG;
    Vsystem.doAttachBaseContext(this);
    Vsystem.setConfig(
        VsystemConfig.build(
            config -> {
              config.setAppHomeComponentName(
                  new ComponentName(base.getPackageName(), MainActivity.class.getName()));
              config.setEnableOaidHook(true);
              config.setEnableDaemonService(true);
              config.setNotification(
                  new NotificationBuilder() {
                    @NonNull
                    @Override
                    public Notification build(@NonNull Context context) {
                      return createNotification();
                    }
                  });
              config.setAppProcessListener(
                  (packageName, processName, packageInfo, classLoader) -> {
                    initCrashInfo(packageName, processName, packageInfo, classLoader);
                    PinedHookKt.hook(classLoader);
                    PluginManager.INSTANCE
                        .getPlugins()
                        .forEach(
                            (s, appInfo) -> {
                              if (appInfo.isDefault()) {
                                File moduleFile = new File(appInfo.getApplicationInfo().sourceDir);
                                PineXposed.loadModule(
                                    moduleFile,
                                    moduleFile.getParentFile().getAbsolutePath() + "/lib",
                                    false);
                                PineXposed.onPackageLoad(
                                    packageName,
                                    processName,
                                    packageInfo.applicationInfo,
                                    packageName.equals(packageName),
                                    classLoader);
                              }
                            });
                  });
              return null;
            }));
    MMKV.initialize(FileUtilKt.getMmkvDir().getAbsolutePath());
  }

  private void initCrashInfo(
      String packageName, String processName, PackageInfo packageInfo, ClassLoader classLoader) {
    AGConnectCrash aGCCrash = AGConnectCrash.getInstance();
    aGCCrash.setCustomKey("app_packageName", packageName);
    aGCCrash.setCustomKey(
        "app_name",
        packageInfo.applicationInfo.loadLabel(App.instance.getPackageManager()).toString());
    aGCCrash.setCustomKey("app_versionName", packageInfo.versionName);
    aGCCrash.setCustomKey("app_versionCode", packageInfo.versionCode);
    aGCCrash.setCustomKey("app_processName", processName);
    aGCCrash.setCustomKey("app_target_sdk_version", packageInfo.applicationInfo.targetSdkVersion);
  }

  private Notification createNotification() {
    String channelID = getPackageName() + ".vbox_core";
    NotificationManager notificationManager =
        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    NotificationChannel notificationChannel = notificationManager.getNotificationChannel(channelID);
    if (notificationChannel == null) {
      NotificationChannel channel =
          new NotificationChannel(
              channelID, "保活防止系统冻结server进程", NotificationManager.IMPORTANCE_MIN);
      notificationManager.createNotificationChannel(channel);
    }
    Notification.Builder builder =
        new Notification.Builder(this, channelID) // 获取一个Notification构造器
            .setSmallIcon(R.mipmap.ic_launcher)
            .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher))
            .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间
    return builder.build();
  }

  @Override
  public void onCreate() {
    super.onCreate();
    if (AGConnectInstance.getInstance() == null) {
      AGConnectInstance.initialize(getApplicationContext());
    }
  }
}
