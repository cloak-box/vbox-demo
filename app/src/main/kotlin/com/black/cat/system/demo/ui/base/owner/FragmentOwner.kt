package com.black.cat.system.demo.ui.base.owner

import androidx.appcompat.app.AppCompatActivity

interface FragmentOwner {
  fun hostActivity(): AppCompatActivity
  fun showLoading()
  fun hideLoading()
}
