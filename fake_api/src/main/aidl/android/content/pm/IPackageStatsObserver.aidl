package android.content.pm;

import android.content.pm.PackageStats;
interface IPackageStatsObserver {
    void onGetStatsCompleted(in PackageStats pStats, boolean succeeded);
}