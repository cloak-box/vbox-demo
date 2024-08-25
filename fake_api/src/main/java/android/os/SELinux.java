package android.os;

public class SELinux {
  public static final String getContext() {
    throw new UnsupportedOperationException("STUB");
  }

  public static final boolean isSELinuxEnabled() {
    throw new UnsupportedOperationException("STUB");
  }

  public static final boolean isSELinuxEnforced() {
    throw new UnsupportedOperationException("STUB");
  }

  public static final native boolean checkSELinuxAccess(
      String scon, String tcon, String tclass, String perm);
}
