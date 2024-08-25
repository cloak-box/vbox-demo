package com.black.cat.system.demo.ui.act

import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.ViewGroup.MarginLayoutParams
import com.black.cat.system.demo.R
import com.black.cat.system.demo.config.AppConfig
import com.black.cat.system.demo.contants.UrlConstant
import com.black.cat.system.demo.databinding.ActivityLoginBinding
import com.black.cat.system.demo.ui.act.webview.WebViewActivity
import com.black.cat.system.demo.ui.base.BaseActivity
import com.black.cat.system.demo.utils.OaidHelper
import com.black.cat.system.demo.utils.StringExt
import com.black.cat.system.demo.utils.ViewUtil
import com.black.cat.system.demo.utils.ViewUtil.singleClickListener
import com.black.cat.system.demo.view.UserViewModel
import com.xuexiang.xui.utils.StatusBarUtils
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog

class LoginActivity : BaseActivity() {
  private val viewModel by lazy { createViewModel(UserViewModel::class.java) }
  private val viewBinding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    ViewUtil.hideSystemUI(this)
    val height = StatusBarUtils.getStatusBarHeight(this)
    val lp = viewBinding.rlAppLauncherArea.layoutParams as MarginLayoutParams
    lp.topMargin = height
    setContentView(viewBinding.root)
    val loginPolicy = getString(R.string.login_policy)
    val loginProtocol = getString(R.string.login_protocol)
    viewBinding.tvLoginReadProtocol.text =
      StringExt.toSpannableStringBuilder(
        getColor(R.color.main_theme_color),
        getString(R.string.login_read_protocol, loginPolicy, loginProtocol),
        loginPolicy to UrlConstant.PRIVACY_URL,
        loginProtocol to UrlConstant.PROTOCOL_URL,
      ) { _, pair ->
        WebViewActivity.start(this, pair.second, pair.first.replace("《", "").replace("》", ""))
      }
    viewBinding.tvLoginReadProtocol.movementMethod = LinkMovementMethod.getInstance()
    viewBinding.btnLoginAgreement.singleClickListener { login() }
    viewBinding.tvLoginDisagreement.singleClickListener {
      val spannable =
        StringExt.toSpannableStringBuilder(
          getColor(R.color.main_theme_color),
          getString(R.string.login_dlg_read_protocol, loginPolicy, loginProtocol),
          loginPolicy to UrlConstant.PRIVACY_URL,
          loginProtocol to UrlConstant.PROTOCOL_URL,
        ) { _, pair ->
          WebViewActivity.start(this, pair.second, pair.first.replace("《", "").replace("》", ""))
        }

      MaterialDialog.Builder(this)
        .title(R.string.login_dlg_policy_and_protocol)
        .content(spannable)
        .positiveText(R.string.login_agreement)
        .negativeText(R.string.login_disagreement)
        .onPositive { _, _ -> login() }
        .onNegative { _, _ ->
          AppConfig.saveLoginState()
          startActivity(Intent(this, MainActivity::class.java))
          finish()
        }
        .show()
    }
    viewBinding.imgCloseApp.singleClickListener {
      MaterialDialog.Builder(this)
        .iconRes(R.drawable.icon_tip)
        .title(R.string.tip_infos)
        .content(R.string.login_close_app)
        .negativeText(R.string.submit)
        .positiveText(R.string.cancel)
        .onNegative { _, _ -> finish() }
        .show()
    }
    viewModel.registerUserObserver.observe(this) {
      hideLoading()
      AppConfig.saveLoginState()
      AppConfig.saveShowPrivacyPolicyState()
      startActivity(Intent(this, MainActivity::class.java))
      finish()
    }
  }

  private fun login() {
    OaidHelper.register(application)
    showLoading()
    viewModel.registerUser(this)
  }
}
