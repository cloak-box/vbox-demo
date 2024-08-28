package android.os;

import android.os.Bundle;

/** @hide */
oneway interface IRemoteCallback {
    void sendResult(in Bundle data);
}
