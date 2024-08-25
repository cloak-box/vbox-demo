package com.black.cat.system.demo.ui.act.webview

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import android.widget.LinearLayout
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.PopupMenu.OnMenuItemClickListener
import com.black.cat.system.demo.R
import com.black.cat.system.demo.databinding.FragmentAgentwebBinding
import com.black.cat.system.demo.ui.base.BaseActivity
import com.black.cat.system.demo.utils.ViewUtil.singleClickListener
import com.black.cat.vsystem.api.Vlog
import com.just.agentweb.AgentWeb
import com.just.agentweb.DefaultWebClient
import com.just.agentweb.WebChromeClient
import com.just.agentweb.WebViewClient

class WebViewActivity : BaseActivity() {

  companion object {
    const val KEY_WEB_VIEW_URL = "key_web_view_url"
    const val KEY_WEB_VIEW_TITLE = "key_web_view_title"

    fun start(context: Context, url: String, actTitle: String) {
      context.startActivity(
        Intent(context, WebViewActivity::class.java).apply {
          putExtra(KEY_WEB_VIEW_URL, url)
          putExtra(KEY_WEB_VIEW_TITLE, actTitle)
        }
      )
    }
  }

  private val viewBinding by lazy { FragmentAgentwebBinding.inflate(layoutInflater) }
  private val targetUrl by lazy { intent.getStringExtra(KEY_WEB_VIEW_URL) }
  private val targetTitle by lazy { intent.getStringExtra(KEY_WEB_VIEW_TITLE) }
  private lateinit var mPopupMenu: PopupMenu
  private var mAgentWeb: AgentWeb? = null
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(viewBinding.root)
    changeTitleVisibility(View.GONE)
    mAgentWeb =
      AgentWeb.with(this)
        .setAgentWebParent(viewBinding.root, LinearLayout.LayoutParams(-1, -1))
        .useDefaultIndicator()
        .setWebChromeClient(
          object : WebChromeClient() {
            override fun onReceivedTitle(p0: WebView?, p1: String?) {
              super.onReceivedTitle(p0, p1)
              if (p1 != null) {
                viewBinding.includeToolbarWeb.toolbarTitle.text = p1
              }
            }
          }
        )
        .setWebViewClient(
          object : WebViewClient() {
            override fun onPageStarted(p0: WebView?, p1: String?, p2: Bitmap?) {
              super.onPageStarted(p0, p1, p2)
              if (p1 == targetUrl) {
                changeTitleVisibility(View.GONE)
              } else {
                changeTitleVisibility(View.VISIBLE)
              }
              Vlog.d("WebViewActivity", "load url = $p1")
            }

            override fun onPageFinished(p0: WebView?, p1: String?) {
              if (p1 == targetUrl) {
                viewBinding.includeToolbarWeb.ivMore.visibility = View.VISIBLE
                viewBinding.includeToolbarWeb.ivMore.singleClickListener { showPoPup(it) }
              }
            }
          }
        )
        .setMainFrameErrorView(com.just.agentweb.R.layout.agentweb_error_page, -1)
        .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
        .setWebLayout(WebLayout(this))
        .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.ASK) // 打开其他应用时，弹窗咨询用户是否前往其他应用
        .interceptUnkownUrl() // 拦截找不到相关页面的Scheme
        .addJavascriptInterface("new_user_tutorial", JsBridge(this))
        .createAgentWeb()
        .ready()
        .go(targetUrl)
    if (targetTitle.isNullOrEmpty()) {
      viewBinding.includeToolbarWeb.toolbarTitle.text = targetTitle
    }
    viewBinding.includeToolbarWeb.ivFinish.singleClickListener { finish() }
    viewBinding.includeToolbarWeb.ivBack.singleClickListener {
      if (mAgentWeb?.back() == false) {
        finish()
      }
    }
  }

  private fun changeTitleVisibility(visibility: Int) {
    viewBinding.includeToolbarWeb.viewLine.visibility = visibility
    viewBinding.includeToolbarWeb.ivFinish.visibility = visibility
  }

  private fun toCopy(context: Context, text: String) {
    val mClipboardManager = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
    mClipboardManager.setPrimaryClip(ClipData.newPlainText(null, text))
  }

  /**
   * 打开浏览器
   *
   * @param targetUrl 外部浏览器打开的地址
   */
  private fun openBrowser(targetUrl: String) {
    if (TextUtils.isEmpty(targetUrl) || targetUrl.startsWith("file://")) {
      return
    }
    try {
      val intent = Intent()
      intent.action = "android.intent.action.VIEW"
      val mUri = Uri.parse(targetUrl)
      intent.data = mUri
      startActivity(intent)
    } catch (th: Throwable) {}
  }

  private fun shareWebUrl(url: String) {
    try {
      val shareIntent = Intent()
      shareIntent.action = Intent.ACTION_SEND
      shareIntent.putExtra(Intent.EXTRA_TEXT, url)
      shareIntent.type = "text/plain"
      // 设置分享列表的标题，并且每次都显示分享列表
      startActivity(Intent.createChooser(shareIntent, "分享到"))
    } catch (th: Throwable) {}
  }
  private fun showPoPup(view: View) {
    if (!::mPopupMenu.isInitialized) {
      mPopupMenu = PopupMenu(this, view)
      mPopupMenu.inflate(R.menu.toolbar_menu)
      mPopupMenu.setOnMenuItemClickListener(
        object : OnMenuItemClickListener {
          override fun onMenuItemClick(item: MenuItem): Boolean {
            when (item.itemId) {
              R.id.refresh -> mAgentWeb?.urlLoader?.reload()
              R.id.copy ->
                mAgentWeb?.webCreator?.webView?.url?.let { toCopy(this@WebViewActivity, it) }
              R.id.default_browser -> mAgentWeb?.webCreator?.webView?.url?.let { openBrowser(it) }
              R.id.default_clean -> mAgentWeb?.clearWebCache()
              R.id.default_share -> mAgentWeb?.webCreator?.webView?.url?.let { shareWebUrl(it) }
              else -> return false
            }
            return true
          }
        }
      )
    }
    mPopupMenu.show()
  }

  override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
    if (mAgentWeb?.handleKeyEvent(keyCode, event) == true) {
      return true
    }
    return super.onKeyDown(keyCode, event)
  }

  override fun onPause() {
    mAgentWeb?.webLifeCycle?.onPause()
    super.onPause()
  }

  override fun onResume() {
    mAgentWeb?.webLifeCycle?.onResume()
    super.onResume()
  }

  override fun onDestroy() {
    super.onDestroy()
    // mAgentWeb.destroy();
    mAgentWeb?.webLifeCycle?.onDestroy()
  }
}
