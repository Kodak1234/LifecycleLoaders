package ume.loaders;


import android.os.Bundle;
import androidx.annotation.UiThread;
import android.util.SparseArray;

final class LoaderManagerHelper {
    private static LoaderManagerHelper loaderManagerHelper;
    private final String KEY = "LoaderManagerHelper.manager_key";
    private SparseArray<LifeCycleLoaderManager> managers;
    private int key;

    private LoaderManagerHelper() {
        managers = new SparseArray<>();
    }

    synchronized static LoaderManagerHelper getLoaderManagerHelper() {
        if (loaderManagerHelper == null)
            loaderManagerHelper = new LoaderManagerHelper();

        return loaderManagerHelper;
    }

    LifeCycleLoaderManager create(Bundle state, boolean create) {
        LifeCycleLoaderManager manager;
        if (state == null || create) {
            manager = create ? installManager() : null;
        } else {
            int key = state.getInt(KEY, -1);
            manager = managers.get(key);
        }

        return manager;
    }

    void save(Bundle state, int key) {
        state.putInt(KEY, key);
    }

    void destroy(int key) {
        LifeCycleLoaderManager manager = managers.get(key);
        if (manager == null)
            throw new IllegalArgumentException("No manager with key[" + key + "]");
        managers.delete(key);
    }

    private LifeCycleLoaderManager installManager() {
        int key = createKey();
        LifeCycleLoaderManager manager = new LifeCycleLoaderManager(key);
        managers.append(key, manager);
        return manager;
    }

    @UiThread
    private int createKey() {
        //avoid number fragmentation
        if (managers.size() > 0)
            key = managers.keyAt(managers.size() - 1) + 1;
        return key++;
    }

}
