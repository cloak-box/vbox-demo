package com.black.cat.system.demo.ui.act.webview

import android.app.Activity
import android.view.ViewGroup
import android.webkit.WebView
import com.black.cat.system.demo.databinding.FragmentTwkWebBinding
import com.just.agentweb.IWebLayout

class WebLayout(activity: Activity) : IWebLayout<WebView, ViewGroup> {

  private val viewBinding by lazy { FragmentTwkWebBinding.inflate(activity.layoutInflater) }

  override fun getLayout(): ViewGroup {
    return viewBinding.refreshLayout
  }

  override fun getWebView(): WebView {
    return viewBinding.webView
  }
}
