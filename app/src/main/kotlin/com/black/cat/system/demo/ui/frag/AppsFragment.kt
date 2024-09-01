package com.black.cat.system.demo.ui.frag

import android.app.Activity
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewbinding.ViewBinding
import com.black.cat.system.demo.R
import com.black.cat.system.demo.bean.AppInfo
import com.black.cat.system.demo.bean.TYPE_APP
import com.black.cat.system.demo.bean.TYPE_CALC
import com.black.cat.system.demo.bean.TYPE_DEFAULT
import com.black.cat.system.demo.config.AppConfig
import com.black.cat.system.demo.contants.UrlConstant
import com.black.cat.system.demo.databinding.FragmentAppsBinding
import com.black.cat.system.demo.databinding.FragmentAppsItemBinding
import com.black.cat.system.demo.ui.act.CalcActivity
import com.black.cat.system.demo.ui.act.PhoneInstalledListActivity
import com.black.cat.system.demo.ui.act.vsys.AppInstallActivity
import com.black.cat.system.demo.ui.act.vsys.AppLauncherActivity
import com.black.cat.system.demo.ui.act.webview.WebViewActivity
import com.black.cat.system.demo.ui.base.BaseFragment
import com.black.cat.system.demo.utils.StringExt
import com.black.cat.system.demo.view.AppsFragmentViewModel
import com.black.cat.system.demo.widget.adapter.BaseAdapter
import com.black.cat.system.demo.widget.adapter.BaseHolder
import com.black.cat.system.demo.widget.adapter.BaseItemClickListener
import com.black.cat.system.demo.widget.adapter.BaseItemLongClickListener
import com.black.cat.system.demo.widget.itemdecoration.GridOffsetsItemDecoration
import com.black.cat.vsystem.api.Vlog
import com.black.cat.vsystem.api.pm.InstallResult
import com.xuexiang.xui.utils.DensityUtils
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog
import com.xuexiang.xui.widget.dialog.materialdialog.simplelist.MaterialSimpleListAdapter
import com.xuexiang.xui.widget.dialog.materialdialog.simplelist.MaterialSimpleListItem
import com.xuexiang.xui.widget.guidview.DismissListener
import com.xuexiang.xui.widget.guidview.FocusShape
import com.xuexiang.xui.widget.guidview.GuideCaseView
import com.xuexiang.xutil.tip.ToastUtils

class AppsFragment : BaseFragment() {

  val selectAppForSdCard =
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
      if (it.resultCode == Activity.RESULT_OK) {
        val uri = it.data?.data ?: return@registerForActivityResult
        Vlog.d("MainActivity", "import for $uri")
        viewModel.copyApkFromSdcard(fragmentOwner.hostActivity(), uri)
      }
    }

  private val binding by lazy {
    FragmentAppsBinding.inflate(fragmentOwner.hostActivity().layoutInflater)
  }
  private val viewModel by lazy { createViewModel(AppsFragmentViewModel::class.java) }
  private val selectAppLauncher =
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
      if (it.resultCode == Activity.RESULT_OK) {
        val applicationInfo: ApplicationInfo? =
          it.data?.getParcelableExtra(PhoneInstalledListActivity.INTENT_KEY_APPLICATION_INFO)
        applicationInfo?.let {
          installApp.launch(
            AppInstallActivity.createIntent(fragmentOwner.hostActivity(), applicationInfo)
          )
        }
      }
    }
  private val installApp =
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
      if (it.resultCode == Activity.RESULT_OK) {
        val installResult: InstallResult? =
          it.data?.getParcelableExtra(AppInstallActivity.KEY_INSTALL_RESULT)
        installResult?.let {
          Vlog.d(
            "MainActivity",
            "install ${installResult.packageName} success ${installResult.success} ${installResult.msg}"
          )
          if (installResult.success) {
            viewModel.getInstalledPackage(0)
          } else {
            ToastUtils.toast("安装失败 请检测安装包是否正确")
          }
        }
      }
    }

  private val installedPackageAdapter by lazy { initInstalledPackageAdapter() }

  private fun initInstalledPackageAdapter(): InstalledPackageAdapter {
    val adapter =
      object : InstalledPackageAdapter() {
        override fun onViewAttachedToWindow(holder: BaseHolder<AppInfo, ViewBinding>) {
          super.onViewAttachedToWindow(holder)
          val appInfp = getData()[holder.adapterPosition]
          if (appInfp.isDefault == TYPE_DEFAULT && AppConfig.needShowInstallAppGuide()) {
            GuideCaseView.Builder(fragmentOwner.hostActivity())
              .focusOn(holder.binding.root)
              .title("点击添加应用")
              .focusShape(FocusShape.ROUNDED_RECTANGLE)
              .roundRectRadius(DensityUtils.dp2px(fragmentOwner.hostActivity(), 15f))
              .setFocusClickListener { selectAppLauncher() }
              .dismissListener(
                object : DismissListener {
                  override fun onDismiss(id: String?) {
                    AppConfig.saveShowInstallAppGuideState()
                  }
                  override fun onSkipped(id: String?) {}
                }
              )
              .showOnce("once_add_app_guide")
              .build()
              .show()
          }
        }
      }
    adapter.itemClickListener =
      object : BaseItemClickListener<AppInfo> {
        override fun onClick(binding: ViewBinding, positionData: AppInfo, position: Int) {
          when (positionData.isDefault) {
            TYPE_DEFAULT -> {
              selectAppLauncher()
            }
            TYPE_CALC -> {
              CalcActivity.start(fragmentOwner.hostActivity())
            }
            else -> {
              AppLauncherActivity.start(
                fragmentOwner.hostActivity(),
                positionData.applicationInfo!!
              )
            }
          }
        }
      }
    adapter.itemLongClickListener =
      object : BaseItemLongClickListener<AppInfo> {
        override fun onLongClick(binding: ViewBinding, positionData: AppInfo, position: Int) {
          if (positionData.isDefault == TYPE_APP) {
            showAppActions(positionData.applicationInfo!!)
          }
        }
      }
    return adapter
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    binding.rvRecyclerView.layoutManager = GridLayoutManager(fragmentOwner.hostActivity(), 3)
    val decoration = GridOffsetsItemDecoration(GridOffsetsItemDecoration.GRID_OFFSETS_VERTICAL)
    decoration.setHorizontalItemOffsets(DensityUtils.dp2px(fragmentOwner.hostActivity(), 8f))
    decoration.setVerticalItemOffsets(DensityUtils.dp2px(fragmentOwner.hostActivity(), 8f))
    binding.rvRecyclerView.addItemDecoration(decoration)
    viewModel.installedPackage.observe(viewLifecycleOwner) {
      fragmentOwner.hideLoading()
      installedPackageAdapter.clear()
      installedPackageAdapter.add(AppInfo(TYPE_DEFAULT))
      installedPackageAdapter.add(AppInfo(TYPE_CALC))
      installedPackageAdapter.addAll(
        it.map { applicationInfo -> AppInfo(applicationInfo = applicationInfo) }
      )
      binding.rvRecyclerView.adapter = installedPackageAdapter
    }
    viewModel.unInstallPackage.observe(viewLifecycleOwner) { applicationInfo ->
      installedPackageAdapter
        .getData()
        .find { appInfo -> applicationInfo.packageName == appInfo.applicationInfo?.packageName }
        ?.let {
          installedPackageAdapter.remove(it)
          installedPackageAdapter.notifyDataSetChanged()
        }
      fragmentOwner.hideLoading()
    }

    viewModel.copyApkFromSdcard.observe(viewLifecycleOwner) {
      fragmentOwner.hideLoading()
      if (it != null) {
        installApp.launch(AppInstallActivity.createIntent(fragmentOwner.hostActivity(), it))
      }
    }
    viewModel.getInstalledPackage(0)
  }

  private fun showAppActions(applicationInfo: ApplicationInfo) {
    val list = mutableListOf<MaterialSimpleListItem>()
    list.add(
      MaterialSimpleListItem.Builder(fragmentOwner.hostActivity())
        .id(R.string.app_clear.toLong())
        .content(R.string.app_clear)
        .icon(R.drawable.icon_app_clear)
        .build()
    )
    list.add(
      MaterialSimpleListItem.Builder(fragmentOwner.hostActivity())
        .id(R.string.app_remove.toLong())
        .content(R.string.app_remove)
        .icon(R.drawable.icon_app_delete)
        .build()
    )
    list.add(
      MaterialSimpleListItem.Builder(fragmentOwner.hostActivity())
        .id(R.string.app_stop.toLong())
        .content(R.string.app_stop)
        .icon(R.drawable.icon_app_stop)
        .build()
    )
    val adapter =
      MaterialSimpleListAdapter(list).setOnItemClickListener {
        dialog: MaterialDialog,
        _: Int,
        item: MaterialSimpleListItem ->
        when (item.id) {
          R.string.app_clear.toLong() -> viewModel.clearApk(applicationInfo)
          R.string.app_remove.toLong() -> {
            fragmentOwner.showLoading()
            viewModel.unInstallApk(applicationInfo)
          }
          R.string.app_stop.toLong() -> viewModel.forceStopApp(applicationInfo)
        }
        dialog.dismiss()
      }
    MaterialDialog.Builder(fragmentOwner.hostActivity()).adapter(adapter, null).show()
  }

  private fun selectAppLauncher() {
    if (AppConfig.needShowPrivacyPolicy()) {
      showReadProtocolDlg {
        selectAppLauncher.launch(
          Intent(fragmentOwner.hostActivity(), PhoneInstalledListActivity::class.java)
        )
      }
    } else {
      selectAppLauncher.launch(
        Intent(fragmentOwner.hostActivity(), PhoneInstalledListActivity::class.java)
      )
    }
  }

  private fun showReadProtocolDlg(block: () -> Unit) {
    val loginPolicy = getString(R.string.login_policy)
    val loginProtocol = getString(R.string.login_protocol)
    val spannable =
      StringExt.toSpannableStringBuilder(
        fragmentOwner.hostActivity().getColor(R.color.main_theme_color),
        getString(R.string.login_dlg_read_protocol, loginPolicy, loginProtocol),
        loginPolicy to UrlConstant.PRIVACY_URL,
        loginProtocol to UrlConstant.PROTOCOL_URL,
      ) { _, pair ->
        WebViewActivity.start(
          fragmentOwner.hostActivity(),
          pair.second,
          pair.first.replace("《", "").replace("》", "")
        )
      }

    MaterialDialog.Builder(fragmentOwner.hostActivity())
      .title(R.string.login_dlg_policy_and_protocol)
      .content(spannable)
      .positiveText(R.string.login_dlg_agreement)
      .negativeText(R.string.login_disagreement)
      .onPositive { _, _ ->
        AppConfig.saveShowPrivacyPolicyState()
        block()
      }
      .show()
  }
}

open class InstalledPackageAdapter : BaseAdapter<AppInfo>() {
  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int
  ): BaseHolder<AppInfo, ViewBinding> {
    val itemView = LayoutInflater.from(parent.context).inflate(R.layout.fragment_apps_item, null)
    return InstalledPackageHolder(itemView)
  }
}

class InstalledPackageHolder(itemView: View) :
  BaseHolder<AppInfo, FragmentAppsItemBinding>(itemView) {
  override fun initBinding(itemView: View) = FragmentAppsItemBinding.bind(itemView)
  override fun bindView(positionData: AppInfo, position: Int) {
    if (positionData.isDefault == TYPE_DEFAULT) {
      binding.imgAppIcon.setImageResource(R.drawable.icon_add_app)
      binding.tvAppName.text = itemView.context.getString(R.string.install_app_list)
    } else if (positionData.isDefault == TYPE_CALC) {
      binding.imgAppIcon.setImageResource(R.drawable.vbox_ic_calc)
      binding.tvAppName.text =
        itemView.context.getString(com.darkempire78.opencalculator.R.string.app_name_display)
    } else {
      binding.tvAppName.text =
        positionData.applicationInfo?.loadLabel(itemView.context.packageManager)
      binding.imgAppIcon.setImageDrawable(
        positionData.applicationInfo?.loadIcon(itemView.context.packageManager)
      )
    }
  }
}
