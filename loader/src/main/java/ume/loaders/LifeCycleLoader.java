package ume.loaders;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import androidx.fragment.app.Fragment;
import android.util.Log;

import java.util.ArrayList;

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
    private ArrayList<Result> results;

    @UiThread
    public LifeCycleLoader() {
        handler = new Handler();
        results = new ArrayList<>();
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
            while (hasPendingResult()) deliverResultInternal();
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

    private void deliverResultInternal() {
        LoaderResultCallback<D> callback = getCallback();
        if (callback == null) return;
        Result result;
        synchronized (this) {
            //deliverResultInternal posted in the handler executed
            //after onStart has delivered all result
            if (!hasPendingResult()) return;
            result = results.remove(0);
        }
        if (result.error != null)
            callback.OnError(result.error, result.type);
        else
            callback.onResultReady(result.result, result.type);
    }

    @CallSuper
    protected void deliverResult(final D d, final int t) {
        synchronized (this) {
            results.add(new Result(d, t));
        }
        if (isStopped()) return;
        //current thread is ui thread, deliver result immediately
        if (isMainThread())
            deliverResultInternal();
        else
            handler.post(this::deliverResultInternal);
    }

    @CallSuper
    protected void deliverError(final Exception e, int t) {
        synchronized (this) {
            results.add(new Result(e, t));
        }
        if (isStopped()) return;
        if (isMainThread())
            deliverResultInternal();
        else
            handler.post(this::deliverResultInternal);
        if (e != null) {
            e.printStackTrace();
            Log.e(TAG, "deliverError: " + getClass().getSimpleName());
        }
    }

    protected final Object requestValue(int valueId) {
        if (isDetached()) return null;
        ValueRequest request = info.getValueRequest();
        return request == null
                ? null : request
                .getValue(getId(), valueId);
    }

    private synchronized boolean hasPendingResult() {
        return !results.isEmpty();
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

    public interface LoaderResultCallback<D> {

        LifeCycleLoader<D> createLifeCycleLoader(int key, Bundle arg);

        void onResultReady(D d, int id);

        void OnError(Exception e, int id);
    }

    private class Result {
        D result;
        int type;
        Exception error;

        Result(D result, int type) {
            this.result = result;
            this.type = type;
        }

        Result(Exception error, int type) {
            this.error = error;
            this.type = type;
        }
    }
}
