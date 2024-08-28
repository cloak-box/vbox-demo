package android.app;

import android.content.ComponentName;

oneway interface IServiceConnection {
    void connected(in ComponentName name, IBinder service, boolean dead);
}
