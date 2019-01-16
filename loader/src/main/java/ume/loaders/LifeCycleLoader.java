package ume.loaders;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import androidx.fragment.app.Fragment;
import ume.loaders.LifeCycleLoaderManager.LoaderInstallInfo;

import static android.os.Looper.getMainLooper;
import static java.lang.Thread.currentThread;
import static ume.Assert.Assert.assertTrue;

public abstract class LifeCycleLoader<D> {

    private static final String TAG = "LifeCycleLoader";
    private volatile boolean stopped = true, detached = true;
    private LoaderInstallInfo info;
    private int id;
    private Handler handler;
    private LifeCycleLoader.DataStream<D> stream;

    @UiThread
    public LifeCycleLoader(DataStream<D> stream) {
        this.stream = stream;
        handler = new Handler();
    }

    static boolean isExiting(Activity activity) {
        return activity.isFinishing();
    }

    static boolean isExiting(Fragment fragment) {
        return fragment.requireActivity().isFinishing()
                || fragment.isRemoving() && !fragment.requireActivity()
                .isChangingConfigurations();
    }

    protected void attachedToHost() {

    }

    @CallSuper
    protected void onStop() {
        stopped = true;
    }

    @CallSuper
    protected void onStart() {
        stopped = false;
        synchronized (this) {
            while (hasPendingData()) dispatch();
        }
    }

    @SuppressWarnings("ConstantConditions")
    protected void onDestroy() {

    }

    /**
     * Called when onDestroy of the hosting activity or fragment is called. It does not
     * mean onDestroy will be called. OnDestroy will be called next if the host is finishing
     */
    protected void onReset() {

    }

    final void release() {
        info = null;
        detached = true;
    }

    final void attachHost(LoaderInstallInfo info) {
        assertTrue("Loader already attached to a host with id: " + id, detached);
        detached = false;
        this.info = info;
        attachedToHost();
    }

    private LoaderResultCallback<D> getCallback() {
        return isDetached() ? null : info.getCallback();
    }

    private boolean isMainThread() {
        return currentThread().equals(getMainLooper().getThread());
    }

    private void dispatch() {
        LoaderResultCallback<D> callback = getCallback();
        if (callback != null) {
            synchronized (this) {
                if (hasPendingData())
                    stream.dispatch(callback);
            }
        }
    }

    @CallSuper
    protected void deliverResult(final D d, final int t) {
        synchronized (this) {
            stream.onData(d, t);
            //results.add(new Result(d, t));
        }
        if (!isStopped() && hasPendingData()) {
            //current thread is ui thread, deliver result immediately
            if (isMainThread())
                dispatch();
            else
                handler.post(this::dispatch);
        }
    }

    @CallSuper
    protected void deliverError(final Exception e, int t) {
        synchronized (this) {
            //results.add(new Result(e, t));
            stream.onError(e, t);
        }

        if (!isStopped() && hasPendingData()) {
            if (isMainThread())
                dispatch();
            else
                handler.post(this::dispatch);
        }
    }

    protected final Object requestValue(int valueId) {
        if (isDetached()) return null;
        ValueRequest request = info.getValueRequest();
        return request == null
                ? null : request
                .getValue(getId(), valueId);
    }

    private synchronized boolean hasPendingData() {
        return stream.hasData();
    }

    public final boolean isDetached() {
        return detached;
    }

    public final boolean isStopped() {
        return stopped;
    }

    public final Context getContext() {
        return isDetached() ? null : info.getContext();
    }

    public final int getId() {
        return id;
    }

    final void setId(int id) {
        this.id = id;
    }

    /**
     * Loaders can use this interface to request values
     */
    public interface ValueRequest {
        Object getValue(int loaderId, int valueId);
    }

    public interface DataStream<D> {
        /**
         * Return true if there is data available to be delivered
         *
         * @return true if data is available
         */
        boolean hasData();

        /**
         * Store data
         *
         * @param d  Data
         * @param id data identifier
         */
        void onData(D d, int id);

        /**
         * Store error. This error should be wrapped in data
         * D and returned when DataStream#next is called
         *
         * @param e  Error
         * @param id identifier
         */
        void onError(Exception e, int id);

        /**
         * Dispatch pending data to the host
         *
         * @param callback LoaderResultCallback
         */
        void dispatch(LoaderResultCallback<D> callback);
    }

    public interface LoaderResultCallback<D> {

        LifeCycleLoader<D> createLifeCycleLoader(int key, Bundle arg);

        void onResultReady(D d, int id);

        void OnError(Exception e, int id);
    }
}
