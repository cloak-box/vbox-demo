package com.black.cat.system.demo.ui.act

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.core.view.get
import com.black.cat.system.demo.R
import com.black.cat.system.demo.databinding.ActivityMainBinding
import com.black.cat.system.demo.ui.base.BaseActivity
import com.black.cat.system.demo.ui.frag.AppsFragment
import com.black.cat.system.demo.ui.frag.MeFragment
import com.black.cat.system.demo.ui.frag.XPluginFragment
import com.black.cat.system.demo.widget.adapter.FragmentAdapter
import com.google.android.material.tabs.TabLayout
import com.hjq.permissions.XXPermissions
import com.xuexiang.xui.widget.actionbar.TitleBar.TextAction

class MainActivity : BaseActivity() {
  private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(binding.root)
    binding.viewpager.isUserInputEnabled = false
    val appsFragment = AppsFragment()
    val xPluginFragment = XPluginFragment()

    val fragments = listOf(appsFragment, xPluginFragment, MeFragment())
    binding.viewpager.adapter = FragmentAdapter(this, fragments)
    val tabCount = binding.tabLayout.tabCount
    val titles = listOf(R.string.main_tab_apps, R.string.main_tab_x_plugin, R.string.main_tab_me)
    for (index in 0 until tabCount) {
      binding.tabLayout.getTabAt(index)?.id = index
      binding.tabLayout.getTabAt(index)?.tag = titles[index]
    }
    val actionView =
      if (
        Build.VERSION.SDK_INT >= 29 ||
          Build.VERSION.SDK_INT >= 28 && Build.VERSION.PREVIEW_SDK_INT == 1
      ) {
        val action =
          object : TextAction("内存卡导入") {
            override fun performAction(view: View?) {
              val position = binding.tabLayout.selectedTabPosition
              if (position == -1) return
              val tab = binding.tabLayout.getTabAt(position) ?: return
              val intent =
                Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                  type = "application/vnd.android.package-archive"
                }

              if (tab.id == 0) {
                appsFragment.selectAppForSdCard.launch(intent)
              } else if (tab.id == 1) {
                xPluginFragment.selectPluginForSdCard.launch(intent)
              }
            }
          }
        binding.titleBar.addAction(action)
        binding.titleBar.getViewByAction(action)
      } else {
        null
      }

    binding.tabLayout.addOnTabSelectedListener(
      object : TabLayout.OnTabSelectedListener {

        override fun onTabSelected(tab: TabLayout.Tab) {
          binding.viewpager.setCurrentItem(tab.id, false)
          binding.titleBar.setLeftText(tab.tag as Int)
          actionView?.visibility = if (tab.id == 2) View.GONE else View.VISIBLE
        }

        override fun onTabUnselected(tab: TabLayout.Tab) {}

        override fun onTabReselected(tab: TabLayout.Tab) {}
      }
    )
    XXPermissions.with(this)
      .permission(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.READ_PHONE_STATE,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_MEDIA_IMAGES,
        android.Manifest.permission.READ_MEDIA_VIDEO,
        //        android.Manifest.permission.READ_CONTACTS
      )
      .request(null)
  }

  override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
    if (keyCode == KeyEvent.KEYCODE_BACK) {
      moveTaskToBack(true)
      return true
    }
    return super.onKeyDown(keyCode, event)
  }
}
