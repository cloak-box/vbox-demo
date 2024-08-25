package android.app;

import android.content.IContentProvider;
import android.content.pm.ProviderInfo;
import android.os.IBinder;

/** Created by Milk on 2021/5/7. * ∧＿∧ (`･ω･∥ 丶　つ０ しーＪ 此处无Bug */
public class ContentProviderHolder {

  public ProviderInfo info = null;
  public IContentProvider provider;
  public IBinder connection;

  public boolean mLocal;
  public boolean noReleaseNeeded;

  public ContentProviderHolder(ProviderInfo _info) {
    this.info = _info;
  }
}
