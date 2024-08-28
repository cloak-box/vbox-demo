package com.black.cat.system.demo.ui.act

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionManager
import androidx.viewbinding.ViewBinding
import com.black.cat.system.demo.R
import com.black.cat.system.demo.bean.SystemInstallAppInfo
import com.black.cat.system.demo.databinding.ActivityPhoneInstalledListBinding
import com.black.cat.system.demo.databinding.PhoneInstalledListItemBinding
import com.black.cat.system.demo.ui.base.BaseActivity
import com.black.cat.system.demo.utils.HanziToPinyin
import com.black.cat.system.demo.view.PhoneInstalledListViewModel
import com.black.cat.system.demo.widget.VSearchView
import com.black.cat.system.demo.widget.WaveSideBarView.OnTouchLetterChangeListener
import com.black.cat.system.demo.widget.adapter.BaseAdapter
import com.black.cat.system.demo.widget.adapter.BaseHolder
import com.black.cat.system.demo.widget.adapter.BaseItemClickListener
import com.xuexiang.xui.utils.DensityUtils
import com.xuexiang.xui.utils.KeyboardUtils
import com.xuexiang.xui.widget.actionbar.TitleBar.ImageAction
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog
import java.util.regex.Pattern

class PhoneInstalledListActivity : BaseActivity() {
  private val binding by lazy { ActivityPhoneInstalledListBinding.inflate(layoutInflater) }
  private val adapter by lazy { SystemInstallAppAdapter() }
  private val viewModel by lazy { createViewModel(PhoneInstalledListViewModel::class.java) }
  private val allSystemInstallAppInfos = mutableListOf<SystemInstallAppInfo>()
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(binding.root)
    binding.rvRecyclerView.layoutManager = LinearLayoutManager(this)
    binding.titleBar.setLeftClickListener { finish() }
    binding.titleBar.addAction(
      object : ImageAction(R.drawable.icon_action_query) {
        override fun performAction(view: View?) {
          TransitionManager.beginDelayedTransition(binding.rlTitleRoot)
          val lp = binding.titleBarSearchview.layoutParams as RelativeLayout.LayoutParams
          lp.topMargin = 0
          binding.titleBarSearchview.layoutParams = lp
          binding.titleBarSearchview.editText.requestFocus()
          KeyboardUtils.showSoftInput(binding.titleBarSearchview.editText)
        }
      }
    )

    binding.titleBarSearchview.setSearchViewListener(
      object : VSearchView.SearchViewListener {
        override fun onDismiss() {
          val topMargin =
            DensityUtils.dp2px(
              this@PhoneInstalledListActivity,
              -binding.titleBarSearchview.height.toFloat()
            )
          val lp = binding.titleBarSearchview.layoutParams as RelativeLayout.LayoutParams
          if (topMargin == lp.topMargin) {
            return
          }
          TransitionManager.beginDelayedTransition(binding.rlTitleRoot)
          lp.topMargin =
            DensityUtils.dp2px(
              this@PhoneInstalledListActivity,
              -binding.titleBarSearchview.height.toFloat()
            )
          binding.titleBarSearchview.layoutParams = lp
          binding.titleBarSearchview.editText.clearFocus()
          KeyboardUtils.hideSoftInput(binding.titleBarSearchview.editText)
          filterApp("")
        }

        override fun onTextChange(txt: String) {
          filterApp(txt)
        }
      }
    )

    initRecyclerView()
    showLoading()
    viewModel.getPhoneInstalledApps(this)
    viewModel.installedApps.observe(this) { applications ->
      hideLoading()
      adapter.clear()
      adapter.addAll(applications)
      adapter.notifyDataSetChanged()
      allSystemInstallAppInfos.clear()
      allSystemInstallAppInfos.addAll(applications)
    }
    viewModel.installedAppsFromCache.observe(this) { applications ->
      if (adapter.getData().isEmpty()) {
        adapter.addAll(applications)
        adapter.notifyDataSetChanged()
        hideLoading()
      }
    }
  }

  private fun initRecyclerView() {
    adapter.addAppOnClickListener =
      object : BaseItemClickListener<SystemInstallAppInfo> {
        override fun onClick(
          binding: ViewBinding,
          positionData: SystemInstallAppInfo,
          position: Int
        ) {
          MaterialDialog.Builder(this@PhoneInstalledListActivity)
            .iconRes(R.drawable.icon_tip)
            .title(R.string.tip_infos)
            .content(R.string.install_description, positionData.appName)
            .positiveText(R.string.submit)
            .negativeText(R.string.cancel)
            .onPositive { dialog, which ->
              val intentData = Intent()
              intentData.putExtra(INTENT_KEY_APPLICATION_INFO, positionData.applicationInfo)
              setResult(Activity.RESULT_OK, intentData)
              finish()
            }
            .show()
          KeyboardUtils.hideSoftInputClearFocus(
            this@PhoneInstalledListActivity.binding.titleBarSearchview.editText
          )
        }
      }
    binding.rvRecyclerView.adapter = adapter
    binding.waveSideBarView.setOnTouchLetterChangeListener(
      object : OnTouchLetterChangeListener {
        override fun onLetterChange(letter: String?) {
          val position = adapter.findFirstPositionByLetter(letter)
          if (position != -1) {
            val manager = binding.rvRecyclerView.layoutManager as LinearLayoutManager
            manager.scrollToPositionWithOffset(position, 0)
          }
        }

        override fun onTouchStart() {
          binding.titleBarSearchview.dismiss()
        }
      }
    )
  }

  private fun isLetter(str: String): Boolean {
    val c = str[0]
    // 正则表达式，判断首字母是否是英文字母
    val pattern = Pattern.compile("^[A-Za-z]+$")
    return pattern.matcher(c.toString() + "").matches()
  }
  private fun filterApp(newText: String?) {
    val filterAppInfos =
      if (newText.isNullOrEmpty()) {
        allSystemInstallAppInfos
      } else {
        if (!TextUtils.isEmpty(newText) && newText.length == 1 && isLetter(newText)) {
          allSystemInstallAppInfos.filter { it.firstLetter.equals(newText, true) }
        } else {
          allSystemInstallAppInfos.filter {
            it.appName.contains(newText, true) or
              HanziToPinyin.getInstance().getSpelling(it.appName).contains(newText, true)
          }
        }
      }
    adapter.clear()
    adapter.addAll(filterAppInfos)
    adapter.notifyDataSetChanged()
  }
  companion object {
    const val INTENT_KEY_APPLICATION_INFO = "intent_key_application_info"
  }
}

class SystemInstallAppAdapter : BaseAdapter<SystemInstallAppInfo>() {
  var addAppOnClickListener: BaseItemClickListener<SystemInstallAppInfo>? = null
  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int
  ): BaseHolder<SystemInstallAppInfo, ViewBinding> {
    val itemView =
      LayoutInflater.from(parent.context).inflate(R.layout.phone_installed_list_item, null)
    return SystemInstallAppHolder(itemView)
  }

  fun findFirstPositionByLetter(letter: String?): Int {
    if (letter == null) return -1
    var position = -1
    for (index in 0 until getData().size) {
      if (getData()[index].firstLetter == letter) {
        position = index
        break
      }
    }
    return position
  }
  inner class SystemInstallAppHolder(itemView: View) :
    BaseHolder<SystemInstallAppInfo, PhoneInstalledListItemBinding>(itemView) {
    override fun initBinding(itemView: View) = PhoneInstalledListItemBinding.bind(itemView)

    override fun bindView(positionData: SystemInstallAppInfo, position: Int) {
      binding.imgAppIcon.setImageDrawable(
        positionData.applicationInfo.loadIcon(itemView.context.packageManager)
      )
      binding.tvAppName.text =
        "${positionData.appName} ${positionData.applicationInfo.targetSdkVersion}"
      binding.tvAppPackageName.text = positionData.applicationInfo.packageName
      addAppOnClickListener?.let { tmpAddAppOnClickListener ->
        binding.rlAddApp.setOnClickListener {
          tmpAddAppOnClickListener.onClick(binding, positionData, position)
        }
      }
      var visibility = View.VISIBLE
      if (position != 0 && getData()[position - 1].firstLetter == positionData.firstLetter) {
        visibility = View.GONE
      }
      if (visibility == View.VISIBLE) {
        binding.tvSortLetter.text = positionData.firstLetter
      }
      binding.tvSortLetter.visibility = visibility
    }
  }
}
