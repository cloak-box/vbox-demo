package com.android.internal.widget;

interface ILockSettings {
  int[] getRecoverySecretTypes() ;
  Map getRecoveryStatus() ;
  void initRecoveryServiceWithSigFile(in String str,in byte[] bArr, in byte[] bArr2) ;
  void setRecoverySecretTypes(in int[] iArr) ;
  void setRecoveryStatus(String str, int i) ;
  void setServerParams(in byte[] bArr) ;
  void setSnapshotCreatedPendingIntent(in PendingIntent pendingIntent) ;
}
