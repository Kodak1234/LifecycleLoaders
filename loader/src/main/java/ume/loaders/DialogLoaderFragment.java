package ume.loaders;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;

import ume.loaders.LifeCycleLoader.LoaderResultCallback;
import ume.loaders.LifeCycleLoaderManager.LoaderInstallCallback;

public abstract class DialogLoaderFragment<D> extends AppCompatDialogFragment implements LoaderInstallCallback,
        LoaderResultCallback<D> {

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

    @Override
    public LifeCycleLoader<D> createLifeCycleLoader(int key, Bundle arg) {
        throw new UnsupportedOperationException("child should implement");
    }

    @Override
    public void onResultReady(D d, int type) {

    }

    @Override
    public void OnError(Exception e, int type) {

    }

    @Override
    public LifeCycleLoaderManager.LoaderInstallInfo getLoaderInfo(int key) {
        return new LifeCycleLoaderManager.LoaderInstallInfo(requireContext(), this);
    }

    @SuppressWarnings("unchecked")
    public <L extends LifeCycleLoader> L getLoader(int id, Bundle arg) {
        return (L) getLifeCycleLoaderManager().init(id, this, arg);
    }

    public LifeCycleLoaderManager getLifeCycleLoaderManager() {
        return loaderHelper.getLifeCycleLoaderManager();
    }
}

