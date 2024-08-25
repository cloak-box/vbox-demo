package com.black.cat.system.demo.ui.frag

import android.app.Activity
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
import com.black.cat.system.demo.databinding.FragmentXPluginItemBinding
import com.black.cat.system.demo.databinding.XPluginFragmentBinding
import com.black.cat.system.demo.ui.act.vsys.AppInstallActivity
import com.black.cat.system.demo.ui.act.vsys.AppLauncherActivity
import com.black.cat.system.demo.ui.base.BaseFragment
import com.black.cat.system.demo.view.XPluginFragmentViewModel
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
import com.xuexiang.xutil.tip.ToastUtils

class XPluginFragment : BaseFragment() {
  val selectPluginForSdCard =
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
      if (it.resultCode == Activity.RESULT_OK) {
        val uri = it.data?.data ?: return@registerForActivityResult
        Vlog.d("MainActivity", "import for $uri")
        viewModel.copyXposedPlugin(fragmentOwner.hostActivity(), uri)
      }
    }
  private val installXPlugin =
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
      if (it.resultCode == Activity.RESULT_OK) {
        val installResult: InstallResult? =
          it.data?.getParcelableExtra(AppInstallActivity.KEY_INSTALL_RESULT)
        installResult?.let {
          if (installResult.success) {
            viewModel.getInstalledPlugin(0)
          } else {
            ToastUtils.toast("安装失败 请检测安装包是否正确")
          }
        }
      }
    }
  private val viewBinding by lazy { XPluginFragmentBinding.inflate(layoutInflater) }
  private val viewModel by lazy { createViewModel(XPluginFragmentViewModel::class.java) }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    return viewBinding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    viewModel.copyXposedPluginObserver.observe(this.viewLifecycleOwner) {
      fragmentOwner.hideLoading()
      it?.let {
        if (it.metaData?.getBoolean("xposedmodule") == true) {
          installXPlugin.launch(AppInstallActivity.createIntent(fragmentOwner.hostActivity(), it))
        } else {
          ToastUtils.toast("请选择xposed插件")
        }
      }
    }
    val adapter = InstalledPluginAdapter()
    adapter.itemLongClickListener =
      object : BaseItemLongClickListener<AppInfo> {
        override fun onLongClick(binding: ViewBinding, positionData: AppInfo, position: Int) {
          showAppActions(positionData.applicationInfo!!)
        }
      }
    adapter.itemClickListener =
      object : BaseItemClickListener<AppInfo> {
        override fun onClick(binding: ViewBinding, positionData: AppInfo, position: Int) {
          AppLauncherActivity.start(fragmentOwner.hostActivity(), positionData.applicationInfo!!)
        }
      }
    adapter.checkedChangeListener =
      object : AdapterCheckedChangeListener {
        override fun onCheckedChanged(positionData: AppInfo, isChecked: Boolean) {
          positionData.isDefault = isChecked
          viewModel.savePlugins(adapter.getData())
        }
      }
    viewModel.installedPluginObserver.observe(this.viewLifecycleOwner) {
      adapter.clear()
      adapter.addAll(it)
      viewBinding.rvRecyclerView.adapter = adapter
    }

    viewModel.unInstallPluginObserver.observe(viewLifecycleOwner) { applicationInfo ->
      adapter
        .getData()
        .find { appInfo -> applicationInfo.packageName == appInfo.applicationInfo?.packageName }
        ?.let {
          adapter.remove(it)
          adapter.notifyDataSetChanged()
        }
      fragmentOwner.hideLoading()
    }

    viewBinding.rvRecyclerView.layoutManager = GridLayoutManager(fragmentOwner.hostActivity(), 1)
    val decoration = GridOffsetsItemDecoration(GridOffsetsItemDecoration.GRID_OFFSETS_VERTICAL)
    decoration.setHorizontalItemOffsets(DensityUtils.dp2px(fragmentOwner.hostActivity(), 8f))
    decoration.setVerticalItemOffsets(DensityUtils.dp2px(fragmentOwner.hostActivity(), 8f))
    viewBinding.rvRecyclerView.addItemDecoration(decoration)
    viewModel.getInstalledPlugin(0)
  }

  private fun showAppActions(applicationInfo: ApplicationInfo) {
    val list = mutableListOf<MaterialSimpleListItem>()

    list.add(
      MaterialSimpleListItem.Builder(fragmentOwner.hostActivity())
        .id(R.string.app_remove.toLong())
        .content(R.string.plugin_remove)
        .icon(R.drawable.icon_app_delete)
        .build()
    )

    val adapter =
      MaterialSimpleListAdapter(list).setOnItemClickListener {
        dialog: MaterialDialog,
        _: Int,
        item: MaterialSimpleListItem ->
        when (item.id) {
          R.string.app_remove.toLong() -> {
            fragmentOwner.showLoading()
            viewModel.unInstallPlugin(applicationInfo)
          }
        }
        dialog.dismiss()
      }
    MaterialDialog.Builder(fragmentOwner.hostActivity()).adapter(adapter, null).show()
  }
}

interface AdapterCheckedChangeListener {
  fun onCheckedChanged(positionData: AppInfo, isChecked: Boolean)
}

class InstalledPluginAdapter : BaseAdapter<AppInfo>() {
  var checkedChangeListener: AdapterCheckedChangeListener? = null
  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int
  ): BaseHolder<AppInfo, ViewBinding> {
    val itemView =
      LayoutInflater.from(parent.context).inflate(R.layout.fragment_x_plugin_item, null)
    return InstalledPluginHolder(itemView).also { it.checkedChangeListener = checkedChangeListener }
  }
}

class InstalledPluginHolder(itemView: View) :
  BaseHolder<AppInfo, FragmentXPluginItemBinding>(itemView) {
  var checkedChangeListener: AdapterCheckedChangeListener? = null
  override fun initBinding(itemView: View) = FragmentXPluginItemBinding.bind(itemView)
  override fun bindView(positionData: AppInfo, position: Int) {
    binding.tvPluginName.text =
      positionData.applicationInfo?.loadLabel(itemView.context.packageManager)
    binding.imgPluginIcon.setImageDrawable(
      positionData.applicationInfo?.loadIcon(itemView.context.packageManager)
    )
    binding.tvPluginDesc.text =
      positionData.applicationInfo?.metaData?.getString("xposeddescription")
    binding.scEnablePlugin.isChecked = positionData.isDefault
    if (checkedChangeListener != null) {
      binding.scEnablePlugin.setOnCheckedChangeListener { _, isChecked ->
        checkedChangeListener?.onCheckedChanged(positionData, isChecked)
      }
    }
  }
}
