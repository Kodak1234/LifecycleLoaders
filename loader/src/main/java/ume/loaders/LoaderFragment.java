package ume.loaders;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import ume.loaders.LifeCycleLoaderManager.LoaderInstallCallback;

public abstract class LoaderFragment extends Fragment implements LoaderInstallCallback {

    private LoaderHelper loaderHelper;

    @Override
    public void onCreate(@Nullable Bundle savedState) {
        super.onCreate(savedState);
        loaderHelper = new LoaderHelper(this);
        loaderHelper.onCreate(savedState);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        loaderHelper.onSaveInstanceState(outState);

    }

    @Override
    public void onStart() {
        super.onStart();
        loaderHelper.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        loaderHelper.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        loaderHelper.onDestroy(this);
    }

    public LifeCycleLoaderManager getLifeCycleLoaderManager() {
        return loaderHelper.getLifeCycleLoaderManager();
    }
}
