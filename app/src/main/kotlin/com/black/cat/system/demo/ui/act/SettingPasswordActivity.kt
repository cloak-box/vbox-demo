package com.black.cat.system.demo.ui.act

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import com.black.cat.system.demo.R
import com.black.cat.system.demo.config.AppConfig
import com.black.cat.system.demo.databinding.ActivityPasswordBinding
import com.black.cat.system.demo.ui.base.BaseActivity
import com.black.cat.system.demo.utils.ViewUtil.singleClickListener
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog
import com.xuexiang.xutil.tip.ToastUtils

class SettingPasswordActivity : BaseActivity() {
  private val handler: Handler = Handler(Looper.getMainLooper())
  private val viewBinding by lazy { ActivityPasswordBinding.inflate(layoutInflater) }
  private var appPassWordNew: String = ""
  private var appPassWordNewAgain: String = ""
  private var showInputPassWordNewAgain = false
  private val runnable = { viewBinding.activitySettingPasswordFrag.icvPasswordCode.showSoftInput() }
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(viewBinding.root)
    initView(AppConfig.getAppPassword())
  }

  private fun showSettingPasswordNextFrag() {
    viewBinding.activitySettingPasswordTipFrag.root.visibility = View.GONE
    viewBinding.activitySettingPasswordFrag.root.visibility = View.VISIBLE
    handler.postDelayed(runnable, 500)
  }

  private fun initView(appPassWord: String) {
    viewBinding.titleBar.setLeftClickListener { finish() }
    if (appPassWord.isEmpty()) {
      viewBinding.activitySettingPasswordTipFrag.btnSettingPasswordTitle.setText(
        R.string.password_setting
      )
      viewBinding.activitySettingPasswordTipFrag.btnSettingPasswordNextFrag.setText(
        R.string.password_setting_btn_text
      )
      viewBinding.activitySettingPasswordTipFrag.btnSettingPasswordClear.visibility = View.GONE
    } else {
      viewBinding.activitySettingPasswordTipFrag.btnSettingPasswordTitle.setText(
        R.string.password_setting_change
      )
      viewBinding.activitySettingPasswordTipFrag.btnSettingPasswordNextFrag.setText(
        R.string.password_setting_change
      )
      viewBinding.activitySettingPasswordTipFrag.btnSettingPasswordClear.visibility = View.VISIBLE
      viewBinding.activitySettingPasswordTipFrag.btnSettingPasswordClear.singleClickListener {
        MaterialDialog.Builder(this)
          .iconRes(R.drawable.icon_tip)
          .title(R.string.prompt_popup_title)
          .content(R.string.clear_password_description)
          .positiveText(R.string.clear_password_sure)
          .negativeText(R.string.clear_password_cancel)
          .onPositive { _, _ ->
            AppConfig.saveAppPassword("")
            initView("")
          }
          .show()
      }
    }
    viewBinding.activitySettingPasswordTipFrag.btnSettingPasswordNextFrag.singleClickListener {
      showSettingPasswordNextFrag()
    }
    viewBinding.activitySettingPasswordFrag.icvPasswordCode.setTextChangeListener {
      val tmpPassword = it
      if (showInputPassWordNewAgain) {
        appPassWordNewAgain = tmpPassword
      } else {
        appPassWordNew = tmpPassword
      }

      if (showInputPassWordNewAgain) {
        if (appPassWordNewAgain.length == 4) {
          if (appPassWordNew != appPassWordNewAgain) {
            ToastUtils.toast(getString(R.string.input_password_error))
            viewBinding.activitySettingPasswordFrag.icvPasswordCode.text = ""
            showInputPassWordNewAgain = false
            viewBinding.activitySettingPasswordFrag.tvInputPassword.setText(R.string.input_password)
          } else {
            ToastUtils.toast(getString(R.string.input_password_success))
            AppConfig.saveAppPassword(appPassWordNew)
            viewBinding.activitySettingPasswordFrag.icvPasswordCode.hideSoftInput()
            AppConfig.saveShowWarningPasswordTip(true)
            CalcActivity.start(this, checkPassword = true)
            finish()
          }
        }
      } else {
        if (appPassWordNew.length == 4) {
          showInputPassWordNewAgain = true
          viewBinding.activitySettingPasswordFrag.tvInputPassword.setText(
            R.string.input_password_again
          )
          viewBinding.activitySettingPasswordFrag.icvPasswordCode.text = ""
        }
      }
    }
  }

  override fun onPause() {
    super.onPause()
    hideInput()
  }

  override fun onBackPressed() {
    if (viewBinding.activitySettingPasswordTipFrag.root.visibility == View.VISIBLE) {
      super.onBackPressed()
    } else {
      viewBinding.activitySettingPasswordFrag.icvPasswordCode.hideSoftInput()
      viewBinding.activitySettingPasswordTipFrag.root.visibility = View.VISIBLE
      viewBinding.activitySettingPasswordFrag.root.visibility = View.GONE
      viewBinding.activitySettingPasswordFrag.icvPasswordCode.text = ""
      viewBinding.activitySettingPasswordFrag.tvInputPassword.setText(R.string.input_password)
      showInputPassWordNewAgain = false
      appPassWordNewAgain = ""
    }
  }
  private fun imm(): InputMethodManager {
    return getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
  }

  private fun hideInput() {
    hideInput(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
  }

  private fun hideInput(flag: Int) {
    try {
      window.setSoftInputMode(flag)
      if (window.currentFocus != null) {
        imm().hideSoftInputFromWindow(window.currentFocus!!.windowToken, 0)
      } else {
        imm().hideSoftInputFromWindow(window.decorView.windowToken, 0)
      }
    } catch (ignored: Exception) {}
  }

  override fun onDestroy() {
    handler.removeCallbacks(runnable)
    super.onDestroy()
  }
}
