package com.black.cat.system.demo.utils

import java.security.MessageDigest

object Md5Utils {
  private val hexDigits =
    charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')
  fun String.md5(): String {

    val messageDigest = MessageDigest.getInstance("MD5")
    val inputByteArray: ByteArray = this.toByteArray(charset("utf-8"))
    messageDigest.update(inputByteArray)
    val resultByteArray = messageDigest.digest()
    return byteArrayToHex(resultByteArray)
  }
  private fun byteArrayToHex(byteArray: ByteArray): String {
    val resultCharArray = CharArray(byteArray.size * 2)
    var index = 0
    for (b in byteArray) {
      resultCharArray[index++] = hexDigits[b.toInt() ushr 4 and 0xf]
      resultCharArray[index++] = hexDigits[b.toInt() and 0xf]
    }
    return resultCharArray.concatToString()
  }
}
