package com.black.cat.system.demo.user;

import com.black.cat.system.demo.data.User;
import com.tencent.mmkv.MMKV;

public class UserManager {
  private static final String KEY_USER_ID = "key_user_id";
  private static final String KEY_NICK_NAME = "key_nick_name";
  private static final String KEY_CREATE_TIME = "key_create_time";
  private static final MMKV userConfigKV = MMKV.mmkvWithID("user_config.kv");
  private static User user = new User();

  static {
    user.setUserId(userConfigKV.getLong(KEY_USER_ID, user.getUserId()));
    user.setCreateTime(userConfigKV.getLong(KEY_CREATE_TIME, user.getCreateTime()));
    user.setNickName(userConfigKV.getString(KEY_NICK_NAME, user.getNickName()));
  }

  private static void saveUserInfoToLocal() {
    userConfigKV
        .putLong(KEY_USER_ID, user.getUserId())
        .putLong(KEY_CREATE_TIME, user.getCreateTime())
        .putString(KEY_NICK_NAME, user.getNickName())
        .apply();
  }

  public static void updateUserInfo(User user) {
    UserManager.user.setUserId(user.getUserId());
    UserManager.user.setCreateTime(user.getCreateTime());
    UserManager.user.setNickName(user.getNickName());
    saveUserInfoToLocal();
  }

  public static void clearUserInfo() {
    user = new User();
    saveUserInfoToLocal();
  }

  public static boolean hasUser() {
    return user.getUserId() != -1;
  }

  public static User user() {
    return user;
  }
}
