package com.black.cat.system.demo.ui.act.vsys

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import com.black.cat.system.demo.R

class AppInstallActivity : VsysActivity() {
  override fun initAction(applicationInfo: ApplicationInfo) {
    viewModel.installApp(this, applicationInfo)
    viewModel.installResult.observe(this) {
      setResult(Activity.RESULT_OK, Intent().apply { putExtra(KEY_INSTALL_RESULT, it) })
      finish()
    }
    viewBinding.tvDescription.setText(R.string.app_install_tip)
  }

  companion object {
    const val KEY_INSTALL_RESULT = "key_install_result"
    fun createIntent(context: Context, applicationInfo: ApplicationInfo) =
      Intent(context, AppInstallActivity::class.java).apply {
        putExtra(KEY_LAUNCHER_APPLICATION_INFO, applicationInfo)
      }
  }
}
