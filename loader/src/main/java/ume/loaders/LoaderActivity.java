package ume.loaders;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import ume.loaders.LifeCycleLoader.LoaderResultCallback;
import ume.loaders.LifeCycleLoaderManager.LoaderInstallCallback;
import ume.loaders.LifeCycleLoaderManager.LoaderInstallInfo;


public abstract class LoaderActivity<D> extends AppCompatActivity implements LoaderInstallCallback,
        LoaderResultCallback<D> {
    private LoaderHelper loaderHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loaderHelper = new LoaderHelper(this);
        loaderHelper.onCreate(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        loaderHelper.onSaveInstanceState(outState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        loaderHelper.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        loaderHelper.onStop();
    }

    @Override
    protected void onDestroy() {
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
    public void OnError(Exception e) {

    }

    @Override
    public LoaderInstallInfo getLoaderInfo(int key) {
        return new LoaderInstallInfo(this,this);
    }

    public LifeCycleLoaderManager getLifeCycleLoaderManager() {
        return loaderHelper.getLifeCycleLoaderManager();
    }

    @SuppressWarnings("unchecked")
    public <L extends LifeCycleLoader> L getLoader(int id, Bundle arg) {
        return (L) getLifeCycleLoaderManager().init(id, this, arg);
    }
}
