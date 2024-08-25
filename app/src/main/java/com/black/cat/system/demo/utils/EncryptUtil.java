package com.black.cat.system.demo.utils;

import com.black.cat.vsystem.api.Vlog;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class EncryptUtil {

  public static String getSHA256(String str) {
    MessageDigest messageDigest;
    String encodestr = "";
    try {
      messageDigest = MessageDigest.getInstance("SHA-256");
      messageDigest.update(str.getBytes(StandardCharsets.UTF_8));
      encodestr = byte2Hex(messageDigest.digest());
    } catch (Exception ignore) {
    }
    return encodestr;
  }

  private static String byte2Hex(byte[] bytes) {
    StringBuilder stringBuilder = new StringBuilder();
    String temp = null;
    for (byte aByte : bytes) {
      temp = Integer.toHexString(aByte & 0xFF);
      if (temp.length() == 1) {
        stringBuilder.append("0");
      }
      stringBuilder.append(temp);
    }
    return stringBuilder.toString();
  }
}
