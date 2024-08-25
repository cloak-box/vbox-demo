package com.black.cat.system.demo.config

import com.tencent.mmkv.MMKV

object AppConfig {
  private const val KEY_NEED_SHOW_PRIVACY_POLICY = "need_show_privacy_policy"
  private const val KEY_HAS_LOGIN = "key_has_login"
  private const val KEY_INSTALL_APP_GUIDE = "key_install_app_guide"
  private const val KEY_OAID = "k_d_i_O"
  private val appConfigKV = MMKV.mmkvWithID("app_config.kv")!!
  fun hasLogin() = appConfigKV.getBoolean(KEY_HAS_LOGIN, false)
  fun saveLoginState(boolean: Boolean = true) {
    appConfigKV.putBoolean(KEY_HAS_LOGIN, boolean)
  }
  fun needShowPrivacyPolicy() = appConfigKV.getBoolean(KEY_NEED_SHOW_PRIVACY_POLICY, true)
  fun saveShowPrivacyPolicyState(boolean: Boolean = false) {
    appConfigKV.putBoolean(KEY_NEED_SHOW_PRIVACY_POLICY, boolean)
  }
  fun needShowInstallAppGuide() = appConfigKV.getBoolean(KEY_INSTALL_APP_GUIDE, true)
  fun saveShowInstallAppGuideState() {
    appConfigKV.putBoolean(KEY_INSTALL_APP_GUIDE, false)
  }

  fun getOaidFromLocal() = appConfigKV.getString(KEY_OAID, "")
  fun saveOaidToLocal(oaid: String) {
    appConfigKV.putString(KEY_OAID, oaid)
  }
}