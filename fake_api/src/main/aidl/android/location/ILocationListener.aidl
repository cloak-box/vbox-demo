package android.location;

import android.location.Location;
import android.os.Bundle;

interface ILocationListener {
    void onLocationChanged(in Location location) ;
    void onProviderDisabled(String str) ;
    void onProviderEnabled(String str) ;
    void onRemoved() ;
    void onStatusChanged(String str, int i, in Bundle bundle) ;
        void onProviderEnabledChanged(String provider, boolean enabled);
        void onFlushComplete(int requestCode);
}
