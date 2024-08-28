package android.database;

import android.net.Uri;

interface IContentObserver
{
    void onChange(boolean selfUpdate, in Uri uri, int userId);
}
