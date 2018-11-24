package ume.loaders;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.UiThread;
import android.util.SparseArray;

import ume.loaders.LifeCycleLoader.LoaderResultCallback;
import ume.loaders.LifeCycleLoader.ValueRequest;

import static ume.loaders.LoaderManagerHelper.getLoaderManagerHelper;


public final class LifeCycleLoaderManager {
    private SparseArray<LifeCycleLoader> loaders;
    private int key;
    private boolean stopped = true;

    LifeCycleLoaderManager(int key) {
        this.key = key;
        loaders = new SparseArray<>();
    }

    @SuppressWarnings("unchecked")
    void onCreate(LoaderInstallCallback callback) {
        int size = loaders.size();
        for (int i = 0; i < size; i++) {
            LifeCycleLoader loader = loaders.valueAt(i);
            if (loader.isDetached()) {
                LoaderInstallInfo info = callback.getLoaderInfo(loader.getId());
                loader.attachHost(info);
            }
        }
    }

    void onStart() {
        stopped = false;
        int size = loaders.size();
        for (int i = 0; i < size; i++) {
            LifeCycleLoader loader = loaders.valueAt(i);
            loader.onStart();
        }
    }

    void onStop() {
        stopped = true;
        int size = loaders.size();
        for (int i = 0; i < size; i++) {
            loaders.valueAt(i).onStop();
        }
    }

    void onDestroy(boolean exiting) {
        int size = loaders.size();
        for (int i = 0; i < size; i++) {
            LifeCycleLoader loader = loaders.valueAt(i);
            loader.onReset();
            if (exiting)
                loader.onDestroy();
            loader.release();
        }

        if (exiting) {
            loaders.clear();
            getLoaderManagerHelper().destroy(key);
        }

    }

    void save(Bundle state) {
        getLoaderManagerHelper().save(state, key);
    }

    private void installLoader(int id, LifeCycleLoader loader) {
        loader.setId(id);
        loaders.put(id, loader);
    }

    @SuppressWarnings("unchecked")
    @UiThread
    public <D> LifeCycleLoader<D> init(int key, LoaderInstallCallback callback, Bundle arg) {
        LifeCycleLoader<D> loader = loaders.get(key);
        if (loader == null) {
            LoaderInstallInfo info = callback.getLoaderInfo(key);
            loader = info.callback.createLifeCycleLoader(key, arg);
            installLoader(key, loader);
            loader.attachHost(info);
        }

        if (!stopped && loader.isStopped())
            loader.onStart();

        return loader;
    }

    public interface LoaderInstallCallback {
        LoaderInstallInfo getLoaderInfo(int key);
    }

    public static class LoaderInstallInfo {
        private Context context;
        private LoaderResultCallback callback;
        private ValueRequest valueRequest;

        public LoaderInstallInfo(Context context, LoaderResultCallback callback) {
            this.context = context;
            this.callback = callback;
        }

        Context getContext() {
            return context;
        }

        ValueRequest getValueRequest() {
            return valueRequest;
        }

        public LoaderInstallInfo setValueRequest(ValueRequest valueRequest) {
            this.valueRequest = valueRequest;
            return this;
        }

        public LoaderResultCallback getCallback() {
            return callback;
        }
    }

}
