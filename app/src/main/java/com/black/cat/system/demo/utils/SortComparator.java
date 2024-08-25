package com.black.cat.system.demo.utils;

import com.black.cat.system.demo.bean.SystemInstallAppInfo;
import java.util.Comparator;
import java.util.regex.Pattern;

public class SortComparator implements Comparator {
  private int compareCallNum = 0;

  @Override
  public int compare(Object o1, Object o2) {
    compareCallNum = 0;
    return compareString(
        ((SystemInstallAppInfo) o1).getAppName(), ((SystemInstallAppInfo) o2).getAppName());
  }

  private int compareString(String o1, String o2) {
    compareCallNum++;
    // 若存在长度为0的情况：
    if ((o1.length() == 0) && (o2.length() == 0)) {
      return 0;
    } else if (o1.length() == 0) {
      return -1;
    } else if (o2.length() == 0) {
      return 1;
    }

    String firstStrA = o1.substring(0, 1);
    String firstStrB = o2.substring(0, 1);

    int typeA = getFirstCharType(o1);
    int typeB = getFirstCharType(o2);

    if (typeA > typeB) {
      return -1; // 返回负值，则往前排
    } else if (typeA < typeB) {
      return 1;
    }

    // 类型相同，需要进行进一步的比较
    int compareResult;

    if ((typeA == 12 || typeA == 0) && (typeB == 12 || typeB == 0)) {
      compareResult = firstStrA.compareTo(firstStrB);
      if (compareResult != 0) {
        // 若不同，立即出来比较结果
        return compareResult;
      } else {
        // 若相同，则递归调用
        return compareString(o1.substring(1), o2.substring(1));
      }
    }

    // 是字母或汉字

    // 若是首字母，先用第一个字母或拼音进行比较
    // 否则，先判断字符类型
    String firstPinyinA = HanziToPinyin.getSortLetter(o1).first;
    String firstPinyinB = HanziToPinyin.getSortLetter(o2).first;
    if (compareCallNum == 1) {
      compareResult = firstPinyinA.compareTo(firstPinyinB);
      if (compareResult != 0) {
        return compareResult;
      }
    }
    // 若首字的第一个字母相同，或不是首字，判断原字符是汉字还是字母，汉字排在前面
    typeA = getFirstCharType2(o1);
    typeB = getFirstCharType2(o2);
    //    if (SuggestAppUtil.INSTANCE.isInSuggest(o1)) {
    //      return -1;
    //    } else if (SuggestAppUtil.INSTANCE.isInSuggest(o2)) {
    //      return 1;
    //    }

    if (typeA > typeB) {
      return -1;
    } else if (typeA < typeB) {
      return 1;
    }

    // 不是首字母，在字符类型之后判断，第一个字母或拼音进行比较
    if (compareCallNum != 1) {
      compareResult = firstPinyinA.compareTo(firstPinyinB);
      if (compareResult != 0) {
        return compareResult;
      }
    }

    if (isLetter(o1) && isLetter(o2)) {
      // 若是同一个字母，还要比较下大小写
      compareResult = firstStrA.compareTo(firstStrB);
      if (compareResult != 0) {
        return compareResult;
      }
    }

    if (isHanzi(o1) && isHanzi(o2)) {
      // 使用姓的拼音进行比较
      //            compareResult = firstPinyinA.compareTo(firstPinyinB);
      compareResult =
          HanziToPinyin.getSortLetter(o1).first.compareTo(HanziToPinyin.getSortLetter(o2).first);
      if (compareResult != 0) {
        return compareResult;
      }

      // 若姓的拼音相同，比较汉字是否相同
      compareResult = firstStrA.compareTo(firstStrB);
      if (compareResult != 0) {
        return compareResult;
      }
    }
    // 若相同，则进行下一个字符的比较（递归调用）
    return compareString(o1.substring(1), o2.substring(1));
  }

  private int getFirstCharType2(String str) {
    if (isHanzi(str)) {
      return 10;
    } else if (isLetter(str)) {
      return 9;
    } else if (isNumber(str)) {
      return 8;
    } else {
      return 0;
    }
  }

  private int getFirstCharType(String str) {
    if (isHanzi(str)) {
      return 10;
    } else if (isLetter(str)) {
      return 10;
    } else if (isNumber(str)) {
      return 12;
    } else {
      return 0;
    }
  }

  private boolean isLetter(String str) {
    char c = str.charAt(0);
    // 正则表达式，判断首字母是否是英文字母
    Pattern pattern = Pattern.compile("^[A-Za-z]+$");
    return pattern.matcher(c + "").matches();
  }

  private boolean isNumber(String str) {
    char c = str.charAt(0);
    // 正则表达式，判断首字母是否是英文字母
    Pattern pattern = Pattern.compile("^[1-9]+$");
    return pattern.matcher(c + "").matches();
  }

  private boolean isHanzi(String str) {
    char c = str.charAt(0);
    // 正则表达式，判断首字母是否是英文字母
    Pattern pattern = Pattern.compile("[\\u4E00-\\u9FA5]+");
    return pattern.matcher(c + "").matches();
  }
}
