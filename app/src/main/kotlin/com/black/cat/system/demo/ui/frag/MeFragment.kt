package com.black.cat.system.demo.ui.frag

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.black.cat.system.demo.BuildConfig
import com.black.cat.system.demo.R
import com.black.cat.system.demo.api.models.BaseResponse
import com.black.cat.system.demo.config.AppConfig
import com.black.cat.system.demo.contants.UrlConstant
import com.black.cat.system.demo.data.User
import com.black.cat.system.demo.databinding.FragmentMeBinding
import com.black.cat.system.demo.kbus.EVENT_UPDATE_USER_INFO
import com.black.cat.system.demo.kbus.KBaseEvent
import com.black.cat.system.demo.ui.act.LoginActivity
import com.black.cat.system.demo.ui.act.webview.WebViewActivity
import com.black.cat.system.demo.ui.base.BaseFragment
import com.black.cat.system.demo.user.UserManager
import com.black.cat.system.demo.utils.ViewUtil.singleClickListener
import com.black.cat.system.demo.view.MeFragmentViewModel
import com.black.cat.system.demo.view.StateObserver
import com.black.cat.system.demo.view.UserViewModel
import com.black.cat.vsystem.api.Vlog
import com.black.cat.vsystem.api.Vsystem
import com.huawei.agconnect.crash.AGConnectCrash
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog
import com.xuexiang.xutil.tip.ToastUtils
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MeFragment : BaseFragment() {
  private val binding by lazy {
    FragmentMeBinding.inflate(fragmentOwner.hostActivity().layoutInflater)
  }

  private val userViewModel by lazy { createViewModel(UserViewModel::class.java) }
  private val meFragmentViewModel by lazy { createViewModel(MeFragmentViewModel::class.java) }
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    binding.rlPolicy.singleClickListener {
      WebViewActivity.start(fragmentOwner.hostActivity(), UrlConstant.PRIVACY_URL, "隐私政策")
    }
    binding.rlUserAgreement.singleClickListener {
      WebViewActivity.start(fragmentOwner.hostActivity(), UrlConstant.PROTOCOL_URL, "用户协议")
    }

    binding.rlGithub.singleClickListener {
      WebViewActivity.start(
        fragmentOwner.hostActivity(),
        "https://github.com/cloak-box/Vbox",
        "cloak-box"
      )
    }
    binding.meVersion.text = getString(R.string.me_version, BuildConfig.VERSION_NAME)
    binding.rlVersion.singleClickListener { ToastUtils.toast(R.string.me_last_version) }
    binding.rlAddQqGroup.singleClickListener {
      val key = "DRaKIsUTy7QVBIG66Ei5NAT2Mc0TM4J6"
      val intent = Intent()
      intent.data =
        Uri.parse(
          "mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26jump_from%3Dwebapi%26k%3D$key"
        )
      // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面
      // //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      try {
        startActivity(intent)
      } catch (e: Exception) {
        ToastUtils.toast(getString(R.string.me_not_support))
      }
    }
    userViewModel.registerUserIfNeed(fragmentOwner.hostActivity())
    updateUserInfo()
    binding.tvRegisterAccount.singleClickListener {
      fragmentOwner.showLoading()
      userViewModel.registerUser(fragmentOwner.hostActivity())
    }

    userViewModel.registerUserObserver.observe(
      fragmentOwner.hostActivity(),
      object : StateObserver<User?>() {
        override fun changed(t: BaseResponse<User?>?) {
          fragmentOwner.hideLoading()
          if (t != null && t.isSuccess() && t.data != null) {
            updateUserInfo(t.data!!)
          }
        }
      }
    )

    userViewModel.unregisterUserObserver.observe(
      fragmentOwner.hostActivity(),
      object : StateObserver<Boolean>() {
        override fun changed(t: BaseResponse<Boolean>?) {
          fragmentOwner.hideLoading()
          if (t?.isSuccess() == true) {
            UserManager.clearUserInfo()
            AppConfig.saveShowPrivacyPolicyState(true)
            AppConfig.saveLoginState(false)
            fragmentOwner
              .hostActivity()
              .startActivity(Intent(fragmentOwner.hostActivity(), LoginActivity::class.java))
            fragmentOwner.hostActivity().finish()
          }
        }
      }
    )

    binding.rlApplyForCancellation.singleClickListener {
      AGConnectCrash.getInstance().testIt(context)
      MaterialDialog.Builder(fragmentOwner.hostActivity())
        .iconRes(R.drawable.icon_tip)
        .title(R.string.me_logout_popup_title)
        .content(R.string.me_logout_popup_description)
        .positiveText(R.string.me_logout_popup_confirm)
        .negativeText(R.string.me_logout_popup_cancel)
        .onPositive { _, _ -> unregisterUser() }
        .show()
    }

    meFragmentViewModel.installGmsPackageObserver.observe(fragmentOwner.hostActivity()) {
      fragmentOwner.hideLoading()
      if (it) {
        ToastUtils.toast(getString(R.string.install_gms_success))
      } else {
        ToastUtils.toast(getString(R.string.install_gms_failure))
      }
    }
    binding.rlOpenGms.singleClickListener {
      if (Vsystem.isSystemHasInstallGms()) {
        fragmentOwner.showLoading()
        meFragmentViewModel.installGms(fragmentOwner.hostActivity())
      }
    }
  }

  private fun unregisterUser() {
    fragmentOwner.showLoading()
    userViewModel.unregisterUser(fragmentOwner.hostActivity(), UserManager.user().userId)
  }

  private fun updateUserInfo() {
    if (UserManager.hasUser()) {
      updateUserInfo(UserManager.user())
    }
  }

  private fun updateUserInfo(user: User) {
    binding.tvUserNickName.visibility = View.VISIBLE
    binding.tvUserId.visibility = View.VISIBLE
    binding.tvRegisterAccount.visibility = View.GONE
    binding.tvUserNickName.text = user.nickName
    binding.tvUserId.text = "ID:${user.userId}"
    Vlog.d("MeFragment ") { "updateUserInfo $user" }
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  fun onMessageEvent(event: KBaseEvent) {
    if (event.eventName() == EVENT_UPDATE_USER_INFO) {
      updateUserInfo(event.eventValue() as User)
    }
  }

  override fun onStart() {
    EventBus.getDefault().register(this)
    super.onStart()
  }

  override fun onStop() {
    EventBus.getDefault().unregister(this)
    super.onStop()
  }
}
