package ume.loaders;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import ume.loaders.LifeCycleLoaderManager.LoaderInstallCallback;

import static ume.loaders.LifeCycleLoader.isExiting;
import static ume.loaders.LoaderManagerHelper.getLoaderManagerHelper;


public class LoaderHelper {
    private LifeCycleLoaderManager loaderManager;
    private Bundle savedState;
    private boolean start = false;
    private LoaderInstallCallback installCallback;

    public LoaderHelper(LoaderInstallCallback installCallback) {
        this.installCallback = installCallback;
    }

    public void onCreate(@Nullable Bundle savedState) {
        this.savedState = savedState;
        loaderManager = getLoaderManagerHelper().create(savedState, false);
        if (loaderManager != null)
            loaderManager.onCreate(installCallback);
    }

    public void onSaveInstanceState(Bundle outState) {
        if (loaderManager != null)
            loaderManager.save(outState);
    }

    public void onStart() {
        start = true;
        if (loaderManager != null)
            loaderManager.onStart();
    }

    public void onStop() {
        start = false;
        if (loaderManager != null)
            loaderManager.onStop();
    }

    public void onDestroy(Fragment fragment) {
        if (loaderManager != null)
            loaderManager.onDestroy(isExiting(fragment));
    }

    public void onDestroy(AppCompatActivity activity) {
        if (loaderManager != null)
            loaderManager.onDestroy(isExiting(activity));
    }

    public LifeCycleLoaderManager getLifeCycleLoaderManager() {
        if (loaderManager == null) {
            loaderManager = getLoaderManagerHelper().create(savedState, true);
            if (start)
                loaderManager.onStart();
        }
        return loaderManager;
    }
}
