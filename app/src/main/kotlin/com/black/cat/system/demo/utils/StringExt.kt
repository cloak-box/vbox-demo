package com.black.cat.system.demo.utils

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import java.security.MessageDigest
import kotlin.String

object StringExt {

  fun toSpannableStringBuilder(
    hightLightColor: Int,
    origin: String,
    vararg pairs: Pair<String, String>,
    block: (View, Pair<String, String>) -> Unit
  ): SpannableStringBuilder {
    val clickableHtmlBuilder = SpannableStringBuilder(origin)
    for (index in pairs.indices) {
      val pair = pairs[index]

      clickableHtmlBuilder.setSpan(
        object : ClickableSpan() {
          override fun updateDrawState(ds: TextPaint) {
            ds.color = hightLightColor
          }

          override fun onClick(widget: View) {
            block(widget, pair)
          }
        },
        origin.indexOf(pair.first),
        origin.indexOf(pair.first) + pair.first.length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
      )
    }
    return clickableHtmlBuilder
  }

  fun md5(input: String): String {
    val md = MessageDigest.getInstance("MD5")
    val bytes = md.digest(input.toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
  }
}
