package com.black.cat.system.demo.ui.act.vsys

import android.content.pm.ApplicationInfo
import android.os.Bundle
import com.black.cat.system.demo.databinding.LauncherActivityBinding
import com.black.cat.system.demo.ui.base.BaseActivity
import com.black.cat.system.demo.utils.ViewUtil
import com.black.cat.system.demo.view.AppLauncherViewModel

abstract class VsysActivity : BaseActivity() {
  protected val viewBinding by lazy { LauncherActivityBinding.inflate(layoutInflater) }
  protected val viewModel by lazy { createViewModel(AppLauncherViewModel::class.java) }
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    ViewUtil.hideSystemUI(this)
    val applicationInfo: ApplicationInfo? = intent.getParcelableExtra(KEY_LAUNCHER_APPLICATION_INFO)
    if (applicationInfo == null) {
      finish()
    }
    setContentView(viewBinding.root)
    val drawable = applicationInfo!!.loadIcon(packageManager)
    viewBinding.ivIcon.setImageDrawable(drawable)
    viewBinding.tvAppName.text = applicationInfo.loadLabel(packageManager)
    viewModel.generateViewColor(drawable)
    viewModel.bgColor.observe(this) {
      viewBinding.root.setBackgroundColor(it.first)
      viewBinding.tvAppName.setTextColor(it.second)
      viewBinding.pbProgressBar.indeterminateDrawable.setTint(it.second)
      viewBinding.tvDescription.setTextColor(it.second)
    }
    initAction(applicationInfo)
  }

  abstract fun initAction(applicationInfo: ApplicationInfo)

  companion object {
    const val KEY_LAUNCHER_APPLICATION_INFO = "key_launcher_application_info"
  }
}
